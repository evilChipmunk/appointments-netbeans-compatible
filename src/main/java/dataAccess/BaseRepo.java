package dataAccess;

import application.Configuration;
import application.services.IApplicationState;
import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.AuditInfo;
import models.BaseEntity;

import java.sql.*;
import java.time.*;
import java.util.*;

public abstract class BaseRepo<T extends BaseEntity> implements IRepo<T> {

    protected final Configuration config;
    private final IApplicationState applicationState;
    private final ISqlRetryPolicy retryPolicy;


    BaseRepo(Configuration config, IApplicationState applicationState, ISqlRetryPolicy retryPolicy){

        this.config = config;
        this.applicationState = applicationState;
        this.retryPolicy = retryPolicy;
    }


    public T getById(int id, Includes...includes) throws AppointmentException{
        try (Connection conn = getConnection()) {
            String statement = "CALL " + getByIdProc() + "(?);";
            try(PreparedStatement ps = conn.prepareStatement(statement)){

                ps.setInt(1, id);
                T entity = serialize(ps.executeQuery(), includes);
                return entity;
            }
        } catch (SQLException ex) {
            throw new AppointmentException("Error getting data from database", ex);
        }
    }

    public T save(T entity) throws AppointmentException {

        modifyAuditInfo(entity);

        ArrayList<ParameterInfo> params = getSaveParams(entity);
        if (! params.stream().anyMatch(x -> x.getName().equals("id"))){
            params.add(new ParameterInfo("id", entity.getId()));
        }

        try(Connection con = getConnection()){
            try(CallableStatement ps = createExecuteStatement(getSaveProc(), params, con))
            {
                ps.registerOutParameter("id", Types.INTEGER);

                try(ResultSet results = ps.executeQuery()){
                    results.first();
                    //ps.execute();
                    //  int id = ps.getInt("id");
                    int id = results.getInt("id");
                    entity.setId(id);
                    return entity;
                }
            }
        }
        catch(SQLException ex){
            throw new AppointmentException("Error saving data", ex);
        }
        catch (Exception ex){
            throw new AppointmentException("Error saving data", ex);
        }
    }

    public boolean delete(T entity) throws AppointmentException {

        try(Connection conn = getConnection()){

            String statement =  "CALL " + getDeleteProc() + "(?)";
            try(PreparedStatement ps = conn.prepareStatement(statement)){

                ps.setInt(1, entity.getId());
                ps.execute();

                return true;
            }
        }
        catch(SQLException ex) {
            throw new AppointmentException("Error deleting data", ex);
        }
    }

    abstract String getByIdProc();
    abstract String getDeleteProc();
    abstract String getSaveProc();
    abstract T mapEntity(ResultSet results, Includes... includes) throws SQLException, ValidationException;
    abstract ArrayList<ParameterInfo> getSaveParams(T entity);

    boolean hasInclude(Includes searchItem, Includes... includes){
        if (includes == null){
            return false;
        }

        if (includes.length == 0 ){
            return false;
        }
        for (int i =0; i < includes.length; i++){
            if (includes[i] == searchItem){
                return true;
            }
        }

        return false;
    }

    AuditInfo createAudit(ResultSet results) throws SQLException {
        String createBy = results.getString("CreatedBy");
        ZonedDateTime createDate = DatabaseDateTimeConverter.getZoneDateTime(results.getTimestamp("CreateDate"));
        String lastUpdatedBy = results.getString("LastUpdatedBy");
        ZonedDateTime lastUpdate = DatabaseDateTimeConverter.getZoneDateTime(results.getTimestamp("LastUpdate"));
        return new AuditInfo(createBy, createDate, lastUpdatedBy, lastUpdate);
    }

    void addAuditParams(ArrayList<ParameterInfo> params, T entity) {

        params.add(new ParameterInfo("createdBy",  entity.getAudit().getCreatedBy()));

        params.add(new ParameterInfo("createDate", DatabaseDateTimeConverter.getSqlDate(entity.getAudit().getCreateDate())));

        params.add(new ParameterInfo("lastUpdatedBy", entity.getAudit().getLastUpdateBy()));

        params.add(new ParameterInfo("lastUpdate", DatabaseDateTimeConverter.getSqlTimestamp(entity.getAudit().getLastUpdate())));
    }

    T executeResultSingle(String procName, ArrayList<ParameterInfo> params) throws AppointmentException {

        try (Connection con = getConnection()) {
            try (CallableStatement ps = createExecuteStatement(procName, params, con);) {
                try(ResultSet results = ps.executeQuery())
                {
                    return serialize(results);
                }
            }
            catch (SQLException ex){
                throw new AppointmentException("Error executing single", ex);
            }
        } catch (SQLException ex) {
            throw new AppointmentException("Error executing single", ex);
        }
    }

    ArrayList<T> executeResultList(String procName, ArrayList<ParameterInfo> params) throws AppointmentException {

        try (Connection con = getConnection()) {
            try (CallableStatement ps = createExecuteStatement(procName, params, con);) {
                try(ResultSet results = ps.executeQuery())
                {
                    return serializeList(results);
                }
            }
            catch (SQLException ex){
                throw new AppointmentException("Error executing list", ex);
            }
        } catch (SQLException ex) {
            throw new AppointmentException("Error executing list", ex);
        }
    }


    private void modifyAuditInfo(T entity){
        String userName = applicationState.getLoggedInUser().getName();
        entity.getAudit().updateAudit(userName, ZonedDateTime.now());
    }

    private ArrayList<T> serializeList(ResultSet results, Includes... includes) throws SQLException, ValidationException {

        ArrayList<T> list = new ArrayList<T>();
        while(results.next()) {
            T entity = mapEntity(results, includes);
            list.add(entity);
        }
        return list;
    }

    private T serialize(ResultSet results, Includes... includes) throws SQLException, ValidationException {
        if (results.next()){

            T entity = mapEntity(results, includes);
            return entity;
        }
        return  null;
    }

    private CallableStatement createExecuteStatement(String procName, ArrayList<ParameterInfo> params, Connection con)
            throws SQLException {

        String paramMarks = "";

        for (int p = 0; p < params.size(); p++){
            paramMarks += "?,";
        }
        if (!paramMarks.isEmpty()){
            paramMarks = paramMarks.substring(0, paramMarks.length() -1);
        }


        String statement = "CALL " + procName + "(" + paramMarks + ");";

        CallableStatement ps = con.prepareCall(statement);

        for (ParameterInfo param:params) {

            if (param.getName() == "lastUpdate"
                    ||
                    param.getName() == "start"
                    ||
                    param.getName() == "end"
                    ){
                DatabaseDateTimeConverter.setSqlTimestamp((Timestamp) param.getValue(), ps, param.getName());
            }
            else{
                ps.setObject(param.getName(), param.getValue());
            }
        }

        return ps;
    }

    private Connection getConnection() throws SQLException {

        return retryPolicy.Execute((x) -> {

            Properties props = new Properties();
            props.setProperty("user", config.getConnectionUserName());
            props.setProperty("password", config.getConnectionPassword());
            props.setProperty("useTimezone", "true");
            props.setProperty("useLegacyDatetimeCode", "false");
            props.setProperty("serverTimezone", "UTC");
            return DriverManager.getConnection(config.getConnectionString(), props);
        });
    }
}



