package models;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;

public class Reminder extends BaseEntity{
    private ZonedDateTime reminderDate;
    private int snoozeIncrement;
    private IncrementType snoozeIncrementType;
    private Appointment appointment;

    public Reminder(int id, ZonedDateTime reminderDate, int snoozeIncrement, IncrementType incrementType, Appointment appointment, AuditInfo audit) {
        super(id, audit);
        this.reminderDate = reminderDate;
        this.snoozeIncrement = snoozeIncrement;
        this.snoozeIncrementType = incrementType;
        this.appointment = appointment;
    }

    public Reminder(ZonedDateTime reminderDate, int defaultSnoozeIncrement, IncrementType incrementType, Appointment appointment) {
        super();
        this.reminderDate = reminderDate;
        this.snoozeIncrement = defaultSnoozeIncrement;
        this.snoozeIncrementType = incrementType;
        this.appointment = appointment;

    }

    public ZonedDateTime getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(ZonedDateTime reminderDate) {
        this.reminderDate = reminderDate;
    }

    public int getSnoozeIncrement() {
        return snoozeIncrement;
    }

    public void setSnoozeIncrement(int snoozeIncrement) {
        this.snoozeIncrement = snoozeIncrement;
    }

    public IncrementType getSnoozeIncrementType() {
        return snoozeIncrementType;
    }

    public void setSnoozeIncrementType(IncrementType snoozeIncrementType) {
        this.snoozeIncrementType = snoozeIncrementType;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }
}
