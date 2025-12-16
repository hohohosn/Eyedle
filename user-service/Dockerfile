# JDK가 포함된 기본 이미지 사용
FROM eclipse-temurin:17-jdk AS runtime

# 빌드된 JAR 파일 경로를 변수로 지정
ARG JAR_FILE_PATH=build/libs/user-service-0.0.1-SNAPSHOT.jar

# JAR 파일을 이미지 안으로 복사
COPY build/libs/*.jar app.jar

# 애플리케이션 포트 오픈
EXPOSE 19100

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]