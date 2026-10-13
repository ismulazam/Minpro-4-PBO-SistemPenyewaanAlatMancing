package Model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 *
 * @author mismu
 */
@Entity
@DiscriminatorValue("Joran")
public class Joran extends AlatMancing {
    @Column(name = "kekuatan")
    private String kekuatan;

    protected Joran() { }

    public Joran(String idAlat, String namaAlat, int stok, double hargaSewa, String kekuatan) {
        super(idAlat, namaAlat, stok, hargaSewa);
        this.kekuatan = kekuatan;
    }

    public String getKekuatan() { return kekuatan; }

    @Override
    public String getInfo() {
        return String.format("%-6s | %-15s | %-8s | Stok: %-3d | Sewa: Rp%-7.0f | Kekuatan: %s",
                getIdAlat(), getNamaAlat(), "Joran", getStok(), getHargaSewaPerHari(), kekuatan);
    }

    @Override
    public double hitungTotalSewa(int lamaHari) {
        return getHargaSewaPerHari() * lamaHari;
    }
}
