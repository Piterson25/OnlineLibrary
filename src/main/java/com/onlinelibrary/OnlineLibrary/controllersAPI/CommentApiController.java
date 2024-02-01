package com.onlinelibrary.OnlineLibrary.controllersAPI;

import com.onlinelibrary.OnlineLibrary.domain.Book;
import com.onlinelibrary.OnlineLibrary.domain.Comment;
import com.onlinelibrary.OnlineLibrary.service.BookService;
import com.onlinelibrary.OnlineLibrary.service.CommentService;
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
@RequestMapping("/api/comments")
public class CommentApiController {

    private final CommentService commentService;
    private final BookService bookService;

    @Autowired
    public CommentApiController(CommentService commentService, BookService bookService) {
        this.commentService = commentService;
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCommentById(@PathVariable Long id) {
        try {
            Comment comment = commentService.getCommentById(id);
            if (comment != null) {
                return new ResponseEntity<>(comment, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Couldn't find comment with given id: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<String> addComment(@RequestBody @Valid Comment comment) {
        try {
            Book book = bookService.getBookById(comment.getBookId());
            if (book != null) {
                commentService.addComment(comment);
                book.addComment(comment);
                bookService.addBook(book);
                System.out.println("New comment: " + comment);
                return new ResponseEntity<>("Comment added successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Couldn't find book from comment with this id: " + comment.getBookId(), HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateComment(@PathVariable Long id,
                                                @RequestBody @Valid Comment newComment) {
        try {
            Comment comment = commentService.getCommentById(id);
            if (comment != null) {
                comment.setDate(newComment.getDate());
                comment.setAuthorId(newComment.getAuthorId());
                comment.setAuthor(newComment.getAuthor());
                comment.setText(newComment.getText());
                commentService.addComment(comment);
                Book book = bookService.getBookById(id);
                book.addComment(comment);
                bookService.addBook(book);
                System.out.println("Updated comment: " + comment);
                return new ResponseEntity<>("Comment updated successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Couldn't find comment with given id: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        try {
            Comment comment = commentService.getCommentById(id);
            if (comment != null) {
                System.out.println("Deleted comment: " + comment);
                commentService.deleteCommentById(id);
                return new ResponseEntity<>("Comment deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Couldn't find comment with given id: " + id, HttpStatus.NOT_FOUND);
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

