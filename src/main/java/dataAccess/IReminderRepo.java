package dataAccess;

import exceptions.AppointmentException;
import models.Appointment;
import models.Reminder;

import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;

public interface IReminderRepo extends IRepo<Reminder>{

    ArrayList<Reminder> getReminders(ZonedDateTime startingDate, ZonedDateTime endingDate, String userName) throws AppointmentException;
}
