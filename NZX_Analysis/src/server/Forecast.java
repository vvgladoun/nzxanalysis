package server;

import java.util.Date;

/**
 * Forecast for NZX's company
 * (based on DB table nzx.f_quotes)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public final class Forecast {

    // SID from database, by default = 0 (will be generated in DB)
    private int id = 0;
    // SID of the company
    private int id_company;
    // SID of the method
    private int id_method;
    // date of trade (forecast)
    private Date forecast_date;
    // forecast: -1 sell; 0 hold; 1 buy
    private int forecast;
    // result (multiplier, for 5% will be 1,05)
    private double result;
    // succeeded flag: 0 - failed, 1 - success
    private int succeeded;

    /**
     * default constructor
     */
    public Forecast() {
        this(0, 0, 0, null, 0, 0.0, 1);
    }

    /**
     * Overloaded constructor
     * with all attributes
     *
     * @param id_company - company's SID
     * @param id_method - method's SID
     * @param forecast_date - date of trade (forecasted)
     * @param forecast - forecast: -1 sell; 0 hold; 1 buy
     * @param result - multiplier for origin sum
     * @param succeeded - flag: -1 false, 0 none (unknown),  1 true
     */
    public Forecast(int id, int id_company, int id_method, Date forecast_date,
                     int forecast, double result, int succeeded){

        this.id = id;
        this.id_company = id_company;
        this.id_method = id_method;
        this.forecast_date = forecast_date;
        this.forecast = forecast;
        this.result = result;
        this.succeeded = succeeded;
    }

    /**
     *
     * @return entry SID
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id entry SID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return company's SID
     */
    public int getId_company() {
        return id_company;
    }

    /**
     *
     * @param id_company company's SID
     */
    public void setId_company(int id_company) {
        this.id_company = id_company;
    }

    /**
     *
     * @return company's SID
     */
    public int getId_method() {
        return id_method;
    }

    /**
     *
     * @param id_method method's SID
     */
    public void setId_method(int id_method) {
        this.id_method = id_method;
    }

    /**
     *
     * @return forecast date
     */
    public Date getForecast_date() {
        return forecast_date;
    }

    /**
     *
     * @param forecast_date date of forecast
     */
    public void setForecast_date(Date forecast_date) {
        this.forecast_date = forecast_date;
    }

    /**
     *
     * @return forecast (sell/hold/buy -1/0/1)
     */
    public int getForecast() {
        return forecast;
    }

    /**
     *
     * @param forecast (sell/hold/buy -1/0/1)
     */
    public void setForecast(int forecast) {
        this.forecast = forecast;
    }

    /**
     *
     * @return multiplier for origin amount
     */
    public double getResult() {
        return result;
    }

    /**
     *
     * @param result multiplier for origin amount
     */
    public void setResult(double result) {
        this.result = result;
    }

    /**
     *
     * @return flag of success
     */
    public int getSucceeded() {
        return succeeded;
    }

    /**
     *
     * @param succeeded flag: 0-false, 1-true
     */
    public void setSucceeded(int succeeded) {
        this.succeeded = succeeded;
    }
}
