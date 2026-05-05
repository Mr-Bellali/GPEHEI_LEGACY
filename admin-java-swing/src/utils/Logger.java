package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static String format(String level, String message) {
        return String.format("[%s] [%s] %s",
                LocalDateTime.now().format(FORMATTER),
                level,
                message);
    }

    public static void info(String message) {
        System.out.println(format("INFO", message));
    }

    public static void warn(String message) {
        System.out.println(format("WARN", message));
    }

    public static void error(String message) {
        System.err.println(format("ERROR", message));
    }

    public static void error(String message, Exception e) {
        System.err.println(format("ERROR", message));
        e.printStackTrace();
    }
}

/*
In DAO
Logger.error("Failed to fetch student by ID", e);
In Service
Logger.warn("Login attempt failed for email: " + email);
In Controller
Logger.info("Admin logged in successfully");
 */