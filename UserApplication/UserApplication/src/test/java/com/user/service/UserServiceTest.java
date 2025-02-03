package com.user.service;

import com.user.application.UserService;
import com.user.domain.User;
import com.user.infrastructure.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User createTestUser() {
        return User.builder()
                .username("testuser")
                .name("Test User")
                .email("test@example.com")
                .gender("male")
                .picture("http://example.com/test.jpg")
                .country("Spain")
                .state("Madrid")
                .city("Madrid")
                .build();
    }

    @Test
    void getAllUsers_ShouldReturnPage() {

        List<User> users = Collections.singletonList(createTestUser());
        Page<User> page = new PageImpl<>(users);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<User> result = userService.getAllUsers(Pageable.unpaged());

        assertEquals(1, result.getTotalElements());
        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    void createUser_WhenUsernameExists_ShouldThrow() {

        User user = createTestUser();
        when(userRepository.existsById(user.getUsername())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> userService.createUser(user));
    }

    @Test
    void updateUser_ShouldUpdateFields() {

        User existingUser = createTestUser();
        User updatedUser = createTestUser().toBuilder()
                .name("New Name")
                .email("new@email.com")
                .build();

        when(userRepository.findByUsername(existingUser.getUsername())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User result = userService.updateUser(existingUser.getUsername(), updatedUser);

        assertEquals("New Name", result.getName());
        assertEquals("new@email.com", result.getEmail());
    }

    @Test
    void generateRandomUsers_ShouldOnlySaveNewUsers() {

        User newUser = createTestUser();
        List<User> externalUsers = Arrays.asList(newUser, newUser.toBuilder().username("anotheruser").build());

        when(userRepository.existsById(anyString())).thenReturn(false);
        when(userRepository.saveAll(anyList())).thenReturn(externalUsers);

        List<User> result = userService.generateRandomUsers(2);

        assertEquals(2, result.size());
        verify(userRepository).saveAll(anyList());
    }

    @Test
    void getUsersTree_ShouldGroupCorrectly() {

        User user1 = createTestUser();
        User user2 = createTestUser().toBuilder()
                .state("Barcelona")
                .city("Barcelona")
                .build();

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        var tree = userService.getUsersTree();

        assertTrue(tree.containsKey("Spain"));
        assertEquals(2, tree.get("Spain").size()); // Madrid y Barcelona
    }
}