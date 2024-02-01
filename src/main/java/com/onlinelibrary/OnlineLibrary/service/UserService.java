package com.onlinelibrary.OnlineLibrary.service;

import com.onlinelibrary.OnlineLibrary.domain.User;
import com.onlinelibrary.OnlineLibrary.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@Service
@Getter
@Validated
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        Map<String, User> userMap = context.getBeansOfType(User.class);
        userMap.values().forEach(this::addUser);
    }

    public List<User> getAllUsers() {
        return this.repository.findAll();
    }

    public User getUserById(Long id) {
        return this.repository.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return this.repository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public void addUser(@Valid User user) {
        this.repository.save(user);
    }

    public void deleteUserById(Long id) {
        this.repository.deleteById(id);
    }

    public boolean isUserExists(String email) {
        return this.repository.findByEmail(email).isPresent();
    }
}
