package blockspring.blockspring.blockchain.network;

import blockspring.blockspring.blockchain.core.Block;
import blockspring.blockspring.blockchain.core.Blockchain;
import blockspring.blockspring.blockchain.core.Transaction;
import blockspring.blockspring.blockchain.core.Wallet;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/blockchain")
public class NodeController {

    @Autowired
    private Node node;

    @Autowired
    private Blockchain blockchain;

    @GetMapping("/chain")
    public ResponseEntity<List<Block>> getBlockchain() {
        return ResponseEntity.ok(blockchain.getBlockchain());
    }

    @GetMapping("/chain/latest")
    public ResponseEntity<Block> getLatestBlock() {
        return ResponseEntity.ok(blockchain.getLatestBlock());
    }

    @GetMapping("/chain/valid")
    public ResponseEntity<Boolean> isChainValid() {
        return ResponseEntity.ok(blockchain.isChainValid());
    }

    @GetMapping("/mine")
    public ResponseEntity<Block> mineBlock() {
        node.mineBlock();
        return ResponseEntity.ok(blockchain.getLatestBlock());
    }

    @PostMapping("/transaction/new")
    public ResponseEntity<String> newTransaction(@RequestBody TransactionRequest request) {
        // In a real implementation, we would validate the transaction signature
        // and create a proper Transaction object

        try {
            // This is a simplified version, actual implementation would require wallet handling
            Transaction transaction = new Transaction(
                    null, // sender public key
                    null, // recipient public key
                    request.getAmount(),
                    null  // inputs
            );

            node.broadcastTransaction(transaction);
            return ResponseEntity.ok("Transaction added to pending transactions");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Transaction failed: " + e.getMessage());
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Transaction>> getPendingTransactions() {
        return ResponseEntity.ok(node.getPendingTransactions());
    }

    @PostMapping("/peers/add")
    public ResponseEntity<String> addPeer(@RequestBody Map<String, String> request) {
        String peerAddress = request.get("address");
        if (peerAddress != null && !peerAddress.isEmpty()) {
            node.addPeer(peerAddress);
            return ResponseEntity.ok("Peer added: " + peerAddress);
        }
        return ResponseEntity.badRequest().body("Invalid peer address");
    }

    @GetMapping("/peers")
    public ResponseEntity<Set<String>> getPeers() {
        return ResponseEntity.ok(node.getPeers());
    }

    @Getter
    @Setter
    public static class TransactionRequest {
        private String sender;
        private String recipient;
        private float amount;
    }
}