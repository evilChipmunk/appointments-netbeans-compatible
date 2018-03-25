package dataAccess;

import java.sql.SQLException;

@FunctionalInterface
public interface ICheckedFunction<T, R> {
    R apply(T t) throws SQLException;
}
