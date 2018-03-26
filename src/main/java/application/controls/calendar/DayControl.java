package application.controls.calendar;

import application.*;
import application.controls.ScreenLoader;
import application.controls.screens.AppointmentControl;
import application.factories.AppointmentControlFactory;
import application.factories.AppointmentLineControlFactory;
import javafx.beans.property.ReadOnlyObjectProperty; 
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent; 
import javafx.scene.text.Text;
import javafx.stage.Modality;
import models.Appointment; 
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.SortedSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class DayControl extends VBox implements IDayPart {
    private final VBox appointmentPane = new VBox(); 
    private final TextFlow flow = new TextFlow();
    private final Text txtMonth = new Text();
    private final Text txtDay = new Text();
        
    private final ObservableList<AppointmentLineControl> appointmentControls = FXCollections.observableArrayList(); 
    private final ZonedDateTime date;
    private final ICalandarPart parent; 
    private final AppointmentLineControlFactory lineControlFactory;  
    private final SortedSet<Appointment> sortedAppointments;
    private final ICalendarControl calendarControl;
    ReadOnlyObjectProperty<Bounds> mainBounds;

    @Override
    public ICalendarControl getCalendar() {
        return parent.getCalendar();
    }
  
    public DayControl(ZonedDateTime date, SortedSet<Appointment> appointments
            , ICalandarPart parent, Configuration config, AppointmentControlFactory appointmentControlFactory,
                      AppointmentLineControlFactory lineControlFactory){
        this.parent = parent;  
        this.lineControlFactory = lineControlFactory; 
        this.date = date; 
        this.sortedAppointments = appointments;
        this.calendarControl = parent.getCalendar(); 
 
        if (date == null){
            throw new IllegalArgumentException("Date is required for Day Control");
        }

        if (appointments == null){
            throw new IllegalArgumentException("Appointments are required for Day Control");
        }
       
        setupCSS(date);

        setupDoubleClick(appointmentControlFactory, date, config);
    
        setupContent(date);
        
        this.displayAppointments(); 
    }

    private void setupDoubleClick(AppointmentControlFactory appointmentControlFactory1, ZonedDateTime date1, Configuration config1) {
        addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
            if (event.getClickCount() > 1) {
                AppointmentControl control = appointmentControlFactory1.build(date1);
                ScreenLoader.loadNewStage(control, config1.getSmallAppointmentWidth(), config1.getSmallAppointmentHeight(), Modality.APPLICATION_MODAL);
            }
        });
    }
 
    private void setupCSS(ZonedDateTime date){
        
        this.getStylesheets().add("/styles/day.css");
        this.getStyleClass().add("body");
        Month selectedMonth = calendarControl.getSelectedMonth();
        if (selectedMonth == date.getMonth()){
            this.getStyleClass().add("currentMonth");
        }
        else{
            this.getStyleClass().add("otherMonth");
        }
 
        txtMonth.setStyle("-fx-font-weight: bold");
        
               
        boolean isBoldDay = LocalDateTime.now().getDayOfMonth() == date.getDayOfMonth();   
        if (isBoldDay){
            txtMonth.setStyle("-fx-font-weight: bold");
        }
    }
    
    private void setupContent(ZonedDateTime date){
                
        String month = "";
        if (date.getDayOfMonth() == 1){
            month = date.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        }

        String dayOnly = " " + date.getDayOfMonth() + "\n";
 

        txtMonth.setText(month); 
        txtDay.setText(dayOnly);
        flow.getChildren().add(txtMonth);
        flow.getChildren().add(txtDay);
       
        
        this.getChildren().add(flow);
        this.getChildren().add(appointmentPane); 
    }

    @Override
    public void setContentSize(double width, double height) {

        this.setPrefWidth(width);
        this.setPrefHeight(height);
    }
  
    @Override
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

    @Override
    public boolean addAppointment(Appointment appointment) {
        removeAppointment(appointment);
        sortedAppointments.add(appointment);
        displayAppointments();
        return true;
    }

    @Override
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
        if (sortedAppointments.isEmpty()){
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
