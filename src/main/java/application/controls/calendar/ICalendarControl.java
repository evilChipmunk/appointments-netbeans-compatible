package application.controls.calendar;

import exceptions.AppointmentException;
import javafx.collections.ObservableList;
import java.time.Month;
import java.time.ZonedDateTime;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Bounds;

public interface ICalendarControl extends ICalandarPart{
    ObservableList<IMonthPart> getMonths();
    int getMonthCount();
    ZonedDateTime getFirstDate(); 

    int getNumberOfWeeks();

    void AddMonth(ZonedDateTime date) throws AppointmentException;
    IMonthPart getCurrentMonth();
    double getHeight();
    double getWidth();
    Month getSelectedMonth();
    void setContentSize(ReadOnlyObjectProperty<Bounds> readOnlyBounds);
}
