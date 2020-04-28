DROP TABLE IF EXISTS saptables;

CREATE TABLE saptables (
    id INT PRIMARY KEY,
    accessToken VARCHAR(255) NOT NULL,
    paramsHash INT UNIQUE NOT NULL,
    tableName VARCHAR(30) NOT NULL,
    tableRecordsCount INT DEFAULT NULL,
    tableFull BOOL DEFAULT UNKNOWN,
    tableLanguage CHAR DEFAULT NULL,
    tableWhere VARCHAR(40) DEFAULT NULL,
    tableOrder VARCHAR(40) DEFAULT NULL,
    tableGroup VARCHAR(40) DEFAULT NULL,
    tableFieldNames VARCHAR(50) DEFAULT NULL,
    tableData CLOB NOT NULL,
    creationDate TIMESTAMP DEFAULT now(),
    updateDate TIMESTAMP DEFAULT now()
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