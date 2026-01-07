package id.ac.campus.antiexam.data;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; // Changed from Exception to SQLException
import java.util.ArrayList;
import java.util.List;

public class DosenData {

    public boolean checkLogin(String username, String password) throws SQLException { // Changed from Exception to
                                                                                      // SQLException
        String sql = "SELECT id FROM dosen WHERE username = ? AND password = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Object[]> listLecturers() throws SQLException { // Changed from Exception to SQLException
        List<Object[]> list = new ArrayList<>();
        // Changed 'name' to 'nama_lengkap' in SELECT and ORDER BY
        String sql = "SELECT id, username, nama_lengkap FROM dosen ORDER BY nama_lengkap ASC";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[] {
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("nama_lengkap") // Changed 'name' to 'nama_lengkap'
                });
            }
        }
        return list;
    }

    public void createLecturer(String username, String password, String name) throws SQLException { // Changed from
                                                                                                    // Exception to
                                                                                                    // SQLException
        // FIX: nip default '-' karena di database NOT NULL, name -> nama_lengkap
        // Changed 'name' to 'nama_lengkap' in INSERT and added 'nip' column
        String sql = "INSERT INTO dosen (username, password, nama_lengkap, nip) VALUES (?, ?, ?, '-')";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, name); // 'name' parameter maps to 'nama_lengkap' column
            ps.executeUpdate();
        }
    }

    public void updateLecturer(int id, String username, String name) throws SQLException { // Changed from Exception to
                                                                                           // SQLException
        // Changed 'name' to 'nama_lengkap' in UPDATE
        String sql = "UPDATE dosen SET username = ?, nama_lengkap = ? WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, name); // 'name' parameter maps to 'nama_lengkap' column
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public void deleteLecturerByUsername(String username) throws Exception {
        String sql = "DELETE FROM dosen WHERE username = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        }
    }
}
