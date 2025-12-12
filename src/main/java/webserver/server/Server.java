package webserver.server;

import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.time.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import webserver.dialogs.*;
import webserver.managers.LogFileManager;
import webserver.managers.LogFileManager.Log;
import webserver.managers.WhitelistManager;
import webserver.util.ErrorHandler;

public class Server{
    private static Server instance;

    private boolean ON = false;
    private String IP;
    private int port;
    private int backlog; 
    private int clientCounter;
    
    private ServerSocket serverSocket;

    private ArrayList<Client> clients = new ArrayList<Client>();
    private ArrayList<Thread> threads = new ArrayList<Thread>();

    public Server(){

    }

    public Server(int port, int backlog, String IP){
        this.IP = IP;
        this.port = port;
        this.backlog = backlog;
    }

    public static Server getInstance(){
        if (instance == null){
            instance = new Server();
        }

        return instance;
    }

    public void start(){
        try{
            if (ON == false){

                serverSocket = new ServerSocket();
                InetSocketAddress socketAddress = new InetSocketAddress(IP, port);
                serverSocket.bind(socketAddress);

                System.out.println("Server listening on port " + port);
                System.out.println("Address: " + serverSocket.getInetAddress().getHostAddress());

                ON = true;    

                new Thread(new Dialog(serverSocket)).start();

                while(ON){

                    Socket clientSocket = serverSocket.accept();
                    
                    String clientIP = clientSocket.getInetAddress().getHostAddress();
                    String clientName = clientSocket.getInetAddress().getHostName();
                    Client client = new Client(clientIP, clientName);

                    if (!clients.contains(client)){
                        clients.add(client);
                    }

                    LocalDate date = LocalDate.now();
                    LocalTime time = LocalTime.now();
                    String log = "New user connected. IP: " + clientIP + " HostName: " + clientName + " Date: " + date + " Time: " + time;
                    new LogFileManager(Log.LOGIN).addLog(log);
                    System.out.println(log);
                    
                    WhitelistManager whitelistManager = new WhitelistManager();

                     if (whitelistManager.ClientOK(client)){
                        Thread thread = new Thread(new ClientHandler(clientSocket));
                        thread.start();
                        threads.add(thread);
                    } else{
                        String logNotWhitelisted = "User not whitelisted. Connection refused."; 
                        System.out.println(logNotWhitelisted);
                        new LogFileManager(Log.LOGIN).addLog(logNotWhitelisted);
                    }
                }
            } else {
                System.out.println("Server already started");
            }  
        } catch(SocketException e){
            for (int i = 0; i < threads.size();i++){
                Thread thread = threads.get(i);
                thread.interrupt();
            }
            ON = false;    
            System.out.println("Server closed");
        } catch(IOException e){
            System.out.println("Server exception");
            new ErrorHandler().printToConsoleAddLog(e);
        }
    }   

    public boolean getONState(){
        return this.ON;
    }

    public void close() throws IOException{
        ON = false;
        if (serverSocket != null){
            serverSocket.close();
        } else {
            System.out.println("Server not open");
        }
    }

    public void setClientCounter(Integer clientCounter){
        this.clientCounter = clientCounter;
    }

    public void setPort(int port){
        this.port = port;
    }

    public void setBacklog(int backlog){
        this.backlog = backlog;
    }

    public void setIP(String IP){
        this.IP = IP;
    }

    public Integer getCLientCounter(){
        return this.clientCounter;
    }

    public ArrayList<Client> getClients(){
        return this.clients; 
    }

    public void reduceClientCounterByOne(){
        this.clientCounter--;
    }

    public void increaseClientCounterByOne(){
        this.clientCounter++;
    }
    
}