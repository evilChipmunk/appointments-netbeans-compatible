package application.services;

import dataAccess.IAppointmentRepo;
import models.Appointment;

import java.util.SortedSet;
import java.util.TreeSet;

public class ReportContext implements IReportContext {

    private final IAppointmentRepo appRepo;

    public ReportContext(IAppointmentRepo appRepo) {
        this.appRepo = appRepo;
    }

    @Override
    public SortedSet<Appointment> getAppointments() {

        SortedSet<Appointment> apps = new TreeSet<>(appRepo.getAppointments());
        return apps;
    }
}
