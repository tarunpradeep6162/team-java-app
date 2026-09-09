FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/team-java-app.jar app.jar

ARG APP_VERSION=development

ENV APP_VERSION=${APP_VERSION}
ENV PORT=8080

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]o

