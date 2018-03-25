package dataAccess;

import application.Configuration;
import application.services.IApplicationState;
import application.services.IUserService;
import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.*;

public class ReminderRepo extends BaseRepo<Reminder> implements IReminderRepo {

    private final IIncrementTypeRepo incrementTypeRepo;
    private final IAppointmentRepo appointmentRepo;

    public ReminderRepo(Configuration config, IApplicationState applicationState,
                        IIncrementTypeRepo incrementTypeRepo, IAppointmentRepo appointmentRepo, ISqlRetryPolicy policy) {
        super(config, applicationState, policy);
        this.incrementTypeRepo = incrementTypeRepo;
        this.appointmentRepo = appointmentRepo;
    }


    @Override
    protected String getByIdProc() {
        return "sp_GetReminderById";
    }

    @Override
    protected String getDeleteProc() {
        return "sp_DeleteReminderById";
    }

    @Override
    protected String getSaveProc() {
        return "sp_SaveReminder";
    }

    @Override
    protected ArrayList<ParameterInfo> getSaveParams(Reminder entity) {
        ArrayList<ParameterInfo>  dictionary = new ArrayList<ParameterInfo> ();
         dictionary.add(new ParameterInfo("id", entity.getId()));

        java.sql.Date reminderDate = DatabaseDateTimeConverter.getSqlDate(entity.getReminderDate());

         dictionary.add(new ParameterInfo("reminderDate", reminderDate));
         dictionary.add(new ParameterInfo("snoozeIncrement", entity.getSnoozeIncrement()));
         dictionary.add(new ParameterInfo("incrementTypeId", entity.getSnoozeIncrementType().getId()));
         dictionary.add(new ParameterInfo("appointmentId", entity.getAppointment().getId()));
         addAuditParams(dictionary, entity);
         return dictionary;
    }

    @Override
    protected Reminder mapEntity(ResultSet results, Includes... includes) throws SQLException, ValidationException {
        int id = results.getInt("id");
        ZonedDateTime reminderDate = DatabaseDateTimeConverter.getZoneDateTime(results.getTimestamp("ReminderDate"));

        int snoozeIncrement = results.getInt("SnoozeIncrement");
        int appointmentId = results.getInt("AppointmentId");
        AuditInfo audit = createAudit(results);

        int incrementTypeId = results.getInt("IncrementTypeId");
       IncrementType incrementType = IncrementType.getById(incrementTypeId);
        Appointment appointment = appointmentRepo.getById(appointmentId);
        return new Reminder(id, reminderDate, snoozeIncrement, incrementType, appointment, audit);
    }

    @Override
    public ArrayList<Reminder> getReminders(ZonedDateTime startingDate, ZonedDateTime endingDate, String userName) throws AppointmentException {
        String statement = "sp_GetRemindersByDateAndUser";
        ArrayList<ParameterInfo> dictionary = new ArrayList<ParameterInfo>();
        dictionary.add(new ParameterInfo("name", userName));


        java.sql.Timestamp utcStart = DatabaseDateTimeConverter.getSqlTimestamp(startingDate);
        java.sql.Timestamp utcEnd = DatabaseDateTimeConverter.getSqlTimestamp(endingDate);


        dictionary.add(new ParameterInfo("start", utcStart));
        dictionary.add(new ParameterInfo("end", utcEnd));

        return executeResultList(statement, dictionary);
    }
}
