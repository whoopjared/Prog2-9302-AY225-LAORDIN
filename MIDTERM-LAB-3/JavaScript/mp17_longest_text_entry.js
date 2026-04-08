const fs = require("node:fs");
const readline = require("node:readline");

// Reads user input from terminal prompt.
function askQuestion(rl, promptText) {
    return new Promise((resolve) => {
        rl.question(promptText, (answer) => resolve(answer.trim()));
    });
}

// Creates an input reader that supports interactive and piped execution.
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
                // Nothing to close in piped mode.
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

// Parses a CSV line with support for quoted values.
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

// Returns true when a row has no non-empty fields.
function isEmptyRow(row) {
    return row.every((value) => value.trim() === "");
}

// Keeps row column count aligned with header count.
function normalizeRowSize(row, targetSize) {
    const normalized = [...row];
    while (normalized.length < targetSize) {
        normalized.push("");
    }
    return normalized.slice(0, targetSize);
}

// Loads dataset rows after finding actual header line.
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

// Scans all fields and returns the longest non-empty text entry.
function findLongestTextEntry(dataset) {
    let best = null;

    dataset.rows.forEach((row, rowIndex) => {
        row.forEach((value, colIndex) => {
            const text = value.trim();
            if (!text) {
                return;
            }

            if (!best || text.length > best.length) {
                best = {
                    rowNumber: rowIndex + 1,
                    columnName: dataset.header[colIndex] || `Column ${colIndex + 1}`,
                    value: text,
                    length: text.length,
                    sourceRow: row,
                };
            }
        });
    });

    return best;
}

// Safe getter for row field values.
function getCell(row, index) {
    if (index < 0 || index >= row.length) {
        return "";
    }
    return row[index].trim();
}

// Displays result details in a readable block.
function printResult(header, longestEntry) {
    if (!longestEntry) {
        console.log("No text entry found in dataset rows.");
        return;
    }

    const candidateIdx = header.indexOf("Candidate");
    const examIdx = header.indexOf("Exam");

    console.log();
    console.log("MP17 LONGEST TEXT ENTRY");
    console.log("-----------------------");
    console.log(`Data row number : ${longestEntry.rowNumber}`);
    console.log(`Column name     : ${longestEntry.columnName}`);
    console.log(`Text length     : ${longestEntry.length}`);
    console.log(`Text value      : ${longestEntry.value}`);
    console.log(`Candidate       : ${getCell(longestEntry.sourceRow, candidateIdx)}`);
    console.log(`Exam            : ${getCell(longestEntry.sourceRow, examIdx)}`);
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

        const dataset = loadDataset(datasetPath);
        if (dataset.rows.length === 0) {
            console.log("No valid data rows were found in the dataset.");
            return;
        }

        const longestEntry = findLongestTextEntry(dataset);
        printResult(dataset.header, longestEntry);
    } catch (error) {
        console.log(`File processing error: ${error.message}`);
    } finally {
        inputReader.close();
    }
}

main();
