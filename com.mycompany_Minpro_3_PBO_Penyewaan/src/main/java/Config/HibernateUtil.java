package Config;

import Model.AlatMancing;
import Model.Joran;
import Model.Reel;
import Model.Transaksi;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Membuat SessionFactory Hibernate (framework ORM).
 * Pengaturan koneksi diambil dari db.properties yang sama dengan JDBC.
 */
public class HibernateUtil {
    private static SessionFactory sessionFactory;

    private HibernateUtil() { }

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            Configuration cfg = new Configuration();
            cfg.setProperty("jakarta.persistence.jdbc.url", Koneksi.getProperty("db.url", ""));
            cfg.setProperty("jakarta.persistence.jdbc.user", Koneksi.getProperty("db.user", ""));
            cfg.setProperty("jakarta.persistence.jdbc.password", Koneksi.getProperty("db.password", ""));
            cfg.setProperty("jakarta.persistence.jdbc.driver",
                    Koneksi.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"));
            cfg.setProperty("hibernate.show_sql", Koneksi.getProperty("orm.show_sql", "false"));
            cfg.setProperty("hibernate.hbm2ddl.auto", "none"); // tabel dibuat lewat file .sql

            cfg.addAnnotatedClass(AlatMancing.class);
            cfg.addAnnotatedClass(Joran.class);
            cfg.addAnnotatedClass(Reel.class);
            cfg.addAnnotatedClass(Transaksi.class);

            sessionFactory = cfg.buildSessionFactory();
        }
        return sessionFactory;
    }

    public static synchronized void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            sessionFactory = null;
        }
    }
}
