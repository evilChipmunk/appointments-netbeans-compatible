package application.logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException; 
import java.time.ZonedDateTime;

public class Logger implements ILogger{

    @Override
    public void Log(String message) {

        String filename = "log.txt";
        String path = Logger.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File fullPath = new File(path, filename);

        File directory =  new File(path);
        if (!directory.exists()){

            directory.mkdir();
        }
        try(FileWriter writer = new FileWriter(fullPath,true)){

            writer.write(ZonedDateTime.now() + ": " + message + "\n");

        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void Log(Exception ex) {

    }

    @Override
    public void Log(String message, Exception e) {

    }
}
