package dataAccess;

import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.BaseEntity;

public interface IRepo<T extends BaseEntity> {

    T getById(int id, Includes... includes) throws AppointmentException, ValidationException;

    T save(T entity) throws AppointmentException;

    boolean delete(T entity) throws AppointmentException;
}
