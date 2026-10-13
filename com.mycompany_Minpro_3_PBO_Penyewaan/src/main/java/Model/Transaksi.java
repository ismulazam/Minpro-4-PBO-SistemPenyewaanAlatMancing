package Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 *
 * @author mismu
 */
@Entity
@Table(name = "transaksi")
public class Transaksi {
    @Id
    @Column(name = "id_transaksi")
    private String idTransaksi;

    @Column(name = "nama_penyewa")
    private String namaPenyewa;

    @Column(name = "id_alat")
    private String idAlat;

    @Column(name = "nama_alat")
    private String namaAlat;

    @Column(name = "lama_sewa")
    private int lamaSewaHari;

    @Column(name = "total_biaya")
    private double totalBiaya;

    protected Transaksi() { }

    public Transaksi(String idTransaksi, String namaPenyewa, String idAlat, String namaAlat,
                     int lamaSewaHari, double totalBiaya) {
        this.idTransaksi = idTransaksi;
        this.namaPenyewa = namaPenyewa;
        this.idAlat = idAlat;
        this.namaAlat = namaAlat;
        this.lamaSewaHari = lamaSewaHari;
        this.totalBiaya = totalBiaya;
    }

    public String getIdTransaksi() { return idTransaksi; }
    public String getNamaPenyewa() { return namaPenyewa; }
    public String getIdAlat() { return idAlat; }
    public String getNamaAlat() { return namaAlat; }
    public int getLamaSewaHari() { return lamaSewaHari; }
    public double getTotalBiaya() { return totalBiaya; }

    public String getInfo() {
        return String.format("%-10s | %-15s | %-15s | %-10d | Rp%.0f",
                idTransaksi, namaPenyewa, namaAlat, lamaSewaHari, totalBiaya);
    }
}
