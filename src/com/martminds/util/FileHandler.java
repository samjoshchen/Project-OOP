package com.martminds.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String DATA_DIR = "data/";

    public static void ensureDataDirectoryExists() {
        File directory = new File(DATA_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static List<String> readFile(String filename) {
        List<String> lines = new ArrayList<>();
        String filepath = DATA_DIR + filename;

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (FileNotFoundException e) {
            Logger.info("File not found: " + filename + " (will be created on first write)");
        } catch (IOException e) {
            Logger.error("Error reading file: " + filename + " - " + e.getMessage());
        }

        return lines;
    }

    public static boolean writeFile(String filename, List<String> lines) {
        ensureDataDirectoryExists();
        String filepath = DATA_DIR + filename;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            Logger.error("Error writing file: " + filename + " - " + e.getMessage());
            return false;
        }
    }

    public static boolean appendToFile(String filename, String line) {
        ensureDataDirectoryExists();
        String filepath = DATA_DIR + filename;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, true))) {
            writer.write(line);
            writer.newLine();
            return true;
        } catch (IOException e) {
            Logger.error("Error appending to file: " + filename + " - " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteFile(String filename) {
        String filepath = DATA_DIR + filename;
        File file = new File(filepath);

        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static boolean fileExists(String filename) {
        String filepath = DATA_DIR + filename;
        File file = new File(filepath);
        return file.exists();
    }

    public static String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString().trim());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }

        fields.add(currentField.toString().trim());

        return fields.toArray(new String[0]);
    }

    public static String formatCSVLine(String... fields) {
        StringBuilder line = new StringBuilder();

        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];

            if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
                field = "\"" + field.replace("\"", "\"\"") + "\"";
            }

            line.append(field);
            if (i < fields.length - 1) {
                line.append(",");
            }
        }

        return line.toString();
    }

    public static boolean clearFile(String filename) {
        return writeFile(filename, new ArrayList<>());
    }

    public static boolean backupFile(String filename) {
        String filepath = DATA_DIR + filename;
        String backupPath = DATA_DIR + filename + ".bak";

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath));
                BufferedWriter writer = new BufferedWriter(new FileWriter(backupPath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            Logger.error("Error backing up file: " + filename + " - " + e.getMessage());
            return false;
        }
    }

    public static boolean restoreFromBackup(String filename) {
        String filepath = DATA_DIR + filename;
        String backupPath = DATA_DIR + filename + ".bak";

        File backupFile = new File(backupPath);
        if (!backupFile.exists()) {
            Logger.error("Backup file not found: " + filename + ".bak");
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(backupPath));
                BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {

            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            Logger.error("Error restoring from backup: " + filename + " - " + e.getMessage());
            return false;
        }
    }

    public static String readLine(String filename, int lineNumber) {
        String filepath = DATA_DIR + filename;

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            int currentLine = 0;

            while ((line = reader.readLine()) != null) {
                currentLine++;
                if (currentLine == lineNumber) {
                    return line;
                }
            }
        } catch (IOException e) {
            Logger.error("Error reading line from file: " + filename + " - " + e.getMessage());
        }

        return null;
    }

    public static int countLines(String filename) {
        String filepath = DATA_DIR + filename;
        int count = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            while (reader.readLine() != null) {
                count++;
            }
        } catch (IOException e) {
            Logger.error("Error counting lines in file: " + filename + " - " + e.getMessage());
        }

        return count;
    }

    public static List<String> searchInFile(String filename, String searchText) {
        List<String> matchingLines = new ArrayList<>();
        String filepath = DATA_DIR + filename;

        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(searchText)) {
                    matchingLines.add(line);
                }
            }
        } catch (IOException e) {
            Logger.error("Error searching in file: " + filename + " - " + e.getMessage());
        }

        return matchingLines;
    }
}
