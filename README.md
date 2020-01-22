# SAP access web service

Language: Java

Framework: Spring Boot

The purpose of this project is to provide the access to SAP system through our API.

Net protocol is HTTPS

The project is under development


## API

{server address}/api

* GET /systems - get the list of available SAP systems;

* GET /login - authorize in SAP system and get the access token;
<br>Parameters: system (system name), username, password
<br>Example: https://localhost:8443/api/login?system=TS1&username=admin&password=changeme

* GET /table - get table from SAP
<br>Parameters:

The list will be replenished