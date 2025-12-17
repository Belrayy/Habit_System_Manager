package org.example.model;

import org.example.model.HabitCategory;
import org.example.model.User;
import javafx.beans.property.*;

public class Habit {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final IntegerProperty progress = new SimpleIntegerProperty();
    private final IntegerProperty streak = new SimpleIntegerProperty();
    private boolean completed;
    private HabitCategory HabitCategory;


    public Habit(int id, String title, String description, int progress, int streak,HabitCategory HabitCategory) {
        setId(id);
        setTitle(title);
        setDescription(description);
        setProgress(progress);
        setStreak(streak);
        setCompleted(false);
        setHabitCategory(HabitCategory);
    }

    public void setId(Integer id) {this.id.set(id);}
    public void setTitle(String title) {this.title.set(title);}
    public void setDescription(String description) {this.description.set(description);}
    public void setProgress(Integer progress) {this.progress.set(progress);}
    public void setStreak(Integer streak) {this.streak.set(streak);}
    public void setCompleted(boolean completed) {this.completed=completed;}
    public void setHabitCategory(HabitCategory habitCategory) {this.HabitCategory=habitCategory;}



    public Integer getId() {return this.id.get();}
    public String getTitle() {return this.title.get();}
    public String getDescription() {return this.description.get();}
    public Integer getProgress() {return this.progress.get();}
    public Integer getStreak() {return this.streak.get();}
    public boolean isCompleted() {return this.completed;}
    public HabitCategory getHabitCategory() {return this.HabitCategory;}

}
