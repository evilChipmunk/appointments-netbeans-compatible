package application.services;

import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.Appointment;
import models.Customer;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public interface ICalendarContext {

    ArrayList<Customer> getCustomers() throws AppointmentException, ValidationException;

    ArrayList<Appointment> getAppointments(int customerId) throws AppointmentException, ValidationException;

    ArrayList<Appointment> getAppointments() throws AppointmentException, ValidationException;

    ArrayList<Appointment> getMonthlyAppointments(ZonedDateTime startingDate, ZonedDateTime endingDate) throws AppointmentException, ValidationException;
;

}
