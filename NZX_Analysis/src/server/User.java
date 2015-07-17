package server;

/**
 * user's of the system
 * (based on DB table nzx.d_user)
 *
 * @author XYZ (15015413c@cornell.nz.ac, 14115394b@cornell.nz.ac)
 */
public final class User {

    // attributes from nzx.d_user
    // SID from database, by default = 0 (will be generated in DB)
    private int id;
    // user name
    private String firstname;
    private String lastname;
    // credentials to log in
    private String login;
    private String password;
    // role (0-public/ 1-administrator)
    private int id_user_role = 0;

    /**
     * default constructor
     */
    public User() {
        this(0, "","", 0, "", "");
    }

    /**
     * overloaded constructor (login/pass/role)
     *
     * @param login
     * @param password
     * @param id_user_role
     */
    public User(String login, String password, int id_user_role) {

        this(0, login, password, id_user_role, "", "");
    }

    /**
     * overloaded constructor (login/pass/role/firstname/lastname)
     *
     * @param login
     * @param password
     * @param id_user_role
     * @param firstname
     * @param lastname
     */
    public User(int id, String login, String password, int id_user_role,
                String firstname, String lastname) {

        this.id = id;
        this.login = login;
        this.password = password;
        this.id_user_role = id_user_role;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId_user_role() {
        return id_user_role;
    }

    public void setId_user_role(int id_user_role) {
        this.id_user_role = id_user_role;
    }
}
