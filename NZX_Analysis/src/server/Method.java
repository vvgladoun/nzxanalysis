package server;

/**
 * (abstract) method of technical analysis
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
abstract class Method {

    // SID from database, by default = 0 (will be generated in DB)
    protected int id;
    // description of the method
    protected String description;
    // fee for operation (percent of volume)
    protected double feePercent;

    /**
     *
     * @return method's id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id - method's id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return method's description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description - method's description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return fee for a trade (percent of volume)
     */
    public double getFeePercent() {
        return feePercent;
    }

    /**
     *
     * @param feePercent - fee for a trade
     */
    public void setFeePercent(double feePercent) {
        this.feePercent = feePercent;
    }
}

