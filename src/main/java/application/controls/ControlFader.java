package application.controls;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class ControlFader{

    public void fadeInChild(Node nodeToAdd, Pane stackPane, DoubleProperty opacity, double transitionTimer) {
        if (!stackPane.getChildren().isEmpty()) {    //if there is more than one screen
            Timeline fade = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)),
                    new KeyFrame(new Duration(transitionTimer), new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent t) {
                            stackPane.getChildren().remove(0);        //remove the displayed screen
                            stackPane.getChildren().add(0, nodeToAdd);     //add the screen
                            Timeline fadeIn = new Timeline(
                                    new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                                    new KeyFrame(new Duration(transitionTimer), new KeyValue(opacity, 1.0)));
                            fadeIn.play();
                        }
                    }, new KeyValue(opacity, 0.0)));
            fade.play();

        } else {
            stackPane.setOpacity(0.0);
            stackPane.getChildren().add(nodeToAdd);       //no one else been displayed, then just show
            Timeline fadeIn = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)),
                    new KeyFrame(new Duration(transitionTimer), new KeyValue(opacity, 1.0)));
            fadeIn.play();
        }
    }


}
