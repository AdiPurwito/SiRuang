package gui;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;

public class LoginPane {
    private Stage stage;

    public LoginPane(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // Title
        Label icon = new Label("\uD83D\uDCC5"); // 🗓 Emoji ikon
        icon.setStyle("-fx-font-size: 40px; -fx-text-fill: white;");

        Label title = new Label("Cek Ruang & Jadwal Kuliah");
        title.getStyleClass().add("label-title");

        VBox titleBox = new VBox(5, icon, title);
        titleBox.setAlignment(Pos.CENTER);

        // Input Fields
        TextField username = new TextField();
        username.setPromptText("Username");
        username.getStyleClass().add("text-field");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.getStyleClass().add("password-field");

        // Show password toggle
        CheckBox showPass = new CheckBox("Show Password");
        showPass.setStyle("-fx-text-fill: white;");
        TextField plainPass = new TextField();
        plainPass.managedProperty().bind(showPass.selectedProperty());
        plainPass.visibleProperty().bind(showPass.selectedProperty());
        plainPass.textProperty().bindBidirectional(password.textProperty());
        plainPass.getStyleClass().add("text-field");

        password.managedProperty().bind(showPass.selectedProperty().not());
        password.visibleProperty().bind(showPass.selectedProperty().not());

        // Error label
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 12px;");
        errorLabel.setVisible(false);

        // Login Button
        Button btnLogin = new Button("Login");
        btnLogin.getStyleClass().add("button-login");
        btnLogin.setOnAction(e -> handleLogin(username.getText(), password.getText(), errorLabel));

        // Enter key support
        password.setOnAction(e -> handleLogin(username.getText(), password.getText(), errorLabel));
        plainPass.setOnAction(e -> handleLogin(username.getText(), password.getText(), errorLabel));

        VBox loginBox = new VBox(10, username, password, plainPass, showPass, errorLabel, btnLogin);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.getStyleClass().add("login-box");

        // Footer
        Label footer = new Label("©2025 Informatika");
        footer.getStyleClass().add("label-footer");

        // Instructions
        Label instructions = new Label("Default users:\nAdmin: admin/123\nMahasiswa: mahasiswa/123");
        instructions.setStyle("-fx-text-fill: rgba(255,255,255,0.7); -fx-font-size: 10px; -fx-text-alignment: center;");

        VBox root = new VBox(20, titleBox, loginBox, instructions, footer);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setPrefSize(500, 500);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/login.css").toExternalForm());

        stage.setTitle("SiRuang Login");
        stage.setScene(scene);
        stage.show();
    }

    private void handleLogin(String usernameText, String passwordText, Label errorLabel) {
        errorLabel.setVisible(false);

        if (usernameText.trim().isEmpty() || passwordText.trim().isEmpty()) {
            showError(errorLabel, "Username dan password harus diisi!");
            return;
        }

        // Get users from Main class
        var users = Main.getUsers();
        User loggedInUser = null;

        for (User user : users) {
            if (user.getUsername().equals(usernameText) && user.cekPassword(passwordText)) {
                loggedInUser = user;
                break;
            }
        }

        if (loggedInUser != null) {
            // Login berhasil - buka dashboard sesuai role
            if (loggedInUser instanceof Admin) {
                openAdminDashboard((Admin) loggedInUser);
            } else if (loggedInUser instanceof Mahasiswa) {
                openMahasiswaDashboard((Mahasiswa) loggedInUser);
            }
        } else {
            showError(errorLabel, "Username atau password salah!");
        }
    }

    private void showError(Label errorLabel, String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void openAdminDashboard(Admin admin) {
        // TODO: Implement AdminDashboard GUI
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Login Berhasil");
        alert.setHeaderText("Selamat datang, " + admin.getUsername() + "!");
        alert.setContentText("Dashboard Admin GUI belum diimplementasi.\nSilakan gunakan mode CLI dengan argument -cli");
        alert.showAndWait();
    }

    private void openMahasiswaDashboard(Mahasiswa mahasiswa) {
        // TODO: Implement MahasiswaDashboard GUI
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Login Berhasil");
        alert.setHeaderText("Selamat datang, " + mahasiswa.getUsername() + "!");
        alert.setContentText("Dashboard Mahasiswa GUI belum diimplementasi.\nSilakan gunakan mode CLI dengan argument -cli");
        alert.showAndWait();
    }
}