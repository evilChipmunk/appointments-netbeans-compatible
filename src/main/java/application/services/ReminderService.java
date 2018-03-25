package application.services;

import application.Configuration;
import application.logging.ILogger;
import dataAccess.IReminderRepo;
import exceptions.AppointmentException;
import javafx.beans.property.SimpleBooleanProperty;
import models.Reminder;

import java.sql.Array;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ReminderService implements IReminderService {

    private final IReminderRepo repo;
    private final IApplicationState state;
    private final ArrayList<Reminder> reminders;
    private final Configuration config;
    private final Timer timer;
    private final SimpleBooleanProperty hasReminders = new SimpleBooleanProperty(false);
    private final ArrayList<Integer> acknowledgedReminders = new ArrayList<>();
    private final ILogger logger;

    public ReminderService(IReminderRepo repo, IApplicationState state, Configuration config, ILogger logger){
        this.logger = logger;

        this.reminders = new ArrayList<>();
        this.repo = repo;
        this.state = state;
        this.config = config;
        this.timer = new Timer();


    }

    @Override
    public void Start(){

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                setReminders();
            }
        }, 100, config.getReminderCheck());
    }

    @Override
    public void Stop(){
        timer.cancel();
    }

    @Override
    public void Acknowledge(Reminder reminder){
        acknowledgedReminders.add(reminder.getId());
    }



    @Override
    public ArrayList<Reminder> getReminders(){
        hasReminders.set(false);
        return reminders;
    }

    private void setReminders() {

        try {
            reminders.clear();
            ArrayList<Reminder> rems = getUserReminders();
            for (Reminder app : rems) {

                if (!acknowledgedReminders.contains(app.getId())){
                    reminders.add(app);
                }

            }
            if (rems.size() > 0){
                hasReminders.set(true);
            }

        } catch (AppointmentException e) {
            logger.Log("Error while setting reminders", e);
        }
    }

    private ArrayList<Reminder> getUserReminders() throws AppointmentException {

        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime cutOff = now.plusMinutes(config.getReminderRange());

        return repo.getReminders(now, cutOff, state.getLoggedInUser().getName());
    }

    public SimpleBooleanProperty getHasReminders(){
        return hasReminders;
    }
}



