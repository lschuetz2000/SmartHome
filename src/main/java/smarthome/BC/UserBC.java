package smarthome.BC;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;

import smarthome.exceptions.UserNotFoundException;
import smarthome.database.DatabaseConnector;
import smarthome.database.DatabaseOperator;
import smarthome.util.ErrorHandler;
import smarthome.models.User;

public class UserBC{

    public void writeUser(){
        try(Connection connection = new DatabaseConnector().getConnection()){
            DatabaseOperator operator = new DatabaseOperator(connection);
            
        } catch (SQLException e){
            new ErrorHandler().printToConsoleAddLog(e);
        }
    }

    public ArrayList<User> getUsers(User user) throws UserNotFoundException {
        ResultSet rs = null;
        ArrayList<User> users = null;
        
        
        return users;
    }
}