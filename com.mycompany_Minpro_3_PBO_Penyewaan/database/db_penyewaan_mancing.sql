-- =====================================================
-- Database Sistem Penyewaan Alat Mancing (Minpro 3 PBO)
-- Jalankan file ini SEKALI sebelum menjalankan program.
-- =====================================================
CREATE DATABASE IF NOT EXISTS db_penyewaan_mancing
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_penyewaan_mancing;

DROP TABLE IF EXISTS transaksi;
DROP TABLE IF EXISTS alat_mancing;

-- Satu tabel untuk Joran dan Reel (kolom 'jenis' sebagai pembeda)
CREATE TABLE alat_mancing (
    id_alat             VARCHAR(10)  NOT NULL,
    nama_alat           VARCHAR(50)  NOT NULL,
    jenis               VARCHAR(10)  NOT NULL,            -- 'Joran' atau 'Reel'
    stok                INT          NOT NULL DEFAULT 0,
    harga_sewa_per_hari DOUBLE       NOT NULL,
    catatan_terakhir    VARCHAR(255) NOT NULL DEFAULT '',
    kekuatan            VARCHAR(30)  NULL,                -- khusus Joran
    jumlah_bearing      INT          NULL,                -- khusus Reel
    PRIMARY KEY (id_alat),
    CONSTRAINT chk_jenis CHECK (jenis IN ('Joran', 'Reel'))
);

-- nama_alat disimpan ulang di transaksi agar riwayat tetap utuh
-- walaupun alatnya kelak dihapus (id_alat menjadi NULL).
CREATE TABLE transaksi (
    id_transaksi VARCHAR(20) NOT NULL,
    nama_penyewa VARCHAR(50) NOT NULL,
    id_alat      VARCHAR(10) NULL,
    nama_alat    VARCHAR(50) NOT NULL,
    lama_sewa    INT         NOT NULL,
    total_biaya  DOUBLE      NOT NULL,
    PRIMARY KEY (id_transaksi),
    CONSTRAINT fk_transaksi_alat FOREIGN KEY (id_alat)
        REFERENCES alat_mancing (id_alat)
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- ---------------- DATA AWAL (DUMMY) ----------------
INSERT INTO alat_mancing
    (id_alat, nama_alat, jenis, stok, harga_sewa_per_hari, catatan_terakhir, kekuatan, jumlah_bearing)
VALUES
    ('J-001', 'Maguro Extreme', 'Joran', 15, 25000, '', 'Medium Heavy', NULL),
    ('R-001', 'Shimano Stella', 'Reel',   5, 50000, '', NULL, 12);

INSERT INTO transaksi
    (id_transaksi, nama_penyewa, id_alat, nama_alat, lama_sewa, total_biaya)
VALUES
    ('TRX-001', 'Budi Santoso', 'J-001', 'Maguro Extreme', 2,  50000),
    ('TRX-002', 'Siti Aminah',  'R-001', 'Shimano Stella', 3, 135000);
