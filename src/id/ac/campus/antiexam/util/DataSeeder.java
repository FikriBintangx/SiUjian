package id.ac.campus.antiexam.util;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import java.sql.Connection;
import java.sql.Statement;

/**
 * =============================================================================
 * KELAS DATA SEEDER (PENGISI DATA AWAL)
 * =============================================================================
 * File ini berguna untuk membuat AKUN DEFAULT (Bawaan) saat aplikasi pertama
 * kali dijalankan.
 * Jika Anda ingin mengubah Password Admin, Dosen, atau Pengawas bawaan, edit di
 * bagian bawah!
 */
public class DataSeeder {

    // Main method: Bisa dijalankan terpisah (Run File) untuk reset data awal
    public static void main(String[] args) {
        seed();
    }

    public static void seed() {
        System.out.println("ðŸŒ± Memulai Pengisian Data Awal (Seeder)...");
        try (Connection conn = KoneksiDatabase.getConnection()) {

            // 1. BUAT TABEL (Jika belum ada)
            Statement stmt = conn.createStatement();

            // Tabel Admin
            stmt.execute("CREATE TABLE IF NOT EXISTS admin (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT)");

            // Tabel Dosen
            stmt.execute("CREATE TABLE IF NOT EXISTS dosen (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT, " +
                    "name TEXT)");

            // Tabel Pengawas (Proctor)
            stmt.execute("CREATE TABLE IF NOT EXISTS pengawas (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT, " +
                    "name TEXT)");

            // Tabel Mahasiswa
            stmt.execute("CREATE TABLE IF NOT EXISTS mahasiswa (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "name TEXT, " +
                    "nim TEXT UNIQUE, " +
                    "nama_kelas TEXT)");

            // Tabel Ujian
            stmt.execute("CREATE TABLE IF NOT EXISTS ujian (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "token TEXT, code TEXT, target_kelas TEXT, title TEXT, kode_matkul TEXT, " +
                    "type TEXT, durasi_menit INTEGER, jadwal_waktu DATETIME, status TEXT)");

            // =====================================================================
            // EDIT AKUN & PASSWORD BAWAAN DI SINI
            // =====================================================================

            // 2. MASUKKAN DATA TESTING (Bawaan)

            // [A] AKUN ADMIN
            // Ganti 'admin' dan 'admin123' sesuai keinginan Anda
            insertOrSkip(conn, "INSERT INTO admin (username, password) VALUES ('admin', 'admin123')");

            // [B] AKUN DOSEN
            // Ganti 'dosen' dan 'dosen123'
            insertOrSkip(conn,
                    "INSERT INTO dosen (username, password, name) VALUES ('dosen', 'dosen123', 'Dr. Budi Santoso')");

            // [C] AKUN PENGAWAS
            // Ganti 'pengawas' dan 'pengawas123'
            insertOrSkip(conn,
                    "INSERT INTO pengawas (username, password, name) VALUES ('pengawas', 'pengawas123', 'Siti Aminah, S.Kom')");

            // [D] AKUN MAHASISWA TEST
            // NIM: 12345678 (User Test)
            insertOrSkip(conn,
                    "INSERT INTO mahasiswa (name, nim, nama_kelas) VALUES ('Mahasiswa Test', '12345678', 'TI-2024')");

            // =====================================================================

            System.out.println("âœ… Data Awal Berhasil Dibuat!");
            System.out.println("   -------------------------------------------------");
            System.out.println("   ðŸ”‘ Admin    : admin / admin123");
            System.out.println("   ðŸ”‘ Dosen    : dosen / dosen123");
            System.out.println("   ðŸ”‘ Pengawas : pengawas / pengawas123");
            System.out.println("   ðŸ”‘ Student  : NIM 12345678");
            System.out.println("   -------------------------------------------------");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("â Œ Seeder Gagal: " + e.getMessage());
        }
    }

    // Fungsi bantu: Insert data tapi jangan error kalau datanya udah ada
    private static void insertOrSkip(Connection conn, String sql) {
        try {
            conn.createStatement().execute(sql);
        } catch (Exception e) {
            // Abaikan error kalau data duplikat (artinya data sudah aman)
        }
    }
}
