package dataAccess;

import application.Configuration;
import application.services.IApplicationState;
import exceptions.AppointmentException;
import exceptions.ValidationException;
import java.lang.reflect.Type;
import models.AuditInfo;
import models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList; 

public class UserRepo extends BaseRepo<User> implements IUserRepo {

    public UserRepo(Configuration config, IApplicationState state, ISqlRetryPolicy policy) {
        super(config, state, policy);
    }

    @Override
    protected String getByIdProc() {
        return "sp_GetUserById";
    }

    @Override
    protected String getDeleteProc() {
        return "sp_DeleteUserById";
    }

    @Override
    protected String getSaveProc() {
        return "sp_SaveUser";
    }
    
    
    @Override
    protected Type getEntityType() {
      return User.class;
    }

    @Override
    protected ArrayList<ParameterInfo> getSaveParams(User entity) {
        ArrayList<ParameterInfo> dictionary = new ArrayList<>();
        dictionary.add(new ParameterInfo("id", entity.getId()));
        dictionary.add(new ParameterInfo("name", entity.getName()));
        dictionary.add(new ParameterInfo("password", entity.getPassword()));
        dictionary.add(new ParameterInfo("active", entity.isActive()));
        addAuditParams(dictionary, entity);
        return dictionary;
    }

    @Override
    protected User mapEntity(ResultSet results, Includes... includes) throws SQLException {

        if (results.next()) {
            int id = results.getInt("Id");
            String name = results.getString("Name");
            String password = results.getString("Password");
            boolean active = results.getBoolean("Active");
            AuditInfo audit = super.createAudit(results);
            return new User(id, name, password, active, audit);
        }
        return null;
    }

    @Override
    public User getUser(String userName, String password) throws ValidationException, AppointmentException {
        String statement = "sp_GetUserByNameAndPassword";
        ArrayList<ParameterInfo> dictionary = new ArrayList<>();
        dictionary.add(new ParameterInfo("name", userName));
        dictionary.add(new ParameterInfo("password", password));

        return executeResultSingle(statement, dictionary);
    }
}
