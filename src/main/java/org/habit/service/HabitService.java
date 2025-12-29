package org.habit.service;

import org.habit.model.Habit;
import org.habit.model.HabitCategory;
import org.habit.model.User;
import org.habit.repository.HabitRepository;

import java.time.LocalDate;
import java.util.List;

public class HabitService {
    private final HabitRepository repository = new HabitRepository();
    private final UserService userService = UserService.getInstance();

    public boolean createHabit(String title, String description, int categoryId, int target) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            System.err.println("No user logged in");
            return false;
        }

        Habit habit = new Habit(title, description, categoryId, target);

        boolean success = repository.save(habit, currentUser);
        if (success) {
            System.out.println("Habit saved successfully: " + title);
        } else {
            System.err.println("Failed to save habit");
        }
        return success;
    }

    public List<Habit> getUserHabits() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) return List.of();

        return repository.findByUserId(currentUser.getId());
    }

    public List<Habit> getHabitsByCategory(int categoryId) {
        return repository.findByCategoryId(categoryId);
    }

    // Singleton pattern
    private static HabitService instance;
    public static HabitService getInstance() {
        if (instance == null) {
            instance = new HabitService();
        }
        return instance;
    }

    public int getTotalHabitsCount() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) return 0;
        return getUserHabits().size();
    }

    public long getCompletedTodayCount() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) return 0;

        return getUserHabits().stream()
                .filter(habit -> habit.getLastCompleted() != null)
                .filter(habit -> habit.getLastCompleted().isEqual(LocalDate.now()))
                .count();
    }
}