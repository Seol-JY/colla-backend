FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar

# 로그 디렉토리 생성
RUN mkdir /logs

# 애플리케이션 JAR 파일 복사
COPY ${JAR_FILE} app.jar

# 로그 디렉토리에 대한 권한 설정
RUN chmod 777 /logs

ENTRYPOINT ["java","-jar","app.jar","--spring.profiles.active=prod","-Duser.timezone=Asia/Seoul"]

# 볼륨 설정
VOLUME ["/logs"]
