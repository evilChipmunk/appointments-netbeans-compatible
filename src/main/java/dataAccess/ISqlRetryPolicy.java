package dataAccess;

import exceptions.AppointmentException;

import java.sql.Connection;
import java.sql.SQLException;

public interface ISqlRetryPolicy {
    Connection Execute(ICheckedFunction<Void, Connection>  sqlAction)  throws SQLException, AppointmentException;
}
