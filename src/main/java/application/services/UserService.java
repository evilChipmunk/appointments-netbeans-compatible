package application.services;


import dataAccess.IUserRepo;
import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.User;


public class UserService implements IUserService {
    private final IUserRepo userRepo;
    private final IApplicationState applicationState;


    public UserService(IUserRepo userRepo, IApplicationState applicationState){

        this.userRepo = userRepo;
        this.applicationState = applicationState;
    }


    public void registerUser(String userName, String password) throws AppointmentException {
        User user = new User(userName, password, true);
        userRepo.save(user);
    }

    public User authenticateUser(String userName, String password) throws AppointmentException, ValidationException {
        User currentLoggedInUser = userRepo.getUser(userName, password);
        applicationState.setLoggedInUser(currentLoggedInUser);
        return currentLoggedInUser;
    }

    public void logInUser(User user) {
        if (user != null){
            //write to text file user logged in
        }
    }

    public void logOutUser(User user) {
        applicationState.setLoggedInUser(null);
    }


    public User getCurrentLoggedInUser() {
        return applicationState.getLoggedInUser();
    }
}
