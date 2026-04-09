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


CREATE TABLE IF NOT EXISTS symbols (
                                       id BIGINT PRIMARY KEY,
                                       symbol VARCHAR(8) NOT NULL UNIQUE,
    name VARCHAR(255),
    sector VARCHAR(50) NOT NULL,
    CONSTRAINT chk_sector CHECK (sector IN ('TECH', 'FINANCE', 'ENERGY', 'HEALTH'))
    );

-- TECH
INSERT INTO symbols (id, symbol, name, sector) VALUES (1, 'AAPL', 'Apple Inc.', 'TECH');
INSERT INTO symbols (id, symbol, name, sector) VALUES (2, 'TSLA', 'Tesla Inc.', 'TECH');
INSERT INTO symbols (id, symbol, name, sector) VALUES (3, 'MSFT', 'Microsoft Corporation', 'TECH');
INSERT INTO symbols (id, symbol, name, sector) VALUES (4, 'GOOGL', 'Alphabet Inc.', 'TECH');
INSERT INTO symbols (id, symbol, name, sector) VALUES (5, 'NVDA', 'NVIDIA Corporation', 'TECH');

-- FINANCE
INSERT INTO symbols (id, symbol, name, sector) VALUES (6, 'JPM', 'JPMorgan Chase & Co.', 'FINANCE');
INSERT INTO symbols (id, symbol, name, sector) VALUES (7, 'GS', 'Goldman Sachs Group Inc.', 'FINANCE');
INSERT INTO symbols (id, symbol, name, sector) VALUES (8, 'BAC', 'Bank of America Corporation', 'FINANCE');
INSERT INTO symbols (id, symbol, name, sector) VALUES (9, 'MS', 'Morgan Stanley', 'FINANCE');
INSERT INTO symbols (id, symbol, name, sector) VALUES (10, 'WFC', 'Wells Fargo & Company', 'FINANCE');

-- ENERGY
INSERT INTO symbols (id, symbol, name, sector) VALUES (11, 'XOM', 'Exxon Mobil Corporation', 'ENERGY');

-- HEALTH
INSERT INTO symbols (id, symbol, name, sector) VALUES (12, 'JNJ', 'Johnson & Johnson', 'HEALTH');

CREATE TABLE IF NOT EXISTS orders (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      trader_id BIGINT,
    stock_id BIGINT,
    quantity INT,
    take_profit DOUBLE,
    stop_loss DOUBLE,
    side VARCHAR(10),
    state VARCHAR(10),
    created_at BIGINT,
    expires_at BIGINT,
    CONSTRAINT fk_order_user FOREIGN KEY (trader_id) REFERENCES users(id),
    CONSTRAINT fk_order_stock FOREIGN KEY (stock_id) REFERENCES symbols(id)
    );





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



-- abdullah (user_id = 1)
INSERT INTO orders (trader_id, stock_id, quantity, take_profit, stop_loss, side, state, created_at, expires_at)
VALUES
    (1, 1, 20, 200, 150, 'BUY', 'PENDING', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP), EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)+3600), -- AAPL
    (1, 2, 10, 300, 200, 'SELL', 'PENDING', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP), EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)+3600), -- TSLA
    (1, 5, 15, 800, 600, 'BUY', 'PENDING', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP), EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)+3600); -- NVDA
-- tech_user (user_id = 2)
INSERT INTO orders (trader_id, stock_id, quantity, take_profit, stop_loss, side, state, created_at, expires_at)
VALUES
    (2, 3, 25, 400, 300, 'BUY', 'PENDING', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP), EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)+7200), -- MSFT
    (2, 4, 10, 180, 120, 'SELL', 'PENDING', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP), EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)+7200), -- GOOGL
    (2, 1, 5, 210, 160, 'SELL', 'PENDING', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP), EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)+7200); -- AAPL
-- finance_user (user_id = 3)
INSERT INTO orders (trader_id, stock_id, quantity, take_profit, stop_loss, side, state, created_at, expires_at)
VALUES
    (3, 6, 20, 180, 120, 'BUY', 'PENDING', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP), EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)+3600), -- JPM
    (3, 7, 15, 350, 250, 'SELL', 'PENDING', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP), EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)+3600); -- GS
-- balanced_user (user_id = 4)
INSERT INTO orders (trader_id, stock_id, quantity, take_profit, stop_loss, side, state, created_at, expires_at)
VALUES
    (4, 11, 10, 120, 90, 'BUY', 'PENDING', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP), EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)+3600), -- XOM
    (4, 12, 5, 200, 140, 'SELL', 'PENDING', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP), EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)+3600); -- JNJ
-- mixed_user (user_id = 5)
INSERT INTO orders (trader_id, stock_id, quantity, take_profit, stop_loss, side, state, created_at, expires_at)
VALUES
    (5, 1, 10, 210, 150, 'BUY', 'PENDING', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP), EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)+3600), -- AAPL
    (5, 2, 5, 320, 220, 'SELL', 'PENDING', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP), EXTRACT(EPOCH FROM CURRENT_TIMESTAMP)+3600); -- TSLA