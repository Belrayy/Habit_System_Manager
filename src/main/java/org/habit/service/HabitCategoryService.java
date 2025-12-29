package org.habit.service;

import org.habit.model.HabitCategory;
import org.habit.model.User;
import org.habit.repository.HabitCategoryRepository;

import java.util.List;

public class HabitCategoryService {
    private final HabitCategoryRepository repository = new HabitCategoryRepository();
    private final UserService userService = UserService.getInstance();

    public boolean createCategory(String title, String description) {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            System.err.println("No user logged in");
            return false;
        }

        HabitCategory category = new HabitCategory(0, title, description);
        category.SetDescription(description);

        boolean success = repository.save(category, currentUser);
        if (success) {
            System.out.println("Category saved successfully: " + title);
        } else {
            System.err.println("Failed to save category");
        }
        return success;
    }

    public List<HabitCategory> getUserCategories() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) return List.of();

        return repository.findByUserId(currentUser.getId());
    }

    // Singleton pattern
    private static HabitCategoryService instance;
    public static HabitCategoryService getInstance() {
        if (instance == null) {
            instance = new HabitCategoryService();
        }
        return instance;
    }
}