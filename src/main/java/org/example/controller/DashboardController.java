package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.model.User;
import org.example.service.UserService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    @FXML private Button homeBtn;
    @FXML private Button profileBtn;
    @FXML private Button logoutBtn;

    @FXML private Label welcomeLabel;
    @FXML private Label userEmailLabel;
    @FXML private Label dashboardWelcomeLabel;
    @FXML private Label statusLabel;
    @FXML private Label loginTimeLabel;

    @FXML private VBox homeContent;
    @FXML private VBox profileContent;

    @FXML private Label profileUsernameLabel;
    @FXML private Label profileEmailLabel;
    @FXML private Label profileNameLabel;

    private final UserService userService = UserService.getInstance();
    private final LocalDateTime loginTime = LocalDateTime.now();

    @FXML
    public void initialize() {
        // Load current user info
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            updateUserInfo(currentUser);
        }

        // Set login time
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        loginTimeLabel.setText("Logged in at: " + loginTime.format(formatter));

        // Set initial status
        statusLabel.setText("Dashboard loaded");

        // Show home by default
        showHome();
    }

    private void updateUserInfo(User user) {
        String welcomeText = "Welcome, " + user.getFirstName() + "!";
        welcomeLabel.setText(welcomeText);
        dashboardWelcomeLabel.setText(welcomeText);
        userEmailLabel.setText(user.getEmail());

        // Update profile labels
        profileUsernameLabel.setText("Username: " + user.getUsername());
        profileEmailLabel.setText("Email: " + user.getEmail());
        profileNameLabel.setText("Name: " + user.getFirstName() + " " + user.getLastName());
    }

    @FXML
    private void showHome() {
        // Show home content, hide profile
        homeContent.setVisible(true);
        profileContent.setVisible(false);

        // Update button styles
        homeBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 14px;");
        profileBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-size: 14px;");

        statusLabel.setText("Home page");
    }

    @FXML
    private void showProfile() {
        // Show profile content, hide home
        homeContent.setVisible(false);
        profileContent.setVisible(true);

        // Update button styles
        homeBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px;");
        profileBtn.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-font-size: 14px;");

        statusLabel.setText("Profile page");
    }

    @FXML
    private void handleLogout() {
        try {
            // Logout from service
            userService.logout();

            // Navigate back to login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) logoutBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to logout: " + e.getMessage());
        }
    }

    @FXML
    private void handleExit() {
        Stage stage = (Stage) logoutBtn.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void showProfilePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/profile.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) profileBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Profile Settings");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load profile page: " + e.getMessage());
        }
    }
}