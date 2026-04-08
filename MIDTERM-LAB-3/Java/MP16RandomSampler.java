import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * MP16 - Random dataset sampler.
 * This program asks for the dataset path first, then sample size,
 * and prints random rows without replacement in a formatted table.
 */
public class MP16RandomSampler {

    // Dataset container with header and parsed rows.
    private static class Dataset {
        List<String> header;
        List<List<String>> rows;

        Dataset(List<String> header, List<List<String>> rows) {
            this.header = header;
            this.rows = rows;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Required order: dataset path first.
            System.out.print("Enter CSV dataset file path: ");
            String datasetPath = scanner.nextLine().trim();
            if (datasetPath.isEmpty()) {
                System.out.println("Dataset path is required.");
                return;
            }

            System.out.print("Enter sample size: ");
            String sampleInput = scanner.nextLine().trim();
            int requestedSample = Integer.parseInt(sampleInput);
            if (requestedSample <= 0) {
                System.out.println("Sample size must be greater than 0.");
                return;
            }

            Dataset dataset = loadDataset(datasetPath);
            if (dataset.rows.isEmpty()) {
                System.out.println("No valid data rows were found in the dataset.");
                return;
            }

            List<List<String>> sampledRows = getRandomSample(dataset.rows, requestedSample);
            printSampleTable(dataset.header, sampledRows);

            System.out.println();
            System.out.println("Requested sample size: " + requestedSample);
            System.out.println("Actual sample size   : " + sampledRows.size());
            System.out.println("Total data rows      : " + dataset.rows.size());
        } catch (NumberFormatException ex) {
            System.out.println("Invalid sample size. Please enter a whole number.");
        } catch (IOException ex) {
            System.out.println("File processing error: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Unexpected error: " + ex.getMessage());
        } finally {
            scanner.close();
        }
    }

    // Loads data after detecting the actual CSV header row.
    private static Dataset loadDataset(String datasetPath) throws IOException {
        List<String> header = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();
        boolean headerFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(datasetPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<String> parsed = parseCsvLine(line);

                if (!headerFound) {
                    if (!parsed.isEmpty() && "Candidate".equalsIgnoreCase(parsed.get(0).trim())) {
                        header = new ArrayList<>(parsed);
                        headerFound = true;
                    }
                    continue;
                }

                if (isEmptyRow(parsed)) {
                    continue;
                }

                rows.add(normalizeRowSize(parsed, header.size()));
            }
        }

        if (!headerFound) {
            throw new IOException("Header row not found. Expected first column to be 'Candidate'.");
        }

        return new Dataset(header, rows);
    }

    // Returns a random sample without replacement.
    private static List<List<String>> getRandomSample(List<List<String>> rows, int requestedSample) {
        List<List<String>> shuffled = new ArrayList<>(rows);
        Collections.shuffle(shuffled, new Random());
        int actualSampleSize = Math.min(requestedSample, shuffled.size());
        return new ArrayList<>(shuffled.subList(0, actualSampleSize));
    }

    // Prints selected rows with key columns in aligned format.
    private static void printSampleTable(List<String> header, List<List<String>> rows) {
        int candidateIdx = header.indexOf("Candidate");
        int examIdx = header.indexOf("Exam");
        int scoreIdx = header.indexOf("Score");
        int resultIdx = header.indexOf("Result");
        int timeIdx = header.indexOf("Time Used");

        // Keep fixed widths so table stays readable on narrow terminals.
        int noWidth = 2;
        int candidateWidth = 15;
        int examWidth = 22;
        int scoreWidth = 5;
        int resultWidth = 4;
        int timeWidth = 7;

        String format = "%-" + noWidth + "s | %%-" + candidateWidth + "s | %%-" + examWidth
            + "s | %%-" + scoreWidth + "s | %%-" + resultWidth + "s | %%-" + timeWidth + "s%n";
        format = format.replace("%%", "%");

        int dividerLength = noWidth + candidateWidth + examWidth + scoreWidth + resultWidth + timeWidth + (5 * 3);
        String divider = "-".repeat(dividerLength);

        System.out.println();
        System.out.println("MP16 RANDOM SAMPLE");
        System.out.println(divider);
        System.out.printf(format, "No", "Candidate", "Exam", "Score", "Res", "Time");
        System.out.println(divider);

        for (int i = 0; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            String candidate = truncate(getCell(row, candidateIdx), candidateWidth);
            String exam = truncate(getCell(row, examIdx), examWidth);
            String score = truncate(getCell(row, scoreIdx), scoreWidth);
            String result = truncate(getCell(row, resultIdx), resultWidth);
            String timeUsed = truncate(compactTime(getCell(row, timeIdx)), timeWidth);

            System.out.printf(format,
                String.valueOf(i + 1), candidate, exam, score, result, timeUsed);
        }

        System.out.println(divider);
    }

    // Safely fetches a value from a row.
    private static String getCell(List<String> row, int index) {
        if (index < 0 || index >= row.size()) {
            return "";
        }
        return row.get(index).trim();
    }

    // Limits text length for compact table display.
    private static String truncate(String value, int maxLen) {
        if (value.length() <= maxLen) {
            return value;
        }
        if (maxLen <= 3) {
            return value.substring(0, maxLen);
        }
        return value.substring(0, maxLen - 3) + "...";
    }

    // Converts "48 min 37 sec" to "48m37s" to reduce column width.
    private static String compactTime(String value) {
        String[] parts = value.trim().split("\\s+");
        if (parts.length >= 4 && "min".equalsIgnoreCase(parts[1]) && "sec".equalsIgnoreCase(parts[3])) {
            return parts[0] + "m" + parts[2] + "s";
        }
        return value;
    }

    // CSV parsing that handles commas inside quoted fields.
    private static List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        result.add(current.toString());
        return result;
    }

    // Checks whether a parsed row has any meaningful content.
    private static boolean isEmptyRow(List<String> row) {
        for (String value : row) {
            if (!value.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // Ensures row width matches header width.
    private static List<String> normalizeRowSize(List<String> row, int targetSize) {
        List<String> normalized = new ArrayList<>(row);
        while (normalized.size() < targetSize) {
            normalized.add("");
        }
        if (normalized.size() > targetSize) {
            normalized = new ArrayList<>(normalized.subList(0, targetSize));
        }
        return normalized;
    }
}
