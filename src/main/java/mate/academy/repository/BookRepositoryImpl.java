package mate.academy.repository;

import jakarta.persistence.criteria.CriteriaQuery;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.exceptions.DataProcessingException;
import mate.academy.exceptions.EntityNotFoundException;
import mate.academy.model.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class BookRepositoryImpl implements BookRepository {

    private final SessionFactory sessionFactory;

    @Override
    public Book add(Book book) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.persist(book);
            transaction.commit();
            return book;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't insert book " + book + ".", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public List<Book> findAll() {
        try (Session session = sessionFactory.openSession()) {
            CriteriaQuery<Book> criteriaQuery = session.getCriteriaBuilder()
                    .createQuery(Book.class);
            criteriaQuery.from(Book.class);
            return session.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            throw new EntityNotFoundException("Can't find all books.", e);
        }
    }

    @Override
    public Book getById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Book.class, id);
        } catch (Exception e) {
            throw new EntityNotFoundException("Can't get book by id: " + id, e);
        }
    }
}
