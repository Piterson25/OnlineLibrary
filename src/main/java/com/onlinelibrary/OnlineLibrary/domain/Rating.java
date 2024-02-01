package com.onlinelibrary.OnlineLibrary.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "ratings")
public class Rating {
    public Rating() {
    }

    public Rating(Long authorId, String date, Integer rating) {
        this.authorId = authorId;
        this.date = LocalDateTime.parse(date);
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", date='" + date + '\'' +
                ", authorId='" + authorId + '\'' +
                ", rating='" + rating + '\'' +
                '}';
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long authorId;
    private LocalDateTime date;
    private Integer rating;
}
