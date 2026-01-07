package id.ac.campus.antiexam.konfigurasi;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * =============================================================================
 * KELAS INISIALISASI DATABASE
 * =============================================================================
 * Kelas ini bertugas membuat TABEL dan mengisi DATA CONTOH (Dummy) secara
 * otomatis
 * saat aplikasi pertama kali dijalankan.
 * 
 * - Jika Anda ingin MERESET database, Anda bisa menghapus database di
 * phpMyAdmin/cPanel,
 * lalu jalankan aplikasi ini lagi. Otomatis tabel akan dibuat ulang.
 * 
 * - Lihat method 'seedDummyData' di paling bawah untuk melihat daftar User &
 * Password bawaan.
 */
public class InisialisasiDatabase {

        public static void initialize() {
                try (Connection conn = KoneksiDatabase.getConnection();
                                Statement stmt = conn.createStatement()) {

                        // =================================================================
                        // 1. BAGIAN PEMBUATAN TABEL (CREATE TABLE)
                        // =================================================================
                        // Kode di bawah ini mengecek: "Kalau tabel belum ada, buat dong!"

                        // Tabel Admin
                        stmt.execute("CREATE TABLE IF NOT EXISTS admin (" +
                                        "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                                        "username VARCHAR(255) NOT NULL UNIQUE, " + // FIX: TEXT -> VARCHAR
                                        "password TEXT NOT NULL)");

                        // Tabel Dosen (Lecturers)
                        stmt.execute("CREATE TABLE IF NOT EXISTS dosen (" +
                                        "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                                        "username VARCHAR(255) NOT NULL UNIQUE, " + // FIX: TEXT -> VARCHAR
                                        "password TEXT NOT NULL, " +
                                        "name TEXT NOT NULL)");

                        // Tabel Pengawas (Proctors)
                        stmt.execute("CREATE TABLE IF NOT EXISTS pengawas (" +
                                        "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                                        "username VARCHAR(255) NOT NULL UNIQUE, " + // FIX: TEXT -> VARCHAR
                                        "password TEXT NOT NULL, " +
                                        "name TEXT NOT NULL)");

                        // Tabel Mahasiswa (Students)
                        stmt.execute("CREATE TABLE IF NOT EXISTS mahasiswa (" +
                                        "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                                        "name TEXT NOT NULL, " +
                                        "nim VARCHAR(255) NOT NULL UNIQUE, " + // FIX: TEXT -> VARCHAR
                                        "nama_kelas TEXT NOT NULL)");

                        // Tabel Mata Kuliah
                        stmt.execute("CREATE TABLE IF NOT EXISTS mata_kuliah (" +
                                        "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                                        "kode_matkul TEXT NOT NULL, " +
                                        "name TEXT NOT NULL, " +
                                        "nama_kelas TEXT NOT NULL, " +
                                        "username_dosen TEXT NOT NULL, " +
                                        "username_pengawas TEXT, " +
                                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

                        // Tabel Ruangan
                        stmt.execute("CREATE TABLE IF NOT EXISTS ruangan (" +
                                        "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                                        "name VARCHAR(255) NOT NULL UNIQUE)"); // FIX: TEXT -> VARCHAR

                        // Tabel Ujian
                        stmt.execute("CREATE TABLE IF NOT EXISTS ujian (" +
                                        "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                                        "code TEXT NOT NULL, " +
                                        "target_kelas TEXT NOT NULL, " +
                                        "title TEXT NOT NULL, " +
                                        "kode_matkul TEXT NOT NULL, " +
                                        "type VARCHAR(50) NOT NULL, " + // FIX: TEXT -> VARCHAR
                                        "exam_mode VARCHAR(50) DEFAULT 'PG', " +
                                        "jadwal_waktu TIMESTAMP, " +
                                        "tahun_akademik INTEGER, " +
                                        "durasi_menit INTEGER, " +
                                        "username_dosen TEXT NOT NULL, " +
                                        "username_pengawas TEXT, " +
                                        "ruangan TEXT, " +
                                        "path_file_soal TEXT, " +
                                        "token TEXT, " +
                                        "broadcast_message TEXT, " +
                                        "status VARCHAR(50) DEFAULT 'SCHEDULED', " + // FIX: TEXT -> VARCHAR biar bisa
                                                                                     // DEFAULT
                                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

                        // Tabel Ujian Mahasiswa (Log ujian per siswa)
                        stmt.execute("CREATE TABLE IF NOT EXISTS ujian_mahasiswa (" +
                                        "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                                        "id_ujian INTEGER NOT NULL, " +
                                        "id_mahasiswa INTEGER NOT NULL, " +
                                        "status TEXT NOT NULL, " +
                                        "violation_count INTEGER DEFAULT 0, " +
                                        "nilai INTEGER DEFAULT 0, " +
                                        "correct_answers INTEGER DEFAULT 0, " +
                                        "wrong_answers INTEGER DEFAULT 0, " +
                                        "waktu_mulai TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                        "waktu_selesai TIMESTAMP, " +
                                        "FOREIGN KEY(id_ujian) REFERENCES ujian(id), " +
                                        "FOREIGN KEY(id_mahasiswa) REFERENCES mahasiswa(id))");

                        // Tabel Bank Soal Ujian
                        stmt.execute("CREATE TABLE IF NOT EXISTS soal_ujian (" +
                                        "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                                        "id_ujian INTEGER NOT NULL, " +
                                        "type TEXT, " +
                                        "pertanyaan TEXT, " +
                                        "option_a TEXT, " +
                                        "option_b TEXT, " +
                                        "option_c TEXT, " +
                                        "option_d TEXT, " +
                                        "kunci_jawaban TEXT, " +
                                        "paket_soal VARCHAR(10) DEFAULT 'SEMUA', " + // BARU: SEMUA, GANJIL, GENAP
                                        "FOREIGN KEY(id_ujian) REFERENCES ujian(id))");

                        // Tabel Jawaban Mahasiswa
                        stmt.execute("CREATE TABLE IF NOT EXISTS jawaban (" +
                                        "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                                        "id_ujian_mahasiswa INTEGER NOT NULL, " +
                                        "nomor_soal INTEGER, " +
                                        "jawaban TEXT, " +
                                        "FOREIGN KEY(id_ujian_mahasiswa) REFERENCES ujian_mahasiswa(id))");

                        // =================================================================
                        // 2. BAGIAN PENGISIAN DATA CONTOH (SEEDER)
                        // =================================================================
                        seedDummyData(stmt);

                        System.out.println("[INFO] Database berhasil diinisialisasi & tabel siap digunakan.");

                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }

        // Fungsi ini isinya data-data palsu (Dummy) buat testing
        private static void seedDummyData(Statement stmt) throws SQLException {

                // Helper biar pendek
                String passAdmin = id.ac.campus.antiexam.util.KeamananService.hashPassword("admin123");
                String passDosen = id.ac.campus.antiexam.util.KeamananService.hashPassword("123456");

                // ---------------------------------------------------------------------
                // DATA ADMIN Bawaan
                // ---------------------------------------------------------------------
                // Password: admin123
                insertOrIgnore(stmt, "INSERT INTO admin (username, password) VALUES ('ADMIN1', '" + passAdmin + "')");
                insertOrIgnore(stmt, "INSERT INTO admin (username, password) VALUES ('ADMIN909', '" + passAdmin + "')");

                // ---------------------------------------------------------------------
                // DATA DOSEN Bawaan
                // ---------------------------------------------------------------------
                // Format: Username (NIDN), Password, Nama
                insertOrIgnore(stmt,
                                "INSERT INTO dosen (username, password, name) VALUES ('11009010', '" + passDosen
                                                + "', 'Arif Nurochman')");
                insertOrIgnore(stmt,
                                "INSERT INTO dosen (username, password, name) VALUES ('11009011', '" + passDosen
                                                + "', 'Jalal')");
                insertOrIgnore(stmt,
                                "INSERT INTO dosen (username, password, name) VALUES ('11009012', '" + passDosen
                                                + "', 'Ramadhan')");

                // ---------------------------------------------------------------------
                // DATA PENGAWAS Bawaan
                // ---------------------------------------------------------------------
                insertOrIgnore(stmt,
                                "INSERT INTO pengawas (username, password, name) VALUES ('Petugas1', '" + passDosen
                                                + "', 'Petugas Ujian')");

                // ---------------------------------------------------------------------
                // DATA MAHASISWA Bawaan
                // ---------------------------------------------------------------------
                // Format: Nama, NIM (Username), Kelas
                insertOrIgnore(stmt,
                                "INSERT INTO mahasiswa (name, nim, nama_kelas) VALUES ('Fikri Bintang', '1124140140', 'IF-21-A')");
                insertOrIgnore(stmt,
                                "INSERT INTO mahasiswa (name, nim, nama_kelas) VALUES ('Ahmad ilyas', '1124140141', 'IF-21-A')");
                insertOrIgnore(stmt,
                                "INSERT INTO mahasiswa (name, nim, nama_kelas) VALUES ('Anom rizki', '1124140142', 'IF-21-A')");
                insertOrIgnore(stmt,
                                "INSERT INTO mahasiswa (name, nim, nama_kelas) VALUES ('Budi lope', '1124140143', 'IF-21-A')");
                insertOrIgnore(stmt,
                                "INSERT INTO mahasiswa (name, nim, nama_kelas) VALUES ('Deny dermawan', '1124140144', 'IF-21-A')");

                // ---------------------------------------------------------------------
                // DATA RUANGAN
                // ---------------------------------------------------------------------
                String[] rooms = { "Lab Komputer 1", "Lab Komputer 2", "Lab Jaringan", "R. 301", "R. 302" };
                for (String r : rooms) {
                        insertOrIgnore(stmt, "INSERT INTO ruangan (name) VALUES ('" + r + "')");
                }

                // ---------------------------------------------------------------------
                // DATA MATKUL & UJIAN CONTOH
                // ---------------------------------------------------------------------
                // Matkul 1: Struktur Data
                insertOrIgnore(stmt,
                                "INSERT INTO mata_kuliah (kode_matkul, name, nama_kelas, username_dosen) VALUES ('IF202', 'Struktur Data', 'IF-21-A', '11009010')");

                // Matkul 2: Algoritma
                insertOrIgnore(stmt,
                                "INSERT INTO mata_kuliah (kode_matkul, name, nama_kelas, username_dosen) VALUES ('IF101', 'Algoritma', 'IF-21-A', '11009011')");

                // Matkul 3: Pemrograman Web
                insertOrIgnore(stmt,
                                "INSERT INTO mata_kuliah (kode_matkul, name, nama_kelas, username_dosen) VALUES ('IF305', 'Pemrograman Web', 'IF-21-A', '11009012')");
        }

        // Helper biar gak error kalau data udah ada (pakai Try-Catch)
        private static void insertOrIgnore(Statement stmt, String sql) {
                try {
                        stmt.execute(sql);
                } catch (SQLException e) {
                        // Data sudah ada, abaikan saja error-nya (aman)
                }
        }

        public static void main(String[] args) {
                initialize();
        }
}
