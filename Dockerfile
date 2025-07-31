# 1. JDK가 설치된 베이스 이미지 사용
FROM openjdk:17-jdk-slim

# 2. JAR 파일을 이미지 내부로 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 3. 컨테이너 시작 시 실행할 명령
ENTRYPOINT ["java", "-jar", "/app.jar"]
