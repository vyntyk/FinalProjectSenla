FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY target/food-monitoring-system.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
