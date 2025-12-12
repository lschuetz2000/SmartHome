package webserver.BC;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;

import webserver.exceptions.UserNotFoundException;
import webserver.database.DatabaseConnector;
import webserver.database.DatabaseOperator;
import webserver.util.ErrorHandler;
import webserver.models.User;

public class UserBC{

    public void writeUser(){
        try(Connection connection = new DatabaseConnector().getConnection()){
            
        } catch (SQLException e){
            new ErrorHandler().printToConsoleAddLog(e);
        }
    }

    public ArrayList<User> getUsers(User user) throws UserNotFoundException {
        ResultSet rs = null;
        ArrayList<User> users = null;
        
        try(DatabaseOperator operator = new DatabaseOperator()){
            rs = new DatabaseOperator().getUsers(user);
        } catch (SQLException e){
            new ErrorHandler().printToConsoleAddLog(e);
        }
        
        return 
    }
}