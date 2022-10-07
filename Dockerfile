FROM adoptopenjdk/openjdk11:ubi
ADD target/price_monitoring_system-1.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]