package dataAccess;

import application.Configuration;
import application.services.IApplicationState;
import exceptions.AppointmentException;
import exceptions.ValidationException;
import java.lang.reflect.Type;
import models.Address;
import models.AuditInfo;
import models.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CustomerRepo extends BaseRepo<Customer> implements ICustomerRepo {

    private final IAddressRepo addressRepo;

    public CustomerRepo(Configuration config, IApplicationState applicationState, IAddressRepo addressRepo, ISqlRetryPolicy policy) {
        super(config, applicationState, policy);
        this.addressRepo = addressRepo;
    }

    @Override
    protected String getByIdProc() {
        return "sp_GetCustomerById";
    }

    @Override
    protected String getDeleteProc() {
        return "sp_DeleteCustomerById";
    }

    @Override
    protected String getSaveProc() {
        return "sp_SaveCustomer";
    }
    
    @Override
    protected Type getEntityType() {
      return Customer.class;
    }

    @Override
    protected ArrayList<ParameterInfo> getSaveParams(Customer entity) {
        ArrayList<ParameterInfo> params = new ArrayList<>();
        params.add(new ParameterInfo("id", entity.getId()));
        params.add(new ParameterInfo("name", entity.getName()));
        params.add(new ParameterInfo("addressId", entity.getAddress().getId()));
        params.add(new ParameterInfo("active", entity.isActive()));
        addAuditParams(params, entity);
        return params;
    }

    @Override
    protected Customer mapEntity(ResultSet results, Includes... includes) throws SQLException, ValidationException {
        int id = results.getInt("Id");
        String name = results.getString("Name");
        int addressId = results.getInt("AddressId");
        boolean active = results.getBoolean("Active");

        AuditInfo audit = createAudit(results);

        Customer customer;
        if (hasInclude(Includes.Address, includes)) {
            Address address = addressRepo.getById(addressId);
            customer = new Customer(id, name, address, active, audit);
        } else {
            customer = new Customer(id, name, addressId, active, audit);
        }

        return customer;
    }

    @Override
    public ArrayList<Customer> getCustomers(Includes... includes) throws AppointmentException, ValidationException {
        String statement = "sp_GetCustomers";
        ArrayList<ParameterInfo> dictionary = new ArrayList<>();
        return executeResultList(statement, dictionary);
    }
}
