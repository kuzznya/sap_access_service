# SAP access RESTful Web service

Company: ALPE Consulting

Language: Java

Framework: Spring Boot

The purpose of this project is to provide the access to SAP system through our API.

Response type: JSON

For HTTPS connection self-signed certificate is used now

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

### App 1

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

*The list will be replenished*

## Applications

1. Table view
2. Add information from QR to SAP

## Download

[Latest test build (v.0.3.3)](../master/builds/sap_access_service-0.3.3.jar)

[View all builds](../master/builds)

## Configuration

All configuration could be done from the console

* Add new system:  
  `-config -add -name [system name] -address [address]`
* Remove system:  
  `-config -remove -name`
* Set appUser lifetime:  
  `-config -set_session_lifetime`


## Program launch

* Test some features (for development only):  
  `-test`
* Print sessions info when checking active/inactive sessions:  
  `-print_sessions_info`

## Additional information

Current configuration is now stored in the same folder as the program (*will be changed later (maybe)*)
in files `systems.properties` (list of available systems with addresses) & `params.properties` (token lifetime and other data)
