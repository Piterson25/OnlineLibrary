package com.onlinelibrary.OnlineLibrary.service;

import com.onlinelibrary.OnlineLibrary.domain.Order;
import com.onlinelibrary.OnlineLibrary.enums.OrderStatus;
import com.onlinelibrary.OnlineLibrary.repository.OrderRepository;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Service
@Configuration
@EnableScheduling
public class OrderService {
    private final OrderRepository repository;

    @Autowired
    public OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    public void placeOrder(@Valid Order order) {
        this.repository.save(order);
    }

    public List<Order> getAllOrders() {
        return this.repository.findAll();
    }

    public void deleteOrderById(Long id) {
        this.repository.deleteById(id);
    }

    @Scheduled(fixedRate = 10000)
    public void updateOrderStatus() {
        for (Order order : this.repository.findAll()) {
            switch (order.getStatus()) {
                case CONFIRMED:
                    order.setStatus(OrderStatus.IN_PROGRESS);
                    break;
                case IN_PROGRESS:
                    order.setStatus(OrderStatus.BORROWED);
                    break;
                default:
                    break;
            }
            repository.save(order);
        }
    }

    public Order getOrderById(Long orderId) {
        return this.repository.findById(orderId).orElse(null);
    }
}
