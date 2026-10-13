# Sistem Manajemen Penyewaan Alat Mancing

Program berbasis **Java (CLI / Console)** untuk mengelola penyewaan alat mancing, dibuat sebagai tugas **Minpro 3 – Pemrograman Berorientasi Objek (PBO)**. Program memakai pola **MVC**, terhubung ke database **MySQL** lewat **JDBC (PreparedStatement) + DAO**, dan juga menyediakan implementasi **ORM (Hibernate)**.

## 👤 Identitas

| | |
|---|---|
| **Nama** | Mukhammad Ismukl Azam Atmoko |
| **NIM** | 2509116034 |
| **Kelas** | A |
| **Mata Kuliah** | Pemrograman Berorientasi Objek |

---

## Daftar Isi
1. [Deskripsi Singkat Program](#1-deskripsi-singkat-program)
2. [Penjelasan Struktur Package](#2-penjelasan-struktur-package)
3. [Penjelasan Alur Program](#3-penjelasan-alur-program)
4. [Penerapan Encapsulation dan Inheritance](#4-penerapan-encapsulation-dan-inheritance)
5. [Penerapan Polymorphism dan Abstraction](#5-penerapan-polymorphism-dan-abstraction)
6. [Penerapan JDBC](#6-penerapan-jdbc-preparedstatement-dan-dao)
7. [Penerapan ORM (Hibernate)](#7-penerapan-orm-hibernate)
8. [Pembuatan Database](#8-pembuatan-database)
9. [Output dan Pengujian](#9-output-dan-pengujian)
10. [Penerapan Nilai Tambah](#10-penerapan-nilai-tambah)
11. [Cara Menjalankan Program](#11-cara-menjalankan-program)

---

## 1. Deskripsi Singkat Program

Sistem ini membantu pengelola persewaan alat mancing dalam mencatat alat dan transaksi sewa. Alat terdiri dari dua jenis, yaitu **Joran** dan **Reel**. Seluruh data disimpan di database MySQL `db_penyewaan_mancing`, sehingga tidak hilang saat program ditutup.

| No | Fitur | Keterangan |
|----|-------|------------|
| 1 | Tampilkan Data Alat Mancing | Membaca seluruh alat dari database |
| 2 | Tambah Alat Mancing Baru | Menambah Joran atau Reel dengan validasi ID unik |
| 3 | Update Stok Alat Mancing | Menambah stok beserta catatan |
| 4 | Hapus Alat Mancing | Menghapus alat berdasarkan ID |
| 5 | Catat Transaksi Sewa Baru | Menghitung biaya, menyimpan transaksi, mengurangi stok |
| 6 | Tampilkan Riwayat Transaksi | Membaca seluruh transaksi dari database |
| 7 | Keluar | Mengakhiri program |

Data awal (dummy) diisi lewat file `database/db_penyewaan_mancing.sql`, **bukan lewat kode Java**:

| ID | Nama | Jenis | Stok | Harga/Hari | Atribut Khusus |
|----|------|-------|------|-----------|----------------|
| J-001 | Maguro Extreme | Joran | 15 | Rp25.000 | Kekuatan: Medium Heavy |
| R-001 | Shimano Stella | Reel | 5 | Rp50.000 | Bearing: 12 |

Transaksi awal: TRX-001 (Budi Santoso, Maguro Extreme, 2 hari, Rp50.000) dan TRX-002 (Siti Aminah, Shimano Stella, 3 hari, Rp135.000).

<img width="820" height="696" alt="image" src="https://github.com/user-attachments/assets/378ccd2f-504b-4351-b01b-cd0da49078b2" />

*Menu utama program dan mode akses data*

---

## 2. Penjelasan Struktur Package

```
com.mycompany_Minpro_3_PBO_Penyewaan
├── pom.xml
├── database
│   └── db_penyewaan_mancing.sql
└── src/main
    ├── resources
    │   └── db.properties
    └── java
        ├── Main        → Main.java
        ├── Config      → Koneksi.java, HibernateUtil.java
        ├── Model       → Disewakan, AlatMancing, Joran, Reel, Penyewa, Transaksi
        ├── DAO         → AlatDAO, TransaksiDAO, AlatDAOJdbc, TransaksiDAOJdbc,
        │                 AlatDAOHibernate, TransaksiDAOHibernate
        ├── View        → SewaView.java
        └── Controller  → SewaController.java
```

| Package | Peran |
|---------|-------|
| `Main` | Titik masuk program. Memilih mode akses data (`jdbc` atau `orm`), membuat DAO, View, dan Controller. |
| `Config` | `Koneksi` menyediakan koneksi JDBC; `HibernateUtil` membangun `SessionFactory`. Keduanya membaca `db.properties`. |
| `Model` | Data dan aturan bisnis: interface `Disewakan`, abstract class `AlatMancing`, turunan `Joran` dan `Reel`, serta `Transaksi`. `Penyewa` belum dipakai dalam alur; nama penyewa disimpan langsung di `Transaksi`. |
| `DAO` | Interface `AlatDAO` dan `TransaksiDAO`, dengan implementasi JDBC dan implementasi Hibernate. |
| `View` | Semua tampilan menu, output, serta input dan validasi input. |
| `Controller` | Penghubung Model, View, dan DAO; berisi alur program. |

<img width="515" height="772" alt="Screenshot 2026-10-13 221857" src="https://github.com/user-attachments/assets/7f213078-f47e-4707-b4b2-591c220fcc5f" />

*Struktur package project di NetBeans*

---

## 3. Penjelasan Alur Program

1. `Main` membaca mode (`jdbc` / `orm`) dari argumen atau `db.properties`, lalu membuat DAO yang sesuai, `SewaView`, dan `SewaController`.
2. `controller.mulai()` menampilkan menu 1-7 secara berulang sampai pengguna memilih 7.
3. Setiap menu memanggil DAO untuk membaca atau menulis database:
   - **Menu 1**: `alatDAO.getAll()` → View menampilkan `getInfo()` tiap alat.
   - **Menu 2**: input jenis, ID (dicek ke database agar tidak duplikat), nama, stok, harga, atribut khusus → `alatDAO.insert()`.
   - **Menu 3**: ambil alat dengan `getById()` → `tambahStok()` → `alatDAO.updateStok()`.
   - **Menu 4**: `getById()` → `alatDAO.delete()`.
   - **Menu 5**: cek ID transaksi → cek alat dan stok → cek lama sewa minimal 1 hari → `hitungTotalSewa()` → `sewa()` mengurangi stok → `transaksiDAO.simpan()` (insert transaksi dan update stok dalam satu transaksi database).
   - **Menu 6**: `transaksiDAO.getAll()` → tabel riwayat.
   - **Menu 7**: loop berhenti.

```
        ┌──────────────┐
        │  Main.main() │
        └──────┬───────┘
               ▼
   Buat SewaView & SewaController
               ▼
   Controller memuat data dummy (1 Joran + 1 Reel)
               ▼
        controller.mulai()
               ▼
   ┌─────────► Tampilkan Menu Utama (1-7) ◄──────────┐
   │                     │                            │
   │   ┌─────┬─────┬─────┼─────┬─────┬─────┐          │
   │   ▼     ▼     ▼     ▼     ▼     ▼     ▼          │
   │  [1]   [2]   [3]   [4]   [5]   [6]   [7]         │
   │ Lihat Tambah Update Hapus Catat Riwayat Keluar   │
   │  alat  alat  stok  alat  trans. trans.   │       │
   │   └─────┴─────┴─────┴─────┴─────┴────────┼───────┘
   │                                           ▼
   └───────────── (ulangi selama berjalan) →  Selesai
```
*Alur program: Main.main() dan SewaController.mulai()*

---

## 4. Penerapan Encapsulation dan Inheritance

**Encapsulation.** Semua atribut Model bersifat `private` dan diakses lewat getter. Stok tidak punya setter bebas; perubahan hanya lewat `sewa()` dan `tambahStok()`. Constructor memvalidasi data (nama kosong menjadi "Tanpa Nama", stok dan harga minimal 0). Constructor tanpa parameter dibuat `protected` dan hanya dibutuhkan Hibernate.

**Inheritance.** `Joran` dan `Reel` mewarisi `AlatMancing` (`extends`), memanggil constructor induk dengan `super(...)`, dan hanya menambah atribut khusus: `kekuatan` pada Joran dan `jumlahBearing` pada Reel. Method `sewa()`, `tambahStok()`, dan getter diwarisi.

```java
// Reel.java
@Override
public double hitungTotalSewa(int lamaHari) {
    double total = getHargaSewaPerHari() * lamaHari;
    if (lamaHari >= 3) {
      total = total - (total * 0.1); // diskon 10% untuk sewa 3 hari atau lebih
    }
    return total;
    }
```
```java
// Joran.java
    @Override
    public double hitungTotalSewa(int lamaHari) {
        return getHargaSewaPerHari() * lamaHari;
    }
```

*Encapsulation pada AlatMancing dan inheritance pada Joran dan Reel*

---

## 5. Penerapan Polymorphism dan Abstraction

**Polymorphism**
- **Overriding**: `getInfo()` dan `hitungTotalSewa()` ditulis ulang di `Joran` dan `Reel`. Joran menghitung `harga × lama`; Reel memberi diskon 10% bila sewa 3 hari atau lebih.
- **Pemanggilan polimorfik**: `List<AlatMancing>` dari DAO berisi `Joran` maupun `Reel`; saat `getInfo()` atau `hitungTotalSewa()` dipanggil, Java menjalankan versi class aslinya.
- **Overloading**: `tambahStok(int)` dan `tambahStok(int, String)`.
- **Polimorfisme DAO**: Controller hanya mengenal interface `AlatDAO` dan `TransaksiDAO`; objek aslinya (JDBC atau Hibernate) ditentukan di `Main`.

**Abstraction**
- Abstract class `AlatMancing` tidak bisa diinstansiasi langsung dan memiliki method abstract `getInfo()`.
- Interface `Disewakan` mendefinisikan kontrak `hitungTotalSewa()` dan `sewa()`.
- Interface `AlatDAO` dan `TransaksiDAO` menyembunyikan cara penyimpanan data.

![Overriding, overloading, abstract class, dan interface](screenshots/05-polymorphism-abstraction.png)
*Overriding, overloading, abstract class, dan interface*

---

## 6. Penerapan JDBC (PreparedStatement dan DAO)

- `Koneksi.getConnection()` membuat koneksi dengan `DriverManager` memakai URL, user, dan password dari `db.properties`. Driver: `mysql-connector-j` (di `pom.xml`).
- Semua query memakai **PreparedStatement** dengan parameter `?` (aman dari SQL injection) dan `try-with-resources`.
- Pola **DAO**: Controller tidak menulis SQL. `AlatDAOJdbc` dan `TransaksiDAOJdbc` mengimplementasikan interface DAO.
- `mapRow()` membaca kolom `jenis` untuk membuat objek `Joran` atau `Reel`.
- `TransaksiDAOJdbc.simpan()` memakai `setAutoCommit(false)`, `commit()`, dan `rollback()` agar insert transaksi dan update stok tersimpan bersama.

| Operasi | Method DAO | SQL |
|---------|-----------|-----|
| Create | `insert()` | `INSERT INTO alat_mancing ...` |
| Read | `getAll()`, `getById()` | `SELECT ... FROM alat_mancing` |
| Update | `updateStok()` | `UPDATE alat_mancing SET stok = ?, catatan_terakhir = ?` |
| Delete | `delete()` | `DELETE FROM alat_mancing WHERE id_alat = ?` |

![Penerapan JDBC: db.properties, Koneksi, PreparedStatement, mapRow, commit/rollback](screenshots/06-jdbc.png)
*Penerapan JDBC: db.properties, Koneksi, PreparedStatement, mapRow, commit/rollback*

---

## 7. Penerapan ORM (Hibernate)

Class Model dipetakan ke tabel dengan anotasi Jakarta Persistence. Hierarki `AlatMancing → Joran / Reel` memakai `SINGLE_TABLE`: satu tabel `alat_mancing` dengan kolom `jenis` sebagai pembeda. `HibernateUtil` membangun `SessionFactory` dari `db.properties` (`hbm2ddl.auto=none`, tabel dibuat lewat file SQL). `AlatDAOHibernate` dan `TransaksiDAOHibernate` mengimplementasikan interface DAO yang sama sehingga Controller tidak berubah. Mode dipilih lewat `dao.mode` di `db.properties` atau argumen `orm`.

![Penerapan ORM Hibernate: entity, HibernateUtil, dan DAO Hibernate](screenshots/07-orm.png)
*Penerapan ORM Hibernate: entity, HibernateUtil, dan DAO Hibernate*

---

## 8. Pembuatan Database

Database dibuat memakai **Laragon** (MySQL) dan **HeidiSQL**. Langkah: (1) jalankan Laragon dengan **Start All**, (2) buka HeidiSQL lewat tombol **Database**, (3) buat database `db_penyewaan_mancing`, (4) jalankan file `database/db_penyewaan_mancing.sql` (membuat tabel `alat_mancing` dan `transaksi` serta mengisi data awal), (5) cek struktur tabel.

![Pembuatan database dengan Laragon dan HeidiSQL](screenshots/08-pembuatan-database.png)
*Pembuatan database dengan Laragon dan HeidiSQL*

---

## 9. Output dan Pengujian

Pengujian dilakukan pada mode JDBC (`dao.mode=jdbc`), kemudian pada mode ORM (`dao.mode=orm`).

| No | Pengujian | Hasil yang diharapkan | Screenshot |
|----|-----------|-----------------------|------------|
| 1 | Menu 1 dan 6 tepat setelah program dijalankan | Data awal dari file SQL langsung tampil | 09 |
| 2 | Tambah Joran `J-002` dan Reel `R-002` | Data tersimpan di database | 10 |
| 3 | Update stok `J-001` (+5, dengan catatan) | Stok menjadi 20 dan catatan tersimpan | 10 |
| 4 | Hapus alat `R-002` | Baris hilang dari database | 10 |
| 5 | Transaksi Joran 2 hari (ID alat huruf kecil `j-001`) | Total Rp50.000, stok berkurang | 11 |
| 6 | Transaksi Reel 3 hari | Total Rp135.000 (diskon 10%), stok berkurang | 11 |
| 7 | ID alat duplikat, tambahan stok 0, ID alat tidak ada | Pesan error, database tidak berubah | 12 |
| 8 | ID transaksi duplikat, lama sewa 0, stok kosong | Pesan error, database tidak berubah | 12 |
| 9 | Input huruf pada menu dan input angka | Input diulang, program tidak crash | 12 |
| 10 | Hapus alat yang sudah punya transaksi | Riwayat transaksi tetap ada (`id_alat` menjadi NULL) | 13 |
| 11 | Tutup lalu jalankan program lagi | Data tetap tersimpan | 13 |
| 12 | Mode ORM: tampil, tambah, update stok, transaksi | Berfungsi sama dengan mode JDBC | 14 |

![Data awal dari file SQL langsung tampil (menu 1 dan 6) dan isi tabel di database](screenshots/09-data-awal.png)
*Data awal dari file SQL langsung tampil (menu 1 dan 6) dan isi tabel di database*

![Tambah, update stok, dan hapus alat beserta hasilnya di database](screenshots/10-tambah-update-hapus.png)
*Tambah, update stok, dan hapus alat beserta hasilnya di database*

![Transaksi sewa Joran dan Reel (diskon), stok berkurang, dan hasil di database](screenshots/11-transaksi.png)
*Transaksi sewa Joran dan Reel (diskon), stok berkurang, dan hasil di database*

![Validasi data dan validasi input](screenshots/12-validasi.png)
*Validasi data dan validasi input*

![Riwayat transaksi tetap ada walau alat dihapus, dan data tetap tersimpan](screenshots/13-riwayat-tetap-ada.png)
*Riwayat transaksi tetap ada walau alat dihapus, dan data tetap tersimpan*

![Pengujian mode ORM (Hibernate) beserta SQL yang dibuat Hibernate](screenshots/14-pengujian-orm.png)
*Pengujian mode ORM (Hibernate) beserta SQL yang dibuat Hibernate*

---

## 10. Penerapan Nilai Tambah

| No | Nilai Tambah | Letak Penerapan | Bukti Screenshot |
|----|--------------|-----------------|------------------|
| 1 | Data awal lewat file `.sql`, bukan kode | `database/db_penyewaan_mancing.sql` | 08, 09 |
| 2 | Arsitektur MVC + DAO | Package `Model`, `View`, `Controller`, `DAO` | 02 |
| 3 | Dua implementasi DAO yang bisa dipertukarkan (JDBC dan ORM) | `DAO/*Jdbc.java`, `DAO/*Hibernate.java`, `Main.java` | 07, 14 |
| 4 | Transaksi database atomik (commit / rollback) | `TransaksiDAOJdbc.simpan()` | 06, 11 |
| 5 | Stok berkurang otomatis saat disewa | `SewaController.catatTransaksi()` | 11 |
| 6 | Riwayat transaksi tetap utuh saat alat dihapus (`ON DELETE SET NULL`) | `db_penyewaan_mancing.sql` | 08, 13 |
| 7 | Konfigurasi database terpisah dari kode | `db.properties`, `Koneksi.java` | 06 |
| 8 | Kombinasi Interface + Abstract Class | `Disewakan.java`, `AlatMancing.java` | 05 |
| 9 | Method Overloading | `AlatMancing.tambahStok(...)` | 05 |
| 10 | Diskon otomatis 10% untuk Reel ≥ 3 hari | `Reel.hitungTotalSewa()` | 05, 11 |
| 11 | Validasi input angka | `SewaView.inputInt()`, `inputDouble()` | 12 |
| 12 | Validasi data (ID duplikat, ID tidak ditemukan, stok kosong, lama sewa < 1, tambahan stok ≤ 0) | `SewaController`, `AlatMancing` | 12 |
| 13 | Pencarian ID tidak case-sensitive | `getById()` pada DAO | 11, 12 |
| 14 | Catatan penambahan stok tersimpan di database | `AlatMancing.catatanTerakhir` | 10 |

---

## 11. Cara Menjalankan Program

**Kebutuhan:** JDK (sesuai `pom.xml`), Maven, dan MySQL (Laragon).

```bash
git clone https://github.com/ismulazam/Minpro-3-PBO-SistemPenyewaanAlatMancing.git
cd Minpro-3-PBO-SistemPenyewaanAlatMancing/com.mycompany_Minpro_3_PBO_Penyewaan

# Jalankan Laragon (Start All), lalu buat database + data awal (sekali saja)
# lewat HeidiSQL: File > Run SQL file > database/db_penyewaan_mancing.sql
# atau lewat Terminal Laragon:
mysql -u root < database/db_penyewaan_mancing.sql

# Sesuaikan user/password di src/main/resources/db.properties
mvn compile
mvn exec:java                       # mode JDBC
mvn exec:java -Dexec.args="orm"     # mode ORM (Hibernate)
```

Atau buka project di **NetBeans** lalu klik kanan `Main.java` → **Run File**. Untuk mode ORM, ubah `dao.mode=orm` di `db.properties`.
