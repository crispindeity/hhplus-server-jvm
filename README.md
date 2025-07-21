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

<details>
<summary>주차별 피드백</summary>

### 2주차

```text
- 좋았던 점
    - 별도의 png로도 올려주셔서 보는 사람 입장에서 과제 제출되었다고 생각이 들었어요
    - 낙관적 잠금을 고려한 wallet 테이블 분리 좋아요, 확실한 의도가 있는 분리! + 거래 이력에 대한 관리까지 고민하신 부분도 좋습니다
    - internal 키워드 꼼꼼히 붙여주시는군요ㅎㅎ
    - date와 같은 데이터 필터링 용도로 쿼리 파라미터도 적절하게 사용하신 것 같아요
- 아쉬웠던 점
    - 이 케이스에서 굳이 WebSocket을 사용할 필요까지는 없지 않았을까 싶네요!
    - 실패 케이스 중에서도 핵심 부분은 남겨도 괜찮지 않을까 싶어요, 예를 들어 토큰 조회 실패 같은 부분 말고요!
    - 브라우저 닫기와 같은 부분은 조금 많이 상세했던 것 같네요!
    - MySQL 기준으로 데이터베이스의 [클러스터링 인덱스 구조](https://mangkyu.tistory.com/285)에 대해 알아보시면 좋을 것 같아요, 그리고 왜 PK는 Long 타입 정수형으로 선언하면 좋은지 고민해보기. PK 문자열 사용에 대한 참고 자료: https://kccoder.com/mysql/uuid-vs-int-insert-performance/
    - queue_tokens에서 number는 너무 모호한 네이밍의 컬럼인 것 같아요
    - reservation과 reservation_seats를 굳이 분리할 필요가 있을까 라는 생각이 당장은 드는데, 코드를 보고 작업하면서 다시 살펴보면 좋겠네요!
    - /api/reservations/{id}/payment/points를 기준으로 리소스 계층 구조는 잘 잡아주신 것 같은데, 결제 수단은 굳이 path에 표현되지 않아도 되는 것 같아요, 이것은 리소스 계층보다는 수단 중 하나의 개념이라
    - 저라면 /api/reservations/available-dates는 반대로 어떤 콘서트에 대한 예약 정보인지, 콘서트 정보를 path에 노출시킬 것 같아요
    - 유효성 검사 부분은 spring-boot-validation을 저는 사용하기를 선호합니다!
- 리뷰 피드백
    - ERD 관련 부분 위에서 코멘트 남겨드렸습니다
    - 설계 문서 코멘트도 위에서 남겨드렸습니다.
    - API Path 설계 부분은 위에서 코멘트 남겨드렸습니다!
    - 말씀해주신대로 ControllerTest를 통해 문서화를 작성시키는 방법이 있어서, 둘을 합칠 수 있는 방법으로 고도화해보시면 좋을 것 같아요
```

### 피드백 수정 내용

#### 설계

- 설계 부분은 직접적인 수정은 하지 않고, 다시 한번 생각 해 보는 방식으로 피드백을 수용하자.
- [ ] 웹 소켓 사용에 대한 생각
- [ ] 시퀀스 다이어 그램에서 핵심적인 예외 부분은 남겨도 좋을것 같다.
- [ ] 브라우저 닫기 등의 내용은 너무 상세한 것 같다.
- [ ] 클러스터링 인덱스 구조에 대해 학습
- [ ] PK 는 왜 Long 타입 정수형으로 선언하면 좋은지 고민해보기.
- [ ] 모호한 컬럼 네이밍 수정
- [ ] URL Path 수정

#### Mock API

- Mock API 에서 어디까지 작성해야 하는지 그 기준이 모호 했었는데, 너무 작게 잡은것 같다. 응답 관련된 부분은 전부 작성하는게 좋은것 같다.(예외, Validation 등등)
- [ ] 커스텀 예외 응답 추가
- [ ] Validation 추가

</details>

## 플로우 차트

