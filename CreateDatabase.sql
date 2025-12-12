CREATE DATABASE webserver;

USE webserver;

CREATE TABLE Users (UserID int AUTO_INCREMENT, UserEmail varchar(128), UserPassword varchar(256), PRIMARY KEY (UserID));

