package application;

public class Configuration {


    public Configuration() {
    }

    public String getConnectionString() {
        return  "jdbc:mysql://52.206.157.109/U05bhH?autoReconnect=true&useSSL=false";
    }

    public String getConnectionUserName(){return "U05bhH";}

    public String getConnectionPassword() {return "53688456792";}

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

