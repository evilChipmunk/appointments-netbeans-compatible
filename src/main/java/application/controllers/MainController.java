package application.controllers;

import application.alerts.AlertContent;
import application.alerts.AlertFactory;
import application.alerts.ReminderAlertBuilder;
import application.controls.SidePanelControl;
import application.messaging.Commands;
import application.Configuration; 
import application.controls.ControlFader;
import application.controls.IMainPanelView;
import application.controls.SystemMessageControl;
import application.controls.calendar.CalendarControl;
import application.controls.calendar.ICalendarControl;
import application.controls.calendar.MonthPickerDialog;
import application.controls.screens.*;
import application.messaging.IListener;
import application.services.IReminderService;
import exceptions.AppointmentException;
import exceptions.ValidationException;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Bounds; 
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*; 
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import models.Reminder; 
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional; 

public class MainController implements IListener {

    private static MainController instance;
    private final Configuration config;
    private final IReminderService reminderService;
    private final AlertFactory alertFactory;
    private final LoginControl login;
    private Stage stage;
    private boolean widthChanged;
    private boolean heightChanged;
    private double windowWidth;
    private double windowHeight;
    AnchorPane aPane = new AnchorPane();


    public MainController(LoginControl login, SidePanelControl sidePanel, IReminderService reminderService, AlertFactory alertFactory, Configuration config) {

        instance = this;
        this.login = login;
        this.config = config;
        this.sidePanel = sidePanel;

        this.reminderService = reminderService;
        this.alertFactory = alertFactory;

    }

    public static MainController getInstance() {
        return instance;
    }
     
    public ReadOnlyObjectProperty<Bounds> getMainPanelBoundsProperty() {
        return aPane.layoutBoundsProperty();
        // return borderPane.getCenter().boundsInParentProperty();
    }

    public Stage getStage() {
        return stage;
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
        //the first time the window is shown, it is the login (small) size
        //after that has been established start listening for screen changes
        //that way if a user has resized their window, subsequent calls to
        //addMainControl will not resize their winddow
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, window -> {
            stage.widthProperty().addListener((observable, oldValue, newValue)
                    -> {
                windowWidth = newValue.doubleValue();
                widthChanged = true;
            });

            stage.heightProperty().addListener((observable, oldValue, newValue) -> {

                windowHeight = newValue.doubleValue();
                heightChanged = true;
            });
        });

