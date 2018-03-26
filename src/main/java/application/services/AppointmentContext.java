package application.services;

import application.Configuration;
import dataAccess.IAppointmentRepo;
import dataAccess.ICustomerRepo;
import dataAccess.IReminderRepo;
import exceptions.AppointmentException;
import exceptions.ScheduleOverlapException;
import exceptions.ValidationException;
import models.Appointment;
import models.Customer;
import models.IncrementType;
import models.Reminder;

import java.time.ZonedDateTime;
import java.util.ArrayList;

public class AppointmentContext implements IAppointmentContext {

    private final ICustomerRepo customerRepo;
    private final IAppointmentRepo appointmentRepo;
    private final IReminderRepo remRepo;
    private final IApplicationState state;
    private final Configuration config;

    public AppointmentContext(ICustomerRepo customerRepo, IAppointmentRepo appointmentRepo,
            IReminderRepo remRepo,
            IApplicationState state,
            Configuration config) {

        this.customerRepo = customerRepo;
        this.appointmentRepo = appointmentRepo;
        this.remRepo = remRepo;
        this.state = state;
        this.config = config;
    }

    @Override
    public ArrayList<Customer> getCustomers() throws AppointmentException, ValidationException {
        return customerRepo.getCustomers();
    }

    @Override
    public Appointment save(Appointment appointment) throws AppointmentException {

        validate(appointment);

        Reminder rem = null;
        if (appointment.getId() == 0) {
            rem = new Reminder(appointment.getStart().minusMinutes(config.getReminderRange()), config.getDefaultSnoozeIncrement(),
                    IncrementType.getByDesription(IncrementType.Minutes), appointment);

        }
        appointment = appointmentRepo.save(appointment);

        if (rem != null) {
            remRepo.save(rem);
        }

        return appointment;
    }

    public Appointment remove(Appointment appointment) throws AppointmentException {
        appointmentRepo.delete(appointment);
        return appointment;
    }

    private void validate(Appointment appointment) throws AppointmentException {

        ArrayList<Appointment> scheduledAppointments
                = appointmentRepo.getMonthlyAppointments(appointment.getStart(), appointment.getEnd());

        for (Appointment scheduled : scheduledAppointments) {

            if (scheduled.getId() == appointment.getId()) {
                continue;
            }

            String appCreatedByName = appointment.getAudit().getCreatedBy();
            String schCreatedByName = scheduled.getAudit().getCreatedBy();
            String userName = state.getLoggedInUser().getName();

            //make sure any appointment for the logged in user doesn't overlap
            if ((appCreatedByName.equals(schCreatedByName) || (appCreatedByName.equals("system") && schCreatedByName.equals(userName)))

                    && isOverlap(appointment.getStart(), appointment.getEnd(),
                            scheduled.getStart(), scheduled.getEnd())) {
                throw new ScheduleOverlapException("This appointment cannot be saved because it overlaps another scheduled appointment");
            }
        }
    }

    private boolean isOverlap(ZonedDateTime startA, ZonedDateTime endA,
            ZonedDateTime startB, ZonedDateTime endB) {
        return (endB == null || startA == null || !startA.isAfter(endB))
                && (endA == null || startB == null || !endA.isBefore(startB));
    }
}
