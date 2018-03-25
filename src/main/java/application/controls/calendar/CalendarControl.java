package application.controls.calendar;

import application.Configuration;
import application.controllers.MainController;
import application.controls.IMainPanelView;
import application.controls.MainPanelControl;
import application.controls.ScreenLoader;
import application.factories.MonthControlFactory;
import application.messaging.Commands;
import application.messaging.IListener;
import application.services.ICalendarContext;
import application.services.IScheduler;
import exceptions.AppointmentException;
import exceptions.ValidationException;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler; 
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import models.Appointment;

import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CalendarControl extends MainPanelControl implements ICalendarControl {
    private ZonedDateTime firstDate;
    private IMonthPart currentMonth;
    private SortedSet<Appointment> sortedAppointments;
    private boolean isWeekly = false;
    private SimpleBooleanProperty currentMonthLoaded = new SimpleBooleanProperty(false);
    private IListener listener;
    private Month selectedMonth;
    private final ObservableList<IMonthPart> months;
    private final ICalendarContext context;
    private final IScheduler scheduler;
    private final MonthControlFactory monthFactory;
    private final String monthly = "View Month";
    private final String weekly = "View Week";

 
    private final GridPane monthPane = new GridPane(); 
    private final Hyperlink lblMonth = new Hyperlink(); 
    private final Hyperlink lblViewSelect = new Hyperlink(); 
    private final ImageView imgLeft = new ImageView(); 
    private final ImageView imgRight = new ImageView();
    
    private final HBox header = new HBox();
    private final HBox hLeft = new HBox();
    private final HBox hRight = new HBox();
    private final VBox vLeft = new VBox();
    private final VBox vRight = new VBox();

    public CalendarControl(ICalendarContext context, IScheduler scheduler, MonthControlFactory monthFactory) {
        this.context = context;
        this.scheduler = scheduler;
        this.monthFactory = monthFactory;
        months = FXCollections.observableArrayList();
        this.firstDate = ZonedDateTime.now();
 
        setupCSS();
        
        setupScheduler();
 
        setupImages();
        
        setupImageHovers();
        
        setupImageClicks();
        
        setupLinkClicks();
        
        setupHeader();
        
        setupMainContent();
        
        lblViewSelect.setText(weekly);
 
        lblMonth.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                changeMonthAction(event.getScreenX(), event.getScreenY());
            }
        });  
    }
    
    private void setupCSS(){
        this.getStylesheets().add("styles/calendar.css");
        this.getStyleClass().add("body");
        
//        imgLeft.getStyleClass().add("leftNav");
//        imgRight.getStyleClass().add("rightNav");
        
        hLeft.getStyleClass().add("calendarNav");
        hRight.getStyleClass().add("calendarNav");
        
        hLeft.setAlignment(Pos.TOP_LEFT);
        hRight.setAlignment(Pos.TOP_RIGHT);
        
        lblMonth.getStyleClass().add("calendarNavLink");
        lblViewSelect.getStyleClass().add("calendarNavLink");
    }
    
    private void setupScheduler(){
         
        scheduler.getHasAppointmentProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                appointmentScheduled(); 
            }
        });

        scheduler.getHasRemovalProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue){
                appointmentRemoved(); 
            }
        });
    }
 
    private void setupImages(){ 
        imgLeft.setImage(new Image("/images/light-arrow-alt-circle-left.png"));
        imgRight.setImage(new Image("/images/light-arrow-alt-circle-right.png"));
        
        imgLeft.setPreserveRatio(true);
        imgLeft.setFitHeight(50);
        
        imgRight.setPreserveRatio(true);
        imgRight.setFitHeight(50);
         
    }
    
    private void setupImageHovers(){
        
        this.vLeft.setOnMouseEntered(this::leftEntered);
        this.vRight.setOnMouseEntered(this::rightEntered); 
        
        
        this.vLeft.setOnMouseExited(this::leftExited);
        this.vRight.setOnMouseExited(this::rightExited); 
    }
    
    private void setupImageClicks(){
        this.vLeft.setOnMouseClicked(this::leftClicked);
        this.vRight.setOnMouseClicked(this::rightClicked); 
    }
    
    private void setupLinkClicks(){
        
        this.lblMonth.setOnAction(this::changeMonth);
        this.lblViewSelect.setOnAction(this::changeView); 
    }
    
    private void setupHeader() {
        vLeft.getChildren().add(imgLeft);

        vRight.getChildren().add(imgRight);

        
        Insets inset = new Insets(10);
        
        hLeft.setPadding(inset);
        hLeft.getChildren().add(vLeft);
        hLeft.getChildren().add(vRight);
        hLeft.getChildren().add(this.lblMonth);

        hRight.setPadding(inset);
        hRight.getChildren().add(lblViewSelect);

        header.getChildren().add(hLeft);
        header.getChildren().add(hRight);
        
        this.setTop(header);
    }
    
    private void setupMainContent(){
        this.setCenter(monthPane);
    }

    public void addListener(IListener listener){
        this.listener = listener;
    }


    @Override
    public ICalendarControl getCalendar() {
        return this;
    }

    @Override
    public ZonedDateTime getFirstDate() {
        return firstDate;
    }

 
    private void setFirstDate(ZonedDateTime date){
        this.firstDate = date;
    }

    @Override
    public ObservableList<IMonthPart> getMonths() {
        return months;
    }

    @Override
    public int getMonthCount() {

        return months.size();
    }

    @Override
    public IMonthPart getCurrentMonth() {
        return currentMonth;
    }

    @Override
    public int getNumberOfWeeks(){
        if (isWeekly){
            return 1;
        }
        //this controls how many weeks are visible on the screen
        return 5;
    }


 
    public void leftEntered(MouseEvent event){
        imgLeft.setImage(new Image("/images/arrow-alt-circle-left.png"));
    }

 
    public void leftExited(MouseEvent event){
        imgLeft.setImage(new Image("/images/light-arrow-alt-circle-left.png"));
    }

 
    public void rightEntered(MouseEvent event){
        imgRight.setImage(new Image("/images/arrow-alt-circle-right.png"));
    }

 
    public void rightExited(MouseEvent event){
        imgRight.setImage(new Image("/images/light-arrow-alt-circle-right.png"));
    }

