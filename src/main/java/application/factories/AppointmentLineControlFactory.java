package application.factories;

import application.dependencyInjection.IAppointmentLineControlFunc;
import application.controls.calendar.AppointmentLineControl;
import models.Appointment;

public class AppointmentLineControlFactory{
    private final IAppointmentLineControlFunc lineControlFunc;

    public AppointmentLineControlFactory(IAppointmentLineControlFunc lineControlFunc){

        this.lineControlFunc = lineControlFunc;
    }

    public AppointmentLineControl build(Appointment app){
        return lineControlFunc.apply(app);
    }
}
