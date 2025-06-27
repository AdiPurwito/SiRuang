package model;

public class Mahasiswa extends User {
    private String fakultas;
    private String prodi;

    public Mahasiswa(String username, String password, String nama, String fakultas, String prodi) {
        super(username, password, nama);
        this.fakultas = fakultas;
        this.prodi = prodi;
    }

    public String getFakultas() { return fakultas; }
    public String getProdi() { return prodi; }

    @Override
    public void tampilkanDashboard() {
        System.out.println("Selamat datang, " + nama + " (Mahasiswa)");
    }
}