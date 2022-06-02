## Introduction
Application for calculating congestion tax fees for vehicles within the Gothenburg area

## Prerequisites
* Java 17
* Spring Boot
* Lombok
* Postman

## Steps to starting application 

### Build the application
```
mvn -N wrapper:wrapper
```

```
mvnw clean install
```

### Run the application
```
java -jar .\target\congestion-tax-calculator-0.0.1-SNAPSHOT.jar
```

### Run the application with different configuration properties
```
java -jar .\target\congestion-tax-calculator-0.0.1-SNAPSHOT.jar --spring.config.location=<ConfigFile Path>
```

## Swagger 
Endpoint : http://localhost:8181/swagger-ui.html

## Test the application
REST Endpoint:

*API URL:* http://localhost:8181/api/v1/road-tolls/congestiontax

*API method:*  **POST**

**Input Json:**

    {
        "vehicleType": "Car",
        "dates": [
            "2013-01-14 21:00:00","2013-01-15 21:00:00",
            "2013-02-07 06:23:27","2013-02-07 15:27:00",
            "2013-02-08 06:27:00","2013-02-08 06:20:27",
            "2013-02-08 14:35:00","2013-02-08 15:29:00",
            "2013-02-08 15:47:00","2013-02-08 16:01:00",
            "2013-02-08 16:48:00","2013-02-08 17:49:00",
            "2013-02-08 18:29:00","2013-02-08 18:35:00",
            "2013-03-26 14:25:00","2013-03-28 14:07:27"
        ]
    }
Output:

       {
    		"vehicleType": "Car",
    		"tax": 58
       }