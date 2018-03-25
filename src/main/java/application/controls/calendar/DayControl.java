package application.controls.calendar;

import application.*;
import application.controllers.MainController;
import application.controls.ScreenLoader;
import application.controls.screens.AppointmentControl;
import application.factories.AppointmentControlFactory;
import application.factories.AppointmentLineControlFactory;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.EventHandler; 
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import models.Appointment;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Iterator;
import java.util.Locale;
import java.util.SortedSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.text.TextFlow;

public class DayControl extends VBox implements IDayPart {
    private final ZonedDateTime date;
    private final ICalandarPart parent;
    private final Configuration config;
    private final AppointmentControlFactory appointmentControlFactory;
    private final AppointmentLineControlFactory lineControlFactory;
    private int dayNumber;
    private DayOfWeek dayOfWeek;
    private SortedSet<Appointment> sortedAppointments;
    private final ObservableList<AppointmentLineControl> appointmentControls;
    private ICalendarControl calendarControl;
    ReadOnlyObjectProperty<Bounds> mainBounds;

    @Override
    public ICalendarControl getCalendar() {
        return parent.getCalendar();
    }
 

  
    private VBox appointmentPane;

    public DayControl(ZonedDateTime date, SortedSet<Appointment> appointments
            , ICalandarPart parent, Configuration config, AppointmentControlFactory appointmentControlFactory,
                      AppointmentLineControlFactory lineControlFactory){
        this.parent = parent;
        this.config = config;
        this.appointmentControlFactory = appointmentControlFactory;
        this.lineControlFactory = lineControlFactory;
        this.appointmentControls = FXCollections.observableArrayList(); 
        this.date = date;
        this.dayOfWeek = date.getDayOfWeek();
        this.sortedAppointments = appointments;

 
      //  ScreenLoader.load("/views/DayView.fxml", this, this);
        
        if (date == null){
            throw new IllegalArgumentException("Date is required for Day Control");
        }

        if (appointments == null){
            throw new IllegalArgumentException("Appointments are required for Day Control");
        }
       
        this.calendarControl = parent.getCalendar(); 

        this.getStylesheets().add("/styles/day.css");
        this.getStyleClass().add("body");
        Month selectedMonth = calendarControl.getSelectedMonth();
        if (selectedMonth == date.getMonth()){
            this.getStyleClass().add("currentMonth");
        }
        else{
            this.getStyleClass().add("otherMonth");
        }
 
        addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    AppointmentControl control = appointmentControlFactory.build(date);
                    ScreenLoader.loadNewStage(control, config.getSmallAppointmentWidth()
                            , config.getSmallAppointmentHeight(), Modality.APPLICATION_MODAL);
                }
            }
        });
   
        
        String month = "";
        if (date.getDayOfMonth() == 1){
            month = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        }

        String dayOnly = " " + date.getDayOfMonth() + "\n";
        
        boolean isBoldDay = LocalDateTime.now().getDayOfMonth() == date.getDayOfMonth();
 
        TextFlow flow = new TextFlow();
        Text txtMonth = new Text();
        txtMonth.setStyle("-fx-font-weight: bold");
        txtMonth.setText(month);
        
        Text txtDay = new Text();
        if (isBoldDay){
            txtMonth.setStyle("-fx-font-weight: bold");
        }
        txtDay.setText(dayOnly);
        flow.getChildren().add(txtMonth);
        flow.getChildren().add(txtDay);
      
        
        appointmentPane = new VBox();
        
        this.getChildren().add(flow);
        this.getChildren().add(appointmentPane);
//        this.setPrefWidth(staticWidth);
//        this.setMinWidth(staticWidth);
//        this.setPrefHeight(staticHeight);
//        this.setMinHeight(staticHeight);
 
//       MainController.getInstance().getMainPanelBoundsProperty().addListener((observable, oldValue, newValue) -> { 
//              
//            setDynamicHeight(newValue.getHeight());
//            setDynamicWidth(newValue.getWidth());
// 
//        }); 
        this.displayAppointments(); 
    }
 

    public void setContentSize(double width, double height) {

        this.setPrefWidth(width);
        this.setPrefHeight(height);
    }
//    private int staticWidth = 173;
//    private int staticHeight = 145;

