package com.onlinelibrary.OnlineLibrary.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@MappedSuperclass
@Getter
@Setter
public abstract class AbstractUser implements UserDetails {
    @NotNull(message = "Username cannot be null")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Column(name = "username")
    protected String username;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Invalid email format")
    @Column(name = "email")
    protected String email;
    @NotNull(message = "Password cannot be null")
    @Size(min = 4, message = "Password must be at least 4 characters")
    @Column(name = "password")
    protected String password;

    public abstract boolean isAdmin();

    public abstract Collection<? extends GrantedAuthority> getAuthorities();
}
