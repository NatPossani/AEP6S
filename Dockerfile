# =========================
# Etapa 1 - Build
# =========================
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src ./src

# Executa testes e gera o relatório de cobertura
RUN mvn clean package -DskipTests


# =========================
# Etapa 2 - Execução
# =========================
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/banco-alimentos-aep-1.0.0.jar app.jar

ENV MONGO_URI=mongodb://aep-mongo:27017

ENTRYPOINT ["java", "-jar", "app.jar"]
