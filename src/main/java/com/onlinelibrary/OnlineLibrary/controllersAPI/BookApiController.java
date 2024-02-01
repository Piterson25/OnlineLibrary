package com.onlinelibrary.OnlineLibrary.controllersAPI;

import com.onlinelibrary.OnlineLibrary.domain.Book;
import com.onlinelibrary.OnlineLibrary.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookApiController {

    private final BookService bookService;

    @Autowired
    public BookApiController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok().body(bookService.getAllBooks());
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchBooks(@RequestParam("query") String query,
                                         @RequestParam(value = "sortBy", required = false) String sortBy,
                                         @RequestParam(value = "sortOrder", required = false) String sortOrder) {
        try {
            List<Book> foundBooks = bookService.searchBooks(query);

            if (sortBy != null && sortOrder != null) {
                foundBooks = bookService.sortBooks(foundBooks, sortBy, sortOrder);
            }

            return new ResponseEntity<>(foundBooks, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        try {
            Book book = bookService.getBookById(id);
            if (book != null) {
                return new ResponseEntity<>(book, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Couldn't find book with given id: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<String> addBook(@RequestBody @Valid Book book) {
        try {
            bookService.addBook(book);
            System.out.println("New book: " + book);
            return new ResponseEntity<>("Book added successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> editBook(@PathVariable Long id, @RequestBody @Valid Book updatedBook) {
        try {
            Book book = bookService.getBookById(id);
            if (book != null) {
                book.setTitle(updatedBook.getTitle());
                book.setImageUrl(updatedBook.getImageUrl());
                book.setDescription(updatedBook.getDescription());
                book.setOriginalDate(updatedBook.getOriginalDate());
                book.setGenres(updatedBook.getGenres());
                book.setAuthors(updatedBook.getAuthors());
                book.setPublisher(updatedBook.getPublisher());
                book.setPublishYear(updatedBook.getPublishYear());
                bookService.addBook(book);
                System.out.println("Updated book: " + book);
                return new ResponseEntity<>("Book added successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Couldn't find book with given id: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        try {
            Book book = bookService.getBookById(id);
            if (book != null) {
                System.out.println("Deleted book: " + book);
                bookService.deleteBookById(id);
                return new ResponseEntity<>("Book deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Couldn't find book with given id: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public HashMap<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        HashMap<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMsg = error.getDefaultMessage();
            errors.put(fieldName, errorMsg);
        });
        return errors;
    }
}
