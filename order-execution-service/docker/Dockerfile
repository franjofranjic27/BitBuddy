FROM eclipse-temurin:21-jre

# Arbeitsverzeichnis im Container
WORKDIR /app

# Jar aus dem Maven-Build in den Container kopieren
COPY target/*.jar app.jar

# Port, den deine App nutzt (Spring Boot default = 8080)
EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
