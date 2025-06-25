package model;

public class Admin extends User {

    public Admin(String username, String password, String nama) {
        super(username, password, nama);
    }

    @Override
    public void tampilkanDashboard() {
        System.out.println("Selaat datang, " + nama + " (Admin)");
    }
}
