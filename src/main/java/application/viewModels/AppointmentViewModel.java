package application.viewModels;

import application.Configuration;
import exceptions.AppointmentException;
import exceptions.ValidationException;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Appointment;
import models.Customer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class AppointmentViewModel {

    private static ObservableList<String> validTimes;

    private Appointment modelAppointment;
    private final ArrayList<Customer> customers;
    private final Configuration config;
    private final SimpleBooleanProperty canDeleteProperty = new SimpleBooleanProperty(false);
    private final SimpleStringProperty titleProperty = new SimpleStringProperty("");
    private final SimpleStringProperty descriptionProperty = new SimpleStringProperty("");
    private final SimpleStringProperty locationProperty = new SimpleStringProperty("");
    private final SimpleStringProperty contactProperty = new SimpleStringProperty("");
    private final SimpleStringProperty urlProperty = new SimpleStringProperty("");
    private final ObjectProperty<ZonedDateTime> startProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<ZonedDateTime> endProperty = new SimpleObjectProperty<>();
    private final SimpleStringProperty customerProperty = new SimpleStringProperty("");
    private final SimpleStringProperty startTimeProperty = new SimpleStringProperty("");
    private final SimpleStringProperty endTimeProperty = new SimpleStringProperty("");
    private final SimpleBooleanProperty hasInvalidDataProperty = new SimpleBooleanProperty();

    public AppointmentViewModel(Appointment appointment, ArrayList<Customer> customers, Configuration config) {
        this.modelAppointment = appointment;
        this.customers = customers;
        this.config = config;

        canDeleteProperty.setValue(true);

        customerProperty.set(appointment.getCustomer().getName());
        titleProperty.set(appointment.getTitle());
        descriptionProperty.set(appointment.getDescription());
        locationProperty.set(appointment.getLocation());
        contactProperty.set(appointment.getContact());
        urlProperty.set(appointment.getUrl());
        startProperty.setValue(appointment.getStart());
        endProperty.setValue(appointment.getEnd());

        startTimeProperty.set(getTimeFromDate(appointment.getStart()));
        endTimeProperty.set(getTimeFromDate(appointment.getEnd()));

        //just set the start/end date properties, so the hookup here needs to happen after start/end time properties are set
        startTimeProperty.addListener((observable, oldValue, newValue) -> startProperty.set(getDateTime(startProperty.get(), newValue)));
        endTimeProperty.addListener((observable, oldValue, newValue) -> endProperty.set(getDateTime(endProperty.get(), newValue)));

        hasInvalidDataProperty.set(appointment.getHasInvalidData());
    }

    public AppointmentViewModel(ArrayList<Customer> customers, Configuration config) {
        this.customers = customers;
        this.config = config;

        startProperty.setValue(getZoneDateTimeFromNow(0));
        endProperty.setValue(getZoneDateTimeFromNow(30));
        startTimeProperty.set(getTimeFromDate(startProperty.get()));
        endTimeProperty.set(getTimeFromDate(endProperty.get()));

        //just set the start/end date properties, so the hookup here needs to happen after start/end time properties are set
        startTimeProperty.addListener((observable, oldValue, newValue) -> startProperty.set(getDateTime(startProperty.get(), newValue)));
        endTimeProperty.addListener((observable, oldValue, newValue) -> endProperty.set(getDateTime(endProperty.get(), newValue)));

        hasInvalidDataProperty.set(false);
    }

    public AppointmentViewModel(ZonedDateTime seedDate, ArrayList<Customer> customers, Configuration config) {

        this.customers = customers;
        this.config = config;

        startProperty.setValue(getZoneDateTime(seedDate, 0));
        endProperty.setValue(getZoneDateTime(seedDate, 30));
        startTimeProperty.set(getTimeFromDate(startProperty.get()));
        endTimeProperty.set(getTimeFromDate(endProperty.get()));

        //just set the start/end date properties, so the hookup here needs to happen after start/end time properties are set
        startTimeProperty.addListener((observable, oldValue, newValue) -> startProperty.set(getDateTime(startProperty.get(), newValue)));
        endTimeProperty.addListener((observable, oldValue, newValue) -> endProperty.set(getDateTime(endProperty.get(), newValue)));

        hasInvalidDataProperty.set(false);
    }

    public Appointment getAppointment() throws ValidationException {

        String title = titleProperty.get();
        String description = descriptionProperty.get();
        String location = locationProperty.get();
        String contact = contactProperty.get();
        String url = urlProperty.get();
        ZonedDateTime start = startProperty.get();
        ZonedDateTime end = endProperty.get();
        String customerName = customerProperty.get();

        if (customerName == null || customerName.isEmpty()) {
            throw new ValidationException("Customer is required");
        }

        Customer customer = customers.stream().filter(x -> x.getName().toLowerCase().equals(customerName.toLowerCase())).findAny().orElse(null);

        if (modelAppointment != null) {
            return new Appointment(modelAppointment.getId(), modelAppointment.getCustomer(), title, description, location,
                    contact, url, start, end, modelAppointment.getAudit(), config, false);
        }

        return new Appointment(customer, title, description, location, contact, url, start, end, config);
    }

    private String getTimeNumber(String time) {
        SimpleDateFormat date12Format = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm");
        String timeNumber = null;
        try {
            timeNumber = date24Format.format(date12Format.parse(time));
        } catch (ParseException e) {
            throw new AppointmentException("Error while trying to parse a selected time value", e);
        }
        return timeNumber;
    }

    private ZonedDateTime getDateTime(ZonedDateTime date, String selectedTime) {
        String timeNumber = selectedTime;
        if (selectedTime.contains("A") || selectedTime.contains("P")) {
            timeNumber = getTimeNumber(selectedTime);
        }

        String[] times = timeNumber.split(":");
        int hour = Integer.parseInt(times[0]);
        int minute = Integer.parseInt(times[1]);
        return ZonedDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                hour, minute, 0, 0, ZoneId.systemDefault());
    }

    private ZonedDateTime roundDateToMinutesBlock(ZonedDateTime date) {
        int minute = date.getMinute();
        if (minute >= 30) {
            while (date.getMinute() > 30) {
                date = date.minusMinutes(1);
            }
        } else {
            while (date.getMinute() > 0) {
                date = date.minusMinutes(1);
            }
        }
        return ZonedDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                date.getHour(), date.getMinute(), 0, 0, ZoneId.systemDefault());

    }

    public ObservableList<String> getValidDateTimes() {

        //set up as a one time operation because this list wont change
        if (validTimes == null) {

            validTimes = FXCollections.observableArrayList();

            DateTimeFormatter format = DateTimeFormatter.ofPattern("hh:mm a");

            ZonedDateTime seed = ZonedDateTime.of(1900, 1, 1,
                    8, 0, 0, 0, ZoneId.systemDefault());
            while (seed.getHour() < 20) {
                validTimes.add(seed.format(format));
                seed = seed.plusMinutes(30);
            }
        }
        return validTimes;

    }

    public String getTimeFromDate(ZonedDateTime date) {

        DateTimeFormatter format = DateTimeFormatter.ofPattern("hh:mm a");
        return date.format(format);
    }

    public ZonedDateTime getZoneDateTime(ZonedDateTime date, int plusMinutes) {

        date = roundDateToMinutesBlock(date);
        date = date.plusMinutes(plusMinutes);

        return sanitizeDate(date);
    }

    public ZonedDateTime getZoneDateTimeFromNow(ZonedDateTime date, int plusMinutes) {

        date = roundDateToMinutesBlock(date);
        while (date.isBefore(ZonedDateTime.now())) {
            date = date.plusMinutes(30);
        }
        date = date.plusMinutes(plusMinutes);

        while (date.getHour() < config.getBusinessStartHour()) {
            date = date.plusHours(1);
        }

        while (date.getHour() > config.getBusinessEndHour()) {
            date = date.minusHours(1);
        }

        return sanitizeDate(date);
    }

    public ZonedDateTime getZoneDateTimeFromNow(int plusMinutes) {

        ZonedDateTime date = getZoneDateTimeFromNow(ZonedDateTime.now(), plusMinutes);

        return sanitizeDate(date);
    }

    private ZonedDateTime sanitizeDate(ZonedDateTime date) {
        while (date.getHour() <= config.getBusinessStartHour()) {
            date = date.plusMinutes(30);
        }

        while (date.getHour() >= config.getBusinessEndHour()) {
            date = date.minusMinutes(30);
        }
        return date;
    }

    public SimpleStringProperty getTitleProperty() {
        return titleProperty;
    }

    public SimpleStringProperty getDescriptionProperty() {
        return descriptionProperty;
    }

    public SimpleStringProperty getLocationProperty() {
        return locationProperty;
    }

    public SimpleStringProperty getContactProperty() {
        return contactProperty;
    }

    public SimpleStringProperty getURLProperty() {
        return urlProperty;
    }

    public ObjectProperty<ZonedDateTime> getEndProperty() {
        return endProperty;
    }

    public ObjectProperty<ZonedDateTime> getStartProperty() {
        return startProperty;
    }

    public SimpleStringProperty getCustomerProperty() {
        return customerProperty;
    }

    public SimpleStringProperty getStartTimeProperty() {
        return startTimeProperty;
    }

    public SimpleStringProperty getEndTimeProperty() {
        return endTimeProperty;
    }

    public SimpleBooleanProperty getCanDeleteProperty() {
        return canDeleteProperty;
    }

}
