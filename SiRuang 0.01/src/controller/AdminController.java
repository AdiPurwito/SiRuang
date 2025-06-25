package controller;

import model.*;
import java.util.*;

public class AdminController {
    private Scanner sc = new Scanner(System.in);
    private ArrayList<Jadwal> daftarJadwal;
    private ArrayList<Ruang> daftarRuang;
    private ArrayList<Booking> daftarBooking;

    public AdminController(ArrayList<Jadwal> jadwal, ArrayList<Ruang> ruang, ArrayList<Booking> bookings) {
        this.daftarJadwal = jadwal;
        this.daftarRuang = ruang;
        this.daftarBooking = bookings;
    }

    public void tampilkanMenu() {
        int pilih;
        do {
            System.out.println("\n== Dashboard Admin ==");
            System.out.println("1. Lihat semua jadwal");
            System.out.println("2. Tambah jadwal");
            System.out.println("3. Hapus jadwal");
            System.out.println("4. Lihat & proses booking");
            System.out.println("0. Keluar");
            System.out.print("Pilih: ");
            pilih = sc.nextInt(); sc.nextLine();

            switch (pilih) {
                case 1: tampilkanJadwal(); break;
                case 2: tambahJadwal(); break;
                case 3: hapusJadwal(); break;
                case 4: prosesBooking(); break;
                case 0: System.out.println("Keluar dashboard admin."); break;
                default: System.out.println("Pilihan tidak valid.");
            }
        } while (pilih != 0);
    }

    private void tampilkanJadwal() {
        System.out.println("\n-- Semua Jadwal --");
        for (int i = 0; i < daftarJadwal.size(); i++) {
            System.out.print((i+1) + ". ");
            daftarJadwal.get(i).tampilkan();
        }
    }

    private void tambahJadwal() {
        System.out.print("Hari: ");
        String hari = sc.nextLine();
        System.out.print("Jam mulai (hh:mm): ");
        String jm = sc.nextLine();
        System.out.print("Jam selesai (hh:mm): ");
        String js = sc.nextLine();
        System.out.print("Mata kuliah: ");
        String matkul = sc.nextLine();

        System.out.println("Pilih ruang:");
        for (int i = 0; i < daftarRuang.size(); i++) {
            System.out.println((i+1) + ". " + daftarRuang.get(i).getNama());
        }
        System.out.print("Pilih nomor ruang: ");
        int indexRuang = sc.nextInt() - 1; sc.nextLine();
        Ruang ruang = daftarRuang.get(indexRuang);

        Jadwal jadwalBaru = new Jadwal(hari,
                java.time.LocalTime.parse(jm),
                java.time.LocalTime.parse(js),
                matkul, ruang);
        daftarJadwal.add(jadwalBaru);
        System.out.println("Jadwal berhasil ditambahkan.");
    }

    private void hapusJadwal() {
        tampilkanJadwal();
        System.out.print("Pilih jadwal yang ingin dihapus (nomor): ");
        int idx = sc.nextInt() - 1;
        if (idx >= 0 && idx < daftarJadwal.size()) {
            daftarJadwal.remove(idx);
            System.out.println("Jadwal berhasil dihapus.");
        } else {
            System.out.println("Index tidak valid.");
        }
    }

    private void prosesBooking() {
        daftarBooking.sort(Comparator.comparingLong(Booking::getWaktuPengajuan));
        for (int i = 0; i < daftarBooking.size(); i++) {
            Booking b = daftarBooking.get(i);
            System.out.println("\n[" + (i+1) + "]");
            b.tampilkanInfo();
            if (!b.getStatus().equals("Menunggu")) continue;

            System.out.print("ACC booking ini? (y/n): ");
            String acc = sc.nextLine();
            if (acc.equalsIgnoreCase("y")) {
                b.setStatus("Diterima");
                System.out.println("Booking diterima.");
            } else {
                b.setStatus("Ditolak");
                System.out.println("Booking ditolak.");
            }
        }
    }
}
