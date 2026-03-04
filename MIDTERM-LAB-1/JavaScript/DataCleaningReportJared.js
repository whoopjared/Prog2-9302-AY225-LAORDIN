/**
 * DataCleaningReport.js
 * Data Cleaning and Validation Report (Node.js)
 *
 * PROGRAMMING 2 - MACHINE PROBLEM
 * University of Perpetual Help System DALTA - Molino Campus
 * BS Information Technology - Game Development
 *
 * Task: Data Cleaning and Validation Report (LAORDIN)
 *
 * Scenario: You are assigned to audit dataset quality.
 * Requirements:
 *   - Detect Missing values
 *   - Detect Negative sales
 *   - Detect Invalid dates
 *   - Detect Duplicate records
 *   - Display data quality report
 */

const fs = require('fs');
const readline = require('readline');

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

// ===================== DATA RECORD CLASS =====================

/**
 * Represents a single record (row) from the CSV dataset.
 */
class DataRecord {
    constructor(rowNumber, fields) {
        this.rowNumber = rowNumber;
        this.img = fields.length > 0 ? fields[0].trim() : "";
        this.title = fields.length > 1 ? fields[1].trim() : "";
        this.console = fields.length > 2 ? fields[2].trim() : "";
        this.genre = fields.length > 3 ? fields[3].trim() : "";
        this.publisher = fields.length > 4 ? fields[4].trim() : "";
        this.developer = fields.length > 5 ? fields[5].trim() : "";
        this.criticScore = fields.length > 6 ? fields[6].trim() : "";
        this.totalSales = fields.length > 7 ? fields[7].trim() : "";
        this.naSales = fields.length > 8 ? fields[8].trim() : "";
        this.jpSales = fields.length > 9 ? fields[9].trim() : "";
        this.palSales = fields.length > 10 ? fields[10].trim() : "";
        this.otherSales = fields.length > 11 ? fields[11].trim() : "";
        this.releaseDate = fields.length > 12 ? fields[12].trim() : "";
        this.lastUpdate = fields.length > 13 ? fields[13].trim() : "";
    }

    /**
     * Returns a unique key based on title, console, and release_date
     * for duplicate detection.
     */
    getDuplicateKey() {
        return `${this.title}|${this.console}|${this.releaseDate}`.toLowerCase();
    }
}

// ===================== UTILITY FUNCTIONS =====================

/**
 * Checks if a string value is empty or blank.
 */
function isEmpty(value) {
    return value === null || value === undefined || value.trim() === "";
}

/**
 * Checks if a string value represents a negative number.
 */
function isNegative(value) {
    if (isEmpty(value)) return false;
    const num = parseFloat(value);
    return !isNaN(num) && num < 0;
}

/**
 * Validates a date string in the expected format YYYY-MM-DD.
 * Checks that year, month, and day are within valid ranges.
 */
function isValidDate(dateStr) {
    if (!dateStr || dateStr.trim() === "") return false;

    const parts = dateStr.trim().split("-");
    if (parts.length !== 3) return false;

    const year = parseInt(parts[0], 10);
    const month = parseInt(parts[1], 10);
    const day = parseInt(parts[2], 10);

    if (isNaN(year) || isNaN(month) || isNaN(day)) return false;
    if (year < 1900 || year > 2025) return false;
    if (month < 1 || month > 12) return false;
    if (day < 1 || day > 31) return false;

    return true;
}

/**
 * Truncates a string to a specified maximum length, adding "..." if truncated.
 */
function truncate(str, maxLen) {
    if (!str) return "";
    if (str.length <= maxLen) return str;
    return str.substring(0, maxLen - 3) + "...";
}

/**
 * Pads a string to a specified length (left-aligned).
 */
function padRight(str, len) {
    str = String(str);
    while (str.length < len) str += " ";
    return str;
}

/**
 * Creates a separator line of dashes.
 */
function separator(len) {
    return "-".repeat(len);
}

// ===================== CSV PARSER =====================