```mermaid
flowchart TD
    User["사용자"]
    API["API 요청"]
    Queue{"대기열"}
    Date["예약 가능 일 조회"]
    Seat["좌석 조회"]
    Reservation["예약 요청"]
    SeatStatus{"좌석 점유"}
    SeatHold["좌석 임시 배정"]
    Payment["결제"]
    PaymentTime{"결제 시간"}
    Balance{"잔액"}
    Charge["포인트 충전"]
    Completed["예약 완료"]
    User --> API
    API --> Queue
    Queue -- 순번 도달 --> Date
    Queue -- 대기 --> Queue
    Date --> Seat
    Seat --> Reservation
    Reservation --> SeatStatus
    SeatStatus -- 실패 --> Seat
    SeatStatus -- 성공 --> SeatHold
    SeatHold --> Payment
    Payment --> PaymentTime
    PaymentTime -- 5분 초과 --> Date
    PaymentTime -- 5분 미만 --> Balance
    Balance -- 부족 --> Charge
    Charge --> Payment
    Balance -- 충분 --> Completed
    Completed -- 만료 --> Queue
```

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
    User ->> FE: 예약 페이지 진입
    FE ->> TokenAPI: POST /token (UUID 포함)
    TokenAPI ->> QueueService: 대기열 토큰 생성
    QueueService ->> DB: 토큰 및 순번 저장
    TokenAPI -->> FE: 대기열 토큰 응답
    FE ->> WS: WebSocket 연결 시작
    WS ->> DB: 대기열 순번 확인
    WS -->> FE: 대기 순번 업데이트

    alt 연결 종료
        WS ->> QueueService: 연결 종료 감지
        QueueService ->> DB: 토큰 상태 → CANCELLED
    end
```

### 예약 및 임시 배정 흐름

```mermaid
sequenceDiagram
    autonumber
    participant FE as Frontend
    participant SeatAPI as ReservationController
    participant ReserveService as ReservationService
    participant DB as RDB
    FE ->> SeatAPI: GET /dates
    SeatAPI ->> ReserveService: 예약 가능 날짜 조회 요청
    ReserveService ->> DB: 예약 가능한 날짜 조회
    SeatAPI -->> FE: 날짜 목록 응답
    FE ->> SeatAPI: GET
    SeatAPI ->> ReserveService: 해당 날짜 좌석 상태 조회 요청 
    ReserveService ->> DB: 해당 날짜 좌석 상태 조회
    SeatAPI -->> FE: 좌석 목록 응답
    FE ->> SeatAPI: POST
    SeatAPI ->> ReserveService: 예약 요청
    ReserveService ->> DB: 좌석 임시 배정 (상태: HOLD, 만료시간 저장)
    Note right of ReserveService: 스케줄러가 5분 후 HOLD 상태 자동 해제
    SeatAPI -->> FE: 임시 예약 응답

    alt 유저가 예약 포기
        ReserveService ->> DB: HOLD 상태 → CANCELLED
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
    FE ->> PointAPI: GET
    PointAPI ->> PointService: 사용자 포인트 조회
    PointService ->> DB: 포인트 정보 조회
    PointService -->> PointAPI: 보유 포인트 응답
    PointAPI -->> FE: 현재 포인트 응답

    alt 포인트 부족
        FE ->> PointAPI: POST
        PointAPI ->> PointService: 포인트 충전
        PointService ->> DB: 포인트 잔액 갱신
        PointService -->> PointAPI: 충전 완료
        PointAPI -->> FE: 충전 성공
    end

    FE ->> PayAPI: POST
    PayAPI ->> PaymentService: 결제 처리
    PaymentService ->> PointService: 포인트 차감 요청
    PointService ->> DB: 보유 포인트 차감
    PointService -->> PaymentService: 차감 완료
    PaymentService ->> DB: 좌석 상태 → CONFIRMED
    PaymentService ->> DB: 결제 내역 생성
    PaymentService ->> DB: 대기열 토큰 상태 → COMPLETED
    PaymentService -->> PayAPI: 결제 성공
    PayAPI -->> FE: 결제 완료
    WS -->> FE: 대기열 종료 알림
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
    participant SeatAPI as ReservationController
    participant ReserveService as ReservationService
    participant PointAPI as PointController
    participant PointService
    participant PayAPI as PaymentController
    participant PaymentService
    participant DB as RDB
