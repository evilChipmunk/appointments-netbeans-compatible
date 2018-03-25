package dataAccess;

import application.Configuration;
import application.services.IApplicationState;
import exceptions.ValidationException;
import models.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class AddressRepo extends BaseRepo<Address> implements IAddressRepo{

    private final ICityRepo cityRepo;

    public AddressRepo(Configuration config, IApplicationState applicationState, ICityRepo cityRepo, ISqlRetryPolicy policy) {
        super(config, applicationState, policy);
        this.cityRepo = cityRepo;
    }

    @Override
    protected Address mapEntity(ResultSet results, Includes... includes) throws SQLException, ValidationException {
        int id = results.getInt("Id");
        String streetOne = results.getString("Address");
        String streetTwo = results.getString("Address2");
        int cityId = results.getInt("CityId");
        String postalCode = results.getString("PostalCode");
        String phone = results.getString("Phone");

        AuditInfo audit = createAudit(results);
        City city = cityRepo.getById(cityId);
        return new Address(id, streetOne, streetTwo, city, postalCode, phone, audit);
    }


    @Override
    protected String getByIdProc() {
        return "sp_GetAddressById";
    }

    @Override
    protected String getDeleteProc() {
        return null;
    }

    @Override
    protected String getSaveProc() {
        return "sp_SaveAddress";
    }

    @Override
    protected ArrayList<ParameterInfo> getSaveParams(Address entity) {
        ArrayList<ParameterInfo> dictionary = new ArrayList<ParameterInfo>();
        dictionary.add(new ParameterInfo("id", entity.getId()));
        dictionary.add(new ParameterInfo("address", entity.getStreetOne()));
        dictionary.add(new ParameterInfo("address2", entity.getStreetTwo()));
        dictionary.add(new ParameterInfo("cityId", entity.getCity().getId()));
        dictionary.add(new ParameterInfo("postalCode", entity.getPostalCode()));
        dictionary.add(new ParameterInfo("phone", entity.getPhone()));

        addAuditParams(dictionary, entity);
        return dictionary;
    }

}
