package com.onlinelibrary.OnlineLibrary.service;

import com.onlinelibrary.OnlineLibrary.domain.Book;
import com.onlinelibrary.OnlineLibrary.domain.Rating;
import com.onlinelibrary.OnlineLibrary.repository.BookRepository;
import jakarta.persistence.Tuple;
import jakarta.validation.Valid;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        Map<String, Book> bookMap = context.getBeansOfType(Book.class);
        bookMap.values().forEach(this::addBook);
    }

    public List<Book> getAllBooks() {
        return this.repository.findAll();
    }

    public Book getBookById(Long id) {
        return this.repository.findById(id).orElse(null);
    }

    public void addBook(@Valid Book book) {
        this.repository.save(book);
    }

    public void deleteBookById(Long id) {
        this.repository.deleteById(id);
    }

    public List<Book> getTopRatedBooks(int numberOfBooks) {
        return this.repository.findAll().stream()
                .sorted(Comparator.comparingDouble(Book::getAverageRating).reversed())
                .limit(numberOfBooks)
                .collect(Collectors.toList());
    }

    public List<Book> searchBooks(String query) {
        List<Book> allBooks = getAllBooks();

        return allBooks.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(query.toLowerCase())
                        || book.getAuthors().stream().anyMatch(author -> author.toLowerCase().contains(query.toLowerCase()))
                        || book.getGenres().stream().anyMatch(genre -> genre.toLowerCase().contains(query.toLowerCase())))
                .collect(Collectors.toList());
    }

    public List<Book> sortBooks(List<Book> books, String sortBy, String sortOrder) {
        Comparator<Book> comparator = switch (sortBy) {
            case "publisher" -> Comparator.comparing(Book::getPublisher);
            case "publishYear" -> Comparator.comparing(Book::getPublishYear);
            case "averageRating" -> Comparator.comparing(Book::getAverageRating);
            case "numberOfRatings" -> Comparator.comparing(Book::getNumberOfRatings);
            default -> Comparator.comparing(Book::getTitle);
        };

        if ("DESC".equals(sortOrder)) {
            comparator = comparator.reversed();
        }

        return books.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public LocalDateTime getLastRatingTimeForBookByUser(Long bookId, Long userId) {
        if (userId == null) {
            return LocalDateTime.now().minusDays(1);
        }

        Book book = getBookById(bookId);
        if (book != null) {
            List<Rating> userRatings = book.getRatings().stream()
                    .filter(rating -> userId.equals(rating.getAuthorId()))
                    .toList();

            if (!userRatings.isEmpty()) {
                Rating lastRating = userRatings.get(userRatings.size() - 1);
                return lastRating.getDate();
            }
        }

        return LocalDateTime.now().minusDays(1);
    }

    public void rateBook(Long bookId, Long userId, int rating) {
        Book book = getBookById(bookId);
        if (book != null) {
            book.getRatings().add(new Rating(userId, LocalDateTime.now().toString(), rating));
            this.repository.save(book);
        }
    }
}
