package server;

import java.util.List;

/**
 * Methods for data access to
 * users of the system
 * (based on DB table nzx.d_user)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public interface UserDAO {
    /**
     * get all users
     * @return list of all users
     */
    List<User> findAll();

    /**
     * get user by id
     *
     * @param id - user's id in data store
     * @return users has been found by id
     */
    User findById(int id);

    /**
     * get user by login
     *
     * @param login - user's login in data store
     * @return users has been found by login
     */
    User findByLogin(String login);


    /**
     * Insert new entry to user data store
     *
     * @param user - user description
     * @return true if insert was successful
     */
    boolean insertUser(User user);

    /**
     * Update existed entry in user data store
     *
     * @param user - user description
     * @return true if update was successful
     */
    boolean updateUser(User user);

    /**
     * Delete entry from user data store
     *
     * @param user - user description
     * @return true if delete was successful
     */
    boolean deleteUser(User user);
}
