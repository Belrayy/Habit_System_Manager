package org.example.model;

import java.util.ArrayList;
import java.util.List;
import org.example.model.Habit;
import org.example.model.User;
import javafx.beans.property.*;

public class HabitCategory {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final IntegerProperty progress = new SimpleIntegerProperty();
}
