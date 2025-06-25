package model;

public class Ruang {

    private String nama;
    private String gedung;
    private int kapasitas;

    public Ruang(String nama, String gedung, int kapasitas) {
        this.nama = nama;
        this.gedung = gedung;
        this.kapasitas = kapasitas;
    }

    public String getNama() {
        return nama;
    }
    public String getGedung() {
        return gedung;
    }

    public int getKapasitas() {
        return kapasitas;
    }

    public void tampilkanInfo() {
        System.out.println("Ruang " + nama + " | Gedung " + gedung + " | Kapasitas " + kapasitas);
    }
}
