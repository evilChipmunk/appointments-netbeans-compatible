package application.factories;

import application.controls.calendar.ICalandarPart;
import application.controls.calendar.WeekControl;
import models.Appointment;

import java.time.ZonedDateTime;
import java.util.SortedSet;

public class WeekControlFactory{

    private final DayControlFactory dayFactory;

    public WeekControlFactory(DayControlFactory dayFactory){

        this.dayFactory = dayFactory;
    }

    public WeekControl build(ZonedDateTime startingDate, ZonedDateTime endingDate
            , int week, SortedSet<Appointment> appointments, ICalandarPart parent){
        return new WeekControl(startingDate, endingDate, week   , appointments, dayFactory, parent);
    }
}

