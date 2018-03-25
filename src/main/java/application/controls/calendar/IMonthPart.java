package application.controls.calendar;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import models.Appointment;

import java.time.Month;
import java.time.ZonedDateTime;
import java.util.SortedSet;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Bounds;

public interface IMonthPart extends ICalandarPart {
    ObservableList<IWeekPart> getWeekParts();
    int getWeekPartCount();
    double getHeaderHeight();
    double getHeight();
    double getWidth();
    boolean hasAppointment(Appointment appointment); 
}
