package exceptions;

import java.util.Collection;

public class AppointmentException extends  BusinessException{

    public AppointmentException(String msg) {
        super(msg);
    }

    public AppointmentException(String msg, Exception cause) {
        super(msg, cause);
    }

    public AppointmentException(Collection<String> messages) {
        super(messages);
    }

    public AppointmentException(Collection<String> messages, Exception cause) {
        super(messages, cause);
    }
}
