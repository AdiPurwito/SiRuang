package app;

import model.*;
import util.DummyData;
import controller.*;

import java.util.Scanner;
import java.util.ArrayList;

public class Main {

    private static ArrayList<User> users;
    private static ArrayList<Ruang> ruangList;
    private static ArrayList<Jadwal> jadwalList;
    private static ArrayList<Booking> bookingList;

    public static void main(String[] args) {
        users = DummyData.getUserList();
        ruangList = DummyData.getRuangList();
        jadwalList = DummyData.getJadwalList(ruangList);
        bookingList = new ArrayList<>();

        runCLIMode();
    }

    private static void runCLIMode() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("==== Login SiRuang ====");
            System.out.print("Username: ");
            String user = sc.nextLine();
            System.out.print("Password: ");
            String pass = sc.nextLine();

            User loggedIn = null;
            for (User u : users) {
                if (u.getUsername().equals(user) && u.checkPassword(pass)) {
                    loggedIn = u;
                    break;
                }
            }

            if (loggedIn != null) {
                System.out.println("\nLogin berhasil sebagai " + loggedIn.getNama());
                loggedIn.tampilkanDashboard();

                if (loggedIn instanceof Admin) {
                    AdminController ac = new AdminController(jadwalList, ruangList, bookingList);
                    ac.tampilkanMenu();
                } else if (loggedIn instanceof Mahasiswa mhs) {
                    MahasiswaController mc = new MahasiswaController(mhs, jadwalList, ruangList, bookingList);
                    mc.tampilkanMenu();
                }
            } else {
                System.out.println("\nLogin gagal. Username atau password salah.");
            }

            // Tanya apakah ingin login ulang atau keluar
            System.out.print("\nKetik 'y' untuk login ulang, atau tekan Enter untuk keluar: ");
            String ulang = sc.nextLine().trim();
            if (!ulang.equalsIgnoreCase("y")) {
                System.out.println("Keluar dari aplikasi. Sampai jumpa!");
                break;
            }
            System.out.println();
        }

        sc.close();
    }


}
