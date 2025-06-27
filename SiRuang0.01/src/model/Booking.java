package model;

import java.time.LocalTime;
import java.time.LocalDateTime;

public class Booking {
    private Mahasiswa pemesan;
    private Ruang ruang;
    private String hari;
    private LocalTime jamMulai;
    private LocalTime jamSelesai;
    private String status; // Menunggu, Diterima, Ditolak, Selesai
    private long waktuPengajuan;
    private LocalDateTime waktuBooking;
    private LocalDateTime waktuSelesai;

    public Booking(Mahasiswa pemesan, Ruang ruang, String hari, LocalTime jamMulai, LocalTime jamSelesai) {
        this.pemesan = pemesan;
        this.ruang = ruang;
        this.hari = hari;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
        this.status = "Menunggu";
        this.waktuPengajuan = System.currentTimeMillis();
        this.waktuBooking = LocalDateTime.now().with(jamMulai);
        this.waktuSelesai = LocalDateTime.now().with(jamSelesai);
    }

    // Getters
    public Mahasiswa getPemesan() { return pemesan; }
    public Ruang getRuang() { return ruang; }
    public String getHari() { return hari; }
    public LocalTime getJamMulai() { return jamMulai; }
    public LocalTime getJamSelesai() { return jamSelesai; }
    public String getStatus() { return status; }
    public long getWaktuPengajuan() { return waktuPengajuan; }
    public LocalDateTime getWaktuBooking() { return waktuBooking; }
    public LocalDateTime getWaktuSelesai() { return waktuSelesai; }

    public void setStatus(String status) { this.status = status; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(waktuSelesai) && status.equals("Diterima");
    }

    public void checkAndUpdateStatus() {
        if (isExpired() && status.equals("Diterima")) {
            this.status = "Selesai";
        }
    }

    public void tampilkanInfo() {
        System.out.println("Booking oleh: " + pemesan.getUsername()
                + " | Ruang: " + ruang.getNama()
                + " | " + hari + " " + jamMulai + "-" + jamSelesai
                + " | Status: " + status);
    }
}