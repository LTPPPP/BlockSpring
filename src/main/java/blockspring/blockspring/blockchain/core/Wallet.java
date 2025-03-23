package blockspring.blockspring.blockchain.core;

import blockspring.blockspring.blockchain.crypto.SignatureUtil;
import lombok.Getter;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class Wallet {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Map<String, Transaction.TransactionOutput> UTXOs = new HashMap<>();

    public Wallet() {
        KeyPair keyPair = SignatureUtil.generateKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();
    }

    public float getBalance(Blockchain blockchain) {
        float total = 0;
        for (Map.Entry<String, Transaction.TransactionOutput> item : blockchain.getUTXOs().entrySet()) {
            Transaction.TransactionOutput UTXO = item.getValue();
            if (UTXO.isMine(publicKey)) {
                UTXOs.put(UTXO.getId(), UTXO);
                total += UTXO.getValue();
            }
        }
        return total;
    }

    public Transaction sendFunds(PublicKey recipient, float value, Blockchain blockchain) {
        if (getBalance(blockchain) < value) {
            System.out.println("Not enough funds to send transaction. Transaction discarded.");
            return null;
        }

        List<Transaction.TransactionInput> inputs = new ArrayList<>();
        float total = 0;
        for (Map.Entry<String, Transaction.TransactionOutput> item : UTXOs.entrySet()) {
            Transaction.TransactionOutput UTXO = item.getValue();
            total += UTXO.getValue();
            inputs.add(new Transaction.TransactionInput(UTXO.getId()));
            if (total > value) break;
        }

        Transaction newTransaction = new Transaction(publicKey, recipient, value, inputs);
        newTransaction.generateSignature(privateKey);

        // Remove spent UTXOs from the wallet
        for (Transaction.TransactionInput input : inputs) {
            UTXOs.remove(input.getTransactionOutputId());
        }

        return newTransaction;
    }
}