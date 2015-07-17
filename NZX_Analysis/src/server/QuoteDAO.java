package server;

import java.util.Date;
import java.util.List;

/**
 * Methods for data access to
 * NZX quotes
 * (based on DB table nzx.f_quotes)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public interface QuoteDAO {
    /**
     * Get all quotes (for defined company)
     * @return list of all quotes
     */
    List<Quote> findAll(int id_company);

    /**
     * get quotes on date (for defined company)
     *
     * @param id_company - company's id in data store
     * @param date - date of the quotes
     * @return quotes has been found by date
     */
    List<Quote> findByDate(int id_company, Date date);

    /**
     * get quotes in period (for defined company)
     *
     * @param id_company - company's id in data store
     * @param dateFrom - start date of the period
     * @param dateTo - start date of the period
     *
     * @return quotes has been found in the period
     */
    List<Quote> findByPeriod(int id_company, Date dateFrom, Date dateTo);

    /**
     * Insert new entry to quote datastore
     *
     * @param quote - company description
     * @return true if insert was successful
     */
    boolean insertQuote(Quote quote);

    /**
     * Update existed entry in quote datastore
     *
     * @param quote - quote description
     * @return true if update was successful
     */
    boolean updateQuote(Quote quote);

    /**
     * Delete entry from quote datastore
     *
     * @param quote - quote description
     * @return true if delete was successful
     */
    boolean deleteQuote(Quote quote);
}
