package server;

import server.integration.*;

import java.util.ArrayList;
import java.util.Date;


/**
 * TO DELETE!!!
 * Created by vladimirgladun on 12/05/15.
 */
public class TestDownloadURL {

    // ArrayList for data from csv
    private ArrayList<String> rowsArray = new ArrayList<String>();


    public static void main(String[] args) {

        System.out.println("Started: " + (new Date()));

//        //re-create DB: (user, password, db_name)
//        DBConnection dbc = new DBConnection("postgres", "27513095", "postgres");
//        System.out.println(dbc.getStatus());
//        dbc.createDB();
//        System.out.println(dbc.getStatus());
//        dbc.closeConnection();
//        System.out.println(dbc.getStatus());

//        // read list of companies from CSV and insert into DB
//        // using batch load
//        CSVReader reader = new CSVReader("src/files/companies.csv");
//        reader.importCompany();
//        System.out.println(reader.getStatusString());
//
//
//        //download all quotes
//        HttpDownloadUtility.downloadAllData();
//        System.out.println("Quotes downloaded: " + (new Date()));
//
//        //make forecasts
//        CalculateSMA.makeAllForecastsSMA(true);
//        System.out.println("Forecasts made: " + (new Date()));
//
//        //check all forecasts
//        CalculateForecasts.checkForecasts();
//        System.out.println("Forecasts checked: " + (new Date()));
//
//        //calculate for over-night trading
//        CalculateForecasts.calculateAllForecastsOverNight();
//        System.out.println("Overnight trades calculated: " + (new Date()));


        System.out.println("Finished: " + (new Date()));
    }

}
