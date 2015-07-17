package server.integration;

import server.integration.DBConnection;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;



/**
 * Methods for reading CSV files
 * and import them into DB
 * Developed for import tables: d_company, f_quotes
 *
 * @author Vladimir GLADUN (vvgladoun@gmail.com)
 */
public final class CSVReader {

    // Number of rows to commit (for batch load to DB)
    private static int BATCH_SIZE = 50;

    // Properties of csv file
    private String fileName;
    private String delimeter = ",";

    // indexes of columns in csv for companies
    private LinkedHashMap<String, Integer> companyColumns = new LinkedHashMap<String, Integer>();

    // indexes of columns in csv for quotes
    private LinkedHashMap<String, Integer> quoteColumns = new LinkedHashMap<String, Integer>();

    // default value for column id_company in f_quotes
    private int id_company;


    // ArrayList for data from csv
    private ArrayList<String> rowsArray = new ArrayList<String>();


    // Status string (if import failed, defines the error)
    private String statusString = "";

    // number of rows, used as a header (without data)
    private int headerRows = 0;



    // instance initialization for columns in CSV
    {
        //for d_company
        companyColumns.put("code", 0);
        companyColumns.put("name", 1);
        //for f_quote
        quoteColumns.put("id_company", -1); //-1 for id_company
        quoteColumns.put("close_date", 0);
        quoteColumns.put("open_price", 1);
        quoteColumns.put("high_price", 2);
        quoteColumns.put("low_price", 3);
        quoteColumns.put("close_price", 4);
        quoteColumns.put("volume", 5);
        quoteColumns.put("adj_close", 6);
    }

    public CSVReader() {
    }

    public CSVReader(String csvFilename){
        this.fileName = csvFilename;
    }

    public static int getBatchSize() {
        return BATCH_SIZE;
    }

