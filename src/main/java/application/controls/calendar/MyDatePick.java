package application.controls.calendar;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class MyDatePick{
    int year;
    int monthNum;
    String month;

    public MyDatePick(int year, int monthNum){
        this.year = year;
        this.monthNum = monthNum;
        this.month = getMonth(monthNum);
    }

    public int getYear(){
        return year;
    }

    public String getMonth(){
        return month;
    }

    public int getMonthNum(){
        return monthNum;
    }

    @Override
    public String toString(){
        return getMonth() + " " + getYear();
    }

    public ZonedDateTime getDate(){
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime seedDate = ZonedDateTime.of(year, monthNum, 1, now.getHour(), 0, 0, 0, ZoneId.systemDefault());

        return seedDate;
    }

    private String getMonth(int monthNum){
        switch (monthNum){
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
        }
        return "";
    }
}
