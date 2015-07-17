package server;

import java.util.ArrayList;
import java.util.List;

/**
 * Portfolio - user's list of companies
 * (based on DB tables nzx.d_portfolio, f_portfolio)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public final class Portfolio {

    // SID from database, by default = 0 (will be generated in DB)
    private int id;
    // SID of the user
    private int id_user;
    // portfolio name/description
    private String description;
    // list of companies
    private List<Company> companies;

    /**
     * default constructor
     */
    public Portfolio() {

        this(0, 0, "New portfolio", new ArrayList<Company>());
    }

    /**
     * overloaded constructor (id_user/description)
     */
    public Portfolio(int id, int id_user, String description, List<Company> companies) {

        this.id = id;
        this.id_user = id_user;
        this.description = description;
        this.companies = companies;
    }

    /**
     *
     * @return portfolio SID
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id - Portfolio SID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return user's SID
     */
    public int getId_user() {
        return id_user;
    }

    /**
     *
     * @param id_user - user's SID
     */
    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    /**
     *
     * @return portfolio's name/description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description - portfolio's name
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return list of companies in portfolio
     */
    public List<Company> getCompanies() {
        return companies;
    }

    /**
     *
     * @param companies - list of companies
     *                  in portfolio
     */
    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    /**
     * Add company to companies list
     *
     * @param company - Company object
     */
    public void addCompany(Company company) {
        this.companies.add(company);
    }

    /**
     * name generator:
     * number of companies
     * and list of companies' codes
     *
     * @return generated name
     */
    public String generateDescription() {

        if (companies.isEmpty()) {
            //no companies were added
            return "New empty portfolio";
        }
        // generate string
        StringBuilder sbName = new StringBuilder();
        //number of companies
        int numComp = companies.size();
        // if more then one company, add number
        if (numComp > 1) {
            sbName.append("" + numComp + " companies: ");
        }

        for (int i = 0; i < numComp; i++) {
            // get name of the company from list
            sbName.append(companies.get(i).getCode());
            //if not the last, add coma
            if ((i+1) < numComp) {
                sbName.append(", ");
            }
        }

        // return generated name
        return sbName.toString();
    }
}