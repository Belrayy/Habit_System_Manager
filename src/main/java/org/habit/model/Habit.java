package org.habit.model;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Habit {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty categoryId = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final IntegerProperty progress = new SimpleIntegerProperty();
    private final IntegerProperty streak = new SimpleIntegerProperty();
    private final IntegerProperty target = new SimpleIntegerProperty(1); // Default target is 1
    private final ObjectProperty<LocalDate> lastCompleted = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> createdAt = new SimpleObjectProperty<>(LocalDate.now());
    private final BooleanProperty completed = new SimpleBooleanProperty(false);

    public Habit(int id, int categoryId, String title, String description, int progress, int streak) {
        setId(id);
        setCategoryId(categoryId);
        setTitle(title);
        setDescription(description);
        setProgress(progress);
        setStreak(streak);
    }

    public Habit(String title, String description, int categoryId, int target) {
        setTitle(title);
        setDescription(description);
        setCategoryId(categoryId);
        setTarget(target);
        setProgress(0);
        setStreak(0);
        setCompleted(false);
    }

    public void setId(Integer id) {this.id.set(id);}
    public void setCategoryId(int categoryId) {this.categoryId.set(categoryId);}
    public void setTitle(String title) {this.title.set(title);}
    public void setDescription(String description) {this.description.set(description);}
    public void setProgress(Integer progress) {this.progress.set(progress);}
    public void setStreak(Integer streak) {this.streak.set(streak);}
    public void setCompleted(boolean completed) {this.completed.set(completed);}
    public void setTarget(int target) { this.target.set(target); }
    public void setLastCompleted(LocalDate date) { this.lastCompleted.set(date); }
    public void setCreatedAt(LocalDate date) { this.createdAt.set(date); }


    public Integer getId() {return this.id.get();}
    public int getCategoryId() {return categoryId.get();}
    public String getTitle() {return this.title.get();}
    public String getDescription() {return this.description.get();}
    public Integer getProgress() {return this.progress.get();}
    public Integer getStreak() {return this.streak.get();}
    public BooleanProperty isCompleted() {return this.completed;}
    public Integer getTarget() {return this.target.get();}
    public IntegerProperty targetProperty() { return target; }
    public ObjectProperty<LocalDate> lastCompletedProperty() { return lastCompleted; }
    public LocalDate getLastCompleted() { return lastCompleted.get(); }
    public ObjectProperty<LocalDate> createdAtProperty() { return createdAt; }
    public LocalDate getCreatedAt() { return createdAt.get(); }
    public BooleanProperty completedProperty() { return completed; }
    public boolean getCompleted() { return completed.get(); }

    public void incrementProgress() {
        this.progress.set(this.progress.get() + 1);
        this.completed.set(this.progress.get() >= this.target.get());
    }

}
