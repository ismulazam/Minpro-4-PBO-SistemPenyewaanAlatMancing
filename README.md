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

```java
// Disewakan.java (Interface Kontrak)
public interface Disewakan {
    double hitungTotalSewa(int lamaHari);
    void sewa(int jumlah);
}

// AlatMancing.java (Abstract Class)
public abstract class AlatMancing implements Disewakan {
    private String idAlat;
    private String nama;
    private double hargaSewaPerHari;
    private int stok;

    // Abstract method: memaksa class turunan mengimplementasikan getInfo()
    public abstract String getInfo();

    // Overloading: tambahStok(int) dan tambahStok(int, String)
    public void tambahStok(int jumlah) {
        this.stok += jumlah;
    }

    public void tambahStok(int jumlah, String catatan) {
        this.stok += jumlah;
        System.out.println("Stok ditambah " + jumlah + " (" + catatan + ")");
    }

    public double getHargaSewaPerHari() { return hargaSewaPerHari; }
    public String getNama() { return nama; }
}
```

```java
// Joran.java
public class Joran extends AlatMancing {
    private String kekuatan;

    @Override
    public String getInfo() {
        return "Joran: " + getNama() + " | Kekuatan: " + kekuatan;
    }

    @Override
    public double hitungTotalSewa(int lamaHari) {
        return getHargaSewaPerHari() * lamaHari;
    }
}

// Reel.java
public class Reel extends AlatMancing {
    private int jumlahBearing;

    @Override
    public String getInfo() {
        return "Reel: " + getNama() + " | Bearing: " + jumlahBearing;
    }

    @Override
    public double hitungTotalSewa(int lamaHari) {
        double total = getHargaSewaPerHari() * lamaHari;
        if (lamaHari >= 3) {
            total = total - (total * 0.1); // Diskon 10% jika sewa 3 hari atau lebih
        }
        return total;
    }
}
```

```java
// AlatDAO.java (Interface DAO)
public interface AlatDAO {
    List<AlatMancing> getAll();
}

// Main.java / Controller
public class Main {
    public static void main(String[] args) {
        // Polimorfisme DAO: Controller hanya kenal interface AlatDAO
        AlatDAO alatDao = new AlatDAOHibernate(); // atau AlatDAOJdbc()

        // Pemanggilan Polimorfik dari List<AlatMancing>
        List<AlatMancing> daftarAlat = alatDao.getAll();

        for (AlatMancing alat : daftarAlat) {
            // Java secara dinamis menjalankan getInfo() dan hitungTotalSewa() 
            // sesuai class aslinya (Joran / Reel)
            System.out.println(alat.getInfo());
            System.out.println("Total 3 Hari: " + alat.hitungTotalSewa(3));
        }
    }
}
```
Abstraction: Ditempatkan di Disewakan (interface) dan AlatMancing (abstract class dengan public abstract String getInfo()).

Overriding: Terlihat pada implementasi getInfo() dan hitungTotalSewa() di Joran dan Reel.

Overloading: Terlihat pada method tambahStok(int) dan tambahStok(int, String) di AlatMancing.

Pemanggilan Polimorfik & DAO: Terlihat saat iterasi List<AlatMancing> dan penggunaan AlatDAO.
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

```properties
# db.properties
db.url=jdbc:mysql://localhost:3306/db_mancing
db.user=root
db.password=
```

```java
// Koneksi.java
public class Koneksi {
    public static Connection getConnection() throws SQLException {
        // Membaca konfigurasi dari db.properties
        Properties props = new Properties();
        try (InputStream input = Koneksi.class.getClassLoader().getResourceAsStream("db.properties")) {
            props.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(props.getProperty("db.url"), props.getProperty("db.user"), props.getProperty("db.password"));
    }
}
```

```java
// AlatDAOJdbc.java (PreparedStatement, try-with-resources, & mapRow)
public class AlatDAOJdbc implements AlatDAO {
    
    @Override
    public List<AlatMancing> getAll() {
        List<AlatMancing> list = new ArrayList<>();
        String sql = "SELECT * FROM alat_mancing";
        
        // Memakai try-with-resources & PreparedStatement
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Pemetaan ResultSet ke Objek Joran/Reel
    private AlatMancing mapRow(ResultSet rs) throws SQLException {
        String jenis = rs.getString("jenis");
        if ("Joran".equalsIgnoreCase(jenis)) {
            return new Joran(rs.getString("id_alat"), rs.getString("nama"), rs.getDouble("harga"), rs.getInt("stok"), rs.getString("kekuatan"));
        } else {
            return new Reel(rs.getString("id_alat"), rs.getString("nama"), rs.getDouble("harga"), rs.getInt("stok"), rs.getInt("jumlah_bearing"));
        }
    }
}
```

