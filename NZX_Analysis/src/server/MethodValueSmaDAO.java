package server;

import java.util.Date;
import java.util.List;

/**
 * Methods for data access to
 * values of SMA's
 * (based on DB table nzx.f_method_sma_values)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public interface MethodValueSmaDAO {

    /**
     * Get all quotes (for defined company)
     *
     * @param id_company - company's id in data store
     * @return list of all values
     */
    List<MethodValueSMA> findAll(int id_company);

    /**
     * Get all quotes (for defined company and length)
     *
     * @param id_company - company's id in data store
     * @param sma_length - number of days of SMA
     * @return list of all values
     */
    List<MethodValueSMA> findAllByLength(int id_company, int sma_length);

    /**
     * get quotes on date (for defined company and length)
     *
     * @param id_company - company's id in data store
     * @param sma_length - number of days of SMA
     * @param date - date of the quotes
     * @return values has been found by date
     */
    List<MethodValueSMA> findByDateLength(int id_company, int sma_length, Date date);

    /**
     * get quotes in period (for defined company and length)
     *
     * @param id_company - company's id in data store
     * @param sma_length - number of days of SMA
     * @param dateFrom - start date of the period
     * @param dateTo - start date of the period
     *
     * @return quotes has been found in the period
     */
    List<MethodValueSMA> findByPeriodLength(int id_company, int sma_length, Date dateFrom, Date dateTo);

    /**
     * Insert new entry to SMA values datastore
     *
     * @param valueSMA - SMA value description
     * @return true if insert was successful
     */
    boolean insertMethodValueSMA(MethodValueSMA valueSMA);

    /**
     * Update existed entry in SMA values datastore
     *
     * @param valueSMA - SMA value description
     * @return true if update was successful
     */
    boolean updateMethodValueSMA(MethodValueSMA valueSMA);

    /**
     * Delete entry from SMA values datastore
     *
     * @param valueSMA - SMA value description
     * @return true if delete was successful
     */
    boolean deleteMethodValueSMA(MethodValueSMA valueSMA);
}
