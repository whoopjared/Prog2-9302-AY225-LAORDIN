import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * DataCleaningReport.java
 * Data Cleaning and Validation Report
 * 
 * PROGRAMMING 2 - MACHINE PROBLEM
 * University of Perpetual Help System DALTA - Molino Campus
 * BS Information Technology - Game Development
 * 
 * Task: Data Cleaning and Validation Report (LAORDIN)
 * 
 * Scenario: You are assigned to audit dataset quality.
 * Requirements:
 * - Detect Missing values
 * - Detect Negative sales
 * - Detect Invalid dates
 * - Detect Duplicate records
 * - Display data quality report
 */
public class DataCleaningReportJared {

    // Storage for all loaded records
    private static List<DataRecordJared> records = new ArrayList<>();
    // Storage for the header columns
    private static String[] headers;
    // Display mode chosen by user (1: 10, 2: half, 3: all)
    private static int displayMode = 1;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        File file;

        // ===== STEP 1: Welcome message and prompt for file path =====
        System.out.println();
        System.out.println("+======================================================================+");
        System.out.println("|                                                                      |");
        System.out.println("|   [#]  DATA CLEANING AND VALIDATION REPORT TOOL                      |");
        System.out.println("|   [*]  Programming 2 - Machine Problem (LAORDIN)                     |");
        System.out.println("|   [>]  University of Perpetual Help System DALTA                     |");
        System.out.println("|                                                                      |");
        System.out.println("+======================================================================+");
        System.out.println();
        System.out.println("  [?] This program analyzes a CSV dataset for data quality issues.");
        System.out.println();
        System.out.println("  [i] Checks performed:");
        System.out.println("      (1) Missing Values     (2) Negative Sales");
        System.out.println("      (3) Invalid Dates      (4) Duplicate Records");
        System.out.println();
        System.out.println("  [>] Please provide the full file path to the CSV dataset.");
        System.out.println("  [>] Example: C:\\Users\\Laordin\\Downloads\\vgchartz-2024.csv");
        System.out.println("  " + "-".repeat(66));

        while (true) {
            System.out.println();
            System.out.print("Enter dataset file path: ");
            String path = input.nextLine().trim();
            file = new File(path);

            if (file.exists() && file.isFile()) {
                if (path.toLowerCase().endsWith(".csv")) {
                    System.out.println();
                    System.out.println("  [OK] File found! Processing...");
                    break;
                } else {
                    System.out.println("  [X] Error: File is not in CSV format. Please try again.");
                }
            } else {
                System.out.println("  [X] Invalid file path. Please try again.");
            }
        }

        // ===== STEP 2: Load dataset into memory =====
        try {
            loadDataset(file);
        } catch (IOException e) {
            System.out.println();
            System.out.println("  [X] Error reading file: " + e.getMessage());
            input.close();
            return;
        }

        System.out.println("  [OK] Dataset loaded successfully!");
        System.out.println("  [i] Total records loaded: " + records.size());

        System.out.println();
        System.out.println("  [?] How many error samples would you like to display per category?");
        System.out.println("      (1) Up to 10 samples (Recommended)");
        System.out.println("      (2) Half of the errors");
        System.out.println("      (3) All errors");
        while (true) {
            System.out.print("  [>] Enter your choice (1-3): ");
            String cStr = input.nextLine().trim();
            if (cStr.equals("1") || cStr.equals("2") || cStr.equals("3")) {
                displayMode = Integer.parseInt(cStr);
                break;
            }
            System.out.println("  [X] Invalid choice. Please enter 1, 2, or 3.");
        }

        // ===== STEP 3: Perform data cleaning and validation =====
        System.out.println();
        System.out.println("+======================================================================+");
        System.out.println("|          DATA CLEANING AND VALIDATION REPORT                         |");
        System.out.println("+======================================================================+");

        // 3a. Detect Missing Values
        detectMissingValues();
        pause(input);

        // 3b. Detect Negative Sales
        detectNegativeSales();
        pause(input);

        // 3c. Detect Invalid Dates
        detectInvalidDates();
        pause(input);

        // 3d. Detect Duplicate Records
        detectDuplicateRecords();
        pause(input);

        // 3e. Display Summary
        displaySummary();

