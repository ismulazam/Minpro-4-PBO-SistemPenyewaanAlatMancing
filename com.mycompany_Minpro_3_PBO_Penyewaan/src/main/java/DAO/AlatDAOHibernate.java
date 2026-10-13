package DAO;

import Config.HibernateUtil;
import Model.AlatMancing;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 * Implementasi AlatDAO memakai Hibernate (ORM). Tidak ada SQL manual:
 * Hibernate membuat SQL dari anotasi pada class Model.
 */
public class AlatDAOHibernate implements AlatDAO {

    @Override
    public List<AlatMancing> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from AlatMancing order by idAlat", AlatMancing.class).list();
        } catch (RuntimeException e) {
            System.err.println("ORM ERROR (getAll alat): " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public AlatMancing getById(String idAlat) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "from AlatMancing a where lower(a.idAlat) = lower(:id)", AlatMancing.class)
                    .setParameter("id", idAlat)
                    .uniqueResult();
        } catch (RuntimeException e) {
            System.err.println("ORM ERROR (getById alat): " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean insert(AlatMancing alat) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(alat);
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            rollback(tx);
            System.err.println("ORM ERROR (insert alat): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateStok(AlatMancing alat) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(alat);
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            rollback(tx);
            System.err.println("ORM ERROR (update stok): " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(String idAlat) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            AlatMancing alat = session.find(AlatMancing.class, idAlat);
            if (alat == null) {
                tx.rollback();
                return false;
            }
            session.remove(alat);
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            rollback(tx);
            System.err.println("ORM ERROR (delete alat): " + e.getMessage());
            return false;
        }
    }

    private void rollback(Transaction tx) {
        if (tx != null && tx.isActive()) {
            tx.rollback();
        }
    }
}
