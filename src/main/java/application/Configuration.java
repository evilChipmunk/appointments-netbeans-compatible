package application;

public class Configuration {


    public Configuration() {
    }

    public String getConnectionString() {
        return  "jdbc:mysql://localhost:3306/appointments?autoReconnect=true&useSSL=false";
    }

    public String getConnectionUserName(){return "mysqluser";}

    public String getConnectionPassword() {return "mysql";}

    public int getRetry() {
        return 3;
    }

    public double getMainPanelPrefWidth(){
        return 900;
    }

    public double getMainPanelPrefHeight(){
        return 600;
    }


    public double getMainWindowPrefWidth(){
        return 1490;
    }

    public double getMainWindowPrefHeight(){
        return 900;
    }


    public double getBusinessStartHour(){
        return 8;
    }

    public double getBusinessEndHour(){
        return 19;
    }

    public double getSmallAppointmentWidth() {
        return 710;
    }

    public double getSmallAppointmentHeight(){
        return 460;
    }

    public long getReminderCheck() {
        return 60000;
    }

    public int getDefaultSnoozeIncrement() {
        return 5;
    }

    public long getReminderRange() {
        return 15;
    }
}

