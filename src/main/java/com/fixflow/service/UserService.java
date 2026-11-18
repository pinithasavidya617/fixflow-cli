package com.fixflow.service;

import com.fixflow.model.User;
import com.fixflow.repository.UserRepository;
import com.fixflow.util.PasswordUtil;

import java.util.List;


public class UserService {

    private static UserRepository userRepo = new UserRepository();

    private UserService() {
    }

    public static void setRepository(UserRepository ur) {
        userRepo = ur;
    }

    public static UserRepository getUserRepository() {
        return userRepo;
    }


    public static User authenticate(String username, String plainPassword) {
        if (username == null || username.trim().isEmpty() || plainPassword == null || plainPassword.isEmpty()) {
            return null;
        }

        User user = userRepo.findByUsername(username.trim());
        if (user != null && user.getPasswordHash() != null) {
            if (PasswordUtil.verifyPassword(plainPassword, user.getPasswordHash())) {
                return user;
            }
        }
        return null;
    }

    public static boolean changePassword(int userId, String newPlainPassword) {
        if (newPlainPassword == null || newPlainPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty.");
        }
        User user = userRepo.findById(userId);
        if (user == null) {
            return false;
        }

        user.setPasswordHash(PasswordUtil.hashPassword(newPlainPassword.trim()));
        userRepo.update(user);
        return true;
    }

    public static List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public static User getUserById(int id) {
        return userRepo.findById(id);
    }
}
