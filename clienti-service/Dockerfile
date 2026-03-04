FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml .
COPY clienti-service/pom.xml clienti-service/pom.xml
COPY conti-service/pom.xml conti-service/pom.xml

RUN mvn -q -pl clienti-service -am dependency:go-offline

COPY clienti-service/src clienti-service/src
RUN mvn -q -pl clienti-service -am clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /workspace/clienti-service/target/clienti-service-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
