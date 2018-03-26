package exceptions;

import java.util.Collection;

///custom exception to handle business exception types
//allows for a list of messages to be passed in
public class BusinessException extends RuntimeException {

    Collection<String> messages;

    public BusinessException(String msg) {
        super(msg);
    }

    public BusinessException(String msg, Exception cause) {
        super(msg, cause);
    }

    public BusinessException(Collection<String> messages) {
        super();
        this.messages = messages;
    }

    public BusinessException(Collection<String> messages, Exception cause) {
        super(cause);
        this.messages = messages;
    }

    @Override
    public String getMessage() {
        String msg = "";

        if (this.messages != null && !this.messages.isEmpty()) {
            for (String message : this.messages) {
                msg += message + "\r\n";
            }

        } else {
            msg = super.getMessage();
        }

        return msg;
    }
}
