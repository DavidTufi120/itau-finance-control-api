FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl unzip \
    && curl -fsSL "https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip" -o /tmp/newrelic-java.zip \
    && unzip -q /tmp/newrelic-java.zip -d /app \
    && rm /tmp/newrelic-java.zip \
    && mkdir -p /app/logs

COPY --from=build /app/target/finance-control-api-1.0.0.jar /app/app.jar
COPY --from=build /app/src/main/resources/newrelic.yml /app/newrelic/newrelic.yml

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -javaagent:/app/newrelic/newrelic.jar -Dnewrelic.environment=production -Dnewrelic.config.file=/app/newrelic/newrelic.yml -Dnewrelic.config.license_key=$NEW_RELIC_LICENSE_KEY -Dnewrelic.config.app_name=${NEW_RELIC_APP_NAME:-finance-control-api} -jar /app/app.jar"]

