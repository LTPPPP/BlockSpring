package blockspring.blockspring.blockchain.core;

import blockspring.blockspring.blockchain.crypto.HashUtil;
import lombok.Getter;
import lombok.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Getter
public class Blockchain {
    private final List<Block> blockchain;
    private final int difficulty = 0; // 0: no proof of work, the simplest way to create a blockchain
    private final Map<String, Transaction.TransactionOutput> UTXOs;
    private final float minimumTransaction = 0.1f;
    private int transactionSequence = 0;

    public Blockchain() {
        this.blockchain = new ArrayList<>();
        this.UTXOs = new HashMap<>();
        createGenesisBlock();
    }

    private void createGenesisBlock() {
        Block genesisBlock = new Block("0");
        genesisBlock.setTimestamp(System.currentTimeMillis());
        genesisBlock.mineBlock(difficulty);
        blockchain.add(genesisBlock);
        System.out.println("Genesis Block created: " + genesisBlock.getHash());
    }

    public Block getLatestBlock() {
        return blockchain.get(blockchain.size() - 1);
    }

    public void addBlock(Block newBlock) {
        newBlock.setPreviousHash(getLatestBlock().getHash());
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

    public boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        Map<String, Transaction> tempUTXOs = new HashMap<>();

        // Loop through blockchain to check hashes:
        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            // Compare registered hash and calculated hash:
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }

            // Compare previous hash and registered previous hash:
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                System.out.println("Previous Hashes not equal");
                return false;
            }

            // Check if hash is solved:
            if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }

        return true;
    }

    public void updateUTXO(Transaction transaction) {
        // Remove spent inputs from UTXO list
        if (transaction.getInputs() != null) {
            for (Transaction.TransactionInput input : transaction.getInputs()) {
                UTXOs.remove(input.getTransactionOutputId());
            }
        }

        // Add outputs to UTXO list
        for (Transaction.TransactionOutput output : transaction.getOutputs()) {
            UTXOs.put(output.getId(), output);
        }
    }

    public int getNextTransactionSequence() {
        return transactionSequence++;
    }
}