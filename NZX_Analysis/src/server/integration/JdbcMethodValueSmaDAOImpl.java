package server.integration;

import server.MethodSMA;
import server.MethodValueSMA;
import server.MethodValueSmaDAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JDBC implementation of DAO for SMA values
 *  (integration with DB table nzx.f_method_sma_values)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public final class JdbcMethodValueSmaDAOImpl implements MethodValueSmaDAO {

    /**
     * Select statement generator for SMA values
     *
     * @param whereStatement - where clause
     * @return list of values
     */
    private List<MethodValueSMA> selectValueSMA(String whereStatement) {

        List<MethodValueSMA> valuesList = new ArrayList<MethodValueSMA>();

        DBConnection dbc = new DBConnection();
        if (!dbc.isConnected()) {
            // connection to DB failed
            return valuesList;
        }
        // connection to DB
        Connection conn = dbc.getDbConnect();
        // select statement
        Statement stmt = null;
        try {
            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT id, id_company, " +
                    "close_date, sma_length, sma_value FROM nzx.f_method_sma_values "
                    + whereStatement + " ;");
            while (rs.next()) {
                int id = rs.getInt("id");
                int id_company = rs.getInt("id_company");
                Date close_date = rs.getDate("close_date");
                int sma_length = rs.getInt("sma_length");
                Double sma_value = rs.getDouble("sma_value");

                MethodValueSMA valueSMA = new MethodValueSMA(id, id_company, close_date,
                        sma_length, sma_value);

                valuesList.add(valueSMA);
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

        return valuesList;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<MethodValueSMA> findAll(int id_company) {
        return selectValueSMA(" WHERE id_company = " + id_company);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MethodValueSMA> findAllByLength(int id_company, int sma_length) {
        return selectValueSMA(" WHERE id_company = " + id_company +
                " AND sma_length = " + sma_length + " ");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<MethodValueSMA> findByDateLength(int id_company, int sma_length, Date date) {
        return selectValueSMA(" WHERE id_company = " + id_company +
                " AND sma_length = " + sma_length + " AND close_date = '" +
                DBConnection.formatDateForQuery(date) + "' ");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<MethodValueSMA> findByPeriodLength(int id_company, int sma_length, Date dateFrom, Date dateTo) {
        return selectValueSMA(" WHERE id_company = " + id_company +
                " AND sma_length = " + sma_length + " AND close_date BETWEEN '"
                + DBConnection.formatDateForQuery(dateFrom) + "' AND '"
                + DBConnection.formatDateForQuery(dateTo) + "' ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insertMethodValueSMA(MethodValueSMA valueSMA) {
        // create connection and return status of
        // insert statement
        DBConnection dbc = new DBConnection();
        return dbc.dmlStatement("INSERT INTO nzx.f_method_sma_values (id_company, " +
                "close_date, sma_length, sma_value ) VALUES (" + valueSMA.getId_company() +
                ", '" + DBConnection.formatDateForQuery(valueSMA.getClose_date()) +
                "', " + valueSMA.getSma_length() + ", " + valueSMA.getValue() + ")");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateMethodValueSMA(MethodValueSMA valueSMA) {
        if (valueSMA.getId() == 0) {
            // new entry, cannot update, must insert first!
            return false;
        }
        // create connection and return status of
        // update statement
        DBConnection dbc = new DBConnection();
        return dbc.dmlStatement("UPDATE nzx.f_method_sma_values SET id_company = " + valueSMA.getId_company()
                + ", close_date = '" + DBConnection.formatDateForQuery(valueSMA.getClose_date())
                + "', sma_length = " + valueSMA.getSma_length()
                + ", sma_value = " + valueSMA.getValue()
                + " WHERE id = " + valueSMA.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteMethodValueSMA(MethodValueSMA valueSMA) {
        if (valueSMA.getId() == 0) {
            // new entry, nothing to delete
            return false;
        }
        // create connection and return status of
        // delete statement
        DBConnection dbc = new DBConnection();
        return dbc.dmlStatement("DELETE FROM nzx.f_method_sma_values "
                + " WHERE id = " + valueSMA.getId());
    }
}
