package application.controls.calendar;

import application.Configuration;
import application.controls.ScreenLoader;
import application.controls.screens.AppointmentControl;
import application.factories.AppointmentControlFactory;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import models.Appointment;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

public class AppointmentLineControl extends VBox {

    private final Label lblTitle = new Label();
    private final AppointmentControlFactory appointmentControlFactory;
    private final Configuration config;
    private final Appointment appointment;

    public AppointmentLineControl(Appointment appointment, AppointmentControlFactory appointmentControlFactory, Configuration config) {
        this.appointmentControlFactory = appointmentControlFactory;
        this.config = config;
        this.appointment = appointment;

        setupCSS(appointment);

        setupLabel(appointment);

        setToolTip(appointment);

        setupClick();

    }

    private void setupCSS(Appointment appointment) {

        this.getStylesheets().add("/styles/appointmentLine.css");
        this.getStyleClass().add("body");

        if (appointment.getHasInvalidData()) {
            this.getStyleClass().add("invalidData");
        }
    }

    private void setupLabel(Appointment appointment) {

        lblTitle.setWrapText(false);

        DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("h:mma")
                .toFormatter(Locale.US);

        String time = appointment.getStart().format(fmt);
        this.lblTitle.setText(time + "  " + appointment.getTitle());

        this.getChildren().add(lblTitle);
    }

    private void bindTooltip(final Node node, final Tooltip tooltip) {
        node.setOnMouseMoved((MouseEvent event) -> {
            // +15 moves the tooltip 15 pixels below the mouse cursor;
            // if you don't change the y coordinate of the tooltip, you
            // will see constant screen flicker
            tooltip.show(node, event.getScreenX(), event.getScreenY() + 15);
        });
        node.setOnMouseExited((MouseEvent event) -> {
            tooltip.hide();
        });
    }

    private void setToolTip(Appointment appointment) {

        DateTimeFormatter format = DateTimeFormatter.ofPattern("hh:mm a");
        String text = "\n" + appointment.getTitle() + "\n"
                + "Customer: " + appointment.getCustomer().getName() + "\n"
                + "Start: " + appointment.getStart().format(format) + "\n"
                + "End: " + appointment.getEnd().format(format) + "\n"
                + "Location: " + appointment.getLocation() + "\n"
                + "Contact: " + appointment.getContact() + "\n"
                + "URL: " + appointment.getUrl() + "\n" + "\n"
                + appointment.getDescription();

        final Tooltip tooltip = new Tooltip();
        tooltip.getStyleClass().add("tooltip");

        tooltip.setText(text);
        tooltip.setWrapText(true);
        tooltip.setTextOverrun(OverrunStyle.ELLIPSIS);

        bindTooltip(this, tooltip);
        this.lblTitle.setTooltip(tooltip);
    }

    private void setupClick() {

        this.setOnMouseClicked(event -> {

            AppointmentControl control = appointmentControlFactory.build(appointment);

            ScreenLoader.loadNewStage(control, config.getSmallAppointmentWidth(),
                     config.getSmallAppointmentHeight(), Modality.APPLICATION_MODAL);
        });
    }

}
