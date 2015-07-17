package server;

import java.util.Date;

/**
 * Quotes of NZX's company
 * (based on DB table nzx.f_quotes)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public final class Quote {

    // SID from database, by default = 0 (will be generated in DB)
    private int id = 0;
    // SID of the company
    private int id_company;
    // date of trade
    private Date close_date;
    // prices: open (start of trades)
    private double open_price;
    //  maximum price during the day
    private double high_price;
    //  minimum price during the day
    private double low_price;
    // close (end of trades)
    private double close_price;
    // adjusted close price
    private double adjusted_close;

    // volume of securities being trade
    private double volume;

    /**
     * Default constructor for quotes
     */
    public Quote(){
        // create 'empty' quote
        this(0, 0, null, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    /**
     * Overloaded constructor for quotes
     * with all attributes
     *
     * @param id
     * @param id_company
     * @param close_date
     * @param open_price
     * @param high_price
     * @param low_price
     * @param close_price
     * @param volume
     * @param adjusted_close
     */
    public Quote(int id, int id_company, Date close_date,
                 double open_price, double high_price, double low_price,
                 double close_price, double volume, double adjusted_close){

        this.id = id;
        this.id_company = id_company;
        this.close_date = close_date;
        this.open_price = open_price;
        this.high_price = high_price;
        this.low_price = low_price;
        this.close_price = close_price;
        this.adjusted_close = adjusted_close;
        this.volume = volume;

    }

    /**
     *
     * @return adjusted close price (numeric)
     */
    public double getAdjusted_close() {
        return adjusted_close;
    }

    /**
     *
     * @param adjusted_close adjusted close price
     */
    public void setAdjusted_close(double adjusted_close) {
        this.adjusted_close = adjusted_close;
    }

    /**
     *
     * @return volume (total of the daily trade)
     */
    public double getVolume() {
        return volume;
    }

    /**
     *
     * @param volume total of the daily trade
     */
    public void setVolume(double volume) {
        this.volume = volume;
    }

    /**
     *
     * @return price on close of daily trades
     */
    public double getClose_price() {
        return close_price;
    }

    /**
     *
     * @param close_price price on close
     *                    of daily trades
     */
    public void setClose_price(double close_price) {
        this.close_price = close_price;
    }

    /**
     *
     * @return the lowest price of daily trades
     */
    public double getLow_price() {
        return low_price;
    }

    /**
     *
     * @param low_price the lowest price
     *                  of daily trades
     */
    public void setLow_price(double low_price) {
        this.low_price = low_price;
    }

    /**
     *
     * @return the highest price of daily trades
     */
    public double getHigh_price() {
        return high_price;
    }

    /**
     *
     * @param high_price - the highest price of daily trades
     */
    public void setHigh_price(double high_price) {
        this.high_price = high_price;
    }

    /**
     *
     * @return price on start
     *                    of daily trades
     */
    public double getOpen_price() {
        return open_price;
    }

    /**
     *
     * @param open_price price on close
     *                    of daily trades
     */
    public void setOpen_price(double open_price) {
        this.open_price = open_price;
    }

    /**
     *
     * @return date of trades
     */
    public Date getClose_date() {
        return close_date;
    }

    /**
     *
     * @param close_date - date of trades
     */
    public void setClose_date(Date close_date) {
        this.close_date = close_date;
    }

    /**
     *
     * @return Company's SID in data store
     */
    public int getId_company() {
        return id_company;
    }

    /**
     *
     * @param id_company Company's SID in data store
     */
    public void setId_company(int id_company) {
        this.id_company = id_company;
    }

    /**
     *
     * @return quote's SID in data store
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id quote's SID in data store
     */
    public void setId(int id) {
        this.id = id;
    }

}
