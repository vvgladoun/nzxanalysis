package server.integration;

import server.Company;
import server.Quote;
import server.QuoteDAO;
import server.integration.CSVReader;
import server.integration.HttpDownloadUtility;
import server.integration.JdbcCompanyDAOImpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JDBC implementation of DAO for Quotes
 *  (integration with DB table nzx.f_quotes)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public final class JdbcQuotesDAOImpl implements QuoteDAO {

    /**
     * Select statement generator for quotes
     *
     * @param whereStatement - where clause
     * @return list of quotes
     */
    private List<Quote> selectQuote(String whereStatement) {

        List<Quote> quotesList = new ArrayList<Quote>();

        DBConnection dbc = new DBConnection();
        if (!dbc.isConnected()) {
            // connection to DB failed
            return quotesList;
        }
        // connection to DB
        Connection conn = dbc.getDbConnect();
        // select statement
        Statement stmt = null;
        try {

            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT id, id_company, " +
                    "close_date, open_price, high_price, low_price,  " +
                    "close_price, volume, adj_close adjusted_price FROM nzx.f_quotes "
                    + whereStatement + " ORDER BY close_date DESC ;");
            while (rs.next()) {
                int id = rs.getInt("id");
                int id_company = rs.getInt("id_company");
                Date close_date = rs.getDate("close_date");
                Double open_price = rs.getDouble("open_price");
                Double high_price = rs.getDouble("high_price");
                Double low_price = rs.getDouble("low_price");
                Double close_price = rs.getDouble("close_price");
                Double volume = rs.getDouble("volume");
                Double adjusted_close = rs.getDouble("adjusted_price");


                Quote quote = new Quote(id, id_company, close_date,
                open_price, high_price, low_price,
                close_price, volume, adjusted_close);
                quotesList.add(quote);


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

        return quotesList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Quote> findAll(int id_company) {
        return selectQuote(" WHERE id_company = " + id_company);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Quote> findByDate(int id_company, Date date) {
        return selectQuote(" WHERE id_company = " + id_company +
                " AND close_date = '" +
                DBConnection.formatDateForQuery(date) + "' ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Quote> findByPeriod(int id_company, Date dateFrom, Date dateTo) {
        return selectQuote(" WHERE id_company = " + id_company + " and close_date between '"
                + DBConnection.formatDateForQuery(dateFrom) + "' and '"
                + DBConnection.formatDateForQuery(dateTo) + "' ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insertQuote(Quote quote) {
        // create connection and return status of
        // insert statement
        DBConnection dbc = new DBConnection();
        return dbc.dmlStatement("INSERT INTO nzx.f_quotes (id_company, " +
                "close_date, open_price, high_price, low_price, close_price, " +
                "volume, adj_close ) VALUES (" + quote.getId_company() +
                ", '" + DBConnection.formatDateForQuery(quote.getClose_date()) +
                "', " + quote.getOpen_price() + ", " + quote.getHigh_price() +
                ", " + quote.getLow_price() + ", " + quote.getClose_price() +
                ", " + quote.getVolume() + ", " + quote.getAdjusted_close() + ")");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateQuote(Quote quote) {
        if (quote.getId() == 0) {
            // new entry, cannot update, must insert first!
            return false;
        }
        // create connection and return status of
        // update statement
        DBConnection dbc = new DBConnection();
        return dbc.dmlStatement("UPDATE nzx.f_quotes SET id_company = " + quote.getId_company()
                + ", close_date = '" + DBConnection.formatDateForQuery(quote.getClose_date())
                + "', open_price = " + quote.getOpen_price()
                + ", high_price = " + quote.getHigh_price()
                + ", low_price = " + quote.getLow_price()
                + ", close_price = " + quote.getClose_price()
                + ", volume = " + quote.getVolume()
                + ", adj_close = " + quote.getAdjusted_close()
                + " WHERE id = " + quote.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteQuote(Quote quote) {
        if (quote.getId() == 0) {
            // new entry, nothing to delete
            return false;
        }
        // create connection and return status of
        // delete statement
        DBConnection dbc = new DBConnection();
        return dbc.dmlStatement("DELETE FROM nzx.f_quotes "
                + " WHERE id = " + quote.getId());
    }


}
