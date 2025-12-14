package org.example;

import org.example.model.User;
import org.example.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/login.fxml")
        );

        Scene scene = new Scene(loader.load());
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Quick test
        UserService service = UserService.getInstance();

        // Test user
        User testUser = new User(1, "test", "test123", "test@test.com", "Test", "User");

        try {
            service.register(testUser);
            System.out.println("Test user registered");

            User loggedIn = service.login("test", "test123");
            System.out.println("Test login successful: " + loggedIn.getUsername());
        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
        }
        launch(args);
    }
}