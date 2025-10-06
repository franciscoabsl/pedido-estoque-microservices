FROM maven:3.9.5-eclipse-temurin-21-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY pedidos/pom.xml pedidos/
COPY pedidos/src pedidos/src
COPY estoque/pom.xml estoque/

RUN mvn dependency:go-offline -B -DskipTests

RUN mvn clean install -pl :pedidos -am -DskipTests -B

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
EXPOSE 8081

COPY --from=builder /app/pedidos/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]