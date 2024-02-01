package com.onlinelibrary.OnlineLibrary.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "books")
public class Book {
    public Book() {
        this.comments = new ArrayList<>();
    }

    public Book(String title, String imageUrl, String description, String originalDate, List<String> genres,
                List<String> authors, String publisher, Integer publishYear, List<Rating> ratings) {
        this.ownerId = null;
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.originalDate = LocalDate.parse(originalDate, formatter);
        this.genres = genres;
        this.authors = authors;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.ratings = ratings;
        this.comments = new ArrayList<>();
    }

    public void copyBookInfo(Book sourceBook) {
        this.setId(sourceBook.getId());
        this.setOwnerId(sourceBook.getOwnerId());
        this.setTitle(sourceBook.getTitle());
        this.setImageUrl(sourceBook.getImageUrl());
        this.setDescription(sourceBook.getDescription());
        this.setOriginalDate(sourceBook.getOriginalDate());
        this.setGenres(new ArrayList<>(sourceBook.getGenres()));
        this.setAuthors(new ArrayList<>(sourceBook.getAuthors()));
        this.setPublisher(sourceBook.getPublisher());
        this.setPublishYear(sourceBook.getPublishYear());
        this.setRatings(sourceBook.getRatings());
        this.setComments(sourceBook.getComments());
    }

    public Double getAverageRating() {
        final double average = this.ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0.0);

        return Math.round(average * 10.0) / 10.0;
    }

    public Integer getNumberOfRatings() {
        return this.ratings.size();
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void deleteComment(Long id) {
        this.comments.removeIf(comment -> (comment.getId().equals(id)));
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", desc='" + description + '\'' +
                ", originalDate=" + originalDate +
                ", genres=" + genres +
                ", authors=" + authors +
                ", publisher='" + publisher + '\'' +
                ", publishYear=" + publishYear +
                ", ratings=" + ratings +
                ", comments=" + comments +
                '}';
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long ownerId;
    @NotBlank(message = "Title cannot be blank")
    @Column(columnDefinition = "TEXT")
    private String title;
    @NotBlank(message = "Image URL cannot be blank")
    @URL(message = "Invalid Image URL format")
    @Column(columnDefinition = "TEXT")
    private String imageUrl;
    @NotBlank(message = "Description cannot be blank")
    @Column(columnDefinition = "TEXT")
    private String description;

    public String getOriginalDate() {
        return originalDate.toString();
    }

    public void setOriginalDate(String originalDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.originalDate = LocalDate.parse(originalDate, formatter);
    }

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private LocalDate originalDate;
    @NotEmpty(message = "Genres list cannot be empty")
    @ElementCollection
    private List<String> genres;
    @NotEmpty(message = "Genres list cannot be empty")
    @ElementCollection
    private List<String> authors;
    @NotBlank(message = "Publisher cannot be blank")
    @Column(columnDefinition = "TEXT")
    private String publisher;
    @PositiveOrZero(message = "Publish year must be a positive or zero value")
    private Integer publishYear;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Rating> ratings;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Comment> comments;
}
