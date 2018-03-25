package application.controls.calendar;

import javafx.collections.ObservableList;

public interface IWeekPart extends ICalandarPart{
    ObservableList<IDayPart> getDayParts();
    int getDayPartCcount();

}
