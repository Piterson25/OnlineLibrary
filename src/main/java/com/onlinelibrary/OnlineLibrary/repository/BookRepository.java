package com.onlinelibrary.OnlineLibrary.repository;

import com.onlinelibrary.OnlineLibrary.domain.Book;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b LEFT JOIN b.ratings r GROUP BY b.id, b.description, b.imageUrl, b.originalDate, b.ownerId, b.publishYear, b.publisher, b.title ORDER BY AVG(r.rating) DESC, b.title ASC")
    List<Book> findTopRatedBooks();

    @Query("SELECT AVG(r.rating) FROM Book b JOIN b.ratings r")
    Double findAverageRating();

    @Query("SELECT a, COUNT(b) FROM Book b JOIN b.authors a GROUP BY a ORDER BY COUNT(b) DESC")
    List<Tuple> findTopAuthors();

    @Query("SELECT g, COUNT(b) FROM Book b JOIN b.genres g GROUP BY g ORDER BY COUNT(b) DESC")
    List<Tuple> findTopGenres();

    @Query("SELECT b FROM Book b WHERE b.originalDate = (SELECT MIN(b2.originalDate) FROM Book b2)")
    Book findOldestBook();

    @Query("SELECT b FROM Book b WHERE b.originalDate = (SELECT MAX(b2.originalDate) FROM Book b2)")
    Book findNewestBook();
}
