package com.campus.service;

import com.campus.dao.UserDao;
import com.campus.model.User;
import com.campus.util.PasswordUtil;

/** Verifies login credentials against stored BCrypt hashes. */
public class AuthService {

    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Authenticates a username/password pair. Deliberately uses the same
     * "Invalid username or password" message for an unknown username and a
     * wrong password so a failed login does not reveal which usernames exist.
     */
    public User authenticate(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            throw new AuthenticationException("Invalid username or password.");
        }
        User user = userDao.findByUsername(username.trim())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password."));
        if (!PasswordUtil.verify(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password.");
        }
        if (!user.isActive()) {
            throw new AuthenticationException("This account has been deactivated.");
        }
        return user;
    }
}
