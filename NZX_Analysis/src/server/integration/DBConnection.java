package server.integration;

import server.Company;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Establishing connection with
 *
 * Created by vladimirgladun on 19/05/15.
 */
public final class DBConnection  {

    // PostgreSQL JDBC connection string

    private static String DB_URL = "jdbc:postgresql://";
    private static String DB_HOST = "localhost";
    private static String DB_PORT = "5433";
    // default user and password to connect to database
    private static String DB_USER = "nzxadmin";
    private static String DB_PASSWORD = "nzxadmin";
    private static String DB_NAME = "nzxa";

    // Connection to SQL server
    private Connection dbConnect;
    // Current status, true if connected to DB
    private boolean connected = false;


    // Current status description string
    private String status = "";

    /**
     * Default constractor
     * Establish connection with default
     * login and password
     */
    public DBConnection() {
        this(DB_USER, DB_PASSWORD, DB_NAME, DB_HOST, DB_PORT);
    }

    /**
     * Overloaded constractor
     * Establish connection with user defined
     * login ,password and database name
     *
     * @param db_user
     * @param db_password
     * @param db_name
     */
    public DBConnection(String db_user, String db_password, String db_name) {
        this(db_user, db_password, db_name, DB_HOST, DB_PORT);
    }

    /**
     * Overloaded constractor
     * Establish connection with user defined
     * login and password
     *
     * @param db_user
     * @param db_password
     * @param db_name
     * @param db_host
     * @param db_port
     */
    public DBConnection(String db_user, String db_password, String db_name, String db_host, String db_port) {
        // Create connection to Database
        this.DB_HOST = db_host;
        this.DB_PORT = db_port;
        this.DB_USER = db_user;
        this.DB_PASSWORD = db_password;
        this.DB_NAME = db_name;
        connect();
    }

    public static String getDbUrl() {
        return DB_URL;
    }

    public static void setDbUrl(String dbUrl) {
        DB_URL = dbUrl;
    }

    public static String getDbHost() {
        return DB_HOST;
    }

    public static void setDbHost(String dbHost) {
        DB_HOST = dbHost;
    }

    public static String getDbPort() {
        return DB_PORT;
    }

    public static void setDbPort(String dbPort) {
        DB_PORT = dbPort;
    }

    public static String getDbUser() {
        return DB_USER;
    }

    public static void setDbUser(String dbUser) {
        DB_USER = dbUser;
    }

    public static String getDbPassword() {
        return DB_PASSWORD;
    }

    public static void setDbPassword(String dbPassword) {
        DB_PASSWORD = dbPassword;
    }

    public static String getDbName() {
        return DB_NAME;
    }

    public static void setDbName(String dbName) {
        DB_NAME = dbName;
    }

    /**
     *
     * @return status string
     */
    public String getStatus() {
        return status;
    }

    /**
     * establish connection with database
     */
    public void connect(){
        if (connected) {
            // connection already established
            return;
        }
        // Create connection to Database
        Properties props = new Properties();
        props.setProperty("user", DB_USER);
        props.setProperty("password", DB_PASSWORD);
        //props.setProperty("ssl", "true");
        //"Connecting to database..."
        try {
            Class.forName("org.postgresql.Driver");
            String connection_string = generateURL();
            dbConnect = DriverManager.getConnection(connection_string, props);
            //"Connection established"
            status = "Connection established";
            connected = true;
        } catch (SQLException e) {
            //"Connection failed: SQL error"
            status = "Connection failed: " + e.getMessage();
            connected = false;
        } catch (Exception e) {
            //"Connection failed: unknown error"
            status = "Connection failed: " + e.getMessage();
            connected = false;
        }
    }

    /**
     * Getter for current connection
     *
     * @return connection to db
     */
    public Connection getDbConnect() {
        return dbConnect;
    }

    /**
     * Getter for connection status
     * true if connected to DB
     *
     * @return status
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Close current DB connection
     *
     * @return true if connection closed successfully
     */
    public boolean closeConnection(){
        try {
            dbConnect.close();
        } catch (SQLException e) {
            status = "Connection close failed: " + e.getMessage();
            return false;
        }
        // connection closed
        status = "Connection closed";
        connected = false;
        return true;
    }