/**
 * Parses a single CSV line, properly handling quoted fields
 * that may contain commas.
 */
function parseCSVLine(line) {
    const fields = [];
    let inQuotes = false;
    let current = "";

    for (let i = 0; i < line.length; i++) {
        const c = line[i];
        if (c === '"') {
            inQuotes = !inQuotes;
        } else if (c === ',' && !inQuotes) {
            fields.push(current);
            current = "";
        } else {
            current += c;
        }
    }
    fields.push(current);
    return fields;
}

// ===================== DATASET LOADING =====================

/**
 * Loads the CSV dataset from a file path.
 * Returns an object with headers and records.
 */
function loadDataset(filePath) {
    const content = fs.readFileSync(filePath, 'utf-8');
    const lines = content.split(/\r?\n/);
    const records = [];
    let headers = [];

    for (let i = 0; i < lines.length; i++) {
        const line = lines[i].trim();
        if (line === "") continue;

        const fields = parseCSVLine(line);

        if (i === 0) {
            headers = fields.map(f => f.trim());
            continue;
        }

        records.push(new DataRecord(i + 1, fields));
    }

    return { headers, records };
}

// ===================== VALIDATION FUNCTIONS =====================

/**
 * Detects records with missing values (empty or blank fields).
 */
function detectMissingValues(records) {
    console.log();
    console.log("  +--------------------------------------------------------------------+");
    console.log("  |  (1) MISSING VALUES REPORT                                         |");
    console.log("  +--------------------------------------------------------------------+");

    const columnsToCheck = [
        "title", "console", "genre", "publisher", "developer",
        "critic_score", "total_sales", "na_sales", "jp_sales",
        "pal_sales", "other_sales", "release_date"
    ];

    const missingCounts = {};
    columnsToCheck.forEach(col => missingCounts[col] = 0);

    let totalMissingRecords = 0;

    const fieldGetters = {
        title: r => r.title,
        console: r => r.console,
        genre: r => r.genre,
        publisher: r => r.publisher,
        developer: r => r.developer,
        critic_score: r => r.criticScore,
        total_sales: r => r.totalSales,
        na_sales: r => r.naSales,
        jp_sales: r => r.jpSales,
        pal_sales: r => r.palSales,
        other_sales: r => r.otherSales,
        release_date: r => r.releaseDate
    };

    for (const record of records) {
        let hasMissing = false;

        for (const col of columnsToCheck) {
            if (isEmpty(fieldGetters[col](record))) {
                missingCounts[col]++;
                hasMissing = true;
            }
        }

        if (hasMissing) totalMissingRecords++;
    }

    console.log(`  ${padRight("Column", 20)} | ${padRight("Missing Count", 15)} | ${"% Missing"}`);
    console.log("  " + separator(50));
    for (const col of columnsToCheck) {
        const count = missingCounts[col];
        const pct = ((count / records.length) * 100).toFixed(2);
        console.log(`  ${padRight(col, 20)} | ${padRight(count, 15)} | ${pct}%`);
    }
    console.log();
    console.log(`  >> Total records with at least one missing value: ${totalMissingRecords}`);
    console.log(`  >> Percentage of records affected: ${((totalMissingRecords / records.length) * 100).toFixed(2)}%`);

    return totalMissingRecords;
}

/**
 * Detects records where any sales column has a negative value.
 */
