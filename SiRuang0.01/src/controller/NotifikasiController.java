package controller;

import model.Notifikasi;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotifikasiController {
    private static ArrayList<Notifikasi> notifikasi = new ArrayList<>();

    public static void addNotification(String pesan, String tipe, String penerima) {
        notifikasi.add(new Notifikasi(pesan, tipe, penerima));
    }

    public static List<Notifikasi> getNotificationsForUser(String username) {
        return notifikasi.stream()
                .filter(n -> n.getPenerima().equals(username))
                .collect(Collectors.toList());
    }

    public static List<Notifikasi> getUnreadNotificationsForUser(String username) {
        return notifikasi.stream()
                .filter(n -> n.getPenerima().equals(username) && !n.isDibaca())
                .collect(Collectors.toList());
    }

    public static void markAsRead(String username) {
        notifikasi.stream()
                .filter(n -> n.getPenerima().equals(username))
                .forEach(n -> n.setDibaca(true));
    }
}