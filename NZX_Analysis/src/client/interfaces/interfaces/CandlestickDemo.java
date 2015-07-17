package client.interfaces;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.XYPlot;
//import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.xy.*;
import server.Company;
import server.Quote;
import server.integration.JdbcQuotesDAOImpl;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;

/**
 * Window for candlestick chart
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public class CandlestickDemo extends JFrame {
    private Date startDate, endDate;

    /**
     * Class constructor for candlestick chart
     *
     * @param company selected company
     * @param sDate start date
     * @param eDate end date
     */
    public CandlestickDemo(Company company, Date sDate, Date eDate) {
        super("Candlestick ");
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        startDate = sDate;
        endDate = eDate;
        DateAxis    domainAxis       = new DateAxis("Date");
        NumberAxis  rangeAxis        = new NumberAxis("Price");
        CandlestickRenderer renderer = new CandlestickRenderer(5);
        XYDataset   dataset          = getDataSet(company);

        XYPlot mainPlot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);

        //Do some setting up, see the API Doc
        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);
        domainAxis.setTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());

        //Now create the chart and chart panel
        JFreeChart chart = new JFreeChart(company.getName(), null, mainPlot, false);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 300));

        this.add(chartPanel);
        this.pack();
    }


    /**
     * Get data set by company
     *
     * @param company selected company
     * @return data set for candlestick chart
     */
    protected AbstractXYDataset getDataSet(Company company) {
        //This is the dataset we are going to create
        DefaultOHLCDataset result = null;
        //This is the data needed for the dataset
        OHLCDataItem[] data;
        //This is where we go get the data, replace with your own data source
        data = getData(company);

        //Create a dataset, an Open, High, Low, Close dataset
        result = new DefaultOHLCDataset(company.getName(), data);
        return result;
    }

    /**
     * Get data from data store
     * and format to OHLC data format
     *
     * @param company - selected company
     * @return data array for candlestick chart
     */
    protected OHLCDataItem[] getData(Company company) {
        List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();

        try {

            java.util.List<Quote> quotes = (new JdbcQuotesDAOImpl()).findByPeriod(company.getId(),
                    startDate, endDate);
            System.out.println(quotes.size());
            int i = 0;
            for(Quote qoute: quotes){
                i++;
                Date date       = qoute.getClose_date();
                double open     = qoute.getOpen_price();
                double high     = qoute.getHigh_price();
                double low      = qoute.getLow_price();
                double close    = qoute.getClose_price();
                double volume   = qoute.getVolume();

                OHLCDataItem item = new OHLCDataItem(date, open, high, low, close, volume);
                dataItems.add(item);
                //if(i==50) break;
            }
            System.out.println(i);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //Data from data store is from newest to oldest. Reverse so it is oldest to newest
        Collections.reverse(dataItems);

        //Convert the list into an array
        OHLCDataItem[] data = dataItems.toArray(new OHLCDataItem[dataItems.size()]);

        return data;
    }

}