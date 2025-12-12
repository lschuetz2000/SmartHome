package webserver.server;

import webserver.managers.ScreenManager;
import webserver.util.ErrorHandler;

import java.net.*;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.*;


public class ClientHandler implements Runnable{
    
    private Socket socket;
    private boolean awaitResponse = true;

    private PrintWriter writer;
    private BufferedReader reader;

    public ClientHandler(Socket socket){
        this.socket = socket;
    }
    
    @Override
    public void run() {
        try{

            while (awaitResponse && !Thread.interrupted()){

                OutputStream output = socket.getOutputStream();
                InputStream input = socket.getInputStream();

                writer = new PrintWriter(output);
                reader = new BufferedReader(new InputStreamReader(input));   

                ArrayList<String> request = new ArrayList<String>();
                
                boolean read = true;    
                
                while (read){
                    String line = reader.readLine();
                    if (line == null){
                        read = false;
                        break;
                    }

                    if (line.isEmpty()){
                        read = false;
                    } else {
                        request.add(line);
                    }
                }

                /* *for testing http* 
                */
                for (int i = 0; i < request.size(); i++){
                    System.out.println(request.get(i));
                }
                if (request.size() != 0){
                    String line0 = request.get(0);
                    
                    if (line0.contains("GET")){
                        String[] parts = line0.split(" ");
                        String requestedFile = parts[1];
                        sendFile(requestedFile,output);
                    } else if (line0.contains("POST")){
                        if (line0.contains("fW6zTqJ0nPBmKv19aXcdLryOUE38gZsj")){
                            
                        }
                    }
                }
            }
        } catch(Exception e){
            new ErrorHandler().printToConsoleAddLog(e);
        }
    }

    private void sendFile(String fileName, OutputStream output){
        String contentType = "";
        String endOfFileName = "";
        boolean fileFound = true;

        if (fileName.equals("index.html")){
            fileName = fileName.replace("index", "login");
        }

        if (fileName.contains(".")){

            endOfFileName = fileName.split("\\.")[1];
            
            switch (endOfFileName) {
                case "html":
                    contentType = "text/html; charset=UTF-8s";    
                break;
            
                case "css":
                    contentType = "text/css";
                break;
    
                case "javascript":
                    contentType = "application/javascript";
                break;
    
                case "png":
                    contentType = "image/png";
                break;
    
                case "jpeg":
                    contentType = "image/jpeg";
                break;
            }

        } else if (fileName.equals("/")){
            contentType = "text/html; charset=UTF-8s";
            endOfFileName = "html";
            fileName = "login.html";
        } else {    
            fileFound = false;
        }

        byte [] content = null;

        try {
            Path path = Path.of(".\\frontend\\" + endOfFileName + "\\" + fileName);
            content = Files.readAllBytes(path);
        } catch (Exception e){
            fileFound = false;    
        }

        if (fileFound == true){
            sendContentHTTP(content, contentType, output);
        } else{
            sendTextHTTP("404 Not Found", "404 Not Found");
        }
                    	    
    }
    
    private void sendContentHTTP(byte [] content, String contentType, OutputStream output){
        writer.println("HTTP/1.1 200 OK");
        writer.println("Content-Type: " + contentType);
        writer.println("Content-Length: " + content.length);
        writer.println();
        writer.flush();

        try{
            output.write(content);
            output.flush();
        } catch (Exception e){
            new ErrorHandler().printToConsoleAddLog(e);
        }
    }

    private void sendTextHTTP(String HTTPstatus, String text){
        writer.println("HTTP/1.1 " + HTTPstatus);
        writer.println("Content-Type: text/plain");
        writer.println("Content-Length: " + Integer.toString(text.length()));
        writer.println();
        writer.println(text);
        writer.flush();
    }

    public void setResponse(boolean awaitResponse){
        this.awaitResponse = awaitResponse;
    }

}