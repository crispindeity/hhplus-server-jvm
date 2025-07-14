# 프로젝트(콘서트 예약 서비스)

## Getting Started

### Prerequisites

#### Running Docker Containers

`local` profile 로 실행하기 위하여 인프라가 설정되어 있는 Docker 컨테이너를 실행해주셔야 합니다.

```bash
docker-compose up -d
```

## 요구사항

<details>
<summary>요구사항 보기</summary>

### 필수 요구사항

1. 유저 토큰 발급 API
2. 예약 가능 날짜 / 좌석 API
3. 좌석 예약 요청 API
4. 잔액 충전 / 조회 API
5. 결제 API

- 각 기능 및 제약사항에 대해 단위 테스트를 반드시 하나 이상 작성하도록 합니다.
- 다수의 인스턴스로 어플리케이션이 동작하더라도 기능에 문제가 없도록 작성하도록 합니다.
- 동시성 이슈를 고려하여 구현합니다.
- 대기열 개념을 고려해 구현합니다.

### API Specs

1️⃣`주요` 유저 대기열 토큰 기능

- 서비스를 이용할 토큰을 발급받는 API를 작성합니다.
- 토큰은 유저의 UUID 와 해당 유저의 대기열을 관리할 수 있는 정보 ( 대기 순서 or 잔여 시간 등 ) 를 포함합니다.
- 이후 모든 API 는 위 토큰을 이용해 대기열 검증을 통과해야 이용 가능합니다.

> 기본적으로 폴링으로 본인의 대기열을 확인한다고 가정하며, 다른 방안 또한 고려해보고 구현해 볼 수 있습니다.
>> *대기열 토큰 발급 API   
> *대기번호 조회 API

예약이 시작되면 예약 요청을 보내는 모든 유저는 토큰을 대기열 토큰을 받을 수 있으며, 지속적으로 자기의 대기열 번호를 확인 할 수 있어야 한다.(아마도 웹 소켓을 활용할 예정)
토큰에는 유저의 식별자와 유저의 대기열을 관리할 수 있는 정보가 담겨 있어야 한다.
병렬 처리를 위해 특정 유저의 수 만큼 이후 API 를 요청할 수 있다.(이건 아직 미정)

대기열 관리를 어떻게 할것인가?

- 큐에 대기열 순서대로 토큰을 넣어서 관리해야 하나? 일단 redis 는 사용하지 말고 RDB 를 사용해서 큐를 관리하자 요청자 수 만큼 토큰을 생성해서 순서대로 DB 에 적재

2️⃣`기본` 예약 가능 날짜 / 좌석 API

- 예약가능한 날짜와 해당 날짜의 좌석을 조회하는 API 를 각각 작성합니다.
- 예약 가능한 날짜 목록을 조회할 수 있습니다.
- 날짜 정보를 입력받아 예약가능한 좌석정보를 조회할 수 있습니다.

> 좌석 정보는 1 ~ 50 까지의 좌석번호로 관리됩니다.

대기열 토큰을 가지고 예약 가능 날짜 및 죄석을 조회 하는 API 호출
예약 가능 날짜 조회와, 해당 날짜의 좌석을 조회하는 API 를 각각 작성해야 한다.
목록으로 조회 해야 하며, 아마도 페이징 처리 까지는 필요 없을것 같다.
날짜 정보만 입력

3️⃣`주요` 좌석 예약 요청 API

- 날짜와 좌석 정보를 입력받아 좌석을 예약 처리하는 API 를 작성합니다.
- 좌석 예약과 동시에 해당 좌석은 그 유저에게 약 **5분**간 임시 배정됩니다. ( 시간은 정책에 따라 자율적으로 정의합니다. )
- 만약 배정 시간 내에 결제가 완료되지 않는다면 좌석에 대한 임시 배정은 해제되어야 한다.
- 누군가에게 점유된 동안에는 해당 좌석은 다른 사용자가 예약할 수 없어야 한다.

좌석을 예약 처리하는 API 가 필요하며, 예약과 동시에 임시 배정
임시 배정의 경우는 스케쥴러를 사용해서 크론잡으로 해제 하는 방식을 사용하자 (최소 30초)
점유 된 좌석의 경우는 좌석 리스트를 보여줄때 임시 배정으로 보여주고 예약 처리를 할 수 없도록 하자.

4️⃣`기본` 잔액 충전 / 조회 API

- 결제에 사용될 금액을 API 를 통해 충전하는 API 를 작성합니다.
- 사용자 식별자 및 충전할 금액을 받아 잔액을 충전합니다.
- 사용자 식별자를 통해 해당 사용자의 잔액을 조회합니다.

