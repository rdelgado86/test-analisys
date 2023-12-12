# 4Core - Account API


<https://sodexo-brs.atlassian.net/wiki/spaces/MF4/overview>




### Environment Variables

	Datasource URL = ${DB_URL}
	Datasource Username = ${DB_USER}
	Datasource Password = ${DB_PASS}



## External Libs

**Spring Web** provides the basic Spring MVC features that allow you to create web services.

**Project Lombok** is an annotation-based helper that saves you from creating many getters, setters, and constructors. Check out their website to see all the features.

**Spring Data JPA** includes the ability to interact with databases and map Java objects to data model instances and database structures.

**H2 Database** adds an in-memory database implementation that does not persist between sessions and is excellent for testing and projects like this.



### Open API 3

<http://localhost:8080/swagger-ui/index.html>

<http://localhost:8080/api-docs>

<http://localhost:8080/api-docs.yaml>


### Actuator Metrics

<http://localhost:8080/actuator/health>

<http://localhost:8080/actuator/metrics>



## Building da aplicação

Just run:

	$ ./mvnw clean install


Docker: 

	docker run -d -p 8080:8080 -t template-api:1.0


5 - Access it

<http://localhost:8080>



