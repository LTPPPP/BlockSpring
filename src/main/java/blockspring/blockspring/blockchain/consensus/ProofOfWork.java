package blockspring.blockspring.blockchain.consensus;

import blockspring.blockspring.blockchain.core.Block;
import blockspring.blockspring.blockchain.crypto.HashUtil;

public class ProofOfWork {
    private final int difficulty;

    public ProofOfWork(int difficulty) {
        this.difficulty = difficulty;
    }

    public void mineBlock(Block block) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!block.getHash().substring(0, difficulty).equals(target)) {
            block.setNonce(block.getNonce() + 1);
            block.setHash(block.calculateHash());
        }
        System.out.println("Block mined: " + block.getHash());
    }

    public boolean validate(Block block) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        String hash = block.calculateHash();
        return hash.substring(0, difficulty).equals(target) && hash.equals(block.getHash());
    }
}