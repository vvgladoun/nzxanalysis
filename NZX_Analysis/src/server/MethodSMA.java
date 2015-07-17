package server;

/**
 * SMA method of technical analysis
 * A simple, or arithmetic, moving average (SMA) that is calculated
 * by adding the closing price of the security
 * for a number of time periods and then dividing this total
 * by the number of time periods.
 * Short-term averages respond quickly to changes in the price
 * of the underlying, while long-term averages are slow to react.
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public final class MethodSMA extends Method {

    // number of days in short SMA
    private int shortSMA;
    // number of days in long SMA
    private int longSMA;

    /**
     * default constractor
     */
    public MethodSMA() {
        this(0, "New SMA method", 5, 10, 0.0);
    }

    /*
     * overloaded constructor
     * with all attributes
     *
     * @param id
     * @param description
     * @param shortSMA
     * @param longSMA
     * @param feePercent
     */
    public MethodSMA(int id, String description, int shortSMA, int longSMA, double feePercent) {
        this.id = id;
        this.description = description;
        this.shortSMA = shortSMA;
        this.longSMA = longSMA;
        this.feePercent = feePercent;
    }

    public int getShortSMA() {
        return shortSMA;
    }

    public void setShortSMA(int shortSMA) {
        this.shortSMA = shortSMA;
    }

    public int getLongSMA() {
        return longSMA;
    }

    public void setLongSMA(int longSMA) {
        this.longSMA = longSMA;
    }
}
