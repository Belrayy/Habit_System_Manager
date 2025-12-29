package org.habit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.habit.model.User;
import org.habit.service.UserService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    @FXML private Button homeBtn;
    @FXML private Button profileBtn;
    @FXML private Button logoutBtn;
    @FXML private Label welcomeLabel;
    @FXML private Label userEmailLabel;
    @FXML private Label statsLabel;
    @FXML private Label statusLabel;
    @FXML private Label loginTimeLabel;
    @FXML private BorderPane contentArea;

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
    }

    @FXML
    public void showHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard_home.fxml"));
            Parent homeView = loader.load();

            DashboardHomeController homeController = loader.getController();
            homeController.setUser(userService.getCurrentUser());

            contentArea.setCenter(homeView);

            // Update button styles
            homeBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold;");
            profileBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");

            statusLabel.setText("Home page loaded");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load dashboard: " + e.getMessage());
        }
    }

    @FXML
    public void refreshDashboard() {
        showHome();
        statusLabel.setText("Dashboard refreshed at " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    @FXML
    private void goToProfile() {
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

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setHeaderText("Are you sure you want to logout?");
        confirm.setContentText("Your progress will be saved automatically.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userService.logout();

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
        });
    }

    @FXML
    private void openCreateCategory() {
        loadPage("/view/create_category.fxml");
    }

    @FXML
    private void openCreateHabit() {
        loadPage("/view/create_habit.fxml");
    }

    private void loadPage(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            contentArea.setCenter(view);

            String pageName = fxmlPath.substring(fxmlPath.lastIndexOf('/') + 1);
            statusLabel.setText("Loaded: " + pageName.replace(".fxml", "").replace("_", " "));

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load page: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}