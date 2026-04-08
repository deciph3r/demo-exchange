CREATE TABLE IF NOT EXISTS users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(60) NOT NULL
);
MERGE INTO users (username, password) KEY(username)
    VALUES
    ('abdullah', 'password'),
    ('tech_user', 'password'),
    ('finance_user', 'password'),
    ('balanced_user', 'password'),
    ('mixed_user', 'password');

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
                                       symbol VARCHAR(8) NOT NULL UNIQUE,
    name VARCHAR(255)
    );


INSERT INTO symbols (id, symbol, name) VALUES (1, 'AAPL', 'Apple Inc.');
INSERT INTO symbols (id, symbol, name) VALUES (2, 'TSLA', 'Tesla Inc.');
INSERT INTO symbols (id, symbol, name) VALUES (3, 'MSFT', 'Microsoft Corporation');
INSERT INTO symbols (id, symbol, name) VALUES (4, 'GOOGL', 'Alphabet Inc.');
INSERT INTO symbols (id, symbol, name) VALUES (5, 'NVDA', 'NVIDIA Corporation');

INSERT INTO symbols (id, symbol, name) VALUES (6, 'JPM', 'JPMorgan Chase & Co.');
INSERT INTO symbols (id, symbol, name) VALUES (7, 'GS', 'Goldman Sachs Group Inc.');
INSERT INTO symbols (id, symbol, name) VALUES (8, 'BAC', 'Bank of America Corporation');
INSERT INTO symbols (id, symbol, name) VALUES (9, 'MS', 'Morgan Stanley');
INSERT INTO symbols (id, symbol, name) VALUES (10, 'WFC', 'Wells Fargo & Company');

INSERT INTO symbols (id, symbol, name) VALUES (11, 'XOM', 'Exxon Mobil Corporation');
INSERT INTO symbols (id, symbol, name) VALUES (12, 'JNJ', 'Johnson & Johnson');

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

INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (1, 1, 100); -- AAPL
INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (1, 2, 80);  -- TSLA
INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (1, 5, 60);  -- NVDA


INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (2, 1, 50); -- AAPL
INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (2, 3, 50); -- MSFT
INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (2, 4, 50); -- GOOGL
INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (2, 2, 50); -- TSLA
INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (2, 5, 50); -- NVDA

INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (3, 6, 40); -- JPM
INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (3, 7, 40); -- GS
INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (3, 8, 40); -- BAC
INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (3, 9, 40); -- MS
INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (3, 10, 40); -- WFC

INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (4, 1, 70); -- AAPL
INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (4, 6, 70); -- JPM
INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (4, 11, 70); -- XOM
INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (4, 12, 70); -- JNJ
INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (4, 2, 70); -- TSLA


INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (5, 1, 30); -- AAPL
INSERT INTO portfolio (user_id, symbol_id, quantity) VALUES (5, 2, 30); -- TSLA
