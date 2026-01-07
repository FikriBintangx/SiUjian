package id.ac.campus.antiexam.data;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MataKuliahData {

    public void createSubject(String code, String name, int credits) throws SQLException {
        // FIX: Remove 'sks' from SQL because 'mata_kuliah' table doesn't have it.
        // Also ensure we use 'name' and 'kode_matkul' as verified before.
        String sql = "INSERT INTO mata_kuliah (kode_matkul, name) VALUES (?, ?)";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, name);
            // credits (sks) is ignored because no column exists
            ps.executeUpdate();
        }
    }

    public void updateSubject(int id, String code, String name, int credits) throws SQLException {
        // FIX: Remove 'sks' from SQL
        String sql = "UPDATE mata_kuliah SET kode_matkul = ?, name = ? WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, name);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public void deleteSubject(int id) throws SQLException {
        String sql = "DELETE FROM mata_kuliah WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Object[]> listSubjects() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        // FIX: Remove 'sks' from SQL
        String sql = "SELECT id, kode_matkul, name FROM mata_kuliah ORDER BY name ASC";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[] {
                        rs.getInt("id"),
                        rs.getString("kode_matkul"),
                        rs.getString("name"),
                        3 // Default SKS dummy karena db ga punya kolom
                });
            }
        }
        return list;
    }

    public Object[] getSubjectByCode(String code) throws SQLException {
        // FIX: Remove 'sks' from SQL
        String sql = "SELECT id, kode_matkul, name FROM mata_kuliah WHERE kode_matkul = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[] {
                            rs.getInt("id"),
                            rs.getString("kode_matkul"),
                            rs.getString("name"),
                            3 // Default SKS
                    };
                }
            }
        }
        return null;
    }
}
