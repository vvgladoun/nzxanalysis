package server;

/**
 * Company listed on NZX
 * (based on DB table nzx.d_company)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public final class Company {

    // attributes from nzx.d_company
    // SID from database, by default = 0 (will be generated in DB)
    private int id;
    // code on NZX
    private String code;
    private String name;
    // addition information about the company
    private String description;


    /**
     * default constructor
     */
    public Company(){
        this(0, "", "New company", "");
    }

    /**
     * overloaded constructor (defines name)
     *
     * @param name - name of the company
     */
    public Company(String name){
        this(0, "", name, "");
    }

    /**
     * overloaded constructor (defines name)
     *
     * @param name - name of the company
     */
    public Company(int id, String code, String name, String description){
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
    }

    /**
     *
     * @return company's id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return company's code
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @param code comapany's code (on NZX)
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     *
     * @return company's name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name - company's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return additional info for the company
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description - additional info for the company
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
