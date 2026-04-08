const fs = require("node:fs");
const readline = require("node:readline");

// Asks a terminal question and returns trimmed input.
function askQuestion(rl, promptText) {
    return new Promise((resolve) => {
        rl.question(promptText, (answer) => resolve(answer.trim()));
    });
}

// Creates an input reader that works for interactive and piped stdin.
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

// Parses CSV line while preserving commas inside quotes.
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

// Checks whether the row contains only blank fields.
function isEmptyRow(row) {
    return row.every((value) => value.trim() === "");
}

// Makes row size consistent with header size.
function normalizeRowSize(row, targetSize) {
    const normalized = [...row];
    while (normalized.length < targetSize) {
        normalized.push("");
    }
    return normalized.slice(0, targetSize);
}

// Reads and parses the dataset rows after finding real header.
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

// Returns random rows without replacement.
function getRandomSample(rows, requestedSize) {
    const shuffled = [...rows];

    // Fisher-Yates shuffle for unbiased random ordering.
    for (let i = shuffled.length - 1; i > 0; i -= 1) {
        const j = Math.floor(Math.random() * (i + 1));
        [shuffled[i], shuffled[j]] = [shuffled[j], shuffled[i]];
    }

    const actualSize = Math.min(requestedSize, shuffled.length);
    return shuffled.slice(0, actualSize);
}

// Gets value safely from row by index.
function getCell(row, index) {
    if (index < 0 || index >= row.length) {
        return "";
    }
    return row[index].trim();
}

// Limits text length for compact table output.
function truncate(value, maxLen) {
    if (value.length <= maxLen) {
        return value;
    }
    if (maxLen <= 3) {
        return value.slice(0, maxLen);
    }
    return `${value.slice(0, maxLen - 3)}...`;
}

// Converts "48 min 37 sec" to "48m37s" to reduce column width.
function compactTime(value) {
    const match = value.trim().match(/^(\d+)\s*min\s*(\d+)\s*sec$/i);
    if (match) {
        return `${match[1]}m${match[2]}s`;
    }
    return value;
}

// Prints selected sample rows as aligned table.
function printSampleTable(header, rows) {
    const candidateIdx = header.indexOf("Candidate");
    const examIdx = header.indexOf("Exam");
    const scoreIdx = header.indexOf("Score");
    const resultIdx = header.indexOf("Result");
    const timeIdx = header.indexOf("Time Used");

    // Keep fixed widths so output fits small terminal windows.
    const noWidth = 2;
    const candidateWidth = 15;
    const examWidth = 22;
    const scoreWidth = 5;
    const resultWidth = 4;
    const timeWidth = 7;

    const dividerLength = noWidth + candidateWidth + examWidth + scoreWidth + resultWidth + timeWidth + (5 * 3);
    const divider = "-".repeat(dividerLength);
    console.log();
    console.log("MP16 RANDOM SAMPLE");
    console.log(divider);
    console.log(
        `${"No".padEnd(noWidth)} | ${"Candidate".padEnd(candidateWidth)} | ${"Exam".padEnd(examWidth)} | ${"Score".padEnd(scoreWidth)} | ${"Res".padEnd(resultWidth)} | ${"Time".padEnd(timeWidth)}`
    );
    console.log(divider);

    rows.forEach((row, index) => {
        const candidate = truncate(getCell(row, candidateIdx), candidateWidth).padEnd(candidateWidth);
        const exam = truncate(getCell(row, examIdx), examWidth).padEnd(examWidth);
        const score = truncate(getCell(row, scoreIdx), scoreWidth).padEnd(scoreWidth);
        const result = truncate(getCell(row, resultIdx), resultWidth).padEnd(resultWidth);
        const timeUsed = truncate(compactTime(getCell(row, timeIdx)), timeWidth).padEnd(timeWidth);

        console.log(`${String(index + 1).padEnd(noWidth)} | ${candidate} | ${exam} | ${score} | ${result} | ${timeUsed}`);
    });

    console.log(divider);
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

        const sampleInput = await inputReader.ask("Enter sample size: ");
        const requestedSize = Number.parseInt(sampleInput, 10);

        if (!Number.isInteger(requestedSize) || requestedSize <= 0) {
            console.log("Sample size must be a whole number greater than 0.");
            return;
        }

        const dataset = loadDataset(datasetPath);
        if (dataset.rows.length === 0) {
            console.log("No valid data rows were found in the dataset.");
            return;
        }

        const sampledRows = getRandomSample(dataset.rows, requestedSize);
        printSampleTable(dataset.header, sampledRows);

        console.log();
        console.log(`Requested sample size: ${requestedSize}`);
        console.log(`Actual sample size   : ${sampledRows.length}`);
        console.log(`Total data rows      : ${dataset.rows.length}`);
    } catch (error) {
        console.log(`File processing error: ${error.message}`);
    } finally {
        inputReader.close();
    }
}

main();
