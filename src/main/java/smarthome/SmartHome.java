package smarthome;

import smarthome.dialogs.*;
import smarthome.util.ErrorHandler;

public class SmartHome {
    public static void main(String[] args) {
        try{
            new Dialog().start(args);
        } catch(Exception e){
            new ErrorHandler().printToConsoleAddLog(e);
        }  
    }
}