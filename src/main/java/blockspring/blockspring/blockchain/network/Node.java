package blockspring.blockspring.blockchain.network;

import blockspring.blockspring.blockchain.core.Block;
import blockspring.blockspring.blockchain.core.Blockchain;
import blockspring.blockspring.blockchain.core.Transaction;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

@Component
@Getter
@Setter
public class Node implements Serializable {
    private String nodeId;
    private Set<String> peers;
    private Blockchain blockchain;
    private List<Transaction> pendingTransactions;

    private Gson gson = new Gson();

    public Node(Blockchain blockchain) {
        this.nodeId = UUID.randomUUID().toString();
        this.peers = new HashSet<>();
        this.blockchain = blockchain;
        this.pendingTransactions = new ArrayList<>();
    }

    public void addPeer(String peerAddress) {
        peers.add(peerAddress);
    }

    public void removePeer(String peerAddress) {
        peers.remove(peerAddress);
    }

    public void broadcastTransaction(Transaction transaction) {
        pendingTransactions.add(transaction);
        String transactionJson = gson.toJson(transaction);
        // Implementation of actual broadcast to peers would go here
    }

    public void broadcastBlock(Block block) {
        String blockJson = gson.toJson(block);
        // Implementation of actual broadcast to peers would go here
    }

    public void receiveBlock(String blockJson) {
        Block receivedBlock = gson.fromJson(blockJson, Block.class);

        // Verify block
        if (isBlockValid(receivedBlock)) {
            blockchain.addBlock(receivedBlock);
            System.out.println("Block added to the blockchain: " + receivedBlock.getHash());

            // Remove transactions included in this block from pending list
            for (Transaction tx : receivedBlock.getTransactions()) {
                pendingTransactions.removeIf(pendingTx ->
                        pendingTx.getTransactionId().equals(tx.getTransactionId()));
            }
        } else {
            System.out.println("Received invalid block: " + receivedBlock.getHash());
        }
    }

    private boolean isBlockValid(Block block) {
        // Check if previous hash matches last block in chain
        if (!block.getPreviousHash().equals(blockchain.getLatestBlock().getHash())) {
            return false;
        }

        // Check if hash is valid
        if (!block.getHash().equals(block.calculateHash())) {
            return false;
        }

        // Check if block meets difficulty requirement
        String target = new String(new char[blockchain.getDifficulty()]).replace('\0', '0');
        return block.getHash().substring(0, blockchain.getDifficulty()).equals(target);
    }

    public void mineBlock() {
        if (pendingTransactions.isEmpty()) {
            System.out.println("No transactions to mine");
            return;
        }

        Block newBlock = new Block(blockchain.getLatestBlock().getHash());

        // Add pending transactions to block (up to a limit)
        int txCount = 0;
        while (!pendingTransactions.isEmpty() && txCount < 10) {
            Transaction tx = pendingTransactions.getFirst();
            if (newBlock.addTransaction(tx, blockchain)) {
                pendingTransactions.removeFirst();
                txCount++;
            } else {
                // If transaction is invalid, remove it
                pendingTransactions.removeFirst();
            }
        }

        // Mine the block
        newBlock.mineBlock(blockchain.getDifficulty());

        // Add to blockchain
        blockchain.addBlock(newBlock);

        // Broadcast to peers
        broadcastBlock(newBlock);

        System.out.println("Block mined and added to blockchain: " + newBlock.getHash());
    }
}