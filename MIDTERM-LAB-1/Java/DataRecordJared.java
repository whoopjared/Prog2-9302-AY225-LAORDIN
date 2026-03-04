/**
 * DataRecord.java
 * Represents a single record (row) from the vgchartz-2024.csv dataset.
 * 
 * PROGRAMMING 2 - MACHINE PROBLEM
 * University of Perpetual Help System DALTA - Molino Campus
 * BS Information Technology - Game Development
 * 
 * Task: Data Cleaning and Validation Report (LAORDIN)
 */
public class DataRecordJared {
    private int rowNumber;
    private String img;
    private String title;
    private String console;
    private String genre;
    private String publisher;
    private String developer;
    private String criticScore;
    private String totalSales;
    private String naSales;
    private String jpSales;
    private String palSales;
    private String otherSales;
    private String releaseDate;
    private String lastUpdate;

    public DataRecordJared(int rowNumber, String[] fields) {
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

    // Getters
    public int getRowNumber() {
        return rowNumber;
    }

    public String getImg() {
        return img;
    }

    public String getTitle() {
        return title;
    }

    public String getConsole() {
        return console;
    }

    public String getGenre() {
        return genre;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getDeveloper() {
        return developer;
    }

    public String getCriticScore() {
        return criticScore;
    }

    public String getTotalSales() {
        return totalSales;
    }

    public String getNaSales() {
        return naSales;
    }

    public String getJpSales() {
        return jpSales;
    }

    public String getPalSales() {
        return palSales;
    }

    public String getOtherSales() {
        return otherSales;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    /**
     * Returns a unique key based on title, console, and release_date
     * for duplicate detection.
     */
    public String getDuplicateKey() {
        return (title + "|" + console + "|" + releaseDate).toLowerCase();
    }
}
