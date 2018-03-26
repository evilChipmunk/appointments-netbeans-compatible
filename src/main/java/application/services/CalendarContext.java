package application.services;

import dataAccess.IAppointmentRepo;
import dataAccess.ICustomerRepo;
import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.Appointment;
import models.Customer;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class CalendarContext implements ICalendarContext {

    private final ICustomerRepo customerRepo;
    private final IAppointmentRepo appointmentRepo;

    public CalendarContext(ICustomerRepo customerRepo, IAppointmentRepo appointmentRepo) {

        this.customerRepo = customerRepo;
        this.appointmentRepo = appointmentRepo;
    }

    @Override
    public ArrayList<Customer> getCustomers() throws AppointmentException, ValidationException {
        return customerRepo.getCustomers();
    }

    @Override
    public ArrayList<Appointment> getAppointments(int customerId) throws AppointmentException, ValidationException {
        return appointmentRepo.getAppointments(customerId);
    }

    @Override
    public ArrayList<Appointment> getAppointments() throws AppointmentException, ValidationException {
        return appointmentRepo.getAppointments();
    }

    @Override
    public ArrayList<Appointment> getMonthlyAppointments(ZonedDateTime startingate, ZonedDateTime endingDate) throws AppointmentException, ValidationException {
        return appointmentRepo.getMonthlyAppointments(startingate, endingDate);
    }
}
