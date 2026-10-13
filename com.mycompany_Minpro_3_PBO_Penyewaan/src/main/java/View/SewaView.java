package View;

/**
 *
 * @author mismu
 */
import Model.AlatMancing;
import Model.Transaksi;
import java.util.List;
import java.util.Scanner;

public class SewaView {
    private final Scanner scanner = new Scanner(System.in);

    public int tampilkanMenuUtama() {
        System.out.println("\n==============================================");
        System.out.println(" SISTEM MANAJEMEN PENYEWAAN ALAT MANCING");
        System.out.println("==============================================");
        System.out.println("1. Tampilkan Data Alat Mancing");
        System.out.println("2. Tambah Alat Mancing Baru");
        System.out.println("3. Update Stok Alat Mancing");
        System.out.println("4. Hapus Alat Mancing");
        System.out.println("5. Catat Transaksi Sewa Baru");
        System.out.println("6. Tampilkan Riwayat Transaksi");
        System.out.println("7. Keluar");
        return inputInt("Pilih menu (1-7): ");
    }

    public void tampilkanDaftarAlat(List<AlatMancing> daftar) {
        System.out.println("\n--- DAFTAR ALAT MANCING ---");
        if (daftar.isEmpty()) {
            System.out.println("Data alat mancing kosong.");
        } else {
            for (AlatMancing alat : daftar) {
                System.out.println(alat.getInfo());
            }
        }
    }

    public void tampilkanDaftarTransaksi(List<Transaksi> daftar) {
        System.out.println("\n--- RIWAYAT TRANSAKSI ---");
        if (daftar.isEmpty()) {
            System.out.println("Belum ada data transaksi.");
        } else {
            System.out.printf("%-10s | %-15s | %-15s | %-10s | %s\n",
                    "ID Trans", "Nama Penyewa", "Alat Disewa", "Lama(Hari)", "Total Biaya");
            System.out.println("-----------------------------------------------------------------------------");
            for (Transaksi t : daftar) {
                System.out.println(t.getInfo());
            }
        }
    }

    public String inputString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int inputInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(">> ERROR INPUT: Harap masukkan angka bulat!");
            }
        }
    }

    public double inputDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println(">> ERROR INPUT: Harap masukkan angka yang valid!");
            }
        }
    }

    public void tampilkanPesan(String pesan) {
        System.out.println(">> " + pesan);
    }
}