function detectNegativeSales(records, mode) {
    console.log();
    console.log("  +--------------------------------------------------------------------+");
    console.log("  |  (2) NEGATIVE SALES REPORT                                         |");
    console.log("  +--------------------------------------------------------------------+");

    let negativeSalesCount = 0;
    const negativeSamples = [];

    const salesFields = [
        { name: "total_sales", getter: r => r.totalSales },
        { name: "na_sales", getter: r => r.naSales },
        { name: "jp_sales", getter: r => r.jpSales },
        { name: "pal_sales", getter: r => r.palSales },
        { name: "other_sales", getter: r => r.otherSales }
    ];

    for (const record of records) {
        const negCols = [];

        for (const field of salesFields) {
            const value = field.getter(record);
            if (!isEmpty(value)) {
                const num = parseFloat(value);
                if (!isNaN(num) && num < 0) {
                    negCols.push(field.name);
                }
            }
        }

        if (negCols.length > 0) {
            negativeSalesCount++;
            negativeSamples.push({
                row: record.rowNumber,
                title: record.title,
                cols: negCols.join(", ")
            });
        }
    }

    if (negativeSalesCount === 0) {
        console.log("  [OK] No negative sales values detected. All clear!");
    } else {
        console.log(`  [!] Total records with negative sales: ${negativeSalesCount}`);
        console.log();

        let limit = mode === 1 ? 10 : (mode === 2 ? Math.max(1, Math.floor(negativeSamples.length / 2)) : negativeSamples.length);
        limit = Math.min(limit, negativeSamples.length);
        const modeStr = mode === 1 ? "up to 10" : (mode === 2 ? "half" : "all");

        console.log(`  Records with negative sales (${modeStr} - displaying ${limit}):`);
        console.log(`  ${padRight("Row", 10)} | ${padRight("Title", 40)} | ${"Affected Columns"}`);
        console.log("  " + separator(65));
        for (let i = 0; i < limit; i++) {
            const sample = negativeSamples[i];
            console.log(`  ${padRight("Row " + sample.row, 10)} | ${padRight(truncate(sample.title, 40), 40)} | ${sample.cols}`);
        }
    }

    return negativeSalesCount;
}

/**
 * Detects records with invalid date formats.
 * Expected format: YYYY-MM-DD
 */
function detectInvalidDates(records, mode) {
    console.log();
    console.log("  +--------------------------------------------------------------------+");
    console.log("  |  (3) INVALID DATES REPORT                                          |");
    console.log("  +--------------------------------------------------------------------+");

    let invalidReleaseDateCount = 0;
    let invalidLastUpdateCount = 0;
    const invalidDateSamples = [];

    for (const record of records) {
        let releaseDateInvalid = false;
        let lastUpdateInvalid = false;

        if (!isEmpty(record.releaseDate) && !isValidDate(record.releaseDate)) {
            invalidReleaseDateCount++;
            releaseDateInvalid = true;
        }

        if (!isEmpty(record.lastUpdate) && !isValidDate(record.lastUpdate)) {
            invalidLastUpdateCount++;
            lastUpdateInvalid = true;
        }

        if (releaseDateInvalid || lastUpdateInvalid) {
            let issue = "";
            if (releaseDateInvalid) issue += `release_date=${record.releaseDate}`;
            if (lastUpdateInvalid) {
                if (issue) issue += ", ";
                issue += `last_update=${record.lastUpdate}`;
            }
            invalidDateSamples.push({
                row: record.rowNumber,
                title: record.title,
                issue: issue
            });
        }
    }

    console.log(`  ${padRight("Column", 20)} | ${"Invalid Count"}`);
    console.log("  " + separator(40));
    console.log(`  ${padRight("release_date", 20)} | ${invalidReleaseDateCount}`);
    console.log(`  ${padRight("last_update", 20)} | ${invalidLastUpdateCount}`);

    if (invalidDateSamples.length > 0) {
        console.log();

        let limit = mode === 1 ? 10 : (mode === 2 ? Math.max(1, Math.floor(invalidDateSamples.length / 2)) : invalidDateSamples.length);
        limit = Math.min(limit, invalidDateSamples.length);
        const modeStr = mode === 1 ? "up to 10" : (mode === 2 ? "half" : "all");

        console.log(`  Invalid date records (${modeStr} - displaying ${limit}):`);
        console.log(`  ${padRight("Row", 10)} | ${padRight("Title", 40)} | ${"Invalid Date(s)"}`);
        console.log("  " + separator(65));
        for (let i = 0; i < limit; i++) {
            const sample = invalidDateSamples[i];
            console.log(`  ${padRight("Row " + sample.row, 10)} | ${padRight(truncate(sample.title, 40), 40)} | ${sample.issue}`);
        }
    }

    return invalidReleaseDateCount + invalidLastUpdateCount;
}