    public static void setBatchSize(int batchSize) {
        BATCH_SIZE = batchSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDelimeter() {
        return delimeter;
    }

    public void setDelimeter(String delimeter) {
        this.delimeter = delimeter;
    }

    public String getStatusString() {
        return statusString;
    }

    public void setId_company(int id_company) {
        this.id_company = id_company;
    }

    public LinkedHashMap<String, Integer> getCompanyColumns() {
        return companyColumns;
    }

    public LinkedHashMap<String, Integer> getQuoteColumns() {
        return quoteColumns;
    }

    public void setHeaderRows(int headerRows) {
        this.headerRows = headerRows;
    }

    /**
     * Replace column index by column name for d_company
     *
     * @param columnName - name of the column (key)
     * @param columnIndex - index of the column (value)
     */
    public void setCompanyColumns(String columnName, Integer columnIndex) {
        setTableColumns(companyColumns, columnName, columnIndex);
    }

    /**
     * Replace column index by column name for f_quotes
     *
     * @param columnName - name of the column (key)
     * @param columnIndex - index of the column (value)
     */
    public void setQuoteColumns(String columnName, Integer columnIndex) {
        setTableColumns(quoteColumns, columnName, columnIndex);
    }

    /**
     * Replace column index by column name
     * in columns' map
     *
     * @param mapColumns - hash map for columns and indexes
     * @param columnName - name of the column (key)
     * @param columnIndex - index of the column (value)
     */
    public void setTableColumns(LinkedHashMap<String, Integer> mapColumns,
                                String columnName, Integer columnIndex) {
        try {
            mapColumns.replace(columnName, columnIndex);
        } catch (Exception e) {
            //wrong column definition
            return;
        }
    }


    /**
     * Insert statement generator for batch load
     *
     * @param mapColumns
     * @return prepared insert statement
     */
    private String createInsertStatement(LinkedHashMap<String, Integer> mapColumns, String tablename){
        StringBuilder sb = new StringBuilder("INSERT INTO nzx." + tablename + "(");
        StringBuilder sb_ending = new StringBuilder(") VALUES (");
        for (String col : mapColumns.keySet()) {
            sb.append(col + ",");
            sb_ending.append("?,");
        }
        sb.setLength(sb.length() - 1);
        sb_ending.setLength(sb_ending.length() - 1);
        sb.append(sb_ending.toString() + ")");

        return sb.toString();
    }

    /**
     * Import data from array to d_company
     * table will be truncated before load
     *
     * @return true if import was successful, otherwise - false
     */
    public boolean importCompany(){
        boolean importStatus = importData(companyColumns, "d_company", true, false);
        HttpDownloadUtility.updateCompaniesDescription(); //try to update descriptions
        return importStatus;
    }

    /**
     * Import data from array to f_quotes
     * data for id_company will be deleted before load
     *
     * @return true if import was successful, otherwise - false
     */
    public boolean importQuote(){
        return importData(quoteColumns, "f_quotes", false, true);
    }




    /**
     * Import data from array to DB table,
     * described in hashMap
     *
     * @param tableColumns - hashMap with table columns and their
     *                     indexes in CSV file
     * @param tableName - name of the table in DB
     * @param truncateBeforeLoad - if true table will be truncated
     * @param deleteByIdCompany - if true will try to delete data by id_company
     *
     * @return true if import was successful, otherwise - false
     */
    public boolean importData(LinkedHashMap<String, Integer> tableColumns,
                              String tableName, boolean truncateBeforeLoad,
                              boolean deleteByIdCompany){

        if (!readCSV(fileName)) {
            this.statusString = "cannot read csv file";
            return false;
        }

        DBConnection dbc = new DBConnection();
        if (!dbc.isConnected()) {
            this.statusString = "cannot connect to database";
            return false;
        }
        Connection conn = dbc.getDbConnect();

        // Truncate table if needed
        if (truncateBeforeLoad) {
            try {
                Statement stmt = conn.createStatement();
                String sql = "TRUNCATE TABLE nzx." + tableName + " CASCADE;";
                stmt.execute(sql);
                stmt.close();
            } catch (SQLException e) {
                this.statusString = "truncate failed";
                //e.printStackTrace();
                return false;
            }
        } else {
            // delete by company's id
            if (deleteByIdCompany) {
                try {
                    Statement stmt = conn.createStatement();
                    String sql = "DELETE FROM nzx." + tableName +
                            " WHERE id_company = " + id_company + ";";
                    stmt.execute(sql);
                    stmt.close();
                } catch (SQLException e) {
                    this.statusString = "cannot delete data by id_company";
                    //e.printStackTrace();
                    return false;
                }
            }
        }

        // Insert data into DB
        PreparedStatement ps = null;
        String query = createInsertStatement(tableColumns, tableName);

        try {
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(query);

            int counter = 0;

            // Loop through result array
            for (String row : rowsArray) {

                // read data row (separated string)
                String[] dataRow = row.split(delimeter);
                int i = 1;
                // for each column, read data by index from HashMap
                for (Map.Entry<String, Integer> entry : tableColumns.entrySet()) {
                    String colName = entry.getKey();
                    if (colName == "id_company") {
                        ps.setInt(i, this.id_company);
                    } else if (colName == "code" || colName == "name") {
                        // set string
                        ps.setString(i, dataRow[entry.getValue()]);
                    } else if (colName == "close_date") {
                        // parse date
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        java.util.Date date = format.parse(dataRow[entry.getValue()]);
                        java.sql.Date sqlDate = new Date(date.getTime());
                        ps.setDate(i, sqlDate);
                    } else {
                        // parse decimal
                        ps.setDouble(i, Double.parseDouble(dataRow[entry.getValue()]));
                    }
                    i++;
                }

                ps.addBatch();
                // if batch is ready -> commit
                if (++counter % BATCH_SIZE == 0) {
                    ps.executeBatch();
                    //"batch loaded"
                }
            }

            // insert remaining rows
            ps.executeBatch();
            conn.commit();
            conn.close();
            //"Connection closed"
        } catch (SQLException e) {
            //e.printStackTrace();
            this.statusString = "data insert failed";
            return false;
        } catch (ParseException e) {
            //e.printStackTrace();
            this.statusString = "date parsing failed";
            return false;
        }

        dbc = null; //for GC to delete connection
        this.statusString = "data successfully imported";
        return true;
    }


    /**
     * Read CSV file to ArrayList
     *
     * @param Filepath - full path to CSV file
     *
     * @return true if no exception were caught,
     *  otherwise - false
     */
    private boolean readCSV(String Filepath) {
        //var for current string
        String line;
        //reader for csv file
        BufferedReader br = null;
        //read status
        Boolean readSuccessful = false;
        //current row num
        int row = 0;

        // Read CSV file
        try {
            br = new BufferedReader(new FileReader(Filepath));
            while ((line = br.readLine()) != null) {
                if (row < headerRows) {
                    row++;
                    //skip header row
                    continue;
                }
                rowsArray.add(line);
            }
            readSuccessful = true;
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
            readSuccessful = false;
        } catch (IOException e) {
            //e.printStackTrace();
            readSuccessful = false;
        } finally {
            // Try to close buffer reader
            if (br != null) {
                try {
                    br.close();
                    // reader closed
                } catch (IOException e) {
                    //e.printStackTrace();
                    //buffer was not removed from memory
                }
            }
        }

        return readSuccessful;
    }
}
