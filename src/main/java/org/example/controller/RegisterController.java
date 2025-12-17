package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.model.User;
import org.example.service.UserService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField emailField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    // Use the singleton instance - SAME instance as LoginController
    private final UserService userService = UserService.getInstance();

    @FXML
    private void handleRegister() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);

        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();

        // Validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Username and password are required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        if (password.length() < 8) {
            showError("Password must be at least 6 characters");
            return;
        }

        if (email.isEmpty()) {
            showError("Email is required");
            return;
        }

        if (firstName.isEmpty()) {
            showError("First name is required");
            return;
        }

        try {
            // Generate ID (in real app, use database auto-increment)
            int newId = userService.getAllUsers().size() + 1;
            User newUser = new User(newId, username, password, email, firstName, lastName);

            userService.register(newUser);

            showSuccess("Registration successful! You can now login.");
            clearForm();

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("An unexpected error occurred: " + e.getMessage());
        }
    }

    @FXML
    public void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        emailField.clear();
        firstNameField.clear();
        lastNameField.clear();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
        successLabel.setStyle("-fx-text-fill: green;");
    }
}