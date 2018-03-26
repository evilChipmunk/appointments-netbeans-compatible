package application.alerts;

import javafx.scene.control.Alert;

public abstract class AlertBuilder {

    protected Alert alert;

    public abstract void buildAlertType(AlertType alertType);

    public abstract void buildMessage(String message);

    public abstract void buildTitle(String title);

    public abstract void buildExpandableContent(String message, String content);

    public abstract void buildStyling();

    public Alert getAlert() {
        return alert;
    }

    protected Alert.AlertType getAlertTypeForAlert(AlertType alertType) {
        switch (alertType) {
            case None:
            case Information:
                return Alert.AlertType.INFORMATION;
            case Warning:
                return Alert.AlertType.WARNING;
            case Error:
                return Alert.AlertType.ERROR;
        }
        return Alert.AlertType.INFORMATION;

    } 
}
