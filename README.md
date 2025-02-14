# Online Library - Backend (Spring Boot + H2 Database)

## Overview
This is the backend of the Online Library application, built with **Spring Boot**, **H2 Database (in-memory)**, and **Spring Data JPA**. It provides RESTful APIs for managing books and generating AI-powered insights.

## Setup and Installation
Clone the repository and navigate to the backend folder:
```sh
git clone https://github.com/user/online-library.git
cd online-library/backend```

This project uses an in-memory H2 database, so no external database setup is required.

Before running the application, make sure to build the project and install dependencies:
mvn clean install

To start the backend server, run: 
mvn spring-boot:run

or if using Gradle: 
./gradlew bootRun

Once the server is running, access the API documentation at:http://localhost:8080/swagger-ui/index.html

Running Tests
To execute unit and integration tests, run: mvn test
To generate a code coverage report: mvn jacoco:report

Locate the JaCoCo Report
target/site/jacoco/index.html

The OpenAI API Key is stored in the application.properties file.
Make sure to add your key before running the application:

openai.api.key=your-api-key-here