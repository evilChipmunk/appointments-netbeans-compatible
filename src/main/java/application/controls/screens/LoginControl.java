package application.controls.screens;

import application.alerts.AlertContent;
import application.alerts.AlertFactory;
import application.alerts.AlertType;
import application.alerts.LoginAlertBuilder;
import application.logging.ILogger;
import application.messaging.Commands;
import application.messaging.IListener;
import application.services.IUserService;
import exceptions.AppointmentException;
import exceptions.ValidationException;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import models.User;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginControl extends VBox {

    private final IUserService userService;
    private final AlertFactory factory;
    private final ILogger logger;
    private IListener listener;
    private boolean authenticated;
    private ResourceBundle messageBundle;

    private SimpleBooleanProperty buttonEnabledProperty = new SimpleBooleanProperty(true);


    @FXML
    private TextField txtUserName;

    @FXML
    private PasswordField txtPassword;

    @FXML
    Label lblUserName;

    @FXML
    Label lblPassword;

    @FXML
    Button btnLogin;

    @FXML
    Button btnRegister;

    public LoginControl(IUserService userService, AlertFactory factory,  ILogger logger) {
        this.userService = userService;
        this.factory = factory;
        this.logger = logger;



        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    public void initialize() {

        btnRegister.disableProperty().bindBidirectional(buttonEnabledProperty);
        btnLogin.disableProperty().bindBidirectional(buttonEnabledProperty);
        txtUserName.textProperty().addListener((observable, oldValue, newValue) ->
                buttonEnabledProperty.set(newValue.isEmpty() || txtPassword.getText().isEmpty()));

        txtPassword.textProperty().addListener((observable, oldValue, newValue) ->
                buttonEnabledProperty.set(newValue.isEmpty() || txtUserName.getText().isEmpty()));

        Locale locale = Locale.getDefault();  //Locale.GERMAN;
        messageBundle = ResourceBundle.getBundle("LogInView", locale);

        String userName = messageBundle.getString("lblUserName");
        String password = messageBundle.getString("lblPassword");
        String loginCommand = messageBundle.getString("btnLogin");
        String registerCommand = messageBundle.getString("btnRegister");


        txtUserName.setPromptText(userName);
        lblUserName.setText(userName);

        txtPassword.setPromptText(password);
        lblPassword.setText(password);

        btnLogin.setText(loginCommand);
        btnRegister.setText(registerCommand);
        
//        this.txtUserName.setText("user");
//        this.txtPassword.setText("pass");
      //  authenticateUser();
    }

    @FXML
    public void register(ActionEvent event) throws AppointmentException {
        try{

            if(txtPassword.getText().isEmpty() || txtUserName.getText().isEmpty()){
                throw new Exception();
            }
            userService.registerUser(txtUserName.getText(), txtPassword.getText());
            User authUser = userService.authenticateUser(txtUserName.getText(), txtPassword.getText());
            authenticated = true;
            listener.actionPerformed(Commands.authenticate);
        }
        catch (Exception ex){
            authenticated = false;
            AlertContent content = new AlertContent();
            content.setAlertType(AlertType.Error);
            content.setTitle(messageBundle.getString("Error"));
            content.setMessage(messageBundle.getString("registerError"));
            Alert alert = factory.create(new LoginAlertBuilder(), content);
            alert.show();
        }
    }

    @FXML
    public void login(ActionEvent actionEvent) throws AppointmentException, ValidationException {

        authenticateUser();
    }

    public void addListener(IListener listener){
        this.listener = listener;
    }

    @FXML
    public void userName(KeyEvent event){
        lblUserName.setVisible(true);

    }

    @FXML
    public void password(KeyEvent event){

        lblPassword.setVisible(true);
    }

    private void authenticateUser() throws AppointmentException {

        String userName = txtUserName.getText();


        try{

            if(txtPassword.getText().isEmpty() || txtUserName.getText().isEmpty()){
                throw new Exception();
            }
            User authUser = userService.authenticateUser(userName, txtPassword.getText());
            if (authUser == null){

                logger.Log(userName + " attempted to login at " + ZonedDateTime.now());
                showAlert(() -> {
                    AlertContent content = new AlertContent();
                    content.setTitle(messageBundle.getString("Warning"));
                    content.setMessage(messageBundle.getString("loginError"));
                    return factory.create(new LoginAlertBuilder(), content);
                });
            }
            else{
                logger.Log(userName + " logged in at " + ZonedDateTime.now());
                authenticated = true;
                listener.actionPerformed(Commands.authenticate);
            }
        }
        catch (Exception ex){
            showAlert(() -> {
                AlertContent content = new AlertContent();
                content.setTitle(messageBundle.getString("Error"));
                content.setMessage(messageBundle.getString("loginError"));
                return factory.create(new LoginAlertBuilder(), content);
            });
        }
    }

    private interface IShow{
        Alert getAlert();
    }

    private void showAlert(IShow show){
        Alert alert = show.getAlert();
        alert.show();
    }
}
