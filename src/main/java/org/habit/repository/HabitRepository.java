package org.habit.repository;

import org.habit.config.DatabaseConfig;
import org.habit.model.Habit;
import org.habit.model.User;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HabitRepository {

    public boolean save(Habit habit, User user) {
        String sql = "INSERT INTO habit (user_id, category_id, title, description, progress, target, streak, last_completed, completed) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, user.getId());
            pstmt.setInt(2, habit.getCategoryId());
            pstmt.setString(3, habit.getTitle());
            pstmt.setString(4, habit.getDescription());
            pstmt.setInt(5, habit.getProgress());
            pstmt.setInt(6, habit.getTarget());
            pstmt.setInt(7, habit.getStreak());

            if (habit.getLastCompleted() != null) {
                pstmt.setDate(8, Date.valueOf(habit.getLastCompleted()));
            } else {
                pstmt.setNull(8, Types.DATE);
            }

            pstmt.setBoolean(9, habit.isCompleted().get());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        habit.setId(rs.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving habit: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Habit> findByUserId(int userId) {
        List<Habit> habits = new ArrayList<>();
        String sql = "SELECT * FROM habit WHERE user_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Habit habit = new Habit(
                        rs.getInt("id"),
                        rs.getInt("category_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("progress"),
                        rs.getInt("streak")
                );

                // Set additional properties
                habit.setTarget(rs.getInt("target"));

                Date lastCompleted = rs.getDate("last_completed");
                if (lastCompleted != null) {
                    habit.setLastCompleted(lastCompleted.toLocalDate());
                }

                habit.setCompleted(rs.getBoolean("completed"));
                habits.add(habit);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching habits: " + e.getMessage());
        }
        return habits;
    }

    public List<Habit> findByCategoryId(int categoryId) {
        List<Habit> habits = new ArrayList<>();
        String sql = "SELECT * FROM habit WHERE category_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Habit habit = new Habit(
                        rs.getInt("id"),
                        rs.getInt("category_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("progress"),
                        rs.getInt("streak")
                );

                habit.setTarget(rs.getInt("target"));

                Date lastCompleted = rs.getDate("last_completed");
                if (lastCompleted != null) {
                    habit.setLastCompleted(lastCompleted.toLocalDate());
                }

                habit.setCompleted(rs.getBoolean("completed"));
                habits.add(habit);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching habits by category: " + e.getMessage());
        }
        return habits;
    }
}