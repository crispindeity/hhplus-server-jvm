CREATE TABLE CONCERTS
(
  id         BIGINT PRIMARY KEY,
  title      VARCHAR(255) NOT NULL,
  date       DATE         NOT NULL,
  start_time TIME         NOT NULL,
  created_at TIMESTAMP    NOT NULL,
  updated_at TIMESTAMP    NOT NULL
);

CREATE TABLE CONCERT_SEATS
(
  id         BIGINT PRIMARY KEY,
  concert_id BIGINT      NOT NULL,
  seat_id    BIGINT      NOT NULL,
  status     VARCHAR(20) NOT NULL CHECK (status IN ('HELD', 'AVAILABLE', 'RESERVED')),
  created_at TIMESTAMP   NOT NULL,
  updated_at TIMESTAMP   NOT NULL,
  UNIQUE (concert_id, seat_id)
);

CREATE INDEX idx_concert_status
  ON CONCERT_SEATS (concert_id, status);

CREATE TABLE USERS
(
  id         VARCHAR(36) PRIMARY KEY,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE SEATS
(
  id         BIGINT PRIMARY KEY,
  number     INT       NOT NULL,
  price      BIGINT    NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE POINT_WALLETS
(
  id         BIGINT PRIMARY KEY,
  user_id    VARCHAR(36) NOT NULL,
  balance    BIGINT      NOT NULL,
  created_at TIMESTAMP   NOT NULL,
  updated_at TIMESTAMP   NOT NULL,
  version    BIGINT      NOT NULL
);

CREATE INDEX idx_user_id ON POINT_WALLETS (user_id);

CREATE TABLE RESERVATIONS
(
  id           BIGINT PRIMARY KEY,
  user_id      VARCHAR(36) NOT NULL,
  concert_id   BIGINT      NOT NULL,
  payment_id   BIGINT      NOT NULL,
  confirmed_at TIMESTAMP DEFAULT NULL,
  reserved_at  TIMESTAMP   NOT NULL,
  expires_at   TIMESTAMP   NOT NULL,
  status       VARCHAR(20) NOT NULL CHECK (status IN
                                           ('IN_PROGRESS', 'CANCELLED', 'CONFIRMED', 'EXPIRED')),
  created_at   TIMESTAMP   NOT NULL,
  updated_at   TIMESTAMP   NOT NULL
);

CREATE TABLE RESERVATION_SEATS
(
  id              BIGINT PRIMARY KEY,
  reservation_id  BIGINT    NOT NULL,
  concert_seat_id BIGINT    NOT NULL,
  created_at      TIMESTAMP NOT NULL,
  updated_at      TIMESTAMP NOT NULL,
  UNIQUE (reservation_id, concert_seat_id)
);

CREATE TABLE QUEUE_TOKENS
(
  id         BIGINT PRIMARY KEY,
  user_id    VARCHAR(36)   NOT NULL,
  number     INT           NOT NULL,
  token      VARCHAR(1024) NOT NULL,
  status     VARCHAR(20)   NOT NULL CHECK (status IN ('WAITING', 'COMPLETED', 'CANCELLED', 'EXPIRED')),
  expires_at TIMESTAMP     NOT NULL,
  created_at TIMESTAMP     NOT NULL,
  updated_at TIMESTAMP     NOT NULL
);

CREATE INDEX idx_status ON QUEUE_TOKENS (status);
CREATE INDEX idx_user_id ON QUEUE_TOKENS (user_id);

CREATE TABLE POINT_TRANSACTIONS
(
  id              BIGINT PRIMARY KEY,
  point_wallet_id BIGINT      NOT NULL,
  type            VARCHAR(10) NOT NULL CHECK (type IN ('CHARGED', 'USED')),
  amount          BIGINT      NOT NULL,
  created_at      TIMESTAMP   NOT NULL,
  updated_at      TIMESTAMP   NOT NULL
);

CREATE INDEX idx_point_wallet_id ON POINT_TRANSACTIONS (point_wallet_id);

CREATE TABLE PAYMENTS
(
  id         BIGINT PRIMARY KEY,
  user_id    VARCHAR(36) NOT NULL,
  status     VARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'COMPLETED', 'CANCELLED')),
  price      BIGINT      NOT NULL,
  paid_at    TIMESTAMP DEFAULT NULL,
  created_at TIMESTAMP   NOT NULL,
  updated_at TIMESTAMP   NOT NULL
);

CREATE INDEX idx_user_id_status ON PAYMENTS (user_id, status);
CREATE INDEX idx_status ON PAYMENTS (status);

CREATE TABLE SEAT_HOLDS
(
  id              BIGINT PRIMARY KEY,
  concert_seat_id BIGINT      NOT NULL,
  user_id         VARCHAR(36) NOT NULL,
  held_at         TIMESTAMP   NOT NULL,
  expires_at      TIMESTAMP   NOT NULL,
  UNIQUE (concert_seat_id)
);

CREATE INDEX idx_status_expires_at ON SEAT_HOLDS (status, expires_at);
