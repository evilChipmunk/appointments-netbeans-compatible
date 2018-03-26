package application.services;

import dataAccess.ICheckedFunction;
import exceptions.AppointmentException;
import exceptions.ScheduleOverlapException; 
import javafx.beans.property.SimpleBooleanProperty;
import models.Appointment;
 

public interface IScheduler {

    Appointment Schedule(ICheckedFunction<Void, Appointment> appointmentAction) throws AppointmentException, ScheduleOverlapException;

    void RemoveFromSchedule(ICheckedFunction<Void, Appointment> appointmentAction) throws AppointmentException;

    SimpleBooleanProperty getHasAppointmentProperty();

    SimpleBooleanProperty getHasRemovalProperty();

    Appointment getAppointment();

    Appointment getRemovedAppointment();
}