//    private void setInitialSize(ReadOnlyObjectProperty<Bounds> bounds){
//
//        ICalendarControl control = this.getCalendar();
//        int weeks = control.getNumberOfWeeks();
//
//        //for whatever reason, the bounds is much shorter than the visual screen
//        //and is not growing into the region. this causes the below 'fix' to set height
//        double width = bounds.get().getWidth();
//        double height = bounds.get().getHeight();
////
////        width = staticWidth;
////        height = staticHeight;
//
//        if (weeks == 1){
//            height = height * 5; //such a hack...... a month is configured to have 5 weeks
//        }
//
////          would be uncommented if bounds worked on initialize
////        setDynamicHeight(width);
////        setDynamicWidth(height);
//        this.setPrefWidth(width);
//        this.setMinWidth(width);
//        this.setPrefHeight(height);
//        this.setMinHeight(height);
//    }
//
//    private void setDynamicSize(ReadOnlyObjectProperty<Bounds> bounds) {
//        bounds.addListener((observable, oldValue, newValue) -> {
//
//            //these are just too buggy. JavaFX does not respond well enough to visual tree changes.
//            //when binding to the height/width properties, if initial child contents are outside of
//            //or even to the main panel bounds, then these properties ONLY get bigger, even when
//            //actually shrinking the screen.
//            //When using the bounds property
//
//            setDynamicHeight(newValue.getHeight());
//            setDynamicWidth(newValue.getWidth());
//
//            //setInitialSize();
//        });
//    }

//    private void setDynamicWidth(double newValue) {
//
//        double width = newValue;
//        double column = width / 7;
//        column = column-7;
//
//        this.setPrefWidth(column);
//         this.setMinWidth(column);
//         this.setMaxWidth(column);
//    }
//
//    private void setDynamicHeight(double newValue) {
//      //  this.control = parent.getCalendar();
//       // if (control != null) {
//            //IMonthPart monthControl = control.getCurrentMonth();
//
//           // if (monthControl != null) {
//                double height = newValue;
//                double header = 100;// monthControl.getHeaderHeight();
//                height = height - header;
//
// 
//
//                double row = height / calendarControl.getNumberOfWeeks();
//               row = row - 20;
//
//                this.setPrefHeight(row);
//                this.setMinHeight(row);
//                this.setMaxHeight(row);
//           // }
//       // }
//    }


    public ZonedDateTime getDate(){
        return date;
    }

    @Override
    public boolean hasAppointment(Appointment appointment) {
        return sortedAppointments.stream().anyMatch(x -> x.getId() == appointment.getId());
    }

    @Override
    public boolean schedulesForThisDay(ZonedDateTime date) {
        return date.getMonth() == this.date.getMonth() && date.getYear() == this.date.getYear() && date.getDayOfMonth() == this.date.getDayOfMonth();
    }

    public boolean addAppointment(Appointment appointment) {
        removeAppointment(appointment);
        sortedAppointments.add(appointment);
        displayAppointments();
        return true;
    }

    public boolean changeAppointment(Appointment appointment){
        removeAppointment(appointment);

        ZonedDateTime appDate = appointment.getStart();

        //make sure that the change of appointment still falls within this time range
        boolean wasAdded = false;
        if (appDate.getMonth() == date.getMonth() && appDate.getDayOfMonth() == date.getDayOfMonth()){
            sortedAppointments.add(appointment);
            wasAdded = true;
        }
        displayAppointments();
        return wasAdded;
    }

    @Override
    public void removeAppointment(Appointment appointment) {
        if (sortedAppointments.size() == 0){
            displayAppointments();
            return;
        }
        Appointment foundAppointment = sortedAppointments.stream().filter(x -> x.getId() == appointment.getId())
                .findFirst().orElse(null);
        if (foundAppointment != null){
                sortedAppointments.remove(foundAppointment);

        }
        displayAppointments();
    }

    private void displayAppointments(){
 
        appointmentControls.clear();
        appointmentPane.getChildren().clear(); 
        for(Appointment app : sortedAppointments){
           AppointmentLineControl control = lineControlFactory.build(app); 
           control.setPrefWidth(this.getWidth());
          
            appointmentPane.getChildren().add(control);  
            appointmentControls.add(control);
        
        }  
        
        this.requestFocus();
        this.layout();
    } 
     
}
