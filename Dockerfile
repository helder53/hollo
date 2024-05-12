FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} hollo.jar
CMD ["java", "-jar", "hollo.jar"]