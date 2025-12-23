#!/bin/bash

# 서비스 목록
services=("eureka-server" "gateway" "user-service" "feed-service" "comment-service" "notification-service" "chat-service" "search-service" "monitor-service")

for service in "${services[@]}"; do
    echo "========================================="
    echo "🚀 Processing Service: $service"
    echo "========================================="

    # 1. 폴더 이동 후 Gradle 빌드
    cd $service
    ./gradlew clean build -x test

    # 실패 시 스크립트 종료
    if [ $? -ne 0 ]; then
        echo "❌ Gradle Build Failed for $service"
        exit 1
    fi

    # 루트로 복귀
    cd ..

    # 2. Docker 빌드
    echo "🐳 Docker Image Build..."
    docker build -t eyedle/$service:latest ./$service
done

echo "✅ All builds completed!"