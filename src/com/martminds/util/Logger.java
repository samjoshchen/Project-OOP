package com.martminds.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {
    private static final String LOG_FILE = "data/system-log.txt";
    private static boolean enableFileLogging = false;
    
    public static void setFileLogging(boolean enable) {
        enableFileLogging = enable;
    }
    
    public static void info(String message) {
        log("INFO", message);
    }

    public static void warning(String message) {
        log("WARNING", message);
    }
    
    public static void error(String message) {
        log("ERROR", message);
    }
    
    public static void debug(String message) {
        log("DEBUG", message);
    }
    
    private static void log(String level, String message) {
        String timestamp = DateTimeUtil.getCurrentTimestamp();
        String logEntry = String.format("[%s] [%s] %s", timestamp, level, message);
        
        System.out.println(logEntry);
        
        if (enableFileLogging) {
            writeToFile(logEntry);
        }
    }
    
    private static void writeToFile(String logEntry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            writer.write(logEntry);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}
