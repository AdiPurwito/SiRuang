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

public class MahasiswaDashboard {
    private Stage stage;
    private Mahasiswa mahasiswa;
    private BorderPane root;
    private TableView<JadwalTableData> jadwalTable;
    private ComboBox<String> jamMulaiCombo, jamSelesaiCombo, ruangCombo;
    private Label notificationLabel;
    private Timer updateTimer;
    private ListView<String> myBookingsList;

    public MahasiswaDashboard(Stage stage, Mahasiswa mahasiswa) {
        this.stage = stage;
        this.mahasiswa = mahasiswa;
        initializeComponents();
        startUpdateTimer();
    }

    private void initializeComponents() {
        root = new BorderPane();
        root.getStyleClass().add("root");

        // Top Section
        createTopSection();

        // Center Section - Jadwal Table
        createCenterSection();

        // Bottom Section - Booking Form
        createBottomSection();

        updateNotifications();
    }

    private void createTopSection() {
        HBox topBox = new HBox();
        topBox.setPadding(new Insets(20));
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.getStyleClass().add("header-box");

        Label titleLabel = new Label("Dashboard Mahasiswa");
        titleLabel.getStyleClass().add("header-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.CENTER_RIGHT);

        Label fakultasLabel = new Label("Fakultas: " + mahasiswa.getFakultas());
        Label jurusanLabel = new Label("Jurusan: " + mahasiswa.getProdi());
        fakultasLabel.getStyleClass().add("info-label");
        jurusanLabel.getStyleClass().add("info-label");

        infoBox.getChildren().addAll(fakultasLabel, jurusanLabel);

        HBox rightBox = new HBox(15);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        notificationLabel = new Label();
        notificationLabel.getStyleClass().add("notification-label");

        Button waktuButton = new Button("Waktu: " + LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        waktuButton.getStyleClass().add("waktu-button");

        rightBox.getChildren().addAll(notificationLabel, infoBox, waktuButton);

        topBox.getChildren().addAll(titleLabel, spacer, rightBox);
        root.setTop(topBox);
    }

    private void createCenterSection() {
        VBox centerBox = new VBox(10);
        centerBox.setPadding(new Insets(0, 20, 20, 20));

        Label tableTitle = new Label("Jadwal Kuliah");
        tableTitle.getStyleClass().add("table-title");

        // Create table
        jadwalTable = new TableView<>();
        jadwalTable.getStyleClass().add("jadwal-table");

        // Table columns
        TableColumn<JadwalTableData, String> hariCol = new TableColumn<>("Hari");
        TableColumn<JadwalTableData, String> jamCol = new TableColumn<>("Jam");
        TableColumn<JadwalTableData, String> matkulCol = new TableColumn<>("Mata Kuliah");
        TableColumn<JadwalTableData, String> semesterCol = new TableColumn<>("Semester");
        TableColumn<JadwalTableData, String> kelasCol = new TableColumn<>("Kelas");
        TableColumn<JadwalTableData, String> sksCol = new TableColumn<>("SKS");
        TableColumn<JadwalTableData, String> dosenCol = new TableColumn<>("Dosen");
        TableColumn<JadwalTableData, String> ruangCol = new TableColumn<>("Ruang");

        hariCol.setCellValueFactory(new PropertyValueFactory<>("hari"));
        jamCol.setCellValueFactory(new PropertyValueFactory<>("jam"));
        matkulCol.setCellValueFactory(new PropertyValueFactory<>("mataKuliah"));
        semesterCol.setCellValueFactory(new PropertyValueFactory<>("semester"));
        kelasCol.setCellValueFactory(new PropertyValueFactory<>("kelas"));
        sksCol.setCellValueFactory(new PropertyValueFactory<>("sks"));
        dosenCol.setCellValueFactory(new PropertyValueFactory<>("dosen"));
        ruangCol.setCellValueFactory(new PropertyValueFactory<>("ruang"));

        jadwalTable.getColumns().addAll(hariCol, jamCol, matkulCol, semesterCol, kelasCol, sksCol, dosenCol, ruangCol);

        loadJadwalData();

        centerBox.getChildren().addAll(tableTitle, jadwalTable);
        root.setCenter(centerBox);
    }

    private void createBottomSection() {
        VBox bottomBox = new VBox(15);
        bottomBox.setPadding(new Insets(20));
        bottomBox.getStyleClass().add("booking-section");

        Label bookingTitle = new Label("Booking Ruang");
        bookingTitle.getStyleClass().add("section-title");

        // Booking form
        GridPane bookingForm = new GridPane();
        bookingForm.setHgap(15);
        bookingForm.setVgap(10);
        bookingForm.setAlignment(Pos.CENTER);

        // Time selection
        Label jamMulaiLabel = new Label("Jam Mulai:");
        jamMulaiCombo = new ComboBox<>();
        jamMulaiCombo.getStyleClass().add("time-combo");
        loadTimeOptions(jamMulaiCombo);

        Label jamSelesaiLabel = new Label("Jam Selesai:");
        jamSelesaiCombo = new ComboBox<>();
        jamSelesaiCombo.getStyleClass().add("time-combo");
        loadTimeOptions(jamSelesaiCombo);

        // Room selection
        Label ruangLabel = new Label("Pilih Ruang:");
        ruangCombo = new ComboBox<>();
        ruangCombo.getStyleClass().add("room-combo");
        loadAvailableRooms();

        Button bookingButton = new Button("Booking Sekarang");
        bookingButton.getStyleClass().add("booking-button");
        bookingButton.setOnAction(e -> handleBooking());

        Button refreshButton = new Button("Refresh Ruang");
        refreshButton.getStyleClass().add("refresh-button");
        refreshButton.setOnAction(e -> loadAvailableRooms());

        bookingForm.add(jamMulaiLabel, 0, 0);
        bookingForm.add(jamMulaiCombo, 1, 0);
        bookingForm.add(jamSelesaiLabel, 2, 0);
        bookingForm.add(jamSelesaiCombo, 3, 0);
        bookingForm.add(ruangLabel, 0, 1);
        bookingForm.add(ruangCombo, 1, 1);
        bookingForm.add(bookingButton, 2, 1);
        bookingForm.add(refreshButton, 3, 1);

        // My bookings section
        Label myBookingsTitle = new Label("Booking Saya");
        myBookingsTitle.getStyleClass().add("section-title");

        myBookingsList = new ListView<>();
        myBookingsList.getStyleClass().add("bookings-list");
        myBookingsList.setPrefHeight(120);
        loadMyBookings();

        bottomBox.getChildren().addAll(bookingTitle, bookingForm, myBookingsTitle, myBookingsList);
        root.setBottom(bottomBox);
    }

    private void loadTimeOptions(ComboBox<String> combo) {
        ObservableList<String> times = FXCollections.observableArrayList();
        for (int hour = 7; hour <= 18; hour++) {
            times.add(String.format("%02d:00", hour));
            times.add(String.format("%02d:30", hour));
        }
        combo.setItems(times);
    }

    private void loadAvailableRooms() {
        ObservableList<String> availableRooms = FXCollections.observableArrayList();

        String hariSekarang = LocalDate.now().getDayOfWeek().toString();
        LocalTime jamSekarang = LocalTime.now();

        for (Ruang ruang : Main.ruangList) {
            boolean isAvailable = true;

            // Check jadwal conflict
            for (Jadwal jadwal : Main.jadwalList) {
                if (jadwal.getRuang().getNama().equals(ruang.getNama()) &&
                        jadwal.getHari().equalsIgnoreCase(hariSekarang) &&
                        jamSekarang.isAfter(jadwal.getJamMulai()) &&
                        jamSekarang.isBefore(jadwal.getJamSelesai())) {
                    isAvailable = false;
                    break;
                }
            }

            // Check booking conflict
            if (isAvailable) {
                for (Booking booking : Main.bookingList) {
                    if (booking.getRuang().getNama().equals(ruang.getNama()) &&
                            booking.getHari().equalsIgnoreCase(hariSekarang) &&
                            booking.getStatus().equals("Diterima") &&
                            jamSekarang.isAfter(booking.getJamMulai()) &&
                            jamSekarang.isBefore(booking.getJamSelesai())) {
                        isAvailable = false;
                        break;
                    }
                }
            }

            if (isAvailable) {
                availableRooms.add(ruang.getNama() + " (" + ruang.getGedung() + ") - Kapasitas: " + ruang.getKapasitas());
            }
        }

        ruangCombo.setItems(availableRooms);
    }

    private void handleBooking() {
        String jamMulaiStr = jamMulaiCombo.getValue();
        String jamSelesaiStr = jamSelesaiCombo.getValue();
        String ruangStr = ruangCombo.getValue();

        if (jamMulaiStr == null || jamSelesaiStr == null || ruangStr == null) {
            showAlert("Error", "Harap lengkapi semua field!");
            return;
        }

        LocalTime jamMulai = LocalTime.parse(jamMulaiStr);
        LocalTime jamSelesai = LocalTime.parse(jamSelesaiStr);

        if (!jamMulai.isBefore(jamSelesai)) {
            showAlert("Error", "Jam mulai harus lebih awal dari jam selesai!");
            return;
        }

        // Get selected room
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

        // Validate time conflict
        String hariSekarang = LocalDate.now().getDayOfWeek().toString();

        // Check against jadwal
        for (Jadwal jadwal : Main.jadwalList) {
            if (jadwal.getRuang().getNama().equals(selectedRoom.getNama()) &&
                    jadwal.getHari().equalsIgnoreCase(hariSekarang) &&
                    TimeUtil.isTimeConflict(jamMulai, jamSelesai, jadwal.getJamMulai(), jadwal.getJamSelesai())) {
                showAlert("Error", "Waktu bertabrakan dengan jadwal kuliah!");
                return;
            }
        }

        // Check against existing bookings
        for (Booking booking : Main.bookingList) {
            if (booking.getRuang().getNama().equals(selectedRoom.getNama()) &&
                    booking.getHari().equalsIgnoreCase(hariSekarang) &&
                    booking.getStatus().equals("Diterima") &&
                    TimeUtil.isTimeConflict(jamMulai, jamSelesai, booking.getJamMulai(), booking.getJamSelesai())) {
                showAlert("Error", "Waktu bertabrakan dengan booking lain!");
                return;
            }
        }

        // Create booking
        Booking newBooking = new Booking(mahasiswa, selectedRoom, hariSekarang, jamMulai, jamSelesai);
        Main.bookingList.add(newBooking);

        // Send notification to admin
        NotifikasiController.addNotification(
                "Booking baru dari " + mahasiswa.getUsername() + " untuk ruang " + selectedRoom.getNama() +
                        " pada " + jamMulai + "-" + jamSelesai,
                "INFO",
                "admin"
        );

        // Send notification to mahasiswa
        NotifikasiController.addNotification(
                "Booking ruang " + selectedRoom.getNama() + " sedang diproses",
                "INFO",
                mahasiswa.getUsername()
        );

        showAlert("Success", "Booking berhasil diajukan! Menunggu persetujuan admin.");
        loadAvailableRooms();
        loadMyBookings();
    }

    private void loadMyBookings() {
        ObservableList<String> myBookings = FXCollections.observableArrayList();

        for (Booking booking : Main.bookingList) {
            if (booking.getPemesan().getUsername().equals(mahasiswa.getUsername())) {
                String status = booking.getStatus();
                String statusIcon = getStatusIcon(status);

                myBookings.add(statusIcon + " " + booking.getRuang().getNama() +
                        " | " + booking.getJamMulai() + "-" + booking.getJamSelesai() +
                        " | " + status);
            }
        }

        myBookingsList.setItems(myBookings);
    }

    private String getStatusIcon(String status) {
        switch (status) {
            case "Menunggu": return "⏳";
            case "Diterima": return "✅";
            case "Ditolak": return "❌";
            case "Selesai": return "🏁";
            default: return "❓";
        }
    }

    private void loadJadwalData() {
        ObservableList<JadwalTableData> data = FXCollections.observableArrayList();

        for (Jadwal jadwal : Main.jadwalList) {
            data.add(new JadwalTableData(
                    jadwal.getHari(),
                    jadwal.getJamMulai() + "-" + jadwal.getJamSelesai(),
                    jadwal.getMatkul(),
                    "1", // Default semester
                    "A", // Default class
                    "3", // Default SKS
                    "Dosen", // Default dosen
                    jadwal.getRuang().getNama()
            ));
        }

        jadwalTable.setItems(data);
    }

    private void updateNotifications() {
        Platform.runLater(() -> {
            List<Notifikasi> unreadNotifications = NotifikasiController.getUnreadNotificationsForUser(mahasiswa.getUsername());
            if (!unreadNotifications.isEmpty()) {
                notificationLabel.setText("🔔 " + unreadNotifications.size() + " notifikasi baru");
                notificationLabel.setOnMouseClicked(e -> showNotifications());
            } else {
                notificationLabel.setText("");
            }
        });
    }

    private void showNotifications() {
        List<Notifikasi> notifications = NotifikasiController.getNotificationsForUser(mahasiswa.getUsername());

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifikasi");
        alert.setHeaderText("Notifikasi Anda:");

        StringBuilder content = new StringBuilder();
        for (Notifikasi notif : notifications) {
            content.append(notif.getPesan()).append("\n");
            content.append("Waktu: ").append(TimeUtil.formatDateTime(notif.getWaktu())).append("\n\n");
        }

        alert.setContentText(content.toString());
        alert.showAndWait();

        // Mark as read
        NotifikasiController.markAsRead(mahasiswa.getUsername());
        updateNotifications();
    }

    private void startUpdateTimer() {
        updateTimer = new Timer(true);
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Main.updateBookingStatus();
                updateNotifications();
                Platform.runLater(() -> loadMyBookings());
            }
        }, 0, 30000); // Update every 30 seconds
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show() {
        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/resources/css/mahasiswa.css").toExternalForm());

        stage.setTitle("SiRuang - Dashboard Mahasiswa");
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> {
            if (updateTimer != null) {
                updateTimer.cancel();
            }
        });
        stage.show();
    }

    // Table data class
    public static class JadwalTableData {
        private String hari, jam, mataKuliah, semester, kelas, sks, dosen, ruang;

        public JadwalTableData(String hari, String jam, String mataKuliah, String semester,
                               String kelas, String sks, String dosen, String ruang) {
            this.hari = hari;
            this.jam = jam;
            this.mataKuliah = mataKuliah;
            this.semester = semester;
            this.kelas = kelas;
            this.sks = sks;
            this.dosen = dosen;
            this.ruang = ruang;
        }

        // Getters
        public String getHari() { return hari; }
        public String getJam() { return jam; }
        public String getMataKuliah() { return mataKuliah; }
        public String getSemester() { return semester; }
        public String getKelas() { return kelas; }
        public String getSks() { return sks; }
        public String getDosen() { return dosen; }
        public String getRuang() { return ruang; }
    }
}