package com.onlinelibrary.OnlineLibrary.controllers;

import com.onlinelibrary.OnlineLibrary.domain.Book;
import com.onlinelibrary.OnlineLibrary.domain.Comment;
import com.onlinelibrary.OnlineLibrary.domain.User;
import com.onlinelibrary.OnlineLibrary.service.BookService;
import com.onlinelibrary.OnlineLibrary.service.CommentService;
import com.onlinelibrary.OnlineLibrary.service.StatsService;
import com.onlinelibrary.OnlineLibrary.service.UserService;
import jakarta.persistence.Tuple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class AdminController {
    private final UserService userService;
    private final BookService bookService;
    private final CommentService commentService;
    private final StatsService statsService;

    @Autowired
    public AdminController(UserService userService, BookService bookService, CommentService commentService, StatsService statsService) {
        this.userService = userService;
        this.bookService = bookService;
        this.commentService = commentService;
        this.statsService = statsService;
    }

    @GetMapping("/panel")
    public String panel() {
        return "panel";
    }

    @GetMapping("/panel/books")
    public String books(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "panelBooks";
    }

    @GetMapping("/panel/users")
    public String users(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "panelUsers";
    }

    @GetMapping("/panel/comments")
    public String comments(Model model) {
        List<Comment> comments = commentService.getAllComments();
        model.addAttribute("comments", comments);
        return "panelComments";
    }

    @GetMapping("/statistics")
    public String statistics(Model model) {
        List<Book> topRatedBooks = statsService.getTopRatedBooks(10);
        Double averageRating = Math.round(statsService.findAverageRating() * 10.0) / 10.0;

        Map<String, Long> topAuthors = statsService.findTopAuthors(10);
        Map<String, Long> topGenres = statsService.findTopGenres(5);

        Book oldestBook = statsService.findOldestBook();
        Book newestBook = statsService.findNewestBook();

        model.addAttribute("topGenres", topGenres);
        model.addAttribute("oldestBook", oldestBook);
        model.addAttribute("newestBook", newestBook);
        model.addAttribute("topRatedBooks", topRatedBooks);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("topAuthors", topAuthors);

        return "statistics";
    }
}
