package server.integration;

import server.MethodSMA;
import server.MethodSmaDAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of DAO for SMA method
 *  (integration with DB table nzx.f_method_sma)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public final class JdbcMethodSmaDAOImpl implements MethodSmaDAO {


    /**
     * Select statement generator for users
     *
     * @param whereStatement - where clause
     * @return list of users
     */
    private List<MethodSMA> selectMethod(String whereStatement) {

        List<MethodSMA> methodList = new ArrayList<MethodSMA>();

        DBConnection dbc = new DBConnection();
        if (!dbc.isConnected()) {
            // connection to DB failed
            return methodList;
        }
        // connection to DB
        Connection conn = dbc.getDbConnect();
        // select statement
        Statement stmt = null;
        try {

            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT id, COALESCE(description,'') description, " +
                    "COALESCE(fee_percent, 0.0) fee_percent, first_ma, " +
                    "second_ma FROM nzx.d_method_sma " + whereStatement + " ;");
            while (rs.next()) {
                int id = rs.getInt("id");
                String description = rs.getString("description");
                double feePercent = rs.getDouble("fee_percent");
                int shortSMA = rs.getInt("first_ma");
                int longSMA = rs.getInt("second_ma");

                MethodSMA method = new MethodSMA(id, description, shortSMA, longSMA, feePercent);
                methodList.add(method);
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

        return methodList;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<MethodSMA> findAll() {
        return selectMethod("");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MethodSMA> findById(int id) {
        return selectMethod(" WHERE id = " + id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MethodSMA> findByName(String name) {
        return selectMethod(" WHERE description like '" + name + "%' ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insertMethod(MethodSMA method) {
        // create connection and return status of
        // insert statement
        DBConnection dbc = new DBConnection();
        return dbc.dmlStatement("INSERT INTO nzx.d_method_sma (description, " +
                "fee_percent, first_ma, second_ma) "
                + "VALUES ('" + method.getDescription().replace("'", "''") + "', "
                + method.getFeePercent() + ", " + method.getShortSMA() + ", "
                + method.getLongSMA() + ")");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateMethod(MethodSMA method) {
        if (method.getId() == 0) {
            // new entry, cannot update, must insert first!
            return false;
        }
        // create connection and return status of
        // update statement
        DBConnection dbc = new DBConnection();
        return dbc.dmlStatement("UPDATE nzx.d_method_sma SET "
                + "description = '" + method.getDescription().replace("'", "''")
                + "', fee_percent = " + method.getFeePercent()
                + ", first_ma = " + method.getShortSMA()
                + ", second_ma = " + method.getLongSMA()
                + " WHERE id = " + method.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteMethod(MethodSMA method) {
        if (method.getId() == 0) {
            // new entry, nothing to delete
            return false;
        }
        // create connection and return status of
        // delete statement
        DBConnection dbc = new DBConnection();
        return dbc.dmlStatement("DELETE FROM nzx.d_method_sma "
                + " WHERE id = " + method.getId());
    }

}
