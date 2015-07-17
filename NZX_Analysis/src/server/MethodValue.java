package server;

import java.util.Date;

/**
 * (abstract) values of method of technical analysis
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
abstract class MethodValue {

    // SID from database, by default = 0
    // (will be generated in DB)
    protected int id;
    // SID of the company
    protected int id_company;
    // date of trade
    protected Date close_date;
    // value
    protected double value;

    /**
     *
     * @return entry's id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id - entry's id
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
     * @return trading date
     */
    public Date getClose_date() {
        return close_date;
    }

    /**
     *
     * @param close_date trading date
     */
    public void setClose_date(Date close_date) {
        this.close_date = close_date;
    }

    /**
     *
     * @return value of the method attribute on date
     */
    public double getValue() {
        return value;
    }

    /**
     *
     * @param value - value of the method
     *              attribute on date
     */
    public void setValue(double value) {
        this.value = value;
    }
}

