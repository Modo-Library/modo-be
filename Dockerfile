FROM openjdk:17-alpine
LABEL maintainer="wkazxf@gmail.com"
WORKDIR /deploy/app
ENV TZ="Asia/Seoul"
VOLUME /tmp
ARG JAR_FILE_PATH=build/libs/*.jar
COPY ${JAR_FILE_PATH} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]