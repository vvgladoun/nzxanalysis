package client;

import server.Company;
import server.integration.HttpDownloadUtility;
import server.integration.JdbcCompanyDAOImpl;
import server.integration.JdbcQuotesDAOImpl;

import javax.swing.*;
import java.util.List;

/**
 * Applet to import all data
 *
 * @author Vladimir GLADUN (vvgladoun@gmail.com)
 */
public class AppImportAllData extends JApplet {

    /**
     * Get all available companies from DB
     * and download historical data
     * from the year 2000 until now
     * for each company
     */
    public static void downloadAllData(){
        //Get the list of companies
        List<Company> companies = (new JdbcCompanyDAOImpl()).findAll();
        //For each company download historical data
        if (companies == null) {
            // there are no companies
        } else {
            for (Company company : companies) {
                HttpDownloadUtility.downloadQuotesByCompany(company);
            }
        }

    }

}
