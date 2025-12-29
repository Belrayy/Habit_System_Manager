package org.habit.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.habit.model.Habit;
import org.habit.service.HabitService;
import java.time.format.DateTimeFormatter;

public class HabitCardController {

    @FXML private HBox root;
    @FXML private Label titleLabel;
    @FXML private Label streakLabel;
    @FXML private Label progressText;
    @FXML private Label lastCompletedLabel;
    @FXML private ProgressBar progressBar;
    @FXML private CheckBox completionCheckBox;

    private Habit habit;
    private final HabitService habitService = HabitService.getInstance();

    public void setHabit(Habit habit) {
        this.habit = habit;
        updateUI();
    }

    private void updateUI() {
        if (habit != null) {
            titleLabel.setText(habit.getTitle());
            streakLabel.setText("🔥 " + habit.getStreak());

            int progress = habit.getProgress();
            int target = habit.getTarget();

            progressText.setText(progress + "/" + target);

            double progressPercentage = (double) progress / target;
            progressBar.setProgress(progressPercentage);

            // Update progress bar color
            if (progressPercentage >= 1.0) {
                progressBar.setStyle("-fx-accent: #27ae60;"); // Green
                completionCheckBox.setSelected(true);
            } else if (progressPercentage >= 0.5) {
                progressBar.setStyle("-fx-accent: #f39c12;"); // Orange
                completionCheckBox.setSelected(false);
            } else {
                progressBar.setStyle("-fx-accent: #e74c3c;"); // Red
                completionCheckBox.setSelected(false);
            }

            // Update last completed
            if (habit.getLastCompleted() != null) {
                lastCompletedLabel.setText("Last: " +
                        habit.getLastCompleted().format(DateTimeFormatter.ofPattern("MMM d")));
            } else {
                lastCompletedLabel.setText("Never completed");
                lastCompletedLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
            }
        }
    }

    @FXML
    private void handleIncrement() {
        if (habit != null) {
            habit.incrementProgress();
            updateUI();

            // Update in database
            // We'll need to add an update method to HabitService
            System.out.println("Incremented progress for: " + habit.getTitle());
        }
    }

    @FXML
    private void handleCompletionToggle() {
        if (habit != null) {
            boolean completed = completionCheckBox.isSelected();
            habit.setCompleted(completed);

            if (completed) {
                // Mark as completed for today
                habit.setLastCompleted(java.time.LocalDate.now());
            }

            updateUI();
        }
    }

    @FXML
    private void handleEdit() {
        System.out.println("Edit habit: " + habit.getTitle());
    }

    @FXML
    private void handleDelete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Habit");
        alert.setHeaderText("Delete " + habit.getTitle() + "?");
        alert.setContentText("Are you sure you want to delete this habit? This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("Delete habit: " + habit.getTitle());
                // We'll add delete functionality to HabitService
            }
        });
    }
}