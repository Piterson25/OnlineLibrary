package com.onlinelibrary.OnlineLibrary.repository;

import com.onlinelibrary.OnlineLibrary.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
