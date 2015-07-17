package client.interfaces;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.XYPlot;
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

public class CandlestickDemo extends JFrame {
    private Date startDate, endDate;

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
    //This method uses yahoo finance to get the OHLC data
    protected OHLCDataItem[] getData(Company company) {
        List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();

        try {

//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.YEAR, 2014);
//            calendar.set(Calendar.DAY_OF_MONTH, 1);
//            calendar.set(Calendar.MONTH, 0);
//            //java.sql.Date startDate = new java.sql.Date(calendar.getTime().getTime());
//            Date startDate = new Date(calendar.getTime().getTime());
//            System.out.println(startDate);
//            calendar.set(Calendar.YEAR, 2014);
//            calendar.set(Calendar.DAY_OF_MONTH, 1);
//            calendar.set(Calendar.MONTH, 1);
//            java.sql.Date endDate = new java.sql.Date(calendar.getTime().getTime());

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
        //Data from Yahoo is from newest to oldest. Reverse so it is oldest to newest
        Collections.reverse(dataItems);

        //Convert the list into an array
        OHLCDataItem[] data = dataItems.toArray(new OHLCDataItem[dataItems.size()]);

        return data;
    }

}