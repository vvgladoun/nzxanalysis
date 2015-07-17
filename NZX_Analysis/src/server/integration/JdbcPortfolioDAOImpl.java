package server.integration;

import server.Company;
import server.Portfolio;
import server.PortfolioDAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of DAO for Portfolio
 *  (integration with DB table nzx.d_portfolio)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public final class JdbcPortfolioDAOImpl implements PortfolioDAO {

    /**
     * Select statement generator for portfolios
     *
     * @param whereStatement - where clause
     * @return list of portfolios
     */
    private List<Portfolio> selectPortfolio(String whereStatement) {

        List<Portfolio> portfolioList = new ArrayList<Portfolio>();

        DBConnection dbc = new DBConnection();
        if (!dbc.isConnected()) {
            // connection to DB failed
            return portfolioList;
        }
        // connection to DB
        Connection conn = dbc.getDbConnect();
        // select statement
        Statement stmt = null;
        try {

            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT id, " +
                    "id_user, COALESCE(description, '') description " +
                    " FROM nzx.d_portfolio " + whereStatement + " ;");
            while (rs.next()) {
                int id = rs.getInt("id");
                int id_user = rs.getInt("id_user");
                String description = rs.getString("description");
                // get list of companies in portfolio
                List<Company> companies = (new JdbcCompanyDAOImpl()).findByPortfolioId(id);
                if (companies == null) {
                    companies = new ArrayList<Company>();
                }
                Portfolio portfolio = new Portfolio(id, id_user, description, companies);
                portfolioList.add(portfolio);
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

        return portfolioList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Portfolio> findAll() {
        return selectPortfolio("");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Portfolio> findById(int id) {
        return selectPortfolio(" WHERE id = " + id + " ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Portfolio> findByUserId(int id_user) {
        return selectPortfolio(" WHERE id_user = " + id_user + " ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insertPortfolio(Portfolio portfolio) {
        // create connection and return status of
        // insert statement
        DBConnection dbc = new DBConnection();
        boolean d_portfolio_status = dbc.dmlStatement("INSERT INTO nzx.d_portfolio (id_user, " +
                "description) VALUES (" + portfolio.getId_user() + ", '"
                + portfolio.getDescription() + "')");
        boolean f_portfolio_status = true;
        if (d_portfolio_status) {
            List<Company> companies = portfolio.getCompanies();
            if (companies == null) {
                // no companies to insert into f_portfolio
                return d_portfolio_status;
            }

            if (portfolio.getCompanies().size() > 0) {
                for (Company company : companies) {
                    // insert maps portfolio-company
                    f_portfolio_status = dbc.dmlStatement("INSERT INTO nzx.f_portfolio (id_portfolio, " +
                            "id_company) VALUES (" + portfolio.getId() + ", "
                            + company.getId() + ")");
                }
            }
        }

        return (d_portfolio_status && f_portfolio_status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updatePortfolio(Portfolio portfolio) {

        if (portfolio.getId() == 0) {
            // new entry, cannot update, must insert first!
            return false;
        }
        // create connection and return status of
        // update statement
        DBConnection dbc = new DBConnection();
        boolean d_portfolio_status = dbc.dmlStatement("UPDATE nzx.d_portfolio SET "
                + "id_user = " + portfolio.getId_user() + ", "
                + "description = '" + portfolio.getDescription().replace("'", "''")
                + "' WHERE id = " + portfolio.getId());

        boolean f_portfolio_status = true;
        //if d_portfolio updated, update f_portfolio
        if (d_portfolio_status) {
            //delete old companies from portfolio and add new
            boolean f_portfolio_deleted = dbc.dmlStatement("DELETE FROM nzx.f_portfolio " +
                    "WHERE id_portfolio = " + portfolio.getId());

            List<Company> companies = portfolio.getCompanies();
            if (companies == null) {
                return d_portfolio_status && f_portfolio_deleted;
            }

            if (portfolio.getCompanies().size() > 0) {
                for (Company company : companies) {
                    // insert maps portfolio-company
                    f_portfolio_status = dbc.dmlStatement("INSERT INTO nzx.f_portfolio (id_portfolio, " +
                            "id_company) VALUES (" + portfolio.getId() + ", "
                            + company.getId() + ")");
                }
            }
        }
        // true if both portfolio tables were updated successfully
        return (d_portfolio_status && f_portfolio_status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deletePortfolio(Portfolio portfolio) {

        if (portfolio.getId() == 0) {
            // new entry, nothing to delete
            return false;
        }
        // create connection and return status of
        // delete statement
        DBConnection dbc = new DBConnection();
        boolean f_portfolio_status = dbc.dmlStatement("DELETE FROM nzx.f_portfolio "
                + " WHERE id_portfolio = " + portfolio.getId());
        boolean d_portfolio_status = dbc.dmlStatement("DELETE FROM nzx.d_portfolio "
                + " WHERE id = " + portfolio.getId());

        return (d_portfolio_status && f_portfolio_status);
    }
}
