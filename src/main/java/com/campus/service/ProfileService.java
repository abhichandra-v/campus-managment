package com.campus.service;

import com.campus.dao.UserDao;
import com.campus.model.User;
import java.util.regex.Pattern;

/** Business rules shared by every role for editing the account-level fields (email, full name). */
public class ProfileService {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UserDao userDao;

    public ProfileService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void updateAccountInfo(long userId, String email, String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new ValidationException("Full name is required.");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("A valid email address is required.");
        }
        String trimmedEmail = email.trim();
        User existing = userDao.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        if (!trimmedEmail.equalsIgnoreCase(existing.getEmail()) && userDao.existsByEmail(trimmedEmail)) {
            throw new ValidationException("That email address is already in use.");
        }
        userDao.updateProfile(userId, trimmedEmail, fullName.trim());
    }
}
