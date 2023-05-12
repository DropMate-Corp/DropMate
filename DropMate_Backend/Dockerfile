FROM maven:3.8.3-openjdk-17
WORKDIR /app
COPY . .      

# At build time, only compile the application but do not run it
RUN mvn clean install

# When you launch the container, this will be the main command
CMD mvn spring-boot:run


#FROM openjdk:17-oracle
#ARG JAR_FILE=target/*.jar
#COPY ${JAR_FILE} app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]