%% --- 대기열 진입 및 WebSocket 연결 ---
    User ->> FE: 예약 페이지 진입
    FE ->> TokenAPI: POST (UUID 포함)
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
        FE ->> SeatAPI: GET
        SeatAPI ->> DB: 예약 가능한 날짜 조회
        SeatAPI -->> FE: 날짜 목록 응답
        FE ->> SeatAPI: GET
        SeatAPI ->> DB: 해당 날짜 좌석 상태 조회
        SeatAPI -->> FE: 좌석 목록 응답
        FE ->> ReserveService: POST
        ReserveService ->> DB: 좌석 임시 배정 (상태: HOLD, 만료시간 저장)
        ReserveService -->> FE: 임시 예약 완료
    %% --- 포인트 조회 및 충전 ---
        FE ->> PointAPI: GET
        PointAPI ->> PointService: 포인트 조회 요청
        PointService ->> DB: 포인트 정보 조회
        PointService -->> PointAPI: 보유 포인트 응답
        PointAPI -->> FE: 포인트 응답

        alt 포인트 부족
            FE ->> PointAPI: POST
            PointAPI ->> PointService: 포인트 충전
            PointService ->> DB: 포인트 잔액 갱신
            PointService -->> PointAPI: 충전 완료
            PointAPI -->> FE: 충전 성공
        end

    %% --- 결제 흐름 ---
        FE ->> PayAPI: POST
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

## ERD

```mermaid
erDiagram
    USERS {
        VARCHAR(36) id PK
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    POINT_WALLETS {
        BIGINT id PK
        VARCHAR(36) user_id 
        BIGINT balance
        BIGINT version
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    POINT_TRANSACTIONS {
        BIGINT id PK
        BIGINT point_wallet_id 
        VARCHAR type "CHARGED | USED"
        BIGINT amount
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    QUEUE_TOKENS {
        BIGINT id PK
        VARCHAR(36) user_id 
        INT number
        VARCHAR(1024) token
        VARCHAR status "WAITING | COMPLETED | CANCELLED | EXPIRED"
        TIMESTAMP expires_at
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    SEATS {
        BIGINT id PK
        INT number
        BIGINT price
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    CONCERTS {
        BIGINT id PK
        VARCHAR(255) title
        DATE date
        TIME start_time
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    CONCERT_SEATS {
        BIGINT id PK
        BIGINT concert_id 
        BIGINT seat_id 
        VARCHAR status "HELD | AVAILABLE | RESERVED"
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    SEAT_HOLDS {
        BIGINT id PK
        BIGINT concert_seat_id 
        VARCHAR(36) user_id 
        TIMESTAMP held_at
        TIMESTAMP expires_at
    }

    RESERVATIONS {
        BIGINT id PK
        VARCHAR(36) user_id 
        BIGINT concert_id 
        BIGINT payment_id 
        TIMESTAMP confirmed_at
        TIMESTAMP reserved_at
        TIMESTAMP expires_at
        VARCHAR status "IN_PROGRESS | CANCELLED | CONFIRMED | EXPIRED"
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    RESERVATION_SEATS {
        BIGINT id PK
        BIGINT reservation_id 
        BIGINT concert_seat_id
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    PAYMENTS {
        BIGINT id PK
        VARCHAR(36) user_id 
        VARCHAR status "PENDING | COMPLETED | CANCELLED"
        BIGINT price
        TIMESTAMP paid_at
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    USERS ||--o{ POINT_WALLETS: "has (logical)"
    USERS ||--o{ QUEUE_TOKENS: "receives (logical)"
    USERS ||--o{ RESERVATIONS: "makes (logical)"
    USERS ||--o{ PAYMENTS: "initiates (logical)"
    POINT_WALLETS ||--o{ POINT_TRANSACTIONS: "tracks (logical)"
    RESERVATIONS ||--o{ RESERVATION_SEATS: "contains"
    RESERVATIONS ||--|| PAYMENTS: "paid_by"
    RESERVATION_SEATS ||--|| SEATS: "maps_to"
    RESERVATION_SEATS ||--|| CONCERTS: "scheduled_in"
    CONCERTS ||--o{ CONCERT_SEATS: "has"
    CONCERT_SEATS ||--|| SEATS: "assigned_to"
    CONCERT_SEATS ||--o{ SEAT_HOLDS: "held_by"
    CONCERT_SEATS ||--o{ RESERVATION_SEATS: "reserved_by"
```
