-- Geolocation foundation (Part A).
-- Declarative, hierarchical administrative localities + PostGIS spatial support.
-- Requires a PostGIS-enabled PostgreSQL image (postgis/postgis).

-- CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS unaccent;

-- ── Administrative localities (REGION > DEPARTEMENT > ARRONDISSEMENT) ──────────
-- centroid is a STORED generated geography derived from lat/lng so JPA entities
-- can stay free of spatial types while native queries still use the GiST index.
CREATE TABLE localities (
    id          UUID PRIMARY KEY,
    name        VARCHAR(160) NOT NULL,
    level       VARCHAR(16)  NOT NULL,
    parent_id   UUID         REFERENCES localities (id),
    latitude    NUMERIC(9,6) NOT NULL,
    longitude   NUMERIC(9,6) NOT NULL,
    centroid    geography(Point, 4326)
                GENERATED ALWAYS AS (ST_SetSRID(ST_MakePoint(longitude, latitude), 4326)::geography) STORED,
    approximate BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT chk_localities_level CHECK (level IN ('REGION', 'DEPARTEMENT', 'ARRONDISSEMENT')),
    CONSTRAINT chk_localities_lat CHECK (latitude BETWEEN -90 AND 90),
    CONSTRAINT chk_localities_lng CHECK (longitude BETWEEN -180 AND 180),
    CONSTRAINT uq_localities_parent_name UNIQUE (parent_id, name)
);

CREATE INDEX idx_localities_centroid ON localities USING GIST (centroid);
CREATE INDEX idx_localities_parent   ON localities (parent_id);
CREATE INDEX idx_localities_level    ON localities (level);

-- ── Transport rate cards (admin-managed, not hard-coded) ──────────────────────
CREATE TABLE transport_rates (
    id                  UUID PRIMARY KEY,
    vehicle_class       VARCHAR(64)   NOT NULL,
    cost_per_km_per_ton NUMERIC(12,2) NOT NULL,
    min_cost            NUMERIC(12,2) NOT NULL DEFAULT 0,
    active              BOOLEAN       NOT NULL DEFAULT TRUE,
    valid_from          TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT chk_transport_cost CHECK (cost_per_km_per_ton >= 0),
    CONSTRAINT chk_transport_min CHECK (min_cost >= 0)
);

CREATE INDEX idx_transport_rates_active ON transport_rates (active);

-- ── Additive geo columns (backward compatible) ────────────────────────────────
-- NOTE: offers.location (free text) is intentionally kept for backward
-- compatibility. Enforcing locality_id NOT NULL and renaming location to
-- location_legacy is a follow-up migration once existing rows are backfilled.
ALTER TABLE offers ADD COLUMN locality_id UUID REFERENCES localities (id);
ALTER TABLE offers ADD COLUMN latitude    NUMERIC(9,6);
ALTER TABLE offers ADD COLUMN longitude   NUMERIC(9,6);
CREATE INDEX idx_offers_locality ON offers (locality_id);

ALTER TABLE users ADD COLUMN locality_id UUID REFERENCES localities (id);
ALTER TABLE users ADD COLUMN latitude    NUMERIC(9,6);
ALTER TABLE users ADD COLUMN longitude   NUMERIC(9,6);
