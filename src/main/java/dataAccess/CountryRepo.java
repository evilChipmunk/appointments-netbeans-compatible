package dataAccess;

import application.Configuration;
import application.services.IApplicationState;
import application.services.IUserService;
import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.AuditInfo;
import models.Country;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.UUID;

public class CountryRepo extends BaseRepo<Country> implements ICountryRepo{


    public CountryRepo(Configuration config,IApplicationState applicationState, ISqlRetryPolicy policy) {
        super(config, applicationState, policy);
    }

    @Override
    protected String getByIdProc() {
        return "sp_GetCountryById";
    }

    @Override
    protected String getDeleteProc() {
        return null;
    }

    @Override
    protected String getSaveProc() {
        return "sp_SaveCountry";
    }


    @Override
    protected ArrayList<ParameterInfo> getSaveParams(Country entity) {
        ArrayList<ParameterInfo>  dictionary = new ArrayList<ParameterInfo> ();
        dictionary.add(new ParameterInfo("id", entity.getId()));
        dictionary.add(new ParameterInfo("name", entity.getName()));
        addAuditParams(dictionary, entity);
        return dictionary;
    }

    @Override
    protected Country mapEntity(ResultSet results, Includes... includes) throws SQLException {
        int id = results.getInt("Id");
        String name = results.getString("Name");

        AuditInfo audit = super.createAudit(results);
        Country country = new Country(id, name, audit);
        return country;
    }

    @Override
    public ArrayList<Country> getCountries() throws AppointmentException, ValidationException {
        String statement = "sp_GetCountries";
        ArrayList<ParameterInfo> dictionary = new ArrayList<ParameterInfo>();
        return executeResultList(statement, dictionary);
    }
}
