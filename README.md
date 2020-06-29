
# Quotes - A quotes history aggregation system

[Qu]otes is a service responsible for aggregating and returning the quotes from a third party provider and making this available through a REST endpoint.

## Development
Run tests:
`./mvnw clean verify`

Build project:
`./build.sh`

run Application
`./run.sh`
###### NB: This starts the redis db, runs the partner service and starts the application
Management Page
```
http://localhost:6050

```
###### Visit management page for links to basic service functions
URL for Swagger

```
http://localhost:6050/swagger-ui.html

```


run the application as a spring-boot app 

```
mvn spring-boot:run
```