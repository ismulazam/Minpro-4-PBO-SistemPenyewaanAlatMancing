package DAO;

import Model.AlatMancing;
import java.util.List;

/**
 * Kontrak akses data alat mancing. Implementasinya bisa JDBC atau ORM.
 */
public interface AlatDAO {
    List<AlatMancing> getAll();
    AlatMancing getById(String idAlat);      // tidak case-sensitive, null jika tidak ada
    boolean insert(AlatMancing alat);
    boolean updateStok(AlatMancing alat);    // menyimpan stok dan catatan terakhir
    boolean delete(String idAlat);
}
