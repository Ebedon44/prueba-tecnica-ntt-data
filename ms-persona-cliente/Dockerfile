FROM amazoncorretto:21-alpine

LABEL maintainer="com.banco"

# Añadimos un volumen temporal
VOLUME /tmp

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]