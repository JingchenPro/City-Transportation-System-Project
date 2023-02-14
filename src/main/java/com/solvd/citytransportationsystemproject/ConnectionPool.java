package com.solvd.citytransportationsystemproject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.*;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectionPool {
    final static Logger logger = LogManager.getLogger(ARunner.class.getName());

    private static ConnectionPool instance;
    private static final int MAX_CONNECTIONS = 10;
    private LinkedBlockingQueue<java.sql.Connection> availableConnections;
    private String url;
    private String username;
    private String password;

    public ConnectionPool() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(
                    "C:\\Users\\House Games\\eclipse-workspace\\citytransportationsystemproject\\src\\main\\resources\\properties\\config.properties"));
        } catch (FileNotFoundException e) {
            logger.info(e);
        } catch (IOException e) {
            logger.info(e);
        }
        this.url = prop.getProperty("db.url");
        this.username = prop.getProperty("db.username");
        this.password = prop.getProperty("db.password");
        availableConnections = new LinkedBlockingQueue<>(MAX_CONNECTIONS);
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            try {
                availableConnections.add(DriverManager.getConnection(url, username, password));
            } catch (SQLException e) {
                logger.info(e);
            }
        }
    }

    public static ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    public java.sql.Connection getConnection() throws InterruptedException {
        return availableConnections.take();
    }

    public void releaseConnection(java.sql.Connection connection) throws InterruptedException {
        availableConnections.put(connection);
    }

}