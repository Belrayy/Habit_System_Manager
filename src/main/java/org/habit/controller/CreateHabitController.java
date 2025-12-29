package org.habit.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.habit.model.HabitCategory;
import org.habit.service.HabitCategoryService;
import org.habit.service.HabitService;

import java.util.List;

public class CreateHabitController {

    @FXML private ComboBox<HabitCategory> categoryComboBox;
    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private TextField targetField;

    private final HabitCategoryService categoryService = HabitCategoryService.getInstance();
    private final HabitService habitService = HabitService.getInstance();

    @FXML
    public void initialize() {
        loadCategories();
    }

    private void loadCategories() {
        List<HabitCategory> categories = categoryService.getUserCategories();
        ObservableList<HabitCategory> categoryList = FXCollections.observableArrayList(categories);

        categoryComboBox.setItems(categoryList);
        categoryComboBox.setCellFactory(param -> new ListCell<HabitCategory>() {
            @Override
            protected void updateItem(HabitCategory item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                }
            }
        });

        categoryComboBox.setButtonCell(new ListCell<HabitCategory>() {
            @Override
            protected void updateItem(HabitCategory item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Select Category");
                } else {
                    setText(item.getTitle());
                }
            }
        });
    }

    @FXML
    private void handleSave() {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String targetText = targetField.getText().trim();

        HabitCategory selectedCategory = categoryComboBox.getValue();

        if (title.isEmpty()) {
            showAlert("Error", "Habit title cannot be empty");
            return;
        }

        if (selectedCategory == null) {
            showAlert("Error", "Please select a category");
            return;
        }

        int target;
        try {
            target = Integer.parseInt(targetText);
            if (target <= 0) {
                showAlert("Error", "Target must be a positive number");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid number for target");
            return;
        }

        boolean success = habitService.createHabit(title, description, selectedCategory.getId(), target);

        if (success) {
            showAlert("Success", "Habit created successfully!");
            handleCancel();
            loadCategories(); // Refresh categories in case they were updated
        } else {
            showAlert("Error", "Failed to create habit. Please try again.");
        }
    }

    @FXML
    private void handleCancel() {
        titleField.clear();
        descriptionField.clear();
        targetField.clear();
        categoryComboBox.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}