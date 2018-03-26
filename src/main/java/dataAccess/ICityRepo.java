package dataAccess;

import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.City;

import java.util.ArrayList;

public interface ICityRepo extends IRepo<City> {

    ArrayList<City> getCities() throws AppointmentException, ValidationException;
}
