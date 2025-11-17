FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080
EXPOSE 5005

ENTRYPOINT ["sh", "-c", "java $JAVA_TOOL_OPTIONS -jar app.jar"]
