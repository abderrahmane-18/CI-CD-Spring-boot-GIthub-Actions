# ===== BUILD STAGE =====
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn --batch-mode --no-transfer-progress dependency:go-offline
COPY src ./src
RUN mvn --batch-mode --no-transfer-progress clean verify

# ===== RUN STAGE =====
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN groupadd --system spring && useradd --system --gid spring --home-dir /app --shell /usr/sbin/nologin spring
COPY --from=build --chown=spring:spring /app/target/employee-manager.jar app.jar
USER spring:spring
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
