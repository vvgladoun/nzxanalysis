package server.integration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Methods to calculate results for forecasts
 * (indicator independent methods)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public final class CalculateForecasts {

    /**
     * Update result columns for forecasts
     */
    public static void checkForecasts(){
        DBConnection dbc = new DBConnection();

        //Update with 'hold' forecast
        dbc.dmlStatement("UPDATE   nzx.f_forecast  SET result = 1, succeeded = 0 " +
                "WHERE forecast = 0 AND result is null;");

        //Update with 'buy' forecast
        dbc.dmlStatement("UPDATE nzx.f_forecast\n" +
                " SET result = round(1 + ((tt.next_close - tt.next_open)/tt.next_open) , 4),\n" +
                " succeeded = (CASE WHEN tt.next_close > tt.next_open THEN 1 \n" +
                " WHEN tt.next_close < tt.next_open THEN -1 \n" +
                " ELSE 0 END)\n" +
                "\n" +
                " FROM (SELECT \n" +
                " fq.id_company,\n" +
                " fq.close_date,\n" +
                " fq.close_price,\n" +
                " min(fq.close_price) OVER (PARTITION BY fq.id_company ORDER BY close_date\n" +
                "                      ROWS BETWEEN 1 following AND 1 FOLLOWING) next_close,\n" +
                " min(fq.open_price) OVER (PARTITION BY fq.id_company ORDER BY close_date\n" +
                "                      ROWS BETWEEN 1 following AND 1 FOLLOWING) next_open\n" +
                "\n" +
                " FROM nzx.f_quotes fq) tt \n" +
                " WHERE f_forecast.id_company = tt.id_company \n" +
                " AND f_forecast.forecast_date = tt.close_date\n" +
                " AND f_forecast.forecast = 1\n" +
                " AND NOT (tt.next_close is null)\n" +
                " AND f_forecast.result is null\n");

        //Update with 'sell' forecast
        dbc.dmlStatement("UPDATE nzx.f_forecast\n" +
                " SET result = round(1 + ((tt.next_open - tt.next_close)/tt.next_close) , 4),\n" +
                " succeeded = (CASE WHEN tt.next_close > tt.next_open THEN -1 \n" +
                " WHEN tt.next_close < tt.next_open THEN 1 \n" +
                " ELSE 0 END)\n" +
                " FROM (SELECT \n" +
                " fq.id_company,\n" +
                " fq.close_date,\n" +
                " fq.close_price,\n" +
                " min(fq.close_price) OVER (PARTITION BY fq.id_company ORDER BY close_date\n" +
                "                      ROWS BETWEEN 1 following AND 1 FOLLOWING) next_close,\n" +
                " min(fq.open_price) OVER (PARTITION BY fq.id_company ORDER BY close_date\n" +
                "                      ROWS BETWEEN 1 following AND 1 FOLLOWING) next_open\n" +
                " FROM nzx.f_quotes fq) tt \n" +
                " WHERE f_forecast.id_company = tt.id_company \n" +
                " AND f_forecast.forecast_date = tt.close_date\n" +
                " AND f_forecast.forecast = -1\n" +
                " AND NOT (tt.next_close is null)\n" +
                " AND f_forecast.result is null\n");

    }


    /**
     * Calculates forecasts for over-night trading
     * (with results) by method and company
     * WARNING: data in f_forecast must be calculated
     *
     * @param id_company company's ID in data store
     * @param id_method method's ID in data store
     * @return status - true if calculated
     */
    public static boolean calculateForecastOverNight(int id_company, int id_method) {

        //calculate SMA of each length from all methods
        DBConnection dbc = new DBConnection();
        if (!dbc.isConnected()) {
            // connection to DB failed
            return false;
        }

        // delete forecast if exists
        boolean status_delete = dbc.dmlStatement("DELETE FROM nzx.f_forecast_overnight "
                + " WHERE id_method = " + id_method +
                " AND id_company = " + id_company);

        // create forecast (with results)
        boolean status_insert = dbc.dmlStatement("WITH tt AS (SELECT \n" +
                " fq.id_company, \n" +
                " fq.close_date, \n" +
                " fq.close_price, \n" +
                " min(fq.close_price) OVER (PARTITION BY fq.id_company ORDER BY close_date \n" +
                "                      ROWS BETWEEN 1 following AND 1 FOLLOWING) next_close, \n" +
                " min(fq.open_price) OVER (PARTITION BY fq.id_company ORDER BY close_date \n" +
                "                      ROWS BETWEEN 1 following AND 1 FOLLOWING) next_open \n" +
                " FROM nzx.f_quotes fq \n" +
                " WHERE id_company = " + id_company +
                " )\n" +
                " , tf AS (SELECT \n" +
                " ff.id_company,\n" +
                " ff.id_method,\n" +
                " ff.forecast_date,\n" +
                " ff.forecast,\n" +
                " min(ff.forecast) OVER (PARTITION BY ff.id_company, ff.id_method ORDER BY forecast_date DESC\n" +
                "                      ROWS BETWEEN 1 following AND 1 following) previous_forecast\n" +
                " FROM nzx.f_forecast ff\n" +
                " WHERE ff.id_method = " + id_method + " AND ff.id_company = " + id_company +
                " )\n" +
                " , trades AS (SELECT\n" +
                " tt.id_company,\n" +
                " tf.id_method,\n" +
                " tf.forecast,\n" +
                " tf.previous_forecast,\n" +
                " tt.close_date,\n" +
                " tt.next_open,\n" +
                " tt.next_close\n" +
                " FROM  tt \n" +
                " INNER JOIN tf ON \n" +
                " tf.id_company = tt.id_company \n" +
                " AND tf.forecast_date = tt.close_date\n" +
                " WHERE tf.previous_forecast <> tf.forecast\n" +
                " AND NOT (tt.next_close is null) )\n" +
                " ,results as (\n" +
                " SELECT\n" +
                " tr.id_company,\n" +
                " tr.id_method,\n" +
                " tr.forecast,\n" +
                " tr.close_date forecast_date,\n" +
                " tr.next_open open_price,\n" +
                " min(tr.next_open) OVER (PARTITION BY tr.id_company, tr.id_method ORDER BY tr.close_date\n" +
                " ROWS BETWEEN 1 following AND 1 FOLLOWING) close_price\n" +
                " FROM trades tr\n" +
                " ) " +
                " INSERT INTO nzx.f_forecast_overnight \n" +
                " (id_company, id_method, forecast_date, forecast, result, succeeded)\n" +
                " SELECT \n" +
                " r.id_company, r.id_method, r.forecast_date, r.forecast, \n" +
                " CASE WHEN r.forecast = 0 THEN 1 \n" +
                " WHEN r.forecast = 1 THEN (CASE WHEN r.close_price is null THEN 1 ELSE 1 + round((r.close_price - r.open_price)/r.open_price, 4) END)\n" +
                " ELSE (CASE WHEN r.close_price is null THEN 1 ELSE 1 + round((r.open_price - r.close_price)/r.close_price,4) END) END result,\n" +

                " CASE WHEN (r.close_price IS NULL) THEN 0\n" +
                " WHEN (r.close_price = r.open_price) OR (r.forecast = 0) THEN 0\n" +
                " WHEN r.close_price > r.open_price THEN (CASE WHEN r.forecast = 1 THEN 1 ELSE -1 END)\n" +
                " ELSE (CASE WHEN r.forecast = 1 THEN -1 ELSE -1 END) END succeeded\n" +
                " FROM results r");

        return status_delete && status_insert;
    }

    /**
     * Calculates forecasts for over-night trading
     * (with results) for all methods and companies
     * WARNING: data in f_forecast must be calculated
     *
     * @return status - true if calculated
     */
    public static boolean calculateAllForecastsOverNight() {

        //calculate SMA of each length from all methods
        DBConnection dbc = new DBConnection();
        if (!dbc.isConnected()) {
            // connection to DB failed
            return false;
        }

        // delete forecast if exists
        boolean status_delete = dbc.dmlStatement("TRUNCATE TABLE nzx.f_forecast_overnight; ");

        // create forecast (with results)
        boolean status_insert = dbc.dmlStatement("WITH tt AS (SELECT \n" +
                " fq.id_company, \n" +
                " fq.close_date, \n" +
                " fq.close_price, \n" +
                " min(fq.close_price) OVER (PARTITION BY fq.id_company ORDER BY close_date \n" +
                "                      ROWS BETWEEN 1 following AND 1 FOLLOWING) next_close, \n" +
                " min(fq.open_price) OVER (PARTITION BY fq.id_company ORDER BY close_date \n" +
                "                      ROWS BETWEEN 1 following AND 1 FOLLOWING) next_open \n" +
                " FROM nzx.f_quotes fq \n" +

                " ) " +
                " , tf AS (SELECT \n" +
                " ff.id_company,\n" +
                " ff.id_method,\n" +
                " ff.forecast_date,\n" +
                " ff.forecast,\n" +
                " min(ff.forecast) OVER (PARTITION BY ff.id_company, ff.id_method ORDER BY forecast_date DESC\n" +
                "                      ROWS BETWEEN 1 following AND 1 following) previous_forecast\n" +
                " FROM nzx.f_forecast ff\n" +
                " )\n" +
                " , trades AS (SELECT\n" +
                " tt.id_company,\n" +
                " tf.id_method,\n" +
                " tf.forecast * (-1) forecast,\n" +
                " tf.previous_forecast * (-1) previous_forecast,\n" +
                " tt.close_date,\n" +
                " tt.next_open,\n" +
                " tt.next_close\n" +
                " FROM  tt \n" +
                " INNER JOIN tf ON \n" +
                " tf.id_company = tt.id_company \n" +
                " AND tf.forecast_date = tt.close_date\n" +
                " WHERE tf.previous_forecast <> tf.forecast\n" +
                " AND NOT (tt.next_close is null) )\n" +
                " ,results as (\n" +
                " SELECT\n" +
                " tr.id_company,\n" +
                " tr.id_method,\n" +
                " tr.forecast,\n" +
                " tr.close_date forecast_date,\n" +
                " tr.next_open open_price,\n" +
                " min(tr.next_open) OVER (PARTITION BY tr.id_company, tr.id_method ORDER BY tr.close_date\n" +
                "                      ROWS BETWEEN 1 following AND 1 FOLLOWING) close_price\n" +
                " FROM trades tr\n" +
                " ) \n" +
                " INSERT INTO nzx.f_forecast_overnight \n" +
                " (id_company, id_method, forecast_date, forecast, result, succeeded)\n" +
                " SELECT \n" +
                " r.id_company, r.id_method, r.forecast_date, r.forecast, \n" +
                " CASE WHEN r.forecast = 0 THEN 1 \n" +
                " WHEN r.forecast = 1 THEN (CASE WHEN r.close_price is null THEN 1 ELSE 1 + round((r.close_price - r.open_price)/r.open_price, 4) END)\n" +
                " ELSE (CASE WHEN r.close_price is null THEN 1 ELSE 1 + round((r.open_price - r.close_price)/r.close_price,4) END) END result,\n" +
                " CASE WHEN (r.close_price IS NULL) THEN 0\n" +
                " WHEN (r.close_price = r.open_price) OR (r.forecast = 0) THEN 0\n" +
                " WHEN r.close_price > r.open_price THEN (CASE WHEN r.forecast = 1 THEN 1 ELSE -1 END)\n" +
                " ELSE (CASE WHEN r.forecast = 1 THEN -1 ELSE 1 END) END succeeded\n" +
                " FROM results r");

        return status_delete && status_insert;
    }
}
