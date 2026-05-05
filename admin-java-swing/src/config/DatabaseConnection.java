package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
1-downlod:https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.0.31/mysql-connector-j-8.0.31.jar
2-IntelliJ:

File → Project Structure → Libraries → + → Add JAR
 */
public class DatabaseConnection {
    //herer we use concespt singleton in owner app we have just one cycle life to access in db use instance
    private static DatabaseConnection instance;
    private Connection connection;

    // DB CONFIG
    private static final String URL =
            "jdbc:mysql://localhost:3306/GPEHEI_LEGACY";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // change if needed

    private DatabaseConnection() {
        try {
            // Load driver (optional for newer JDBC but safe)
            Class.forName("com.mysql.cj.jdbc.Driver");

            this.connection = DriverManager.getConnection(
                    URL, USER, PASSWORD
            );

            System.out.println("[DB] Connected to GPEHEI_LEGACY successfully.");

        } catch (ClassNotFoundException e) {
            System.err.println("[DB ERROR] MySQL Driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Connection failed");
            e.printStackTrace();
        }
    }

    // Singleton access
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // Get connection
    public Connection getConnection() {
        return connection;
    }

    // Optional: close connection safely
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DB] Connection closed.");
            } catch (SQLException e) {
                System.err.println("[DB ERROR] Failed to close connection");
                e.printStackTrace();
            }
        }
    }
}