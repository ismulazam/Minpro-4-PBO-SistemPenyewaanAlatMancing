package Model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

/**
 *
 * @author mismu
 */
@Entity
@Table(name = "alat_mancing")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "jenis", discriminatorType = DiscriminatorType.STRING)
public abstract class AlatMancing implements Disewakan {
    @Id
    @Column(name = "id_alat")
    private String idAlat;

    @Column(name = "nama_alat")
    private String namaAlat;

    @Column(name = "harga_sewa_per_hari")
    private double hargaSewaPerHari;

    @Column(name = "stok")
    private int stok;

    @Column(name = "catatan_terakhir")
    private String catatanTerakhir = "";

    // Dibutuhkan Hibernate untuk membuat objek dari hasil query
    protected AlatMancing() { }

    public AlatMancing(String idAlat, String namaAlat, int stok, double hargaSewaPerHari) {
        this.idAlat = idAlat;
        this.namaAlat = (namaAlat == null || namaAlat.trim().isEmpty()) ? "Tanpa Nama" : namaAlat.trim();
        this.stok = Math.max(stok, 0);
        this.hargaSewaPerHari = Math.max(hargaSewaPerHari, 0);
    }

    public String getIdAlat() { return idAlat; }
    public String getNamaAlat() { return namaAlat; }
    public double getHargaSewaPerHari() { return hargaSewaPerHari; }
    public int getStok() { return stok; }
    public String getCatatanTerakhir() { return catatanTerakhir; }

    // Dipakai DAO JDBC saat membentuk objek dari baris database
    public void setCatatanTerakhir(String catatan) {
        this.catatanTerakhir = (catatan == null) ? "" : catatan;
    }

    public abstract String getInfo();

    @Override
    public boolean sewa() {
        if (stok > 0) {
            stok--;
            return true;
        }
        return false;
    }

    public boolean tambahStok(int tambahan) {
        if (tambahan <= 0) {
            return false;
        }
        stok += tambahan;
        return true;
    }

    public boolean tambahStok(int tambahan, String catatan) {
        if (!tambahStok(tambahan)) {
            return false;
        }
        catatanTerakhir = catatan;
        return true;
    }
}
