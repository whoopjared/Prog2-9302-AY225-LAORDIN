import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * MP17 - Find longest text entry.
 * This program asks for dataset path first, parses the CSV,
 * and finds the longest non-empty text field in all data rows.
 */
public class MP17LongestTextEntry {

    // Parsed dataset container.
    private static class Dataset {
        List<String> header;
        List<List<String>> rows;

        Dataset(List<String> header, List<List<String>> rows) {
            this.header = header;
            this.rows = rows;
        }
    }

    // Holds details about the longest text match.
    private static class LongestEntry {
        int rowNumber;
        String columnName;
        String value;
        int length;
        List<String> sourceRow;

        LongestEntry(int rowNumber, String columnName, String value, int length, List<String> sourceRow) {
            this.rowNumber = rowNumber;
            this.columnName = columnName;
            this.value = value;
            this.length = length;
            this.sourceRow = sourceRow;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Required order: ask dataset path first.
            System.out.print("Enter CSV dataset file path: ");
            String datasetPath = scanner.nextLine().trim();
            if (datasetPath.isEmpty()) {
                System.out.println("Dataset path is required.");
                return;
            }

            Dataset dataset = loadDataset(datasetPath);
            if (dataset.rows.isEmpty()) {
                System.out.println("No valid data rows were found in the dataset.");
                return;
            }

            LongestEntry longest = findLongestTextEntry(dataset);
            printLongestEntryResult(dataset.header, longest);
        } catch (IOException ex) {
            System.out.println("File processing error: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Unexpected error: " + ex.getMessage());
        } finally {
            scanner.close();
        }
    }

    // Reads the CSV and stores rows after detecting the true header row.
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

    // Finds the longest non-empty text among all columns and rows.
    private static LongestEntry findLongestTextEntry(Dataset dataset) {
        LongestEntry best = null;

        for (int rowIndex = 0; rowIndex < dataset.rows.size(); rowIndex++) {
            List<String> row = dataset.rows.get(rowIndex);

            for (int colIndex = 0; colIndex < dataset.header.size() && colIndex < row.size(); colIndex++) {
                String value = row.get(colIndex).trim();
                if (value.isEmpty()) {
                    continue;
                }

                if (best == null || value.length() > best.length) {
                    best = new LongestEntry(
                            rowIndex + 1,
                            dataset.header.get(colIndex),
                            value,
                            value.length(),
                            row
                    );
                }
            }
        }

        return best;
    }

    // Prints the longest entry details in a readable format.
    private static void printLongestEntryResult(List<String> header, LongestEntry entry) {
        if (entry == null) {
            System.out.println("No text entry found in dataset rows.");
            return;
        }

        int candidateIdx = header.indexOf("Candidate");
        int examIdx = header.indexOf("Exam");
        String candidate = getCell(entry.sourceRow, candidateIdx);
        String exam = getCell(entry.sourceRow, examIdx);

        System.out.println();
        System.out.println("MP17 LONGEST TEXT ENTRY");
        System.out.println("-----------------------");
        System.out.println("Data row number : " + entry.rowNumber);
        System.out.println("Column name     : " + entry.columnName);
        System.out.println("Text length     : " + entry.length);
        System.out.println("Text value      : " + entry.value);
        System.out.println("Candidate       : " + candidate);
        System.out.println("Exam            : " + exam);
    }

    // Retrieves a value safely from a row.
    private static String getCell(List<String> row, int index) {
        if (index < 0 || index >= row.size()) {
            return "";
        }
        return row.get(index).trim();
    }

    // CSV parsing with quote awareness.
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

    // Checks if all values in a row are empty.
    private static boolean isEmptyRow(List<String> row) {
        for (String value : row) {
            if (!value.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // Aligns row width with header width.
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
