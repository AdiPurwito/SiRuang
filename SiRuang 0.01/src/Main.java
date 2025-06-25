import model.*;
import util.DummyData;
import controller.*;

import javafx.application.Application;
import javafx.stage.Stage;
import gui.LoginPane;

import java.util.Scanner;
import java.util.ArrayList;

public class Main extends Application  {

    // Static variables untuk menyimpan data yang akan digunakan di GUI
    private static ArrayList<User> users;
    private static ArrayList<Ruang> ruangList;
    private static ArrayList<Jadwal> jadwalList;
    private static ArrayList<Booking> bookingList;

    @Override
    public void start(Stage primaryStage) {
        // Inisialisasi data dummy
        users = DummyData.getUserList();
        ruangList = DummyData.getRuangList();
        jadwalList = DummyData.getJadwalList(ruangList);
        bookingList = new ArrayList<>();

        // Tampilkan GUI login
        new LoginPane(primaryStage).show();
    }

    public static void main(String[] args) {
        // Cek apakah user ingin menggunakan CLI atau GUI
        if (args.length > 0 && args[0].equals("-cli")) {
            // Mode CLI
            runCLIMode();
        } else {
            // Mode GUI (default)
            launch(args);
        }
    }

    private static void runCLIMode() {
        Scanner sc = new Scanner(System.in);
        var users = DummyData.getUserList();

        System.out.println("==== Login SiRuang ====");
        System.out.print("Username: ");
        String user = sc.nextLine();
        System.out.print("Password: ");
        String pass = sc.nextLine();

        User loggedIn = null;
        for (User u : users) {
            if (u.getUsername().equals(user) && u.cekPassword(pass)) {
                loggedIn = u;
                break;
            }
        }

        if (loggedIn != null) {
            loggedIn.tampilkanDashboard();

            if (loggedIn instanceof Admin) {
                AdminController ac = new AdminController(
                        DummyData.getJadwalList(DummyData.getRuangList()),
                        DummyData.getRuangList(),
                        new ArrayList<>()
                );
                ac.tampilkanMenu();
            } else if (loggedIn instanceof Mahasiswa mhs) {
                MahasiswaController mc = new MahasiswaController(
                        mhs,
                        DummyData.getJadwalList(DummyData.getRuangList()),
                        DummyData.getRuangList(),
                        new ArrayList<>()
                );
                mc.tampilkanMenu();
            }
        } else {
            System.out.println("Login gagal. Username atau password salah.");
        }

        sc.close();
    }

    // Getter methods untuk mengakses data dari GUI
    public static ArrayList<User> getUsers() { return users; }
    public static ArrayList<Ruang> getRuangList() { return ruangList; }
    public static ArrayList<Jadwal> getJadwalList() { return jadwalList; }
    public static ArrayList<Booking> getBookingList() { return bookingList; }
}