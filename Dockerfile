FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
COPY *.json ./

RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/*.json ./

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]