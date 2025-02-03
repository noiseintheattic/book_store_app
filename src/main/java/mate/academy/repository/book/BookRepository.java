package mate.academy.repository.book;

import java.util.List;
import mate.academy.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @Query("FROM Book b LEFT JOIN FETCH b.booksCategories bc WHERE bc.id = :categoryId")
    List<Book> findAllByCategoryId(@Param("categoryId")Long categoryId);
}
