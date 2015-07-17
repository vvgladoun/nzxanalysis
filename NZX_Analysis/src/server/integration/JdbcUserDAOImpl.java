package server.integration;

import server.User;
import server.UserDAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of DAO for User
 *  (integration with DB table nzx.d_user)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public final class JdbcUserDAOImpl implements UserDAO {

    /**
     * Select statement generator for users
     *
     * @param whereStatement - where clause
     * @return list of users
     */
    private List<User> selectUser(String whereStatement) {

        List<User> userList = new ArrayList<User>();

        DBConnection dbc = new DBConnection();
        if (!dbc.isConnected()) {
            // connection to DB failed
            return userList;
        }
        // connection to DB
        Connection conn = dbc.getDbConnect();
        // select statement
        Statement stmt = null;
        try {

            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT id, COALESCE(login,'') login, " +
                    "COALESCE(pass, '') pass, COALESCE(id_user_role, 0) id_user_role, " +
                    "COALESCE(firstname, '') firstname, " +
                    "COALESCE(lastname, '') lastname FROM nzx.d_user " +
                    whereStatement + " ;");
            while (rs.next()) {
                int id = rs.getInt("id");
                String login = rs.getString("login");
                String pass = rs.getString("pass");
                int id_user_role = rs.getInt("id_user_role");
                String firstname = rs.getString("firstname");
                String lastname = rs.getString("lastname");

                User user = new User(id, login, pass, id_user_role, firstname, lastname);
                userList.add(user);
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

        return userList;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findAll() {
        return selectUser("");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findById(int id) {
        List<User> userList = new ArrayList<User>();
        userList = selectUser(" WHERE id = " + id);
        if (userList.size() == 0) {
            return null;
        }

        return userList.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findByLogin(String login) {
        List<User> userList = new ArrayList<User>();
        userList = selectUser(" WHERE login = '" + login + "' ");
        if (userList.size() == 0) {
            return null;
        }

        return userList.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean insertUser(User user) {
        // create connection and return status of
        // insert statement
        DBConnection dbc = new DBConnection();
        return dbc.dmlStatement("INSERT INTO nzx.d_user (firstname, " +
                "lastname, login, pass, id_user_role) "
                + "VALUES ('" + user.getFirstname() + "', '"
                + user.getLastname() + "', '" + user.getLogin() +  "', '"
                + user.getPassword() + "', " + user.getId_user_role() + ")");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateUser(User user) {
        if (user.getId() == 0) {
            // new entry, cannot update, must insert first!
            return false;
        }
        // create connection and return status of
        // update statement
        DBConnection dbc = new DBConnection();
        return dbc.dmlStatement("UPDATE nzx.d_user SET "
                + "firstname = '" + user.getFirstname().replace("'", "''")
                + "', lastname = '" + user.getLastname().replace("'", "''")
                + "', login = '" + user.getLogin().replace("'", "''")
                + "', pass = '" + user.getPassword().replace("'", "''")
                + "', id_user_role = " + user.getId_user_role()
                + " WHERE id = " + user.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteUser(User user) {
        if (user.getId() == 0) {
            // new entry, nothing to delete
            return false;
        }
        // create connection and return status of
        // delete statement
        DBConnection dbc = new DBConnection();
        return dbc.dmlStatement("DELETE FROM nzx.d_user "
                + " WHERE id = " + user.getId());
    }
}
