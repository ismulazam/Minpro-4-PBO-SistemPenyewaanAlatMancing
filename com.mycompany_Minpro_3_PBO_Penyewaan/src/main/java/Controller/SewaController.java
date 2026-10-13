package Controller;

/**
 *
 * @author mismu
 */
import DAO.AlatDAO;
import DAO.TransaksiDAO;
import Model.AlatMancing;
import Model.Joran;
import Model.Reel;
import Model.Transaksi;
import View.SewaView;

public class SewaController {
    private final AlatDAO alatDAO;
    private final TransaksiDAO transaksiDAO;
    private final SewaView view;

    public SewaController(SewaView view, AlatDAO alatDAO, TransaksiDAO transaksiDAO) {
        this.view = view;
        this.alatDAO = alatDAO;
        this.transaksiDAO = transaksiDAO;
    }

    public void mulai() {
        boolean berjalan = true;
        while (berjalan) {
            int pilihan = view.tampilkanMenuUtama();
            switch (pilihan) {
                case 1: view.tampilkanDaftarAlat(alatDAO.getAll()); break;
                case 2: tambahData(); break;
                case 3: updateStok(); break;
                case 4: hapusAlat(); break;
                case 5: catatTransaksi(); break;
                case 6: view.tampilkanDaftarTransaksi(transaksiDAO.getAll()); break;
                case 7:
                    berjalan = false;
                    view.tampilkanPesan("Sampai jumpa!");
                    break;
                default:
                    view.tampilkanPesan("ERROR: Pilihan menu tidak valid!");
            }
        }
    }

    private void tambahData() {
        view.tampilkanPesan("--- TAMBAH ALAT BARU ---");
        int jenis = view.inputInt("Pilih Jenis (1. Joran / 2. Reel): ");
        if (jenis != 1 && jenis != 2) {
            view.tampilkanPesan("ERROR: Pilihan jenis tidak dikenali.");
            return;
        }

        String id = view.inputString("Masukkan ID Alat    : ");
        if (alatDAO.getById(id) != null) {
            view.tampilkanPesan("ERROR: ID sudah dipakai!");
            return;
        }
        String nama = view.inputString("Masukkan Nama Alat  : ");
        int stok = view.inputInt("Masukkan Stok       : ");
        double harga = view.inputDouble("Harga Sewa/Hari     : ");

        AlatMancing alatBaru;
        if (jenis == 1) {
            String kekuatan = view.inputString("Masukkan Kekuatan   : ");
            alatBaru = new Joran(id, nama, stok, harga, kekuatan);
        } else {
            int bearing = view.inputInt("Jumlah Bearing      : ");
            alatBaru = new Reel(id, nama, stok, harga, bearing);
        }

        if (alatDAO.insert(alatBaru)) {
            view.tampilkanPesan("SUCCESS: Data berhasil ditambahkan!");
        } else {
            view.tampilkanPesan("ERROR: Data gagal disimpan ke database!");
        }
    }

    private void updateStok() {
        AlatMancing alat = alatDAO.getById(view.inputString("Masukkan ID Alat yang akan diupdate: "));
        if (alat == null) {
            view.tampilkanPesan("ERROR: ID Alat tidak ditemukan!");
            return;
        }
        int tambah = view.inputInt("Masukkan jumlah tambahan stok: ");
        String catatan = view.inputString("Ketik catatan penambahan: ");
        if (!alat.tambahStok(tambah, catatan)) {
            view.tampilkanPesan("ERROR: Jumlah tambahan harus lebih dari 0!");
            return;
        }
        if (alatDAO.updateStok(alat)) {
            view.tampilkanPesan("SUCCESS: Stok diupdate. Catatan: " + alat.getCatatanTerakhir());
        } else {
            view.tampilkanPesan("ERROR: Stok gagal disimpan ke database!");
        }
    }

    private void hapusAlat() {
        AlatMancing alat = alatDAO.getById(view.inputString("Masukkan ID Alat yang dihapus: "));
        if (alat == null) {
            view.tampilkanPesan("ERROR: ID Alat tidak ditemukan!");
            return;
        }
        if (alatDAO.delete(alat.getIdAlat())) {
            view.tampilkanPesan("SUCCESS: Alat berhasil dihapus!");
        } else {
            view.tampilkanPesan("ERROR: Alat gagal dihapus dari database!");
        }
    }

    private void catatTransaksi() {
        view.tampilkanPesan("--- CATAT TRANSAKSI ---");
        String idTrans = view.inputString("Masukkan ID Transaksi : ");
        if (transaksiDAO.exists(idTrans)) {
            view.tampilkanPesan("ERROR: ID Transaksi sudah dipakai!");
            return;
        }
        String namaPenyewa = view.inputString("Masukkan Nama Penyewa : ");
        AlatMancing alat = alatDAO.getById(view.inputString("Masukkan ID Alat Sewa : "));

        if (alat == null) {
            view.tampilkanPesan("ERROR: ID Alat tidak ditemukan!");
            return;
        }
        if (alat.getStok() <= 0) {
            view.tampilkanPesan("ERROR: Stok alat sedang kosong!");
            return;
        }
        int lama = view.inputInt("Masukkan Lama Sewa (hari): ");
        if (lama < 1) {
            view.tampilkanPesan("ERROR: Lama sewa minimal 1 hari!");
            return;
        }

        double total = alat.hitungTotalSewa(lama);   // polymorphism: Joran / Reel
        alat.sewa();                                 // stok berkurang 1
        Transaksi transaksi = new Transaksi(idTrans, namaPenyewa,
                alat.getIdAlat(), alat.getNamaAlat(), lama, total);

        if (transaksiDAO.simpan(transaksi, alat)) {
            view.tampilkanPesan("SUCCESS: Transaksi dicatat. Total Biaya: Rp" + total);
        } else {
            view.tampilkanPesan("ERROR: Transaksi gagal disimpan ke database!");
        }
    }
}
