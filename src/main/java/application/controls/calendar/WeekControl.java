package application.controls.calendar;

import application.factories.DayControlFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.*;
import models.Appointment;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class WeekControl extends GridPane implements IWeekPart {

    private final ZonedDateTime startingDate;
    private final ZonedDateTime endingDate;
    private final int week;
    private final ObservableList<IDayPart> days = FXCollections.observableArrayList();
    private final ICalandarPart parent;
    private final DayControlFactory factory;
    private final GridPane dayPane = new GridPane();

    ;

    @Override
    public ICalendarControl getCalendar() {
        return parent.getCalendar();
    }

    public WeekControl(ZonedDateTime startingDate, ZonedDateTime endingDate,
             int week, SortedSet<Appointment> appointments, DayControlFactory factory, ICalandarPart parent) {
        this.parent = parent;
        this.factory = factory;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.week = week;

        if (appointments == null) {
            throw new IllegalArgumentException("Appointments are required for Week control");
        }

        if (startingDate == null) {
            throw new IllegalArgumentException("Starting date is required for Week control");
        }

        if (endingDate == null) {
            throw new IllegalArgumentException("Ending date is required for Week control");
        }

        if (week == 0) {
            throw new IllegalArgumentException("Week number is required for Week control");
        }

        setupCSS();

        setupConstraints();

        setupDayPane();

        addDays(appointments);
    }

    private void setupCSS() {

        this.getStylesheets().add("styles/week.css");
        this.getStyleClass().add("body");
    }

    private void setupConstraints() {

        ColumnConstraints weekConstraint = new ColumnConstraints();
        weekConstraint.setHgrow(Priority.SOMETIMES);
        weekConstraint.setPercentWidth(100);
        this.getColumnConstraints().add(weekConstraint);
    }

    @Override
    public void setContentSize(double width, double height) {

        this.setPrefWidth(width);
        this.setPrefHeight(height);
        dayPane.setPrefHeight(height);
        dayPane.setPrefWidth(width);

        double dayWidth = width / 7;

        Iterator itt = days.iterator();
        while (itt.hasNext()) {
            DayControl day = (DayControl) itt.next();
            day.setContentSize(dayWidth, height);
        }
    }

    private void setupDayPane() {

        for (int i = 0; i < 7; i++) {
            ColumnConstraints constraint = new ColumnConstraints();
            constraint.setHgrow(Priority.SOMETIMES);
            constraint.setPercentWidth(14);
            dayPane.getColumnConstraints().add(constraint);
        }
        dayPane.getColumnConstraints().get(0).setPercentWidth(16);

        dayPane.setPrefHeight(this.getHeight());
        dayPane.setPrefWidth(this.getWidth());
    }

    private void addDays(SortedSet<Appointment> appointments) {

        for (int i = 0; i < 7; i++) {
            ZonedDateTime date = startingDate.plusDays(i);
            SortedSet<Appointment> sortedAppointments = filterAppointments(appointments, date);

            int finalI = i;
            DayControl day = factory.build(date, sortedAppointments, this);
            days.add(day);
            GridPane.setConstraints(day, finalI, 0);
            this.dayPane.getChildren().add(day);
        }
        this.getChildren().add(dayPane);
    }

    public Collection<IDayPart> getDays() {
        return Collections.unmodifiableCollection(days);
    }

    private SortedSet<Appointment> filterAppointments(SortedSet<Appointment> appointments, ZonedDateTime date) {

        final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

        List<Appointment> filtered = appointments.stream()
                .filter(x -> formatter.format(x.getStart()).equals(formatter.format(date))).collect(Collectors.toList());
        TreeSet<Appointment> sorted = new TreeSet<>(filtered);
        return sorted;
    }

    @Override
    public ObservableList<IDayPart> getDayParts() {
        return days;
    }

    @Override
    public int getDayPartCcount() {
        return days.size();
    }

}
