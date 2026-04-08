const fs = require("node:fs");
const path = require("node:path");
const readline = require("node:readline");

// Asks a question in the terminal and returns trimmed user input.
function askQuestion(rl, promptText) {
    return new Promise((resolve) => {
        rl.question(promptText, (answer) => resolve(answer.trim()));
    });
}

// Creates an input reader that supports both interactive and piped execution.
function createInputReader() {
    if (!process.stdin.isTTY) {
        const pipedData = fs.readFileSync(0, "utf8").split(/\r?\n/);
        let cursor = 0;

        return {
            ask(promptText) {
                process.stdout.write(promptText);
                const value = (pipedData[cursor] || "").trim();
                cursor += 1;
                if (value) {
                    process.stdout.write(`${value}\n`);
                }
                return Promise.resolve(value);
            },
            close() {
                // No readline interface to close in piped mode.
            },
        };
    }

    const rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout,
    });

    return {
        ask(promptText) {
            return askQuestion(rl, promptText);
        },
        close() {
            rl.close();
        },
    };
}

// Parses one CSV line and keeps commas inside quoted values intact.
function parseCsvLine(line) {
    const values = [];
    let current = "";
    let inQuotes = false;

    for (let i = 0; i < line.length; i += 1) {
        const ch = line[i];

        if (ch === '"') {
            if (inQuotes && i + 1 < line.length && line[i + 1] === '"') {
                current += '"';
                i += 1;
            } else {
                inQuotes = !inQuotes;
            }
        } else if (ch === "," && !inQuotes) {
            values.push(current);
            current = "";
        } else {
            current += ch;
        }
    }

    values.push(current);
    return values;
}

// Checks if all fields in the row are empty.
function isEmptyRow(row) {
    return row.every((value) => value.trim() === "");
}

// Pads or trims rows to match the header length.
function normalizeRowSize(row, targetSize) {
    const normalized = [...row];
    while (normalized.length < targetSize) {
        normalized.push("");
    }
    return normalized.slice(0, targetSize);
}

// Reads CSV, finds header, and returns parsed rows.
function loadDataset(datasetPath) {
    const raw = fs.readFileSync(datasetPath, "utf8");
    const lines = raw.split(/\r?\n/);

    let header = null;
    const rows = [];

    for (const line of lines) {
        const parsed = parseCsvLine(line);

        if (!header) {
            if (parsed.length > 0 && parsed[0].trim().toLowerCase() === "candidate") {
                header = [...parsed];
            }
            continue;
        }

        if (isEmptyRow(parsed)) {
            continue;
        }

        rows.push(normalizeRowSize(parsed, header.length));
    }

    if (!header) {
        throw new Error("Header row not found. Expected first column to be 'Candidate'.");
    }

    return { header, rows };
}

// Escapes one value for valid CSV output.
function escapeCsv(value) {
    const needsQuotes = value.includes(",") || value.includes('"') || value.includes("\n") || value.includes("\r");
    if (!needsQuotes) {
        return value;
    }
    return `"${value.replace(/"/g, '""')}"`;
}

// Writes CSV content to file using header + first N rows.
function exportRows(dataset, outputPath, rowCount) {
    const outputLines = [];
    outputLines.push(dataset.header.map(escapeCsv).join(","));

    for (let i = 0; i < rowCount; i += 1) {
        outputLines.push(dataset.rows[i].map(escapeCsv).join(","));
    }

    fs.writeFileSync(outputPath, `${outputLines.join("\n")}\n`, "utf8");
}

// Compares paths in normalized absolute form to avoid accidental overwrite.
function isSamePath(pathA, pathB) {
    const normalize = (p) => path.resolve(p).replace(/\//g, "\\").toLowerCase();
    return normalize(pathA) === normalize(pathB);
}

async function main() {
    const inputReader = createInputReader();

    try {
        // Required by instruction: ask dataset path first.
        const datasetPath = await inputReader.ask("Enter CSV dataset file path: ");
        if (!datasetPath) {
            console.log("Dataset path is required.");
            return;
        }

        // Auto-create output path inside MIDTERM-LAB-3/JavaScript as requested.
        const outputPath = path.join(__dirname, "mp15_first50_js_output.csv");

        if (isSamePath(datasetPath, outputPath)) {
            console.log("Output file must be different from dataset file.");
            console.log("Please provide a new CSV file path for export.");
            return;
        }

        const dataset = loadDataset(datasetPath);
        const exportCount = Math.min(50, dataset.rows.length);
        exportRows(dataset, outputPath, exportCount);

        // Formatted result summary.
        console.log();
        console.log("MP15 RESULT");
        console.log("-----------");
        console.log(`Total valid data rows: ${dataset.rows.length}`);
        console.log(`Rows exported       : ${exportCount}`);
        console.log(`Output file (auto)  : ${outputPath}`);
        console.log("Export completed successfully.");
    } catch (error) {
        console.log(`File processing error: ${error.message}`);
    } finally {
        inputReader.close();
    }
}

main();
