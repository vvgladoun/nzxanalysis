--drop db and role if exists
DROP DATABASE IF EXISTS nzxa;
DROP ROLE IF EXISTS nzxadmin;
--create role
CREATE ROLE nzxadmin LOGIN
  ENCRYPTED PASSWORD 'nzxadmin'
  SUPERUSER INHERIT CREATEDB CREATEROLE REPLICATION;
COMMENT ON ROLE nzxadmin IS 'Default admin role for NZX analysis';
--create database
CREATE DATABASE nzxa
  WITH OWNER = nzxadmin
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'en_EN.UTF-8'
       LC_CTYPE = 'en_EN.UTF-8'
       CONNECTION LIMIT = -1;

       
--create tables
-- Table: nzx.d_company

-- DROP TABLE nzx.d_company;

CREATE TABLE nzx.d_company
(
  id serial NOT NULL,
  code character varying(4),
  name character varying,
  description character varying,
  CONSTRAINT d_company_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE nzx.d_company
  OWNER TO nzxadmin;

-- Table: nzx.d_user_role

-- DROP TABLE nzx.d_user_role;

CREATE TABLE nzx.d_user_role
(
  id integer NOT NULL,
  description character varying,
  CONSTRAINT d_user_role_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE nzx.d_user_role
  OWNER TO nzxadmin;

-- Table: nzx.d_user

-- DROP TABLE nzx.d_user;

CREATE TABLE nzx.d_user
(
  id serial NOT NULL,
  firstname character varying,
  lastname character varying,
  login character varying NOT NULL,
  pass character varying NOT NULL,
  id_user_role integer DEFAULT 2,
  CONSTRAINT d_user_pkey PRIMARY KEY (id),
  CONSTRAINT d_user_role_id_fk FOREIGN KEY (id_user_role)
      REFERENCES nzx.d_user_role (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE nzx.d_user
  OWNER TO nzxadmin;

-- Table: nzx.d_method_sma

-- DROP TABLE nzx.d_method_sma;

CREATE TABLE nzx.d_method_sma
(
  id serial NOT NULL,
  description character varying,
  first_ma integer NOT NULL,
  second_ma integer NOT NULL,
  fee_percent numeric,
  CONSTRAINT d_method_sma_pkey PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE nzx.d_method_sma
  OWNER TO nzxadmin;
  
-- Table: nzx.d_portfolio

-- DROP TABLE nzx.d_portfolio;

CREATE TABLE nzx.d_portfolio
(
  id serial NOT NULL,
  id_user integer NOT NULL,
  description character varying,
  CONSTRAINT d_portfolio_pkey PRIMARY KEY (id),
  CONSTRAINT d_portfolio_id_user_fk FOREIGN KEY (id_user)
      REFERENCES nzx.d_user (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE nzx.d_portfolio
  OWNER TO nzxadmin;
  
-- Table: nzx.f_portfolio

-- DROP TABLE nzx.f_portfolio;

CREATE TABLE nzx.f_portfolio
(
  id_portfolio integer NOT NULL,
  id_company integer NOT NULL,
  CONSTRAINT f_portfolio_id_company_fkey FOREIGN KEY (id_company)
      REFERENCES nzx.d_company (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT f_portfolio_id_portfolio_fkey FOREIGN KEY (id_portfolio)
      REFERENCES nzx.d_portfolio (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE nzx.f_portfolio
  OWNER TO nzxadmin;
  
-- Table: nzx.f_quotes

-- DROP TABLE nzx.f_quotes;

CREATE TABLE nzx.f_quotes
(
  id serial NOT NULL,
  id_company integer,
  close_date date,
  open_price numeric,
  high_price numeric,
  low_price numeric,
  close_price numeric,
  volume numeric,
  adj_close numeric,
  CONSTRAINT f_quotes_id_company_fkey FOREIGN KEY (id_company)
      REFERENCES nzx.d_company (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE nzx.f_quotes
  OWNER TO nzxadmin;

-- Table: nzx.f_method_sma_values

-- DROP TABLE nzx.f_method_sma_values;

CREATE TABLE nzx.f_method_sma_values
(
  id serial NOT NULL,
  id_company integer,
  close_date date,
  sma_value numeric,
  sma_length integer,
  CONSTRAINT f_method_sma_values_pkey PRIMARY KEY (id),
  CONSTRAINT f_method_sma_values_id_company_fkey FOREIGN KEY (id_company)
      REFERENCES nzx.d_company (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE nzx.f_method_sma_values
  OWNER TO nzxadmin;

 -- Table: nzx.f_forecast

-- DROP TABLE nzx.f_forecast;

CREATE TABLE nzx.f_forecast
(
  id serial NOT NULL,
  id_method integer,
  id_company integer,
  forecast_date date,
  forecast integer,
  result numeric,
  succeeded integer,
  CONSTRAINT f_forecast_pkey PRIMARY KEY (id),
  CONSTRAINT f_forecast_id_company_fkey FOREIGN KEY (id_company)
      REFERENCES nzx.d_company (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT f_forecast_id_method_fkey FOREIGN KEY (id_method)
      REFERENCES nzx.d_method_sma (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE nzx.f_forecast
  OWNER TO nzxadmin;

-- Table: nzx.f_forecast_overnight

-- DROP TABLE nzx.f_forecast_overnight;

CREATE TABLE nzx.f_forecast_overnight
(
  id serial NOT NULL,
  id_method integer,
  id_company integer,
  forecast_date date,
  forecast integer,
  result numeric,
  succeeded integer,
  CONSTRAINT f_forecast_on_pkey PRIMARY KEY (id),
  CONSTRAINT f_forecast_on_id_company_fkey FOREIGN KEY (id_company)
      REFERENCES nzx.d_company (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE,
  CONSTRAINT f_forecast_on_id_method_fkey FOREIGN KEY (id_method)
      REFERENCES nzx.d_method_sma (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITH (
  OIDS=FALSE
);
ALTER TABLE nzx.f_forecast_overnight
  OWNER TO nzxadmin;

 --insert initial values
 --insert user roles
 INSERT INTO nzx.d_user_role (id, description) VALUES (1, 'administrator');
 INSERT INTO nzx.d_user_role (id, description) VALUES (2, 'public');
 
 --insert default user
 INSERT INTO nzx.d_user (login, pass,  id_user_role) VALUES ('admin', 'admin', 1);
                    
 


