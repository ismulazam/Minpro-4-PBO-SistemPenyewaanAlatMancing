package Main;

/**
 *
 * @author mismu
 */
import Config.HibernateUtil;
import Config.Koneksi;
import Controller.SewaController;
import DAO.AlatDAO;
import DAO.AlatDAOHibernate;
import DAO.AlatDAOJdbc;
import DAO.TransaksiDAO;
import DAO.TransaksiDAOHibernate;
import DAO.TransaksiDAOJdbc;
import View.SewaView;

public class Main {
    public static void main(String[] args) {  
        String mode = args.length > 0 ? args[0] : Koneksi.getProperty("dao.mode", "jdbc");
        boolean pakaiOrm = mode.equalsIgnoreCase("orm");

        AlatDAO alatDAO;
        TransaksiDAO transaksiDAO;
        if (pakaiOrm) {
            alatDAO = new AlatDAOHibernate();
            transaksiDAO = new TransaksiDAOHibernate();
        } else {
            alatDAO = new AlatDAOJdbc();
            transaksiDAO = new TransaksiDAOJdbc();
        }

        SewaView view = new SewaView();
        view.tampilkanPesan("Mode akses data: " + (pakaiOrm ? "ORM (Hibernate)" : "JDBC (PreparedStatement)"));

        SewaController controller = new SewaController(view, alatDAO, transaksiDAO);
        controller.mulai();

        if (pakaiOrm) {
            HibernateUtil.shutdown();
        }
    }
}
