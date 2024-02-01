package com.onlinelibrary.OnlineLibrary.domain;

import com.onlinelibrary.OnlineLibrary.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {

    public Order() {
    }

    public Order(Long userId, Book book, LocalDateTime borrowedDate, OrderStatus orderStatus) {
        this.book = new Book();
        this.book.copyBookInfo(book);
        this.userId = userId;
        this.borrowedDate = borrowedDate;
        this.status = orderStatus;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", book'" + book + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "UserId cannot be null")
    private Long userId;
    @ManyToOne
    @JoinColumn(name = "book_id")
    @NotNull(message = "Book cannot be null")
    private Book book;
    @NotNull(message = "BorrowedDate cannot be null")
    private LocalDateTime borrowedDate;
    private LocalDateTime returnedDate;
    @NotNull(message = "Status cannot be null")
    private OrderStatus status;
}
