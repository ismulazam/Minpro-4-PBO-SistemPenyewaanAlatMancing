package DAO;

import Config.Koneksi;
import Model.AlatMancing;
import Model.Joran;
import Model.Reel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementasi AlatDAO memakai JDBC + PreparedStatement.
 */
public class AlatDAOJdbc implements AlatDAO {

    private static final String SQL_SELECT_ALL =
            "SELECT id_alat, nama_alat, jenis, stok, harga_sewa_per_hari, catatan_terakhir, "
          + "kekuatan, jumlah_bearing FROM alat_mancing ORDER BY id_alat";
    private static final String SQL_SELECT_BY_ID =
            "SELECT id_alat, nama_alat, jenis, stok, harga_sewa_per_hari, catatan_terakhir, "
          + "kekuatan, jumlah_bearing FROM alat_mancing WHERE LOWER(id_alat) = LOWER(?)";
    private static final String SQL_INSERT =
            "INSERT INTO alat_mancing (id_alat, nama_alat, jenis, stok, harga_sewa_per_hari, "
          + "catatan_terakhir, kekuatan, jumlah_bearing) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE_STOK =
            "UPDATE alat_mancing SET stok = ?, catatan_terakhir = ? WHERE id_alat = ?";
    private static final String SQL_DELETE =
            "DELETE FROM alat_mancing WHERE id_alat = ?";

    @Override
    public List<AlatMancing> getAll() {
        List<AlatMancing> hasil = new ArrayList<>();
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                hasil.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("DB ERROR (getAll alat): " + e.getMessage());
        }
        return hasil;
    }

    @Override
    public AlatMancing getById(String idAlat) {
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setString(1, idAlat);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("DB ERROR (getById alat): " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean insert(AlatMancing alat) {
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setString(1, alat.getIdAlat());
            ps.setString(2, alat.getNamaAlat());
            ps.setInt(4, alat.getStok());
            ps.setDouble(5, alat.getHargaSewaPerHari());
            ps.setString(6, alat.getCatatanTerakhir());

            if (alat instanceof Joran joran) {
                ps.setString(3, "Joran");
                ps.setString(7, joran.getKekuatan());
                ps.setNull(8, Types.INTEGER);
            } else if (alat instanceof Reel reel) {
                ps.setString(3, "Reel");
                ps.setNull(7, Types.VARCHAR);
                ps.setInt(8, reel.getJumlahBearing());
            } else {
                return false;
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB ERROR (insert alat): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateStok(AlatMancing alat) {
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_STOK)) {
            ps.setInt(1, alat.getStok());
            ps.setString(2, alat.getCatatanTerakhir());
            ps.setString(3, alat.getIdAlat());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB ERROR (update stok): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(String idAlat) {
        try (Connection conn = Koneksi.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setString(1, idAlat);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("DB ERROR (delete alat): " + e.getMessage());
            return false;
        }
    }

    /** Mengubah satu baris tabel menjadi objek Joran atau Reel sesuai kolom 'jenis'. */
    private AlatMancing mapRow(ResultSet rs) throws SQLException {
        String id = rs.getString("id_alat");
        String nama = rs.getString("nama_alat");
        int stok = rs.getInt("stok");
        double harga = rs.getDouble("harga_sewa_per_hari");

        AlatMancing alat;
        if ("Joran".equalsIgnoreCase(rs.getString("jenis"))) {
            alat = new Joran(id, nama, stok, harga, rs.getString("kekuatan"));
        } else {
            alat = new Reel(id, nama, stok, harga, rs.getInt("jumlah_bearing"));
        }
        alat.setCatatanTerakhir(rs.getString("catatan_terakhir"));
        return alat;
    }
}
