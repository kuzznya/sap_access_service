# SAP access RESTful Web service

![Java CI with Maven](https://github.com/kuzznya/sap_access_service/workflows/Java%20CI%20with%20Maven/badge.svg)
[![codecov](https://codecov.io/gh/kuzznya/sap_access_service/branch/master/graph/badge.svg)](https://codecov.io/gh/kuzznya/sap_access_service)

Company: ALPE Consulting

Language: Java

Framework: Spring Boot

The purpose of this project is to provide the access to SAP system through our API.

Response type: JSON

*The project is under development*

Latest version is 0.3.3


## API (v.0.3.3)

All methods except those marked with `w/o auth` need authorization  
Authorization token needs to be placed as `Authorization` header of the request and needs to be prefixed as 'Bearer '

{server address}/api/*

* **GET /token-lifetime** - returns token lifetime (default 600 sec)

* **GET /systems** (`w/o auth`) - get the list of available SAP systems  
  Response: list of systems' names
  
 * **GET /apps** - returns available SAP modules (see [Applications](#Applications))

### Authorization

* **POST /auth** (`w/o auth`) - authorize in SAP system and get the access token  
  Body (JSON):

  | Key        | Description               | Required |
  |------------|---------------------------|----------|
  | `system`   | The name of the system    | true     |
  | `username` | -                         | true     |
  | `password` | -                         | true     |
  | `language` | Lang. of responses (char) | false    |
  
  Response: access token (String)

* **PUT /auth** - refresh access token (notify server that the client is active)

* **DELETE /auth** - delete user  

* **GET /auth** - returns HTTP status "OK" (200) if active user with such access token exists  

### App 1 - Table data

__/apps/1/*__

* **GET /table** - get table from SAP and format it as list of records (not columns)
  Parameters:
  
  | Parameter     | Description                 | Required |
  |---------------|-----------------------------|----------|
  | `name`        | Name of the table           | true     |
  | `offset`      | Get records from this offset| false    |
  | `count`       | Get exact count of records  | false    |
  | `lang`        | Language (char)             | false    |
  | `where`       | "WHERE" SAP condition       | false    |
  | `order`       | The order of the data       | false    |
  | `group`       | "GROUP" SAP condition       | false    |
  | `fields_names`| Names of fields (cols)      | false    |
  
  Multiple values in parameter need to be separated by space code (%20)
  
  Example: `GET https://localhost:8443/api/apps/1/table?name=TBL1&offset=50&count=100&fields_names=MANDT%20BUKRS&lang=R`

* **GET /dataset** - get table from SAP without formatting it (as list of columns)  
  Parameters are the same as in the method GET /table  
  
  Response:  
  Data map (\<String, List\<String\>\>) with special system fields:
  
  | Field        | Description                           |
  |--------------|---------------------------------------|
  | `columnLen`  | width of column in SAP table          |
  | `fieldNames` | System names of fields (cols)         |
  | `dataTypes`  | Field types                           |
  | `repText`    | Names of fields on requested language |
  | `domNames`   | Technical names of fields             |
  | `outputLen`  | max number of symbols for field       |
  | `decimals`   | number of floating decimals           |

## App 3 - Chart data

__/apps/3/*__

* **GET /data** - get chart data  
  Get data from SAP & format it as list of records (points):
  `{value, category, caption}`
  
  | Parameter     | Description                 | Required |
  |---------------|-----------------------------|----------|
  | `table`       | Name of the table           | true     |
  | `values`      | Values column               | true     |
  | `categories`  | Categories column           | false    |
  | `captions`    | Captions column             | false    |

*The list will be replenished*

## Applications

1. Table data
2. Add information from QR to SAP (currently nonexistent)
3. Chart data

## Configguration

Environment variables required for server:

* `PORT` - server port
* `DB_USERNAME` - username for H2 in-memory database
* `DB_PASSWORD` - password for database
* `REQUIRE_SSL` - is SSL protocol required

## Start server (package)

`java -jar sap_access_service-<VERSION>.jar`

## Start server (dev, from source code)

Maven required

`mvn spring-boot:run`

## Setup

These command line args perform configuration & then start server

* Add new system:  
  `-add <NAME>=<address>`
* Remove system:  
  `-remove <NAME>`
* Set token lifetime (& also cached table & chart data)  
  `-token_lifetime`/`-tl` `<TIME (seconds)`

## Additional information

Current configuration is now stored in the same folder as the program (*will be changed later (maybe)*)
in files `systems.properties` (list of available systems with addresses) & `params.properties` (token lifetime and other data)
