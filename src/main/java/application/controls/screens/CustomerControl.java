package application.controls.screens;

import application.alerts.AlertContent;
import application.alerts.AlertFactory;
import application.alerts.AlertType;
import application.alerts.CustomerDeleteAlertBuilder;
import application.controls.AutoCompleteTextField;
import application.controls.IMainPanelView;
import application.controls.MainPanelControl; 
import application.controls.ScreenLoader;
import application.services.ICustomerContext;
import application.viewModels.CustomerViewModel;
import dataAccess.Includes;
import exceptions.AppointmentException;
import exceptions.ValidationException;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import models.*;

import java.util.ArrayList;
import java.util.Optional; 
import javafx.beans.property.ReadOnlyObjectProperty; 
import javafx.geometry.Bounds; 

public class CustomerControl extends MainPanelControl implements IMainPanelView {

    private final ICustomerContext service;
    private final ObservableList<Customer> customers;
    private final AlertFactory alertFactory;
    private ArrayList<Country> countries;
    private ArrayList<City> cities;
    private CustomerViewModel viewModel;

    public CustomerControl(ICustomerContext service, AlertFactory alertFactory) {

        this.alertFactory = alertFactory;
        this.service = service;
        this.customers = FXCollections.observableArrayList();

        ScreenLoader.load("/views/CustomerView.fxml", this, this);
    }

    @FXML
    public void initialize() throws AppointmentException, ValidationException {

        initializeCustomerList();

        populateCustomers();

        populateCities();

        populateCountries();

        this.viewModel = new CustomerViewModel(cities, countries);

        setBindings();

    }

    @FXML
    private ListView lstCustomers;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtAddress2;

    @FXML
    private TextField txtPostalCode;

    @FXML
    private TextField txtPhone;

    @FXML
    private AutoCompleteTextField autoCities;

    @FXML
    private AutoCompleteTextField autoCountries;

    @FXML
    private javafx.scene.control.CheckBox chkActive;

    @FXML
    private Button btnDelete;

    @FXML
    public void save(ActionEvent event) throws AppointmentException {

        try {
            //this would be a good place (specifically in the initalize mthod)to bind button disable
            //if customer can't be saved due to missing information
            //then don't allow this button to be clicked
            Customer customer = viewModel.getCustomer();
 
             
            executeLongAction((Void x) -> 
                {
                    service.saveCustomer(customer);
                    updateCustomerList(customer);
                    sortCustomerList();
                }
            );
 
        } catch (ValidationException vex) {
            this.showValidationMessage(vex.getMessage());
        }
    }
 
    @FXML
    public void delete(ActionEvent event) throws AppointmentException {

        AlertContent content = new AlertContent();
        content.setTitle("Are you sure?");
        content.setMessage("This will delete all of the customer's appointments too!");
        content.setAlertType(AlertType.Warning);
        Alert alert = alertFactory.create(new CustomerDeleteAlertBuilder(), content);
        alert.getButtonTypes().add(1, ButtonType.CANCEL);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            Customer customer = viewModel.getCustomer();

            boolean deleted = service.delete(customer);
            if (deleted) {

                removeCustomerFromList(customer);
                sortCustomerList();

                //reset the fields so that they are blank
                this.viewModel = new CustomerViewModel(cities, countries);
                setBindings();
            }
        }
    }

    @FXML
    public void add(ActionEvent event) {
        this.viewModel = new CustomerViewModel(cities, countries);
        setBindings();
    }

    private void setBindings() {
        txtName.textProperty().bindBidirectional(viewModel.getCustomerNameProperty());
        txtAddress.textProperty().bindBidirectional(viewModel.getAddressOneProperty());
        txtAddress2.textProperty().bindBidirectional(viewModel.getAddressTwoProperty());
        txtPostalCode.textProperty().bindBidirectional(viewModel.getPostalCodeProperty());
        txtPhone.textProperty().bindBidirectional(viewModel.getPhoneProperty());
        autoCities.setIsLoading(true);
        autoCities.textProperty().bindBidirectional(viewModel.getCityProperty());
        autoCountries.setIsLoading(true);
        autoCountries.textProperty().bindBidirectional(viewModel.getCountryProperty());
        chkActive.selectedProperty().bindBidirectional(viewModel.getIsActiveProperty());

        btnDelete.visibleProperty().bindBidirectional(viewModel.getCanDeleteProperty());

    }

    private void populateCountries() throws AppointmentException, ValidationException {
        this.countries = service.getCountries();
        countries.forEach((country) -> {
            autoCountries.getEntries().add(country.getName());
        });
    }

    private void populateCities() throws AppointmentException, ValidationException {
        this.cities = service.getCities();
        cities.forEach((city) -> {
            autoCities.getEntries().add(city.getName());
        });
    }

    private void initializeCustomerList() {
        initializeListCells();
        lstCustomers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue != null) {
                selectCustomer(observable, oldValue, newValue);
            }
        });
    }

    private void populateCustomers() throws ValidationException {
        ArrayList<Customer> custList = service.getCustomers();
        if (custList != null) {
            this.customers.addAll(custList);
        }
        lstCustomers.setItems(this.customers);
        sortCustomerList();
    }

    private void updateCustomerList(Customer customer) {

        boolean found = false;
        for (Object custObj : lstCustomers.getItems().toArray()) {
            Customer cust = (Customer) custObj;
            if (cust.getId() == customer.getId()) {
                lstCustomers.getItems().remove(cust);
                lstCustomers.getItems().add(customer);
                found = true;
            }
        }

        if (!found) {
            lstCustomers.getItems().add(customer);
        }
    }

    private void removeCustomerFromList(Customer customer) {

        for (Object custObj : lstCustomers.getItems().toArray()) {
            Customer cust = (Customer) custObj;
            if (cust.getId() == customer.getId()) {
                lstCustomers.getItems().remove(cust);
                return;
            }
        }
    }

    private void sortCustomerList() {
        lstCustomers.getItems().sort((o1, o2) -> {
            Customer cust1 = (Customer) o1;
            Customer cust2 = (Customer) o2;
            return cust1.getName().compareTo(cust2.getName());
        });
    }

    private void handleCellDisplaying(ListCell<Customer> cell) {
        Customer customer = cell.getItem();
        if (customer != null) {
            cell.setText(customer.getName());
        } else {
            cell.setText("");
        }
    }

    private void initializeListCells() {
        lstCustomers.setCellFactory(param -> {
            ListCell<Customer> cell = new ListCell<>();
            cell.selectedProperty().addListener((observable, oldValue, newValue) -> handleCellDisplaying(cell));
            cell.itemProperty().addListener((observable, oldValue, newValue) -> handleCellDisplaying(cell));
            return cell;
        });
    }

    private void selectCustomer(ObservableValue observable, Object oldValue, Object newValue) throws ValidationException {

        Customer customer = (Customer) newValue;
        if (customer.getAddress() == null) {
            customer.setAddress(service.getAddress(customer.getId(), Includes.Country, Includes.City));
        }

        this.viewModel = new CustomerViewModel(customer, cities, countries);
        setBindings();
    }

    @Override
    public void setContentSize(ReadOnlyObjectProperty<Bounds> readOnlyBounds) {

    }
}
