CREATE TABLE IF NOT EXISTS users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(60) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      trader_id VARCHAR(50),
    stock VARCHAR(255),
    sector VARCHAR(255),
    quantity INT,
    take_profit DOUBLE,
    stop_loss DOUBLE,
    side VARCHAR(10),
    state VARCHAR(10),
    created_at BIGINT,
    expires_at BIGINT
    );

MERGE INTO users (username, password) KEY(username)
    VALUES ('abdullah', 'password');