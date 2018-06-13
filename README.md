# ALEX for Jira (Adapter) 
 
The *ALEX for Jira* adapter allows the mapping of tests between [Zephyr for Jira][zephyr] and the test automation tool [ALEX][alex]. 
The adapter is coupled with the corresponding [add-on][alex-plugin] and is developed with the following technologies: 
 
* Spring Boot 1.5 (Java) 
* VueJS 2 (JavaScript) 
* H2 Database
* Jooq 
 
 
## Requirements 
 
### Development 
 
* Java JDK 8 
* Maven v3 
* NodeJS v10 & NPM v6 
 
### System 
 
* Java JRE or JDK 8 
* x64 operating system 
* A running instance of [ALEX][alex] 
* A running instance of Jira with the *ALEX for Jira* add-on and its dependencies 
 
*Note that if the software products each run on different machines in the same network, they must be reachable for each other.* 
*To be precise, there must be a bidirectional connection between Jira and the adapter and a bidirectional connection between the adapter and ALEX.* 
 
 
## Build instructions 
 
```bash 
# 1. Clone the repository 
git clone ... 

# 2. Navigate to the project directory
cd alex-zephyr-jira-adapter

# 3. Build the application
mvn clean install  
``` 
 
Afterwards, the file `alex-zephyr-jira-adapter-1.0.0-SNAPSHOT.jar` is located in the `target` directory. 
Move the file to the location where the adapter should be installed. 
 
 
## Starting the adapter 
 
The adapter does not require an installation. 
Instead, open a terminal an navigate to where the jar file is located. 
Then, execute the following command in the terminal to start the application: 
 
```bash 
java -jar jira-zephyr-jira-adapter-1.0.0-SNAPSHOT.jar 
``` 
 
Then, open a browser and go to *http://localhost:8080*. 
Per default, the port 8080 is used. 
To change the port, append the command line argument `--server.port=XXXX` (where *XXXX* is the new port) to the start command. 
 
After the first start, a *target* folder is created in the directory where the application has been started.
Inside that folder relies the database, so it is advised not to delete it.
 
### Setup 
 
On the first start, you are asked to setup the adapter by providing login information for Jira and ALEX. 
For both applications, provide the corresponding base URL without a trailing */*.
 
 
## Development environment 
 
In the development environment, the backend and the frontend can be run separately.
To start the **backend**, execute `mvn spring-boot:run [-Dserver.port=XXXX]`. 
The REST API will then be available at *http://localhost:8080*. 
 
To start the **frontend** go to the *src/main/resources/app* directory and execute `npm run serve`. 
Then go to *http://localhost:9001*.  
To change the port, open the file *vue.config.js* and modify the following property accordingly: 
 
``` 
devServer: { 
  port: 9001 // change this to another port 
} 
``` 

## REST documentation
 
We documented the REST endpoints with [Swagger][swagger].
The documentation is available at */swagger-ui.html* in a running application, e.g. *http://localhost:8080/swagger-ui.html*.


[alex]: https://github.com/learnlib/alex 
[alex-plugin]: # 
[jira]: https://de.atlassian.com/software/jira/download 
[zephyr]: https://marketplace.atlassian.com/apps/1014681/zephyr-for-jira-test-management?hosting=server 
[zapi]: https://marketplace.atlassian.com/apps/1211674/zapi?hosting=server&tab=overview 
[atlassian-sdk]: https://developer.atlassian.com/server/framework/atlassian-sdk/ 
[swagger]: https://swagger.io/
