# Deduplicator
A program to manage duplicate files

# Usage instructions

## Start the deduplicator service

##### Prerequisites:
- MYSQL service running
- Have a MYSQL user with enough access to create a nd modify table and database structure

##### Configuration file setup:

After cloning the repo navigate to `Code/deduplicator/src/main/resources` and change the following fields according to mysql server settings and mysql user's username and password

|Property| Description|
|-|-|
|`spring.datasource.username`|mysql user's username|
|`spring.datasource.password`|mysql user's password|
|`spring.datasource.url` | mysql service url and port|
|`server.port`| the deduplicator service port| 


##### Compiling:

In order to compile the application navigate to Code/deduplicator and run the Gradle wrapper by opening the terminal and writing the following command: <br>`./gradlew build` 

The gradle wrapper will create an executable jar in the folder `Code/deduplicator/build/libs/`

##### Executing:
To start the deduplicator service you can run it by simply executing the compiled .jar with java using the following command: <br>
`java -jar build/libs/deduplicator-0.0.8.jar` 

If you want to execute the program s a service follow [this](https://www.baeldung.com/spring-boot-app-as-a-service#on-linux) guide that has a good explanation on how to daemonise the compiled application

## Start the deduplicator GUI server

