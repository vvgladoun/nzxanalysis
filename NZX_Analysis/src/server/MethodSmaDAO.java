package server;

import java.util.List;

/**
 * Methods for data access for
 * SMA methods
 * (based on DB table nzx.d_method_sma)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public interface MethodSmaDAO {
    /**
     * Get all SMA methods
     * @return list of all SMA methods
     */
    List<MethodSMA> findAll();

    /**
     * get SMA method by id
     *
     * @param id - method's id
     * @return SMA methods has been found by id
     */
    List<MethodSMA> findById(int id);

    /**
     * get methods by name
     *
     * @param name - method's name
     * @return methods has been found by name
     */
    List<MethodSMA> findByName(String name);

    /**
     * Insert new entry to SMA method data store
     *
     * @param method - SMA method
     * @return true if insert was successful
     */
    boolean insertMethod(MethodSMA method);

    /**
     * Update existed entry in SMA method data store
     *
     * @param method - SMA method
     * @return true if update was successful
     */
    boolean updateMethod(MethodSMA method);

    /**
     * Delete entry from SMA method data store
     *
     * @param method - SMA method description
     * @return true if delete was successful
     */
    boolean deleteMethod(MethodSMA method);
}
