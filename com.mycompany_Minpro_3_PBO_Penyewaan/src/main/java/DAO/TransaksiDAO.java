package DAO;

import Model.AlatMancing;
import Model.Transaksi;
import java.util.List;

/**
 * Kontrak akses data transaksi sewa.
 */
public interface TransaksiDAO {
    List<Transaksi> getAll();
    boolean exists(String idTransaksi);

    /**
     * Menyimpan transaksi dan memperbarui stok alat dalam SATU transaksi database.
     * Jika salah satu gagal, keduanya dibatalkan (rollback).
     */
    boolean simpan(Transaksi transaksi, AlatMancing alat);
}
