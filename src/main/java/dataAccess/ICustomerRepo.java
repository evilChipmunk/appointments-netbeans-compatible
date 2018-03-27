package dataAccess;

import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.Customer;

import java.util.ArrayList;

public interface ICustomerRepo extends IRepo<Customer> {

    ArrayList<Customer> getCustomers(Includes... includes) throws AppointmentException, ValidationException;

    public boolean hasAppointmentsWithAnotherUser(int customerId);
}
