package web.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {
    private final String driverClassName;
    private final String url;
    private final String username;
    private final String password;

    public ConnectionFactory(String driverClassName, String url, String username, String password) {
        this.driverClassName = driverClassName;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Connection openConnection() throws SQLException {
        loadDriverIfConfigured();
        if (username == null || username.trim().isEmpty()) {
            return DriverManager.getConnection(url);
        }
        return DriverManager.getConnection(url, username, password == null ? "" : password);
    }

    private void loadDriverIfConfigured() {
        if (driverClassName == null || driverClassName.trim().isEmpty()) {
            return;
        }
        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            throw new RepositoryException("JDBC driver class was not found: " + driverClassName, e);
        }
    }
}
