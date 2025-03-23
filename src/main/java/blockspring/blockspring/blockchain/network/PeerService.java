package blockspring.blockspring.blockchain.network;

import blockspring.blockspring.blockchain.core.Block;
import blockspring.blockspring.blockchain.core.Transaction;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Service
public class PeerService {

    @Autowired
    private Node node;

    @Value("${blockchain.network.p2p.port}")
    private int p2pPort;

    private RestTemplate restTemplate;
    private Gson gson;

    @PostConstruct
    public void init() {
        restTemplate = new RestTemplate();
        gson = new Gson();
    }

    public void broadcastTransaction(Transaction transaction) {
        String transactionJson = gson.toJson(transaction);
        for (String peer : node.getPeers()) {
            try {
                restTemplate.postForEntity(
                        "https://" + peer + "/api/blockchain/transaction/receive",
                        transactionJson,
                        String.class
                );
            } catch (Exception e) {
                System.out.println("Failed to broadcast transaction to peer: " + peer);
            }
        }
    }

    public void broadcastBlock(Block block) {
        String blockJson = gson.toJson(block);
        for (String peer : node.getPeers()) {
            try {
                restTemplate.postForEntity(
                        "https://" + peer + "/api/blockchain/block/receive",
                        blockJson,
                        String.class
                );
            } catch (Exception e) {
                System.out.println("Failed to broadcast block to peer: " + peer);
            }
        }
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    public void discoverPeers() {
        Set<String> discoveredPeers = new HashSet<>();

        // For each known peer, get their peers
        for (String peer : node.getPeers()) {
            try {
                String[] peerList = restTemplate.getForObject(
                        "https://" + peer + "/api/blockchain/peers",
                        String[].class
                );

                if (peerList != null) {
                    for (String discoveredPeer : peerList) {
                        // Don't add self
                        if (!discoveredPeer.contains(node.getNodeId())) {
                            discoveredPeers.add(discoveredPeer);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to discover peers from: " + peer);
            }
        }

        // Add all discovered peers
        for (String discoveredPeer : discoveredPeers) {
            node.addPeer(discoveredPeer);
        }
    }

    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void syncBlockchain() {
        // Find the peer with the longest valid chain
        int maxLength = node.getBlockchain().getBlockchain().size();
        String bestPeer = null;

        for (String peer : node.getPeers()) {
            try {
                Integer peerChainLength = restTemplate.getForObject(
                        "https://" + peer + "/api/blockchain/chain/length",
                        Integer.class
                );

                if (peerChainLength != null && peerChainLength > maxLength) {
                    // Verify the peer's chain is valid
                    Boolean isValid = restTemplate.getForObject(
                            "https://" + peer + "/api/blockchain/chain/valid",
                            Boolean.class
                    );

                    if (Boolean.TRUE.equals(isValid)) {
                        maxLength = peerChainLength;
                        bestPeer = peer;
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to get chain info from peer: " + peer);
            }
        }

        // If we found a longer valid chain, replace ours
        if (bestPeer != null) {
            try {
                Block[] peerChain = restTemplate.getForObject(
                        "https://" + bestPeer + "/api/blockchain/chain",
                        Block[].class
                );

                if (peerChain != null && peerChain.length > 0) {
                    // Replace our chain
                    // In a real implementation, we would validate the peer's chain
                    // and handle consensus properly
                    System.out.println("Replacing chain with longer one from peer: " + bestPeer);
                }
            } catch (Exception e) {
                System.out.println("Failed to sync chain from peer: " + bestPeer);
            }
        }
    }
}