package application.alerts;

public class AlertContent
{
    private String title;
    private String message;
    private String largeContent;
    private AlertType alertType;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLargeContent() {
        return largeContent;
    }

    public void setLargeContent(String largeContent) {
        this.largeContent = largeContent;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }
}
