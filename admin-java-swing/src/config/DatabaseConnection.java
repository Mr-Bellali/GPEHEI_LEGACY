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
    //in windows
    //private static final String USER = "root";
    //private static final String PASSWORD = ""; // change if needed
    //in ubuntu check buttom script
    private static final String USER = "javauser";
    private static final String PASSWORD = "1234";



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
        try {
            // Check if connection is null or closed
            if (connection == null || connection.isClosed()) {
                System.out.println("[DB] Connection was closed, reconnecting...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Reconnected successfully.");
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Failed to reconnect");
            e.printStackTrace();
        }
        return connection;
    }

    // Optional: close connection safely
    public void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("[DB] Connection closed.");
                }
            } catch (SQLException e) {
                System.err.println("[DB ERROR] Failed to close connection");
                e.printStackTrace();
            }
        }
    }

    // Check if connection is alive
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}

/* if want to use ubuntu
1-install mysql use command
sudo apt update
sudo apt install mysql-server -y
2-strat service use command to start and enabled or stop if finish work
sudo systemctl start mysql
sudo systemctl enable mysql
3- to check if all good use status to know
sudo systemctl status mysql
4- to login in mysql use command
sudo mysql
after u can use lang sql normal like

create database GPEHEI_LEGACY;....
after finish use command
exit;
----------------------------
now if want to connect ur project java with mysql important to creat user for java to get permistion
step 1
check if mysql run
sudo systemctl status mysql
if not run
after now create user login sql
sudo mysql;

use this create :

CREATE USER 'javauser'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON GPEHEI_LEGACY.* TO 'javauser'@'localhost';
FLUSH PRIVILEGES;
EXIT;
 */