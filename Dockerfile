FROM openjdk:17

ENV TZ=Asia/Seoul
RUN apt-get update && apt-get install -y tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=prod","-Duser.timezone=Asia/Seoul"]
