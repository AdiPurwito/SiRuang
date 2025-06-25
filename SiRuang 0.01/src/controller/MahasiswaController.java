package controller;

import model.*;
import java.util.*;
import java.time.*;

public class MahasiswaController {
    private Mahasiswa mahasiswa;
    private Scanner sc = new Scanner(System.in);
    private ArrayList<Jadwal> daftarJadwal;
    private ArrayList<Ruang> daftarRuang;
    private ArrayList<Booking> daftarBooking;

    public MahasiswaController(Mahasiswa mahasiswa, ArrayList<Jadwal> jadwal, ArrayList<Ruang> ruang, ArrayList<Booking> bookings) {
        this.mahasiswa = mahasiswa;
        this.daftarJadwal = jadwal;
        this.daftarRuang = ruang;
        this.daftarBooking = bookings;
    }

    public void tampilkanMenu() {
        int pilih;
        do {
            System.out.println("\n== Dashboard Mahasiswa ==");
            System.out.println("1. Lihat jadwal hari ini");
            System.out.println("2. Cari ruang kosong");
            System.out.println("3. Booking ruang");
            System.out.println("0. Keluar");
            System.out.print("Pilih: ");
            pilih = sc.nextInt(); sc.nextLine();

            switch (pilih) {
                case 1: lihatJadwal(); break;
                case 2: cariRuangKosong(); break;
                case 3: bookingRuang(); break;
                case 0: System.out.println("Keluar dari dashboard mahasiswa."); break;
                default: System.out.println("Pilihan tidak valid.");
            }
        } while (pilih != 0);
    }

    private void lihatJadwal() {
        String hariIni = LocalDate.now().getDayOfWeek().toString();
        System.out.println("\n-- Jadwal Hari Ini (" + hariIni + ") --");
        for (Jadwal j : daftarJadwal) {
            if (j.getRuang() != null && j.getHari().equalsIgnoreCase(hariIni)) {
                j.tampilkan();
            }
        }
    }

    private void cariRuangKosong() {
        LocalTime sekarang = LocalTime.now();
        String hari = LocalDate.now().getDayOfWeek().toString();

        System.out.print("Cari ruang di gedung mana (GKB1/GKB2/...): ");
        String gkb = sc.nextLine();

        System.out.println("\n-- Ruang Kosong Sekarang di " + gkb + " --");
        for (Ruang r : daftarRuang) {
            if (!r.getGedung().equalsIgnoreCase(gkb)) continue;

            boolean dipakai = false;
            for (Jadwal j : daftarJadwal) {
                if (j.getRuang().getNama().equals(r.getNama()) &&
                        j.getHari().equalsIgnoreCase(hari) &&
                        sekarang.isAfter(j.getJamMulai()) && sekarang.isBefore(j.getJamSelesai())) {
                    dipakai = true;
                    break;
                }
            }
            if (!dipakai) {
                r.tampilkanInfo();
            }
        }
    }

    private void bookingRuang() {
        System.out.println("-- Booking Ruang Real-Time --");

        String hariSekarang = LocalDate.now().getDayOfWeek().toString();
        LocalTime jamSekarang = LocalTime.now();

        // Tampilkan ruang yang benar-benar kosong sekarang
        ArrayList<Ruang> ruangKosong = new ArrayList<>();
        for (Ruang r : daftarRuang) {
            boolean dipakai = false;
            for (Jadwal j : daftarJadwal) {
                if (j.getRuang().getNama().equalsIgnoreCase(r.getNama()) &&
                        j.getHari().equalsIgnoreCase(hariSekarang) &&
                        jamSekarang.isAfter(j.getJamMulai()) &&
                        jamSekarang.isBefore(j.getJamSelesai())) {
                    dipakai = true;
                    break;
                }
            }
            if (!dipakai) ruangKosong.add(r);
        }

        if (ruangKosong.isEmpty()) {
            System.out.println("Tidak ada ruang kosong saat ini.");
            return;
        }

        System.out.println("\nRuang Kosong Sekarang:");
        for (int i = 0; i < ruangKosong.size(); i++) {
            System.out.println((i+1) + ". " + ruangKosong.get(i).getNama() + " | Gedung: " + ruangKosong.get(i).getGedung());
        }

        System.out.print("Pilih nomor ruang yang ingin dibooking: ");
        int pilih = sc.nextInt() - 1; sc.nextLine();
        if (pilih < 0 || pilih >= ruangKosong.size()) {
            System.out.println("Pilihan tidak valid.");
            return;
        }

        Ruang ruangDipilih = ruangKosong.get(pilih);
        System.out.println("Booking ruang " + ruangDipilih.getNama() + " untuk waktu sekarang selama 1 jam.");

        LocalTime jamMulai = jamSekarang;
        LocalTime jamSelesai = jamMulai.plusHours(1);

        // Cek ulang bentrok (jaga-jaga)
        for (Jadwal j : daftarJadwal) {
            if (j.getRuang().getNama().equalsIgnoreCase(ruangDipilih.getNama()) &&
                    j.getHari().equalsIgnoreCase(hariSekarang) &&
                    jamMulai.isBefore(j.getJamSelesai()) &&
                    jamSelesai.isAfter(j.getJamMulai())) {
                System.out.println("Booking gagal: ruang ini akan segera dipakai.");
                return;
            }
        }

        Booking b = new Booking(mahasiswa, ruangDipilih, hariSekarang, jamMulai, jamSelesai);
        daftarBooking.add(b);
        System.out.println("Booking berhasil diajukan dari " + jamMulai + " sampai " + jamSelesai + ".");
    }
}
