package org.habit.repository;

import org.habit.config.DatabaseConfig;
import org.habit.model.HabitCategory;
import org.habit.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HabitCategoryRepository {

    public boolean save(HabitCategory category, User user) {
        String sql = "INSERT INTO habit_category (user_id, title, description, progress) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, user.getId());
            pstmt.setString(2, category.getTitle());
            pstmt.setString(3, category.getDescription());
            pstmt.setInt(4, category.getProgress());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        category.SetId(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving habit category: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<HabitCategory> findByUserId(int userId) {
        List<HabitCategory> categories = new ArrayList<>();
        String sql = "SELECT * FROM habit_category WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                HabitCategory category = new HabitCategory(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description")
                );
                category.SetProgress(rs.getInt("progress"));
                categories.add(category);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching categories: " + e.getMessage());
        }
        return categories;
    }
}