package exceptions;

import java.util.Collection;

public class ScheduleOverlapException extends BusinessException{

    public ScheduleOverlapException(String msg) {
        super(msg);
    }

    public ScheduleOverlapException(String msg, Exception cause) {
        super(msg, cause);
    }

    public ScheduleOverlapException(Collection<String> messages) {
        super(messages);
    }

    public ScheduleOverlapException(Collection<String> messages, Exception cause) {
        super(messages, cause);
    }
}
