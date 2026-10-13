package DAO;

import Config.HibernateUtil;
import Model.AlatMancing;
import Model.Transaksi;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Implementasi TransaksiDAO memakai Hibernate (ORM).
 */
public class TransaksiDAOHibernate implements TransaksiDAO {

    @Override
    public List<Transaksi> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Transaksi order by idTransaksi", Transaksi.class).list();
        } catch (RuntimeException e) {
            System.err.println("ORM ERROR (getAll transaksi): " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public boolean exists(String idTransaksi) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long jumlah = session.createQuery(
                    "select count(t) from Transaksi t where lower(t.idTransaksi) = lower(:id)", Long.class)
                    .setParameter("id", idTransaksi)
                    .uniqueResult();
            return jumlah != null && jumlah > 0;
        } catch (RuntimeException e) {
            System.err.println("ORM ERROR (exists transaksi): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean simpan(Transaksi transaksi, AlatMancing alat) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(transaksi);   // INSERT transaksi
            session.merge(alat);          // UPDATE stok alat
            tx.commit();                  // keduanya disimpan bersama
            return true;
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            System.err.println("ORM ERROR (simpan transaksi): " + e.getMessage());
            return false;
        }
    }
}
