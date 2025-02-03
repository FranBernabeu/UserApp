package com.user.infrastructure.persistence;

import com.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataUserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
}
