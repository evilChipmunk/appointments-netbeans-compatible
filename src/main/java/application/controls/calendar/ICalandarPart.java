package application.controls.calendar;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Bounds;

public interface ICalandarPart {

    ICalendarControl getCalendar();

    void setContentSize(double width, double height);
}

