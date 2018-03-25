package exceptions;


import java.util.Collection;

public class ValidationException extends BusinessException {
    public ValidationException(String msg) {
        super(msg);
    }

    public ValidationException(String msg, Exception cause) {
        super(msg, cause);
    }

    public ValidationException(Collection<String> messages) {
        super(messages);
    }

    public ValidationException(Collection<String> messages, Exception cause) {
        super(messages, cause);
    }
}

