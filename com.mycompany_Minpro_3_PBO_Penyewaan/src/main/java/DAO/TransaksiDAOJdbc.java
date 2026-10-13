package DAO;

import Config.Koneksi;
import Model.AlatMancing;
import Model.Transaksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi TransaksiDAO memakai JDBC + PreparedStatement.
 */
public class TransaksiDAOJdbc implements TransaksiDAO {

    private static final String SQL_SELECT_ALL =
            "SELECT id_transaksi, nama_penyewa, id_alat, nama_alat, lama_sewa, total_biaya "
          + "FROM transaksi ORDER BY id_transaksi";
    private static final String SQL_EXISTS =
            "SELECT 1 FROM transaksi WHERE LOWER(id_transaksi) = LOWER(?)";
    private static final String SQL_INSERT =
            "INSERT INTO transaksi (id_transaksi, nama_penyewa, id_alat, nama_alat, lama_sewa, total_biaya) "
          + "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_STOK =
            "UPDATE alat_mancing SET stok = ? WHERE id_alat = ?";

    @Override
    public List<Transaksi> getAll() {
        List<Transaksi> hasil = new ArrayList<>();
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(new Transaksi(
                        rs.getString("id_transaksi"),
                        rs.getString("nama_penyewa"),
                        rs.getString("id_alat"),
                        rs.getString("nama_alat"),
                        rs.getInt("lama_sewa"),
                        rs.getDouble("total_biaya")));
            }
        } catch (SQLException e) {
            System.err.println("DB ERROR (getAll transaksi): " + e.getMessage());
        }
        return hasil;
    }

    @Override
    public boolean exists(String idTransaksi) {
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS)) {
            ps.setString(1, idTransaksi);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("DB ERROR (exists transaksi): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean simpan(Transaksi t, AlatMancing alat) {
        try (Connection conn = Koneksi.getConnection()) {
            conn.setAutoCommit(false); // mulai transaksi database
            try (PreparedStatement psInsert = conn.prepareStatement(SQL_INSERT);
                 PreparedStatement psStok = conn.prepareStatement(SQL_UPDATE_STOK)) {

                psInsert.setString(1, t.getIdTransaksi());
                psInsert.setString(2, t.getNamaPenyewa());
                psInsert.setString(3, t.getIdAlat());
                psInsert.setString(4, t.getNamaAlat());
                psInsert.setInt(5, t.getLamaSewaHari());
                psInsert.setDouble(6, t.getTotalBiaya());
                psInsert.executeUpdate();

                psStok.setInt(1, alat.getStok());
                psStok.setString(2, alat.getIdAlat());
                psStok.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("DB ERROR (simpan transaksi): " + e.getMessage());
            return false;
        }
    }
}
