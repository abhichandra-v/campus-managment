package com.campus.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.campus.dao.UserDao;
import com.campus.model.Role;
import com.campus.model.User;
import com.campus.util.PasswordUtil;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuthServiceTest {

    private UserDao userDao;
    private AuthService authService;
    private User activeUser;

    @BeforeEach
    void setUp() {
        userDao = mock(UserDao.class);
        authService = new AuthService(userDao);

        activeUser = new User();
        activeUser.setId(1L);
        activeUser.setUsername("jdoe");
        activeUser.setPasswordHash(PasswordUtil.hash("CorrectHorse123!"));
        activeUser.setRole(Role.STUDENT);
        activeUser.setEmail("jdoe@example.edu");
        activeUser.setFullName("Jane Doe");
        activeUser.setActive(true);
    }

    @Test
    void authenticateSucceedsWithCorrectCredentials() {
        when(userDao.findByUsername("jdoe")).thenReturn(Optional.of(activeUser));

        User result = authService.authenticate("jdoe", "CorrectHorse123!");

        assertEquals("jdoe", result.getUsername());
    }

    @Test
    void authenticateFailsWithWrongPassword() {
        when(userDao.findByUsername("jdoe")).thenReturn(Optional.of(activeUser));

        AuthenticationException ex = assertThrows(AuthenticationException.class,
                () -> authService.authenticate("jdoe", "wrongpassword"));
        assertEquals("Invalid username or password.", ex.getMessage());
    }

    @Test
    void authenticateFailsWithUnknownUsername() {
        when(userDao.findByUsername("nosuchuser")).thenReturn(Optional.empty());

        AuthenticationException ex = assertThrows(AuthenticationException.class,
                () -> authService.authenticate("nosuchuser", "whatever"));
        assertEquals("Invalid username or password.", ex.getMessage());
    }

    @Test
    void authenticateFailsForADeactivatedAccountEvenWithCorrectPassword() {
        activeUser.setActive(false);
        when(userDao.findByUsername("jdoe")).thenReturn(Optional.of(activeUser));

        AuthenticationException ex = assertThrows(AuthenticationException.class,
                () -> authService.authenticate("jdoe", "CorrectHorse123!"));
        assertEquals("This account has been deactivated.", ex.getMessage());
    }

    @Test
    void authenticateRejectsBlankCredentialsWithoutHittingTheDatabase() {
        assertThrows(AuthenticationException.class, () -> authService.authenticate("", "x"));
        assertThrows(AuthenticationException.class, () -> authService.authenticate("jdoe", ""));
    }
}
