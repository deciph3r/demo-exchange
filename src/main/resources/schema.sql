CREATE TABLE IF NOT EXISTS users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(60) NOT NULL
);
MERGE INTO users (username, password) KEY(username)
    VALUES ('abdullah', 'password');

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

CREATE TABLE IF NOT EXISTS symbols (
                                       id BIGINT PRIMARY KEY,
                                       symbol VARCHAR(4) NOT NULL UNIQUE,
    name VARCHAR(255)
    );


INSERT INTO symbols (id, symbol)
    VALUES (1, 'AAPL');

CREATE TABLE IF NOT EXISTS portfolio (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         user_id BIGINT NOT NULL,
                                         symbol_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    last_updated BIGINT DEFAULT EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) ,
    CONSTRAINT uq_portfolio_user_stock UNIQUE (user_id, symbol_id),
    CONSTRAINT fk_portfolio_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_portfolio_symbol FOREIGN KEY (symbol_id) REFERENCES symbols(id)
    );

insert INTO portfolio (user_id, symbol_id ,quantity)
    VALUES (1, 1, 100);

