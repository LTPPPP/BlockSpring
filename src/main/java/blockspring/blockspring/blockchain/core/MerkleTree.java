package blockspring.blockspring.blockchain.core;

import blockspring.blockspring.blockchain.crypto.HashUtil;

import java.util.ArrayList;
import java.util.List;

public class MerkleTree {

    public static String getMerkleRoot(List<Transaction> transactions) {
        int count = transactions.size();

        if (count == 0) return "0";
        if (count == 1) return HashUtil.sha256(transactions.getFirst().getTransactionId());

        List<String> previousTreeLayer = new ArrayList<>();
        for (Transaction transaction : transactions) {
            previousTreeLayer.add(transaction.getTransactionId());
        }

        List<String> treeLayer = previousTreeLayer;
        while (count > 1) {
            treeLayer = new ArrayList<>();
            for (int i = 1; i < previousTreeLayer.size(); i += 2) {
                treeLayer.add(HashUtil.sha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
            }

            // If odd number of transactions, hash the last one twice
            if (previousTreeLayer.size() % 2 == 1) {
                treeLayer.add(HashUtil.sha256(previousTreeLayer.getLast() +
                        previousTreeLayer.getLast()));
            }

            count = treeLayer.size();
            previousTreeLayer = treeLayer;
        }

        return (treeLayer.size() == 1) ? treeLayer.getFirst() : "";
    }
}