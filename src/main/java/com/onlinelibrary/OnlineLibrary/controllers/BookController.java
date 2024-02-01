package com.onlinelibrary.OnlineLibrary.controllers;

import com.onlinelibrary.OnlineLibrary.domain.User;
import com.onlinelibrary.OnlineLibrary.service.BookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.onlinelibrary.OnlineLibrary.domain.Book;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Controller
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String getAllBooks(Model model) {
        List<Book> topBooks = bookService.getTopRatedBooks(3);
        model.addAttribute("topBooks", topBooks);
        model.addAttribute("books", bookService.getAllBooks());
        return "home";
    }

    @GetMapping("/search")
    public String searchBooks(
            @RequestParam("query") String query,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortOrder", required = false) String sortOrder,
            Model model
    ) {
        List<Book> foundBooks = bookService.searchBooks(query);

        if (sortBy != null && sortOrder != null) {
            foundBooks = bookService.sortBooks(foundBooks, sortBy, sortOrder);
        }

        model.addAttribute("foundBooks", foundBooks);
        return "search";
    }

    @GetMapping("/books/{id}")
    public String getBookById(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "book";
    }

    @GetMapping("/books/add")
    public String addBook() {
        return "addBook";
    }

    @PostMapping("/books/add")
    public String addBook(@RequestParam String title,
                          @RequestParam String imageUrl,
                          @RequestParam String description,
                          @RequestParam LocalDate originalDate,
                          @RequestParam List<String> genres,
                          @RequestParam List<String> authors,
                          @RequestParam String publisher,
                          @RequestParam Integer publishYear) {
        Book newBook = new Book(title, imageUrl, description, originalDate.toString(), genres, authors, publisher, publishYear, new ArrayList<>());
        bookService.addBook(newBook);

        return "redirect:/panel/books";
    }

    @GetMapping("/books/edit/{id}")
    public String editBook(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "editBook";
    }

    @PostMapping("/books/edit/{id}")
    public String editBook(@PathVariable Long id,
                           @RequestParam String title,
                           @RequestParam String imageUrl,
                           @RequestParam String description,
                           @RequestParam LocalDate originalDate,
                           @RequestParam List<String> genres,
                           @RequestParam List<String> authors,
                           @RequestParam String publisher,
                           @RequestParam Integer publishYear) {
        Book book = bookService.getBookById(id);
        book.setTitle(title);
        book.setImageUrl(imageUrl);
        book.setDescription(description);
        book.setOriginalDate(originalDate.toString());
        book.setGenres(genres);
        book.setAuthors(authors);
        book.setPublisher(publisher);
        book.setPublishYear(publishYear);
        bookService.addBook(book);
        return "redirect:/panel/books";
    }

    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        Book book = bookService.getBookById(id);
        if (book != null) {
            bookService.deleteBookById(id);
        }
        return "redirect:/panel/books";
    }

    @PostMapping("/rateBook/{bookId}")
    public String rateBook(@PathVariable("bookId") Long bookId, @RequestParam("rating") int rating,
                           HttpSession session) {
        User user = (User) session.getAttribute("user");
        Long userId = null;
        if (user != null) {
            userId = user.getId();
        }

        LocalDateTime lastRatingTime = bookService.getLastRatingTimeForBookByUser(bookId, userId);

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(lastRatingTime, now);
        long secondsSinceLastRating = duration.getSeconds();

        if (secondsSinceLastRating < 10) {
            return "redirect:/books/{bookId}";
        }

        bookService.rateBook(bookId, userId, rating);

        return "redirect:/books/{bookId}";
    }
}