        input.close();
    }

    /**
     * Loads the CSV dataset into a list of DataRecord objects.
     * Uses BufferedReader for efficient file reading.
     * Handles quoted fields that may contain commas.
     */
    private static void loadDataset(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        int lineNumber = 0;

        while ((line = br.readLine()) != null) {
            lineNumber++;
            String[] fields = parseCSVLine(line);

            if (lineNumber == 1) {
                // First line is the header row
                headers = fields;
                continue;
            }

            records.add(new DataRecordJared(lineNumber, fields));
        }

        br.close();
    }

    /**
     * Parses a single CSV line, properly handling quoted fields
     * that may contain commas.
     */
    private static String[] parseCSVLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    /**
     * Detects records with missing values (empty or blank fields).
     * Checks important columns: title, console, genre, publisher,
     * developer, critic_score, total_sales, na_sales, jp_sales,
     * pal_sales, other_sales, release_date.
     */
    private static void detectMissingValues() {
        System.out.println();
        System.out.println("  +--------------------------------------------------------------------+");
        System.out.println("  |  (1) MISSING VALUES REPORT                                         |");
        System.out.println("  +--------------------------------------------------------------------+");

        // Column names to check (excluding img and last_update as they are less
        // critical)
        String[] columnsToCheck = { "title", "console", "genre", "publisher", "developer",
                "critic_score", "total_sales", "na_sales", "jp_sales",
                "pal_sales", "other_sales", "release_date" };

        // Track missing counts per column
        Map<String, Integer> missingCounts = new HashMap<>();
        for (String col : columnsToCheck) {
            missingCounts.put(col, 0);
        }

        int totalMissingRecords = 0;

        for (DataRecordJared record : records) {
            boolean hasMissing = false;

            if (isEmpty(record.getTitle())) {
                missingCounts.merge("title", 1, Integer::sum);
                hasMissing = true;
            }
            if (isEmpty(record.getConsole())) {
                missingCounts.merge("console", 1, Integer::sum);
                hasMissing = true;
            }
            if (isEmpty(record.getGenre())) {
                missingCounts.merge("genre", 1, Integer::sum);
                hasMissing = true;
            }
            if (isEmpty(record.getPublisher())) {
                missingCounts.merge("publisher", 1, Integer::sum);
                hasMissing = true;
            }
            if (isEmpty(record.getDeveloper())) {
                missingCounts.merge("developer", 1, Integer::sum);
                hasMissing = true;
            }
            if (isEmpty(record.getCriticScore())) {
                missingCounts.merge("critic_score", 1, Integer::sum);
                hasMissing = true;
            }
            if (isEmpty(record.getTotalSales())) {
                missingCounts.merge("total_sales", 1, Integer::sum);
                hasMissing = true;
            }
            if (isEmpty(record.getNaSales())) {
                missingCounts.merge("na_sales", 1, Integer::sum);
                hasMissing = true;
            }
            if (isEmpty(record.getJpSales())) {
                missingCounts.merge("jp_sales", 1, Integer::sum);
                hasMissing = true;
            }
            if (isEmpty(record.getPalSales())) {
                missingCounts.merge("pal_sales", 1, Integer::sum);
                hasMissing = true;
            }
            if (isEmpty(record.getOtherSales())) {
                missingCounts.merge("other_sales", 1, Integer::sum);
                hasMissing = true;
            }
            if (isEmpty(record.getReleaseDate())) {
                missingCounts.merge("release_date", 1, Integer::sum);
                hasMissing = true;
            }

            if (hasMissing) {
                totalMissingRecords++;
            }
        }

        System.out.printf("  %-20s | %-15s | %-10s%n", "Column", "Missing Count", "% Missing");
        System.out.println("  " + "-".repeat(50));
        for (String col : columnsToCheck) {
            int count = missingCounts.get(col);
            double pct = (double) count / records.size() * 100;
            System.out.printf("  %-20s | %-15d | %.2f%%%n", col, count, pct);
        }
        System.out.println();
        System.out.println("  >> Total records with at least one missing value: " + totalMissingRecords);
        System.out.printf("  >> Percentage of records affected: %.2f%%%n",
                (double) totalMissingRecords / records.size() * 100);
    }

    /**
     * Detects records where any sales column has a negative value.
     * Checks: total_sales, na_sales, jp_sales, pal_sales, other_sales.
     */
    private static void detectNegativeSales() {
        System.out.println();
        System.out.println("  +--------------------------------------------------------------------+");
        System.out.println("  |  (2) NEGATIVE SALES REPORT                                         |");
        System.out.println("  +--------------------------------------------------------------------+");

        int negativeSalesCount = 0;
        List<String> negativeSamplesInfo = new ArrayList<>();

        for (DataRecordJared record : records) {
            boolean hasNegative = false;
            StringBuilder negCols = new StringBuilder();

            hasNegative |= checkNegativeSale(record.getTotalSales(), "total_sales", negCols);
            hasNegative |= checkNegativeSale(record.getNaSales(), "na_sales", negCols);
            hasNegative |= checkNegativeSale(record.getJpSales(), "jp_sales", negCols);
            hasNegative |= checkNegativeSale(record.getPalSales(), "pal_sales", negCols);
            hasNegative |= checkNegativeSale(record.getOtherSales(), "other_sales", negCols);

            if (hasNegative) {
                negativeSalesCount++;
                negativeSamplesInfo.add(String.format("  Row %-6d | %-40s | Negative in: %s",
                        record.getRowNumber(), truncate(record.getTitle(), 40), negCols.toString()));
            }
        }

        if (negativeSalesCount == 0) {
            System.out.println("  [OK] No negative sales values detected. All clear!");
        } else {
            System.out.println("  [!] Total records with negative sales: " + negativeSalesCount);
            System.out.println();

            int limit = displayMode == 1 ? 10
                    : (displayMode == 2 ? Math.max(1, negativeSamplesInfo.size() / 2) : negativeSamplesInfo.size());
            limit = Math.min(limit, negativeSamplesInfo.size());
            String modeStr = displayMode == 1 ? "up to 10" : (displayMode == 2 ? "half" : "all");

            System.out.println("  Records with negative sales (" + modeStr + " - displaying " + limit + "):");
            System.out.printf("  %-10s | %-40s | %s%n", "Row", "Title", "Affected Columns");
            System.out.println("  " + "-".repeat(65));
            for (int i = 0; i < limit; i++) {
                System.out.println(negativeSamplesInfo.get(i));
            }
        }
    }

    /**
     * Checks if a sale value is negative. Appends column name if negative.
     */
    private static boolean checkNegativeSale(String value, String colName, StringBuilder negCols) {
        if (!isEmpty(value)) {
            try {
                double val = Double.parseDouble(value);
                if (val < 0) {
                    if (negCols.length() > 0)
                        negCols.append(", ");
                    negCols.append(colName);
                    return true;
                }
            } catch (NumberFormatException e) {
                // Not a valid number
            }
        }
        return false;
    }

    /**
     * Detects records with invalid date formats.
     * Expected format: YYYY-MM-DD
     * Checks both release_date and last_update columns.
     */
    private static void detectInvalidDates() {
        System.out.println();
        System.out.println("  +--------------------------------------------------------------------+");
        System.out.println("  |  (3) INVALID DATES REPORT                                          |");
        System.out.println("  +--------------------------------------------------------------------+");

        int invalidReleaseDateCount = 0;
        int invalidLastUpdateCount = 0;
        List<String> invalidDateSamples = new ArrayList<>();

        for (DataRecordJared record : records) {
            boolean releaseDateInvalid = false;
            boolean lastUpdateInvalid = false;

            // Only validate non-empty dates
            if (!isEmpty(record.getReleaseDate()) && !isValidDate(record.getReleaseDate())) {
                invalidReleaseDateCount++;
                releaseDateInvalid = true;
            }

            if (!isEmpty(record.getLastUpdate()) && !isValidDate(record.getLastUpdate())) {
                invalidLastUpdateCount++;
                lastUpdateInvalid = true;
            }

            if (releaseDateInvalid || lastUpdateInvalid) {
                String issue = releaseDateInvalid ? "release_date=" + record.getReleaseDate() : "";
                if (lastUpdateInvalid) {
                    if (!issue.isEmpty())
                        issue += ", ";
                    issue += "last_update=" + record.getLastUpdate();
                }
                invalidDateSamples.add(String.format("  Row %-6d | %-40s | %s",
                        record.getRowNumber(), truncate(record.getTitle(), 40), issue));
            }
        }

        System.out.printf("  %-20s | %-15s%n", "Column", "Invalid Count");
        System.out.println("  " + "-".repeat(40));
        System.out.printf("  %-20s | %-15d%n", "release_date", invalidReleaseDateCount);
        System.out.printf("  %-20s | %-15d%n", "last_update", invalidLastUpdateCount);

        if (!invalidDateSamples.isEmpty()) {
            System.out.println();

            int limit = displayMode == 1 ? 10
                    : (displayMode == 2 ? Math.max(1, invalidDateSamples.size() / 2) : invalidDateSamples.size());
            limit = Math.min(limit, invalidDateSamples.size());
            String modeStr = displayMode == 1 ? "up to 10" : (displayMode == 2 ? "half" : "all");

            System.out.println("  Invalid date records (" + modeStr + " - displaying " + limit + "):");
            System.out.printf("  %-10s | %-40s | %s%n", "Row", "Title", "Invalid Date(s)");
            System.out.println("  " + "-".repeat(65));
            for (int i = 0; i < limit; i++) {
                System.out.println(invalidDateSamples.get(i));
            }
        }
    }

    /**
     * Validates a date string in the expected format YYYY-MM-DD.
     * Checks that year, month, and day are within valid ranges.
     */
    private static boolean isValidDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty())
            return false;

        // Expected format: YYYY-MM-DD
        String[] parts = dateStr.trim().split("-");
        if (parts.length != 3)
            return false;

        try {
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            if (year < 1900 || year > 2025)
                return false;
            if (month < 1 || month > 12)
                return false;
            if (day < 1 || day > 31)
                return false;

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Detects duplicate records based on title + console + release_date
     * combination.
     */
    private static void detectDuplicateRecords() {
        System.out.println();
        System.out.println("  +--------------------------------------------------------------------+");
        System.out.println("  |  (4) DUPLICATE RECORDS REPORT                                      |");
        System.out.println("  +--------------------------------------------------------------------+");

        Map<String, List<DataRecordJared>> keyMap = new HashMap<>();

        for (DataRecordJared record : records) {
            String key = record.getDuplicateKey();
            keyMap.computeIfAbsent(key, k -> new ArrayList<>()).add(record);
        }

        int duplicateGroupCount = 0;
        int totalDuplicateRecords = 0;
        List<String> duplicateSamples = new ArrayList<>();

        for (Map.Entry<String, List<DataRecordJared>> entry : keyMap.entrySet()) {
            List<DataRecordJared> group = entry.getValue();
            if (group.size() > 1) {
                duplicateGroupCount++;
                totalDuplicateRecords += group.size();

                DataRecordJared first = group.get(0);
                StringBuilder rows = new StringBuilder();
                for (DataRecordJared r : group) {
                    if (rows.length() > 0)
                        rows.append(", ");
                    rows.append(r.getRowNumber());
                }
                duplicateSamples.add(String.format("  %-40s | %-10s | %-12s | Rows: %s",
                        truncate(first.getTitle(), 40), first.getConsole(),
                        first.getReleaseDate(), rows.toString()));
            }
        }

        System.out.println("  Duplicate groups found: " + duplicateGroupCount);
        System.out.println("  Total duplicate records: " + totalDuplicateRecords);

        if (!duplicateSamples.isEmpty()) {
            System.out.println();

            int limit = displayMode == 1 ? 10
                    : (displayMode == 2 ? Math.max(1, duplicateSamples.size() / 2) : duplicateSamples.size());
            limit = Math.min(limit, duplicateSamples.size());
            String modeStr = displayMode == 1 ? "up to 10" : (displayMode == 2 ? "half" : "all");

            System.out.println("  Duplicate groups (" + modeStr + " - displaying " + limit + "):");
            System.out.printf("  %-40s | %-10s | %-12s | %s%n", "Title", "Console", "Release Date", "Row Numbers");
            System.out.println("  " + "-".repeat(80));
            for (int i = 0; i < limit; i++) {
                System.out.println(duplicateSamples.get(i));
            }
        }
    }

    /**
     * Displays a final summary of the entire data quality report.
     */
    private static void displaySummary() {
        System.out.println();
        System.out.println("+======================================================================+");
        System.out.println("|          DATA QUALITY SUMMARY                                        |");
        System.out.println("+======================================================================+");
        System.out.println("  [i] Total records analyzed: " + records.size());

        // Recount for summary
        int missingCount = 0;
        int negativeCount = 0;
        int invalidDateCount = 0;

        Map<String, List<DataRecordJared>> keyMap = new HashMap<>();

        for (DataRecordJared record : records) {
            // Missing values
            if (isEmpty(record.getTitle()) || isEmpty(record.getConsole()) ||
                    isEmpty(record.getGenre()) || isEmpty(record.getPublisher()) ||
                    isEmpty(record.getDeveloper()) || isEmpty(record.getCriticScore()) ||
                    isEmpty(record.getTotalSales()) || isEmpty(record.getNaSales()) ||
                    isEmpty(record.getJpSales()) || isEmpty(record.getPalSales()) ||
                    isEmpty(record.getOtherSales()) || isEmpty(record.getReleaseDate())) {
                missingCount++;
            }

            // Negative sales
            if (isNegative(record.getTotalSales()) || isNegative(record.getNaSales()) ||
                    isNegative(record.getJpSales()) || isNegative(record.getPalSales()) ||
                    isNegative(record.getOtherSales())) {
                negativeCount++;
            }

            // Invalid dates
            if ((!isEmpty(record.getReleaseDate()) && !isValidDate(record.getReleaseDate())) ||
                    (!isEmpty(record.getLastUpdate()) && !isValidDate(record.getLastUpdate()))) {
                invalidDateCount++;
            }

            // Duplicates
            String key = record.getDuplicateKey();
            keyMap.computeIfAbsent(key, k -> new ArrayList<>()).add(record);
        }

        int duplicateGroups = 0;
        for (List<DataRecordJared> group : keyMap.values()) {
            if (group.size() > 1)
                duplicateGroups++;
        }

        System.out.println();
        System.out.printf("  [!] %-35s : %d%n", "Records with missing values", missingCount);
        System.out.printf("  [$] %-35s : %d%n", "Records with negative sales", negativeCount);
        System.out.printf("  [~] %-35s : %d%n", "Records with invalid dates", invalidDateCount);
        System.out.printf("  [=] %-35s : %d%n", "Duplicate record groups", duplicateGroups);
        System.out.println();

        // Overall quality score
        int totalIssues = missingCount + negativeCount + invalidDateCount + duplicateGroups;
        double qualityScore = (1.0 - ((double) totalIssues / (records.size() * 4))) * 100;
        qualityScore = Math.max(0, Math.min(100, qualityScore));

        String scoreLabel;
        if (qualityScore >= 90)
            scoreLabel = "[***] EXCELLENT";
        else if (qualityScore >= 70)
            scoreLabel = "[**]  GOOD";
        else if (qualityScore >= 50)
            scoreLabel = "[*]   FAIR";
        else
            scoreLabel = "[!]   POOR";

        System.out.printf("  %s - Overall Data Quality Score: %.2f%% %n", scoreLabel, qualityScore);
        System.out.println();
        System.out.println("+======================================================================+");
        System.out.println("|  [OK]  Report generated successfully!                                |");
        System.out.println("+======================================================================+");
        System.out.println();
    }

    // ===================== UTILITY METHODS =====================

    /**
     * Pauses the program flow until the user presses ENTER.
     */
    private static void pause(Scanner input) {
        System.out.println();
        System.out.print("  [>] Press [ENTER] to continue...");
        input.nextLine();
    }

    /**
     * Checks if a string value is empty or blank.
     */
    private static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Checks if a string value represents a negative number.
     */
    private static boolean isNegative(String value) {
        if (isEmpty(value))
            return false;
        try {
            return Double.parseDouble(value) < 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Truncates a string to a specified maximum length, adding "..." if truncated.
     */
    private static String truncate(String str, int maxLen) {
        if (str == null)
            return "";
        if (str.length() <= maxLen)
            return str;
        return str.substring(0, maxLen - 3) + "...";
    }
}
