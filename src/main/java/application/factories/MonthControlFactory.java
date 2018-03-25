package application.factories;

import application.controls.calendar.ICalandarPart;
import application.controls.calendar.MonthControl;
import models.Appointment;

import java.time.ZonedDateTime;
import java.util.SortedSet;

public class MonthControlFactory{
    private final WeekControlFactory weekFactory;

    public MonthControlFactory(WeekControlFactory weekFactory){

        this.weekFactory = weekFactory;
    }

    public MonthControl build(SortedSet<Appointment> sortedAppointments, ZonedDateTime firstDate, int weekCount, ICalandarPart parent){
        return new MonthControl(sortedAppointments, firstDate, weekCount, weekFactory, parent);
    }
}
