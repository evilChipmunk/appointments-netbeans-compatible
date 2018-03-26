package dataAccess;

import exceptions.AppointmentException;
import models.Reminder;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public interface IReminderRepo extends IRepo<Reminder> {

    ArrayList<Reminder> getReminders(ZonedDateTime startingDate, ZonedDateTime endingDate, String userName) throws AppointmentException;
}
