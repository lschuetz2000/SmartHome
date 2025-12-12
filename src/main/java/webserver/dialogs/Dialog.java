package webserver.dialogs;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.InputMismatchException;

import webserver.server.*;
import webserver.util.ErrorHandler;
import webserver.managers.WhitelistManager;

public class Dialog implements Runnable{
    private static final Pattern PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    private ServerSocket serverSocket;

    public Dialog(){
        
    }

    public Dialog(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }
   
    public void start(String[] args){
        Server server;
        String IP;
        int port;
        int backlog;

        if (args.length == 1){
            String mode = args[0];

            switch (mode){
                case "start":
                    IP = "127.0.0.1";
                    port = 420;
                    backlog = 4;

                    server = Server.getInstance();
                    server.setBacklog(backlog);
                    server.setPort(port); 
                    server.setIP(IP);  
                    server.start();                
                break;

                case "startDefineProperties":
                    try{
                        Scanner scanner = new Scanner(System.in);
                        boolean ok = false;

                        System.out.println("Port:");
                        port = scanner.nextInt();
                        scanner.nextLine();

                        System.out.println("Backlog:");
                        backlog = scanner.nextInt();
                        scanner.nextLine();
                        
                        System.out.println("IPAddress:");
                        IP = scanner.nextLine();

                        ok = portOK(port);
                        ok = backlogOK(backlog);

                        boolean ipOK = ipOk(IP);

                        if (ipOK = true){
                            ok = true;
                        } else {
                            System.out.println("Invalid IP");
                            ok = false;
                        }

                        if (ok = true){
                            server = Server.getInstance();
                            server.setBacklog(backlog);
                            server.setPort(port);
                            server.setIP(IP);
                            server.start();                    
                        }

                        scanner.close();
                    } catch(NumberFormatException e){
                        System.out.println("Invalid Port or Number");
                    }
                break;

                case "":
                    System.out.println("Please enter an option");        
                break;

                case "help":
                    System.out.println("Command pattern: java -jar ./target/SmartHome-1.0.jar + 'option'. Options: start, startDefineProperties");
                break;
            }
        } else {
            System.out.println("Invalid command pattern");
        }
    }

    @Override
    public void run() {
        boolean DO = false;
        Scanner scanner = new Scanner(System.in);

        if (serverSocket == null){
            System.out.println("Terminal startet without initialising the Server. Terminal closed.");
            scanner.close();
        } else {
            DO = true;
        }
        
        while(DO){
            System.out.println("Enter command:");
            String command = scanner.nextLine();
            String[] keywords = command.trim().split(" ");
            boolean commandAccepted = false;
            
            switch (keywords[0]){
                
                case "Help":
                case "help":
                    System.out.println("Syntax: Powershell. Commands: 'Close-Server', 'Add-Entry -Target Whitelist -Type Custom', 'Add-Entry -Target Whitelist -Type User', 'Delete-Entry -Target Whitelist'");
                    commandAccepted = true;
                break;

                case "Close-Server":
                    try{
                        serverSocket.close();
                    } catch(IOException e){
                        System.out.println("Server closed");
                        new ErrorHandler().addLog(e);
                    }
                    
                    commandAccepted = true;
                    DO = false;
                    scanner.close();
                break;
                
                // not pretty but better than the previous solution i think   
                case "Add-Entry":
                    switch (keywords[1]) {
                        case "-Target":
                            switch (keywords[2]) {
                                case "Whitelist":
                                    switch (keywords[3]) {
                                        case "-Type":
                                            switch (keywords[4]) {
                                                case "User":
                                                    addUserEntryWhitelistDialog();
                                                    commandAccepted = true;
                                                break;
                                                    
                                                case "Custom":
                                                    addCustomEntryWhitelistDialog();
                                                    commandAccepted = true;                               
                                                break;
                                            
                                                default:
                                                    commandAccepted = false;
                                                break;
                                            }
                                        break;
                                    
                                        default:
                                            commandAccepted = false;
                                        break;
                                    }
                                break;

                                default:
                                    commandAccepted = false;
                                break;
                            }
                        break;
                    
                        default:
                            commandAccepted = false;
                        break;
                    }
                break;

                case "Delete-Entry":
                    switch (keywords[1]) {
                        case "-Target":
                            switch (keywords[2]) {
                                case "Whitelist":
                                    deleteUserEntryWhitelistDialog();
                                    commandAccepted = true;                                    
                                break;

                                default:
                                    commandAccepted = false;
                                break;
                            }
                        break;
                    
                        default:
                            commandAccepted = false;
                        break;
                    }
                break;

                case "Get-Content":
                    switch (keywords[1]) {
                        case "-Target":
                            switch (keywords[2]) {
                                case "Whitelist":
                                    try{
                                        System.out.println(new WhitelistManager().getWhitelist());
                                    } catch (IOException e){
                                        new ErrorHandler().printToConsoleAddLog(e);
                                    }
                                break;

                                default:
                                    commandAccepted = false;
                                break;
                            }
                        break;
                    
                        default:
                            commandAccepted = false;
                        break;
                    }
                break;

            }

            if (commandAccepted == false){
                System.out.println("Command does not exist");
            }
        }    
    }

