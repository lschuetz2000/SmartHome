package smarthome.util;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

import smarthome.managers.LogFileManager;
import smarthome.managers.LogFileManager.Log;

public class ErrorHandler {

    LocalDate date;
    LocalTime time;

    String errorMessage;

    public ErrorHandler(){
        date = LocalDate.now();
        time = LocalTime.now();
        errorMessage = "An error occurred. Date: " + date.toString() + "Time :" + time.toString() + "\n";
    }
    
    public void printToConsole(Exception e){
        System.out.println(errorMessage);
        e.printStackTrace();
    }

    public void addLog(Exception e){
        try {
            new LogFileManager(Log.ERROR).addLog(errorMessage + e.toString());
        } catch (IOException e2){
            e2.printStackTrace();
        }
    }

    public void printToConsoleAddLog(Exception e){
        printToConsole(e);
        addLog(e);
    }


}