```java
// TransaksiDAOJdbc.java (Manajemen Transaksi: Commit & Rollback)
public class TransaksiDAOJdbc implements TransaksiDAO {
    
    public void simpan(Transaksi tx) {
        String sqlTx = "INSERT INTO transaksi (id_tx, id_alat, lama_sewa, total) VALUES (?, ?, ?, ?)";
        String sqlStok = "UPDATE alat_mancing SET stok = stok - ? WHERE id_alat = ?";
        
        Connection conn = null;
        try {
            conn = Koneksi.getConnection();
            conn.setAutoCommit(false); // 1. Matikan Auto Commit

            try (PreparedStatement stmtTx = conn.prepareStatement(sqlTx);
                 PreparedStatement stmtStok = conn.prepareStatement(sqlStok)) {
                
                // Insert Transaksi
                stmtTx.setString(1, tx.getId());
                stmtTx.setString(2, tx.getAlat().getIdAlat());
                stmtTx.setInt(3, tx.getLamaSewa());
                stmtTx.setDouble(4, tx.getTotalHarga());
                stmtTx.executeUpdate();

                // Update Stok
                stmtStok.setInt(1, tx.getJumlahSewa());
                stmtStok.setString(2, tx.getAlat().getIdAlat());
                stmtStok.executeUpdate();

                conn.commit(); // 2. Commit jika kedua query berhasil
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // 3. Rollback jika ada kegagalan
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        }
    }
}
```
*Penerapan JDBC: db.properties, Koneksi, PreparedStatement, mapRow, commit/rollback*

---

## 7. Penerapan ORM (Hibernate)

Class Model dipetakan ke tabel dengan anotasi Jakarta Persistence. Hierarki `AlatMancing → Joran / Reel` memakai `SINGLE_TABLE`: satu tabel `alat_mancing` dengan kolom `jenis` sebagai pembeda. `HibernateUtil` membangun `SessionFactory` dari `db.properties` (`hbm2ddl.auto=none`, tabel dibuat lewat file SQL). `AlatDAOHibernate` dan `TransaksiDAOHibernate` mengimplementasikan interface DAO yang sama sehingga Controller tidak berubah. Mode dipilih lewat `dao.mode` di `db.properties` atau argumen `orm`.

```java
// AlatMancing.java (Abstract Class / Superclass Entity)
import jakarta.persistence.*;

@Entity
@Table(name = "alat_mancing")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Strategi Single Table
@DiscriminatorColumn(name = "jenis", discriminatorType = DiscriminatorType.STRING) // Kolom pembeda Joran/Reel
public abstract class AlatMancing implements Disewakan {
    
    @Id
    @Column(name = "id_alat")
    private String idAlat;

    @Column(name = "nama")
    private String nama;

    @Column(name = "harga_sewa_per_hari")
    private double hargaSewaPerHari;

    @Column(name = "stok")
    private int stok;

    // Constructor tanpa parameter (Dibutuhkan Hibernate/JPA)
    protected AlatMancing() {}

    // Getter, Setter, dan Abstract Method
    public abstract String getInfo();
}
```

```java
// Joran.java (Subclass Entity)
import jakarta.persistence.*;

@Entity
@DiscriminatorValue("Joran") // Nilai pembeda di kolom jenis
public class Joran extends AlatMancing {
    
    @Column(name = "kekuatan")
    private String kekuatan;

    protected Joran() { super(); } // Required by Hibernate

    @Override
    public String getInfo() {
        return "Joran: " + getNama() + " | Kekuatan: " + kekuatan;
    }
}
```

```java
// HibernateUtil.java (Konfigurasi & Builder SessionFactory)
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration cfg = new Configuration();
            // Memuat konfigurasi dari db.properties
            cfg.configure("hibernate.cfg.xml"); // atau dibaca langsung via Properties
            cfg.addAnnotatedClass(AlatMancing.class);
            cfg.addAnnotatedClass(Joran.class);
            cfg.addAnnotatedClass(Reel.class);
            cfg.addAnnotatedClass(Transaksi.class);

            return cfg.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Inisialisasi SessionFactory gagal: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
```

