import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * MP15 - Export first 50 rows to CSV.
 * This program asks for a dataset path first, reads and parses the CSV,
 * then exports the first 50 valid rows (plus header) to a new CSV file.
 */
public class MP15ExportFirst50 {

    // Simple container for header and data rows after parsing.
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
            // Required by instruction: ask for dataset path before any processing.
            System.out.print("Enter CSV dataset file path: ");
            String datasetPath = scanner.nextLine().trim();
            if (datasetPath.isEmpty()) {
                System.out.println("Dataset path is required.");
                return;
            }

            File datasetFile = new File(datasetPath).getCanonicalFile();
            // Auto-create output path inside MIDTERM-LAB-3/Java as requested.
            File datasetParent = datasetFile.getParentFile();
            File outputFile;
            if (datasetParent != null) {
                outputFile = new File(new File(datasetParent, "Java"), "mp15_first50_java_output.csv");
            } else {
                outputFile = new File("mp15_first50_java_output.csv");
            }
            String outputPath = outputFile.getCanonicalPath();

            // Guard against overwriting the original dataset file.
            if (datasetFile.equals(new File(outputPath).getCanonicalFile())) {
                System.out.println("Output file must be different from dataset file.");
                System.out.println("Please provide a new CSV file path for export.");
                return;
            }

            Dataset dataset = loadDataset(datasetPath);
            int exportCount = Math.min(50, dataset.rows.size());
            exportRows(dataset, outputPath, exportCount);

            // Formatted summary output for easier checking.
            System.out.println();
            System.out.println("MP15 RESULT");
            System.out.println("-----------");
            System.out.println("Total valid data rows: " + dataset.rows.size());
            System.out.println("Rows exported       : " + exportCount);
            System.out.println("Output file (auto)  : " + outputPath);
            System.out.println("Export completed successfully.");
        } catch (IOException ex) {
            System.out.println("File processing error: " + ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Unexpected error: " + ex.getMessage());
        } finally {
            scanner.close();
        }
    }

    // Loads CSV, skips metadata lines, finds the header row, and stores valid records.
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

    // Exports header + first N rows into a CSV file.
    private static void exportRows(Dataset dataset, String outputPath, int rowCount) throws IOException {
        File outputFile = new File(outputPath);
        File parent = outputFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writeCsvRow(writer, dataset.header);
            for (int i = 0; i < rowCount; i++) {
                writeCsvRow(writer, dataset.rows.get(i));
            }
        }
    }

    // Parses one CSV line with quote-aware logic to preserve commas inside quoted values.
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

    // Returns true if all fields are blank.
    private static boolean isEmptyRow(List<String> row) {
        for (String value : row) {
            if (!value.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // Pads or trims rows to the same number of columns as the header.
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

    // Writes one row to CSV with proper escaping.
    private static void writeCsvRow(BufferedWriter writer, List<String> row) throws IOException {
        for (int i = 0; i < row.size(); i++) {
            if (i > 0) {
                writer.write(',');
            }
            writer.write(escapeCsv(row.get(i)));
        }
        writer.newLine();
    }

    // Escapes a CSV value when it contains comma, quote, or line break.
    private static String escapeCsv(String value) {
        boolean needsQuotes = value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r");
        if (!needsQuotes) {
            return value;
        }
        return '"' + value.replace("\"", "\"\"") + '"';
    }
}
