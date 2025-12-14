package org.example.controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.service.UserService;

import java.io.IOException;


public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin() {

        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isEmpty()
                || password == null || password.isEmpty()) {
            showError("Please fill all fields");
            return;
        }

        try {
            userService.login(username, password);
            errorLabel.setVisible(false);
            System.out.println("Login successful");

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    public void goToRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/register.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Register");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}

