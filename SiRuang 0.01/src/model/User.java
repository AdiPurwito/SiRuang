package model;

public abstract class  User {

    protected String username;
    protected String password;
    protected String nama;

    public User(String username, String password, String nama) {
        this.username = username;
        this.password = password;
        this.nama = nama;
    }

    public String getUsername() {
        return  username;
    }

    public boolean cekPassword(String pwd) {
        return this.password.equalsIgnoreCase(pwd);
    }

    public abstract void tampilkanDashboard();
}
