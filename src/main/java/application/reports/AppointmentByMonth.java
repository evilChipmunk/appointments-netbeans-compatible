package application.reports;

import models.Appointment;

import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class AppointmentByMonth{
    private String month;
    private final String title;
    private final String customer;
    private String year;

    private static ArrayList<Integer> knownYears = new ArrayList<>();
    private static ArrayList<Month> knownMonths = new ArrayList<>();

    public AppointmentByMonth(Appointment app){

        Month knownMonth = app.getStart().getMonth();

        if (!knownMonths.contains(knownMonth)){
            knownMonths.add(knownMonth);
            this.month = knownMonth.getDisplayName(TextStyle.FULL, Locale.US);
        }


        int knownYear = app.getStart().getYear();
        if (!knownYears.contains(knownYear)){
            knownYears.add(knownYear);
            this.year = String.valueOf(knownYear);
        }

        this.title = app.getTitle();
        this.customer = app.getCustomer().getName();
    }

    public static ArrayList<String> getColumns() {

        knownYears = new ArrayList<>();
        knownMonths = new ArrayList<>();

        ArrayList<String> columns = new ArrayList<>();
        columns.add("Month");
        columns.add("Year");
        columns.add("Title");
        columns.add("Customer");
        return columns;
    }

    public String getMonth() {
        return month;
    }

    public String getTitle() {
        return title;
    }

    public String getCustomer() {
        return customer;
    }

    public String getYear() {
        return year;
    }
}



