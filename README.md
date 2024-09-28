
## Introduction
The Congestion Tax Calculator is a simple application that calculates the congestion tax for vehicles. The application is built using Spring Boot and uses a Postgres database to store the data.

## Problem Statement
To find out more about the requirement refer [assignment](ASSIGNMENT.md).

## Assumptions
  - day before public holiday is also exempted from congestion tax. I have added few public holiday for Gothenburg, Sweden in the `exemption_periods` table in the database. Also I have added a day before public holiday with description as "Day before public holiday" in the exemption period table.Feel free to modify the script to add more public holidays.
  - to allow weekend exemption, I have added a column `weekend_exempted` flag in `cities` table.
  - to allow vehicle type exemption, I have added a column `exempted` flag in `vehicles` table.
  - to configure times for congestion tax, I have added a table `tax_rules`. You can add more rules for different cities and countries.

## Prerequisites
- Java 21
- Maven 3.8.1
- Docker 20.10.7
- Docker Compose 1.29.2
- Postman 8.10.0
- Postgres

## Getting Started
1. Run the following command to build the project:
```shell
mvnw clean install
```

2. Once you have docker and docker-compose installed, run the following command to start the postgres database:
```shell
docker-compose up
```

3. Run the following command to start the application:
```shell
java -jar target/congestion-tax-calculator-0.0.1-SNAPSHOT.jar
```
or 
```shell
mvnw spring-boot:run
```

__Note__: You can also start the application from your IDE.

4. The application will automatically create the tables in the database with some dummy data using flyway.

5. The db scripts are placed in `src/main/resources/db/migration`.

## Test the application
1. Open Postman and import the collection `Congestion-tax-calculator.postman_collection.json` from the root directory.

Sample request 
```json
{
  "vehicleType":"Car",
  "entryTimes" : [
    "2013-07-19T06:00:00",
    "2023-09-19T07:10:00",
    "2023-09-19T06:29:00"
  ],
  "city": "Gothenburg",
  "country": "Sweden"

}
```

## API Endpoints
You can view the API documentation by running the application and navigating to `http://localhost:8080/swagger-ui.html` in your browser.

## Configuration
To change the port, you can modify the `application.properties` file.
```properties
server.port=8080
```
