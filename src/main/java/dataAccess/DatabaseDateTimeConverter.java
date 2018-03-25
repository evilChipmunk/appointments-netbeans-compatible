package dataAccess;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.TimeZone;

public class DatabaseDateTimeConverter{

    public static java.sql.Date getSqlDate(ZonedDateTime date){

        ZonedDateTime convertedDate = convertToUTC(date);
        java.sql.Date sqlDate = new java.sql.Date(convertedDate.toInstant().toEpochMilli());
        return sqlDate;
    }
//
    public static java.sql.Timestamp getSqlTimestamp(ZonedDateTime dateTime) {

        ZonedDateTime convertedDate = convertToUTC(dateTime);
        Timestamp stamp =  new Timestamp(convertedDate.toInstant().getEpochSecond());
        stamp = Timestamp.from(convertedDate.toInstant());
        return stamp;
    }

    public static void setSqlTimestamp(Timestamp stamp, CallableStatement stmt, String paramName) throws SQLException {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

//        ZonedDateTime date = ZonedDateTime.now();
//        ZonedDateTime converted = convertToUTC(date);
//        Timestamp convertedStamp = Timestamp.from(converted.toInstant());

        stmt.setTimestamp(paramName, stamp, cal);
    }

    public static ZonedDateTime convertToUTC(ZonedDateTime date){

        return date.withZoneSameInstant(ZoneId.of("UTC"));
    }

    public static ZonedDateTime convertFromUTC(ZonedDateTime date){
        return date.withZoneSameInstant(ZoneId.of(ZoneId.systemDefault().getId()));
    }



//    public static ZonedDateTime getZoneDateTime(java.sql.Date date){
//
//    }

    public static ZonedDateTime getZoneDateTime(Timestamp timeStamp){

        Instant instant = Instant.ofEpochMilli(timeStamp.getTime());

        ZoneId defaultZone = ZoneId.systemDefault();
        ZonedDateTime zoneDateTime = instant.atZone(defaultZone);

        ZonedDateTime convertedDate = convertFromUTC(zoneDateTime);
        return convertedDate;
    }


    private static String convertSQLDateToString(ZonedDateTime date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String converted = date.toString();
        converted = dateFormat.format(date);

        return converted;
    }

    private String convertSQLDateToString(java.sql.Date date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        String converted = dateFormat.format(date);

        return converted;
    }
}
