FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl unzip && \
    curl -L "https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip" -o newrelic-java.zip && \
    unzip newrelic-java.zip && \
    rm newrelic-java.zip

COPY --from=build /app/target/finance-control-api-1.0.0.jar app.jar
COPY --from=build /app/src/main/resources/newrelic.yml /app/newrelic/newrelic.yml
EXPOSE 8080
ENTRYPOINT ["java", "-javaagent:/app/newrelic/newrelic.jar", "-Dnewrelic.config.file=/app/newrelic/newrelic.yml", "-Dnewrelic.environment=production", "-jar", "app.jar"]
