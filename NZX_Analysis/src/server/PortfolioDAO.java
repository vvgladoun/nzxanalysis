package server;


import java.util.List;

/**
 * Methods for data access to
 * portfolio
 * (based on DB tables
 * nzx.d_portfolio, nzx.f_portfolio)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public interface PortfolioDAO {
    /**
     * get all portfolios
     * @return list of all quotes
     */
    List<Portfolio> findAll();

    /**
     * get portfolio by id
     *
     * @param id - portfolio's id in data store
     * @return portfolios has been found by id
     */
    List<Portfolio> findById(int id);

    /**
     * get portfolio by user id
     *
     * @param id_user - portfolio's login in data store
     * @return portfolios has been found by user id
     */
    List<Portfolio> findByUserId(int id_user);


    /**
     * Insert new entry to portfolio data store
     *
     * @param portfolio - portfolio description
     * @return true if insert was successful
     */
    boolean insertPortfolio(Portfolio portfolio);

    /**
     * Update existed entry in portfolio data store
     *
     * @param portfolio - portfolio description
     * @return true if update was successful
     */
    boolean updatePortfolio(Portfolio portfolio);

    /**
     * Delete entry from portfolio data store
     *
     * @param portfolio - portfolio description
     * @return true if delete was successful
     */
    boolean deletePortfolio(Portfolio portfolio);
}