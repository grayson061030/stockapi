-- 태그 데이터 추가 (존재하지 않는 경우만)
INSERT INTO tags (tag_id, name, description)
SELECT 1, 'POPULAR', '사람들이 많이 본'
    WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'POPULAR');

INSERT INTO tags (tag_id, name, description)
SELECT 2, 'RISING', '가격이 많이 오른'
    WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'RISING');

INSERT INTO tags (tag_id, name, description)
SELECT 3, 'FALLING', '가격이 많이 내린'
    WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'FALLING');

INSERT INTO tags (tag_id, name, description)
SELECT 4, 'VOLUME', 'VOLUME 많은'
    WHERE NOT EXISTS (SELECT 1 FROM tags WHERE name = 'VOLUME');

-- 주식 기본 데이터 추가
INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 1, '5930', '삼성전자', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 1);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 2, '373220', 'LG에너지솔루션', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 2);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 3, '660', 'SK하이닉스', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 3);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 4, '207940', '삼성바이오로직스', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 4);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 5, '5935', '삼성전자우', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 5);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 6, '51910', 'LG화학', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 6);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 7, '6400', '삼성SDI', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 7);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 8, '35420', 'NAVER', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 8);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 9, '5380', '현대차', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 9);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 10, '35720', '카카오', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 10);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 11, '270', '기아', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 11);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 12, '68270', '셀트리온', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 12);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 13, '28260', '삼성물산', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 13);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 14, '5490', 'POSCO홀딩스', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 14);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 15, '105560', 'KB금융', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 15);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 16, '12330', '현대모비스', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 16);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 17, '96770', 'SK이노베이션', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 17);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 18, '55550', '신한지주', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 18);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 19, '34730', 'SK', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 19);

INSERT INTO stocks (stock_id, ticker, name, created_at, updated_at)
SELECT 20, '66570', 'LG전자', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stocks WHERE stock_id = 20);

-- 주식 가격 데이터 추가
INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 1, 61500, 62115, CURRENT_DATE(), 9757604, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 1 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 2, 452000, 447480, CURRENT_DATE(), 1187695, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 2 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 3, 95700, 99528, CURRENT_DATE(), 4499644, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 3 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 4, 875000, 857500, CURRENT_DATE(), 3864512, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 4 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 5, 56400, 55836, CURRENT_DATE(), 7444871, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 5 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 6, 651000, 631470, CURRENT_DATE(), 4450502, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 6 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 7, 624000, 642720, CURRENT_DATE(), 6574804, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 7 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 8, 250000, 262500, CURRENT_DATE(), 3248979, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 8 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 9, 186000, 182280, CURRENT_DATE(), 7517091, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 9 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 10, 79200, 77616, CURRENT_DATE(), 6284548, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 10 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 11, 76700, 74399, CURRENT_DATE(), 2008054, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 11 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 12, 202500, 202500, CURRENT_DATE(), 8414238, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 12 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 13, 120500, 119295, CURRENT_DATE(), 9932438, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 13 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 14, 256000, 253440, CURRENT_DATE(), 2204632, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 14 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 15, 50700, 48672, CURRENT_DATE(), 2228465, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 15 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 16, 211000, 206780, CURRENT_DATE(), 2932042, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 16 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 17, 210500, 216815, CURRENT_DATE(), 8530024, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 17 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 18, 35500, 35855, CURRENT_DATE(), 3812279, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 18 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 19, 236000, 233640, CURRENT_DATE(), 497921, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 19 AND price_date = CURRENT_DATE());

INSERT INTO stock_prices (stock_id, price, previous_price, price_date, volume, created_at, updated_at)
SELECT 20, 102000, 100980, CURRENT_DATE(), 921100, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_prices WHERE stock_id = 20 AND price_date = CURRENT_DATE());

-- 주식 통계 데이터 추가 (랜덤 값)
INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 1, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 1);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 2, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 2);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 3, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 3);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 4, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 4);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 5, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 5);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 6, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 6);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 7, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 7);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 8, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 8);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 9, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 9);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 10, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 10);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 11, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 11);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 12, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 12);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 13, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 13);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 14, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 14);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 15, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 15);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 16, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 16);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 17, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 17);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 18, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 18);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 19, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 19);

INSERT INTO stock_statistics (stock_id, view_count, buy_order_volume, sell_order_volume, turnover_rate, created_at, updated_at)
SELECT 20, FLOOR(RAND() * 10000), FLOOR(RAND() * 100000), FLOOR(RAND() * 100000), RAND() * 10, CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()
    WHERE NOT EXISTS (SELECT 1 FROM stock_statistics WHERE stock_id = 20);