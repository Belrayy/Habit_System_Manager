package org.habit.model;

import javafx.beans.property.*;
import org.habit.util.PasswordUtil;

public class User {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty passwordHash = new SimpleStringProperty();  // Renamed from password
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty firstName = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();

    // Transient field for plain password (not stored in DB)
    private transient String plainPassword;

    public User(int id, String username, String plainPassword, String email,
                String firstName, String lastName) {
        this.id.set(id);
        this.username.set(username);
        setPlainPassword(plainPassword);  // Hash the password and store hash
        this.email.set(email);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
    }

    // Constructor for loading from DB (with hash directly)
    public User(int id, String username, String passwordHash, String email,
                String firstName, String lastName, boolean fromDatabase) {
        this.id.set(id);
        this.username.set(username);
        this.passwordHash.set(passwordHash);  // Directly set hash
        this.email.set(email);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
    }

    // Property getters (return the property object)
    public StringProperty usernameProperty() { return username; }
    public StringProperty passwordHashProperty() { return passwordHash; }
    public StringProperty emailProperty() { return email; }
    public StringProperty firstNameProperty() { return firstName; }
    public StringProperty lastNameProperty() { return lastName; }
    public IntegerProperty idProperty() { return id; }

    // String value getters
    public String getUsername() { return username.get(); }
    public String getPasswordHash() { return passwordHash.get(); }
    public String getEmail() { return email.get(); }
    public String getFirstName() { return firstName.get(); }
    public String getLastName() { return lastName.get(); }
    public int getId() { return id.get(); }

    // Get plain password (for temporary use)
    public String getPlainPassword() { return plainPassword; }

    // String value setters
    public void setUsername(String username) { this.username.set(username); }
    public void setPasswordHash(String passwordHash) { this.passwordHash.set(passwordHash); }
    public void setEmail(String email) { this.email.set(email); }
    public void setFirstName(String firstName) { this.firstName.set(firstName); }
    public void setLastName(String lastName) { this.lastName.set(lastName); }
    public void setId(int id) { this.id.set(id); }

    // Set plain password and auto-hash it
    public void setPlainPassword(String plainPassword) {
        this.plainPassword = plainPassword;
        if (plainPassword != null && !plainPassword.trim().isEmpty()) {
            String hash = PasswordUtil.hashPassword(plainPassword);
            this.passwordHash.set(hash);
        }
    }

    // For backward compatibility (use cautiously)
    @Deprecated
    public String getPassword() {
        return getPasswordHash();
    }

    @Deprecated
    public void setPassword(String password) {
        setPlainPassword(password);
    }

    // Password verification method
    public boolean verifyPassword(String plainPassword) {
        return PasswordUtil.verifyPassword(plainPassword, this.passwordHash.get());
    }

    // Check if password needs rehashing
    public boolean needsPasswordRehash() {
        return PasswordUtil.needsRehash(this.passwordHash.get());
    }

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

    // Additional helper methods
    public void clearPlainPassword() {
        this.plainPassword = null;
    }

    public boolean hasPlainPassword() {
        return plainPassword != null;
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, username=%s, email=%s]",
                getId(), getUsername(), getEmail());
    }
}