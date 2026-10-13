package Model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 *
 * @author mismu
 */
@Entity
@DiscriminatorValue("Reel")
public class Reel extends AlatMancing {
    @Column(name = "jumlah_bearing")
    private int jumlahBearing;

    protected Reel() { }

    public Reel(String idAlat, String namaAlat, int stok, double hargaSewa, int jumlahBearing) {
        super(idAlat, namaAlat, stok, hargaSewa);
        this.jumlahBearing = jumlahBearing > 0 ? jumlahBearing : 1;
    }

    public int getJumlahBearing() { return jumlahBearing; }

    // POLYMORPHISM: Overriding
    @Override
    public String getInfo() {
        return String.format("%-6s | %-15s | %-8s | Stok: %-3d | Sewa: Rp%-7.0f | Bearing: %d",
                getIdAlat(), getNamaAlat(), "Reel", getStok(), getHargaSewaPerHari(), jumlahBearing);
    }

    @Override
    public double hitungTotalSewa(int lamaHari) {
        double total = getHargaSewaPerHari() * lamaHari;
        if (lamaHari >= 3) {
            total = total - (total * 0.1); // diskon 10% untuk sewa 3 hari atau lebih
        }
        return total;
    }
}
