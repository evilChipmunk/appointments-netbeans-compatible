package application.alerts;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.stage.Modality;

public class CustomerDeleteAlertBuilder extends AlertBuilder{
    public CustomerDeleteAlertBuilder(){
        alert = new Alert(Alert.AlertType.WARNING);
        alert.initModality(Modality.APPLICATION_MODAL);
    }

    @Override
    public void buildAlertType(AlertType alertType){
        alert.setAlertType(getAlertTypeForAlert(alertType));
    }

    @Override
    public void buildMessage(String message) {
        Label label = new Label();
        label.setText(message);
        alert.setContentText(message);
    }

    @Override
    public void buildTitle(String title) {
        alert.setHeaderText(title);
    }

    @Override
    public void buildExpandableContent(String message, String content) {

    }

    @Override
    public void buildStyling() {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/alert.css").toExternalForm());

    }
}
