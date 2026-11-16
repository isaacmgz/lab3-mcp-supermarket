CREATE EXTENSION IF NOT EXISTS unaccent;

CREATE TABLE category (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(80)  NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE product (
    id          BIGSERIAL PRIMARY KEY,
    sku         VARCHAR(20)    NOT NULL UNIQUE,
    name        VARCHAR(120)   NOT NULL,
    description VARCHAR(255),
    unit        VARCHAR(30)    NOT NULL,
    price       NUMERIC(12, 2) NOT NULL CHECK (price > 0),
    stock       INTEGER        NOT NULL CHECK (stock >= 0),
    active      BOOLEAN        NOT NULL DEFAULT TRUE,
    category_id BIGINT         NOT NULL REFERENCES category (id)
);

CREATE INDEX idx_product_category ON product (category_id);

CREATE TABLE customer (
    id           BIGSERIAL PRIMARY KEY,
    full_name    VARCHAR(120) NOT NULL,
    phone        VARCHAR(20)  NOT NULL UNIQUE,
    email        VARCHAR(120),
    address      VARCHAR(200) NOT NULL,
    neighborhood VARCHAR(80),
    city         VARCHAR(80)  NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE customer_order (
    id               BIGSERIAL PRIMARY KEY,
    customer_id      BIGINT         NOT NULL REFERENCES customer (id),
    status           VARCHAR(20)    NOT NULL,
    payment_method   VARCHAR(30),
    delivery_address VARCHAR(300),
    subtotal         NUMERIC(12, 2) NOT NULL DEFAULT 0,
    delivery_fee     NUMERIC(12, 2) NOT NULL DEFAULT 0,
    total            NUMERIC(12, 2) NOT NULL DEFAULT 0,
    created_at       TIMESTAMPTZ    NOT NULL DEFAULT now(),
    confirmed_at     TIMESTAMPTZ,
    cancelled_at     TIMESTAMPTZ,
    version          BIGINT         NOT NULL DEFAULT 0
);

CREATE INDEX idx_order_customer ON customer_order (customer_id);

CREATE TABLE order_item (
    id         BIGSERIAL PRIMARY KEY,
    order_id   BIGINT         NOT NULL REFERENCES customer_order (id) ON DELETE CASCADE,
    product_id BIGINT         NOT NULL REFERENCES product (id),
    quantity   INTEGER        NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(12, 2) NOT NULL,
    line_total NUMERIC(12, 2) NOT NULL,
    UNIQUE (order_id, product_id)
);
