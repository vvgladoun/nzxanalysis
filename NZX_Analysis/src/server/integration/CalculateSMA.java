package server.integration;

import server.MethodSMA;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Methods to calculate forecasts
 * and analytics for SMA indicators
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public final class CalculateSMA {

    /**
     * Calculate and insert into nzx.f_method_sma_values
     * values of SMA
     *
     * @param sma_length length (number of days) of SMA
     * @param whereStatement where statement (to limit cqalculation)
     * @return status - true if calculated without errors
     */
    public static boolean calculateSMA(int sma_length, String whereStatement) {

        //Delete existing values
        DBConnection dbc = new DBConnection();
        boolean deleteStatus = dbc.dmlStatement("DELETE FROM nzx.f_method_sma_values "
                + " WHERE sma_length = " + sma_length +
                (whereStatement == "" ? " " : " AND ") + whereStatement);

        //Calculate and insert values
        boolean calculateStatus = dbc.dmlStatement("INSERT INTO nzx.f_method_sma_values (" +
                " close_date, id_company, sma_length, sma_value) " +
                " SELECT close_date, id_company,  sma_length, sma_value FROM (SELECT close_date, " +
                " id_company, " + sma_length + " sma_length, " +
                " ROUND((avg(close_price) OVER (PARTITION BY id_company ORDER BY close_date DESC " +
                " ROWS BETWEEN 0 PRECEDING AND " + (sma_length-1) + " FOLLOWING)), 6)  sma_value, " +
                " COUNT(1) OVER (PARTITION BY id_company ORDER BY close_date DESC " +
                " ROWS BETWEEN 0 PRECEDING AND " + (sma_length-1) + " FOLLOWING) sma_count " +
                " FROM nzx.f_quotes) t where t.sma_count = " + sma_length +
                (whereStatement == "" ? " " : " AND ") + whereStatement + " ; ");

        // return final status (delete + insert)
        return deleteStatus && calculateStatus;

    }

    /**
     * Calculates SMA only for new period
     *
     * @param sma_length length (number of days) of SMA
     * @return status - true if calculated without errors
     */
    public static boolean calculateSMA_incremental(int sma_length){
        String whereStatement = " close_date > COALESCE((SELECT MAX(close_date) FROM " +
                "nzx.f_method_sma_values WHERE sma_length = " +
                sma_length + " ), date('1900-01-01')) ";

        return calculateSMA(sma_length, whereStatement);
    }

    /**
     * Calculate values for short and long SMA
     * @param method - SMA method for calculation
     */
    public static void calculateMethodsSMA(MethodSMA method) {
        // calculate values
        calculateSMA_incremental(method.getShortSMA());
        calculateSMA_incremental(method.getLongSMA());
    }

    /**
     * Make forecast (for the next trading day)
     *
     * @param method - method SMA
     * @param updateSMAValues - flag for calculating SMA values
     * @return
     */
    public static boolean makeForecastSMA(MethodSMA method, boolean updateSMAValues){

        int id_method = method.getId();
        if (id_method == 0) {
            // id_method must not be null (as a foreign key)
            return false;
        }

        // calculate SMA values
        if (updateSMAValues) {
            calculateMethodsSMA(method);
        }

        // delete forecast if exists
        DBConnection dbc = new DBConnection();
        boolean deleteStatus = dbc.dmlStatement("DELETE FROM nzx.f_forecast "
                + " WHERE id_method = " + id_method);

        // create connection and return status of
        // query to calculate forecast (if SMA values exists)
        boolean calculateStatus = dbc.dmlStatement("INSERT INTO nzx.f_forecast ( " +
                "id_company, id_method, forecast_date, forecast) " +
                "SELECT fq.id_company, " + id_method + " id_method, fq.close_date forecast_date, " +
                "CASE WHEN fm1.sma_value > fm2.sma_value THEN 1 " +
                " WHEN fm1.sma_value < fm2.sma_value THEN -1 " +
                " ELSE CASE WHEN fm1.sma_value < fq.close_price THEN 1 " +
                " WHEN fm1.sma_value > fq.close_price THEN -1 " +
                " ELSE 0 END END forecast FROM nzx.f_quotes fq " +
                "INNER JOIN nzx.f_method_sma_values fm1 ON fm1.id_company = fq.id_company " +
                "AND fm1.close_date = fq.close_date AND fm1.sma_length = " + method.getShortSMA() +
                " INNER JOIN nzx.f_method_sma_values fm2 ON fm2.id_company = fq.id_company " +
                " AND fm2.close_date = fq.close_date AND fm2.sma_length = " + method.getLongSMA());

        return deleteStatus && calculateStatus;
    }

    /**
     * Make forecasts for all methods
     */
    public static void makeAllForecastsSMA(boolean calculateSMA) {
        //calculate SMA of each length from all methods
        DBConnection dbc = new DBConnection();
        if (!dbc.isConnected()) {
            // connection to DB failed
            return;
        }

        //calculate all SMA's values
        if (calculateSMA) {
            // connection to DB
            Connection conn = dbc.getDbConnect();
            // select statement
            Statement stmt = null;
            try {

                stmt = conn.createStatement();

                ResultSet rs = stmt.executeQuery("SELECT DISTINCT first_ma sma " +
                        "FROM nzx.d_method_sma " +
                        "UNION " +
                        "SELECT DISTINCT second_ma " +
                        "FROM nzx.d_method_sma ;");
                while (rs.next()) {
                    //SMA values calculation
                    calculateSMA_incremental(rs.getInt("sma"));
                }
                rs.close();
                stmt.close();
                conn.close();
                dbc = null;
            } catch (SQLException e) {
                //e.printStackTrace();
                // sql error
                dbc = null;
            }
        }

        //make forecasts
        for (MethodSMA method : (new JdbcMethodSmaDAOImpl()).findAll()) {
            makeForecastSMA(method, false);
        }

    }


}
