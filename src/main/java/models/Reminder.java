package models;

import java.time.ZonedDateTime;

public class Reminder extends BaseEntity {

    private final ZonedDateTime reminderDate;
    private final int snoozeIncrement;
    private final IncrementType snoozeIncrementType;
    private final Appointment appointment;

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

    public int getSnoozeIncrement() {
        return snoozeIncrement;
    }

    public IncrementType getSnoozeIncrementType() {
        return snoozeIncrementType;
    }

    public Appointment getAppointment() {
        return appointment;
    }

}
