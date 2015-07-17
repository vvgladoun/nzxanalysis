package server.integration;

import server.Forecast;
import server.ForecastDAO;
import server.MethodSMA;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JDBC implementation of DAO for Forecast
 *  (integration with DB table nzx.f_forecast)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public final class JdbcForecastDAOImpl implements ForecastDAO {

    /**
     * Select statement generator for forecasts
     *
     * @param whereStatement - where clause
     * @param latest - flag, if true get only latest data
     * @return list of forecasts
     */
    private List<Forecast> selectForecast(String whereStatement, boolean latest) {

        List<Forecast> forecastList = new ArrayList<Forecast>();

        DBConnection dbc = new DBConnection();
        if (!dbc.isConnected()) {
            // connection to DB failed
            return forecastList; //empty list
        }
        // connection to DB
        Connection conn = dbc.getDbConnect();
        // select statement
        Statement stmt = null;
        try {

            stmt = conn.createStatement();
            String selectStmt = "";
            if (latest) {
                selectStmt = "SELECT f.id, f.id_company, " +
                        "f.id_method, f.forecast_date, f.forecast, f.result,  " +
                        "f.succeeded FROM nzx.f_forecast f INNER JOIN ( " +
                        "SELECT MAX(forecast_date) forecast_date, id_method, id_company " +
                        "FROM nzx.f_forecast " + whereStatement + " GROUP BY id_method," +
                        " id_company ) t ON f.forecast_date = t.forecast_date AND " +
                        "t.id_method = f.id_method AND t.id_company = f.id_company " +
                        " ORDER BY f.forecast_date DESC, f.id_company DESC  ;";
            } else {
                selectStmt = "SELECT id, id_company, " +
                        "id_method, forecast_date, forecast, result,  " +
                        "succeeded FROM nzx.f_forecast "
                        + whereStatement + " ORDER BY forecast_date desc, id_company;";
            }

            ResultSet rs = stmt.executeQuery(selectStmt);
            while (rs.next()) {
                int id = rs.getInt("id");
                int id_company = rs.getInt("id_company");
                int id_method = rs.getInt("id_method");
                Date forecast_date = rs.getDate("forecast_date");
                int forecast_num = rs.getInt("forecast");
                Double result = rs.getDouble("result");
                int succeeded = rs.getInt("succeeded");

                Forecast forecast = new Forecast(id, id_company, id_method,
                        forecast_date, forecast_num, result, succeeded);
                forecastList.add(forecast);
            }
            rs.close();
            stmt.close();
            conn.close();
            dbc = null; //force GC
        } catch (SQLException e) {
            //e.printStackTrace();
            // sql error
            dbc = null; //force GC
        }

        return forecastList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Forecast> findByCompany(int id_company) {
        return selectForecast(" WHERE id_company = " + id_company, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Forecast> findByPortfolio(int id_portfolio) {
        return selectForecast(" WHERE id_company in (SELECT id_company " +
                "FROM nzx.f_portfolio WHERE id_portfolio = "
                + id_portfolio + ") ", false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Forecast> findByMethodPortfolio(int id_method, int id_portfolio) {
        return selectForecast(" WHERE id_company in (SELECT id_company " +
                "FROM nzx.f_portfolio WHERE id_portfolio = " + id_portfolio + ") " +
                " AND id_method = " + id_method, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Forecast> findByCompanyMethod(int id_company, int id_method) {
        return selectForecast(" WHERE id_company  = " + id_company +
                " AND id_method = " + id_method, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Forecast> findLatestByCompany(int id_company) {
        return selectForecast(" WHERE id_company = " + id_company, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Forecast> findLatestByPortfolio(int id_portfolio) {
        return selectForecast(" WHERE id_company in (SELECT id_company " +
                "FROM nzx.f_portfolio WHERE id_portfolio = "
                + id_portfolio + ") ", true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Forecast> findLatestByMethodPortfolio(int id_method, int id_portfolio) {
        return selectForecast(" WHERE id_company in (SELECT id_company " +
                "FROM nzx.f_portfolio WHERE id_portfolio = " + id_portfolio + ") " +
                " AND id_method = " + id_method, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Forecast> findLatestByCompanyMethod(int id_company, int id_method) {
        return selectForecast(" WHERE id_company  = " + id_company +
                " AND id_method = " + id_method, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Forecast> findByPeriodCompanyMethod(int id_company, int id_method, Date dateFrom, Date dateTo) {
        return selectForecast(" WHERE id_company = " + id_company + " and forecast_date between '"
                + DBConnection.formatDateForQuery(dateFrom) + "' and '"
                + DBConnection.formatDateForQuery(dateTo) + "' ", false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insertForecast(Forecast forecast) {
        // create connection and return status of
        // insert statement
        DBConnection dbc = new DBConnection();
        return dbc.dmlStatement("INSERT INTO nzx.f_forecast (id_company, id_method, " +
                "forecast_date, forecast, result, succeeded  ) VALUES ( " + forecast.getId_company() +
                ", " + forecast.getId_method() + ", '" +
                DBConnection.formatDateForQuery(forecast.getForecast_date()) +
                "', " + forecast.getForecast() + ", " + forecast.getResult() +
                ", " + forecast.getSucceeded() + ")");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateForecast(Forecast forecast) {
        if (forecast.getId() == 0) {
            // new entry, cannot update, must insert first!
            return false;
        }
        // create connection and return status of
        // update statement
        DBConnection dbc = new DBConnection();
        return dbc.dmlStatement("UPDATE nzx.f_forecast SET id_company = " + forecast.getId_company()
                + ", forecast_date = '" + DBConnection.formatDateForQuery(forecast.getForecast_date())
                + "', id_method = " + forecast.getId_method()
                + ", forecast = " + forecast.getForecast()
                + ", result = " + forecast.getResult()
                + ", succeeded = " + forecast.getSucceeded()
                + " WHERE id = " + forecast.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteForecast(Forecast forecast) {
        if (forecast.getId() == 0) {
            // new entry, nothing to delete
            return false;
        }
        // create connection and return status of
        // delete statement
        DBConnection dbc = new DBConnection();
        return dbc.dmlStatement("DELETE FROM nzx.f_forecast "
                + " WHERE id = " + forecast.getId());
    }

}
