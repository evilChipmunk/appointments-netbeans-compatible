package application.controls.calendar;
 
import javafx.collections.ObservableList;
import models.Appointment; 

public interface IMonthPart extends ICalandarPart {
    ObservableList<IWeekPart> getWeekParts();
    int getWeekPartCount();
    double getHeaderHeight();
    double getHeight();
    double getWidth();
    boolean hasAppointment(Appointment appointment); 
}
