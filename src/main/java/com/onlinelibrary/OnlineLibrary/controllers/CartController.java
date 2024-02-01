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
import java.util.List;

@Controller
public class CartController {
    private final OrderService orderService;
    private final BookService bookService;

    @Autowired
    public CartController(OrderService orderService, BookService bookService) {
        this.orderService = orderService;
        this.bookService = bookService;
    }

    @GetMapping("/cart")
    public String showCart(HttpSession session, Model model) {
        Object cartObject = session.getAttribute("cart");
        List<Book> items = (List<Book>) cartObject;
        model.addAttribute("items", items);
        return "cart";
    }

    @PostMapping("/add/{bookId}")
    public String addToCart(@PathVariable("bookId") Long bookId, HttpSession session) {
        Object cartObject = session.getAttribute("cart");
        List<Book> items = (List<Book>) cartObject;
        items.add(bookService.getBookById(bookId));
        return "redirect:/cart";
    }

    @GetMapping("/cart/delete")
    public String deleteCart(HttpSession session) {
        Object cartObject = session.getAttribute("cart");
        List<Book> items = (List<Book>) cartObject;
        items.clear();
        return "redirect:/cart";
    }

    @GetMapping("/cart/delete/{itemId}")
    public String deleteItem(@PathVariable("itemId") Long itemId, HttpSession session) {
        Object cartObject = session.getAttribute("cart");
        List<Book> items = (List<Book>) cartObject;
        items.removeIf(item -> item.getId().equals(itemId));
        return "redirect:/cart";
    }

    @GetMapping("/confirmation")
    public String showConfirmationPage() {
        return "confirmation";
    }

    @PostMapping("/confirmation")
    public String placeOrder(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Object cartObject = session.getAttribute("cart");
        List<Book> items = (List<Book>) cartObject;
        for (Book item : items) {
            Book book = bookService.getBookById(item.getId());
            book.setOwnerId(user.getId());
            Order order = new Order(user.getId(), book, LocalDateTime.now(), OrderStatus.CONFIRMED);
            orderService.placeOrder(order);
            System.out.println("New order: " + order);
        }
        items.clear();

        return "confirmation";
    }
}