        stage.setOnCloseRequest(we -> reminderService.Stop());
    }

    @FXML
    private final SidePanelControl sidePanel;

    @FXML
    BorderPane borderPane;
 
    @FXML
    public void initialize() throws AppointmentException, ValidationException {

        setupLogin();
        sidePanel.addListener(this);
    }

    @Override
    public void actionPerformed(Commands command, Object... args) {

        try {
            switch (command) {
                case authenticate:

                    borderPane.setLeft(sidePanel);
                    sidePanel.load();
                    sidePanel.showCalendar();

                    startReminderService();

                    break;
                case appointmentCreated:
                case appointmentDeleted:
                    sidePanel.showCalendar();
                    break;
                case validationException:
                    if (args != null && args.length > 0) {
                        String message = String.valueOf(args[0]);
                        addSystemMessage(message);
                    }
                    break;
                case messageAck:
                    borderPane.setTop(null);
                    break;
                case addControl:
                    addMainControl((IMainPanelView) args[0]);
                    break;
                case monthPickerShown:
                    showMonthPicker(args);
                    break;
                case monthPickerSelected:
                        selectMonth(args);
                    break;

            }
        } catch (AppointmentException ex) {
            ErrorControl control = new ErrorControl(ex);
            control.show();
        }
    }

    private void startReminderService() {

        reminderService.getHasReminders().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                return;
            }
            Platform.runLater(()
                    -> {
                ArrayList<Reminder> list = reminderService.getReminders();
                String header = "You have the following appointments: " + "\n" + "\n";
                String message = "";

                for (Reminder rem : list) {
                    DateTimeFormatter format = DateTimeFormatter.ofPattern("hh:mm a");

                    message
                            += rem.getAppointment().getTitle() + " with "
                            + rem.getAppointment().getCustomer().getName() + " at "
                            + rem.getAppointment().getStart().format(format) + "\n";
                }

                if (message.isEmpty()) {
                    return;
                }
                message = header + message;

                AlertContent content = new AlertContent();
                content.setTitle("Appointment Notice");
                content.setMessage("Customer appointments");
                content.setLargeContent(message);
                Alert alert = alertFactory.create(new ReminderAlertBuilder(), content);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    list.forEach((rem) -> {
                        reminderService.Acknowledge(rem);
                    });
                }
            }
            );
        });
        reminderService.Start();
    }

    private void showMonthPicker(Object[] args) {
        if (args != null && args.length == 2) {
            double x = (double) args[0];
            double y = (double) args[1];
            MonthPickerDialog dialog = new MonthPickerDialog();
            dialog.addListener(this);
            if (x != 0 && y != 0) {
                dialog.setLayoutX(x);
                dialog.setLayoutY(y);
            }

            ICalendarControl control = (ICalendarControl) aPane.getChildren().get(0);
            dialog.show(control.getActualFirstDate());
        }
    }

    private void selectMonth(Object[] args) {
        if (args != null && args.length == 1) {
            ZonedDateTime date = (ZonedDateTime) args[0];
            ICalendarControl control = (ICalendarControl) aPane.getChildren().get(0);
            control.AddMonth(date);
            this.addMainControl((IMainPanelView) control);
        }
    }
    
    private void setupLogin() {
        login.addListener(this);
        borderPane.setCenter(login);
    }
 
    public void addMainControl(IMainPanelView view) {

        view.addListener(this);

        if (heightChanged) {

            stage.setHeight(windowHeight);
        } else {
            stage.setHeight(config.getMainWindowPrefHeight());
        }

        if (widthChanged) {
            stage.setWidth(windowWidth);
        } else {
            stage.setWidth(config.getMainWindowPrefWidth());
        }

        Node nodeView = (Node) view;

        nodeView.prefWidth(config.getMainPanelPrefWidth());
        nodeView.prefHeight(config.getMainPanelPrefHeight());

//            StackPane stackPane = new StackPane();
//            
//            stackPane.prefWidth(config.getMainPanelPrefWidth());
//            stackPane.prefHeight(config.getMainPanelPrefHeight());
//            
//            final DoubleProperty opacity = stackPane.opacityProperty();
//            ControlFader fader = new ControlFader();
//            fader.fadeInChild(nodeView, stackPane, opacity, 300);
        AnchorPane.setTopAnchor(nodeView, 10.0);
        AnchorPane.setLeftAnchor(nodeView, 10.0);
        AnchorPane.setRightAnchor(nodeView, 10.0);
        AnchorPane.setBottomAnchor(nodeView, 10.0);
        aPane.getChildren().clear();
        aPane.getChildren().add(nodeView);

        aPane.prefWidth(config.getMainPanelPrefWidth());
        aPane.prefHeight(config.getMainPanelPrefHeight());

        stage.centerOnScreen();
        borderPane.setCenter(null);
        borderPane.setCenter(aPane);

        if (nodeView instanceof CalendarControl) {
            CalendarControl control = (CalendarControl) nodeView;
            control.setContentSize(getMainPanelBoundsProperty());
        }

        // Label lbl = setTopLabel();
        getMainPanelBoundsProperty().addListener((observable, oldValue, newValue) -> {

            double width = newValue.getWidth();
            double height = newValue.getHeight();

            //  lbl.setText("width: " + width + " height: " + height );
        });

//        aPane.getChildren().add(lbl);
//        borderPane.setCenter(aPane);
////
////        ScrollPane scroller = new ScrollPane();
////        BackgroundFill fill = new BackgroundFill(Color.TRANSPARENT.brighter(), CornerRadii.EMPTY, Insets.EMPTY);
////        Background background =  new Background(fill);
////        scroller.setBackground(background);
////        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
////        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
////        scroller.setContent(nodeView);
//        Node nodeToAdd;
//
//        if (nodeView instanceof CalendarControl) {
//            //nodeToAdd = scroller;
//
//            nodeView.prefWidth(config.getMainPanelPrefWidth());
//            nodeView.prefHeight(config.getMainPanelPrefHeight());
//
//            StackPane stackPane = new StackPane();
//            stackPane.getChildren().add(nodeView);
//
//            stage.centerOnScreen();
//            borderPane.setCenter(null);
//            borderPane.setCenter(stackPane);
//            
//            CalendarControl control = (CalendarControl)nodeView;
//
//            ((CalendarControl) nodeView).loadScreen();
//                        Alert alert = new Alert(AlertType.INFORMATION, control.getWidth() + " " + control.getHeight());
//                        alert.showAndWait();
//        } else {
//            nodeToAdd = nodeView;
//            nodeToAdd.prefWidth(config.getMainPanelPrefWidth());
//            nodeToAdd.prefHeight(config.getMainPanelPrefHeight());
//
//            StackPane stackPane = new StackPane();
//            final DoubleProperty opacity = stackPane.opacityProperty();
//            ControlFader fader = new ControlFader();
//            fader.fadeInChild(nodeToAdd, stackPane, opacity, 300);
//
//            stage.centerOnScreen();
//            borderPane.setCenter(null);
//            borderPane.setCenter(stackPane);
//        }
    }

    private Label setTopLabel() {
        Label lbl = new Label();
        lbl.setStyle("    -fx-text-fill: white!important;\n"
                + "    -fx-font-weight: bold;\n"
                + "    -fx-underline: false;\n"
                + "    -fx-font-size: 16;");
        lbl.setPrefWidth(config.getMainPanelPrefWidth());
        borderPane.setTop(lbl);
        return lbl;
    }

    private void addSystemMessage(String message) {

        SystemMessageControl control = new SystemMessageControl(message, (x) -> {
            borderPane.setTop(null);
        });
        control.getStyleClass().add("validationWarning");

        StackPane stackPane = new StackPane();
        final DoubleProperty opacity = stackPane.opacityProperty();

        borderPane.setTop(stackPane);
        ControlFader fader = new ControlFader();
        fader.fadeInChild(control, stackPane, opacity, 200);

    }

}
