package com.onlinelibrary.OnlineLibrary.service;

import com.onlinelibrary.OnlineLibrary.domain.Role;
import com.onlinelibrary.OnlineLibrary.repository.RoleRepository;
import com.onlinelibrary.OnlineLibrary.repository.UserRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LoginService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public LoginService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        Map<String, Role> roleMap = context.getBeansOfType(Role.class);
        roleMap.values().forEach(this::addRole);
    }

    public void addRole(Role role) {
        this.roleRepository.save(role);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.onlinelibrary.OnlineLibrary.domain.User userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .roles("user")
                .build();
    }
}
