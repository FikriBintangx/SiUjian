# Panduan Import Soal - SiUjian

## Fitur Baru: Import Soal dari File

Fitur import soal telah berhasil diimplementasikan! Dosen sekarang dapat mengimpor soal ujian dari file CSV, PDF, atau TXT.

---

## Cara Menggunakan

### 1. Akses Editor Soal

- Login sebagai **Dosen**
- Pilih ujian dari tabel di Dashboard
- Klik tombol **"Atur Soal (Input Soal)"**

### 2. Import Soal

- Di dialog Editor Soal, klik tombol **"Import Soal"** (di sebelah tombol "+ Manual")
- Pilih file yang ingin diimpor (format: CSV, PDF, atau TXT)
- Konfirmasi bahwa Anda ingin mengganti semua soal lama
- Tunggu proses import selesai

---

## Format File yang Didukung

### A. Format CSV (Recommended)

File CSV harus memiliki kolom dengan urutan berikut:

```
Pertanyaan, Opsi A, Opsi B, Opsi C, Opsi D, Kunci Jawaban, Paket Soal
```

**Contoh CSV:**

```csv
Pertanyaan,Opsi A,Opsi B,Opsi C,Opsi D,Kunci Jawaban,Paket Soal
"Apa ibukota Indonesia?","Jakarta","Bandung","Surabaya","Medan","A","SEMUA"
"Berapa hasil 2+2?","3","4","5","6","B","GANJIL"
"Siapa presiden pertama RI?","Soekarno","Soeharto","Habibie","Megawati","A","GENAP"
```

**Catatan Penting:**

- Baris pertama adalah header (akan diabaikan)
- Gunakan tanda kutip `"` untuk teks yang mengandung koma
- **Kunci Jawaban**: Gunakan huruf A, B, C, atau D
- **Paket Soal**: Gunakan "GANJIL", "GENAP", atau "SEMUA"
  - Jika kosong atau tidak valid, akan otomatis diset ke "SEMUA"

### B. Format PDF

- Soal harus diformat dengan struktur:

  ```
  1. Pertanyaan soal nomor 1?
  A. Opsi A
  B. Opsi B
  C. Opsi C
  D. Opsi D

  2. Pertanyaan soal nomor 2?
  ...
  ```

- Kunci jawaban harus ada di bagian akhir dokumen dengan format:
  ```
  KUNCI JAWABAN:
  1. A
  2. B
  3. C
  ```

### C. Format TXT

- Sama seperti format PDF
- File teks biasa dengan struktur yang sama

---

## Fitur Paket Soal

Sistem mendukung **3 jenis paket soal**:

1. **SEMUA**: Soal akan diberikan ke semua peserta
2. **GANJIL**: Soal hanya untuk peserta dengan nomor absen ganjil
3. **GENAP**: Soal hanya untuk peserta dengan nomor absen genap

Ini berguna untuk mencegah kecurangan dengan memberikan variasi soal kepada peserta yang duduk berdekatan.

---

## Peringatan Penting

⚠️ **IMPORT AKAN MENGHAPUS SEMUA SOAL LAMA!**

Ketika Anda mengimpor soal baru, sistem akan:

1. Menghapus semua soal yang sudah ada untuk ujian tersebut
2. Mengimpor soal baru dari file
3. Menyimpan ke database

Pastikan Anda sudah backup soal lama jika diperlukan!

---

## Troubleshooting

### Error: "Format file belum didukung"

- Pastikan file Anda berekstensi `.csv`, `.pdf`, atau `.txt`
- Periksa nama file tidak mengandung karakter khusus

### Error: "Gagal parsing CSV"

- Periksa format CSV Anda sesuai dengan contoh
- Pastikan ada 7 kolom (Pertanyaan, Opsi A-D, Kunci, Paket)
- Gunakan tanda kutip untuk teks yang mengandung koma

### Error: "Kunci jawaban tidak ditemukan" (PDF/TXT)

- Pastikan ada bagian "KUNCI JAWABAN:" di akhir dokumen
- Format kunci: `1. A`, `2. B`, dll.

### Soal tidak muncul setelah import

- Refresh tabel dengan klik soal lain lalu kembali
- Periksa console/log untuk error message

---

## Update Terbaru (Checkpoint 3)

### Yang Sudah Diimplementasikan:

✅ Tombol "Import Soal" di UI Editor Soal  
✅ Support import dari CSV dengan kolom paket_soal  
✅ Support import dari PDF dan TXT  
✅ Konfirmasi dialog sebelum menghapus soal lama  
✅ Refresh otomatis tabel setelah import berhasil  
✅ Error handling dan pesan yang jelas  
✅ Penyimpanan paket_soal ke database

### Yang Perlu Diverifikasi:

🔲 Mekanisme Lock/Unlock mahasiswa saat tab switch  
🔲 Dashboard Pengawas menampilkan status LOCKED dengan benar  
🔲 Tombol "Buka Bekuan" dan "Bekukan Peserta" berfungsi  
🔲 Fitur "Keluarkan Peserta" berfungsi  
🔲 Laporan PDF mencatat pelanggaran dengan akurat

---

## Langkah Selanjutnya

1. **Install aplikasi terbaru** dari `SiUjian_Setup.msi`
2. **Test fitur import** dengan file CSV contoh
3. **Verifikasi anti-cheat** (lock/unlock mechanism)
4. **Test laporan PDF** untuk memastikan pelanggaran tercatat

---

**Dibuat oleh:** Antigravity AI Assistant  
**Tanggal:** 2026-01-07  
**Versi Aplikasi:** SiUjian v2.0 (dengan Import Soal)
