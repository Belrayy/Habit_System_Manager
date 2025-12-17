package org.example.repository;

import org.example.model.User;
import org.example.config.DatabaseConfig;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public User save(User user) {
        if (findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username '" + user.getUsername() + "' already exists");
        }

        String sql = "INSERT INTO users (username, password_hash, email, first_name, last_name) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());  // Store hashed password, not plain
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFirstName());
            pstmt.setString(5, user.getLastName());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                user.setId(rs.getInt("id"));
                System.out.println("DEBUG: User saved with ID: " + user.getId());
            }

            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user: " + e.getMessage(), e);
        }
    }

    public void update(User user) {
        String sql = "UPDATE users SET email = ?, first_name = ?, last_name = ?, " +
                "updated_at = CURRENT_TIMESTAMP WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getFirstName());
            pstmt.setString(3, user.getLastName());
            pstmt.setString(4, user.getUsername());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalArgumentException("User '" + user.getUsername() + "' not found");
            }
            System.out.println("DEBUG: User updated: " + user.getUsername());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
        }
    }

    // Add this missing method
    public void updatePassword(String username, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ?, last_password_change = CURRENT_TIMESTAMP, " +
                "updated_at = CURRENT_TIMESTAMP WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPasswordHash);
            pstmt.setString(2, username);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalArgumentException("User '" + username + "' not found");
            }
            System.out.println("DEBUG: Password updated for user: " + username);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update password: " + e.getMessage(), e);
        }
    }

    public void deleteByUsername(String username) {
        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalArgumentException("User '" + username + "' not found");
            }
            System.out.println("DEBUG: User deleted: " + username);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user: " + e.getMessage(), e);
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            System.out.println("DEBUG: Found " + users.size() + " users");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Optional: Find by ID
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Optional: Check if user exists
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Optional: Update email only
    public void updateEmail(String username, String newEmail) {
        String sql = "UPDATE users SET email = ?, updated_at = CURRENT_TIMESTAMP WHERE username = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newEmail);
            pstmt.setString(2, username);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new IllegalArgumentException("User '" + username + "' not found");
            }
            System.out.println("DEBUG: Email updated for user: " + username);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update email: " + e.getMessage(), e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        // Use the constructor that accepts password hash directly
        // Note: We need to modify the User class to have this constructor
        User user = new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password_hash"), // Changed from "password" to "password_hash"
                rs.getString("email"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                true // Flag to indicate this is from database
        );
        return user;
    }

    // Optional: Search users by name
    public List<User> searchByName(String searchTerm) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE username ILIKE ? OR first_name ILIKE ? OR last_name ILIKE ? " +
                "ORDER BY username";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String likeTerm = "%" + searchTerm + "%";
            pstmt.setString(1, likeTerm);
            pstmt.setString(2, likeTerm);
            pstmt.setString(3, likeTerm);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            System.out.println("DEBUG: Found " + users.size() + " users matching: " + searchTerm);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Optional: Get total user count
    public int countUsers() {
        String sql = "SELECT COUNT(*) FROM users";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}