```java
// AlatDAOHibernate.java (Implementasi Interface DAO dengan Hibernate Session)
import org.hibernate.Session;
import org.hibernate.Transaction;

public class AlatDAOHibernate implements AlatDAO {

    @Override
    public List<AlatMancing> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Menggunakan HQL (Hibernate Query Language)
            return session.createQuery("FROM AlatMancing", AlatMancing.class).list();
        }
    }

    @Override
    public void insert(AlatMancing alat) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(alat); // Menyimpan entity ke database
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
    }
}
```

```properties
# db.properties (Pengaturan mode DAO)
dao.mode=hibernate
# hibernate.hbm2ddl.auto=none
```
*Penerapan ORM Hibernate: entity, HibernateUtil, dan DAO Hibernate*

---

## 8. Pembuatan Database

Database dibuat memakai **Laragon** (MySQL) dan **HeidiSQL**. Langkah: (1) jalankan Laragon dengan **Start All**, (2) buka phpmyadmin lewat tombol **Database**, (3) buat database `db_penyewaan_mancing`, (4) jalankan file `database/db_penyewaan_mancing.sql` (membuat tabel `alat_mancing` dan `transaksi` serta mengisi data awal), (5) cek struktur tabel.

<img width="1246" height="536" alt="image" src="https://github.com/user-attachments/assets/63832f9f-20fb-4601-b66f-d09397e6caa0" />

```sql
-- 1. Buat Database
CREATE DATABASE IF NOT EXISTS db_penyewaan_mancing;
USE db_penyewaan_mancing;

-- 2. Buat Tabel Single-Table Inheritance (Alat Mancing)
CREATE TABLE IF NOT EXISTS alat_mancing (
    id_alat VARCHAR(20) PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    harga_sewa_per_hari DOUBLE NOT NULL,
    stok INT NOT NULL,
    jenis VARCHAR(20) NOT NULL, -- Pembeda: 'Joran' atau 'Reel'
    kekuatan VARCHAR(50) NULL,   -- Atribut khusus Joran
    jumlah_bearing INT NULL      -- Atribut khusus Reel
);

-- 3. Buat Tabel Transaksi
CREATE TABLE IF NOT EXISTS transaksi (
    id_transaksi VARCHAR(50) PRIMARY KEY,
    id_alat VARCHAR(20) NOT NULL,
    lama_sewa INT NOT NULL,
    total_harga DOUBLE NOT NULL,
    tanggal_transaksi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_alat) REFERENCES alat_mancing(id_alat) ON DELETE CASCADE
);

-- 4. Seed Data Awal
INSERT INTO alat_mancing (id_alat, nama, harga_sewa_per_hari, stok, jenis, kekuatan, jumlah_bearing) VALUES
('JRN-01', 'Shimano Lesath', 50000, 5, 'Joran', '15-30 lbs', NULL),
('REL-01', 'Daiwa Saltiga', 75000, 3, 'Reel', NULL, 12);
```
*Pembuatan database dengan Laragon dan phpmyadmin*

---

## 9. Output dan Pengujian

Pengujian dilakukan pada mode JDBC (`dao.mode=jdbc`), kemudian pada mode ORM (`dao.mode=orm`).

| No | Pengujian | Hasil yang diharapkan |
|----|-----------|-----------------------|
| 1 | Menu 1 dan 6 tepat setelah program dijalankan | Data awal dari file SQL langsung tampil 
| 2 | Tambah Joran `J-002` dan Reel `R-002` | Data tersimpan di database 
| 3 | Update stok `J-001` (+5, dengan catatan) | Stok menjadi 20 dan catatan tersimpan 
| 4 | Hapus alat `R-002` | Baris hilang dari database 
| 5 | Transaksi Joran 2 hari (ID alat huruf kecil `j-001`) | Total Rp50.000, stok berkurang 
| 6 | Transaksi Reel 3 hari | Total Rp135.000 (diskon 10%), stok berkurang 
| 7 | ID alat duplikat, tambahan stok 0, ID alat tidak ada | Pesan error, database tidak berubah 
| 8 | ID transaksi duplikat, lama sewa 0, stok kosong | Pesan error, database tidak berubah 
| 9 | Input huruf pada menu dan input angka | Input diulang, program tidak crash 
| 10 | Hapus alat yang sudah punya transaksi | Riwayat transaksi tetap ada (`id_alat` menjadi NULL) 
| 11 | Tutup lalu jalankan program lagi | Data tetap tersimpan 
| 12 | Mode ORM: tampil, tambah, update stok, transaksi | Berfungsi sama dengan mode JDBC 

