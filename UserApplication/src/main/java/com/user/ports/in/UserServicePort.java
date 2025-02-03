package com.user.ports.in;

import com.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface UserServicePort {
    Page<User> getAllUsers(Pageable pageable);
    User getUserByUsername(String username);
    User createUser(User user);
    User updateUser(String username, User user);
    void deleteUser(String username);
    List<User> generateRandomUsers(int number);
    Map<String, Map<String, Map<String, List<User>>>> getUsersTree();
}
