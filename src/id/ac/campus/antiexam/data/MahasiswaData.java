package id.ac.campus.antiexam.data;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MahasiswaData {

    public void createStudent(String studentNumber, String name, String className) throws SQLException {
        // MAPPING SKEMA: nimble -> nim, name -> nama_lengkap, nama_kelas -> jurusan
        String sql = "INSERT INTO mahasiswa (nim, nama_lengkap, jurusan, username, password) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentNumber);
            ps.setString(2, name);
            ps.setString(3, className); // Disimpan ke kolom 'jurusan'
            ps.setString(4, studentNumber); // Username default = NIM
            ps.setString(5, "123456"); // Password default
            ps.executeUpdate();
        }
    }

    public void updateStudent(int id, String studentNumber, String name, String className) throws SQLException {
        String sql = "UPDATE mahasiswa SET nim = ?, nama_lengkap = ?, jurusan = ? WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentNumber);
            ps.setString(2, name);
            ps.setString(3, className);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }

    public void deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM mahasiswa WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Object[]> listStudents() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        // Sesuaikan query select
        String sql = "SELECT id, nim, nama_lengkap, jurusan FROM mahasiswa ORDER BY jurusan ASC, nama_lengkap ASC";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[] {
                        rs.getInt("id"),
                        rs.getString("nim"),
                        rs.getString("nama_lengkap"),
                        rs.getString("jurusan") // Alias nama_kelas
                });
            }
        }
        return list;
    }

    public Object[] getStudentById(int id) throws SQLException {
        String sql = "SELECT id, nim, nama_lengkap, jurusan FROM mahasiswa WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[] {
                            rs.getInt("id"),
                            rs.getString("nim"),
                            rs.getString("nama_lengkap"),
                            rs.getString("jurusan")
                    };
                }
            }
        }
        return null;
    }
}
