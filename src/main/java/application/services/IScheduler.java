package application.services;

import dataAccess.ICheckedFunction;
import exceptions.AppointmentException;
import exceptions.ScheduleOverlapException;
import exceptions.ValidationException;
import javafx.beans.property.SimpleBooleanProperty;
import models.Appointment;

import java.sql.SQLException;

public interface IScheduler {
    Appointment Schedule(ICheckedFunction<Void, Appointment> appointmentAction) throws AppointmentException, ScheduleOverlapException;
    void RemoveFromSchedule(ICheckedFunction<Void, Appointment> appointmentAction)throws AppointmentException;

    SimpleBooleanProperty getHasAppointmentProperty();
    SimpleBooleanProperty getHasRemovalProperty();

    Appointment getAppointment();
    Appointment getRemovedAppointment();
}
