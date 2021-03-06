package dataAccess;

import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.User;

public interface IUserRepo extends IRepo<User> {

    User getUser(String userName, String password) throws AppointmentException, ValidationException;
}
