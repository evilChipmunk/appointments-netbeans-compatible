package application.controls.calendar;


import application.messaging.Commands;
import application.messaging.IListener;
import exceptions.AppointmentException;
import java.time.ZonedDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class MonthPickerDialog{

    private double layoutX;
    private double layoutY;
    private IListener listener;
    
    public void addListener(IListener listener){
        this.listener = listener;
    }

    public void show(ZonedDateTime selectedDate){

            final Stage dialog = new Stage();
            dialog.setX(layoutX);
            dialog.setY(layoutY);
            dialog.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue){

                   // dialog.hide();
                   dialog.close();
                }
            });

            ObservableList<MyDatePick> picks = FXCollections.observableArrayList();

            for (int year = 1995; year <=2025; year++){
                for (int month = 1; month <=12; month++){
                    picks.add(new MyDatePick(year, month));
                }
            }

            MyDatePick curentPick = picks.stream()
                    .filter(x -> x.getMonthNum() == selectedDate.getMonthValue()
                            && x.getYear() == selectedDate.getYear()).findFirst().get();

            ListView<MyDatePick> lstPicks = new ListView<>();
            lstPicks.setItems(picks);
            lstPicks.getSelectionModel().select(curentPick);
            lstPicks.scrollTo(curentPick);
            lstPicks.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {


               // dialog.hide();
                dialog.close();
                listener.actionPerformed(Commands.monthPickerSelected, newValue.getDate());

            });

            dialog.initModality(Modality.NONE);
            dialog.initStyle(StageStyle.UNDECORATED);

            //  dialog.initOwner(stage);
            VBox dialogVbox = new VBox(20);
            dialogVbox.getChildren().add(new Text("Select another month"));
            dialogVbox.getChildren().add(lstPicks);
            Scene dialogScene = new Scene(dialogVbox, 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();

    }

    public void setLayoutX(double layoutX) {
        this.layoutX = layoutX;
    }
    public void setLayoutY(double layoutY) {
        this.layoutY = layoutY;
    }
}
