package blockspring.blockspring.blockchain.core;

import blockspring.blockspring.blockchain.crypto.HashUtil;
import blockspring.blockspring.blockchain.crypto.SignatureUtil;
import lombok.Getter;
import lombok.Setter;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Transaction {
    private String transactionId;
    private PublicKey sender;
    private PublicKey recipient;
    private float value;
    private byte[] signature;

    private List<TransactionInput> inputs;
    private List<TransactionOutput> outputs = new ArrayList<>();

    private static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float value, List<TransactionInput> inputs) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    public boolean processTransaction(Blockchain blockchain) {
        if (!verifySignature()) {
            System.out.println("Transaction signature failed to verify");
            return false;
        }

        // Gather transaction inputs (ensure they are unspent)
        for (TransactionInput input : inputs) {
            input.setUTXO(blockchain.getUTXOs().get(input.getTransactionOutputId()));
        }

        // Check if transaction is valid
        if (getInputsValue() < blockchain.getMinimumTransaction()) {
            System.out.println("Transaction inputs too small: " + getInputsValue());
            return false;
        }

        // Generate transaction outputs
        float leftOver = getInputsValue() - value; // Get leftover change
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(recipient, value, transactionId)); // Send value to recipient
        outputs.add(new TransactionOutput(sender, leftOver, transactionId)); // Send left over back to sender

        // Add outputs to UTXO list
        blockchain.updateUTXO(this);

        return true;
    }

    public float getInputsValue() {
        float total = 0;
        for (TransactionInput input : inputs) {
            if (input.getUTXO() == null) continue;
            total += input.getUTXO().getValue();
        }
        return total;
    }

    public float getOutputsValue() {
        float total =.0f;
        for (TransactionOutput output : outputs) {
            total += output.getValue();
        }
        return total;
    }

    private String calculateHash() {
        sequence++;
        return HashUtil.sha256(
                HashUtil.getStringFromKey(sender) +
                        HashUtil.getStringFromKey(recipient) +
                        Float.toString(value) +
                        sequence
        );
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = HashUtil.getStringFromKey(sender) + HashUtil.getStringFromKey(recipient) + Float.toString(value);
        signature = SignatureUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature() {
        String data = HashUtil.getStringFromKey(sender) + HashUtil.getStringFromKey(recipient) + Float.toString(value);
        return SignatureUtil.verifyECDSASig(sender, data, signature);
    }

    @Getter
    @Setter
    public static class TransactionInput {
        private String transactionOutputId;
        private TransactionOutput UTXO;

        public TransactionInput(String transactionOutputId) {
            this.transactionOutputId = transactionOutputId;
        }
    }

    @Getter
    public static class TransactionOutput {
        private final String id;
        private final PublicKey recipient;
        private final float value;
        private final String parentTransactionId;

        public TransactionOutput(PublicKey recipient, float value, String parentTransactionId) {
            this.recipient = recipient;
            this.value = value;
            this.parentTransactionId = parentTransactionId;
            this.id = HashUtil.sha256(HashUtil.getStringFromKey(recipient) + Float.toString(value) + parentTransactionId);
        }

        public boolean isMine(PublicKey publicKey) {
            return (publicKey.equals(recipient));
        }
    }
}