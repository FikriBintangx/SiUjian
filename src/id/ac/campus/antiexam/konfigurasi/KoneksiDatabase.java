package id.ac.campus.antiexam.konfigurasi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 * =============================================================================
 * KONFIGURASI DATABASE (TIDB CLOUD)
 * =============================================================================
 * File ini mengatur koneksi aplikasi ke TiDB Cloud.
 */
public class KoneksiDatabase {

    // === [AREA EDIT CREDENTIALS TIDB CLOUD] ===
    // ISI BAGIAN INI DENGAN DATA DARI DASHBOARD TIDB:

    // 1. Copy HOST dari dashboard (misal: gateway01.ap-..tidbcloud.com)
    private static final String DB_HOST = "gateway01.ap-northeast-1.prod.aws.tidbcloud.com";

    // 2. Klik tombol "Generate Password" di dashboard, lalu paste di sini:
    private static final String DB_PASS = "9pDHMx3FHN2WUPhj";

    // 3. Username (Dari screenshot kakak tadi)
    private static final String DB_USER = "PHyPtaJ8HX3VMXn.root";

    private static final String DB_PORT = "4000"; // Port TiDB
    private static final String DB_NAME = "test"; // Database default TiDB

    // ==========================================

    private static Connection koneksi;

    public static Connection getConnection() {
        try {
            // Cek apakah koneksi sudah ada dan masih hidup?
            if (koneksi == null || koneksi.isClosed() || !koneksi.isValid(2)) {

                // 1. Panggil Driver MySQL
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException e) {
                    System.err.println("[PANIC] Driver MySQL Tidak Ditemukan!");
                    e.printStackTrace();
                    return null;
                }

                // 2. Siapkan URL Koneksi
                // Tips: Tambahkan useSSL=true jika pakai Cloud biar aman (dan kadang wajib)
                // 4. ULTIMATE URL (Force TLS 1.2)
                String url = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME
                        + "?useSSL=true"
                        + "&requireSSL=true"
                        + "&verifyServerCertificate=false"
                        + "&allowPublicKeyRetrieval=true"
                        + "&enabledTLSProtocols=TLSv1.2,TLSv1.3"; // Explicit TLS version

                System.out.println("[INFO] Menghubungkan ke Database Cloud: " + DB_HOST + "...");
                koneksi = DriverManager.getConnection(url, DB_USER, DB_PASS);
                System.out.println("[SUKSES] Terhubung ke Database Cloud!");
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Gagal Koneksi: " + e.getMessage());

            // Tampilkan Pesan Error yang Manusiawi
            String msg = "Gagal terhubung ke Database Cloud (" + DB_HOST + ")!\n\n" +
                    "Pastikan hal berikut:\n" +
                    "1. Internet Anda lancar (Wajib Online).\n" +
                    "2. Password TiDB di file KoneksiDatabase.java sudah diisi.\n" +
                    "3. Host TiDB sudah benar.\n\n" +
                    "Error Teknis: " + e.getMessage();

            JOptionPane.showMessageDialog(null, msg, "Koneksi Cloud Gagal", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return koneksi;
    }

    // Method Main untuk Test Koneksi doang (Klik Kanan -> Run File)
    public static void main(String[] args) {
        getConnection();
    }
}
