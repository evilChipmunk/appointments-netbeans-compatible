package application.controls.screens;

import application.messaging.Commands;
import application.Configuration;
import application.controls.*;
import application.messaging.IListener;
import application.services.IAppointmentContext;
import application.services.IScheduler;
import application.viewModels.AppointmentViewModel;
import exceptions.AppointmentException;
import exceptions.ScheduleOverlapException;
import exceptions.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Appointment;
import models.Customer;

import java.awt.event.ActionListener;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.geometry.Bounds;


public class AppointmentControl extends MainPanelControl implements IMainPanelView {

    private final IAppointmentContext appointmentContext;
    private final IScheduler scheduler;
    private final ZonedDateTime seedDate;
    private ArrayList<Customer> customers;
    private AppointmentViewModel viewModel;
    private final Configuration config;
    private Appointment appointment;

    @FXML
    private AutoCompleteTextField autoCustomers;

    @FXML
    private TextField txtTitle;

    @FXML
    private TextField txtDescription;

    @FXML
    private TextField txtLocation;

    @FXML
    private TextField txtContact;

    @FXML
    private TextField txtURL;

    @FXML
    private DateTimePicker startPicker;

    @FXML
    private DateTimePicker endPicker;

    @FXML
    private ComboBox cboStartTime;

    @FXML
    private Button btnDelete;

    @FXML
    private ComboBox cboEndTime;


    public AppointmentControl(IAppointmentContext appointmentContext, IScheduler scheduler, Configuration config){
        this.appointmentContext = appointmentContext;
        this.scheduler = scheduler;
        this.config = config;
        this.seedDate = null;

        ScreenLoader.load("/views/AppointmentView.fxml", this, this);
    }


    public AppointmentControl(IAppointmentContext appointmentContext, IScheduler scheduler, Configuration config,
                              Appointment appointment){
        this.appointmentContext = appointmentContext;
        this.scheduler = scheduler;
        this.config = config;
        this.appointment = appointment;
        this.seedDate = null;

        ScreenLoader.load("/views/AppointmentView.fxml", this, this);
    }


    public AppointmentControl(IAppointmentContext appointmentContext, IScheduler scheduler, Configuration config, ZonedDateTime seedDate){
        this.appointmentContext = appointmentContext;
        this.scheduler = scheduler;
        this.config = config;
        this.appointment = appointment;
        this.seedDate = seedDate;
        ScreenLoader.load("/views/AppointmentView.fxml", this, this);
    }
    
    public void RefreshCustomers(){
        getCustomers();
        autoCustomers.getEntries().clear();
        autoCustomers.setIsLoading(true);
        autoCustomers.getEntries().addAll(this.customers.stream().map(x -> x.getName()).collect(Collectors.toList()));
    }

    @FXML
    public void initialize() throws AppointmentException, ValidationException {
        getCustomers();

        if (appointment != null)
        {
            this.viewModel = new AppointmentViewModel(appointment, this.customers, config);
        }
        else if (seedDate != null){
            this.viewModel = new AppointmentViewModel(seedDate, this.customers, config);
        }
        else
        {
            this.viewModel = new AppointmentViewModel(this.customers, config);
        }

        btnDelete.visibleProperty().bindBidirectional(viewModel.getCanDeleteProperty());
        startPicker.setFormat("yyyy-MM-dd");
        endPicker.setFormat("yyyy-MM-dd");

        autoCustomers.setIsLoading(true);
        autoCustomers.getEntries().addAll(this.customers.stream().map(x -> x.getName()).collect(Collectors.toList()));
        autoCustomers.textProperty().bindBidirectional(viewModel.getCustomerProperty());
        autoCustomers.setIsLoading(false);

        txtTitle.textProperty().bindBidirectional(viewModel.getTitleProperty());
        txtDescription.textProperty().bindBidirectional(viewModel.getDescriptionProperty());
        txtLocation.textProperty().bindBidirectional(viewModel.getLocationProperty());
        txtContact.textProperty().bindBidirectional(viewModel.getContactProperty());
        txtURL.textProperty().bindBidirectional(viewModel.getURLProperty());


        cboStartTime.setItems(viewModel.getValidDateTimes());
        cboEndTime.setItems(viewModel.getValidDateTimes());


        cboStartTime.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            viewModel.getStartTimeProperty().set(newValue.toString());
        });

        cboEndTime.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                    viewModel.getEndTimeProperty().set(newValue.toString());
                }
        );

        startPicker.dateTimeValueProperty().bindBidirectional(viewModel.getStartProperty());
        endPicker.dateTimeValueProperty().bindBidirectional(viewModel.getEndProperty());

        if (viewModel.getStartTimeProperty().get() == null ){
            cboStartTime.getSelectionModel().select(viewModel.getTimeFromDate(viewModel.getStartProperty().get()));
        }
        else{
            cboStartTime.getSelectionModel().select(viewModel.getStartTimeProperty().get());
        }

        if (viewModel.getEndTimeProperty().get() == null){
            cboEndTime.getSelectionModel().select(viewModel.getTimeFromDate(viewModel.getEndProperty().get()));
        }
        else{
            cboEndTime.getSelectionModel().select(viewModel.getEndTimeProperty().get());
        }


    }


    private void getCustomers() throws AppointmentException, ValidationException {
        this.customers = appointmentContext.getCustomers();
    }

    @FXML
    public void save(ActionEvent event) throws AppointmentException {

        try{
            Appointment appointment = viewModel.getAppointment();
            scheduler.Schedule((x) -> { return appointmentContext.save(appointment); } );
            Stage stage = (Stage) this.getScene().getWindow();

            if (listener == null){
                stage.hide();
            }
            else{
                listener.actionPerformed(Commands.appointmentCreated);
            }
        }
        catch (ScheduleOverlapException ovEx){
            this.showValidationMessage(ovEx.getMessage());
        }
        catch (ValidationException vex){
            this.showValidationMessage(vex.getMessage());
        }
    }

    @FXML
    public void delete(ActionEvent event) throws AppointmentException {
        Appointment appointment = viewModel.getAppointment();
        scheduler.RemoveFromSchedule((x) -> { return appointmentContext.remove(appointment); } );

        Stage stage = (Stage) this.getScene().getWindow();

        if (listener == null){
            stage.hide();
        }
        else{

            listener.actionPerformed(Commands.appointmentDeleted);
        }
    }
    
    
    
    @Override
    public void setContentSize(ReadOnlyObjectProperty<Bounds> readOnlyBounds) {
        
    }
}



