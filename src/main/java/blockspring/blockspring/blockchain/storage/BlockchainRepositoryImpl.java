package blockspring.blockspring.blockchain.storage;

import blockspring.blockspring.blockchain.core.Block;
import blockspring.blockspring.blockchain.core.Blockchain;
import com.google.gson.Gson;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class BlockchainRepositoryImpl implements BlockchainRepository {

    private static final String DATA_DIR = "blockchain_data";
    private static final String BLOCKS_DIR = DATA_DIR + "/blocks";

    private Gson gson;

    @PostConstruct
    public void init() {
        // Create directories if they don't exist
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            Files.createDirectories(Paths.get(BLOCKS_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Failed to create data directories", e);
        }

        gson = new Gson();
    }

    @Override
    public void saveBlock(Block block) {
        try {
            String fileName = BLOCKS_DIR + "/" + block.getHash() + ".json";
            try (FileWriter writer = new FileWriter(fileName)) {
                gson.toJson(block, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save block", e);
        }
    }

    @Override
    public void saveBlockchain(Blockchain blockchain) {
        for (Block block : blockchain.getBlockchain()) {
            saveBlock(block);
        }
    }

    @Override
    public Block getBlock(String hash) {
        try {
            String fileName = BLOCKS_DIR + "/" + hash + ".json";
            Path filePath = Paths.get(fileName);
            if (Files.exists(filePath)) {
                try (Reader reader = Files.newBufferedReader(filePath)) {
                    return gson.fromJson(reader, Block.class);
                }
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load block", e);
        }
    }

    @Override
    public List<Block> getAllBlocks() {
        try {
            List<Block> blocks = new ArrayList<>();
            File blocksDir = new File(BLOCKS_DIR);
            if (blocksDir.exists()) {
                File[] blockFiles = blocksDir.listFiles((dir, name) -> name.endsWith(".json"));
                if (blockFiles != null) {
                    for (File file : blockFiles) {
                        try (Reader reader = new FileReader(file)) {
                            Block block = gson.fromJson(reader, Block.class);
                            blocks.add(block);
                        }
                    }
                }
            }
            return blocks;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load blocks", e);
        }
    }

    @Override
    public Block getLatestBlock() {
        List<Block> blocks = getAllBlocks();
        if (blocks.isEmpty()) {
            return null;
        }

        return blocks.stream()
                .max((b1, b2) -> Long.compare(b1.getTimestamp(), b2.getTimestamp()))
                .orElse(null);
    }

    @Override
    public void clearBlocks() {
        try {
            Files.walk(Paths.get(BLOCKS_DIR))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            throw new RuntimeException("Failed to clear blocks", e);
        }
    }
}