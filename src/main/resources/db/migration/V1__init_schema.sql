-- FoodChain AI - initial schema
-- Enums are modelled as VARCHAR + CHECK constraints for portability with JPA.

CREATE TABLE users (
    id            UUID PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(32)  NOT NULL,
    verified      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_users_role CHECK (role IN
        ('AGRICULTEUR','GROSSISTE','RESTAURANT','SUPERMARCHE','TRANSPORTEUR','ADMIN'))
);

CREATE TABLE products (
    id         UUID PRIMARY KEY,
    name       VARCHAR(255)   NOT NULL,
    category   VARCHAR(128)   NOT NULL,
    unit       VARCHAR(32)    NOT NULL,
    base_price NUMERIC(12,2)  NOT NULL,
    CONSTRAINT chk_products_base_price CHECK (base_price >= 0)
);

CREATE INDEX idx_products_category ON products (category);

CREATE TABLE offers (
    id         UUID PRIMARY KEY,
    seller_id  UUID           NOT NULL REFERENCES users (id),
    product_id UUID           NOT NULL REFERENCES products (id),
    quantity   NUMERIC(12,2)  NOT NULL,
    price      NUMERIC(12,2)  NOT NULL,
    available  BOOLEAN        NOT NULL DEFAULT TRUE,
    location   VARCHAR(255)   NOT NULL,
    version    BIGINT         NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ    NOT NULL DEFAULT now(),
    CONSTRAINT chk_offers_quantity CHECK (quantity >= 0),
    CONSTRAINT chk_offers_price CHECK (price >= 0)
);

CREATE INDEX idx_offers_product ON offers (product_id);
CREATE INDEX idx_offers_seller ON offers (seller_id);
CREATE INDEX idx_offers_location ON offers (location);
CREATE INDEX idx_offers_available ON offers (available);

CREATE TABLE orders (
    id         UUID PRIMARY KEY,
    buyer_id   UUID           NOT NULL REFERENCES users (id),
    status     VARCHAR(32)    NOT NULL,
    total      NUMERIC(14,2)  NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ    NOT NULL DEFAULT now(),
    CONSTRAINT chk_orders_status CHECK (status IN
        ('EN_ATTENTE','CONFIRMEE','PAYEE','EXPEDIEE','LIVREE','ANNULEE')),
    CONSTRAINT chk_orders_total CHECK (total >= 0)
);

CREATE INDEX idx_orders_buyer ON orders (buyer_id);

CREATE TABLE order_lines (
    id         UUID PRIMARY KEY,
    order_id   UUID           NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    offer_id   UUID           NOT NULL REFERENCES offers (id),
    quantity   NUMERIC(12,2)  NOT NULL,
    unit_price NUMERIC(12,2)  NOT NULL,
    CONSTRAINT chk_order_lines_quantity CHECK (quantity > 0),
    CONSTRAINT chk_order_lines_unit_price CHECK (unit_price >= 0)
);

CREATE INDEX idx_order_lines_order ON order_lines (order_id);
CREATE INDEX idx_order_lines_offer ON order_lines (offer_id);

CREATE TABLE payments (
    id         UUID PRIMARY KEY,
    order_id   UUID           NOT NULL REFERENCES orders (id),
    method     VARCHAR(32)    NOT NULL,
    status     VARCHAR(32)    NOT NULL,
    amount     NUMERIC(14,2)  NOT NULL,
    reference  VARCHAR(128)   NOT NULL UNIQUE,
    created_at TIMESTAMPTZ    NOT NULL DEFAULT now(),
    CONSTRAINT chk_payments_method CHECK (method IN ('MOBILE_MONEY','A_LA_LIVRAISON')),
    CONSTRAINT chk_payments_status CHECK (status IN ('INITIE','REUSSI','ECHOUE','REMBOURSE')),
    CONSTRAINT chk_payments_amount CHECK (amount >= 0)
);

CREATE INDEX idx_payments_order ON payments (order_id);

CREATE TABLE reviews (
    id         UUID PRIMARY KEY,
    order_id   UUID           NOT NULL REFERENCES orders (id),
    author_id  UUID           NOT NULL REFERENCES users (id),
    rating     INTEGER        NOT NULL,
    comment    VARCHAR(2000),
    created_at TIMESTAMPTZ    NOT NULL DEFAULT now(),
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT uq_reviews_order_author UNIQUE (order_id, author_id)
);

CREATE INDEX idx_reviews_order ON reviews (order_id);

-- Price history table - foundation for Phase 3 analytics.
CREATE TABLE price_history (
    id         UUID PRIMARY KEY,
    product_id UUID           NOT NULL REFERENCES products (id),
    price      NUMERIC(12,2)  NOT NULL,
    ts         TIMESTAMPTZ    NOT NULL DEFAULT now(),
    CONSTRAINT chk_price_history_price CHECK (price >= 0)
);

CREATE INDEX idx_price_history_product_ts ON price_history (product_id, ts);
