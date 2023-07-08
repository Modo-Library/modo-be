FROM openjdk:17-alpine
LABEL maintainer="wkazxf@gmail.com"
ENV TZ="Asia/Seoul"
VOLUME /tmp
ARG JAR_FILE_PATH=build/libs/*.jar
COPY ${JAR_FILE_PATH} app.jar

HEALTHCHECK --interval=5s --timeout=5s --start-period=15s --retries=10 CMD wget http://localhost:8080/actuator/health --quiet --output-document - >/dev/null 2>&1

ENTRYPOINT ["java","-jar","/app.jar"]