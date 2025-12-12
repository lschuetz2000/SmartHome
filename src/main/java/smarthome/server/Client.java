package smarthome.server;

import smarthome.server.Client;

public class Client{
    String ip;
    String hostname;

    public Client(String ip, String hostname){
        this.ip = ip;
        this.hostname = hostname;
    }

    public void setIp(String ip){
        this.ip = ip;
    }

    public void setHostname(String hostname){
        this.hostname = hostname;
    }

    public String getIp(){
        return ip;
    }

    public String getHostname(){
        return hostname;
    }

    public String toString(){
        String clientData = "IP: " + this.ip + " HostName: " + this.hostname;
        return clientData;
    }
}