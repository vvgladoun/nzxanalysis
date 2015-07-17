package server;

import java.util.Date;
import java.util.List;

/**
 * Methods for data access to
 * forecasts
 * (based on DB table nzx.f_forecast)
 *
 * @author Vladimir GLADUN (vvgladoun@gmail.com)
 */
public interface ForecastDAO {

    /**
     * find forecasts by company's ID
     *
     * @param id_company - company's id in data store
     * @return list of all forecasts
     */
    List<Forecast> findByCompany(int id_company);

    /**
     * find forecasts by portfolio
     *
     * @param id_portfolio - user's portfolio
     * @return list of all forecasts
     */
    List<Forecast> findByPortfolio(int id_portfolio);

    /**
     * find forecasts by method and portfolio
     *
     * @param id_method - method's id in data store
     * @param id_portfolio - user's portfolio
     * @return list of forecasts being found
     */
    List<Forecast> findByMethodPortfolio(int id_method, int id_portfolio);

    /**
     * find forecasts by company's ID and method's ID
     *
     * @param id_company - company's id in data store
     * @param id_method - method's id in data store
     * @return list of all forecasts
     */
    List<Forecast> findByCompanyMethod(int id_company, int id_method);


    /**
     * find the latest forecasts by company's ID
     *
     * @param id_company - company's id in data store
     * @return list of all forecasts
     */
    List<Forecast> findLatestByCompany(int id_company);

    /**
     * find the latest forecasts by portfolio
     *
     * @param id_portfolio - user's portfolio
     * @return list of all forecasts
     */
    List<Forecast> findLatestByPortfolio(int id_portfolio);

    /**
     * find the latest forecasts by method and portfolio
     *
     * @param id_method - method's id in data store
     * @param id_portfolio - user's portfolio
     * @return list of forecasts being found
     */
    List<Forecast> findLatestByMethodPortfolio(int id_method, int id_portfolio);

    /**
     * find the latest forecasts by company's ID and method's ID
     *
     * @param id_company - company's id in data store
     * @param id_method - method's id in data store
     * @return list of all forecasts
     */
    List<Forecast> findLatestByCompanyMethod(int id_company, int id_method);

    /**
     * get forecasts in period (for defined company)
     *
     * @param id_company - company's id in data store
     * @param id_method - method's id
     * @param dateFrom - start date of the period
     * @param dateTo - start date of the period
     *
     * @return forecasts has been found in the period
     */
    List<Forecast> findByPeriodCompanyMethod(int id_company, int id_method, Date dateFrom, Date dateTo);

    /**
     * Insert new entry to forecast data store
     *
     * @param forecast - company description
     * @return true if insert was successful
     */
    boolean insertForecast(Forecast forecast);

    /**
     * Update existed entry in forecast data store
     *
     * @param forecast - forecast description
     * @return true if update was successful
     */
    boolean updateForecast(Forecast forecast);

    /**
     * Delete entry from forecast data store
     *
     * @param forecast - forecast description
     * @return true if delete was successful
     */
    boolean deleteForecast(Forecast forecast);
}
