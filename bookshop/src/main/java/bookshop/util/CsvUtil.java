package bookshop.util;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for reading and writing CSV files.
 */
public class CsvUtil {

    /**
     * Reads all non-blank, non-header lines from a CSV file.
     * Assumes first line is a header and skips it.
     */
    public static List<String> readLines(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) return lines;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                if (!line.isBlank()) lines.add(line);
            }
        }
        return lines;
    }

    /**
     * Overwrites a CSV file with the given header and rows.
     */
    public static void writeLines(String filePath, String header, List<String> rows) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            writer.write(header);
            writer.newLine();
            for (String row : rows) {
                writer.write(row);
                writer.newLine();
            }
        }
    }

    /**
     * Appends a single row to a CSV file.
     * Creates the file with header if it doesn't exist yet.
     */
    public static void appendLine(String filePath, String header, String row) throws IOException {
        File file = new File(filePath);
        boolean isNew = !file.exists() || file.length() == 0;
        file.getParentFile().mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (isNew) {
                writer.write(header);
                writer.newLine();
            }
            writer.write(row);
            writer.newLine();
        }
    }
}