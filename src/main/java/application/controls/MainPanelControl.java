package application.controls;

import application.messaging.IListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public abstract class MainPanelControl extends BorderPane implements IMainPanelView {

    protected IListener listener;

    @Override
    public void addListener(IListener listener){
        this.listener = listener;
    }
    
    protected void showValidationMessage(String message) {

        SystemMessageControl control = new SystemMessageControl(message, (Void) -> closeMessage());
        control.getStyleClass().add("validationWarning");

        StackPane stackPane = new StackPane();
        final DoubleProperty opacity = stackPane.opacityProperty();

        this.setTop(stackPane);
        ControlFader fader = new ControlFader();
        fader.fadeInChild(control, stackPane, opacity, 200);
    }


    private void closeMessage(){
        this.setTop(null);
    }
    
    protected void executeLongAction(Consumer<Void> action){
         
      ProgressSpinner spinner = new ProgressSpinner();
      spinner.show();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                action.accept(null);
                Platform.runLater( () -> {spinner.close();}); 
            }); 
    } 
}
