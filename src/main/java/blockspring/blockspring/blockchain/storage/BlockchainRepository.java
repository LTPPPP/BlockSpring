package blockspring.blockspring.blockchain.storage;

import blockspring.blockspring.blockchain.core.Block;
import blockspring.blockspring.blockchain.core.Blockchain;

import java.util.List;

public interface BlockchainRepository {
    void saveBlock(Block block);
    void saveBlockchain(Blockchain blockchain);
    Block getBlock(String hash);
    List<Block> getAllBlocks();
    Block getLatestBlock();
    void clearBlocks();
}