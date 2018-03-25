package application.controls.calendar;

import models.Appointment;

import java.time.ZonedDateTime;

public interface IDayPart extends  ICalandarPart{

    ZonedDateTime getDate();

    boolean hasAppointment(Appointment appointment);
    boolean schedulesForThisDay(ZonedDateTime date);
    boolean addAppointment(Appointment appointment);
    boolean changeAppointment(Appointment appointment);

    void removeAppointment(Appointment appointment);
}
