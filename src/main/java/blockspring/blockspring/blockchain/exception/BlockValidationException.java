package blockspring.blockspring.blockchain.exception;

public class BlockValidationException extends RuntimeException {

    public BlockValidationException(String message) {
        super(message);
    }

    public BlockValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}