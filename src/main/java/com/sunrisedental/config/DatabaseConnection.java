package com.sunrisedental.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton Pattern: DatabaseConnection
 * Provides a single, centralized, thread-safe access point for JDBC Database connections.
 * Configured by default for XAMPP MySQL (localhost:3306, user: root, password: "").
 */
public class DatabaseConnection {

    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    
    private static volatile DatabaseConnection instance;
    private static String jdbcUrl = "jdbc:mysql://localhost:3306/sunrise_dental_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String jdbcUser = "root";
    private static String jdbcPassword = "";
    private static String jdbcDriver = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            // Load MySQL Driver
            Class.forName(jdbcDriver);
            LOGGER.info("MySQL JDBC Driver successfully loaded.");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found in classpath!", e);
        }
    }

    /**
     * Private constructor to enforce Singleton design pattern.
     */
    private DatabaseConnection() {
        // Optional override from properties file if present
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                if (props.getProperty("db.url") != null) jdbcUrl = props.getProperty("db.url");
                if (props.getProperty("db.user") != null) jdbcUser = props.getProperty("db.user");
                if (props.getProperty("db.password") != null) jdbcPassword = props.getProperty("db.password");
            }
        } catch (Exception e) {
            LOGGER.info("Using default XAMPP MySQL configuration (root@localhost:3306/sunrise_dental_db).");
        }
    }

    /**
     * Double-checked locking Singleton accessor.
     * @return DatabaseConnection instance
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * Obtains a new active connection to the XAMPP MySQL database.
     * @return java.sql.Connection
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
    }

    /**
     * Allows custom connection configuration for testing or environment changes.
     */
    public static synchronized void configure(String url, String user, String password) {
        jdbcUrl = url;
        jdbcUser = user;
        jdbcPassword = password;
    }

    public static String getJdbcUrl() {
        return jdbcUrl;
    }
}