    public void addCustomEntryWhitelistDialog(){
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Please enter the user data to save in the whitelist");
        
        System.out.println("IP:");
        String ip = scanner.nextLine();

        System.out.println("Host name:");
        String hostName = scanner.nextLine();

        Client client = new Client(ip,hostName);

        try{
            new WhitelistManager().addClient(client);
        } catch(IOException e){
            new ErrorHandler().printToConsoleAddLog(e);
        }
    }

    public void addUserEntryWhitelistDialog(){
        
        ArrayList<Client> clients =  Server.getInstance().getClients();
        
        if (clients.size() == 0){
            System.out.println("There is no user who tried to connect to the server since the server has started");
        } else {
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("Which user do you want to add to the whitelist?");
            System.out.println("Please enter the number of the user you want to add. To cancel this operation, enter 'cancel'");

            int counter = 0;

            for (int i = 0; i < clients.size(); i++){
                counter++;
    
                Client client = clients.get(i);
    
                String ip = client.getIp();
                String hostName = client.getHostname();
    
                System.out.println("User_" + counter + ":");
                System.out.println("IP: " + ip + "Host name: " + hostName);
            }
            
            String entry = scanner.nextLine();
    
            if (entry.equals("cancel")){
                
            } else {
                int user = Integer.parseInt(entry);
                Client client = clients.get(user);
    
                try{
                    new WhitelistManager().addClient(client);
                } catch(IOException e){
                    new ErrorHandler().printToConsoleAddLog(e);
                }
            }
        }
    }

    public void deleteUserEntryWhitelistDialog(){
        Scanner scanner = new Scanner(System.in);
        String whitelist = "";
 
        try {
            whitelist = new WhitelistManager().getWhitelist();
        } catch (IOException e){
            new ErrorHandler().printToConsoleAddLog(e);
        }

        System.out.println("Whitelist:\n" + whitelist);
        System.out.println("Do you want to delete an entry from this list? Enter Y | N");
        String input = scanner.nextLine();

        String ip;
        String hostName;

        switch (input){

            case "Y":
            case "y":
                System.out.println("Please enter the IP and host name of the client you want to delete");

                System.out.println("IP:");
                ip = scanner.nextLine();

                System.out.println("Host name:");
                hostName = scanner.nextLine();

                String clientData = new Client(ip, hostName).toString();

                if (whitelist.contains(clientData)){
                    whitelist = whitelist.replace(clientData,"");    

                    try {
                        new WhitelistManager().writeEntry(whitelist);
                    } catch(IOException e){
                        new ErrorHandler().printToConsoleAddLog(e);
                    }
                } else {
                    System.out.println("Client not found");
                }

            break;

            case "N":
            case "n":

            break;
        
            default:
                System.out.println("Invalid input");
            break;
        }


    }

    public boolean portOK(int port){
        if (port > 9999){
            System.out.println("Invalid port");
            return false;
        } else if(port < 1000){
            System.out.println("Invalid port");
            return false;
        } return true;
    }
    
    public boolean backlogOK(int backlog){
        if (backlog > 10){
            System.out.println("Invalid backlog. Min: 1 Max: 10");
            return false;
        } else if(backlog < 1){
            System.out.println("Invalid backlog. Min: 1 Max: 10");
            return false;
        } return true;
    }

    public boolean ipOk(String ip) {
        return PATTERN.matcher(ip).matches();
    }
}   
