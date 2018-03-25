package application.controls;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class SystemMessageControl  extends VBox{

    private final String message;
    private final Consumer<Void> closeAction;
    @FXML
    private Label lblMessage;

    @FXML
    private Button btnClose;

    private ActionListener listener;


    public SystemMessageControl(String message, Consumer<Void> closeAction){
        this.message = message;
        this.closeAction = closeAction;

        ScreenLoader.load("/views/SystemMessage.fxml", this, this);
    }

    @FXML
    public void initialize(){
        lblMessage.setText(message);

        btnClose.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                closeAction.accept(null);
            }
        });


    }
    public void addListener(ActionListener listener){
        this.listener = listener;
    }
}
