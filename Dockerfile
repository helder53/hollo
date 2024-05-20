FROM openjdk:17
WORKDIR /hollo
COPY build/libs/hollo-0.0.1-SNAPSHOT.jar hollo.jar
CMD ["java", "-jar", "hollo.jar"]