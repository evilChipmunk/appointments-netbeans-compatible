package dataAccess;

import application.Configuration;
import application.services.IApplicationState;
import application.services.IUserService;
import models.IncrementType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.UUID;

public class IncrementTypeRepo extends BaseRepo<IncrementType> implements IIncrementTypeRepo{


    public IncrementTypeRepo(Configuration config,IApplicationState applicationState, ISqlRetryPolicy policy) {
        super(config, applicationState, policy);
    }

    @Override
    protected String getByIdProc() {
        return null;
    }

    @Override
    protected String getDeleteProc() {
        return null;
    }

    @Override
    protected String getSaveProc() {
        return null;
    }


    @Override
    protected ArrayList<ParameterInfo> getSaveParams(IncrementType entity) {
        return null;
    }

    @Override
    protected IncrementType mapEntity(ResultSet results, Includes... includes) throws SQLException {
        int id = results.getInt("id");
        String description = results.getString("Description");
        return new IncrementType(id, description);
    }
}
