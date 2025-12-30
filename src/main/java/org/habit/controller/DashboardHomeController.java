package org.habit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.habit.model.Habit;
import org.habit.model.HabitCategory;
import org.habit.model.User;
import org.habit.service.HabitCategoryService;
import org.habit.service.HabitService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardHomeController {

    @FXML private Label welcomeLabel;
    @FXML private Label dateLabel;
    @FXML private Label totalCategoriesLabel;
    @FXML private Label totalHabitsLabel;
    @FXML private Label completedTodayLabel;
    @FXML private Label totalProgressLabel;
    @FXML private GridPane categoriesGrid;
    @FXML private VBox recentHabitsContainer;
    @FXML private VBox noCategoriesMessage;
    @FXML private VBox noHabitsMessage;

    private User currentUser;
    private final HabitCategoryService categoryService = HabitCategoryService.getInstance();
    private final HabitService habitService = HabitService.getInstance();

    public void setUser(User user) {
        this.currentUser = user;
        updateUI();
    }

    @FXML
    public void initialize() {
        // Initialize date label
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        dateLabel.setText(LocalDate.now().format(dateFormatter));
    }

    private void updateUI() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome back, " + currentUser.getFirstName() + "!");
            loadCategories();
            loadRecentHabits();
            updateStatistics();
        }
    }

    private void loadCategories() {
        categoriesGrid.getChildren().clear();

        List<HabitCategory> categories = categoryService.getUserCategories();

        if (categories.isEmpty()) {
            noCategoriesMessage.setVisible(true);
            noCategoriesMessage.setManaged(true);
            return;
        }

        noCategoriesMessage.setVisible(false);
        noCategoriesMessage.setManaged(false);

        int row = 0;
        int col = 0;

        for (HabitCategory category : categories) {
            VBox categoryCard = createCategoryCard(category);

            // Add to grid
            categoriesGrid.add(categoryCard, col, row);

            // Update grid position
            col++;
            if (col >= 2) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createCategoryCard(HabitCategory category) {
        VBox card = new VBox();
        card.getStyleClass().add("category-card");
        card.setSpacing(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(300);

        // Card header
        HBox header = new HBox();
        header.setSpacing(10);
        header.setAlignment(Pos.CENTER_LEFT);

        // Progress circle
        StackPane progressCircle = new StackPane();
        progressCircle.getStyleClass().add("progress-circle");
        progressCircle.setMinSize(60, 60);

        Label progressLabel = new Label(category.getProgress() + "%");
        progressLabel.getStyleClass().add("progress-text");

        // Set circle color based on progress
        int progress = category.getProgress();
        String color;
        if (progress >= 80) {
            color = "#27ae60"; // Green
        } else if (progress >= 50) {
            color = "#f39c12"; // Orange
        } else {
            color = "#e74c3c"; // Red
        }
        progressCircle.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 30;");

        progressCircle.getChildren().add(progressLabel);

        // Category info
        VBox info = new VBox(5);

        Label titleLabel = new Label(category.getTitle());
        titleLabel.getStyleClass().add("category-title");
        titleLabel.setWrapText(true);

        String description = category.getDescription();
        Label descLabel = new Label(description != null && !description.isEmpty() ? description : "No description");
        descLabel.getStyleClass().add("category-description");
        descLabel.setWrapText(true);
        descLabel.setMaxHeight(40);

        info.getChildren().addAll(titleLabel, descLabel);

        header.getChildren().addAll(progressCircle, info);

        // Habits list
        List<Habit> habits = habitService.getHabitsByCategory(category.getId());
        VBox habitsList = new VBox(5);
        habitsList.setPadding(new Insets(10, 0, 0, 0));

        Label habitsLabel = new Label("Habits (" + habits.size() + "):");
        habitsLabel.getStyleClass().add("habits-label");
        habitsList.getChildren().add(habitsLabel);

        if (habits.isEmpty()) {
            Label noHabitsLabel = new Label("No habits yet");
            noHabitsLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
            habitsList.getChildren().add(noHabitsLabel);
        } else {
            for (Habit habit : habits) {
                HBox habitRow = createHabitRow(habit);
                habitsList.getChildren().add(habitRow);
            }
        }

        // Actions
        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER_RIGHT);

        Button viewButton = new Button("View");
        viewButton.getStyleClass().addAll("btn-small", "btn-primary");
        viewButton.setOnAction(e -> viewCategory(category));

        Button addHabitButton = new Button("Add Habit");
        addHabitButton.getStyleClass().addAll("btn-small", "btn-success");
        addHabitButton.setOnAction(e -> addHabitToCategory(category));

        actions.getChildren().addAll(viewButton, addHabitButton);

        card.getChildren().addAll(header, new Separator(), habitsList, actions);

        // Add hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #2980b9; -fx-border-width: 2;");
        });

        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-border-color: #ecf0f1;");
        });

        return card;
    }

    private HBox createHabitRow(Habit habit) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(habit.getCompleted());
        checkBox.setOnAction(e -> toggleHabitCompletion(habit, checkBox.isSelected()));

        VBox info = new VBox(2);

        Label title = new Label(habit.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        HBox details = new HBox(10);

        Label progress = new Label(habit.getProgress() + "/" + habit.getTarget());
        progress.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");

        Label streak = new Label("🔥 " + habit.getStreak());
        streak.setStyle("-fx-font-size: 11px; -fx-text-fill: #e74c3c;");

        details.getChildren().addAll(progress, streak);
        info.getChildren().addAll(title, details);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(100);
        double progressValue = (double) habit.getProgress() / habit.getTarget();
        progressBar.setProgress(progressValue);

        // Color based on progress
        if (progressValue >= 1.0) {
            progressBar.setStyle("-fx-accent: #27ae60;");
        } else if (progressValue >= 0.5) {
            progressBar.setStyle("-fx-accent: #f39c12;");
        } else {
            progressBar.setStyle("-fx-accent: #e74c3c;");
        }

        row.getChildren().addAll(checkBox, info, progressBar);
        return row;
    }

    private void loadRecentHabits() {
        recentHabitsContainer.getChildren().clear();

        List<Habit> habits = habitService.getUserHabits();

        if (habits.isEmpty()) {
            noHabitsMessage.setVisible(true);
            noHabitsMessage.setManaged(true);
            return;
        }

        noHabitsMessage.setVisible(false);
        noHabitsMessage.setManaged(false);

        // Get recent habits (last 5)
        List<Habit> recentHabits = habits.size() > 5 ?
                habits.subList(0, Math.min(5, habits.size())) : habits;

        for (Habit habit : recentHabits) {
            HBox habitCard = createRecentHabitCard(habit);
            recentHabitsContainer.getChildren().add(habitCard);
        }
    }

    private HBox createRecentHabitCard(Habit habit) {
        HBox card = new HBox(15);
        card.getStyleClass().add("habit-card");
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER_LEFT);

        // Completion indicator
        StackPane indicator = new StackPane();
        indicator.setMinSize(30, 30);
        indicator.getStyleClass().add("completion-indicator");

        if (habit.getCompleted()) {
            indicator.setStyle("-fx-background-color: #27ae60; -fx-background-radius: 15;");
            Label check = new Label("✓");
            check.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
            indicator.getChildren().add(check);
        } else {
            indicator.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 15;");
        }

        // Habit info
        VBox info = new VBox(3);

        Label title = new Label(habit.getTitle());
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        HBox details = new HBox(10);

        Label categoryLabel = new Label(getCategoryName(habit.getCategoryId()));
        categoryLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");

        Label progressLabel = new Label(habit.getProgress() + "/" + habit.getTarget());
        progressLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #3498db;");

        Label streakLabel = new Label("🔥 " + habit.getStreak() + " days");
        streakLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #e74c3c;");

        details.getChildren().addAll(categoryLabel, progressLabel, streakLabel);
        info.getChildren().addAll(title, details);

        // Progress bar
        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(150);
        double progressValue = (double) habit.getProgress() / habit.getTarget();
        progressBar.setProgress(progressValue);

        // Quick actions
        HBox actions = new HBox(5);

        Button incrementBtn = new Button("+1");
        incrementBtn.getStyleClass().addAll("btn-circle", "btn-success");
        incrementBtn.setOnAction(e -> incrementHabitProgress(habit));

        Button viewBtn = new Button("View");
        viewBtn.getStyleClass().addAll("btn-small", "btn-outline");
        viewBtn.setOnAction(e -> viewHabit(habit));

        actions.getChildren().addAll(incrementBtn, viewBtn);

        card.getChildren().addAll(indicator, info, progressBar, actions);
        return card;
    }

    private void updateStatistics() {
        List<HabitCategory> categories = categoryService.getUserCategories();
        List<Habit> habits = habitService.getUserHabits();

        // Count completed habits today
        long completedToday = habits.stream()
                .filter(habit -> habit.getLastCompleted() != null)
                .filter(habit -> habit.getLastCompleted().isEqual(LocalDate.now()))
                .count();

        // Calculate overall progress
        double totalProgress = 0;
        for (Habit habit : habits) {
            double habitProgress = (double) habit.getProgress() / habit.getTarget();
            totalProgress += habitProgress;
        }

        int avgProgress = habits.isEmpty() ? 0 : (int) ((totalProgress / habits.size()) * 100);

        totalCategoriesLabel.setText(String.valueOf(categories.size()));
        totalHabitsLabel.setText(String.valueOf(habits.size()));
        completedTodayLabel.setText(String.valueOf(completedToday));
        totalProgressLabel.setText(avgProgress + "%");
    }

    private String getCategoryName(int categoryId) {
        List<HabitCategory> categories = categoryService.getUserCategories();
        return categories.stream()
                .filter(c -> c.getId() == categoryId)
                .map(HabitCategory::getTitle)
                .findFirst()
                .orElse("Unknown");
    }

    @FXML
    private void createCategory() {
        // This will be handled by the main DashboardController
        System.out.println("Navigate to create category");
    }

    @FXML
    private void createHabit() {
        // This will be handled by the main DashboardController
        System.out.println("Navigate to create habit");
    }


    private void viewCategory(HabitCategory category) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/category_detail.fxml"));
            Parent view = loader.load();

            CategoryDetailController controller = loader.getController();
            controller.setCategory(category);

            // Get the main content area
            BorderPane contentArea = (BorderPane) welcomeLabel.getScene().lookup("#contentArea");
            if (contentArea != null) {
                contentArea.setCenter(view);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addHabitToCategory(HabitCategory category) {
        // Navigate to create habit with pre-selected category
        System.out.println("Add habit to category: " + category.getTitle());
    }

    private void toggleHabitCompletion(Habit habit, boolean completed) {
        habit.setCompleted(completed);
        if (completed) {
            habit.setLastCompleted(LocalDate.now());
        }
        updateStatistics();
    }

    private void incrementHabitProgress(Habit habit) {
        habit.incrementProgress();
        updateStatistics();
        loadRecentHabits();
    }

    private void viewHabit(Habit habit) {
        System.out.println("View habit: " + habit.getTitle());
        // Implement habit detail view
    }
}