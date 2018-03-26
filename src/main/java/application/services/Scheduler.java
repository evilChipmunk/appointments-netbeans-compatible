package application.services;

import dataAccess.ICheckedFunction;
import exceptions.AppointmentException;
import javafx.beans.property.SimpleBooleanProperty;
import models.Appointment;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class Scheduler implements IScheduler {

    private final Queue<Appointment> appointments = new LinkedList<>();
    private final Queue<Appointment> appointmentRemovals = new LinkedList<>();

    private final SimpleBooleanProperty hasAppointmentProperty = new SimpleBooleanProperty();
    private final SimpleBooleanProperty hasRemovalProperty = new SimpleBooleanProperty();

    public Scheduler() {
    }

    @Override
    public Appointment Schedule(ICheckedFunction<Void, Appointment> appointmentAction) throws AppointmentException {
        Appointment appointment = null;
        try {
            appointment = appointmentAction.apply(null);
        } catch (SQLException e) {
            throw new AppointmentException("Error while trying to load appointment in scheduler", e);
        }
        appointments.add(appointment);
        hasAppointmentProperty.set(true);
        return appointment;
    }

    @Override
    public void RemoveFromSchedule(ICheckedFunction<Void, Appointment> appointmentAction) throws AppointmentException {

        Appointment appointment = null;
        try {
            appointment = appointmentAction.apply(null);
        } catch (SQLException e) {
            throw new AppointmentException("Error while trying to remove appointment in scheduler", e);
        }
        appointmentRemovals.add(appointment);
        hasRemovalProperty.set(true);
    }

    @Override
    public SimpleBooleanProperty getHasAppointmentProperty() {
        return hasAppointmentProperty;
    }

    @Override
    public SimpleBooleanProperty getHasRemovalProperty() {
        return hasRemovalProperty;
    }

    @Override
    public Appointment getAppointment() {

        Appointment appointment = appointments.poll();
        if (appointments.isEmpty()) {
            hasAppointmentProperty.set(false);
        }

        return appointment;
    }

    @Override
    public Appointment getRemovedAppointment() {

        Appointment appointment = appointmentRemovals.poll();
        if (appointmentRemovals.isEmpty()) {
            hasRemovalProperty.set(false);
        }

        return appointment;
    }

}
