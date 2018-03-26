package dataAccess;

import application.Configuration;
import application.services.IApplicationState;
import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.AuditInfo;
import models.City;
import models.Country;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CityRepo extends BaseRepo<City> implements ICityRepo {

    private final ICountryRepo countryRepo;

    public CityRepo(Configuration config, IApplicationState applicationState, ICountryRepo countryRepo, ISqlRetryPolicy policy) {
        super(config, applicationState, policy);
        this.countryRepo = countryRepo;
    }

    @Override
    protected String getByIdProc() {
        return "sp_GetCityById";
    }

    @Override
    protected String getDeleteProc() {
        return null;
    }

    @Override
    protected String getSaveProc() {
        return "sp_SaveCity";
    }

    @Override
    protected ArrayList<ParameterInfo> getSaveParams(City entity) {
        ArrayList<ParameterInfo> dictionary = new ArrayList<>();
        dictionary.add(new ParameterInfo("id", entity.getId()));
        dictionary.add(new ParameterInfo("name", entity.getName()));
        dictionary.add(new ParameterInfo("countryId", entity.getCountry().getId()));
        addAuditParams(dictionary, entity);
        return dictionary;
    }

    @Override
    protected City mapEntity(ResultSet results, Includes... includes) throws SQLException, ValidationException {
        int id = results.getInt("Id");
        String name = results.getString("Name");
        int countryId = results.getInt("CountryId");

        Country country = countryRepo.getById(countryId);
        AuditInfo audit = super.createAudit(results);
        return new City(id, name, country, audit);
    }

    @Override
    public ArrayList<City> getCities() throws AppointmentException, ValidationException {
        String statement = "sp_GetCities";
        ArrayList<ParameterInfo> dictionary = new ArrayList<>();

        return executeResultList(statement, dictionary);
    }

}
