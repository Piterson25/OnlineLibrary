package com.onlinelibrary.OnlineLibrary.controllers;

import com.onlinelibrary.OnlineLibrary.domain.Book;
import com.onlinelibrary.OnlineLibrary.domain.Order;
import com.onlinelibrary.OnlineLibrary.enums.OrderStatus;
import com.onlinelibrary.OnlineLibrary.domain.User;
import com.onlinelibrary.OnlineLibrary.service.BookService;
import com.onlinelibrary.OnlineLibrary.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {
    private final OrderService orderService;
    private final BookService bookService;

    @Autowired
    public OrderController(OrderService orderService, BookService bookService) {
        this.orderService = orderService;
        this.bookService = bookService;
    }

    @GetMapping("/orders")
    public String showOrders(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<Order> orders = new ArrayList<>();

        for (Order order : orderService.getAllOrders()) {
            if (order.getUserId().equals(user.getId())) {
                orders.add(order);
            }
        }

        model.addAttribute("statuses", OrderStatus.class);
        model.addAttribute("orders", orders);
        return "orders";
    }

    @PostMapping("/returnOrder/{orderId}")
    public String returnOrder(@PathVariable("orderId") Long orderId) {
        Order order = orderService.getOrderById(orderId);
        Book book = bookService.getBookById(order.getBook().getId());
        System.out.println("Returned: " + book);
        order.setStatus(OrderStatus.RETURNED);
        order.setReturnedDate(LocalDateTime.now());
        order.getBook().setOwnerId(null);
        orderService.placeOrder(order);
        if (book != null) {
            book.setOwnerId(null);
        }

        return "redirect:/orders";
    }


}
