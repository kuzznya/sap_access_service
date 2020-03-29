DROP TABLE IF EXISTS saptables;

CREATE TABLE saptables (
    id INT PRIMARY KEY,
    access_token VARCHAR(255) NOT NULL,
    params_hash INT UNIQUE NOT NULL,
    table_name VARCHAR(30) NOT NULL,
    table_records_count INT DEFAULT NULL,
    table_full BOOL DEFAULT UNKNOWN,
    table_language CHAR DEFAULT NULL,
    table_where VARCHAR(40) DEFAULT NULL,
    table_order VARCHAR(40) DEFAULT NULL,
    table_group VARCHAR(40) DEFAULT NULL,
    table_field_names VARCHAR(50) DEFAULT NULL,
    table_data CLOB NOT NULL,
    creation_date TIMESTAMP DEFAULT now(),
    update_date TIMESTAMP DEFAULT now()
);

DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT PRIMARY KEY,
    system VARCHAR(10) NOT NULL,
    username VARCHAR(30) NOT NULL,
    password VARCHAR(40) NOT NULL,
    language CHAR DEFAULT NULL,
    access_token VARCHAR(255) UNIQUE NOT NULL,
    accessed TIMESTAMP DEFAULT now()
);