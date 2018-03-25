package application.services;

import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.User;

import java.sql.SQLException;

public interface IUserService {
    void registerUser(String userName, String password) throws AppointmentException;
    User authenticateUser(String userName, String password) throws AppointmentException, ValidationException;
    void logInUser(User user);
    void logOutUser(User user);
    User getCurrentLoggedInUser();
}

