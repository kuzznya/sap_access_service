# SAP access RESTful Web service

Company: ALPE Consulting

Language: Java

Framework: Spring Boot

The purpose of this project is to provide the access to SAP system through our API.

Response type: JSON

For HTTPS connection self-signed certificate is used now

*The project is under development*


## API

{server address}/api

* **GET /systems** - get the list of available SAP systems  
  Response: list of systems' names

* **POST /auth** - authorize in SAP system and get the access token  
  Parameters: system

  | Parameter  | Description               | Required |
  |------------|---------------------------|----------|
  | `system`   | The name of the system    | true     |
  | `username` | -                         | true     |
  | `password` | -                         | true     |
  | `lang`     | Language of the responses | false    |
  
  Response: access token (String)
  
  Example: `https://localhost:8443/api/auth?system=TS1&username=admin&password=changeme`

* **PUT /auth** - refresh access token (notify server that the client is active)  
  Parameters:
  
  | Parameter     | Description                 | Required |
  |---------------|-----------------------------|----------|
  | `access_token`| Access token of the client  | true     |

* **DELETE /auth** - kill session of this client  
  Parameters:
  
  | Parameter     | Description                 | Required |
  |---------------|-----------------------------|----------|
  | `access_token`| Access token of the client  | true     |

* **GET /auth** - returns HTTP status "OK" (200) if active session with such access token exists  
  Parameters:
  
  | Parameter     | Description                 | Required |
  |---------------|-----------------------------|----------|
  | `access_token`| Access token of the client  | true     |
  
* **GET /sessions-lifetime** - returns sessions lifetime (default 600 sec)

* **GET /apps** - returns available SAP modules (see [Applications](#Applications))  
  Parameters:
    
    | Parameter     | Description                 | Required |
    |---------------|-----------------------------|----------|
    | `access_token`| Access token of the client  | true     |

* **GET /table** - get table from SAP  
  Parameters:
  
  | Parameter     | Description                 | Required |
  |---------------|-----------------------------|----------|
  | `access_token`| Access token of the client  | true     |
  | `name`        | Name of the table           | true     |
  | `recs_count`  | Count of the records (lines)| false    |
  | `lang`        | Language                    | false    |
  | `where`       | "WHERE" SAP condition       | false    |
  | `order`       | The order of the data       | false    |
  | `group`       | "GROUP" SAP condition       | false    |
  | `fields_names`| Names of fields (cols)      | false    |
  
  Multiple values in parameter need to be separated by space code (%20)
  
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
  
  Example: `https://localhost:8443/api/table?access_token=12345&name=TBL1&fields_names=MANDT%20BUKRS&lang=R`

*The list will be replenished*

## Applications

1. Application A. Table view
2. Application Z. Add information from QR to SAP

## Download

[Latest test build](../master/builds/sap_access_service-0.0.1-SNAPSHOT.jar)

[View all builds](../master/builds)

## Configuration

All configuration could be done from the console

* Add new system:  
  `-config -add -name [system name] -address [address]`
* Remove system:  
  `-config -remove -name`
* Set session lifetime:  
  `-config -set_session_lifetime`


## Program launch

* Test some features (for development only):  
  `-test`
* Print sessions info when checking active/inactive sessions:  
  `-print_sessions_info`

## Additional information

Current configuration is now stored in the same folder as the program (*will be changed later (maybe)*)
in files `systems.properties` (list of available systems with addresses) & `params.properties` (session lifetime and other data)

## TODO

* ~~Multiple values of the parameters of GET /table (i.e. fields_names) (!)~~
* ~~JAR build attempt (!)~~
* ~~API documentation~~
* ~~Server configuration and launch documentation~~
* ~~Session language management~~
* ~~Add token check & token refresh methods to the API~~
* ~~"get available modules" method~~
* Set up the period of sessions activity normally
* PropertiesHolder class refactoring (+-)
* SapMap and XMLResponse classes refactoring (?)
* Localization table from SAP (need to be added to SAP first)