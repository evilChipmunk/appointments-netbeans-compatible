package application.services;

import dataAccess.*;
import exceptions.AppointmentException;
import exceptions.ValidationException;
import models.Address;
import models.City;
import models.Country;
import models.Customer;

import java.util.ArrayList;

public class CustomerContext implements ICustomerContext {

    private final ICustomerRepo repo;
    private final IAddressRepo addressRepo;
    private final ICityRepo cityRepo;
    private final ICountryRepo countryRepo;

    public CustomerContext(ICustomerRepo repo, IAddressRepo addressRepo, ICityRepo cityRepo, ICountryRepo countryRepo) {

        this.repo = repo;
        this.addressRepo = addressRepo;
        this.cityRepo = cityRepo;
        this.countryRepo = countryRepo;
    }

    @Override
    public ArrayList<Customer> getCustomers() throws AppointmentException, ValidationException {

        return repo.getCustomers();
    }

    @Override
    public void saveCustomer(Customer customer) throws AppointmentException {

        Address address = customer.getAddress();
        City city = address.getCity();
        Country country = city.getCountry();

        countryRepo.save(country);
        cityRepo.save(city);
        addressRepo.save(customer.getAddress());
        repo.save(customer);
    }

    @Override
    public Address getAddress(int id, Includes... includes) throws AppointmentException, ValidationException {
        return addressRepo.getById(id, includes);
    }

    @Override
    public ArrayList<City> getCities() throws AppointmentException, ValidationException {
        return cityRepo.getCities();
    }

    @Override
    public ArrayList<Country> getCountries() throws AppointmentException, ValidationException {
        return countryRepo.getCountries();
    }

    @Override
    public boolean delete(Customer customer) throws AppointmentException {
        return repo.delete(customer);
    }
}
