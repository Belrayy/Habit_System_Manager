package org.example.service;

import org.example.model.User;
import org.example.repository.UserRepository;
import org.example.util.PasswordUtil;
import java.util.List;
import java.util.Optional;

public class UserService {
    private static UserService instance;
    private final UserRepository userRepository;
    private User currentUser;

    private UserService() {
        this.userRepository = new UserRepository();
        // Ensure admin user exists
        ensureAdminUser();
    }

    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }

    private void ensureAdminUser() {
        try {
            // Check if admin exists
            var adminOpt = userRepository.findByUsername("admin");
            if (adminOpt.isEmpty()) {
                // Create admin with hashed password
                User admin = new User(0, "admin", "admin123",
                        "admin@example.com", "Admin", "User");
                userRepository.save(admin);
                System.out.println("DEBUG: Admin user created");
            } else {
                // Check if admin password needs rehashing
                User admin = adminOpt.get();
                if (admin.needsPasswordRehash()) {
                    admin.setPlainPassword("admin123");
                    userRepository.updatePassword("admin", admin.getPasswordHash());
                    System.out.println("DEBUG: Admin password rehashed");
                }
            }
        } catch (IllegalArgumentException e) {
            // Admin already exists, ignore
            System.out.println("DEBUG: Admin user already exists");
        } catch (Exception e) {
            System.err.println("ERROR: Failed to ensure admin user: " + e.getMessage());
        }
    }

    public User register(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        String username = user.getUsername();
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }

        // Validate password strength
        if (!isPasswordStrong(user.getPlainPassword())) {
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters with uppercase, " +
                            "lowercase, digit, and special character"
            );
        }

        System.out.println("DEBUG: Registering user: " + username);
        User savedUser = userRepository.save(user);
        System.out.println("DEBUG: User registered with ID: " + savedUser.getId());

        // Clear plain password from memory for security
        user.clearPlainPassword();

        return savedUser;
    }

    public User login(String username, String password) {
        System.out.println("DEBUG: Login attempt for: " + username);

        if (username == null || password == null) {
            throw new IllegalArgumentException("Username or password is null");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("DEBUG: User not found: " + username);
                    return new IllegalArgumentException("User not found");
                });

        // Use verifyPassword instead of direct string comparison
        if (!user.verifyPassword(password)) {
            System.out.println("DEBUG: Invalid password for user: " + username);
            throw new IllegalArgumentException("Invalid password");
        }

        System.out.println("DEBUG: Password verified successfully for: " + username);

        // Check if password needs rehashing (e.g., using old algorithm)
        if (user.needsPasswordRehash()) {
            System.out.println("DEBUG: Password needs rehash for: " + username);
            user.setPlainPassword(password);
            userRepository.updatePassword(username, user.getPasswordHash());
            System.out.println("DEBUG: Password rehashed for: " + username);
        }

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

    public void updateProfile(User updatedUser) {
        if (updatedUser == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        String username = updatedUser.getUsername();
        System.out.println("DEBUG: Updating profile for: " + username);

        // Don't update password via profile update
        // Keep existing password hash
        User existingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("DEBUG: User not found for update: " + username);
                    return new IllegalArgumentException("User not found");
                });

        // Preserve the password hash
        updatedUser.setPasswordHash(existingUser.getPasswordHash());
        userRepository.update(updatedUser);

        if (currentUser != null && currentUser.getUsername().equals(username)) {
            currentUser = updatedUser;
        }

        System.out.println("DEBUG: Profile updated for: " + username);
    }

    // New method to change password
    public void changePassword(String username, String currentPassword, String newPassword) {
        if (username == null || currentPassword == null || newPassword == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        System.out.println("DEBUG: Changing password for: " + username);

        // Validate new password strength
        if (!isPasswordStrong(newPassword)) {
            throw new IllegalArgumentException(
                    "New password must be at least 8 characters with uppercase, " +
                            "lowercase, digit, and special character"
            );
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    System.out.println("DEBUG: User not found for password change: " + username);
                    return new IllegalArgumentException("User not found");
                });

        // Verify current password
        if (!user.verifyPassword(currentPassword)) {
            System.out.println("DEBUG: Current password incorrect for: " + username);
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Hash and save new password
        String newPasswordHash = PasswordUtil.hashPassword(newPassword);
        userRepository.updatePassword(username, newPasswordHash);

        // Update current user if it's the logged-in user
        if (currentUser != null && currentUser.getUsername().equals(username)) {
            currentUser.setPasswordHash(newPasswordHash);
        }

        System.out.println("DEBUG: Password changed successfully for: " + username);
    }

    public void deleteUser(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username is null");
        }

        System.out.println("DEBUG: Deleting user: " + username);

        userRepository.deleteByUsername(username);

        if (currentUser != null && currentUser.getUsername().equals(username)) {
            System.out.println("DEBUG: Logging out deleted user: " + username);
            currentUser = null;
        }

        System.out.println("DEBUG: User deleted: " + username);
    }

    // For debugging
    public List<User> getAllUsers() {
        System.out.println("DEBUG: Getting all users");
        return userRepository.findAll();
    }

    // Validate password strength
    public boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        // Check for at least one uppercase, one lowercase, one digit, one special char
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        // Common password blacklist
        String[] weakPasswords = {
                "password", "123456", "qwerty", "admin", "welcome",
                "password123", "admin123", "letmein", "monkey", "12345678",
                "abc123", "123456789", "111111", "123123", "sunshine"
        };

        for (String weak : weakPasswords) {
            if (password.equalsIgnoreCase(weak)) {
                return false;
            }
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    // Optional: Get password strength score (1-5)
    public int getPasswordStrengthScore(String password) {
        if (password == null) return 0;

        int score = 0;

        // Length
        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;

        // Character variety
        if (password.matches(".*[A-Z].*")) score++; // Has uppercase
        if (password.matches(".*[a-z].*")) score++; // Has lowercase
        if (password.matches(".*\\d.*")) score++;   // Has digit
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) score++; // Has special

        return Math.min(score, 5); // Cap at 5
    }

    // Optional: Reset password (admin function)
    public void resetPassword(String username, String newPassword) {
        if (username == null || newPassword == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }

        // Validate new password strength
        if (!isPasswordStrong(newPassword)) {
            throw new IllegalArgumentException(
                    "New password must be at least 8 characters with uppercase, " +
                            "lowercase, digit, and special character"
            );
        }

        // Check if user exists
        if (!userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("User not found");
        }

        // Hash and save new password
        String newPasswordHash = PasswordUtil.hashPassword(newPassword);
        userRepository.updatePassword(username, newPasswordHash);

        // Update current user if it's the logged-in user
        if (currentUser != null && currentUser.getUsername().equals(username)) {
            currentUser.setPasswordHash(newPasswordHash);
        }

        System.out.println("DEBUG: Password reset for: " + username);
    }


    public boolean usernameExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }

        System.out.println("DEBUG: Checking if username exists: " + username);

        try {
            // Use the existing findByUsername method
            Optional<User> userOpt = userRepository.findByUsername(username);
            boolean exists = userOpt.isPresent();

            System.out.println("DEBUG: Username '" + username + "' exists: " + exists);
            return exists;

        } catch (Exception e) {
            System.err.println("ERROR checking username existence: " + e.getMessage());
            return false;
        }
    }

    public boolean emailExists(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        System.out.println("DEBUG: Checking if email exists: " + email);

        try {
            // Alternative: Get all users and check for duplicate email
            List<User> allUsers = getAllUsers();

            // Skip checking for current user's email (if they're not changing it)
            String currentEmail = currentUser != null ? currentUser.getEmail() : null;

            boolean exists = allUsers.stream()
                    .filter(user -> currentEmail == null || !user.getEmail().equalsIgnoreCase(currentEmail))
                    .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));

            System.out.println("DEBUG: Email '" + email + "' exists: " + exists);
            return exists;

        } catch (Exception e) {
            System.err.println("ERROR checking email existence: " + e.getMessage());
            return false;
        }
    }
}