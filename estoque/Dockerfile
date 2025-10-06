FROM maven:3.9.5-eclipse-temurin-21-alpine AS builder

WORKDIR /app

COPY pom.xml .
COPY pedidos/pom.xml pedidos/
COPY estoque/pom.xml estoque/
COPY estoque/src estoque/src

RUN mvn dependency:go-offline -B -DskipTests

RUN mvn clean install -pl :estoque -am -DskipTests -B

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
EXPOSE 8082

COPY --from=builder /app/estoque/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]