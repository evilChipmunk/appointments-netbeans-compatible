package application.viewModels;

import exceptions.ValidationException;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import models.*;

import java.util.ArrayList;

public class CustomerViewModel {
    private Customer modelCustomer;
    private Address modelAddress;
    private ArrayList<City> modelCities;
    private ArrayList<Country> modelCountries;
    private City modelCity;
    private Country modelCountry;

    private SimpleStringProperty customerNameProperty;
    private SimpleStringProperty addressOneProperty;
    private SimpleStringProperty addressTwoProperty;
    private SimpleStringProperty postalCodeProperty;
    private SimpleStringProperty phoneProperty;
    private SimpleStringProperty cityProperty;
    private SimpleStringProperty countryProperty;
    private SimpleBooleanProperty isActiveProperty;
    private SimpleBooleanProperty canDeleteProperty;

    public CustomerViewModel(Customer customer, ArrayList<City> cities, ArrayList<Country> countries){

        setCountryAndCityLists(cities, countries);

        setModels(customer);

        initializeProperties();

        setPropertiesFromModels(customer);
    }

    public CustomerViewModel(ArrayList<City> cities, ArrayList<Country> countries) {

        initializeProperties();
        setCountryAndCityLists(cities, countries);
    }

    private void setPropertiesFromModels(Customer customer) {
        this.customerNameProperty.set(customer.getName());
        this.isActiveProperty.set(customer.isActive());

        this.addressOneProperty.set(modelAddress.getStreetOne());
        this.addressTwoProperty.set(modelAddress.getStreetTwo());
        this.postalCodeProperty.set(modelAddress.getPostalCode());
        this.phoneProperty.set(modelAddress.getPhone());

        this.cityProperty.set(modelCity.getName());
        this.countryProperty.set(modelCountry.getName());
        this.canDeleteProperty.set(true);
    }

    private void setModels(Customer customer) {
        this.modelCustomer = customer;
        this.modelAddress = customer.getAddress();
        this.modelCity = modelAddress.getCity();
        this.modelCountry = modelCity.getCountry();
    }

    private void setCountryAndCityLists(ArrayList<City> cities, ArrayList<Country> countries) {
        this.modelCities = cities;
        this.modelCountries = countries;
    }

    private void initializeProperties() {
        customerNameProperty = new SimpleStringProperty();
        addressOneProperty = new SimpleStringProperty();
        addressTwoProperty = new SimpleStringProperty();
        postalCodeProperty = new SimpleStringProperty();
        phoneProperty = new SimpleStringProperty();
        cityProperty = new SimpleStringProperty();
        countryProperty = new SimpleStringProperty();
        isActiveProperty = new SimpleBooleanProperty();
        canDeleteProperty = new SimpleBooleanProperty(false);
    }


    public Customer getCustomer(){

        String customerName = customerNameProperty.get();
        if ( customerName == null || customerName.isEmpty()){
            throw new ValidationException("Customer name is required");
        }

        Boolean isActive = isActiveProperty.get();
        Address address = getAddress();

        if (modelCustomer != null){
            return new Customer(modelCustomer.getId(), customerName, address, isActive, modelCustomer.getAudit());
        }
        return new Customer(customerName, address, isActive);
    }

    private Address getAddress(){

        String streetOne = addressOneProperty.get();
        String streetTwo = addressTwoProperty.get();
        String postalCode = postalCodeProperty.get();
        String phone = phoneProperty.get();

        String countryName = countryProperty.get();
        if (countryName == null || countryName.isEmpty()){
            throw new ValidationException("Country is required");
        }
        String cityName = cityProperty.get();
        if (cityName == null || cityName.isEmpty()){
            throw new ValidationException("City is required");
        }

        Country country = getCountry(countryName);
        City city = getLocatedCity(cityName, country);

        if (modelAddress != null){
            return new Address(modelAddress.getId(), streetOne, streetTwo, city, postalCode,phone, modelAddress.getAudit());
        }

        return new Address(streetOne, streetTwo, city, postalCode, phone);

    }

    private Country getCountry(String countryName){
        for(Country country: modelCountries){
            if (country.getName().toLowerCase().equals(countryName.toLowerCase())){
                return country;
            }
        }
        return new Country(countryName);
    }

    private City getLocatedCity(String cityName, Country country){
        ArrayList<City> foundCities = new ArrayList<>();
        for (City city: modelCities
                ) {
            if (city.getName().toLowerCase().equals(cityName.toLowerCase())){
                foundCities.add(city);
            }
        }

        for(City city: foundCities){
            if (city.getCountry().getId() == country.getId()){
                return city;
            }
        }
        return new City(cityName, country);
    }


    public SimpleStringProperty getCustomerNameProperty(){
        return customerNameProperty;
    }

    public SimpleStringProperty getAddressOneProperty() {
        return addressOneProperty;
    }

    public SimpleStringProperty getAddressTwoProperty() {
         return addressTwoProperty;
    }

    public SimpleStringProperty getPostalCodeProperty(){
        return postalCodeProperty;
    }

    public SimpleStringProperty getPhoneProperty() {
        return phoneProperty;
    }

    public SimpleStringProperty getCityProperty(){
        return cityProperty;
    }

    public SimpleStringProperty getCountryProperty(){
        return countryProperty;
    }

    public SimpleBooleanProperty getIsActiveProperty() {
        return isActiveProperty;
    }

    public SimpleBooleanProperty getCanDeleteProperty() {return canDeleteProperty;}
}
