package model;

import java.time.LocalTime;

public class Jadwal {
    private String hari;
    private LocalTime jamMulai;
    private LocalTime jamSelesai;
    private String matkul;
    private Ruang ruang;

    public Jadwal(String hari, LocalTime jamMulai, LocalTime jamSelesai, String matkul, Ruang ruang) {
        this.hari = hari;
        this.jamMulai = jamMulai;
        this.jamSelesai = jamSelesai;
        this.matkul = matkul;
        this.ruang = ruang;
    }

    public boolean isWaktuBentrok(String hariCek, LocalTime jamCek) {
        return hari.equalsIgnoreCase(hariCek) && (jamCek.isAfter(jamMulai) && jamCek.isBefore(jamSelesai));
    }

    public Ruang getRuang() {
        return ruang;
    }

    public String getHari() {
        return hari;
    }

    public LocalTime getJamMulai() {
        return jamMulai;
    }

    public LocalTime getJamSelesai() {
        return jamSelesai;
    }


    public void tampilkan() {
        System.out.println(hari + " " + jamMulai + "-" + jamSelesai + " | " + matkul + " di ruang " + ruang.getNama());
    }
}
