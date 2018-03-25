package application.factories;

import application.Configuration;
import application.controls.calendar.DayControl;
import application.controls.calendar.ICalandarPart;
import models.Appointment;

import java.time.ZonedDateTime;
import java.util.SortedSet;

public class DayControlFactory {

    private final AppointmentControlFactory appointmentControlFactory;
    private final AppointmentLineControlFactory lineControlFactory;
    private final Configuration config;

    public DayControlFactory(AppointmentControlFactory appointmentControlFactory,
                             AppointmentLineControlFactory lineControlFactory, Configuration config){
        this.appointmentControlFactory = appointmentControlFactory;
        this.lineControlFactory = lineControlFactory;

        this.config = config;
    }

    public DayControl build(ZonedDateTime date, SortedSet<Appointment> appointments, ICalandarPart parent){
        return new DayControl(date, appointments, parent, config, appointmentControlFactory, lineControlFactory);
    }
}

