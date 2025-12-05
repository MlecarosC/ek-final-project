# Etapa 1: Build (Construcción)
# Usamos una imagen con Maven y JDK 21 para compilar
FROM maven:3.9.11-eclipse-temurin-21 AS build

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar archivos de configuración de Maven primero (para aprovechar cache)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Compilar la aplicación y crear el JAR
RUN ./mvnw clean package -DskipTests

# Etapa 2: Runtime (Ejecución)
# Usamos una imagen más liviana solo con JRE 21
FROM eclipse-temurin:21-jre-alpine

# Directorio de trabajo
WORKDIR /app

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Puerto que expone la aplicación
EXPOSE 8085

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
