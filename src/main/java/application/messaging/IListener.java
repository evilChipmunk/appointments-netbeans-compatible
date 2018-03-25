package application.messaging;

public interface IListener {
    void actionPerformed(Commands command, Object...args);
}
