package application.controls.calendar;

import application.factories.WeekControlFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import models.Appointment;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;

public class MonthControl extends GridPane implements Comparable<MonthControl>, IMonthPart {

    private final ZonedDateTime monthFirstDate;
    private final int weekCount;
    private final WeekControlFactory weekFactory;
    private final ICalandarPart parent;
    private final SortedSet<Appointment> sortedAppointments;
    private final RowConstraints rowHeader = new RowConstraints();
    private final RowConstraints rowBody = new RowConstraints();
    private final Label lblSunday = new Label("Sunday");
    private final Label lblMonday = new Label("Monday");
    private final Label lblTuesday = new Label("Tuesday");
    private final Label lblWednesday = new Label("Wednesday");
    private final Label lblThursday = new Label("Thursday");
    private final Label lblFriday = new Label("Friday");
    private final Label lblSaturday = new Label("Saturday");
    private final GridPane monthGrid = new GridPane();
    private final GridPane weekGrid = new GridPane();
    private final ObservableList<IWeekPart> weeks = FXCollections.observableArrayList();

    public MonthControl(SortedSet<Appointment> sortedAppointments, ZonedDateTime firstDate, int weekCount, WeekControlFactory weekFactory,
            ICalandarPart parent) {
        this.weekCount = weekCount;
        this.weekFactory = weekFactory;
        this.parent = parent;
        this.sortedAppointments = sortedAppointments;
        this.monthFirstDate = firstDate;

        if (sortedAppointments == null) {
            throw new IllegalArgumentException("Appointments are required for Month control");
        }

        if (monthFirstDate == null) {
            throw new IllegalArgumentException("First date is required for Month control");
        }

        setupCSS();

        setTodayBold();

        createGrid();

        addWeeks(sortedAppointments, monthFirstDate);

    }

    private void setupCSS() {

        this.getStylesheets().add("styles/month.css");
        this.getStyleClass().add("body");
    }

    private void setTodayBold() {
        DayOfWeek dayOfWeek = ZonedDateTime.now().getDayOfWeek();
        switch (dayOfWeek) {
            case SUNDAY:
                lblSunday.setStyle("-fx-font-weight: bold");
                break;
            case MONDAY:
                lblMonday.setStyle("-fx-font-weight: bold");
                break;
            case TUESDAY:
                lblTuesday.setStyle("-fx-font-weight: bold");
                break;
            case WEDNESDAY:
                lblWednesday.setStyle("-fx-font-weight: bold");
                break;
            case THURSDAY:
                lblThursday.setStyle("-fx-font-weight: bold");
                break;
            case FRIDAY:
                lblFriday.setStyle("-fx-font-weight: bold");
                break;
            case SATURDAY:
                lblSaturday.setStyle("-fx-font-weight: bold");
                break;
        }
    }

    private void createGrid() {

        for (int i = 0; i < 7; i++) {
            ColumnConstraints constraint = new ColumnConstraints();
            constraint.setHgrow(Priority.SOMETIMES);
            constraint.setPercentWidth(14);
            monthGrid.getColumnConstraints().add(constraint);
        }
        monthGrid.getColumnConstraints().get(0).setPercentWidth(16);

        rowHeader.setVgrow(Priority.NEVER);
        rowHeader.setPrefHeight(50);
        monthGrid.getRowConstraints().add(rowHeader);

        rowBody.setPercentHeight(90);
        monthGrid.getRowConstraints().add(rowBody);

        monthGrid.add(lblSunday, 0, 0);
        monthGrid.add(lblMonday, 1, 0);
        monthGrid.add(lblTuesday, 2, 0);
        monthGrid.add(lblWednesday, 3, 0);
        monthGrid.add(lblThursday, 4, 0);
        monthGrid.add(lblFriday, 5, 0);
        monthGrid.add(lblSaturday, 6, 0);

        monthGrid.add(weekGrid, 0, 1, 7, 1);
    }

    public ZonedDateTime getFirstDate() {
        return monthFirstDate;
    }

    public Collection<IWeekPart> getWeeks() {
        return Collections.unmodifiableCollection(weeks);
    }

    private void addWeeks(SortedSet<Appointment> appointments, ZonedDateTime firstDay) {

        for (int w = 1; w < weekCount + 1; w++) {

            ZonedDateTime endingDate = firstDay.plusWeeks(1);
            ZonedDateTime finalFirstDay = firstDay;
            SortedSet<Appointment> sAppointments = new TreeSet<>(appointments.stream()
                    .filter(x -> (x.getStart().compareTo(finalFirstDay) >= 0)
                    && (x.getStart().isBefore(endingDate))).collect(Collectors.toList()));

            int finalW = w;
            WeekControl control = weekFactory.build(finalFirstDay, endingDate, finalW, sAppointments, this);
            control.setPrefWidth(this.getWidth());
            weeks.add(control);
            this.weekGrid.addRow(finalW - 1);
            GridPane.setConstraints(control, 0, finalW);
            this.weekGrid.getChildren().add((Node) control);
            firstDay = endingDate;
        }
        this.getChildren().add(monthGrid);
    }

    @Override
    public int compareTo(MonthControl o) {

        if (this.getFirstDate() == o.getFirstDate()) {
            return 0;
        } else if (this.getFirstDate().isBefore(o.getFirstDate())) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public ICalendarControl getCalendar() {
        return parent.getCalendar();
    }

    @Override
    public ObservableList<IWeekPart> getWeekParts() {
        return weeks;
    }

    @Override
    public int getWeekPartCount() {
        return weeks.size();
    }

    @Override
    public double getHeaderHeight() {
        return this.getHeight() * .10;
    }

    @Override
    public boolean hasAppointment(Appointment appointment) {
        return sortedAppointments.stream().anyMatch(x -> x.getId() == appointment.getId());
    }

    @Override
    public void setContentSize(double width, double height) {

        this.setPrefWidth(width);
        this.setPrefHeight(height);

        monthGrid.setPrefWidth(width);
        monthGrid.setPrefHeight(height);

        double weekHeight = height / this.getWeekPartCount();
        weekGrid.setPrefWidth(width);
        weekGrid.setPrefHeight(weekHeight);

        Iterator itt = weeks.iterator();
        while (itt.hasNext()) {
            WeekControl week = (WeekControl) itt.next();
            week.setContentSize(width, height);
        }
    }

}
