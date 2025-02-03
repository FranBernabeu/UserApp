package com.user.application;

import com.user.infrastructure.external.randomuser.dto.RandomUserResponse;
import com.user.domain.User;
import com.user.infrastructure.persistence.UserRepository;
import com.user.ports.in.UserServicePort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class UserService implements UserServicePort {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public User createUser(User user) {
        if (userRepository.existsById(user.getUsername())) {
            throw new RuntimeException("El username ya existe");
        }
        return userRepository.save(user);
    }

    public User updateUser(String username, User userDetails) {
        User user = getUserByUsername(username);
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setGender(userDetails.getGender());
        user.setPicture(userDetails.getPicture());
        return userRepository.save(user);
    }

    public void deleteUser(String username) {
        userRepository.delete(getUserByUsername(username).getUsername());
    }

    public List<User> generateRandomUsers(int number) {
        String url = "https://randomuser.me/api/?results=" + number;
        RandomUserResponse response = restTemplate.getForObject(url, RandomUserResponse.class);

        List<User> newUsers = Objects.requireNonNull(response).getResults().stream()
                .map(randomUser -> {
                    User user = new User();
                    user.setUsername(randomUser.getLogin().getUsername());
                    user.setName(randomUser.getName().getFirst() + " " + randomUser.getName().getLast());
                    user.setEmail(randomUser.getEmail());
                    user.setGender(randomUser.getGender());
                    user.setPicture(randomUser.getPicture().getLarge());
                    user.setCountry(randomUser.getLocation().getCountry());
                    user.setState(randomUser.getLocation().getState());
                    user.setCity(randomUser.getLocation().getCity());
                    return user;
                })
                .filter(user -> !userRepository.existsById(user.getUsername()))
                .toList();

        return userRepository.saveAll(newUsers);
    }

    public Map<String, Map<String, Map<String, List<User>>>> getUsersTree() {
        List<User> users = userRepository.findAll();

        Map<String, Map<String, Map<String, List<User>>>> tree = new HashMap<>();

        users.forEach(user -> tree.computeIfAbsent(user.getCountry(), k -> new HashMap<>())
                .computeIfAbsent(user.getState(), k -> new HashMap<>())
                .computeIfAbsent(user.getCity(), k -> new ArrayList<>())
                .add(user));

        return tree;
    }
}