package application.controls.screens;

import application.controls.ScreenLoader;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.Collection;

public class ErrorControl extends VBox{
    private final String message;
    private final boolean isRelease;

    @FXML
    private Label lblError;

    @FXML
    private ImageView imgAmbulance;

    public ErrorControl(String message){
        this.message = message;
        this.isRelease = false;
    }

    public ErrorControl(Exception e) {
        message = e.getMessage();
        this.isRelease = false;
    }

    @FXML
    public void initialize(){
        if (!isRelease){
            this.lblError.setText(this.message);
        }

        onLoad();
    }

    public void onLoad( ) {

//        Stage stage = (Stage) imgAmbulance.getScene().getWindow();
//        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, window -> {

            Timeline timeLine = new Timeline();
            Collection<KeyFrame> frames = timeLine.getKeyFrames();
            Duration frameGap = Duration.millis(1000);
            Duration frameTime = Duration.ZERO ;
            for (int i = 0; i < 100; i++){
                double movement = i * 10;
                frameTime = frameTime.add(frameGap);
                double x = imgAmbulance.getX();
                frames.add(new KeyFrame(frameTime, e -> imgAmbulance.setX(x + 10)));
            }
            timeLine.setCycleCount(Timeline.INDEFINITE);
            timeLine.play();
//        });
    }

    public void show() {

        ScreenLoader.loadNewStage("/views/Error.fxml", this, this, "Oh no!");

    }
}
