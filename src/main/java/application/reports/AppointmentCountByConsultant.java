package application.reports;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class AppointmentCountByConsultant {

    private final String consultant;
    private String year;
    private String month;
    private final String total;

    public AppointmentCountByConsultant(String consultant, int total) {
        this.consultant = consultant;
        this.total = String.valueOf(total);
    }

    public AppointmentCountByConsultant(String consultant, int year, int total) {
        this.consultant = consultant;
        this.year = String.valueOf(year);
        this.total = String.valueOf(total);
    }

    public AppointmentCountByConsultant(String consultant, Month month, int total) {
        this.consultant = consultant;
        this.month = month.getDisplayName(TextStyle.FULL, Locale.US);
        this.total = String.valueOf(total);
    }

    public static ArrayList<String> getColumns() {

        ArrayList<String> columns = new ArrayList<>();
        columns.add("Consultant");
        columns.add("Year");
        columns.add("Month");
        columns.add("Total");
        return columns;
    }

    public String getMonth() {
        return month;
    }

    public String getTotal() {
        return total;
    }

    public String getConsultant() {
        return consultant;
    }

    public String getYear() {
        return year;
    }
}
