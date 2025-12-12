package webserver;

import webserver.dialogs.*;
import webserver.util.ErrorHandler;

public class WebServer {
    public static void main(String[] args) {
        try{
            new Dialog().start(args);
        } catch(Exception e){
            new ErrorHandler().printToConsoleAddLog(e);
        }  
    }
}