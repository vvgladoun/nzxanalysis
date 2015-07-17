package server.integration;

import server.Company;
import server.CompanyDAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of DAO for Company
 *  (integration with DB table nzx.d_company)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public final class JdbcCompanyDAOImpl implements CompanyDAO {


    /**
     * Select statement generator for companies
     *
     * @param whereStatement - where clause
     * @return list of companies
     */
    private List<Company> selectCompany(String whereStatement) {

        List<Company> companiesList = new ArrayList<Company>();

        DBConnection dbc = new DBConnection();
        if (!dbc.isConnected()) {
            // connection to DB failed
            return companiesList;
        }
        // connection to DB
        Connection conn = dbc.getDbConnect();
        // select statement
        Statement stmt = null;
        try {

            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT id, COALESCE(code,'') code, " +
                    "COALESCE(name, '') comp_name, " +
                    "COALESCE(description, '') description FROM nzx.d_company " +
                    whereStatement + " order by COALESCE(code, name);");
            while (rs.next()) {
                int id = rs.getInt("id");
                String code = rs.getString("code");
                String name = rs.getString("comp_name");
                String description = rs.getString("description");

                Company comp = new Company(id, code, name, description);
                companiesList.add(comp);
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

        return companiesList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Company> findAll() {

        return selectCompany("");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Company> findByCode(String code) {

        return selectCompany("where code = '" + code + "'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Company> findByName(String name) {
        return selectCompany("where name like '" + name + "%'");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Company> findByPortfolioId(int portfolio_id) {
        // get id-s from portfolio-company map table
        return selectCompany("WHERE id IN (" +
                "SELECT id_company FROM nzx.f_portfolio " +
                "WHERE id_portfolio = " + portfolio_id + " ) ");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Company findById(int id){
        List<Company> companiesList = selectCompany("where id = " + id);
        if (companiesList.size() > 0) {
            return companiesList.get(0);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insertCompany(Company company) {
        // create connection and return status of
        // insert statement
        DBConnection dbc = new DBConnection();
        return dbc.dmlStatement("INSERT INTO nzx.d_company (code, name, description) "
                + "VALUES ('" + company.getCode() + "', '"
                + company.getName().replace("'", "''") + "', '"
                + company.getDescription().replace("'", "''") + "')");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateCompany(Company company) {
        if (company.getId() == 0) {
            // new company, cannot update, must insert first!
            return false;
        }
        // create connection and return status of
        // update statement
        DBConnection dbc = new DBConnection();
        return dbc.dmlStatement("UPDATE nzx.d_company SET "
                + "code = '" + company.getCode()
                + "', name = '" + company.getName().replace("'", "''")
                + "', description = '" + company.getDescription().replace("'", "''")
                + "' WHERE id = " + company.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteCompany(Company company) {
        if (company.getId() == 0) {
            // new company, nothing to delete
            return false;
        }
        // create connection and return status of
        // delete statement
        DBConnection dbc = new DBConnection();
        return dbc.dmlStatement("DELETE FROM nzx.d_company "
                + " WHERE id = " + company.getId() + "");
    }


}
