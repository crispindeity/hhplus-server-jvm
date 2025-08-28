DROP TABLE IF EXISTS seat_holds;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS point_transactions;
DROP TABLE IF EXISTS queue_tokens;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS point_wallets;
DROP TABLE IF EXISTS seats;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS concert_seats;
DROP TABLE IF EXISTS concert_schedules;
DROP TABLE IF EXISTS concerts;
DROP TABLE IF EXISTS queue_numbers;

CREATE TABLE concerts
(
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  title      VARCHAR(255) NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE concert_schedules
(
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  concert_id BIGINT NOT NULL,
  date       DATE   NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE concert_seats
(
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  schedule_id BIGINT      NOT NULL,
  seat_id     BIGINT      NOT NULL,
  status      VARCHAR(20) NOT NULL,
  created_at  TIMESTAMP,
  updated_at  TIMESTAMP
);

CREATE TABLE users
(
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id    VARCHAR(36) NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE seats
(
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  number     INT    NOT NULL,
  price      BIGINT NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE point_wallets
(
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id    VARCHAR(36) NOT NULL,
  balance    BIGINT      NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  version    INT         NOT NULL
);

CREATE TABLE reservations
(
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id         VARCHAR(36) NOT NULL,
  concert_id      BIGINT      NOT NULL,
  payment_id      BIGINT      NOT NULL,
  concert_seat_id BIGINT      NOT NULL,
  confirmed_at    TIMESTAMP DEFAULT NULL,
  reserved_at     TIMESTAMP   NOT NULL,
  expires_at      TIMESTAMP   NOT NULL,
  status          VARCHAR(20) NOT NULL,
  version         INT         NOT NULL,
  created_at      TIMESTAMP,
  updated_at      TIMESTAMP
);

CREATE TABLE queue_tokens
(
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id      VARCHAR(36)   NOT NULL,
  queue_number INT           NOT NULL,
  token        VARCHAR(1024) NOT NULL,
  status       VARCHAR(20)   NOT NULL,
  expires_at   TIMESTAMP     NOT NULL,
  created_at   TIMESTAMP,
  updated_at   TIMESTAMP
);

CREATE TABLE point_transactions
(
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  point_wallet_id BIGINT      NOT NULL,
  type            VARCHAR(10) NOT NULL,
  amount          BIGINT      NOT NULL,
  created_at      TIMESTAMP,
  updated_at      TIMESTAMP
);

CREATE TABLE payments
(
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id    VARCHAR(36) NOT NULL,
  status     VARCHAR(20) NOT NULL,
  price      BIGINT      NOT NULL,
  paid_at    TIMESTAMP DEFAULT NULL,
  version    INT         NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE seat_holds
(
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  concert_seat_id BIGINT      NOT NULL,
  user_id         VARCHAR(36) NOT NULL,
  held_at         TIMESTAMP   NOT NULL,
  expires_at      TIMESTAMP   NOT NULL
);

CREATE TABLE queue_numbers
(
  id         VARCHAR(20),
  number     BIGINT NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE reservation_event_traces
(
  id             BIGINT AUTO_INCREMENT PRIMARY KEY,
  event_id       BIGINT      NOT NULL,
  reservation_id BIGINT      NOT NULL,
  occurred_at    TIMESTAMP   NOT NULL,
  event_type     VARCHAR(20) NOT NULL,
  created_at     TIMESTAMP,
  updated_at     TIMESTAMP
);

CREATE INDEX idx_concert_schedules_concert_id ON concert_schedules (concert_id);
CREATE INDEX idx_concert_seats_schedule_status ON concert_seats (schedule_id, status);
CREATE INDEX idx_reservations_status_reserved_at ON reservations (status, reserved_at);
CREATE INDEX idx_queue_tokens_user_id ON queue_tokens (user_id);
CREATE INDEX idx_queue_status_expires_queue ON queue_tokens (status, expires_at, queue_number);
CREATE INDEX idx_queue_tokens_queue_number ON queue_tokens (queue_number);
CREATE INDEX idx_queue_tokens_user_status ON queue_tokens (user_id, status);
