package id.ac.campus.antiexam.util;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * SERVICE KEAMANAN (SECURITY)
 * ---------------------------
 * Kelas ini bertugas untuk enkripsi/hashing password agar tidak tersimpan
 * sebagai Plain Text.
 * Menggunakan algoritma SHA-256.
 */
public class KeamananService {

    /**
     * Mengubah text biasa menjadi Hash SHA-256
     * 
     * @param plainText Password asli (misal: "admin123")
     * @return String Hash yang aman
     */
    public static String hashPassword(String plainText) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainText.getBytes(StandardCharsets.UTF_8));
            // Convert byte array to Base64 String biar gampang disimpan
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception ex) {
            ex.printStackTrace();
            return plainText; // Fallback kalau error (jarang terjadi)
        }
    }
}
