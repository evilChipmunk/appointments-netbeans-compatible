package application.services;

import application.Configuration;
import dataAccess.IReminderRepo;
import exceptions.AppointmentException;
import models.Reminder;

import java.sql.SQLException;

public class ReminderContext{
    private final IReminderRepo repo;
    private final IApplicationState state;

    public ReminderContext(IReminderRepo repo, IApplicationState state, Configuration config){

        this.repo = repo;
        this.state = state;
    }

    public void save(Reminder reminder) throws AppointmentException {
        repo.save(reminder);
    }

}
