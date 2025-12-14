package org.example.service;

import org.example.model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

public class UserService {
    private final Map<String, User> users = new HashMap<>();
    private User currentUser;
    private static UserService instance;

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }



    public User register(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        String username = user.getUsername();
        String password = user.getPassword();

        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password are required");
        }

        if (users.containsKey(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        users.put(username, user);
        return user;
    }

    public User login(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username or password is null");
        }

        User user = users.get(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid password");
        }

        currentUser = user;
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void updateProfile(String email, String firstName, String lastName) {
        if (currentUser == null) {
            throw new SecurityException("User not logged in");
        }

        currentUser.setEmail(email);
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);

        users.put(currentUser.getUsername(), currentUser);
    }

    public void updateProfile(StringProperty email, StringProperty firstName, StringProperty lastName) {
        if (currentUser == null) {
            throw new SecurityException("User not logged in");
        }

        currentUser.setEmail(email);
        currentUser.setFirstName(firstName);
        currentUser.setLastName(lastName);

        users.put(currentUser.getUsername(), currentUser);
    }

    public void deleteUser(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username is null");
        }

        if (currentUser == null || !currentUser.getUsername().equals(username)) {
            throw new SecurityException("Unauthorized deletion");
        }

        users.remove(username);
        currentUser = null;
    }
}