FROM maven:3.8.1-openjdk-17 AS MAVEN

MAINTAINER BOTTOMHALF

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/
RUN mvn package

FROM openjdk:17-oracle
WORKDIR /app
EXPOSE 8090

COPY --from=MAVEN /build/target/axilcorps_coreservice.jar /app/

ENTRYPOINT ["java", "-jar", "axilcorps_coreservice.jar", "--spring.profiles.active=prod"]