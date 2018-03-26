package application.services;

import dataAccess.IUserRepo;
import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.User;

public class UserService implements IUserService {

    private final IUserRepo userRepo;
    private final IApplicationState applicationState;

    public UserService(IUserRepo userRepo, IApplicationState applicationState) {

        this.userRepo = userRepo;
        this.applicationState = applicationState;
    }

    @Override
    public void registerUser(String userName, String password) throws AppointmentException {
        User user = new User(userName, password, true);
        userRepo.save(user);
    }

    @Override
    public User authenticateUser(String userName, String password) throws AppointmentException, ValidationException {
        User currentLoggedInUser = userRepo.getUser(userName, password);
        applicationState.setLoggedInUser(currentLoggedInUser);
        return currentLoggedInUser;
    }

    @Override
    public void logInUser(User user) {
        if (user != null) {
            //write to text file user logged in
        }
    }

    @Override
    public void logOutUser(User user) {
        applicationState.setLoggedInUser(null);
    }

    @Override
    public User getCurrentLoggedInUser() {
        return applicationState.getLoggedInUser();
    }
}
