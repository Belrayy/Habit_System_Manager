package org.example.service;

import org.example.model.User;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    // SINGLETON PATTERN - only one instance exists
    private static UserService instance;

    private final Map<String, User> users = new HashMap<>();
    private User currentUser;

    // Private constructor prevents creating multiple instances
    private UserService() {
        // Optional: Add a default admin user
        users.put("admin", new User(1, "admin", "admin123", "admin@example.com", "Admin", "User"));
    }

    // Global access point to the single instance
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    // Your existing methods (no changes needed to the logic)
    public User register(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        String username = user.getUsername();
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }

        if (users.containsKey(username)) {
            throw new IllegalArgumentException("Username '" + username + "' already exists");
        }

        users.put(username, user);
        System.out.println("DEBUG: User registered: " + username);
        System.out.println("DEBUG: Total users: " + users.size());
        System.out.println("DEBUG: User list: " + users.keySet());
        return user;
    }

    public User login(String username, String password) {
        System.out.println("DEBUG: Login attempt for: " + username);
        System.out.println("DEBUG: Available users: " + users.keySet());

        if (username == null || password == null) {
            throw new IllegalArgumentException("Username or password is null");
        }

        User user = users.get(username);
        if (user == null) {
            System.out.println("DEBUG: User not found in map!");
            throw new IllegalArgumentException("User not found");
        }

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password");
        }

        // Store current user
        currentUser = user;
        System.out.println("DEBUG: Login successful for: " + username);
        System.out.println("DEBUG: Current user set to: " + currentUser.getUsername());

        return currentUser;
    }

    public void logout() {
        System.out.println("DEBUG: Logging out user: " +
                (currentUser != null ? currentUser.getUsername() : "none"));
        currentUser = null;
    }

    public User getCurrentUser() {
        System.out.println("DEBUG: Getting current user: " +
                (currentUser != null ? currentUser.getUsername() : "null"));
        return currentUser;
    }

    // For debugging
    public Map<String, User> getAllUsers() {
        return new HashMap<>(users);
    }

    public void updateProfile(User updatedUser) {
        if (updatedUser == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        String username = updatedUser.getUsername();
        if (username == null || !users.containsKey(username)) {
            throw new IllegalArgumentException("User not found");
        }

        users.put(username, updatedUser);

        if (currentUser != null && currentUser.getUsername().equals(username)) {
            currentUser = updatedUser;
        }

        System.out.println("DEBUG: Profile updated for user: " + username);
    }

    public void deleteUser(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username is null");
        }

        if (!users.containsKey(username)) {
            throw new IllegalArgumentException("User not found");
        }

        users.remove(username);

        if (currentUser != null && currentUser.getUsername().equals(username)) {
            currentUser = null;
        }

        System.out.println("DEBUG: User deleted: " + username);
    }
}