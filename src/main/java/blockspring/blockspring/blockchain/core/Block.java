package blockspring.blockspring.blockchain.core;

import blockspring.blockspring.blockchain.crypto.HashUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Block implements Serializable {
    private String hash;
    private String previousHash;
    private List<Transaction> transactions;
    private long timestamp;
    private int nonce;
    private String merkleRoot;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.transactions = new ArrayList<>();
        this.timestamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return HashUtil.sha256(
                previousHash +
                        Long.toString(timestamp) +
                        Integer.toString(nonce) +
                        merkleRoot
        );
    }

    public void mineBlock(int difficulty) {
        merkleRoot = MerkleTree.getMerkleRoot(transactions);
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block mined: " + hash);
    }

    public boolean addTransaction(Transaction transaction, Blockchain blockchain) {
        if (transaction == null) return false;

        // Process transaction and check validity
        if (previousHash != "0") {
            if (!transaction.processTransaction(blockchain)) {
                System.out.println("Transaction failed to process. Discarding.");
                return false;
            }
        }

        transactions.add(transaction);
        System.out.println("Transaction added to block");
        return true;
    }
}