package application.controls;

import application.controls.screens.*; 
import application.controls.calendar.CalendarControl; 
import application.messaging.Commands;
import application.messaging.IListener;
import exceptions.AppointmentException;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane; 
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.time.ZonedDateTime;

public class SidePanelControl extends GridPane {


    private final CustomerControl customer;
    private final CalendarControl calendar;
    private final AppointmentControl appointment;
    private final ReportControl report;
    private final ReadmeControl readme;
    private IListener listener;

    public SidePanelControl( CustomerControl customer, CalendarControl calendar,
                            AppointmentControl appointment, ReportControl report, ReadmeControl readme){


        this.customer = customer;
        this.calendar = calendar;
        this.appointment = appointment;
        this.report = report;
        this.readme = readme;
        ScreenLoader.load("/views/SidePanel.fxml", this, this);
    }

     public void addListener(IListener listener){
        this.listener = listener;
     }

    @FXML
    public void initialize(){

    }


    @FXML
    private void customer(javafx.scene.input.MouseEvent event){

        listener.actionPerformed(Commands.addControl, customer);
    }

    @FXML
    private void calendar(javafx.scene.input.MouseEvent event)  {

        try {
            
            calendar.AddMonth(ZonedDateTime.now());
        } catch (AppointmentException e) {
            ErrorControl errorControl = new ErrorControl(e);
            errorControl.show();
        }

        listener.actionPerformed(Commands.addControl, calendar);
    }

    @FXML
    private void appointment(javafx.scene.input.MouseEvent event){
        
        appointment.RefreshCustomers();
        listener.actionPerformed(Commands.addControl, appointment);
    }

    @FXML
    private void report(javafx.scene.input.MouseEvent event){ 
        listener.actionPerformed(Commands.addControl, report);
    }

    @FXML
    private void readme(javafx.scene.input.MouseEvent event) {


        try {

            URL url = this.getClass().getClassLoader().getResource("readme.pdf");
            URI uri = url.toURI();

            File ff = new File(uri);
            String fullPath = ff.getPath();
            if ((new File(fullPath)).exists()) {

                Process p = Runtime
                        .getRuntime()
                        .exec("rundll32 url.dll,FileProtocolHandler " + fullPath);
                p.waitFor();

            }
            else{

                ErrorControl control = new ErrorControl("Unable to display pdf. It is located in the resources section");
                control.show();
            }

        } catch (Exception ex) {
            ErrorControl control = new ErrorControl("Unable to display pdf. It is located in the resources section");
            control.show();
        }

    }

    public void showCalendar() throws AppointmentException {
        calendar(null);
    }
    
    public void load(){
        report.load();
    }
}
