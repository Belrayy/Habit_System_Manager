package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.model.User;
import org.example.service.UserService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.example.util.EmailUtil;
import javafx.application.Platform;

import java.io.IOException;

public class ProfileController {

    // Form Fields
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    // Labels and Messages
    @FXML private Label usernameLabel;
    @FXML private Label emailLabel;
    @FXML private Label nameLabel;
    @FXML private Label memberSinceLabel;
    @FXML private Label messageLabel;

    // Buttons
    @FXML private Button saveChangesBtn;
    @FXML private Button cancelBtn;
    @FXML private Button changePasswordBtn;
    @FXML private Button backToDashboardBtn;

    // Sections
    @FXML private VBox editProfileSection;
    @FXML private VBox changePasswordSection;

    private final UserService userService = UserService.getInstance();
    private User currentUser;

    //Limit rate email verification
    private static final long EMAIL_COOLDOWN_MS = 60 * 1000; // 5 minutes
    private static long lastEmailSentAt = 0;
    private static long lastTestEmailSentAt = 0;

    @FXML
    public void initialize() {
        // Load current user
        currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            showError("No user logged in");
            return;
        }

        // Populate display labels
        updateDisplayInfo();

        // Populate editable fields
        populateEditableFields();

        // Show edit profile section by default, hide password section
        showEditProfileSection();

