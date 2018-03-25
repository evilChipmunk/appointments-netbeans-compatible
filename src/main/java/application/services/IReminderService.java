package application.services;

import javafx.beans.property.SimpleBooleanProperty;
import models.Reminder;

import java.util.ArrayList;

public interface IReminderService {
    ArrayList<Reminder> getReminders();
    SimpleBooleanProperty getHasReminders();
    void Start();
    void Stop();
    void Acknowledge(Reminder reminder);

}
