package com.sunrisedental.service;

import com.sunrisedental.dao.DAOFactory;
import com.sunrisedental.dao.UserDAO;
import com.sunrisedental.model.User;
import com.sunrisedental.util.PasswordUtil;

import java.util.logging.Logger;

/**
 * Business Service: AuthService
 * Handles secure authentication, role verification, and password validation.
 */
public class AuthService {

    private static final Logger LOGGER = Logger.getLogger(AuthService.class.getName());
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = DAOFactory.getUserDAO();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User authenticate(String username, String rawPassword) {
        if (username == null || rawPassword == null) {
            return null;
        }

        User user = userDAO.findByUsername(username.trim());
        if (user == null) {
            LOGGER.warning("Authentication failed: user not found -> " + username);
            return null;
        }

        if (PasswordUtil.verifyPassword(rawPassword.trim(), user.getPasswordHash(), user.getSalt())) {
            LOGGER.info("Authentication successful for user: " + username + " with role: " + user.getRole());
            return user;
        } else {
            LOGGER.warning("Authentication failed: invalid password for user -> " + username);
            return null;
        }
    }
}
