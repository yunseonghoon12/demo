# demo

Spring Boot 기반 JWT 인증 + STOMP WebSocket 채팅 데모 프로젝트입니다.

## 기술 스택

| 구분 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 4.0.4 |
| Build | Gradle |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| 인증 | JWT (HttpOnly Cookie) |
| 실시간 통신 | WebSocket, STOMP, SockJS |

## 주요 기능

- **JWT 로그인** — 로그인 성공 시 HttpOnly Cookie로 JWT 발급
- **HTTP API 인증** — `JwtAuthenticationFilter`로 보호된 API 요청 검증
- **WebSocket 채팅** — STOMP + SockJS 기반 Echo 채팅
- **WebSocket JWT 인증** — Handshake 단계에서 Cookie JWT 검증 후 Session에 username 저장

## 프로젝트 구조

```
src/main/java/com/example/demo/
├── config/
│   ├── WebFilterConfig.java      # JWT 필터 등록
│   └── WebSocketConfig.java      # STOMP / SockJS 설정
├── login/
│   ├── JwtAuthenticationFilter.java   # HTTP JWT 인증 필터
│   ├── JwtHandshakeInterceptor.java   # WebSocket Handshake JWT 검증
│   ├── JwtTokenProvider.java          # JWT 생성·검증
│   ├── LoginController.java
│   └── UserService.java
├── chat/
│   ├── ChatController.java       # /pub/chat → /sub/chat
│   └── ChatMessage.java
└── user/
    ├── User.java
    └── UserRepository.java

src/main/resources/
├── application.properties
└── static/chat.html              # WebSocket 채팅 테스트 페이지
```

## 사전 요구사항

- JDK 17
- PostgreSQL (기본 포트: `5433`)
- 데이터베이스 `demo`, 사용자 `demo_user` (또는 `application.properties` 수정)

## 실행 방법

```bash
# Windows
.\gradlew.bat bootRun

# macOS / Linux
./gradlew bootRun
```

애플리케이션 기본 주소: `http://localhost:8080`

## API

### 로그인

```http
POST /login
Content-Type: application/json

{
  "username": "your_username",
  "password": "your_password"
}
```

- 성공: `Set-Cookie: accessToken=...` (HttpOnly)
- 실패: `login-fail` 페이지 반환

### 인증 예외 경로

아래 경로는 JWT 필터를 거치지 않습니다.

| Method | Path | 설명 |
|--------|------|------|
| GET | `/` | 헬로 월드 |
| POST | `/login` | 로그인 |
| GET | `/chat.html` | 채팅 테스트 페이지 |
| * | `/ws/**` | WebSocket (Handshake에서 별도 JWT 검증) |

그 외 모든 HTTP 요청은 유효한 JWT Cookie가 필요합니다.

## WebSocket 채팅

| 항목 | 값 |
|------|-----|
| 연결 엔드포인트 | `/ws` (SockJS) |
| 메시지 전송 | `/pub/chat` |
| 메시지 구독 | `/sub/chat` |

### 메시지 형식

```json
{
  "sender": "user1",
  "content": "안녕하세요"
}
```

### 인증 흐름

1. `/login`으로 JWT Cookie 발급
2. 브라우저에서 `/ws` 연결 (Cookie 자동 전송)
3. `JwtHandshakeInterceptor`가 Handshake 시 JWT 검증
4. 검증 성공 시 WebSocket Session에 `username` 저장
5. 검증 실패 시 연결 거부

## 채팅 테스트

1. 로그인 API로 JWT Cookie 발급 (같은 브라우저에서 Cookie 유지)
2. `http://localhost:8080/chat.html` 접속
3. **연결** → 메시지 입력 → **전송**
4. 브라우저 탭 2개를 열면 브로드캐스트 동작 확인 가능

## 설정

`src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/demo
spring.datasource.username=demo_user
spring.datasource.password=5690

jwt.secret=demo-jwt-secret-key-must-be-at-least-32-bytes-long
jwt.expiration-ms=3600000
jwt.cookie-name=accessToken
```

운영 환경에서는 `jwt.secret`과 DB 비밀번호를 환경 변수로 분리하는 것을 권장합니다.

## 테스트

```bash
.\gradlew.bat test
```

## 라이선스

이 프로젝트는 학습·데모 목적으로 사용됩니다.
