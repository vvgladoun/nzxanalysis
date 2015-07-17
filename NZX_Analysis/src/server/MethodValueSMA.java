package server;

import java.util.Date;

/**
 * Values of SMA method
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public final class MethodValueSMA extends MethodValue {

    // number of days of SMA
    private double sma_length;

    /**
     * default constructor
     */
    public MethodValueSMA(){

        this(0, 0, null, 0, 0.0);
    }

    /**
     * overloaded constructor
     * (id_company/close_date/sma_length, sma_value)
     *
     * @param id - entry's SID
     * @param id_company - company's SID
     * @param close_date - trading date
     * @param sma_length - number of days
     * @param sma_value - long SMA value
     */
    public MethodValueSMA(int id, int id_company, Date close_date,
                          int sma_length, double sma_value){

        this.id = id;
        this.id_company = id_company;
        this.close_date = close_date;
        this.sma_length = sma_length;
        this.value = sma_value;
    }

    /**
     *
     * @return number of days of SMA
     */
    public double getSma_length() {
        return sma_length;
    }

    /**
     *
     * @param sma_length - number of days of SMA
     */
    public void setSma_length(double sma_length) {
        this.sma_length = sma_length;
    }
}
