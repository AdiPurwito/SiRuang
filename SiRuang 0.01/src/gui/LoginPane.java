package gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;
import app.Main;

public class LoginPane {
    private Stage stage;
    private VBox root;

    public LoginView(Stage stage) {
        this.stage = stage;
        initializeComponents();
    }

    private void initializeComponents() {
        root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(50));
        root.getStyleClass().add("root");

        // Title
        Label titleLabel = new Label("Cek Ruang & Jadwal Kuliah");
        titleLabel.getStyleClass().add("title-label");

        // Input fields
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("input-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("input-field");

        CheckBox showPasswordCheck = new CheckBox("Show Password");
        showPasswordCheck.getStyleClass().add("checkbox");

        // Login button
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("login-button");
        loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText()));

        // Footer
        Label footerLabel = new Label("©2025 Informatika");
        footerLabel.getStyleClass().add("footer-label");

        root.getChildren().addAll(
                titleLabel, usernameField, passwordField,
                showPasswordCheck, loginButton, footerLabel
        );
    }

    private void handleLogin(String username, String password) {
        User loggedInUser = null;

        for (User user : Main.users) {
            if (user.getUsername().equals(username) && user.checkPassword(password)) {
                loggedInUser = user;
                break;
            }
        }

        if (loggedInUser != null) {
            if (loggedInUser instanceof Admin) {
                AdminDashboard adminDashboard = new AdminDashboard(stage, (Admin) loggedInUser);
                adminDashboard.show();
            } else if (loggedInUser instanceof Mahasiswa) {
                MahasiswaDashboard mahasiswaDashboard = new MahasiswaDashboard(stage, (Mahasiswa) loggedInUser);
                mahasiswaDashboard.show();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Username atau password salah!");
            alert.showAndWait();
        }
    }

    public void show() {
        Scene scene = new Scene(root, 400, 500);
        scene.getStylesheets().add(getClass().getResource("/resources/css/login.css").toExternalForm());

        stage.setTitle("SiRuang - Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}