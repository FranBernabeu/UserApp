package com.user.ports.out;

import com.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByUsername(String username);
    Page<User> findAll(Pageable pageable);
    boolean existsById(String username);
    void delete(String username);
    List<User> saveAll(List<User> users);
    List<User> findAll();
}
