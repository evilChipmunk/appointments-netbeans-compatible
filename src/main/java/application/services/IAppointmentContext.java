package application.services;

import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.Appointment;
import models.Customer; 
import java.util.ArrayList;

public interface IAppointmentContext {

    ArrayList<Customer> getCustomers() throws AppointmentException, ValidationException;

    Appointment save(Appointment appointment) throws AppointmentException;

    Appointment remove(Appointment appointment) throws AppointmentException;
}
