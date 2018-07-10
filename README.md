# ALEX for Jira (Adapter) 
 
The *ALEX for Jira* adapter allows the mapping of tests between [Zephyr for Jira][zephyr] and the test automation tool [ALEX][alex]. 
The adapter is coupled with the corresponding [add-on][alex-plugin] and is developed with the following technologies: 
 
* Spring Boot 1.5 (Java) 
* VueJS 2 (JavaScript) 
* H2 Database
* Jooq 
 
## Dependencies

* ALEX v1.6.0
 
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
mvn clean install "-Pcreate-frontend"
``` 
 
Afterwards, the file `alex-zephyr-jira-adapter-1.0.0.jar` is located in the `target` directory. 
Move the file to the location where the adapter should be installed. 
 
 
## Configuration

The configuration for the adapter is specified in a config `.ini` file:

```
// config.ini

# The URL where the adapter is reached in the internet by other applications.
# Add the URL without trailing "/"
app.url=

# Browser specific settings where tests are executed.
# The name of the browser in [chrome, firefox, htmlUnit, remote]. Make sure the browser is configured in ALEX.
app.browser.name=
# The width of the browser window. Has to be > 0
app.browser.width=
# The height of the browser window. Has to be > 0
app.browser.height=
# If the tests should be executed in headless mode in [true, false]. Default should be 'true'-
app.browser.headless=

# The URL where ALEX is reached without trailing "/".
alex.url=
# The credentials that are used to authorize in ALEX.
# Use the account that also contains the related projects.
alex.email=
alex.password=

# The URL where Jira is reached without trailing "/".
jira.url=
# The credentials that are used to authorize in Jira.
# Use an account that has the necessary rights to access the ZAPI REST API.
jira.username=
jira.password=
# Restrict the usage of the adapter to only some Jira projects by their IDs.
# IDs have to be specified as comma separated list, e.g. 10000,10211.
# Leave the field empty to allow access to all projects in Jira.
jira.allowed-project-ids=
```
 
The URLs have to be specified **without** trailing slash:

Wrong:    *http://localhost:8080/* <br>
Correct:  *http://localhost:8080*

When starting the adapter, make sure you provide the config file via a command line argument, see below.

 
## Starting the adapter 
 
The adapter does not require an installation. 
Instead, open a terminal an navigate to where the jar file is located. 
Then, execute the following command in the terminal to start the application: 
 
```bash 
java -jar jira-zephyr-jira-adapter-1.0.0-SNAPSHOT.jar --app.config-file="C:\...\config.ini"
``` 
 
Then, open a browser and go to *http://localhost:8080*. 
Per default, the port 8080 is used. 
To change the port, append the command line argument `--server.port=XXXX` (where *XXXX* is the new port) to the start command. 
 
After the first start, a *target* folder is created in the directory where the application has been started.
Inside that folder relies the database, so it is advised not to delete it.
  
 
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
[alex-plugin]: https://bitbucket.org/abainczyk/alex-zephyr-jira-plugin
[jira]: https://de.atlassian.com/software/jira/download 
[zephyr]: https://marketplace.atlassian.com/apps/1014681/zephyr-for-jira-test-management?hosting=server 
[zapi]: https://marketplace.atlassian.com/apps/1211674/zapi?hosting=server&tab=overview 
[atlassian-sdk]: https://developer.atlassian.com/server/framework/atlassian-sdk/ 
[swagger]: https://swagger.io/
