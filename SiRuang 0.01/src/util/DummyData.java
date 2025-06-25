package util;

import model.*;

import java.util.ArrayList;
import java.time.LocalTime;

public class DummyData {
    public static ArrayList<User> getUserList() {
        ArrayList<User> users = new ArrayList<>();
        users.add(new Admin("admin", "123", "Admin Kampus"));
        users.add(new Mahasiswa("mahasiswa", "123", "Adi", "FT", "TI"));
        return users;
    }

    public static ArrayList<Ruang> getRuangList() {
        ArrayList<Ruang> ruang = new ArrayList<>();
        ruang.add(new Ruang("R101", "GKB1", 30));
        ruang.add(new Ruang("R102", "GKB2", 40));
        return ruang;
    }

    public static ArrayList<Jadwal> getJadwalList(ArrayList<Ruang> ruangList) {
        ArrayList<Jadwal> jadwal = new ArrayList<>();
        jadwal.add(new Jadwal("Senin", LocalTime.of(8, 0), LocalTime.of(10, 0), "PBO", ruangList.get(0)));
        return jadwal;
    }
}
