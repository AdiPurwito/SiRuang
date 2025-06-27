/*
SiRuang 0.01/src/
├── app/
│   └── Main.java
├── controller/
│   ├── AdminController.java
│   ├── MahasiswaController.java
│   └── NotificationController.java (NEW)
├── model/
│   ├── Admin.java
│   ├── Booking.java (ENHANCED)
│   ├── Jadwal.java
│   ├── Mahasiswa.java
│   ├── Ruang.java
│   ├── User.java
│   └── Notification.java (NEW)
├── view/
│   ├── LoginView.java (NEW)
│   ├── AdminDashboard.java (NEW)
│   └── MahasiswaDashboard.java (NEW)
├── util/
│   ├── DummyData.java
│   └── TimeUtil.java (NEW)
└── resources/
    ├── css/
    │   ├── login.css
    │   ├── admin.css
    │   └── mahasiswa.css
    └── icons/
        └── (icon files)
*/
package app;

import javafx.application.Application;
import javafx.stage.Stage;
import model.*;
import util.DummyData;
import gui.LoginPane;
import controller.NotifikasiController;

import java.util.ArrayList;

public class Main extends Application {

    public static ArrayList<User> users;
    public static ArrayList<Ruang> ruangList;
    public static ArrayList<Jadwal> jadwalList;
    public static ArrayList<Booking> bookingList;

    @Override
    public void start(Stage primaryStage) {
        // Initialize data
        users = DummyData.getUserList();
        ruangList = DummyData.getRuangList();
        jadwalList = DummyData.getJadwalList(ruangList);
        bookingList = new ArrayList<>();

        // Show login window
        LoginPane loginPane = new LoginPane(primaryStage);
        loginPane.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Background task untuk update status booking yang expired
    public static void updateBookingStatus() {
        for (Booking booking : bookingList) {
            String oldStatus = booking.getStatus();
            booking.checkAndUpdateStatus();

            // Kirim notifikasi jika status berubah dari Diterima ke Selesai
            if (oldStatus.equals("Diterima") && booking.getStatus().equals("Selesai")) {
                NotifikasiController.addNotification(
                        "Booking ruang " + booking.getRuang().getNama() + " telah selesai.",
                        "INFO",
                        booking.getPemesan().getUsername()
                );
            }
        }
    }
}
