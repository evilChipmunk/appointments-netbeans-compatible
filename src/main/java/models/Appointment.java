package models;

import application.Configuration;
import exceptions.ValidationException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Appointment extends BaseEntity implements Comparable<Appointment> {

    private Customer customer;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String url;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private boolean hasInvalidData;

    public Appointment(int id, Customer customer, String title, String description,
            String location, String contact, String url, ZonedDateTime start, ZonedDateTime end,
             AuditInfo audit, Configuration config, boolean isPreExisting) throws ValidationException {
        super(id, audit);

        validate(customer, start, end, config, isPreExisting);

        setFields(customer, title, description, location, contact, url, start, end);
    }

    public Appointment(Customer customer, String title, String description, String location,
             String contact, String url, ZonedDateTime start, ZonedDateTime end, Configuration config) throws ValidationException {

        validate(customer, start, end, config, false);

        setFields(customer, title, description, location, contact, url, start, end);
    }

    private void validate(Customer customer, ZonedDateTime start, ZonedDateTime end, Configuration config, boolean isPreExisting) {
        if (customer == null) {
            throw new ValidationException("Customer is required");
        }

        try {
            if (start.getHour() < config.getBusinessStartHour()) {
                throw new ValidationException("Start time must be within business hours (8 - 6:30)");
            }

            if (start.getHour() >= config.getBusinessEndHour()) {
                throw new ValidationException("Start time must be within business hours (8 - 6:30)");
            }

            if (end.getHour() < config.getBusinessStartHour()) {
                throw new ValidationException("End time must be within business hours (8 - 6:30)");
            }
            if (end.getHour() >= config.getBusinessEndHour()) {
                throw new ValidationException("End time must be within business hours (8 - 6:30)");
            }

            if (!end.isAfter(start)) {
                throw new ValidationException("End time cannot be the same or less than the Start time");
            }
        } catch (ValidationException vex) {
            if (isPreExisting) {
                this.hasInvalidData = true;
                return;
            }
            throw vex;
        }
    }

    private void setFields(Customer customer, String title, String description, String location, String contact, String url, ZonedDateTime start, ZonedDateTime end) {

        this.customer = customer;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.url = url;
        this.start = setDateFormat(start);
        this.end = setDateFormat(end);
    }

    public Customer getCustomer() {
        return customer;
    }
 
    public String getTitle() {
        return title;
    }
 
    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getContact() {
        return contact;
    }

    public String getUrl() {
        return url;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public ZonedDateTime getEnd() {
        return end;
    }

    private ZonedDateTime setDateFormat(ZonedDateTime date) {
        return date.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                date.getHour(), date.getMinute(), date.getSecond(), 0,
                ZoneId.systemDefault());
    }

    @Override
    public int compareTo(Appointment o) {

        if (this.getStart() == o.getStart()) {
            return 0;
        } else if (this.getStart().isBefore(o.getStart())) {
            return -1;
        } else {
            return 1;
        }
    }

    public boolean getHasInvalidData() {
        return hasInvalidData;
    }

    @Override
    public String toString() {
        String user = "";
        String cName = "";
        String startTime = "";
        String title = "";

        if (this.getAudit() != null) {
            user = this.getAudit().getCreatedBy();
        }
        if (this.getCustomer() != null) {
            cName = this.getCustomer().getName();
        }

        DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");

        startTime = this.getStart().format(format);

        title = this.getTitle();

        return user + " " + cName + " " + title + " " + startTime;
    }
}