        // Set up field listeners for real-time validation
        setupFieldValidation();
    }

    private void updateDisplayInfo() {
        usernameLabel.setText("@" + currentUser.getUsername());
        emailLabel.setText(currentUser.getEmail());
        nameLabel.setText(currentUser.getFirstName() + " " + currentUser.getLastName());
        memberSinceLabel.setText("Member since: Today"); // You can add registration date to User class
    }

    private void populateEditableFields() {
        // Don't allow username change (usually username should be immutable)
        usernameField.setText(currentUser.getUsername());
        usernameField.setDisable(true); // Username cannot be changed

        emailField.setText(currentUser.getEmail());
        firstNameField.setText(currentUser.getFirstName());
        lastNameField.setText(currentUser.getLastName());

        // Clear password fields
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    @FXML
    private void handleSaveChanges() {
        clearMessages();

        // Get updated values
        String email = emailField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();

        // Validation
        if (email.isEmpty()) {
            showError("Email cannot be empty");
            emailField.requestFocus();
            return;
        }

        if (firstName.isEmpty()) {
            showError("First name cannot be empty");
            firstNameField.requestFocus();
            return;
        }

        if (lastName.isEmpty()) {
            showError("Last name cannot be empty");
            lastNameField.requestFocus();
            return;
        }

        // Email format validation (basic)
        if (!email.contains("@") || !email.contains(".")) {
            showError("Please enter a valid email address");
            emailField.requestFocus();
            return;
        }

        try {
            // Update user object
            currentUser.setEmail(email);
            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);

            // Update in UserService
            userService.updateProfile(currentUser);

            // Update display info
            updateDisplayInfo();

            showSuccess("Profile updated successfully!");

        } catch (Exception e) {
            showError("Error updating profile: " + e.getMessage());
        }

        try {
            // Update user object
            currentUser.setEmail(email);
            currentUser.setFirstName(firstName);
            currentUser.setLastName(lastName);

            // Update in UserService
            userService.updateProfile(currentUser);

            // Update display info
            updateDisplayInfo();

            // Send confirmation email
            sendProfileUpdateEmail();

            showSuccess("Profile updated successfully! Confirmation email sent.");

        } catch (Exception e) {
            showError("Error updating profile: " + e.getMessage());
        }
    }

    @FXML
    private void handleChangePassword() {
        clearMessages();

        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation
        if (currentPassword.isEmpty()) {
            showError("Please enter your current password");
            currentPasswordField.requestFocus();
            return;
        }

        if (newPassword.isEmpty()) {
            showError("Please enter a new password");
            newPasswordField.requestFocus();
            return;
        }

        if (newPassword.length() < 6) {
            showError("New password must be at least 6 characters");
            newPasswordField.requestFocus();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("New passwords do not match");
            confirmPasswordField.requestFocus();
            return;
        }

        // Verify current password
        if (!currentUser.getPassword().equals(currentPassword)) {
            showError("Current password is incorrect");
            currentPasswordField.clear();
            currentPasswordField.requestFocus();
            return;
        }

        // Don't allow same password
        if (currentPassword.equals(newPassword)) {
            showError("New password must be different from current password");
            newPasswordField.clear();
            confirmPasswordField.clear();
            newPasswordField.requestFocus();
            return;
        }

        try {
            // Update password
            currentUser.setPasswordHash(newPassword);
            userService.updateProfile(currentUser);

            // Clear password fields
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();

            showSuccess("Password changed successfully!");
            showEditProfileSection(); // Switch back to profile section

        } catch (Exception e) {
            showError("Error changing password: " + e.getMessage());
        }
    }

    @FXML
    private void showEditProfileSection() {
        editProfileSection.setVisible(true);
        editProfileSection.setManaged(true);
        changePasswordSection.setVisible(false);
        changePasswordSection.setManaged(false);

        // Update button styles
        saveChangesBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        changePasswordBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");

        // Re-populate fields in case they were modified elsewhere
        populateEditableFields();
    }

    @FXML
    private void showChangePasswordSection() {
        editProfileSection.setVisible(false);
        editProfileSection.setManaged(false);
        changePasswordSection.setVisible(true);
        changePasswordSection.setManaged(true);

        // Update button styles
        saveChangesBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
        changePasswordBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        // Clear password fields and focus
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
        currentPasswordField.requestFocus();
    }

    @FXML
    private void handleCancel() {
        // Reset to original values
        populateEditableFields();
        clearMessages();
        showEditProfileSection();
    }

    @FXML
    private void handleBackToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) backToDashboardBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard - Habit System");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Cannot load dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteAccount() {
        // Confirmation dialog
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Account");
        alert.setHeaderText("Are you sure you want to delete your account?");
        alert.setContentText("This action cannot be undone. All your data will be permanently lost.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    String username = currentUser.getUsername();
                    userService.deleteUser(username);

                    // Navigate to login
                    navigateToLogin();

                } catch (Exception e) {
                    showError("Error deleting account: " + e.getMessage());
                }
            }
        });
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupFieldValidation() {
        // Email validation
        emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // When focus lost
                String email = emailField.getText().trim();
                if (!email.isEmpty() && (!email.contains("@") || !email.contains("."))) {
                    emailField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1px;");
                } else {
                    emailField.setStyle("");
                }
            }
        });

        // Password strength indicator (simple)
        newPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() < 6) {
                newPasswordField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1px;");
            } else if (newVal.length() < 8) {
                newPasswordField.setStyle("-fx-border-color: #f39c12; -fx-border-width: 1px;");
            } else {
                newPasswordField.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 1px;");
            }

            // Check if passwords match
            String confirm = confirmPasswordField.getText();
            if (!confirm.isEmpty() && !newVal.equals(confirm)) {
                confirmPasswordField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1px;");
            } else if (!confirm.isEmpty()) {
                confirmPasswordField.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 1px;");
            }
        });

        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            String newPass = newPasswordField.getText();
            if (!newVal.equals(newPass)) {
                confirmPasswordField.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 1px;");
            } else {
                confirmPasswordField.setStyle("-fx-border-color: #2ecc71; -fx-border-width: 1px;");
            }
        });
    }

    private void clearMessages() {
        messageLabel.setText("");
        messageLabel.setStyle("");
    }

    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
    }

    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
    }

    private void showInfo(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @FXML
    private void handleSendTestEmail(ActionEvent event) {

        long now = System.currentTimeMillis();

        // Rate limiting
        if (now - lastTestEmailSentAt < EMAIL_COOLDOWN_MS) {
            long remaining = (EMAIL_COOLDOWN_MS - (now - lastTestEmailSentAt)) / 1000;
            showInfo("Please wait " + remaining + " seconds before sending another test email.");
            return;
        }

        lastTestEmailSentAt = now;

        String subject = "Test Email from Habit System";
        String messageBody = """
        Hello %s,
        
        This is a test email sent from your Habit System profile.
        
        Your Profile Information:
        - Username: %s
        - Name: %s %s
        - Email: %s
        
        If you received this email, your email settings are working correctly!
        
        Thank you for using Habit System.
        
        Best regards,
        Habit System Team
        """.formatted(
                currentUser.getFirstName(),
                currentUser.getUsername(),
                currentUser.getFirstName(),
                currentUser.getLastName(),
                currentUser.getEmail()
        );

        new Thread(() -> {
            boolean sent = EmailUtil.sendEmail(
                    currentUser.getEmail(),
                    subject,
                    messageBody
            );

            if (sent) {
                showInfo("Test email sent successfully!");
            } else {
                showInfo("Failed to send test email. Please try again later.");
            }
        }).start();
    }


    // Add this method to send profile update confirmation
    private void sendProfileUpdateEmail() {
        String subject = "Profile Updated - Habit System";
        String messageBody = """
        Dear %s,
        
        Your profile has been successfully updated in Habit System.
        
        Updated Information:
        - Name: %s %s
        - Email: %s
        
        If you did not make these changes, please contact support immediately.
        
        Thank you,
        Habit System Team
        """.formatted(
                currentUser.getFirstName(),
                currentUser.getFirstName(),
                currentUser.getLastName(),
                currentUser.getEmail()
        );

        long now = System.currentTimeMillis();

        if (now - lastEmailSentAt < EMAIL_COOLDOWN_MS) {
            showInfo("A confirmation email was recently sent.\nPlease wait a few minutes before trying again.");
            return;
        }

        lastEmailSentAt = now;

        new Thread(() -> {
            boolean sent = EmailUtil.sendEmail(
                    currentUser.getEmail(),
                    subject,
                    messageBody
            );

            if (sent) {
                showInfo("A confirmation email has been sent to your email address.");
            } else {
                showInfo("Failed to send confirmation email. Please try again later.");
            }
        }).start();

    }
}