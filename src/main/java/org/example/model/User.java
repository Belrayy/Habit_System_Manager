package org.example.model;

import javafx.beans.property.*;

public class User {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty password = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty firstName = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();

    public User(int id, String username, String password, String email, String firstName, String lastName) {
        this.id.set(id);
        this.username.set(username);
        this.password.set(password);
        this.email.set(email);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
    }

    // Property getters (return the property object)
    public StringProperty usernameProperty() { return username; }
    public StringProperty passwordProperty() { return password; }
    public StringProperty emailProperty() { return email; }
    public StringProperty firstNameProperty() { return firstName; }
    public StringProperty lastNameProperty() { return lastName; }
    public IntegerProperty idProperty() { return id; }

    // String value getters
    public String getUsername() { return username.get(); }
    public String getPassword() { return password.get(); }
    public String getEmail() { return email.get(); }
    public String getFirstName() { return firstName.get(); }
    public String getLastName() { return lastName.get(); }
    public int getId() { return id.get(); }

    // String value setters
    public void setUsername(String username) { this.username.set(username); }
    public void setPassword(String password) { this.password.set(password); }
    public void setEmail(String email) { this.email.set(email); }
    public void setFirstName(String firstName) { this.firstName.set(firstName); }
    public void setLastName(String lastName) { this.lastName.set(lastName); }
    public void setId(int id) { this.id.set(id); }

    // StringProperty setters (for your updateProfile method)
    public void setEmail(StringProperty email) {
        if (email != null) this.email.bindBidirectional(email);
    }

    public void setFirstName(StringProperty firstName) {
        if (firstName != null) this.firstName.bindBidirectional(firstName);
    }

    public void setLastName(StringProperty lastName) {
        if (lastName != null) this.lastName.bindBidirectional(lastName);
    }
}