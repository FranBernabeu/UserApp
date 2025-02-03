package com.user.infrastructure.persistence;

import com.user.domain.User;
import com.user.ports.out.UserRepositoryPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserRepository implements UserRepositoryPort {

    @Autowired
    private SpringDataUserRepository springDataRepository;

    @Override
    public User save(User user) {
        return springDataRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return springDataRepository.findById(username);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return springDataRepository.findAll(pageable);
    }

    @Override
    public boolean existsById(String username) {
        return springDataRepository.existsById(username);
    }

    @Override
    public void delete(String username) {
        springDataRepository.deleteById(username);
    }

    @Override
    public List<User> saveAll(List<User> users) {
        return springDataRepository.saveAll(users);
    }

    @Override
    public List<User> findAll() {
        return springDataRepository.findAll();
    }
}