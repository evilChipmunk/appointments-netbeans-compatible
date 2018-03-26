package application.services;

import models.User;

public class ApplicationState implements IApplicationState {

    private User loggedInUser;

    @Override
    public User getLoggedInUser() {
        if (loggedInUser == null) {
            loggedInUser = new User("system", "", false);
        }
        return loggedInUser;
    }

    @Override
    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
}
