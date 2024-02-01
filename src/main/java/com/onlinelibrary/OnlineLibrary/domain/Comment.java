package com.onlinelibrary.OnlineLibrary.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "comments")
public class Comment {
    public Comment() {
    }

    public Comment(LocalDateTime date, Long bookId, Long authorId, String author, String text) {
        this.date = date;
        this.bookId = bookId;
        this.authorId = authorId;
        this.author = author;
        this.text = text;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", authorId='" + authorId + '\'' +
                ", author='" + author + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "BookId cannot be null")
    @Min(value = 1, message = "BookId cannot be lower than 1")
    private Long bookId;
    @NotNull(message = "Comment date cannot be null")
    private LocalDateTime date;
    private Long authorId;
    @NotNull(message = "Author name cannot be null")
    @Size(min = 3, max = 20, message = "Author name must be between 3 and 20 characters")
    private String author;
    @NotNull(message = "Comment text cannot be null")
    private String text;
}