<img width="471" height="575" alt="image" src="https://github.com/user-attachments/assets/0f8b792b-afa5-4956-ad16-a6b94c5f0897" />

<img width="974" height="476" alt="image" src="https://github.com/user-attachments/assets/4fd40869-d284-401e-8751-55bddd3163c2" />

*Data awal dari file SQL langsung tampil (menu 1 dan 6) dan isi tabel di database*

<img width="255" height="426" alt="image" src="https://github.com/user-attachments/assets/5ac030f3-e45a-4624-b8f2-6c82fd2dd241" />

<img width="240" height="157" alt="image" src="https://github.com/user-attachments/assets/97af8cfd-a879-4821-bf23-29390cd11bc2" />

*Tambah, update stok, dan hapus alat beserta hasilnya di database*

<img width="284" height="208" alt="image" src="https://github.com/user-attachments/assets/8c7e0539-af53-4ff6-a2f4-b2a361a8b43c" />

<img width="276" height="210" alt="image" src="https://github.com/user-attachments/assets/9191398b-8d2f-4254-a387-821b5cfff403" />

<img width="938" height="461" alt="image" src="https://github.com/user-attachments/assets/20ecb68a-8a3f-4feb-b03c-462a045502ec" />

*Transaksi sewa Joran dan Reel (diskon), stok berkurang, dan hasil di database*

<img width="251" height="158" alt="image" src="https://github.com/user-attachments/assets/510a2490-ae58-4dee-8ee4-9acc84a3dcab" />

*Validasi data dan validasi input*

<img width="471" height="628" alt="image" src="https://github.com/user-attachments/assets/f3c0e5a0-158c-4de6-9e88-c0b9688be2ce" />

*Riwayat transaksi tetap ada walau alat dihapus, dan data tetap tersimpan*

<img width="415" height="302" alt="image" src="https://github.com/user-attachments/assets/170a1bcd-ce40-4543-a1cf-3dace88c9ba3" />

*Pengujian mode ORM (Hibernate) beserta SQL yang dibuat Hibernate*

---

## 10. Penerapan Nilai Tambah

| No | Nilai Tambah | Letak Penerapan |
|----|--------------|-----------------|
| 1 | Data awal lewat file `.sql`, bukan kode | `database/db_penyewaan_mancing.sql` 
| 2 | Arsitektur MVC + DAO | Package `Model`, `View`, `Controller`, `DAO` 
| 3 | Dua implementasi DAO yang bisa dipertukarkan (JDBC dan ORM) | `DAO/*Jdbc.java`, `DAO/*Hibernate.java`, `Main.java` 
| 4 | Transaksi database atomik (commit / rollback) | `TransaksiDAOJdbc.simpan()` 
| 5 | Stok berkurang otomatis saat disewa | `SewaController.catatTransaksi()` 
| 6 | Riwayat transaksi tetap utuh saat alat dihapus (`ON DELETE SET NULL`) | `db_penyewaan_mancing.sql` 
| 7 | Konfigurasi database terpisah dari kode | `db.properties`, `Koneksi.java` 
| 8 | Kombinasi Interface + Abstract Class | `Disewakan.java`, `AlatMancing.java` 
| 9 | Method Overloading | `AlatMancing.tambahStok(...)` 
| 10 | Diskon otomatis 10% untuk Reel ≥ 3 hari | `Reel.hitungTotalSewa()` 
| 11 | Validasi input angka | `SewaView.inputInt()`, `inputDouble()` 
| 12 | Validasi data (ID duplikat, ID tidak ditemukan, stok kosong, lama sewa < 1, tambahan stok ≤ 0) | `SewaController`, `AlatMancing` 
| 13 | Pencarian ID tidak case-sensitive | `getById()` pada DAO 
| 14 | Catatan penambahan stok tersimpan di database | `AlatMancing.catatanTerakhir` 

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
