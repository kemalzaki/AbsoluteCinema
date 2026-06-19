-- ============================================================
-- AbsoluteCinema — Database Schema (DDL) & Seed Data (DML)
-- ============================================================
-- Target DB : MySQL 8.x
-- Inheritance: JOINED — Tayangan is the parent table; Film &
--              SerialTV are child tables sharing the PK (id)
--              with a FK back to tayangan(id).
--
-- USAGE:
--   Option A (recommended): Start the Spring Boot app once so
--     Hibernate creates the tables (ddl-auto: update), then run
--     ONLY the INSERT section below to seed data.
--   Option B: Run this whole file against an empty database to
--     build the schema and seed data manually before first start.
--
-- NOTE: Run on a fresh database or TRUNCATE existing tables first.
--       Inserts assume empty tables (auto-increment IDs start at 1).
-- ============================================================

-- ------------------------------------------------------------
-- 1. SCHEMA (DDL) — CREATE TABLE IF NOT EXISTS (idempotent)
-- ------------------------------------------------------------

CREATE TABLE IF NOT EXISTS tayangan (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    judul           VARCHAR(255) NOT NULL,
    sinopsis        VARCHAR(1000),
    tahun_rilis     INT          NOT NULL,
    total_skor      DOUBLE       NOT NULL DEFAULT 0,
    jumlah_reviewer INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS film (
    id           BIGINT       NOT NULL,
    durasi_menit INT          NOT NULL,
    genre        VARCHAR(255),
    sutradara    VARCHAR(255),
    PRIMARY KEY (id),
    CONSTRAINT fk_film_tayangan FOREIGN KEY (id) REFERENCES tayangan(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS serial_tv (
    id             BIGINT       NOT NULL,
    jumlah_musim   INT          NOT NULL,
    total_episode  INT          NOT NULL,
    jaringan_tv    VARCHAR(255),
    masih_berjalan BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (id),
    CONSTRAINT fk_serialtv_tayangan FOREIGN KEY (id) REFERENCES tayangan(id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS users (
    id       BIGINT       NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS reviews (
    id          VARCHAR(36)  NOT NULL,
    skor        INT          NOT NULL,
    teks        VARCHAR(1000),
    user_id     BIGINT       NOT NULL,
    tayangan_id BIGINT       NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_review_user     FOREIGN KEY (user_id)     REFERENCES users(id),
    CONSTRAINT fk_review_tayangan FOREIGN KEY (tayangan_id) REFERENCES tayangan(id)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- 2. SEED DATA (DML)
-- ------------------------------------------------------------

-- 2.1 Users — role must be ADMIN or MEMBER (per User entity validator)
INSERT INTO users (username, password, role) VALUES
    ('johndoe', 'password123', 'MEMBER'),
    ('janedoe', 'password123', 'MEMBER'),
    ('admin',   'admin123',    'ADMIN');

-- 2.2 Tayangan (parent rows) — IDs auto-generate as 1, 2, 3
INSERT INTO tayangan (judul, sinopsis, tahun_rilis, total_skor, jumlah_reviewer) VALUES
    ('The Walking Dead', 'Kisah bertahan hidup di tengah zombie apocalypse.', 2010, 0, 0),
    ('Capitol Cinema',   'Film dokumenter tentang bioskop klasik.',           2021, 0, 0),
    ('Classic Films',    'Kumpulan film hitam putih terbaik sepanjang masa.', 1995, 0, 0);

-- 2.3 Film (child rows — id must match tayangan.id, JOINED strategy)
INSERT INTO film (id, durasi_menit, genre, sutradara) VALUES
    (2, 120, 'Documentary', 'Christopher Nolan'),
    (3, 150, 'Drama',       'Steven Spielberg');

-- 2.4 Serial TV (child row — id must match tayangan.id, JOINED strategy)
INSERT INTO serial_tv (id, jumlah_musim, total_episode, jaringan_tv, masih_berjalan) VALUES
    (1, 11, 177, 'AMC', FALSE);

-- 2.5 Sample Reviews — one per tayangan, from different users.
--     Fixed UUIDs so re-running stays stable if tables are truncated.
INSERT INTO reviews (id, skor, teks, user_id, tayangan_id) VALUES
    ('a1b2c3d4-0001-4000-8000-000000000001', 5, 'Zombie apocalypse terbaik sepanjang masa!', 1, 1),
    ('a1b2c3d4-0001-4000-8000-000000000002', 4, 'Dokumenter yang menyentuh hati.',            1, 2),
    ('a1b2c3d4-0001-4000-8000-000000000003', 5, 'Karya klasik yang abadi.',                   2, 3);

-- 2.6 Sync denormalized aggregates on tayangan with the reviews above.
--     (total_skor / jumlah_reviewer drive the displayed average rating.)
UPDATE tayangan SET total_skor = 5, jumlah_reviewer = 1 WHERE id = 1;
UPDATE tayangan SET total_skor = 4, jumlah_reviewer = 1 WHERE id = 2;
UPDATE tayangan SET total_skor = 5, jumlah_reviewer = 1 WHERE id = 3;