/**
 * Detects duplicate records based on title + console + release_date combination.
 */
function detectDuplicateRecords(records, mode) {
    console.log();
    console.log("  +--------------------------------------------------------------------+");
    console.log("  |  (4) DUPLICATE RECORDS REPORT                                      |");
    console.log("  +--------------------------------------------------------------------+");

    const keyMap = {};

    for (const record of records) {
        const key = record.getDuplicateKey();
        if (!keyMap[key]) {
            keyMap[key] = [];
        }
        keyMap[key].push(record);
    }

    let duplicateGroupCount = 0;
    let totalDuplicateRecords = 0;
    const duplicateSamples = [];

    for (const key in keyMap) {
        const group = keyMap[key];
        if (group.length > 1) {
            duplicateGroupCount++;
            totalDuplicateRecords += group.length;

            const first = group[0];
            const rows = group.map(r => r.rowNumber).join(", ");
            duplicateSamples.push({
                title: first.title,
                console: first.console,
                releaseDate: first.releaseDate,
                rows: rows
            });
        }
    }

    console.log(`  Duplicate groups found: ${duplicateGroupCount}`);
    console.log(`  Total duplicate records: ${totalDuplicateRecords}`);

    if (duplicateSamples.length > 0) {
        console.log();

        let limit = mode === 1 ? 10 : (mode === 2 ? Math.max(1, Math.floor(duplicateSamples.length / 2)) : duplicateSamples.length);
        limit = Math.min(limit, duplicateSamples.length);
        const modeStr = mode === 1 ? "up to 10" : (mode === 2 ? "half" : "all");

        console.log(`  Duplicate groups (${modeStr} - displaying ${limit}):`);
        console.log(`  ${padRight("Title", 40)} | ${padRight("Console", 10)} | ${padRight("Release Date", 12)} | ${"Row Numbers"}`);
        console.log("  " + separator(80));
        for (let i = 0; i < limit; i++) {
            const sample = duplicateSamples[i];
            console.log(`  ${padRight(truncate(sample.title, 40), 40)} | ${padRight(sample.console, 10)} | ${padRight(sample.releaseDate, 12)} | ${sample.rows}`);
        }
    }

    return duplicateGroupCount;
}

/**
 * Displays a final summary of the entire data quality report.
 */
function displaySummary(records, missingCount, negativeCount, invalidDateCount, duplicateGroups) {
    console.log();
    console.log("+======================================================================+");
    console.log("|          DATA QUALITY SUMMARY                                        |");
    console.log("+======================================================================+");
    console.log(`  [i] Total records analyzed: ${records.length}`);
    console.log();
    console.log(`  [!] ${padRight("Records with missing values", 35)} : ${missingCount}`);
    console.log(`  [$] ${padRight("Records with negative sales", 35)} : ${negativeCount}`);
    console.log(`  [~] ${padRight("Records with invalid dates", 35)} : ${invalidDateCount}`);
    console.log(`  [=] ${padRight("Duplicate record groups", 35)} : ${duplicateGroups}`);
    console.log();

    // Overall quality score
    const totalIssues = missingCount + negativeCount + invalidDateCount + duplicateGroups;
    let qualityScore = (1.0 - (totalIssues / (records.length * 4))) * 100;
    qualityScore = Math.max(0, Math.min(100, qualityScore));

    let scoreLabel;
    if (qualityScore >= 90) scoreLabel = "[***] EXCELLENT";
    else if (qualityScore >= 70) scoreLabel = "[**]  GOOD";
    else if (qualityScore >= 50) scoreLabel = "[*]   FAIR";
    else scoreLabel = "[!]   POOR";

    console.log(`  ${scoreLabel} - Overall Data Quality Score: ${qualityScore.toFixed(2)}%`);
    console.log();
    console.log("+======================================================================+");
    console.log("|  [OK]  Report generated successfully!                                |");
    console.log("+======================================================================+");
    console.log();
}

