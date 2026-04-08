# MIDTERM LAB 3 - Machine Problems MP15, MP16, MP17

## Student Assignment Coverage
This folder contains complete implementations for your assigned machine problems:
- MP15 - Export first 50 rows to CSV
- MP16 - Random dataset sampler
- MP17 - Find longest text entry

Each machine problem is implemented in both Java and JavaScript, and both versions ask for the dataset path first before processing.

## Folder Structure
- Java/
  - MP15ExportFirst50.java
  - MP16RandomSampler.java
  - MP17LongestTextEntry.java
- JavaScript/
  - mp15_export_first_50.js
  - mp16_random_sampler.js
  - mp17_longest_text_entry.js
- Sample_Data-Prog-2-csv.csv

## How To Run (Java)
From the MIDTERM-LAB-3/Java folder:

```bash
javac MP15ExportFirst50.java MP16RandomSampler.java MP17LongestTextEntry.java
java MP15ExportFirst50
java MP16RandomSampler
java MP17LongestTextEntry
```

## How To Run (JavaScript)
From the MIDTERM-LAB-3/JavaScript folder:

```bash
node mp15_export_first_50.js
node mp16_random_sampler.js
node mp17_longest_text_entry.js
```

## Program Logic Explanations (3-5 sentences each)
### MP15 - Export first 50 rows to CSV
This program prompts the user for the dataset file path first, then automatically creates the output CSV in its language folder. It uses a quote-aware CSV parser so values with commas, such as candidate names, are read correctly. The script/class locates the actual header row and skips the metadata lines above it. It exports only the first 50 valid data rows plus the header into a new CSV file. It also prints a summary showing total rows and exported rows.

Automatic MP15 output locations:
- Java: MIDTERM-LAB-3/Java/mp15_first50_java_output.csv
- JavaScript: MIDTERM-LAB-3/JavaScript/mp15_first50_js_output.csv

### MP16 - Random dataset sampler
This program starts by asking for the dataset path first and then asks for the sample size. After parsing the dataset, it shuffles records and selects random rows without replacement. The output is shown in a readable table with key fields such as Candidate, Exam, Score, Result, and Time Used. It validates invalid sample size input and handles file errors gracefully. It also prints requested size, actual sampled size, and total valid rows.

### MP17 - Find longest text entry
This program first asks for the dataset file path and loads all valid rows after the true CSV header. It scans every field in every row to find the longest non-empty text entry. Once found, it prints the data row number, column name, text length, and the full text value. It also displays related Candidate and Exam values from the same row for context. Input and file errors are handled with clear messages.

## Output Screenshot Requirement
Its in the file of Screenshots folder of Java and Javascript
