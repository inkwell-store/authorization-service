# Stage 1 - setup a jdk build image
FROM eclipse-temurin:25.0.4_7-jdk-noble AS build_stage

WORKDIR /opt/auth-service

COPY . ./

RUN ./mvnw clean package

# Stage 2 - setup the jre image
FROM eclipse-temurin:25.0.4_7-jre-noble AS runtime_stage

WORKDIR /opt/auth-service

COPY --from=build_stage /opt/auth-service/target/authorization-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "authorization-0.0.1-SNAPSHOT.jar"]

EXPOSE 8081