package org.habit.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.habit.service.UserService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserService userService = UserService.getInstance();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please fill all fields");
            return;
        }

        try {
            // Attempt login
            userService.login(username, password);

            // Clear error
            errorLabel.setVisible(false);

            // Navigate to DASHBOARD
            navigateToDashboard();

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard - Habit System");
            stage.setMinWidth(850);
            stage.setMinHeight(650);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Cannot load dashboard: " + e.getMessage());
        }
    }

    @FXML
    public void goToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/register.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Register");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Cannot load registration page");
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}