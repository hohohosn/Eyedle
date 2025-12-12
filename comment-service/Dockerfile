FROM eclipse-temurin:17-jdk AS runtime
# 빌드된 JAR 파일 경로를 변수로 지정
ARG JAR_FILE_PATH=build/libs/comment-service-0.0.1-SNAPSHOT.jar
# JAR 파일을 이미지 안으로 복사
COPY ${JAR_FILE_PATH} app.jar
# 애플리케이션 포트 오픈
EXPOSE 19500
# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar", "--server.port=19200"]