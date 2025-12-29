package org.habit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.habit.model.HabitCategory;
import org.habit.service.HabitCategoryService;
import org.habit.service.HabitService;

import java.io.IOException;
import java.util.List;

public class CategoryCardController {

    @FXML private HBox root;
    @FXML private Label titleLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label progressLabel;
    @FXML private Label habitCountLabel;

    private HabitCategory category;
    private final HabitCategoryService categoryService = HabitCategoryService.getInstance();
    private final HabitService habitService = HabitService.getInstance();

    public void setCategory(HabitCategory category) {
        this.category = category;
        updateUI();
    }

    private void updateUI() {
        if (category != null) {
            titleLabel.setText(category.getTitle());

            String description = category.getDescription();
            if (description == null || description.isEmpty()) {
                descriptionLabel.setText("No description");
                descriptionLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
            } else {
                descriptionLabel.setText(description);
                descriptionLabel.setStyle("-fx-text-fill: #7f8c8d;");
            }

            progressLabel.setText(category.getProgress() + "%");

            // Update progress circle color based on percentage
            int progress = category.getProgress();
            if (progress >= 80) {
                root.setStyle("-fx-border-color: #27ae60; -fx-border-radius: 8; -fx-border-width: 2;");
            } else if (progress >= 50) {
                root.setStyle("-fx-border-color: #f39c12; -fx-border-radius: 8; -fx-border-width: 2;");
            } else {
                root.setStyle("-fx-border-color: #e74c3c; -fx-border-radius: 8; -fx-border-width: 2;");
            }

            // Get habit count for this category
            int habitCount = habitService.getHabitsByCategory(category.getId()).size();
            habitCountLabel.setText(habitCount + " habit" + (habitCount != 1 ? "s" : ""));
        }
    }

    @FXML
    private void handleAddHabit() {
        System.out.println("Add habit to category: " + category.getTitle());
        // We'll implement this later
    }

    @FXML
    private void handleView() {
        System.out.println("View category: " + category.getTitle());
        // Show habits in this category
        loadHabitsView();
    }

    @FXML
    private void handleEdit() {
        System.out.println("Edit category: " + category.getTitle());
        // Open edit dialog
    }

    private void loadHabitsView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/category_detail.fxml"));
            Parent view = loader.load();

            CategoryDetailController controller = loader.getController();
            controller.setCategory(category);

            // Get the main content area from dashboard
            BorderPane contentArea = (BorderPane) root.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}