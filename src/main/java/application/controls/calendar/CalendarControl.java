package application.controls.calendar;

import application.controllers.MainController;
import application.controls.MainPanelControl;
import application.factories.MonthControlFactory;
import application.messaging.Commands; 
import application.services.ICalendarContext;
import application.services.IScheduler;
import exceptions.AppointmentException;
import exceptions.ValidationException;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList; 
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane; 
import models.Appointment;
import java.time.DayOfWeek;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CalendarControl extends MainPanelControl implements ICalendarControl {

    private boolean isWeekly = false;
    private final SimpleBooleanProperty currentMonthLoaded = new SimpleBooleanProperty(false);
    private final ObservableList<IMonthPart> months = FXCollections.observableArrayList();
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

    private ZonedDateTime actualFirstDate;
    private ZonedDateTime monthDisplayBeginningDate;
    private ZonedDateTime monthDisplayEndingDate;
    private IMonthPart currentMonth;
    private SortedSet<Appointment> sortedAppointments;
    private Month selectedMonth;
    private final ICalendarContext context;
    private final IScheduler scheduler;
    private ReadOnlyObjectProperty<Bounds> mainBounds;
    

    public CalendarControl(ICalendarContext context, IScheduler scheduler, MonthControlFactory monthFactory) {
        this.context = context;
        this.scheduler = scheduler;
        this.monthFactory = monthFactory;
        this.actualFirstDate = ZonedDateTime.now();

        setupCSS();

        setupScheduler();

        setupImages();

        setupImageHovers();

        setupImageClicks();

        setupLinkClicks();

        setupHeader();

        setupMainContent();

        lblViewSelect.setText(weekly);

        lblMonth.setOnMouseEntered((MouseEvent event) -> {
            changeMonthAction(event.getScreenX(), event.getScreenY());
        });
    }

    private void setupCSS() {
        this.getStylesheets().add("styles/calendar.css");
        this.getStyleClass().add("body");

        hLeft.getStyleClass().add("calendarNav");
        hRight.getStyleClass().add("calendarNav");

        hLeft.setAlignment(Pos.TOP_LEFT);
        hRight.setAlignment(Pos.TOP_RIGHT);

        lblMonth.getStyleClass().add("calendarNavLink");
        lblViewSelect.getStyleClass().add("calendarNavLink");
    }

    private void setupScheduler() {

        scheduler.getHasAppointmentProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                appointmentScheduled();
            }
        });

        scheduler.getHasRemovalProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                appointmentRemoved();
            }
        });
    }

    private void setupImages() {
        imgLeft.setImage(new Image("/images/light-arrow-alt-circle-left.png"));
        imgRight.setImage(new Image("/images/light-arrow-alt-circle-right.png"));

        imgLeft.setPreserveRatio(true);
        imgLeft.setFitHeight(50);

        imgRight.setPreserveRatio(true);
        imgRight.setFitHeight(50);

    }

    private void setupImageHovers() {

        this.vLeft.setOnMouseEntered(this::leftEntered);
        this.vRight.setOnMouseEntered(this::rightEntered);

        this.vLeft.setOnMouseExited(this::leftExited);
        this.vRight.setOnMouseExited(this::rightExited);
    }

    private void setupImageClicks() {
        this.vLeft.setOnMouseClicked(this::leftClicked);
        this.vRight.setOnMouseClicked(this::rightClicked);
    }

    private void setupLinkClicks() {

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

    private void setupMainContent() {
        this.setCenter(monthPane);
    }

    @Override
    public ICalendarControl getCalendar() {
        return this;
    }

    @Override
    public ZonedDateTime getActualFirstDate() {
        return actualFirstDate;
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
    public int getNumberOfWeeks() {
        if (isWeekly) {
            return 1;
        }
        //this controls how many weeks are visible on the screen
        return 5;
    }

    public void leftEntered(MouseEvent event) {
        imgLeft.setImage(new Image("/images/arrow-alt-circle-left.png"));
    }

    public void leftExited(MouseEvent event) {
        imgLeft.setImage(new Image("/images/light-arrow-alt-circle-left.png"));
    }

    public void rightEntered(MouseEvent event) {
        imgRight.setImage(new Image("/images/arrow-alt-circle-right.png"));
    }

    public void rightExited(MouseEvent event) {
        imgRight.setImage(new Image("/images/light-arrow-alt-circle-right.png"));
    }
 
    public void changeMonth(javafx.event.ActionEvent event) {

        changeMonthAction(0, 0);
    }

    private void changeMonthAction(double x, double y) {

        this.listener.actionPerformed(Commands.monthPickerShown, x, y);
    }

    public void changeView(javafx.event.ActionEvent event) throws AppointmentException {

        isWeekly = lblViewSelect.getText().equals(weekly);

        if (isWeekly) {
            lblViewSelect.setText(monthly);
            AddMonth(this.actualFirstDate);
        } else {
            lblViewSelect.setText(weekly);
            AddMonth(this.actualFirstDate);
        }
    }

    public void leftClicked(MouseEvent event) throws AppointmentException {

        if (this.isWeekly) {
            this.actualFirstDate = this.actualFirstDate.minusWeeks(1);
            navigateWeek(this.actualFirstDate);
        } else {
            this.actualFirstDate = this.actualFirstDate.minusMonths(1);
            AddMonth(this.actualFirstDate);
        }
    }

    public void rightClicked(MouseEvent event) throws AppointmentException {

        if (this.isWeekly) {
            this.actualFirstDate = this.actualFirstDate.plusWeeks(1);
            navigateWeek(this.actualFirstDate);
        } else {
            this.actualFirstDate = this.actualFirstDate.plusMonths(1);
            AddMonth(this.actualFirstDate);
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
            });
        }
    }

    private ZonedDateTime getFloorDate(ZonedDateTime date){
        
        return ZonedDateTime.of(date.getYear(), 
                date.getMonthValue(), 
                date.getDayOfMonth(), 0, 0, 0, 0, ZoneId.systemDefault());
    }

    private ZonedDateTime getActualMonthStartDate(ZonedDateTime date) {

        ZonedDateTime monthStart = date;
        monthStart = monthStart.with(TemporalAdjusters.firstDayOfMonth());
        return monthStart;
    }

    private ZonedDateTime getMonthDisplayBeginningDate(ZonedDateTime actualStartDate) {

        ZonedDateTime monthStart = actualStartDate;
        monthStart = monthStart.with(TemporalAdjusters.firstDayOfMonth());
        while (monthStart.getDayOfWeek() != DayOfWeek.SUNDAY) {
            monthStart = monthStart.minusDays(1);
        }
        return monthStart;
    }

    private ZonedDateTime getMonthDisplayEndingDate(ZonedDateTime date) {

        ZonedDateTime monthStart = date;
        monthStart = monthStart.with(TemporalAdjusters.lastDayOfMonth());
        while (monthStart.getDayOfWeek() != DayOfWeek.SATURDAY) {
            monthStart = monthStart.plusDays(1);
        }
        return monthStart;
    }

    private ZonedDateTime getAppointmentLastDate(ZonedDateTime startingDate, ZonedDateTime endingDate) {

        ZonedDateTime appEndDate = endingDate;
  
        while (appEndDate.getDayOfWeek() != DayOfWeek.SATURDAY) {
            appEndDate = appEndDate.plusDays(1);
        }

        return appEndDate;
    }
    
    private ZonedDateTime getAppointmentLastDate(ZonedDateTime startingDate) {

        ZonedDateTime appEndDate = startingDate.plusWeeks(1); 
        
        while (appEndDate.getDayOfWeek() != DayOfWeek.SATURDAY) {
            appEndDate = appEndDate.plusDays(1);
        }

        return appEndDate;
    }


    @Override
    public void AddMonth(ZonedDateTime date) throws ValidationException, AppointmentException {

        date = getFloorDate(date);
        actualFirstDate = getActualMonthStartDate(date);
        selectedMonth = actualFirstDate.getMonth();
        this.lblMonth.setText(new MyDatePick(actualFirstDate.getYear(), actualFirstDate.getMonthValue()).toString());
        
        monthDisplayBeginningDate = getMonthDisplayBeginningDate(date);
        monthDisplayEndingDate = getMonthDisplayEndingDate(date);

        ZonedDateTime appointmentFirstDate = monthDisplayBeginningDate;
        ZonedDateTime appointmentLastDate = getAppointmentLastDate(appointmentFirstDate, monthDisplayEndingDate);

        this.sortedAppointments = getAppointments(appointmentFirstDate, appointmentLastDate);

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
    
    private void navigateWeek(ZonedDateTime date){
          
        date = getFloorDate(date);
        selectedMonth = actualFirstDate.getMonth();  
        this.lblMonth.setText(new MyDatePick(actualFirstDate.getYear(), actualFirstDate.getMonthValue()).toString());

        ZonedDateTime appointmentFirstDate = actualFirstDate;
        ZonedDateTime appointmentLastDate = getAppointmentLastDate(appointmentFirstDate);

        this.sortedAppointments = getAppointments(appointmentFirstDate, appointmentLastDate);

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

    private SortedSet<Appointment> getAppointments(ZonedDateTime startingDate, ZonedDateTime endingDate) throws AppointmentException {

        ArrayList<Appointment> appointments = context.getMonthlyAppointments(startingDate, endingDate);
        appointments.sort(Comparator.naturalOrder());
        return new TreeSet<>(appointments);
    }
 
    @Override
    public Month getSelectedMonth() {
        return selectedMonth;
    }
 
    @Override
    public void setContentSize(ReadOnlyObjectProperty<Bounds> readOnlyBounds) {

        mainBounds = readOnlyBounds;

        {
            Bounds bounds = readOnlyBounds.get();
            double width = bounds.getWidth();
            double height = bounds.getHeight();

            MonthControl monthControl = (MonthControl) currentMonth;
            monthControl.setPrefHeight(this.getHeight());
            monthControl.setPrefWidth(this.getWidth());
            currentMonth.setContentSize(width, height);
        }

        readOnlyBounds.addListener((observable, oldValue, newValue) -> {

            double width = newValue.getWidth();
            double height = newValue.getHeight();
            MonthControl monthControl = (MonthControl) currentMonth;
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
