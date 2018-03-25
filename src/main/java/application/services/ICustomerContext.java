package application.services;

import dataAccess.Includes;
import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.*;

import java.sql.SQLException;
import java.util.ArrayList;

public interface ICustomerContext {
    ArrayList<Customer> getCustomers() throws AppointmentException, ValidationException;

    void saveCustomer(Customer customer) throws AppointmentException;

    Address getAddress(int id, Includes...includes) throws AppointmentException, ValidationException;

    ArrayList<City> getCities() throws AppointmentException, ValidationException;

    ArrayList<Country> getCountries() throws AppointmentException, ValidationException;

    boolean delete(Customer customer) throws AppointmentException;
}


