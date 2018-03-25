package dataAccess;

import application.Configuration;
import application.services.IApplicationState;
import application.services.IUserService;
import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.Appointment;
import models.AuditInfo;
import models.Customer;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

public class AppointmentRepo extends BaseRepo<Appointment> implements IAppointmentRepo{

    private final ICustomerRepo customerRepo;

    public AppointmentRepo(Configuration config, IApplicationState applicationState, ICustomerRepo customerRepo, ISqlRetryPolicy policy) {
        super(config, applicationState, policy);
        this.customerRepo = customerRepo;
    }


    @Override
    protected String getByIdProc() {
        return "sp_GetAppointmentById";
    }

    @Override
    protected String getDeleteProc() {
        return "sp_DeleteAppointmentById";
    }

    @Override
    protected String getSaveProc() {
        return "sp_SaveAppointment";
    }

    @Override
    protected ArrayList<ParameterInfo>  getSaveParams(Appointment entity) {
        ArrayList<ParameterInfo> dictionary = new ArrayList<ParameterInfo> ();
        dictionary.add(new ParameterInfo("appointmentId", entity.getId()));
        dictionary.add(new ParameterInfo("customerId", entity.getCustomer().getId()));
        dictionary.add(new ParameterInfo("title", entity.getTitle()));
        dictionary.add(new ParameterInfo("description", entity.getDescription()));
        dictionary.add(new ParameterInfo("location", entity.getLocation()));
        dictionary.add(new ParameterInfo("contact", entity.getContact()));
        dictionary.add(new ParameterInfo("uRL", entity.getUrl()));

        java.sql.Timestamp utcStart = DatabaseDateTimeConverter.getSqlTimestamp(entity.getStart());
        java.sql.Timestamp utcEnd = DatabaseDateTimeConverter.getSqlTimestamp(entity.getEnd());

        dictionary.add(new ParameterInfo("start", utcStart));
        dictionary.add(new ParameterInfo("end", utcEnd));
        addAuditParams(dictionary, entity);
        return dictionary;
    }

    @Override
    protected Appointment mapEntity(ResultSet results, Includes... includes) throws SQLException, ValidationException {
        int id = results.getInt("Id");
        int customerId = results.getInt("CustomerId");
        String title = results.getString("Title");
        String description = results.getString("Description");
        String location = results.getString("Location");
        String contact = results.getString("Contact");
        String url = results.getString("URL");
        ZonedDateTime start = DatabaseDateTimeConverter.getZoneDateTime(results.getTimestamp("Start"));
        ZonedDateTime end = DatabaseDateTimeConverter.getZoneDateTime(results.getTimestamp("End"));

        Customer customer = customerRepo.getById(customerId);
        AuditInfo audit = createAudit(results);
        boolean isPreExisting = true;
        return new Appointment(id, customer, title, description, location, contact, url , start, end, audit, config, isPreExisting);

    }

    @Override
    public ArrayList<Appointment> getAppointments(int customerId) throws AppointmentException, ValidationException {
        String statement = "sp_GetAppointmentByCustomerId";
        ArrayList<ParameterInfo> dictionary = new ArrayList<ParameterInfo>();
        dictionary.add(new ParameterInfo("customerId", customerId));
        return executeResultList(statement, dictionary);
    }

    @Override
    public ArrayList<Appointment> getAppointments() throws AppointmentException, ValidationException {
        String statement = "sp_GetAppointments";
        ArrayList<ParameterInfo> dictionary = new ArrayList<ParameterInfo>();

        return executeResultList(statement, dictionary);
    }

    @Override
    public ArrayList<Appointment> getMonthlyAppointments(ZonedDateTime startingDate, ZonedDateTime endingDate)
            throws AppointmentException, ValidationException {
        String statement = "sp_GetMonthlyAppointments";
        ArrayList<ParameterInfo> dictionary = new ArrayList<ParameterInfo>();


        java.sql.Timestamp utcStart = DatabaseDateTimeConverter.getSqlTimestamp(startingDate);
        java.sql.Timestamp utcEnd = DatabaseDateTimeConverter.getSqlTimestamp(endingDate);


        dictionary.add(new ParameterInfo("startingDate", utcStart));
        dictionary.add(new ParameterInfo("endingDate", utcEnd));

        return executeResultList(statement, dictionary);

    }
}
