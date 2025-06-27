package model;

public abstract class User {
    protected String username;
    protected String password;
    protected String nama;

    public User(String username, String password, String nama) {
        this.username = username;
        this.password = password;
        this.nama = nama;
    }

    public String getNama() { return nama;
    }
    public String getUsername() { return username; }
    public boolean checkPassword(String pwd) { return this.password.equals(pwd); }

    public abstract void tampilkanDashboard();
}
