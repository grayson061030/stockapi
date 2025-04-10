CREATE TABLE "stocks" (
  "stock_id" Long PRIMARY KEY,
  "ticker" String NOT NULL,
  "name" String NOT NULL,
  "created_at" DateTime NOT NULL DEFAULT (now()),
  "updated_at" DateTime NOT NULL DEFAULT (now())
);

CREATE TABLE "stock_prices" (
  "price_id" SERIAL PRIMARY KEY,
  "stock_id" Long NOT NULL,
  "price" Double NOT NULL,
  "previous_price" Double NOT NULL,
  "price_date" Date NOT NULL,
  "volume" Long NOT NULL,
  "created_at" DateTime NOT NULL DEFAULT (now()),
  "updated_at" DateTime NOT NULL DEFAULT (now())
);

CREATE TABLE "stock_statistics" (
  "statistics_id" SERIAL PRIMARY KEY,
  "stock_id" Long NOT NULL,
  "view_count" Long NOT NULL DEFAULT 0,
  "buy_order_volume" Long NOT NULL DEFAULT 0,
  "sell_order_volume" Long NOT NULL DEFAULT 0,
  "turnover_rate" Double NOT NULL DEFAULT 0,
  "created_at" DateTime NOT NULL DEFAULT (now()),
  "updated_at" DateTime NOT NULL DEFAULT (now())
);

CREATE TABLE "tags" (
  "tag_id" SERIAL PRIMARY KEY,
  "name" ENUM(POPULAR,RISING,FALLING,VOLUME) UNIQUE NOT NULL,
  "description" String NOT NULL
);

CREATE TABLE "stock_tags" (
  "stock_id" Long NOT NULL,
  "tag_id" Long NOT NULL,
  PRIMARY KEY ("stock_id", "tag_id")
);

ALTER TABLE "stock_prices" ADD FOREIGN KEY ("stock_id") REFERENCES "stocks" ("stock_id");

ALTER TABLE "stock_statistics" ADD FOREIGN KEY ("stock_id") REFERENCES "stocks" ("stock_id");

ALTER TABLE "stock_tags" ADD FOREIGN KEY ("stock_id") REFERENCES "stocks" ("stock_id");

ALTER TABLE "stock_tags" ADD FOREIGN KEY ("tag_id") REFERENCES "tags" ("tag_id");
