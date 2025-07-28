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
  updated_at TIMESTAMP,
  UNIQUE (concert_id, date)
);

CREATE TABLE concert_seats
(
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  schedule_id BIGINT      NOT NULL,
  seat_id     BIGINT      NOT NULL,
  status      VARCHAR(20) NOT NULL CHECK (status IN ('HELD', 'AVAILABLE', 'RESERVED')),
  created_at  TIMESTAMP,
  updated_at  TIMESTAMP,
  UNIQUE (schedule_id, seat_id)
);

CREATE INDEX idx_concert_status ON concert_seats (schedule_id, status);

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
  version    BIGINT      NOT NULL
);

CREATE INDEX idx_user_id ON point_wallets (user_id);

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
  status          VARCHAR(20) NOT NULL CHECK (status IN
                                              ('IN_PROGRESS', 'CANCELLED', 'CONFIRMED', 'EXPIRED')),
  created_at      TIMESTAMP,
  updated_at      TIMESTAMP
);

CREATE TABLE queue_tokens
(
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id      VARCHAR(36)   NOT NULL,
  queue_number INT           NOT NULL,
  token        VARCHAR(1024) NOT NULL,
  status       VARCHAR(20)   NOT NULL CHECK (status IN ('WAITING', 'COMPLETED', 'CANCELLED', 'EXPIRED')),
  expires_at   TIMESTAMP     NOT NULL,
  created_at   TIMESTAMP,
  updated_at   TIMESTAMP
);

CREATE INDEX idx_status ON queue_tokens (status);
CREATE INDEX idx_user_id ON queue_tokens (user_id);

CREATE TABLE point_transactions
(
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  point_wallet_id BIGINT      NOT NULL,
  type            VARCHAR(10) NOT NULL CHECK (type IN ('CHARGED', 'USED')),
  amount          BIGINT      NOT NULL,
  created_at      TIMESTAMP,
  updated_at      TIMESTAMP
);

CREATE INDEX idx_point_wallet_id ON point_transactions (point_wallet_id);

CREATE TABLE payments
(
  id         BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id    VARCHAR(36) NOT NULL,
  status     VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED')),
  price      BIGINT      NOT NULL,
  paid_at    TIMESTAMP DEFAULT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE INDEX idx_user_id_status ON payments (user_id, status);
CREATE INDEX idx_status ON payments (status);

CREATE TABLE seat_holds
(
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  concert_seat_id BIGINT      NOT NULL,
  user_id         VARCHAR(36) NOT NULL,
  held_at         TIMESTAMP   NOT NULL,
  expires_at      TIMESTAMP   NOT NULL,
  UNIQUE (concert_seat_id)
);
