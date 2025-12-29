package org.habit.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.habit.service.HabitCategoryService;

public class CreateCategoryController {

    @FXML private TextField titleField;
    @FXML private TextArea descriptionField;
    @FXML private BorderPane root;

    private final HabitCategoryService categoryService = HabitCategoryService.getInstance();

    @FXML
    private void handleSave() {
        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();

        if (title.isEmpty()) {
            showAlert("Error", "Category title cannot be empty");
            return;
        }

        boolean success = categoryService.createCategory(title, description);

        if (success) {
            showAlert("Success", "Category created successfully!");
            handleCancel();
        } else {
            showAlert("Error", "Failed to create category. Please try again.");
        }
    }

    @FXML
    private void handleCancel() {
        titleField.clear();
        descriptionField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}