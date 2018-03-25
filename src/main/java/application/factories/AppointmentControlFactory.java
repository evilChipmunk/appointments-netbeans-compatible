package application.factories;

import application.Configuration;
import application.dependencyInjection.IAppointmentControlFunc;
import application.controls.screens.AppointmentControl;
import application.services.IAppointmentContext;
import application.services.IScheduler;
import models.Appointment;

import java.time.ZonedDateTime;

public class AppointmentControlFactory{

    private final IAppointmentContext context;
    private final IScheduler scheduler;
    private final Configuration config;
    private final IAppointmentControlFunc controlFunc;

    public AppointmentControlFactory(IAppointmentContext context, IScheduler scheduler, Configuration config, IAppointmentControlFunc controlFunc){

        this.context = context;
        this.scheduler = scheduler;
        this.config = config;
        this.controlFunc = controlFunc;
    }

    public AppointmentControl build(){
        return new AppointmentControl(context, scheduler, config);
    }

    public AppointmentControl build(Appointment appointment){
        return controlFunc.apply(appointment);
    }

    public AppointmentControl build(ZonedDateTime date) {
        return new AppointmentControl(context, scheduler, config, date);
    }
}


