package com.onlinelibrary.OnlineLibrary.controllers;

import com.onlinelibrary.OnlineLibrary.domain.Book;
import com.onlinelibrary.OnlineLibrary.domain.Comment;
import com.onlinelibrary.OnlineLibrary.domain.User;
import com.onlinelibrary.OnlineLibrary.service.BookService;
import com.onlinelibrary.OnlineLibrary.service.CommentService;
import com.onlinelibrary.OnlineLibrary.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Controller
public class CommentController {
    private final CommentService commentService;
    private final BookService bookService;
    private final UserService userService;

    @Autowired
    public CommentController(CommentService commentService, BookService bookService, UserService userService) {
        this.commentService = commentService;
        this.bookService = bookService;
        this.userService = userService;
    }

    @PostMapping("/comment/{id}")
    public String addComment(@PathVariable("id") Long id, @RequestParam("text") String text,
                             HttpSession session) {
        Book book = bookService.getBookById(id);
        User user = (User) session.getAttribute("user");
        Long authorId = null;
        String author = "Anonymous";
        if (user != null) {
            authorId = user.getId();
            author = user.getUsername();
        }
        Comment comment = new Comment(LocalDateTime.now(), book.getId(), authorId, author, text);
        book.addComment(comment);
        commentService.addComment(comment);
        return "redirect:/books/{id}";
    }

    @GetMapping("/comment/edit/{id}")
    public String editComment(@PathVariable Long id, Model model) {
        Comment comment = commentService.getCommentById(id);
        List<User> users = userService.getAllUsers();
        model.addAttribute("comment", comment);
        model.addAttribute("users", users);
        return "editComment";
    }

    @PostMapping("/comment/edit/{id}")
    public String updateComment(@PathVariable Long id,
                                @RequestParam("date") LocalDate date,
                                @RequestParam("time") LocalTime time,
                                @RequestParam("authorId") Long authorId,
                                @RequestParam("text") String text) {
        Comment comment = commentService.getCommentById(id);
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        comment.setDate(dateTime);
        comment.setAuthorId(authorId);
        comment.setAuthor(userService.getUserById(authorId).getUsername());
        comment.setText(text);
        commentService.addComment(comment);
        return "redirect:/panel/comments";
    }

    @GetMapping("/comment/delete/{id}")
    public String deleteComment(@PathVariable Long id) {
        Comment comment = commentService.getCommentById(id);
        if (comment != null) {
            for (Book book : bookService.getAllBooks()) {
                book.deleteComment(id);
                bookService.addBook(book);
            }
            commentService.deleteCommentById(id);
        }

        return "redirect:/panel/comments";
    }
}
