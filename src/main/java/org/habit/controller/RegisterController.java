package org.habit.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import org.habit.model.User;
import org.habit.service.UserService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField emailField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    // Email validation regex pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private final UserService userService = UserService.getInstance();

    @FXML
    public void initialize() {
        // Setup email field validation
        setupEmailValidation();

        // Add focus listeners for better UX
        emailField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // When focus is lost
                validateEmailField();
            }
        });
    }

    private void setupEmailValidation() {
        // Add text formatter to prevent invalid characters
        TextFormatter<String> emailFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();

            // Allow empty text or control changes
            if (newText.isEmpty() || change.isDeleted()) {
                return change;
            }

            // Allow only valid email characters: letters, numbers, @, ., _, -, +
            if (newText.matches("^[A-Za-z0-9@.+_-]*$")) {
                // Limit length to reasonable email size
                if (newText.length() <= 100) {
                    return change;
                }
            }

            // Reject invalid change
            return null;
        });

        emailField.setTextFormatter(emailFormatter);

        // Add real-time visual feedback
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateEmailFieldStyle(newValue);
        });
    }

    private void updateEmailFieldStyle(String email) {
        if (email == null || email.trim().isEmpty()) {
            emailField.setStyle("");
        } else if (isValidEmail(email)) {
            emailField.setStyle("-fx-border-color: #27ae60; -fx-border-width: 1px; -fx-border-radius: 5;");
        } else {
            emailField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1px; -fx-border-radius: 5;");
        }
    }

    private void validateEmailField() {
        String email = emailField.getText().trim();

        if (!email.isEmpty() && !isValidEmail(email)) {
            // Add a subtle shake effect for invalid email
            emailField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px; -fx-border-radius: 5;");
        }
    }

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
        StringBuilder validationErrors = new StringBuilder();

        if (username.isEmpty()) {
            validationErrors.append("Username is required\n");
            usernameField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1px;");
        } else {
            usernameField.setStyle("");
        }

        if (password.isEmpty()) {
            validationErrors.append("Password is required\n");
            passwordField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1px;");
        } else if (password.length() < 6) {
            validationErrors.append("Password must be at least 6 characters\n");
            passwordField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1px;");
        } else {
            passwordField.setStyle("");
        }

        if (confirmPassword.isEmpty()) {
            validationErrors.append("Please confirm your password\n");
            confirmPasswordField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1px;");
        } else if (!password.equals(confirmPassword)) {
            validationErrors.append("Passwords do not match\n");
            confirmPasswordField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1px;");
        } else {
            confirmPasswordField.setStyle("");
        }

        if (email.isEmpty()) {
            validationErrors.append("Email is required\n");
            emailField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
        } else if (!isValidEmail(email)) {
            validationErrors.append("Please enter a valid email address\n");
            emailField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px;");
        } else {
            emailField.setStyle("");
        }

        if (firstName.isEmpty()) {
            validationErrors.append("First name is required\n");
            firstNameField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1px;");
        } else {
            firstNameField.setStyle("");
        }

        if (lastName.isEmpty()) {
            validationErrors.append("Last name is required\n");
            lastNameField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1px;");
        } else {
            lastNameField.setStyle("");
        }

        // If there are validation errors, show them and return
        if (validationErrors.length() > 0) {
            showError(validationErrors.toString().trim());
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
        usernameField.setStyle("");
        passwordField.clear();
        passwordField.setStyle("");
        confirmPasswordField.clear();
        confirmPasswordField.setStyle("");
        emailField.clear();
        emailField.setStyle("");
        firstNameField.clear();
        firstNameField.setStyle("");
        lastNameField.clear();
        lastNameField.setStyle("");
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
    }

    @FXML
    private void onEmailKeyTyped(KeyEvent event) {
        // Real-time validation as user types
        String email = emailField.getText();
        updateEmailFieldStyle(email);
    }

    private boolean isValidEmail(String email) {
        // Trim whitespace
        email = email.trim();

        // Basic validation
        if (email.length() < 5 || email.length() > 100) {
            return false;
        }

        // Check for @ symbol
        int atIndex = email.indexOf('@');
        if (atIndex < 1 || atIndex == email.length() - 1) {
            return false;
        }

        // Check for domain dot
        int dotIndex = email.lastIndexOf('.');
        if (dotIndex <= atIndex + 1 || dotIndex == email.length() - 1) {
            return false;
        }

        // Check for spaces
        if (email.contains(" ")) {
            return false;
        }

        // Check for consecutive dots
        if (email.contains("..")) {
            return false;
        }

        // Check for invalid characters
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            return false;
        }

        // Additional checks for common email issues
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return false;
        }

        String localPart = parts[0];
        String domain = parts[1];

        // Local part validation
        if (localPart.startsWith(".") || localPart.endsWith(".")) {
            return false;
        }

        // Domain validation
        if (domain.startsWith(".") || domain.endsWith(".")) {
            return false;
        }

        String[] domainParts = domain.split("\\.");
        if (domainParts.length < 2) {
            return false;
        }

        // Check TLD (top-level domain)
        String tld = domainParts[domainParts.length - 1];
        if (tld.length() < 2) {
            return false;
        }

        return true;
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        successLabel.setVisible(false);
        errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        successLabel.setVisible(true);
        errorLabel.setVisible(false);
        successLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
    }
}