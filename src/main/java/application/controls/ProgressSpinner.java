 
package application.controls;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox; 
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
 
public class ProgressSpinner {

    final Stage dialog = new Stage();

    public void show() {

        
        final ProgressIndicator pin = new ProgressIndicator(-1);
        pin.setPrefHeight(150);
        pin.setPrefWidth(150);
        pin.setProgress(-1);
        pin.setVisible(true);
        final HBox hb = new HBox();
        hb.setSpacing(5);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().add(pin);
        
        hb.getStylesheets().add("/styles/main.css");
        hb.getStyleClass().add("body");

        dialog.initModality(Modality.NONE);
        dialog.initStyle(StageStyle.UNDECORATED);

        Scene dialogScene = new Scene(hb, 150, 150);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    public void close() {
        dialog.close();
    }
}
