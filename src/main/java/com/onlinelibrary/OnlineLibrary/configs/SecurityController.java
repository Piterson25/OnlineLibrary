package com.onlinelibrary.OnlineLibrary.configs;

import com.onlinelibrary.OnlineLibrary.service.LoginService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityController {

    private final LoginService loginService;

    public SecurityController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.formLogin(Customizer.withDefaults())
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/login/**").permitAll()
                        .requestMatchers("/panel/").hasAnyAuthority("admin")
                        .requestMatchers("/statistics/").hasAnyAuthority("admin")
                        .requestMatchers("/api/stats/").hasAnyAuthority("admin")
                        .anyRequest().authenticated())
                .oauth2Login(Customizer.withDefaults())
                .userDetailsService(loginService)
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .sessionRegistry(sessionRegistry()))
                .logout().logoutSuccessUrl("/login").invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .and()
                .csrf().disable().httpBasic().and().build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
}


