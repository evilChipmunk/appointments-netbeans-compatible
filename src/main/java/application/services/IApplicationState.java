package application.services;

import models.User;

public interface IApplicationState {

    User getLoggedInUser();

    void setLoggedInUser(User loggedInUser);
}
