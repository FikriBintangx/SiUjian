package id.ac.campus.antiexam.konfigurasi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class CekAkun {

    public static void main(String[] args) {
        System.out.println("=== MELAKUKAN DIAGNOSA AKUN ADMIN ===");

        try (Connection conn = KoneksiDatabase.getConnection()) {
            if (conn == null) {
                System.out.println("[FATAL] Tidak bisa konek database. Cek internet/password TiDB.");
                return;
            }

            // 1. Cek Tabel Admin
            String sqlCheck = "SELECT count(*) FROM admin";
            try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sqlCheck)) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("[INFO] Jumlah Admin di Database: " + count);

                    if (count == 0) {
                        System.out.println("[WARNING] Tabel Kosong! Melakukan suntik data darurat...");
                        suntukDataAdmin(conn);
                    } else {
                        tampilkanDataAdmin(conn);
                    }
                }
            } catch (Exception e) {
                System.out.println("[ERROR] Tabel 'admin' belum ada? " + e.getMessage());
                // Coba inisialisasi ulang
                InisialisasiDatabase.initialize();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void suntukDataAdmin(Connection conn) {
        try {
            String sql = "INSERT INTO admin (username, password) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "ADMIN909");
                ps.setString(2, "admin123");
                ps.executeUpdate();
                System.out.println("[SUKSES] Akun ADMIN909 / admin123 berhasil dibuat!");

                ps.setString(1, "ADMIN1");
                ps.setString(2, "admin123");
                ps.executeUpdate();
                System.out.println("[SUKSES] Akun ADMIN1 / admin123 berhasil dibuat!");
            }
        } catch (Exception e) {
            System.err.println("[GAGAL] Gagal suntik data: " + e.getMessage());
        }
    }

    private static void tampilkanDataAdmin(Connection conn) {
        try {
            System.out.println("\n--- DAFTAR ADMIN DI DATABASE ---");
            String sql = "SELECT id, username, password FROM admin";
            try (Statement s = conn.createStatement(); ResultSet rs = s.executeQuery(sql)) {
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") +
                            " | User: " + rs.getString("username") +
                            " | Pass: " + rs.getString("password"));
                }
            }
            System.out.println("--------------------------------\n");
        } catch (Exception e) {
        }
    }
}
