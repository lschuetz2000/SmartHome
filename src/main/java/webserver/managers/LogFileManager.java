package webserver.managers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LogFileManager{

    private File logFile;

    private String loginLogPath = "./src/main/java/webserver/logs/LoginLog.txt";
    private String errorLogPath = "./src/main/java/webserver/logs/ErrorLog.txt";

    private FileWriter fileWriter;

    public enum Log{    
        LOGIN,
        ERROR;
    }

    public LogFileManager(Log chosenLog) throws IOException{

        switch (chosenLog){

            case LOGIN:
                logFile = new File(loginLogPath);
            break;

            case ERROR:
                logFile = new File(errorLogPath);
            break;
        }
        
        logFile.setWritable(true);
        fileWriter = new FileWriter(logFile, true);
        logFile.setReadOnly();
    }

    public void addLog(String log) throws IOException{
        logFile.setWritable(true);
        fileWriter.write(log + "\n");
        logFile.setReadOnly();
    }

    public String getLogs() throws IOException{
        String logs = Files.readString(Path.of(logFile.getAbsolutePath()));
        return logs;
    }
}