package application.services;

import models.User;

public class ApplicationState implements IApplicationState{
    private User loggedInUser;


    public User getLoggedInUser() {
        if (loggedInUser == null){
            loggedInUser = new User("system", "", false);
        }
        return loggedInUser;
    }


    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
}
