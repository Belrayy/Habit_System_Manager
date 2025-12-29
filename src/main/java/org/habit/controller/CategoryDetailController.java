package org.habit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.habit.model.HabitCategory;
import org.habit.model.Habit;
import org.habit.service.HabitCategoryService;
import org.habit.service.HabitService;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CategoryDetailController {

    @FXML private Label categoryTitleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label progressLabel;
    @FXML private Label habitCountLabel;
    @FXML private Label createdLabel;
    @FXML private VBox habitsList;

    private HabitCategory category;
    private final HabitCategoryService categoryService = HabitCategoryService.getInstance();
    private final HabitService habitService = HabitService.getInstance();

    public void setCategory(HabitCategory category) {
        this.category = category;
        updateUI();
    }

    private void updateUI() {
        if (category != null) {
            categoryTitleLabel.setText(category.getTitle());
            descriptionLabel.setText(category.getDescription());
            progressLabel.setText("Progress: " + category.getProgress() + "%");

            // Get habits for this category
            List<Habit> habits = habitService.getHabitsByCategory(category.getId());
            habitCountLabel.setText(habits.size() + " habit" + (habits.size() != 1 ? "s" : ""));

            // Format created date
            // Note: We need to add created date to HabitCategory model
            // createdLabel.setText("Created: " + category.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM d, yyyy")));

            loadHabits(habits);
        }
    }

    private void loadHabits(List<Habit> habits) {
        try {
            habitsList.getChildren().clear();

            if (habits.isEmpty()) {
                Label noHabitsLabel = new Label("No habits in this category yet. Add your first habit!");
                noHabitsLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic; -fx-padding: 20;");
                habitsList.getChildren().add(noHabitsLabel);
            } else {
                for (Habit habit : habits) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/habit_card.fxml"));
                    javafx.scene.layout.HBox habitCard = loader.load();

                    HabitCardController controller = loader.getController();
                    controller.setHabit(habit);

                    habitsList.getChildren().add(habitCard);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        // Go back to dashboard
        // You'll need to implement navigation back to the main dashboard view
    }

    @FXML
    private void handleAddHabit() {
        System.out.println("Add habit to category: " + category.getTitle());
        // Open create habit form pre-filled with this category
    }
}