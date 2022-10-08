# PRICE-MONITORING-SYSTEM

**This _PRESTful_ _MVC_ web-application on _Java 11_ uses the following technologies**:

- **_Spring Boot_**, 
- **_Spring Data_**, 
- **_Spring Security_**,
- **_Hibernate_**
- **_Maven_**
- **_JUnit 5_**, **_Mockito_**
- **_Docker_**
- **_Swagger Open API_**
- **_Slf4j-log4j12_**

## Setting Up Development Environment

**For installing app you need _Maven_, _Docker_:**

**Steps for run:**

**Let's clone repo into your workspace:**

1) `git clone git@github.com:zxc3buttons/price-monitoring-system-on-spring-boot.git` 

**In your work folder run cmd and do:**

2) `mvn clean`

3) `mvn package`

4) `docker image build .`

5) `docker image build ./db`

6) `docker compose up` in your work folder