//    private void setUpImageHovers(ImageView imgView, String path, String altPath){
//
//        imgView.onMouseEnteredProperty().addListener((observable, oldValue, newValue) -> {
//            imgView.setImage(null);
//            imgView.setImage(new Image(altPath));
//        });
//
//        imgView.onMouseExitedProperty().addListener((observable, oldValue, newValue) -> {
//            imgView.setImage(null);
//            imgView.setImage(new Image(path));
//        });
//    }

 
    public void changeMonth(javafx.event.ActionEvent event) {

        changeMonthAction(0, 0);
    }

    private void changeMonthAction(double x, double y){

        this.listener.actionPerformed(Commands.monthPickerShown, x, y);
    }

   
    public void changeView(javafx.event.ActionEvent event) throws AppointmentException {

        isWeekly =lblViewSelect.getText().equals(weekly);

        if (isWeekly){
            lblViewSelect.setText(monthly);
            AddMonth(this.firstDate);
        }
        else{
            lblViewSelect.setText(weekly);
            AddMonth(this.firstDate);
        }
    }

 
    public void  leftClicked(MouseEvent event) throws AppointmentException {

        if (this.isWeekly){
            this.firstDate = this.firstDate.minusWeeks(1);
            AddMonth(this.firstDate);
        }
        else{
            this.firstDate = this.firstDate.minusMonths(1);
            AddMonth(this.firstDate);
        }
    }

 
    public void  rightClicked(MouseEvent event) throws AppointmentException {

        if (this.isWeekly){
            this.firstDate = this.firstDate.plusWeeks(1);
            AddMonth(this.firstDate);
        }
        else{
            this.firstDate = this.firstDate.plusMonths(1);
            AddMonth(this.firstDate);
        }
    }


    private void appointmentScheduled() {

                
        IDayPart dayChanged = null;
        
        Appointment appointment = scheduler.getAppointment();

        boolean wasAdded = false;
        for (IMonthPart month : months) {
            //if the month doesn't have the appointment don't schedule it
            if (!month.hasAppointment(appointment)) {
                continue;
            }

            for (IWeekPart week : month.getWeekParts()) {
                for (IDayPart day : week.getDayParts()) {
                    if (day.hasAppointment(appointment)) {
                        wasAdded = day.changeAppointment(appointment);
                        dayChanged = day;
                        break;
                    }
                }
            }
        }

        // an additional loop needs to happen, because the first loop
        // would remove if it was found on a particular day. Then it is
        // added if the appointment day matched. But the appointment
        // could have changed weeks, which would just removce it
        // this loop will then add it to the right week
        if (!wasAdded) {
            for (IMonthPart month : months) {
                for (IWeekPart week : month.getWeekParts()) {
                    for (IDayPart day : week.getDayParts()) {
                        if (day.schedulesForThisDay(appointment.getStart())) {
                            day.addAppointment(appointment);
                            dayChanged = day; 
                            break;
                        }
                    }
                }
            }
        }

        if (dayChanged != null) { 
            DayControl control = (DayControl) dayChanged;
            Platform.runLater(() -> { 
                control.requestFocus();
                control.layout();
                this.requestFocus();
                this.layout(); 
//                Alert alert = new Alert(AlertType.CONFIRMATION, "added");
//                alert.show();
            });
        }
    }

    private void appointmentRemoved() {
        Appointment appointment = scheduler.getRemovedAppointment();

                
        IDayPart dayChanged = null;
        for (IMonthPart month : months) {
            for (IWeekPart week : month.getWeekParts()) {
                for (IDayPart day : week.getDayParts()) {
                    if (day.hasAppointment(appointment)) {
                        day.removeAppointment(appointment);
                        dayChanged = day; 
                    }
                }
            }
        }

        if (dayChanged != null) { 
            DayControl control = (DayControl) dayChanged;
            Platform.runLater(() -> { 
                control.requestFocus();
                control.layout();
                this.requestFocus();
                this.layout(); 
                
//                Alert alert = new Alert(AlertType.CONFIRMATION, "removed");
//                alert.show();
            });
        }
    }

    public void AddMonth(ZonedDateTime date) throws ValidationException, AppointmentException {

        setFirstDate(date);
        if (isWeekly) {
            while (!firstDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                firstDate = firstDate.minusDays(1);
            }
        } else {
            while (firstDate.getDayOfMonth() != 1) {
                firstDate = firstDate.minusDays(1);
            }
        }

        selectedMonth = firstDate.getMonth();
        ZonedDateTime appointmentFirstDate = getAppointmentsFirstDate(firstDate);
        this.sortedAppointments = getAppointments(appointmentFirstDate, true);

        this.lblMonth.setText(new MyDatePick(date.getYear(), date.getMonthValue()).toString());
        MonthControl monthControl = monthFactory.build(sortedAppointments, appointmentFirstDate, getNumberOfWeeks(), this);
        currentMonth = monthControl;
        currentMonthLoaded.setValue(true);
        months.add(currentMonth);

        this.setCenter(null);
        this.setCenter(monthControl);
        
        //I don't want to pass this in through the AddMonth method
        //and the setContentSize that passes in the bounds isn't called
        //until after the AddMonth method is called. Need a better way but 
        //this works for now
        Bounds bounds = MainController.getInstance().getMainPanelBoundsProperty().get();

        monthControl.setContentSize(bounds.getWidth(), bounds.getHeight());


    }
    
    private SortedSet<Appointment> getAppointments(ZonedDateTime appointmentFirstDate, boolean isMonthly) throws AppointmentException {
  
        ZonedDateTime endingDate;
        if (isMonthly) {
            endingDate = appointmentFirstDate.plusMonths(1);
        } else {
            endingDate = appointmentFirstDate.plusWeeks(1);
        }
        while (endingDate.getDayOfWeek() != DayOfWeek.SATURDAY) {
            endingDate = endingDate.plusDays(1);
        }
        ArrayList<Appointment> appointments = context.getMonthlyAppointments(appointmentFirstDate, endingDate);
        appointments.sort(Comparator.naturalOrder());
        return new TreeSet<>(appointments); 
    }

    private ZonedDateTime getAppointmentsFirstDate(ZonedDateTime firstDate)  {

        ZonedDateTime appointmentFirstDate = firstDate;

        while (appointmentFirstDate.getDayOfWeek() != DayOfWeek.SUNDAY) {
            appointmentFirstDate = appointmentFirstDate.minusDays(1);
        } 
        return appointmentFirstDate;
    }

    public Stage getStage(){
        return (Stage) this.getScene().getWindow();
    }
    
    @Override
    public Month getSelectedMonth() {
        return selectedMonth;
    }
 
    
    private ReadOnlyObjectProperty<Bounds> mainBounds;
    @Override
    public void setContentSize(ReadOnlyObjectProperty<Bounds> readOnlyBounds) {
  
       mainBounds = readOnlyBounds;
       
        {
        Bounds bounds = readOnlyBounds.get();
            double width = bounds.getWidth();
            double height = bounds.getHeight();
 
            MonthControl monthControl = (MonthControl)currentMonth;
            monthControl.setPrefHeight(this.getHeight());
            monthControl.setPrefWidth(this.getWidth());
            currentMonth.setContentSize(width, height);
        }
        
        
                
            
           readOnlyBounds.addListener((observable, oldValue, newValue) -> {

            double width = newValue.getWidth();
            double height = newValue.getHeight();
            MonthControl monthControl = (MonthControl)currentMonth;
            monthControl.setPrefHeight(this.getHeight());
            monthControl.setPrefWidth(this.getWidth());
            monthControl.setContentSize(width, height);
        });
    }

    @Override
    public void setContentSize(double width, double height) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}


