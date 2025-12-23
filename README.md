# eyedle
- **프로젝트 목적** : eyedle은 대규모 트래픽 처리를 고려한 마이크로서비스 아키텍처(MSA) 기반의 소셜 플랫폼 백엔드 프로젝트입니다. Spring Boot 3.5와 Java 17을 기반으로 하며, 다양한 최신 기술 스택을 활용하여 확장성과 안정성을 확보하였습니다.
- **주요 기능** : 피드, 채팅, 실시간 알림, 실시간 인기검색어 기능 및 회원 인증,인가 기능을 지원
- **개발 기간** : 2025/11/24 ~ 2025/12/24

## 프로젝트 배포
- [URL](http://3.230.51.206:19091)

## 구조
- **`eureka-server/`**: 서비스 레지스트리
- **`gateway/`**: API 게이트웨이
- **마이크로서비스**: 각 디렉토리에 `build.gradle`, `Dockerfile`, `src/`가 포함
    - `comment-service/` 댓글 서비스
    - `notification-service/` 알림 서비스
    - `chat-service/` 채팅 서비스
    - `presence-service/` 접속 상태 확인 서비스
    - `feed-service/` 피드 서비스
    - `search-service/` 검색 서비스
    - `user-service/` 회원 및 인증,인가 서비스
    - `monitor-service/` 모니터링 서비스
- [공통모듈](https://github.com/sparta-Eyedle/msa-common-module) : `jit-pack`을 통한 라이브러리화

## 실행방법

### 사전 요구사항
- Docker
- Java 17 이상
- SpringBoot 3.5.8 이상


1. 환경변수 설정(.env)

```
# --------------------------------------------------------
# [Database Credentials]
# --------------------------------------------------------
POSTGRES_USER=eyedle
POSTGRES_PASSWORD=eyedlepassword

# 각 서비스별 DB 이름
POSTGRES_DB_USER=user_db
POSTGRES_DB_FEED=feed_db
POSTGRES_DB_COMMENT=comment_db
POSTGRES_DB_CHAT=chat_db
POSTGRES_DB_MONITOR=monitor_db

# MongoDB
MONGO_USER=eyedle
MONGO_PASSWORD=eyedlepassword
MONGO_DB_NOTI=notification_db

# --------------------------------------------------------
# [Infrastructure Config]
# --------------------------------------------------------
# Redis
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_SSL_ENABLED=false

# Kafka
KAFKA_BROKER=kafka:29092

# Elasticsearch
ES_URI=http://elasticsearch:9200

# Eureka
EUREKA_URI=http://eureka-server:19090/eureka/

# JWT
JWT_SECRET_KEY=VlwEyVlxSnXZ0bKKw_MZudDFYChWwdkBh9LZw6XBKpI=
```

2. 빌드
```powershell
od +x build_all.sh
./build_all.sh
```

3. 컨테이너 실행
```powershell
docker-compose up --build -d
```

## 기술 스택

### Backend
- **Language**: Java 17
- **Framework**: Spring Boot 3.5
- **Build Tool**: Gradle
- **Database**:
    - **PostgreSQL**
    - **MongoDB**
    - **Redis**
- **Event Bus**: Apache Kafka
- **Search Engine**: Elasticsearch
- **Real-time Communication**: websocket, stomp

### Infrastructure & DevOps
- **Cloud**: AWS EC2, ECS
- **Containerization**: Docker, Docker Compose
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway

### Monitoring & Testing
- **Metrics**: Prometheus
- **Visualization**: Grafana
- **Load Testing**: JMeter

## 팀원
| 이름 | 역할  | 담당 기능                                                               |
|:---:|:---:|:--------------------------------------------------------------------|
| **이효선** | 팀장  | Gateway, Eureka, User Service (인증/인가)                               |
| **김민식** | 팀원  | Feed Service (피드 CRUD, 좋아요, 북마크)                                    |
| **김소윤** | 팀원  | Search Service (Elasticsearch 검색, Redis를 통한 실시간 인기 검색어), Monitoring |
| **박수현** | 팀원  | 댓글 API, SSE 통한 실시간 알림 API, 공통모듈                                     |
| **이현주** | 팀원  | Chat Service (실시간 채팅, STOMP), Presence Service (접속 상태 확인)           |

## 서비스
| 서비스 (Service) | 포트 (Port) | 용도 (Purpose) | 비고 |
|----------------|------------|----------------|------|
| Eureka Server | 19090 | Service Discovery | 모든 마이크로서비스 등록 및 관리 |
| API Gateway | 19091 | Gateway / Load Balancing | 외부 요청의 단일 진입점, 라우팅 |
| Monitor Service | 19092 | Monitoring | Prometheus/Grafana 연동 모니터링 |
| User Service | 19100 | User Management | 회원가입, 로그인, 프로필 관리 |
| Chat Service | 19200 | Real-time Chat | 채팅방 관리, 메시지 전송 (WebSocket) |
| Feed Service | 19300 | Feed Management | 피드 작성, 조회, 좋아요, 북마크 |
| Notification Service | 19400 | Notification | 실시간 알림 (SSE), Kafka 이벤트 수신 |
| Comment Service | 19500 | Comment Management | 댓글 작성, 수정, 삭제 |
| Search Service | 19600 | Integrated Search | 통합 검색, 인기 검색어 (Elasticsearch) |

## 주요 API 명세
| 서비스 (Service) | 기능 (Feature) | HTTP Method | URI | 설명 |
|----------------|--------------|-------------|-----|------|
| Auth (인증) | 회원가입 | POST | /auth/signup | 이메일, 비밀번호, 닉네임으로 새로운 사용자를 등록합니다. |
| Auth (인증) | 로그인 | POST | /auth/login | 이메일과 비밀번호로 인증하고 Access/Refresh Token을 발급받습니다. |
| User (회원) | 프로필 조회 | GET | /users/{userId} | 특정 사용자의 프로필 정보(닉네임, 팔로워 수 등)를 조회합니다. |
| User (회원) | 프로필 수정 | PATCH | /users/{userId} | 사용자의 닉네임, 프로필 사진 등을 수정합니다. |
| User (회원) | 팔로우 | POST | /users/{userId}/follow | 특정 사용자를 팔로우합니다. |
| Feed (피드) | 피드 생성 | POST | /feeds | 텍스트 내용과 미디어 파일(이미지/영상)을 포함한 새 피드를 작성합니다. |
| Feed (피드) | 피드 목록 조회 | GET | /feeds | 최신순 또는 인기순으로 피드 목록을 페이징하여 조회합니다. |
| Feed (피드) | 피드 상세 조회 | GET | /feeds/{feedId} | 특정 피드의 상세 내용과 댓글 등을 조회합니다. |
| Feed (피드) | 피드 좋아요 | POST | /feeds/{feedId}/like | 특정 피드에 좋아요를 누르거나 취소합니다. |
| Comment (댓글) | 댓글 작성 | POST | /comments | 특정 피드에 댓글을 작성합니다. |
| Comment (댓글) | 대댓글 작성 | POST | /comments/{commentId}/reply | 특정 댓글에 답글(대댓글)을 작성합니다. |
| Chat (채팅) | 채팅방 생성 | POST | /chats/room | 1:1 또는 그룹 채팅방을 생성합니다. |
| Chat (채팅) | 메시지 전송 | WS | /pub/chat/message | STOMP 프로토콜을 통해 실시간 메시지를 전송합니다. |
| Noti (알림) | 알림 구독 | GET | /notifications/subscribe | 실시간 알림을 받기 위해 SSE(Server-Sent Events) 연결을 맺습니다. |

## 기능 상세
### User
	- 회원가입 & 로그인: JWT 기반 인증/인가 시스템 구축
	- 프로필 관리: 사용자 정보 수정 및 조회
	- 팔로우/언팔로우

### Feed
	- 피드 CRUD: 텍스트 및 이미지/미디어 업로드 지원
	- 소셜 인터랙션: 좋아요(Like), 북마크(Bookmark) 기능
	- 피드 조회: 최신순, 인기순 피드 목록 제공 (Redis 캐싱 적용)

### Comment
	- 댓글 CRUD : 댓글 작성, 수정, 삭제
        - cursor 기반 목록 조회 구현
        - 대댓글 구현으로 계층형 댓글 구조 지원
        - Redis 캐싱으로 조회 성능 최적화

### Chat
	- 실시간 채팅: WebSocket & STOMP 프로토콜을 이용한 실시간 메시지 전송
	- 채팅방 관리: 1:1 채팅 및 그룹 채팅방 생성/참여
	- 메시지 저장: 대용량 채팅 로그 저장

### Notification
	- 실시간 알림 : SSE를 이용한 실시간 알림 전송
	- 이벤트 연동 : apache Kafka를 통해 다른 서비스의 이벤트를 구독하여 알림 생성

### Search
	- 통합 검색: Elasticsearch를 활용한 고성능 피드/유저 검색
	- 실시간 인기 검색어: Redis를 활용한 실시간 검색어 순위 제공

### Monitor
	- 시스템 모니터링: Prometheus & Grafana를 통한 서비스 상태 및 리소스 사용량 시각화