    /**
     * Create NZX database
     *
     * @return true if database was created successfull
     */
    public boolean createDB(){
        // check if connection was established
        if (!connected) {
            return false;
        }

        Statement stmt = null;

        //create role (try to drop first)
        try {
            stmt = dbConnect.createStatement();
            // drop DB if exists
            stmt.executeUpdate("DROP DATABASE IF EXISTS nzxa;");
            // drop role (user) if exists
            stmt.executeUpdate("DROP ROLE IF EXISTS nzxadmin;");
            // create all objects
            // role
            stmt.executeUpdate("CREATE ROLE nzxadmin LOGIN\n" +
                    "  ENCRYPTED PASSWORD 'nzxadmin'\n" +
                    "  SUPERUSER INHERIT CREATEDB CREATEROLE REPLICATION;\n" +
                    "COMMENT ON ROLE nzxadmin IS 'Default admin role for NZX analysis';\n");
            // database
            stmt.executeUpdate("CREATE DATABASE nzxa\n" +
                    "  WITH OWNER = nzxadmin\n" +
                    "       ENCODING = 'UTF8'\n" +
                    "       TABLESPACE = pg_default\n" +
                    "       LC_COLLATE = 'ru_RU.UTF-8'\n" +
                    "       LC_CTYPE = 'ru_RU.UTF-8'\n" +
                    "       CONNECTION LIMIT = -1;\n");
        } catch (SQLException e) {
            //e.printStackTrace();
            status = "Cannot create database: " + e.getMessage();
            return false;
        }

        //reconnect to new db
        closeConnection();
        DB_USER = "nzxadmin";
        DB_PASSWORD = "nzxadmin";
        DB_NAME = "nzxa";
        connect();

        try {
            stmt = dbConnect.createStatement();

            // schema
            stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS nzx\n" +
                    "       AUTHORIZATION nzxadmin;\n");
            // tables
            // d_user
            stmt.executeUpdate("CREATE TABLE nzx.d_user\n" +
                    "(\n" +
                    "  id serial primary key NOT NULL,\n" +
                    "  firstname character varying,\n" +
                    "  lastname character varying,\n" +
                    "  login character varying NOT NULL,\n" +
                    "  pass character varying NOT NULL,\n" +
                    "  id_user_role integer DEFAULT 2\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "ALTER TABLE nzx.d_user\n" +
                    "  OWNER TO nzxadmin;");
            stmt.executeUpdate("INSERT INTO nzx.d_user (login, pass,  id_user_role) " +
                    "VALUES ('admin', 'admin', 1);");
            // d_user_roles
            stmt.executeUpdate("CREATE TABLE nzx.d_user_role\n" +
                    "(\n" +
                    "  id int NOT NULL,\n" +
                    "  description character varying,\n" +
                    "  CONSTRAINT d_user_role_pkey PRIMARY KEY (id)\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "ALTER TABLE nzx.d_user_role\n" +
                    "  OWNER TO nzxadmin;");
            stmt.executeUpdate("INSERT INTO nzx.d_user_role (id, description) VALUES (1, 'administrator');");
            stmt.executeUpdate("INSERT INTO nzx.d_user_role (id, description) VALUES (2, 'public');");
            // d_company
            stmt.executeUpdate("CREATE TABLE nzx.d_company (\n" +
                    "  id serial  primary key NOT NULL,\n" +
                    "  code character varying(4),\n" +
                    "  name character varying,\n" +
                    "  description character varying\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "ALTER TABLE nzx.d_company\n" +
                    "  OWNER TO nzxadmin;\n");
            // d_portfolio
            stmt.executeUpdate("CREATE TABLE nzx.d_portfolio\n" +
                    "(\n" +
                    "  id serial primary key NOT NULL,\n" +
                    "  id_user int NOT NULL, \n" +
                    "  description character varying\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "ALTER TABLE nzx.d_portfolio\n" +
                    "  OWNER TO nzxadmin;");
            // f_portfolio
            stmt.executeUpdate("CREATE TABLE nzx.f_portfolio\n" +
                    "(\n" +
                    "  id_portfolio integer NOT NULL,\n" +
                    "  id_company integer NOT NULL,\n" +
                    "  CONSTRAINT f_portfolio_id_company_fkey FOREIGN KEY (id_company)\n" +
                    "      REFERENCES nzx.d_portfolio (id) MATCH SIMPLE\n" +
                    "      ON UPDATE NO ACTION ON DELETE CASCADE,\n" +
                    "  CONSTRAINT f_portfolio_id_portfolio_fkey FOREIGN KEY (id_portfolio)\n" +
                    "      REFERENCES nzx.d_company (id) MATCH SIMPLE\n" +
                    "      ON UPDATE NO ACTION ON DELETE CASCADE\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "ALTER TABLE nzx.f_portfolio\n" +
                    "  OWNER TO nzxadmin;\n");
            // f_quotes
            stmt.executeUpdate("CREATE TABLE nzx.f_quotes\n" +
                    "(\n" +
                    "  id serial NOT NULL,\n" +
                    "  id_company integer,\n" +
                    "  close_date date,\n" +
                    "  open_price numeric,\n" +
                    "  high_price numeric,\n" +
                    "  low_price numeric,\n" +
                    "  close_price numeric,\n" +
                    "  volume numeric,\n" +
                    "  adj_close numeric,\n" +
                    "  CONSTRAINT f_quotes_id_company_fkey FOREIGN KEY (id_company)\n" +
                    "      REFERENCES nzx.d_company (id) MATCH SIMPLE\n" +
                    "      ON UPDATE NO ACTION ON DELETE CASCADE\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "ALTER TABLE nzx.f_quotes\n" +
                    "  OWNER TO nzxadmin;\n");
            // d_method_type
            stmt.execute("CREATE TABLE nzx.d_method_type\n" +
                    "(\n" +
                    "  id int primary key NOT NULL,\n" +
                    "  type_name character varying\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "ALTER TABLE nzx.d_method_type\n" +
                    "  OWNER TO nzxadmin;\n" +
                    "\n" +
                    "Insert into nzx.d_method_type (id, type_name) values (1, 'MA2');");
            // d_method_sma
            stmt.executeUpdate("CREATE TABLE nzx.d_method_sma\n" +
                    "(\n" +
                    "  id serial primary key NOT NULL,\n" +
                    "  description character varying,\n" +
                    "  first_ma int NOT NULL,\n" +
                    "  second_ma int NOT NULL,\n" +
                    "  fee_percent numeric\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "ALTER TABLE nzx.d_method_sma\n" +
                    "  OWNER TO nzxadmin;\n");
            // f_method_sma_values
            stmt.executeUpdate("CREATE TABLE nzx.f_method_sma_values\n" +
                    "(\n" +
                    "  id serial primary key NOT NULL,\n" +
                    "  id_company integer,\n" +
                    "  close_date date,\n" +
                    "  value numeric,\n" +
                    "  sma_length int,\n" +
                    "  CONSTRAINT f_method_sma_values_id_company_fkey FOREIGN KEY (id_company)\n" +
                    "      REFERENCES nzx.d_company (id) MATCH SIMPLE\n" +
                    "      ON UPDATE NO ACTION ON DELETE CASCADE\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "ALTER TABLE nzx.f_method_sma_values\n" +
                    "  OWNER TO nzxadmin;");
            // f_forecast
            stmt.executeUpdate("CREATE TABLE nzx.f_forecast\n" +
                    "(\n" +
                    "  id serial primary key NOT NULL,\n" +
                    "  id_method integer,\n" +
                    "  id_company integer,\n" +
                    "  forecast_date date,\n" +
                    "  forecast integer,\n" +
                    "  result numeric,\n" +
                    "  succeeded integer,\n" +
                    "  CONSTRAINT f_forecast_id_company_fkey FOREIGN KEY (id_company)\n" +
                    "      REFERENCES nzx.d_company (id) MATCH SIMPLE\n" +
                    "      ON UPDATE NO ACTION ON DELETE CASCADE\n" +
                    ")\n" +
                    "WITH (\n" +
                    "  OIDS=FALSE\n" +
                    ");\n" +
                    "ALTER TABLE nzx.f_forecast\n" +
                    "  OWNER TO nzxadmin;");
            // close statement
            stmt.close();
        } catch (SQLException e) {
            //e.printStackTrace();
            status = "Cannot create database: " + e.getMessage();
            return false;
        }

        status = "Database created successfully";
        return true;

    }

    public static String generateURL() {
        return DB_URL + DB_HOST + ":"
                + DB_PORT + "/" + DB_NAME;
    }

    /**
     * execute DML statement
     * without result set
     *
     * @param statement - update, insert or
     *                  delete statement
     * @return true if script run successfully
     */
    public boolean dmlStatement(String statement) {

        DBConnection dbc = new DBConnection();
        if (!dbc.isConnected()) {
            // connection to DB failed
            this.status = "connection failed";
            return false;
        }
        // connection to DB
        Connection conn = dbc.getDbConnect();
        // select statement
        Statement stmt = null;
        try {
            //run DML (update/insert/delete)
            stmt = conn.createStatement();
            stmt.executeUpdate(statement);
            stmt.close();
            conn.close();
            dbc = null;
            this.status = "";
        } catch (SQLException e) {
            //e.printStackTrace();
            // sql error
            dbc = null;
            return false;
        }

        return true;
    }


    /**
     * format date to default postgres string format
     *
     * @param date
     * @return formatted string
     */
    public static String formatDateForQuery(java.util.Date date){
        return (new SimpleDateFormat("yyyy-MM-dd")).format(date);
    }
}
