package Model;

/**
 *
 * @author mismu
 */
public interface Disewakan {
    double hitungTotalSewa(int lamaHari);
    boolean sewa(); // mengurangi stok 1, return false jika stok kosong
}
