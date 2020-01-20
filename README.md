# SAP access web service

Language: Java

Framework: Spring Boot

The purpose of this project is to provide the access to SAP system through our API.

Net protocol is HTTPS

The project is under development


## API

{server address}/api

* /systems - get the list of available SAP systems;

* /login/{system}/{username}/{password} - authorize in SAP system and get the access token;\n
Example: https://localhost:8443/TS1/admin/changeme

The list will be replenished