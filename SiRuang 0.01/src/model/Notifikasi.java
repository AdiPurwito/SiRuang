package model;

import java.time.LocalDateTime;

public class Notifikasi {
    private String pesan;
    private LocalDateTime waktu;
    private String tipe; // INFO, SUCCESS, WARNING, ERROR
    private boolean dibaca;
    private String penerima; // username

    public Notifikasi(String pesan, String tipe, String penerima) {
        this.pesan = pesan;
        this.tipe = tipe;
        this.penerima = penerima;
        this.waktu = LocalDateTime.now();
        this.dibaca = false;
    }

    // Getters and Setters
    public String getPesan() { return pesan; }
    public LocalDateTime getWaktu() { return waktu; }
    public String getTipe() { return tipe; }
    public boolean isDibaca() { return dibaca; }
    public String getPenerima() { return penerima; }
    public void setDibaca(boolean dibaca) { this.dibaca = dibaca; }
}
