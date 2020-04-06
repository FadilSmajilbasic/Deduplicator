# Deduplicator
A program to manage duplicate files

# Install and startup instructions

## Start the deduplicator REST service

##### Requisites:
- MYSQL service running
- Have a MYSQL user with enough access to create and modify table and database structure
- nodejs version >12.16 
- npm version >6.13.4 

##### Configuration file setup:

After cloning the repo navigate to `5_Applicativo/deduplicator/src/main/resources` and change the following fields according to mysql server settings and mysql user's username and password

|Property| Description|
|-|-|
|`spring.datasource.username`|mysql user's username|
|`spring.datasource.password`|mysql user's password|
|`spring.datasource.url` | mysql service url and port|
|`server.port`| the deduplicator service port| 


##### Compiling:

In order to compile the application navigate to `5_Applicativo/deduplicator` and run the Gradle wrapper by opening the terminal and writing the following command: <br>`./gradlew build` 

The gradle wrapper will create an executable jar in the folder `5_Applicativo/deduplicator/build/libs/`

##### Executing:
To start the deduplicator service you can run it by simply executing the compiled .jar with java using the following command: <br>
`java -jar build/libs/deduplicator-0.0.8.jar` 

If you want to execute the program as a service follow [this](https://www.baeldung.com/spring-boot-app-as-a-service#on-linux) guide that has a good explanation on how to daemonise the compiled application

## Start the deduplicator GUI server
##### Requisites:
- maven version >3.6.0 
#### Compiling

Navigate to `5_Applicativo/deduplicatorVaadinGUI/` 
Run the following command to build an executable jar: `mvn clean package`
Maven will build the jar executable file in the `5_Applicativo/deduplicatorVaadinGUI/target` folder.

##### Executing as a program:

You can run the program as is by simply navigating to the `5_Applicativo/deduplicatorVaadinGUI/target` folder and running it in the terminal like this with the following command `./deduplicatorgui-2.0.jar`

It is possible to make a symbolic link in order to run the program from anywhere. You can do that by running the following command: 
`sudo ln -s /complete/path/to/jar/deduplicatorgui-2.0.jar /sbin/deduplicatorGUI`

Now you can simply write `deduplicatorGUI` and the application will start.

##### Executing as a service:

You need to make a  symbolic link in order to run the program as a service. You can do that by running the following command: 
`sudo ln -s /complete/path/to/jar/deduplicatorgui-2.0.jar /etc/init.d/deduplicatorGUI`

In order to start the service execute the following command: `sudo service deduplicatorGUI start` 