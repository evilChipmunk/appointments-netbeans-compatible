package application.alerts;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;

public class ReminderAlertBuilder extends AlertBuilder{

    public ReminderAlertBuilder(){
        alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initModality(Modality.APPLICATION_MODAL);
    }

    @Override
    public void buildAlertType(AlertType alertType) {

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


        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);


        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
      //  expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.getDialogPane().setExpanded(true);
    }

    @Override
    public void buildStyling() {
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/alert.css").toExternalForm());
        dialogPane.getStylesheets().add(getClass().getResource("/styles/reminderAlert.css").toExternalForm());

    }
}


