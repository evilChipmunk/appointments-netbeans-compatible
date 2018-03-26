package dataAccess;

import application.Configuration;
import application.logging.ILogger;
import exceptions.AppointmentException;

import java.sql.Connection;
import java.sql.SQLException;

public class SqlRetryPolicy implements ISqlRetryPolicy {

    private final Configuration config;
    private final ILogger logger;

    public SqlRetryPolicy(Configuration config, ILogger logger) {
        this.config = config;
        this.logger = logger;
    }

    @Override
    public Connection Execute(ICheckedFunction<Void, Connection> sqlAction) throws SQLException, AppointmentException {
        int retries = config.getRetry();
        while (retries != 0) {
            try {
                return sqlAction.apply(null);
            } catch (SQLException ex) {
                logger.Log(ex);
                retries--;

                //if there is a connection error, give it a little bit of time
                //before trying again. The wait time exponentially grows so that
                //the server has a chance to recover. Although as configured now,
                //with retries set to 3, the maximum wait time before failure
                //will be 14 seconds + connection time. A long time to wait sure,
                //but the alternative is just full stop failure on this process.
                // This might be used with a bulkhead and/or circuit breaker pattern
                //in a more resilient system
                try {
                    Thread.sleep(getWaitTime(retries));
                } catch (InterruptedException e) {
                    throw new AppointmentException("Thread error in retry policy", e);
                }
                if (retries == 0) {
                    throw ex;
                }
            }
        }
        return null;
    }

    private int getWaitTime(int retries) {
        return (int) Math.pow(retries, 2) * 1000;
    }
}
