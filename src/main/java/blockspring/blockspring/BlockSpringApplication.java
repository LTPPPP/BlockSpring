package blockspring.blockspring;

import blockspring.blockspring.blockchain.core.Block;
import blockspring.blockspring.blockchain.core.Blockchain;
import blockspring.blockspring.blockchain.storage.BlockchainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class BlockSpringApplication {

    @Autowired
    private Blockchain blockchain;

    @Autowired
    private BlockchainRepository blockchainRepository;

    public static void main(String[] args) {
        SpringApplication.run(BlockSpringApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            // Load blockchain from storage if available
            List<Block> storedBlocks = blockchainRepository.getAllBlocks();
            if (!storedBlocks.isEmpty()) {
                System.out.println("Loading blockchain from storage...");
                // This is a simplified approach - a real system would validate the chain
                // and ensure proper order
            } else {
                System.out.println("No existing blockchain found. Starting with a new blockchain.");
                // Save genesis block
                blockchainRepository.saveBlock(blockchain.getLatestBlock());
            }

            System.out.println("Blockchain initialized with " +
                    blockchain.getBlockchain().size() + " blocks.");
            System.out.println("Latest block hash: " +
                    blockchain.getLatestBlock().getHash());
        };
    }
}