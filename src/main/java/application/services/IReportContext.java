package application.services;

import models.Appointment;
 
import java.util.SortedSet;

public interface IReportContext {

    SortedSet<Appointment> getAppointments();

}
