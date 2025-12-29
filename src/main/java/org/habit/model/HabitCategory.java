package org.habit.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.*;

public class HabitCategory {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final IntegerProperty progress = new SimpleIntegerProperty();
    private final List<Habit> habits = new ArrayList<>();

    public HabitCategory(int id, String title, String description) {
        this.id.set(id);
        this.title.set(title);
        this.description.set(description);
        this.progress.set(0);
    }

    //Property getters
    public IntegerProperty idProperty() { return id; }
    public StringProperty titleProperty() { return title; }
    public StringProperty descriptionProperty() { return description; }
    public IntegerProperty progressProperty() { return progress; }

    //Value getters
    public int getId() { return id.get(); }
    public String getTitle() { return title.get(); }
    public String getDescription() { return description.get(); }
    public int getProgress() { return progress.get(); }
    public List<Habit> getHabits() { return habits; }

    //Value setters
    public void SetId(int id){this.id.set(id);}
    public void SetTitle(String title){this.title.set(title);}
    public void SetDescription(String description){this.description.set(description);}
    public void SetProgress(int progress){this.progress.set(progress);}

    public void updateProgress() {
        if (habits.isEmpty()) {
            progress.set(0);
            return;
        }

        long completedCount = habits.stream()
                .filter(h -> h.isCompleted().get())
                .count();

        int percent = (int) ((completedCount * 100.0) / habits.size());
        progress.set(percent);
    }

    public int getHabitCount() {
        return habits.size();
    }

    public void addHabit(Habit habit) {
        if (habit.getCategoryId() != this.getId()) {
            throw new IllegalArgumentException("Habit does not belong to this category");
        }
        habits.add(habit);
        updateProgress();
    }

}
