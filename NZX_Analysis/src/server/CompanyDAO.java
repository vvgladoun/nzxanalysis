package server;

import java.util.List;

/**
 * Methods for data access for
 * companies listed on NZX
 * (based on DB table nzx.d_company)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public interface CompanyDAO {

    /**
     * Get all companies
     * @return list of all companies
     */
    List<Company> findAll();

    /**
     * get company/ies by code
     *
     * @param code - company's code on NZX
     * @return companies has been found by code
     */
    List<Company> findByCode(String code);

    /**
     * get company/ies by name
     *
     * @param name - company's name
     * @return companies has been found by name
     */
    List<Company> findByName(String name);

    /**
     * get company/ies by portfolio's SID
     *
     * @param portfolio_id - portfolio's SID in data store
     * @return companies has been found by portfolio
     */
    List<Company> findByPortfolioId(int portfolio_id);

    /**
     * Search for a company by ID
     *
     * @param id - id of the company
     * @return company being found or null
     */
    Company findById(int id);

    /**
     * Insert new entry to company datastore
     *
     * @param company - company description
     * @return true if insert was successful
     */
    boolean insertCompany(Company company);

    /**
     * Update existed entry in company datastore
     *
     * @param company - company description
     * @return true if update was successful
     */
    boolean updateCompany(Company company);

    /**
     * Delete entry from company datastore
     *
     * @param company - company description
     * @return true if delete was successful
     */
    boolean deleteCompany(Company company);

}
