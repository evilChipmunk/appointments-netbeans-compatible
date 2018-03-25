package dataAccess;

import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.Country;

import java.sql.SQLException;
import java.util.ArrayList;

public interface ICountryRepo extends IRepo<Country>{

    ArrayList<Country> getCountries() throws AppointmentException, ValidationException;
}

