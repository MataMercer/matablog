FROM maven:3.8.3-openjdk-8-slim AS builder
WORKDIR /app
COPY . /app
ARG mvn_arg="clean package -DskipTests"

RUN --mount=type=cache,target=/root/.m2 mvn -f /app/pom.xml $mvn_arg

# Run jar
FROM openjdk:8-jre-slim

ARG JAR_FILE=/app/target/matablog-*.jar
ARG PROFILE="docker"
ENV profile_env ${PROFILE}

#ARG DB_HOSTNAME
#ENV db_hostname_env ${DB_HOSTNAME}
#ARG DB_PORT
#ENV db_port_env ${DB_PORT}
#ARG DB_NAME
#ENV db_name_env ${DB_NAME}
#ARG DB_USERNAME
#ENV db_username_env ${DB_USERNAME}
#ARG DB_PASSWORD
#ENV db_password_env ${DB_PASSWORD}
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=0.0.0.0:5005

COPY --from=builder $JAR_FILE /opt/app.jar
WORKDIR /opt/

#Create user to run the app and add folder permissions to it
RUN useradd -m myuser
RUN mkdir upload-dir
RUN chown -R myuser: upload-dir
USER myuser
RUN chmod -R a+rw upload-dir

EXPOSE 8080
CMD ["java", "-jar", "-Dspring.profiles.active=${profile_env}", "app.jar"]
