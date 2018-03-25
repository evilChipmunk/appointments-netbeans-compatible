package dataAccess;

import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.Appointment;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;


public interface IAppointmentRepo extends IRepo<Appointment>{
    ArrayList<Appointment> getAppointments(int customerId) throws AppointmentException, ValidationException;
    ArrayList<Appointment> getAppointments() throws AppointmentException, ValidationException;
    ArrayList<Appointment> getMonthlyAppointments(ZonedDateTime startingDate, ZonedDateTime endingDate) throws AppointmentException, ValidationException;
}

