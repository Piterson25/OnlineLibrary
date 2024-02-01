package com.onlinelibrary.OnlineLibrary.controllers;

import com.onlinelibrary.OnlineLibrary.domain.Admin;
import com.onlinelibrary.OnlineLibrary.domain.User;
import com.onlinelibrary.OnlineLibrary.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;


@Slf4j
@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password,
                            HttpSession session, RedirectAttributes redirectAttributes) {
        User user = userService.getUserByEmail(email);

        if (user == null || !userService.isUserExists(user.getEmail())) {
            redirectAttributes.addAttribute("error", "User does not exist!");
            return "login";
        }

        if (!password.equals(user.getPassword())) {
            redirectAttributes.addAttribute("error", "Password is not correct!");
            return "login";
        }

        if (user instanceof Admin adminUser) {
            session.setAttribute("user", adminUser);
            session.setAttribute("cart", new ArrayList<>());
            return "redirect:/panel";
        } else {
            session.setAttribute("user", user);
            session.setAttribute("cart", new ArrayList<>());
            return "redirect:/";
        }
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid User user, BindingResult result,
                               @RequestParam("confirmPassword") String confirmPassword) {

        if (result.hasErrors()) {
            return "register";
        }

        if (!user.getPassword().equals(confirmPassword)) {
            result.rejectValue("password", "error.password", "Passwords don't match!");
            return "register";
        }

        if (userService.isUserExists(user.getEmail())) {
            result.rejectValue("email", "error.email", "Email already exists");
            return "register";
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(hashedPassword);

        userService.addUser(user);
        System.out.println("New user created: " + user);
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        session.invalidate();

        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/user/add")
    public String addUser() {
        return "addUser";
    }

    @PostMapping("/user/add")
    public String addUser(@RequestParam String username,
                          @RequestParam String email,
                          @RequestParam String password, Model model) {
        User user = new User(username, email, password);

        if (userService.isUserExists(user.getEmail())) {
            model.addAttribute("error", "Email already exists");
            return "addUser";
        }

        userService.addUser(user);
        System.out.println("New user created: " + user);

        return "redirect:/panel/users";
    }

    @GetMapping("/user/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping("/user/edit/{id}")
    public String editUser(@PathVariable Long id,
                           @RequestParam String username,
                           @RequestParam String email,
                           @RequestParam String password) {
        User user = userService.getUserById(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        userService.addUser(user);
        return "redirect:/panel/users";
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user != null) {
            userService.deleteUserById(id);
        }
        return "redirect:/panel/users";
    }
}