잔액 충전이다.
사용자 식별자를 통해 해당 사용자의 잔액을 조회 한다.
지금 시스템에서는 로그인 회원가입 같은게 없다보니 그냥 API 에서 식별자를 받도록하자.
만약 시간이 있다면 로그인을 가정하여 만들어 볼까?

5️⃣`주요` 결제 API

- 결제 처리하고 결제 내역을 생성하는 API 를 작성합니다.
- 결제가 완료되면 해당 좌석의 소유권을 유저에게 배정하고 대기열 토큰을 만료시킵니다.

결제가 완료되면 소유권을 배정, 대기열 토큰 만료.

</details>

## 시퀀스 다이어그램

### 대기열 진입 및 순번 조회

```mermaid
sequenceDiagram
    autonumber
    participant User
    participant FE as Frontend
    participant TokenAPI as TokenController
    participant QueueService as QueueService
    participant WS as WebSocketServer
    participant DB as RDB

    User->>FE: 예약 페이지 진입
    FE->>TokenAPI: POST /token (UUID 포함)
    TokenAPI->>QueueService: 대기열 토큰 생성
    QueueService->>DB: 토큰 및 순번 저장
    TokenAPI-->>FE: 대기열 토큰 응답

    FE->>WS: WebSocket 연결 시작
    WS->>DB: 대기열 순번 확인
    WS-->>FE: 대기 순번 업데이트

    alt 연결 종료
        WS->>QueueService: 연결 종료 감지
        QueueService->>DB: 토큰 상태 → CANCELLED
    end
```

### 예약 및 임시 배정 흐름

```mermaid
sequenceDiagram
    autonumber
    participant FE as Frontend
    participant SeatAPI as SeatController
    participant ReserveService as ReservationService
    participant DB as RDB

    FE->>SeatAPI: GET /dates
    SeatAPI->>DB: 예약 가능한 날짜 조회
    SeatAPI-->>FE: 날짜 목록 응답

    FE->>SeatAPI: GET /seats?date=YYYY-MM-DD
    SeatAPI->>DB: 해당 날짜 좌석 상태 조회
    SeatAPI-->>FE: 좌석 목록 응답

    FE->>ReserveService: POST /seats/reserve
    ReserveService->>DB: 좌석 임시 배정 (상태: HOLD, 만료시간 저장)
    Note right of ReserveService: 스케줄러가 5분 후 HOLD 상태 자동 해제
    ReserveService-->>FE: 임시 예약 응답

    alt 유저가 예약 포기
        ReserveService->>DB: HOLD 상태 → CANCELLED
    end
```

### 포인트 및 결제 흐름

```mermaid
sequenceDiagram
    autonumber
    participant FE as Frontend
    participant PointAPI as PointController
    participant PointService
    participant PayAPI as PaymentController
    participant PaymentService
    participant DB as RDB
    participant WS as WebSocketServer

    FE->>PointAPI: GET /point?userId=...
    PointAPI->>PointService: 사용자 포인트 조회
    PointService->>DB: 포인트 정보 조회
    PointService-->>PointAPI: 보유 포인트 응답
    PointAPI-->>FE: 현재 포인트 응답

    alt 포인트 부족
        FE->>PointAPI: POST /point/charge (userId, amount)
        PointAPI->>PointService: 포인트 충전
        PointService->>DB: 포인트 잔액 갱신
        PointService-->>PointAPI: 충전 완료
        PointAPI-->>FE: 충전 성공
    end

    FE->>PayAPI: POST /pay (좌석 정보 + 결제금액)
    PayAPI->>PaymentService: 결제 처리

    PaymentService->>PointService: 포인트 차감 요청
    PointService->>DB: 보유 포인트 차감
    PointService-->>PaymentService: 차감 완료

    PaymentService->>DB: 좌석 상태 → CONFIRMED
    PaymentService->>DB: 결제 내역 생성
    PaymentService->>DB: 대기열 토큰 상태 → COMPLETED
    PaymentService-->>PayAPI: 결제 성공
    PayAPI-->>FE: 결제 완료
    WS-->>FE: 대기열 종료 알림
```

### 전체 흐름

