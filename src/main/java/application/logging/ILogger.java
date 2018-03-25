package application.logging;


public interface ILogger{
    void Log(String message);
    void Log(Exception ex);
    void Log(String message, Exception e);
}

