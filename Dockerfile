FROM adoptopenjdk/openjdk11:alpine-jre
# Refer to Maven build -> finalName
ARG JAR_FILE=target/user_rest_sample-0.0.1-SNAPSHOT.jar
# cd /opt/app
WORKDIR /opt/app
# cp target/spring-boot-web.jar /opt/app/app.jar
COPY ${JAR_FILE} app.jar
# Make it possible to access port 7000 in dockerfile from physical machine
EXPOSE 7000
# java -jar /opt/app/app.jar
ENTRYPOINT ["java","-jar","app.jar"]