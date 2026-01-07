-- DATABASE SIUJIAN CBT - FULL STRUCTURE & DATA DUMMY
-- Cocok untuk Import cPanel

SET FOREIGN_KEY_CHECKS=0;
SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+07:00";

-- ==========================================
-- 1. TABEL ADMIN
-- ==========================================
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `nama_lengkap` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `admin` (`id`, `username`, `password`, `nama_lengkap`) VALUES
(1, 'admin', 'admin123', 'Administrator Utama'),
(2, 'staff_tu', '123456', 'Staff Tata Usaha'),
(3, 'akademik', '123456', 'Bagian Akademik'),
(4, 'it_support', '123456', 'Tim IT Support'),
(5, 'kepala_lab', '123456', 'Kepala Lab Komputer');

-- ==========================================
-- 2. TABEL DOSEN
-- ==========================================
DROP TABLE IF EXISTS `dosen`;
CREATE TABLE `dosen` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nip` varchar(20) NOT NULL,
  `nama_lengkap` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nip` (`nip`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `dosen` (`id`, `nip`, `nama_lengkap`, `username`, `password`) VALUES
(1, '1001', 'Budi Santoso, M.Kom', 'dosen1', 'dosen123'),
(2, '1002', 'Siti Aminah, S.T., M.T.', 'dosen2', '123456'),
(3, '1003', 'Prof. Dr. Rahmat Hidayat', 'dosen3', '123456'),
(4, '1004', 'Dewi Lestari, M.Cs', 'dosen4', '123456'),
(5, '1005', 'Andi Wijaya, Ph.D', 'dosen5', '123456');

-- ==========================================
-- 3. TABEL MAHASISWA
-- ==========================================
DROP TABLE IF EXISTS `mahasiswa`;
CREATE TABLE `mahasiswa` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nim` varchar(20) NOT NULL,
  `nama_lengkap` varchar(100) NOT NULL,
  `jurusan` varchar(50) DEFAULT NULL,
  `angkatan` int(4) DEFAULT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nim` (`nim`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `mahasiswa` (`id`, `nim`, `nama_lengkap`, `jurusan`, `angkatan`, `username`, `password`) VALUES
(1, '20001', 'Fikri Bintang', 'Informatika', 2023, 'mhs1', 'mhs123'),
(2, '20002', 'Ahmad Dani', 'Sistem Informasi', 2023, 'mhs2', '123456'),
(3, '20003', 'Citra Kirana', 'Teknik Komputer', 2023, 'mhs3', '123456'),
(4, '20004', 'Doni Tata', 'Informatika', 2023, 'mhs4', '123456'),
(5, '20005', 'Eka Putra', 'Manajemen Informatika', 2023, 'mhs5', '123456');

-- ==========================================
-- 4. TABEL PENGAWAS
-- ==========================================
DROP TABLE IF EXISTS `pengawas`;
CREATE TABLE `pengawas` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nip_petugas` varchar(20) NOT NULL,
  `nama_lengkap` varchar(100) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `pengawas` (`id`, `nip_petugas`, `nama_lengkap`, `username`, `password`) VALUES
(1, 'P001', 'Petugas Lab 1', 'pengawas1', 'pengawas123'),
(2, 'P002', 'Petugas Lab 2', 'pengawas2', '123456'),
(3, 'P003', 'Asisten Dosen A', 'asdos1', '123456'),
(4, 'P004', 'Asisten Dosen B', 'asdos2', '123456'),
(5, 'P005', 'Teknisi Jaringan', 'teknisi', '123456');

-- ==========================================
-- 5. TABEL MATA KULIAH
-- ==========================================
DROP TABLE IF EXISTS `mata_kuliah`;
CREATE TABLE `mata_kuliah` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `kode_mk` varchar(20) NOT NULL,
  `nama_mk` varchar(100) NOT NULL,
  `sks` int(2) DEFAULT 3,
  PRIMARY KEY (`id`),
  UNIQUE KEY `kode_mk` (`kode_mk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `mata_kuliah` (`id`, `kode_mk`, `nama_mk`, `sks`) VALUES
(1, 'TI101', 'Algoritma Pemrograman', 4),
(2, 'TI102', 'Basis Data', 3),
(3, 'TI103', 'Jaringan Komputer', 3),
(4, 'TI104', 'Pemrograman Berorientasi Objek', 4),
(5, 'TI105', 'Kecerdasan Buatan', 3);

-- ==========================================
-- 6. TABEL RUANGAN
-- ==========================================
DROP TABLE IF EXISTS `ruangan`;
CREATE TABLE `ruangan` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `kode_ruangan` varchar(20) NOT NULL,
  `nama_ruangan` varchar(50) NOT NULL,
  `kapasitas` int(11) DEFAULT 30,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `ruangan` (`id`, `kode_ruangan`, `nama_ruangan`, `kapasitas`) VALUES
(1, 'LAB-A', 'Laboratorium Komputer A', 40),
(2, 'LAB-B', 'Laboratorium Komputer B', 35),
(3, 'LAB-C', 'Laboratorium Multimedia', 30),
(4, 'R-101', 'Ruang Teori 101', 50),
(5, 'AULA', 'Aula Utama', 100);

-- ==========================================
-- 7. TABEL UJIAN (Jadwal)
-- ==========================================
DROP TABLE IF EXISTS `ujian`;
CREATE TABLE `ujian` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nama_ujian` varchar(100) NOT NULL,
  `mata_kuliah_id` int(11) NOT NULL,
  `dosen_id` int(11) NOT NULL,
  `ruangan_id` int(11) NOT NULL,
  `waktu_mulai` datetime NOT NULL,
  `durasi_menit` int(11) NOT NULL DEFAULT 90,
  `status` enum('draft','aktif','selesai') DEFAULT 'draft',
  `token_ujian` varchar(10) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `mata_kuliah_id` (`mata_kuliah_id`),
  KEY `dosen_id` (`dosen_id`),
  KEY `ruangan_id` (`ruangan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `ujian` (`id`, `nama_ujian`, `mata_kuliah_id`, `dosen_id`, `ruangan_id`, `waktu_mulai`, `durasi_menit`, `status`, `token_ujian`) VALUES
(1, 'UAS Algoritma 2025', 1, 1, 1, '2025-06-15 08:00:00', 90, 'aktif', 'ALGO25'),
(2, 'UTS Basis Data', 2, 2, 2, '2025-06-16 10:00:00', 60, 'draft', 'BD2025'),
(3, 'Kuis Jarkom 1', 3, 3, 3, '2025-06-17 13:00:00', 45, 'selesai', 'JARKOM1'),
(4, 'UAS PBO', 4, 1, 1, '2025-06-18 08:00:00', 100, 'draft', 'PBO123'),
(5, 'Ujian Susulan AI', 5, 5, 2, '2025-06-20 09:00:00', 60, 'draft', 'AIU20');

-- ==========================================
-- 8. TABEL SOAL UJIAN
-- ==========================================
DROP TABLE IF EXISTS `soal_ujian`;
CREATE TABLE `soal_ujian` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ujian_id` int(11) NOT NULL,
  `pertanyaan` text NOT NULL,
  `pilihan_a` text NOT NULL,
  `pilihan_b` text NOT NULL,
  `pilihan_c` text NOT NULL,
  `pilihan_d` text NOT NULL,
  `kunci_jawaban` enum('A','B','C','D') NOT NULL,
  `poin` int(11) DEFAULT 10,
  `tipe_soal` varchar(20) DEFAULT 'PILIHAN_GANDA',
  `gambar` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `ujian_id` (`ujian_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `soal_ujian` (`id`, `ujian_id`, `pertanyaan`, `pilihan_a`, `pilihan_b`, `pilihan_c`, `pilihan_d`, `kunci_jawaban`, `poin`) VALUES
(1, 1, 'Simbol flowchart untuk input/output adalah?', 'Persegi Panjang', 'Jajar Genjang', 'Belah Ketupat', 'Lingkaran', 'B', 10),
(2, 1, 'Manakah yang termasuk bahasa pemrograman?', 'HTML', 'CSS', 'Java', 'Photoshop', 'C', 10),
(3, 1, 'Apa output dari 10 % 3?', '1', '3', '0', '3.33', 'A', 10),
(4, 1, 'Tipe data untuk menyimpan karakter tunggal adalah?', 'String', 'Int', 'Char', 'Boolean', 'C', 10),
(5, 1, 'Looping yang minimal dijalankan satu kali adalah?', 'For', 'While', 'Do-While', 'If-Else', 'C', 10);

-- ==========================================
-- 9. TABEL JAWABAN (Peserta)
-- ==========================================
DROP TABLE IF EXISTS `jawaban`;
CREATE TABLE `jawaban` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mahasiswa_id` int(11) NOT NULL,
  `ujian_id` int(11) NOT NULL,
  `soal_id` int(11) NOT NULL,
  `jawaban_mhs` enum('A','B','C','D') DEFAULT NULL,
  `is_ragu` tinyint(1) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `mahasiswa_id` (`mahasiswa_id`),
  KEY `ujian_id` (`ujian_id`),
  KEY `soal_id` (`soal_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ==========================================
-- 10. TABEL NILAI / HASIL
-- ==========================================
DROP TABLE IF EXISTS `nilai_ujian`;
CREATE TABLE `nilai_ujian` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mahasiswa_id` int(11) NOT NULL,
  `ujian_id` int(11) NOT NULL,
  `jumlah_benar` int(11) DEFAULT 0,
  `jumlah_salah` int(11) DEFAULT 0,
  `nilai_akhir` double DEFAULT 0,
  `waktu_selesai` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `mahasiswa_id` (`mahasiswa_id`),
  KEY `ujian_id` (`ujian_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS=1;
COMMIT;
