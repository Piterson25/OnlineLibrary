package com.onlinelibrary.OnlineLibrary.controllersAPI;

import com.onlinelibrary.OnlineLibrary.domain.Book;
import com.onlinelibrary.OnlineLibrary.domain.Order;
import com.onlinelibrary.OnlineLibrary.enums.OrderStatus;
import com.onlinelibrary.OnlineLibrary.service.BookService;
import com.onlinelibrary.OnlineLibrary.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    private final OrderService orderService;
    private final BookService bookService;

    @Autowired
    public OrderApiController(OrderService orderService, BookService bookService) {
        this.orderService = orderService;
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> showOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            if (order != null) {
                return new ResponseEntity<>(order, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Couldn't find order with given id: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/statuses")
    public ResponseEntity<?> getOrderStatuses() {
        try {
            List<String> statuses = new ArrayList<>();
            for (OrderStatus status : OrderStatus.values()) {
                statuses.add(status.toString());
            }
            return new ResponseEntity<>(statuses, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<String> addOrder(@RequestBody @Valid Order order) {
        try {
            Book book = bookService.getBookById(order.getBook().getId());
            book.setOwnerId(order.getUserId());
            orderService.placeOrder(order);
            System.out.println("New order: " + order);
            return new ResponseEntity<>("Order placed successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> editOrder(@PathVariable Long id, @RequestBody @Valid Order updatedOrder) {
        try {
            Order order = orderService.getOrderById(id);
            if (order != null) {
                Book book = bookService.getBookById(order.getBook().getId());
                order.setStatus(updatedOrder.getStatus());
                order.setReturnedDate(updatedOrder.getReturnedDate());
                order.setBorrowedDate(updatedOrder.getBorrowedDate());
                order.setUserId(updatedOrder.getUserId());
                order.getBook().setOwnerId(updatedOrder.getBook().getOwnerId());
                book.setOwnerId(order.getUserId());
                orderService.placeOrder(order);
                System.out.println("Edited order: " + order);
                return new ResponseEntity<>("Order edited successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Couldn't find order with given id: " + id, HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            if (order != null) {
                System.out.println("Deleted order: " + order);
                orderService.deleteOrderById(id);
                return new ResponseEntity<>("Order deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Couldn't find order with given id: " + id, HttpStatus.NOT_FOUND);
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

