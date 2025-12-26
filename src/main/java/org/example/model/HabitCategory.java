package org.example.model;

import java.util.ArrayList;
import java.util.List;
import org.example.model.Habit;
import org.example.model.User;
import org.example.model.Habit;
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


}
