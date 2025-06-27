package gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.*;
import app.Main;
import controller.NotifikasiController;
import util.TimeUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AdminDashboard {
    private Stage stage;
    private Admin admin;
    private BorderPane root;
    private TableView<BookingTableData> bookingTable;
    private TableView<JadwalTableData> jadwalTable;
    private Label notificationLabel;
    private Timer updateTimer;

    public AdminDashboard(Stage stage, Admin admin) {
        this.stage = stage;
        this.admin = admin;
        initializeComponents();
        startUpdateTimer();
    }

    private void initializeComponents() {
        root = new BorderPane();
        root.getStyleClass().add("root");

        // Top Section
        createTopSection();

        // Center Section - TabPane
        createCenterSection();

        updateNotifications();
    }

    private void createTopSection() {
        HBox topBox = new HBox();
        topBox.setPadding(new Insets(20));
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.getStyleClass().add("header-box");

        Label titleLabel = new Label("Dashboard Admin");
        titleLabel.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox rightBox = new HBox(15);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        notificationLabel = new Label();
        notificationLabel.getStyleClass().add("notification-label");

        Button logoutButton = new Button("Logout");
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setOnAction(e -> handleLogout());

        rightBox.getChildren().addAll(notificationLabel, logoutButton);

        topBox.getChildren().addAll(titleLabel, spacer, rightBox);
        root.setTop(topBox);
    }

    private void createCenterSection() {
        TabPane tabPane = new TabPane();
        tabPane.getStyleClass().add("tab-pane");

        // Booking Management Tab
        Tab bookingTab = new Tab("Kelola Booking");
        bookingTab.setClosable(false);
        bookingTab.setContent(createBookingManagementPane());

        // Schedule Management Tab
        Tab jadwalTab = new Tab("Kelola Jadwal");
        jadwalTab.setClosable(false);
        jadwalTab.setContent(createScheduleManagementPane());

        // Room Management Tab
        Tab ruangTab = new Tab("Kelola Ruang");
        ruangTab.setClosable(false);
        ruangTab.setContent(createRoomManagementPane());

        tabPane.getTabs().addAll(bookingTab, jadwalTab, ruangTab);
        root.setCenter(tabPane);
    }

    private VBox createBookingManagementPane() {
        VBox bookingPane = new VBox(15);
        bookingPane.setPadding(new Insets(20));

        Label titleLabel = new Label("Kelola Booking Mahasiswa");
        titleLabel.getStyleClass().add("section-title");

        // Booking table
        bookingTable = new TableView<>();
        bookingTable.getStyleClass().add("booking-table");

        TableColumn<BookingTableData, String> pemesanCol = new TableColumn<>("Pemesan");
        TableColumn<BookingTableData, String> ruangCol = new TableColumn<>("Ruang");
        TableColumn<BookingTableData, String> hariCol = new TableColumn<>("Hari");
        TableColumn<BookingTableData, String> jamCol = new TableColumn<>("Jam");
        TableColumn<BookingTableData, String> statusCol = new TableColumn<>("Status");
        TableColumn<BookingTableData, String> aksiCol = new TableColumn<>("Aksi");

        pemesanCol.setCellValueFactory(new PropertyValueFactory<>("pemesan"));
        ruangCol.setCellValueFactory(new PropertyValueFactory<>("ruang"));
        hariCol.setCellValueFactory(new PropertyValueFactory<>("hari"));
        jamCol.setCellValueFactory(new PropertyValueFactory<>("jam"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Action column with buttons
        aksiCol.setCellFactory(column -> new TableCell<BookingTableData, String>() {
            private final Button approveButton = new Button("Terima");
            private final Button rejectButton = new Button("Tolak");
            private final HBox buttonBox = new HBox(5, approveButton, rejectButton);

            {
                approveButton.getStyleClass().add("approve-button");
                rejectButton.getStyleClass().add("reject-button");

                approveButton.setOnAction(e -> {
                    BookingTableData data = getTableView().getItems().get(getIndex());
                    handleBookingAction(data, "Diterima");
                });

                rejectButton.setOnAction(e -> {
                    BookingTableData data = getTableView().getItems().get(getIndex());
                    handleBookingAction(data, "Ditolak");
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    BookingTableData data = getTableView().getItems().get(getIndex());
                    if ("Menunggu".equals(data.getStatus())) {
                        setGraphic(buttonBox);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        bookingTable.getColumns().addAll(pemesanCol, ruangCol, hariCol, jamCol, statusCol, aksiCol);
        loadBookingData();

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().add("refresh-button");
        refreshButton.setOnAction(e -> loadBookingData());

        bookingPane.getChildren().addAll(titleLabel, bookingTable, refreshButton);
        return bookingPane;
    }

    private VBox createScheduleManagementPane() {
        VBox jadwalPane = new VBox(15);
        jadwalPane.setPadding(new Insets(20));

        Label titleLabel = new Label("Kelola Jadwal Kuliah");
        titleLabel.getStyleClass().add("section-title");

        // Schedule table
        jadwalTable = new TableView<>();
        jadwalTable.getStyleClass().add("jadwal-table");

        TableColumn<JadwalTableData, String> hariCol = new TableColumn<>("Hari");
        TableColumn<JadwalTableData, String> jamCol = new TableColumn<>("Jam");
        TableColumn<JadwalTableData, String> matkulCol = new TableColumn<>("Mata Kuliah");
        TableColumn<JadwalTableData, String> ruangCol = new TableColumn<>("Ruang");

        hariCol.setCellValueFactory(new PropertyValueFactory<>("hari"));
        jamCol.setCellValueFactory(new PropertyValueFactory<>("jam"));
        matkulCol.setCellValueFactory(new PropertyValueFactory<>("mataKuliah"));
        ruangCol.setCellValueFactory(new PropertyValueFactory<>("ruang"));

        jadwalTable.getColumns().addAll(hariCol, jamCol, matkulCol, ruangCol);
        loadJadwalData();

        // Add schedule form
        GridPane addForm = new GridPane();
        addForm.setHgap(10);
        addForm.setVgap(10);
        addForm.setPadding(new Insets(10));
        addForm.getStyleClass().add("form-grid");

        ComboBox<String> hariCombo = new ComboBox<>();
        hariCombo.setItems(FXCollections.observableArrayList(
                "SENIN", "SELASA", "RABU", "KAMIS", "JUMAT", "SABTU"
        ));

        TextField jamMulaiField = new TextField();
        jamMulaiField.setPromptText("HH:MM");
        TextField jamSelesaiField = new TextField();
        jamSelesaiField.setPromptText("HH:MM");
        TextField matkulField = new TextField();
        matkulField.setPromptText("Nama Mata Kuliah");

        ComboBox<String> ruangCombo = new ComboBox<>();
        ObservableList<String> ruangOptions = FXCollections.observableArrayList();
        for (Ruang ruang : Main.ruangList) {
            ruangOptions.add(ruang.getNama() + " (" + ruang.getGedung() + ")");
        }
        ruangCombo.setItems(ruangOptions);

        Button addButton = new Button("Tambah Jadwal");
        addButton.getStyleClass().add("add-button");
        addButton.setOnAction(e -> handleAddJadwal(hariCombo, jamMulaiField, jamSelesaiField, matkulField, ruangCombo));

        addForm.add(new Label("Hari:"), 0, 0);
        addForm.add(hariCombo, 1, 0);
        addForm.add(new Label("Jam Mulai:"), 2, 0);
        addForm.add(jamMulaiField, 3, 0);
        addForm.add(new Label("Jam Selesai:"), 0, 1);
        addForm.add(jamSelesaiField, 1, 1);
        addForm.add(new Label("Mata Kuliah:"), 2, 1);
        addForm.add(matkulField, 3, 1);
        addForm.add(new Label("Ruang:"), 0, 2);
        addForm.add(ruangCombo, 1, 2);
        addForm.add(addButton, 2, 2);

        jadwalPane.getChildren().addAll(titleLabel, jadwalTable, new Label("Tambah Jadwal Baru:"), addForm);
        return jadwalPane;
    }

    private VBox createRoomManagementPane() {
        VBox ruangPane = new VBox(15);
        ruangPane.setPadding(new Insets(20));

        Label titleLabel = new Label("Kelola Ruang");
        titleLabel.getStyleClass().add("section-title");

        // Room list
        ListView<String> roomList = new ListView<>();
        roomList.getStyleClass().add("room-list");
        loadRoomData(roomList);

        // Add room form
        GridPane addRoomForm = new GridPane();
        addRoomForm.setHgap(10);
        addRoomForm.setVgap(10);
        addRoomForm.setPadding(new Insets(10));
        addRoomForm.getStyleClass().add("form-grid");

        TextField namaRuangField = new TextField();
        namaRuangField.setPromptText("Nama Ruang (contoh: R101)");
        TextField gedungField = new TextField();
        gedungField.setPromptText("Gedung (contoh: GKB1)");
        TextField kapasitasField = new TextField();
        kapasitasField.setPromptText("Kapasitas");

        Button addRoomButton = new Button("Tambah Ruang");
        addRoomButton.getStyleClass().add("add-button");
        addRoomButton.setOnAction(e -> handleAddRoom(namaRuangField, gedungField, kapasitasField, roomList));

        addRoomForm.add(new Label("Nama Ruang:"), 0, 0);
        addRoomForm.add(namaRuangField, 1, 0);
        addRoomForm.add(new Label("Gedung:"), 0, 1);
        addRoomForm.add(gedungField, 1, 1);
        addRoomForm.add(new Label("Kapasitas:"), 0, 2);
        addRoomForm.add(kapasitasField, 1, 2);
        addRoomForm.add(addRoomButton, 1, 3);

        ruangPane.getChildren().addAll(titleLabel, roomList, new Label("Tambah Ruang Baru:"), addRoomForm);
        return ruangPane;
    }

    private void handleBookingAction(BookingTableData data, String newStatus) {
        // Find the actual booking object
        for (Booking booking : Main.bookingList) {
            if (booking.getPemesan().getUsername().equals(data.getPemesan()) &&
                    booking.getRuang().getNama().equals(data.getRuang().split(" \\(")[0]) &&
                    booking.getStatus().equals("Menunggu")) {

                booking.setStatus(newStatus);

                // Send notification to student
                String message = "Booking ruang " + booking.getRuang().getNama() + " " +
                        (newStatus.equals("Diterima") ? "telah disetujui" : "ditolak");
                NotifikasiController.addNotification(message, "INFO", booking.getPemesan().getUsername());

                showAlert("Success", "Booking berhasil " + (newStatus.equals("Diterima") ? "disetujui" : "ditolak"));
                loadBookingData();
                break;
            }
        }
    }

    private void handleAddJadwal(ComboBox<String> hariCombo, TextField jamMulaiField,
                                 TextField jamSelesaiField, TextField matkulField, ComboBox<String> ruangCombo) {
        String hari = hariCombo.getValue();
        String jamMulaiStr = jamMulaiField.getText();
        String jamSelesaiStr = jamSelesaiField.getText();
        String matkul = matkulField.getText();
        String ruangStr = ruangCombo.getValue();

        if (hari == null || jamMulaiStr.isEmpty() || jamSelesaiStr.isEmpty() ||
                matkul.isEmpty() || ruangStr == null) {
            showAlert("Error", "Harap lengkapi semua field!");
            return;
        }

        try {
            LocalTime jamMulai = LocalTime.parse(jamMulaiStr);
            LocalTime jamSelesai = LocalTime.parse(jamSelesaiStr);

            if (!jamMulai.isBefore(jamSelesai)) {
                showAlert("Error", "Jam mulai harus lebih awal dari jam selesai!");
                return;
            }

            // Find selected room
            String roomName = ruangStr.split(" \\(")[0];
            Ruang selectedRoom = null;
            for (Ruang ruang : Main.ruangList) {
                if (ruang.getNama().equals(roomName)) {
                    selectedRoom = ruang;
                    break;
                }
            }

            if (selectedRoom == null) {
                showAlert("Error", "Ruang tidak ditemukan!");
                return;
            }

            // Check for conflicts
            for (Jadwal jadwal : Main.jadwalList) {
                if (jadwal.getRuang().getNama().equals(selectedRoom.getNama()) &&
                        jadwal.getHari().equals(hari) &&
                        TimeUtil.isTimeConflict(jamMulai, jamSelesai, jadwal.getJamMulai(), jadwal.getJamSelesai())) {
                    showAlert("Error", "Jadwal bertabrakan dengan jadwal yang sudah ada!");
                    return;
                }
            }

            // Add new schedule
            Jadwal newJadwal = new Jadwal(hari, jamMulai, jamSelesai, matkul, selectedRoom);
            Main.jadwalList.add(newJadwal);

            // Clear form
            hariCombo.setValue(null);
            jamMulaiField.clear();
            jamSelesaiField.clear();
            matkulField.clear();
            ruangCombo.setValue(null);

            showAlert("Success", "Jadwal berhasil ditambahkan!");
            loadJadwalData();

        } catch (Exception e) {
            showAlert("Error", "Format jam tidak valid! Gunakan format HH:MM");
        }
    }

    private void handleAddRoom(TextField namaRuangField, TextField gedungField,
                               TextField kapasitasField, ListView<String> roomList) {
        String nama = namaRuangField.getText();
        String gedung = gedungField.getText();
        String kapasitasStr = kapasitasField.getText();

        if (nama.isEmpty() || gedung.isEmpty() || kapasitasStr.isEmpty()) {
            showAlert("Error", "Harap lengkapi semua field!");
            return;
        }

        try {
            int kapasitas = Integer.parseInt(kapasitasStr);

            // Check if room already exists
            for (Ruang ruang : Main.ruangList) {
                if (ruang.getNama().equalsIgnoreCase(nama)) {
                    showAlert("Error", "Ruang dengan nama tersebut sudah ada!");
                    return;
                }
            }

            // Add new room
            Ruang newRuang = new Ruang(nama, gedung, kapasitas);
            Main.ruangList.add(newRuang);

            // Clear form
            namaRuangField.clear();
            gedungField.clear();
            kapasitasField.clear();

            showAlert("Success", "Ruang berhasil ditambahkan!");
            loadRoomData(roomList);

        } catch (NumberFormatException e) {
            showAlert("Error", "Kapasitas harus berupa angka!");
        }
    }

    private void loadBookingData() {
        ObservableList<BookingTableData> data = FXCollections.observableArrayList();

        for (Booking booking : Main.bookingList) {
            data.add(new BookingTableData(
                    booking.getPemesan().getUsername(),
                    booking.getRuang().getNama() + " (" + booking.getRuang().getGedung() + ")",
                    booking.getHari(),
                    booking.getJamMulai() + "-" + booking.getJamSelesai(),
                    booking.getStatus()
            ));
        }

        bookingTable.setItems(data);
    }

    private void loadJadwalData() {
        ObservableList<JadwalTableData> data = FXCollections.observableArrayList();

        for (Jadwal jadwal : Main.jadwalList) {
            data.add(new JadwalTableData(
                    jadwal.getHari(),
                    jadwal.getJamMulai() + "-" + jadwal.getJamSelesai(),
                    jadwal.getMatkul(),
                    jadwal.getRuang().getNama()
            ));
        }

        jadwalTable.setItems(data);
    }

    private void loadRoomData(ListView<String> roomList) {
        ObservableList<String> rooms = FXCollections.observableArrayList();

        for (Ruang ruang : Main.ruangList) {
            rooms.add(ruang.getNama() + " - " + ruang.getGedung() + " (Kapasitas: " + ruang.getKapasitas() + ")");
        }

        roomList.setItems(rooms);
    }

    private void updateNotifications() {
        Platform.runLater(() -> {
            List<Notifikasi> unreadNotifications = NotifikasiController.getUnreadNotificationsForUser("admin");
            if (!unreadNotifications.isEmpty()) {
                notificationLabel.setText("🔔 " + unreadNotifications.size() + " notifikasi baru");
                notificationLabel.setOnMouseClicked(e -> showNotifications());
            } else {
                notificationLabel.setText("");
            }
        });
    }

    private void showNotifications() {
        List<Notifikasi> notifications = NotifikasiController.getNotificationsForUser("admin");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifikasi");
        alert.setHeaderText("Notifikasi Admin:");

        StringBuilder content = new StringBuilder();
        for (Notifikasi notif : notifications) {
            content.append(notif.getPesan()).append("\n");
            content.append("Waktu: ").append(TimeUtil.formatDateTime(notif.getWaktu())).append("\n\n");
        }

        alert.setContentText(content.toString());
        alert.showAndWait();

        // Mark as read
        NotifikasiController.markAsRead("admin");
        updateNotifications();
    }

    private void startUpdateTimer() {
        updateTimer = new Timer(true);
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Main.updateBookingStatus();
                updateNotifications();
            }
        }, 0, 30000); // Update every 30 seconds
    }

    private void handleLogout() {
        if (updateTimer != null) {
            updateTimer.cancel();
        }
        LoginPane loginPane = new LoginPane(stage);
        loginPane.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show() {
        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add(getClass().getResource("resources/admin.css").toExternalForm());

        stage.setTitle("SiRuang - Dashboard Admin");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> {
            if (updateTimer != null) {
                updateTimer.cancel();
            }
        });
        stage.show();
    }

    // Table data classes
    public static class BookingTableData {
        private String pemesan, ruang, hari, jam, status;

        public BookingTableData(String pemesan, String ruang, String hari, String jam, String status) {
            this.pemesan = pemesan;
            this.ruang = ruang;
            this.hari = hari;
            this.jam = jam;
            this.status = status;
        }

        public String getPemesan() { return pemesan; }
        public String getRuang() { return ruang; }
        public String getHari() { return hari; }
        public String getJam() { return jam; }
        public String getStatus() { return status; }
    }

    public static class JadwalTableData {
        private String hari, jam, mataKuliah, ruang;

        public JadwalTableData(String hari, String jam, String mataKuliah, String ruang) {
            this.hari = hari;
            this.jam = jam;
            this.mataKuliah = mataKuliah;
            this.ruang = ruang;
        }

        public String getHari() { return hari; }
        public String getJam() { return jam; }
        public String getMataKuliah() { return mataKuliah; }
        public String getRuang() { return ruang; }
    }
}