package server.integration;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import server.Company;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Utility to download files from web
 * (geberate URL, download file etc)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public final class HttpDownloadUtility extends Thread{

    // buffer size to read file from web
    private static final int BUFFER_SIZE = 4096;
    // default temp directory to save downloaded file
    private static final String DEFAULT_TEMP_DIR = "src/";


    // for progress bar
    private static int downloaded = 0;
    private static String currentCompany = "";

    /**
     *
     * @return num of downloaded
     */
    public static int getDownloaded() {
        return downloaded;
    }

    /**
     *
     * @param downloaded num of downloaded companies
     */
    public static void setDownloaded(int downloaded) {
        HttpDownloadUtility.downloaded = downloaded;
    }

    /**
     *
     * @return string name of loading company
     */
    public static String getCurrentCompany() {
        return currentCompany;
    }

    /**
     *
     * @param currentCompany string name of loading company
     */
    public static void setCurrentCompany(String currentCompany) {
        HttpDownloadUtility.currentCompany = currentCompany;
    }

    /**
     * Downloads a file from a URL
     * @param fileURL URL to download file
     * @param tempDir directory to save the file
     * @param fileName name of the saved file
     *
     * @throws IOException
     */
    public static void downloadFile(String fileURL, String tempDir, String fileName)
            throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = tempDir + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();

            //"File downloaded"
        } else {
            //"No file to download. Server replied HTTP code: " + responseCode
        }
        httpConn.disconnect();
    }

    /**
     * Create URL to download CSV with company's historical data
     * from date from to date to
     *
     * @param companyCode - code of the company
     * @param yearFrom - year in date from
     * @param monthFrom - month in date from
     * @param dayFrom - day in date from
     * @param yearTo - year in date to
     * @param monthTo - month in date to
     * @param dayTo - day in date to
     * @return URL to download CSV file
     */
    private static String generateQuotesURL(String companyCode, int yearFrom, int monthFrom, int dayFrom,
                    int yearTo, int monthTo, int dayTo) {
        //generate URL for yahoo finance
        String fileURL = "http://real-chart.finance.yahoo.com/table.csv?s=" +
                companyCode + ".NZ&a=" + String.format("%02d",(monthFrom-1))+ "&b=" + dayFrom +
                "&c=" + yearFrom + "&d=" + String.format("%02d",(monthTo-1)) + "&e=" + dayTo +
                "&f=" + yearTo + "&g=d&ignore=.csv";

        return fileURL;
    }

    /**
     * Download available historical data
     * from the year 2000 to current date
     *
     * @param companyCode - code of the company
     * @param tempDir - path of the temporal folder
     * @return status - true if file was downloaded successfully
     */
    public static boolean saveHistoricalDataUntilNow(String companyCode, String tempDir) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2000);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, 1);

        return saveHistoricalDataInPeriod(companyCode, tempDir, calendar.getTime(), new Date());

    }

    /**
     * Download available historical data
     * for NZX company in defined period
     *
     * @param companyCode - code of the company
     * @param tempDir - path of the temporal folder
     * @param dateFrom - start date of the period
     * @param dateTo - end date of the period
     * @return status - true if file was downloaded successfully
     */
    public static boolean saveHistoricalDataInPeriod(String companyCode,
                                                  String tempDir, Date dateFrom, Date dateTo) {

        //current status: true if file was downloaded successful
        boolean downloaded = false;
        //calendar object for date parsing
        Calendar cal = Calendar.getInstance();

        //parse date from
        cal.setTime(dateFrom);
        int yearFrom = cal.get(Calendar.YEAR);
        int monthFrom = cal.get(Calendar.MONTH) + 1;
        int dayFrom = cal.get(Calendar.DAY_OF_MONTH);

        //parse date to
        cal.setTime(dateTo);
        int yearTo = cal.get(Calendar.YEAR);
        int monthTo = cal.get(Calendar.MONTH) + 1;
        int dayTo = cal.get(Calendar.DAY_OF_MONTH);

        //generate URL
        String urlNZX = generateQuotesURL(companyCode, yearFrom, monthFrom,
                dayFrom, yearTo, monthTo, dayTo);

        //download quotes
        try {
            downloadFile(urlNZX, tempDir, companyCode);
            downloaded = true;
        } catch (IOException ex) {
            //ex.printStackTrace();
        }

        return downloaded;
    }



    /**
     * Get all available companies from DB
     * and download historical data
     * from the year 2000 until now
     * for each company
     */
    public static void downloadAllData(){
        downloaded = 0;
        //Get the list of companies
        List<Company> companies = (new JdbcCompanyDAOImpl()).findAll();
        //For each company download historical data
        if (companies == null) {
            // there are no companies
        } else {
            for (Company company : companies) {
                downloadQuotesByCompany(company);
                downloaded++;
                currentCompany = company.getName();
            }
        }

    }

    /**
     * Extra method to download quotes by company
     *
     * @param company company object
     * @return
     */
    public static synchronized boolean downloadQuotesByCompany(Company company){
        String code = company.getCode();
        int id = company.getId();

        if (HttpDownloadUtility.saveHistoricalDataUntilNow(code, DEFAULT_TEMP_DIR)) {
            CSVReader quotesReader = new CSVReader(DEFAULT_TEMP_DIR + code);
            quotesReader.setHeaderRows(1);
            quotesReader.setId_company(id);

            boolean import_status = quotesReader.importQuote();
            //try to delete temp file
            try {
                quotesReader = null;
                (new File(DEFAULT_TEMP_DIR + code)).delete();
            } catch (Exception e) {
                //cannot delete temp file
            }
            return import_status;
        }

        return false;
    }

    /**
     * Extra method to download quotes by company's code
     *
     * @param code company's code
     * @return status, true if downloaded without errors
     */
    public static boolean downloadQuotesByCode(String code){

        List<Company> companies = (new JdbcCompanyDAOImpl()).findByCode(code);
        if (companies == null) {
            // no company was found by id
            return false;
        }
        // get company from result and get its id
        return downloadQuotesByCompany(companies.get(0));
    }

    /**
     * Try to find and get company's description
     * from parsed (using JSoup open source lib) NZX web page
     *
     * @param code company's code
     * @return description
     */
    public static String getCompanyDescriptionByCode(String code) {

        String description = " ";
        //Document doc = null;
        try {
            Document doc = Jsoup.connect("https://www.nzx.com/markets/NZSX/securities/" + code + "/analysis").get();
            Element links = doc.select("article").first();
            Element linksText = links.select("p").first();

            description = linksText.text();
        } catch (IOException e) {
            //e.printStackTrace();
        } catch (Exception e) {
            //e.printStackTrace();
        }

        return description;
    }

    /**
     * Try to update descriptions for all companies
     */
     public static void updateCompaniesDescription(){
         //for each company from database update description
         // to result of getCompanyDescriptionByCode()
         for (Company comp : (new JdbcCompanyDAOImpl()).findAll()) {
             String desc = getCompanyDescriptionByCode(comp.getCode());
             comp.setDescription(desc);
             //update company in DB
             (new JdbcCompanyDAOImpl()).updateCompany(comp);
         }
     }
}