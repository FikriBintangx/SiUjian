package id.ac.campus.antiexam;

import id.ac.campus.antiexam.ui.ux.PilihPeranFrame;

/**
 * =============================================================================
 * KELAS UTAMA (MAIN CLASS)
 * =============================================================================
 * Ini adalah pintu gerbang aplikasi. Saat aplikasi dijalankan (Run),
 * file inilah yang pertama kali dipanggil.
 */
public class AplikasiUjian {

    public static void main(String[] args) {

        // 1. Cek & Siapkan Database
        // Ini akan otomatis membuat tabel-tabel di database jika belum ada.
        // Konfigurasinya ada di file 'InisialisasiDatabase.java'
        id.ac.campus.antiexam.konfigurasi.InisialisasiDatabase.initialize();

        // 2. Jalankan Tampilan Awal (Frame Pilih Peran)
        // Kita gunakan invokeLater agar tampilan UI lebih responsif dan aman (Thread
        // Safety)
        java.awt.EventQueue.invokeLater(() -> {

            // Tampilkan jendela 'Pilih Peran' (Admin, Dosen, Mahasiswa, Pengawas)
            new PilihPeranFrame().setVisible(true);

        });
    }
}
