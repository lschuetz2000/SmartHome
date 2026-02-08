package smarthome.server;

import java.io.*;
import java.net.*;
import java.lang.Thread;
import java.time.*;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import java.util.ArrayList;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import smarthome.dialogs.*;
import smarthome.managers.LogFileManager;
import smarthome.managers.LogFileManager.Log;
import smarthome.managers.WhitelistManager;
import smarthome.util.ErrorHandler;

public class Server{
    private static Server instance;

    private boolean ON = false;
    private String IP;
    private int port;
    private int backlog; 
    private int clientCounter;
    
    private SSLServerSocket serverSocket;

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

    public void start(String SSLCertificatePassword){
        if (ON == false){
            try{
                
                KeyStore keyStore = KeyStore.getInstance("PKCS12");
                try (FileInputStream inputStream = new FileInputStream("./certificates/SSLLocalHostTestCertificate.pxf")){
                    keyStore.load(inputStream, SSLCertificatePassword.toCharArray());
                }

                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(keyStore, SSLCertificatePassword.toCharArray());

                SSLContext sslContext = SSLContext.getInstance("TLS");  
                sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
                
                serverSocket = (SSLServerSocket) sslContext.getServerSocketFactory().createServerSocket(port, 10, InetAddress.getByName("127.0.0.1"));

                System.out.println("Server listening on port " + port);
                System.out.println("Address: "  + serverSocket.getInetAddress().getHostAddress());

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
              
            } catch(SocketException e){
                for (int i = 0; i < threads.size();i++){
                    Thread thread = threads.get(i);
                    thread.interrupt();
                }
                ON = false;    
                new ErrorHandler().printToConsoleAddLog(e);
            } catch(KeyStoreException e){
                new ErrorHandler().printToConsoleAddLog(e);
            } catch(CertificateException e){
                new ErrorHandler().printToConsoleAddLog(e);
            } catch(UnrecoverableKeyException e){
                new ErrorHandler().printToConsoleAddLog(e);
            } catch(KeyManagementException e) {
                new ErrorHandler().printToConsoleAddLog(e);        
            } catch(NoSuchAlgorithmException e){    
                new ErrorHandler().printToConsoleAddLog(e);
            } catch(IOException e){
                new ErrorHandler().printToConsoleAddLog(e);
            }
        } else {
                System.out.println("Server already started");
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