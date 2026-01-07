package id.ac.campus.antiexam.data;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * =============================================================================
 * KELAS DATA OTENTIKASI (LOGIN LOGIC)
 * =============================================================================
 * Kelas ini berisi logika pengecekan Username & Password ke Database.
 * Jika Login gagal, cek query SQL di file ini.
 */
public class AuthData {

    /**
     * Cek Login Mahasiswa
     * 
     * @param password (Optional/Diabaikan saat ini untuk kemudahan)
     * @param nim      Nomor Induk Mahasiswa
     * @return Data Mahasiswa [id, nama_kelas] jika sukses, null jika gagal
     */
    public String[] getStudentDetails(String password, String nim) throws Exception {
        // Saat ini login Mahasiswa hanya butuh NIM saja (Password diabaikan biar
        // gampang demo)
        String sql = "SELECT id, nama_kelas FROM mahasiswa WHERE nim = ?";

        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nim);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Balikin ID dan Kelas mahasiswa
                    return new String[] {
                            String.valueOf(rs.getInt("id")),
                            rs.getString("nama_kelas")
                    };
                }
            }
        }
        return null; // Login Gagal (NIM tidak ditemukan)
    }

    /**
     * Cek Login Dosen
     * 
     * @param nidn     NIDN (Username)
     * @param password Password Dosen
     */
    public boolean loginLecturer(String nidn, String password) throws Exception {
        // Cek tabel 'dosen', cocokkan username & password
        String sql = "SELECT id FROM dosen WHERE username = ? AND password = ?";

        // HASH PASSWORD DULU SEBELUM CEK KE DB
        String securePassword = id.ac.campus.antiexam.util.KeamananService.hashPassword(password);

        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nidn);
            ps.setString(2, securePassword);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Kalau ada hasilnya, berarti login sukses
            }
        }
    }

    /**
     * Cek Login Admin
     */
    public boolean loginAdmin(String username, String password) throws Exception {
        String sql = "SELECT id FROM admin WHERE username = ? AND password = ?";

        // HASH PASSWORD
        String securePassword = id.ac.campus.antiexam.util.KeamananService.hashPassword(password);

        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, securePassword);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Cek Login Pengawas
     */
    public boolean loginProctor(String username, String password) throws Exception {
        String sql = "SELECT id FROM pengawas WHERE username = ? AND password = ?";

        // HASH PASSWORD
        String securePassword = id.ac.campus.antiexam.util.KeamananService.hashPassword(password);

        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, securePassword);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
