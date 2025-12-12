package smarthome.managers;

import java.io.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import smarthome.util.ErrorHandler;


public class ScreenManager{

    private OutputStream output;
    
    public ScreenManager(OutputStream output){
        this.output = output;
    }

    public void showLogin(){
        try {
            File file = new File("./frontend/index.html");
            Document doc = Jsoup.parse(file, "UTF-8");
            output.write(doc.outerHtml().getBytes());
        } catch (IOException e){
            new ErrorHandler().printToConsoleAddLog(e);
        }
    }

}