// ===================== MAIN PROGRAM FLOW =====================

/**
 * Prompts the user for the display limit before running checks.
 */
function askDisplayMode(records) {
    console.log();
    console.log("  [?] How many error samples would you like to display per category?");
    console.log("      (1) Up to 10 samples (Recommended)");
    console.log("      (2) Half of the errors");
    console.log("      (3) All errors");
    rl.question("  [>] Enter your choice (1-3): ", function (choice) {
        choice = choice.trim();
        if (choice === "1" || choice === "2" || choice === "3") {
            const mode = parseInt(choice, 10);

            // Perform data cleaning and validation
            console.log();
            console.log("+======================================================================+");
            console.log("|          DATA CLEANING AND VALIDATION REPORT                         |");
            console.log("+======================================================================+");

            const missingCount = detectMissingValues(records);
            console.log();
            rl.question("  [>] Press [ENTER] to continue...", function () {
                const negativeCount = detectNegativeSales(records, mode);
                console.log();
                rl.question("  [>] Press [ENTER] to continue...", function () {
                    const invalidDateCount = detectInvalidDates(records, mode);
                    console.log();
                    rl.question("  [>] Press [ENTER] to continue...", function () {
                        const duplicateGroups = detectDuplicateRecords(records, mode);
                        console.log();
                        rl.question("  [>] Press [ENTER] to continue...", function () {
                            displaySummary(records, missingCount, negativeCount, invalidDateCount, duplicateGroups);
                            rl.close();
                        });
                    });
                });
            });
        } else {
            console.log("  [X] Invalid choice. Please enter 1, 2, or 3.");
            askDisplayMode(records);
        }
    });
}

/**
 * Prompts the user for the dataset file path.
 * Validates file existence and CSV format.
 * Loops until a valid path is provided.
 */
function askFilePath() {
    console.log();
    console.log("+======================================================================+");
    console.log("|                                                                      |");
    console.log("|   [#]  DATA CLEANING AND VALIDATION REPORT TOOL                      |");
    console.log("|   [*]  Programming 2 - Machine Problem (LAORDIN)                     |");
    console.log("|   [>]  University of Perpetual Help System DALTA                     |");
    console.log("|                                                                      |");
    console.log("+======================================================================+");
    console.log();
    console.log("  [?] This program analyzes a CSV dataset for data quality issues.");
    console.log();
    console.log("  [i] Checks performed:");
    console.log("      (1) Missing Values     (2) Negative Sales");
    console.log("      (3) Invalid Dates      (4) Duplicate Records");
    console.log();
    console.log("  [>] Please provide the full file path to the CSV dataset.");
    console.log("  [>] Example: C:\\Users\\Laordin\\Downloads\\vgchartz-2024.csv");
    console.log("  " + separator(66));
    console.log();
    rl.question("Enter dataset file path: ", function (path) {
        path = path.trim();

        // Validate file existence
        if (!fs.existsSync(path)) {
            console.log("  [X] Invalid file path. Try again.");
            askFilePath();
            return;
        }

        // Validate that it is a file (not directory)
        try {
            const stats = fs.statSync(path);
            if (!stats.isFile()) {
                console.log("  [X] Error: Path is not a file. Please try again.");
                askFilePath();
                return;
            }
        } catch (err) {
            console.log("  [X] Error reading file: " + err.message);
            askFilePath();
            return;
        }

        // Validate CSV format
        if (!path.toLowerCase().endsWith('.csv')) {
            console.log("  [X] Error: File is not in CSV format. Please try again.");
            askFilePath();
            return;
        }

        console.log();
        console.log("  [OK] File found! Processing...");

        // Load and process the dataset
        try {
            const { headers, records } = loadDataset(path);

            console.log("  [OK] Dataset loaded successfully!");
            console.log(`  [i] Total records loaded: ${records.length}`);

            askDisplayMode(records);

        } catch (err) {
            console.log("  [X] Error processing file: " + err.message);
            rl.close();
        }
    });
}

// Start the program
askFilePath();