```mermaid
sequenceDiagram
    autonumber
    participant User
    participant FE as Frontend (Web + WebSocket)
    participant TokenAPI as TokenController
    participant QueueService
    participant WS as WebSocketServer
    participant SeatAPI as SeatController
    participant ReserveService as ReservationService
    participant PointAPI as PointController
    participant PointService
    participant PayAPI as PaymentController
    participant PaymentService
    participant DB as RDB
%% --- 대기열 진입 및 WebSocket 연결 ---
    User ->> FE: 예약 페이지 진입
    FE ->> TokenAPI: POST /token (UUID 포함)
    TokenAPI ->> QueueService: 대기열 토큰 생성
    QueueService ->> DB: 토큰 및 순번 저장
    TokenAPI -->> FE: 대기열 토큰 응답
    FE ->> WS: WebSocket 연결 시작
    WS ->> DB: 대기열 순번 확인 (폴링 or PUSH)
    WS -->> FE: 대기 순번 업데이트

    alt 브라우저 닫기/연결 종료
        WS ->> QueueService: 연결 종료 감지
        QueueService ->> DB: 토큰 상태 → CANCELLED
        QueueService ->> DB: HOLD 좌석 있을 경우 예약도 취소
    end

    alt 순번 도달
    %% --- 좌석 예약 흐름 ---
        FE ->> SeatAPI: GET /dates
        SeatAPI ->> DB: 예약 가능한 날짜 조회
        SeatAPI -->> FE: 날짜 목록 응답
        FE ->> SeatAPI: GET /seats?date=YYYY-MM-DD
        SeatAPI ->> DB: 해당 날짜 좌석 상태 조회
        SeatAPI -->> FE: 좌석 목록 응답
        FE ->> ReserveService: POST /seats/reserve (날짜 + 좌석)
        ReserveService ->> DB: 좌석 임시 배정 (상태: HOLD, 만료시간 저장)
        Note right of ReserveService: 스케줄러가 HOLD 상태 5분 후 자동 해제
        ReserveService -->> FE: 임시 예약 완료
    %% --- 포인트 조회 및 충전 ---
        FE ->> PointAPI: GET /point?userId=...
        PointAPI ->> PointService: 포인트 조회 요청
        PointService ->> DB: 포인트 정보 조회
        PointService -->> PointAPI: 보유 포인트 응답
        PointAPI -->> FE: 포인트 응답

        alt 포인트 부족
            FE ->> PointAPI: POST /point/charge (userId, amount)
            PointAPI ->> PointService: 포인트 충전
            PointService ->> DB: 포인트 잔액 갱신
            PointService -->> PointAPI: 충전 완료
            PointAPI -->> FE: 충전 성공
        end

    %% --- 결제 흐름 ---
        FE ->> PayAPI: POST /pay (좌석 정보 + 결제금액)
        PayAPI ->> PaymentService: 결제 처리 요청
        PaymentService ->> PointService: 포인트 차감 요청
        PointService ->> DB: 보유 포인트 차감
        PointService -->> PaymentService: 차감 성공
        PaymentService ->> DB: 좌석 상태 → CONFIRMED
        PaymentService ->> DB: 결제 내역 생성
        PaymentService ->> DB: 대기열 토큰 상태 → COMPLETED
        PaymentService -->> WS: 결제 완료 상태 전송
        WS -->> FE: 결제 완료 상태 응답
        FE ->> WS: WebSocket 연결 종료
        WS -->> FE: 연결 종료 완료
        PayAPI -->> FE: 결제 성공 응답
    end
```

## 클래스 다이어그램

```mermaid
classDiagram
    class User {
        UUID id
        String name
    }

    class QueueToken {
        Long id
        UUID userId
        int queueNumber
        TokenStatus status
        LocalDateTime createdAt
    }

    class Seat {
        Long id
        int number
    }

    class Reservation {
        Long id
        UUID userId
        Seat seat
        ReservationStatus status
        LocalDateTime reservedAt
        LocalDateTime expiresAt
    }

    class Point {
        UUID userId
        int balance
    }

    class Payment {
        Long id
        UUID userId
        int amount
        LocalDateTime paidAt
    }

    Reservation --> Seat
    Reservation --> User
    QueueToken --> User
    Point --> User
    Payment --> User
```

## ERD

```mermaid
erDiagram
    USERS ||--o{ QUEUE_TOKENS: has
    USERS ||--o{ RESERVATIONS: makes
    USERS ||--o{ POINTS: owns
    USERS ||--o{ PAYMENTS: pays
    SEATS ||--o{ RESERVATIONS: has

    QUEUE_TOKENS {
        UUID id PK
        UUID user_id
        INT queue_number
        STRING status
        DATETIME created_at
    }

    USERS {
        UUID id PK
        STRING name
    }

    SEATS {
        BIGINT id PK
        INT seat_number
    }

    RESERVATIONS {
        BIGINT id PK
        UUID user_id
        BIGINT seat_id
        STRING status
        DATETIME reserved_at
        DATETIME expires_at
    }

    POINTS {
        UUID user_id PK
        INT balance
    }

    PAYMENTS {
        BIGINT id PK
        UUID user_id
        BIGINT reservation_id
        INT amount
        DATETIME paid_at
    }
```
