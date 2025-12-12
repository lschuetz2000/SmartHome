package webserver.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import webserver.models.User;

public class DatabaseOperator{
    
    private Connection connection;

    public DatabaseOperator(Connection connection) throws SQLException{
        this.connection = connection;
    }

    public void createUser(User user) throws SQLException{
        PreparedStatement statement = connection.prepareStatement("INSERT INTO users (UserEmail, UserPassword) VALUES (?,?");
        statement.setString(1, user.getEmail());
        statement.setString(2, user.getPassword());
        statement.executeUpdate();
    }

    public void updateUser(User user) throws SQLException{
        PreparedStatement statement = connection.prepareStatement("UPDATE users SET UserEmail = ?, UserPassword = ? WHERE UserID = ?");
        statement.setString(1, user.getEmail());
        statement.setString(2, user.getPassword());
        statement.setString(3, user.getID());
        statement.executeUpdate();
    }

    public ResultSet getUsers(User user) throws SQLException{
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE UserID = '?' OR WHERE UserEmail = '?'");
        statement.setString(1, user.getEmail());
        return statement.executeQuery();
    }
}
