package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.model.User;
import org.example.service.UserService;

import java.io.IOException;

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private Label errorLabel;

    @FXML
    private Label successLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleRegister() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);

        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Username and password are required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        if (password.length() < 6) {
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
            User newUser = new User(0, username, password, email, firstName, lastName);

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
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
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