package id.ac.campus.antiexam.ui.ux.pengawas;

import id.ac.campus.antiexam.data.UjianData;
import id.ac.campus.antiexam.data.SesiUjianData;
import id.ac.campus.antiexam.ui.ux.PilihPeranFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * =============================================================================
 * DASHBOARD PENGAWAS UJIAN
 * =============================================================================
 * Halaman utama untuk Pengawas Ruangan.
 * Fungsi:
 * 1. Memulai Ujian & Generate Token.
 * 2. Monitoring Realtime (Siapa yang sedang ujian, deteksi kecurangan).
 * 3. Kirim Pesan Broadcast ke peserta.
 * 4. Aksi Disipliner (Bekukan Sesi, Kick Peserta).
 */
public class BerandaPengawasFrame extends JFrame {

    private static final String APP_LOGO_PATH = "C:\\Users\\fikri\\Documents\\NetBeansProjects\\Appujian\\assets\\logo.png";

    private final String proctorUsername;
    private final UjianData examRepository = new UjianData();
    private final SesiUjianData sessionRepository = new SesiUjianData();

    private CardLayout contentCardLayout;
    private JPanel mainContentPanel;

    private JButton btnMenuOverview;
    private JButton btnMenuAccount;

    private JTable examTable;
    private DefaultTableModel examModel;

    private JTable sessionTable;
    private DefaultTableModel sessionModel;

    // Labels Statistik
    private JLabel lblStatTotal, lblStatOngoing, lblStatPelanggarans;
    private JLabel lblMonitoringTitle;
    private JTextField txtToken; // Field token (Generate otomatis)

    // Timer Realtime
    private Timer liveTimer;
    private int selectedExamId = -1;

    // Palet Warna (Neo-Brutalism)
    private final Color COL_PRIMARY = new Color(88, 101, 242);
    private final Color COL_SECONDARY = new Color(16, 185, 129); // Hijau
    private final Color COL_BG_MAIN = new Color(248, 250, 252);
    private final Color COL_BG_SIDEBAR = new Color(88, 101, 242);
    private final Color COL_DANGER = new Color(239, 68, 68); // Merah
    private final Color COL_INFO = new Color(59, 130, 246); // Biru

    private final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 18);

    public BerandaPengawasFrame(String proctorUsername) {
        this.proctorUsername = proctorUsername;
        setTitle("SiUjian - Dashboard Pengawas Ruangan");
        setSize(1440, 900);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(COL_BG_MAIN);

        try {
            setIconImage(new ImageIcon(APP_LOGO_PATH).getImage());
        } catch (Exception e) {
        }

        initComponents();
        loadExams();
    }

    private void initComponents() {
        // Sidebar
        JPanel sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        // Konten Utama
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(COL_BG_MAIN);

        JPanel headerPanel = createTopHeader();
        rightPanel.add(headerPanel, BorderLayout.NORTH);

        contentCardLayout = new CardLayout();
        mainContentPanel = new JPanel(contentCardLayout);
        mainContentPanel.setBackground(COL_BG_MAIN);
        mainContentPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        mainContentPanel.add(createOverviewView(), "VIEW_OVERVIEW");

        rightPanel.add(mainContentPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(COL_BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(280, getHeight()));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 4, Color.BLACK));

        // Logo Header
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 18));
        brandPanel.setOpaque(false);
        JLabel lblLogo = new JLabel("SiUjian");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Panel Pengawas");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(224, 231, 255));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(lblLogo);
        textPanel.add(lblSubtitle);
        brandPanel.add(textPanel);

        // Menu Tombol
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(20, 16, 20, 16));

        btnMenuOverview = new NeoButton("Monitoring Ujian", Color.WHITE, COL_PRIMARY); // Aktif
        btnMenuAccount = new NeoButton("Profil Saya", COL_BG_SIDEBAR, Color.WHITE); // Pasif

        btnMenuOverview.addActionListener(e -> {
            contentCardLayout.show(mainContentPanel, "VIEW_OVERVIEW");
            ((NeoButton) btnMenuOverview).setColors(Color.WHITE, COL_PRIMARY);
            ((NeoButton) btnMenuAccount).setColors(COL_BG_SIDEBAR, Color.WHITE);
        });

        btnMenuAccount.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Login sebagai Pengawas: " + proctorUsername + "\nStatus: Aktif");
        });

        menuPanel.add(btnMenuOverview);
        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(btnMenuAccount);

        sidebar.add(brandPanel, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        return sidebar;
    }

    private JPanel createTopHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(getWidth(), 70));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, Color.BLACK));
        header.setBorder(new EmptyBorder(0, 32, 0, 32));

        JLabel lblTitle = new JLabel("Monitoring & Pengawasan Ujian");
        lblTitle.setFont(FONT_H2);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        // Tombol Keluar
        NeoButton btnLogout = new NeoButton("Keluar (Logout)", COL_DANGER, Color.WHITE);
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin keluar?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new PilihPeranFrame().setVisible(true);
                dispose();
            }
        });
        rightPanel.add(btnLogout);

        header.add(lblTitle, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    // === TAMPILAN DASHBOARD UTAMA ===
    private JPanel createOverviewView() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        // Statistik Cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 120));

        lblStatTotal = new JLabel("0");
        lblStatOngoing = new JLabel("0");
        lblStatPelanggarans = new JLabel("0");

        statsPanel.add(createStatCard("Total Peserta", lblStatTotal, COL_INFO));
        statsPanel.add(createStatCard("Sedang Mengerjakan", lblStatOngoing, COL_SECONDARY));
        statsPanel.add(createStatCard("Terindikasi Pelanggaran", lblStatPelanggarans, COL_DANGER));

        // Layout Split (Kiri: Daftar Ujian, Kanan: Monitoring Detail)
        JPanel contentSplit = new JPanel(new BorderLayout(20, 0));
        contentSplit.setOpaque(false);

        // --- Panel Kiri: Daftar Ujian ---
        NeoPanel exCard = new NeoPanel();
        exCard.setLayout(new BorderLayout());
        exCard.setPreferredSize(new Dimension(480, 0)); // Lebarkan dikit
        exCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblListTitle = new JLabel("Daftar Jadwal Ujian");
        lblListTitle.setFont(FONT_H2);

        NeoButton btnRefresh = new NeoButton("Muat Ulang", Color.WHITE, Color.BLACK);
        btnRefresh.setPreferredSize(new Dimension(110, 40));
        btnRefresh.addActionListener(e -> loadExams());

        JPanel headEx = new JPanel(new BorderLayout());
        headEx.setOpaque(false);
        headEx.add(lblListTitle, BorderLayout.WEST);
        headEx.add(btnRefresh, BorderLayout.EAST);

        examModel = new DefaultTableModel(
                new Object[] { "ID", "Kode", "Kelas", "Judul Ujian", "Mapel", "Durasi", "Status" }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        examTable = createStyledTable(examModel);

        // Tombol Aksi Ujian
        NeoButton btnSelect = new NeoButton("Mulai / Aktifkan Ujian", COL_PRIMARY, Color.WHITE);
        btnSelect.addActionListener(e -> selectUjian());

        NeoButton btnPreview = new NeoButton("Lihat Soal", COL_INFO, Color.WHITE);
        btnPreview.addActionListener(e -> previewUjian());

        NeoButton btnBroadcast = new NeoButton("Kirim Pengumuman", new Color(236, 72, 153), Color.WHITE);
        btnBroadcast.addActionListener(e -> {
            int row = examTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih ujian yang sedang berjalan!");
                return;
            }
            int exId = (Integer) examModel.getValueAt(row, 0);
            String msg = JOptionPane.showInputDialog(this, "Masukkan pesan untuk peserta ujian:");
            if (msg != null && !msg.trim().isEmpty())
                sendBroadcast(exId, msg);
        });

        NeoButton btnStop = new NeoButton("Stop Ujian Permanen", COL_DANGER, Color.WHITE);
        btnStop.addActionListener(e -> stopUjianGlobal());

        JPanel examFooter = new JPanel(new GridLayout(2, 2, 10, 10));
        examFooter.setOpaque(false);
        examFooter.add(btnPreview);
        examFooter.add(btnSelect);
        examFooter.add(btnBroadcast);
        examFooter.add(btnStop);

        exCard.add(headEx, BorderLayout.NORTH);
        exCard.add(new JScrollPane(examTable), BorderLayout.CENTER);
        exCard.add(examFooter, BorderLayout.SOUTH);

        // --- Panel Kanan: Live Monitoring ---
        NeoPanel monCard = new NeoPanel();
        monCard.setLayout(new BorderLayout());
        monCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        lblMonitoringTitle = new JLabel("Live Monitoring (Pilih Ujian)");
        lblMonitoringTitle.setFont(FONT_H2);

        JPanel monHeader = new JPanel(new BorderLayout());
        monHeader.setOpaque(false);
        monHeader.add(lblMonitoringTitle, BorderLayout.WEST);

        // Tampilan Token
        JPanel tokenPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tokenPanel.setOpaque(false);
        JLabel lblToken = new JLabel("TOKEN AKSES: ");
        lblToken.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtToken = new JTextField(" - - - - - ");
        txtToken.setEditable(false);
        txtToken.setFont(new Font("Consolas", Font.BOLD, 22));
        txtToken.setHorizontalAlignment(JTextField.CENTER);
        txtToken.setBackground(new Color(255, 241, 118)); // Kuning Terang
        txtToken.setForeground(Color.BLACK);
        txtToken.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.BLACK));
        txtToken.setPreferredSize(new Dimension(140, 40));

        tokenPanel.add(lblToken);
        tokenPanel.add(txtToken);
        monHeader.add(tokenPanel, BorderLayout.EAST);

        monCard.add(monHeader, BorderLayout.NORTH);

        sessionModel = new DefaultTableModel(
                new Object[] { "ID Sesi", "Mahasiswa", "Status", "Waktu Mulai", "Pelanggaran", "Nilai" }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        sessionTable = createStyledTable(sessionModel);
        JScrollPane monScroll = new JScrollPane(sessionTable);
        monScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // Tombol Aksi Peserta
        JPanel footerMon = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerMon.setOpaque(false);

        NeoButton btnFreeze = new NeoButton("Bekukan Peserta", COL_INFO, Color.WHITE);
        NeoButton btnUnfreeze = new NeoButton("Buka Bekuan", COL_SECONDARY, Color.WHITE);
        NeoButton btnKick = new NeoButton("Keluarkan (KICK)", COL_DANGER, Color.WHITE);
        NeoButton btnExport = new NeoButton("Download Laporan", COL_PRIMARY, Color.WHITE);

        btnFreeze.addActionListener(e -> performAction("FREEZE"));
        btnUnfreeze.addActionListener(e -> performAction("UNFREEZE"));
        btnKick.addActionListener(e -> performAction("KICK"));
        btnExport.addActionListener(e -> exportToPdf());

        footerMon.add(btnFreeze);
        footerMon.add(btnUnfreeze);
        footerMon.add(btnKick);
        footerMon.add(btnExport);

        monCard.add(monScroll, BorderLayout.CENTER);
        monCard.add(footerMon, BorderLayout.SOUTH);

        contentSplit.add(exCard, BorderLayout.WEST);
        contentSplit.add(monCard, BorderLayout.CENTER);

        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(contentSplit, BorderLayout.CENTER);
        return panel;
    }

    private NeoPanel createStatCard(String title, JLabel val, Color color) {
        NeoPanel p = new NeoPanel();
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setForeground(Color.DARK_GRAY);

        val.setFont(new Font("Segoe UI", Font.BOLD, 36));
        val.setForeground(color);

        p.add(t, BorderLayout.NORTH);
        p.add(val, BorderLayout.CENTER);
        return p;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        return table;
    }

    // === LOGIKA PENDUKUNG ===

    private void loadExams() {
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() {
                try {
                    return examRepository.listExamsForProctor(proctorUsername);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new java.util.ArrayList<>();
                }
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> list = get();
                    examModel.setRowCount(0);
                    for (Object[] r : list) {
                        String duration = r[6] + " Menit";
                        String status = (String) r[7];
                        examModel.addRow(new Object[] { r[0], r[1], r[2], r[3], r[4], duration, status });
                    }
                } catch (Exception e) {
                }
            }
        }.execute();
    }

    private void selectUjian() {
        int r = examTable.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih jadwal ujian yang mau dimulai!");
            return;
        }
        selectedExamId = (Integer) examModel.getValueAt(r, 0);
        String title = (String) examModel.getValueAt(r, 3);

        String currentToken = "";

        // Cek dulu apakah token udah ada (lanjut ujian)
        try (Connection conn = id.ac.campus.antiexam.konfigurasi.KoneksiDatabase.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT token FROM ujian WHERE id=?");
            ps.setInt(1, selectedExamId);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getString("token") != null && !rs.getString("token").isEmpty()) {
                currentToken = rs.getString("token");
            }
        } catch (Exception e) {
        }

        try {
            if (currentToken.isEmpty()) {
                currentToken = generateToken();
            }

            txtToken.setText(currentToken);

            // Update Status Ujian jadi ONGOING dan Set Token
            examRepository.startUjian(selectedExamId, currentToken);

            lblMonitoringTitle.setText("Monitoring: " + title);

            // Mulai Auto Refresh (Live Poll) tiap 3 detik
            if (liveTimer == null) {
                liveTimer = new Timer(3000, e -> loadSessions());
                liveTimer.start();
            }
            loadSessions(); // Load awal
            JOptionPane.showMessageDialog(this,
                    "Ujian DIMULAI / DIAKTIFKAN.\nToken Akses: " + currentToken
                            + "\n\nBerikan token ini kepada peserta.");
            loadExams(); // Refresh status di tabel
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memulai ujian: " + e.getMessage());
        }
    }

    private void stopUjianGlobal() {
        int r = examTable.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Pilih ujian yang mau dihentikan.");
            return;
        }
        int exId = (Integer) examModel.getValueAt(r, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "PERINGATAN: Apakah Anda yakin ingin MENGHENTIKAN TOTAL ujian ini?\n\nSemua sesi peserta akan dipaksa selesai (Submitted).\nAksi ini tidak dapat dibatalkan.",
                "Konfirmasi Stop Ujian", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                examRepository.stopUjian(exId);
                sessionRepository.updateStatusByUjian(exId, "FINISHED"); // Paksa semua sesi selesai
                JOptionPane.showMessageDialog(this, "Ujian telah dihentikan secara permanen.");
                loadExams();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal menghentikan ujian: " + e.getMessage());
            }
        }
    }

    private String generateToken() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int idx = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(idx));
        }
        return sb.toString();
    }

    private void loadSessions() {
        if (selectedExamId == -1)
            return;
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() {
                try {
                    return sessionRepository.listSessionsSummary(selectedExamId);
                } catch (Exception e) {
                    return new java.util.ArrayList<>();
                }
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> res = get();
                    sessionModel.setRowCount(0);
                    int total = res.size(), ongoing = 0, vio = 0;
                    for (Object[] row : res) {
                        String st = (String) row[3];
                        int v = (Integer) row[5];
                        if ("ONGOING".equals(st))
                            ongoing++;
                        if (v > 0)
                            vio++;
                        sessionModel.addRow(row);
                    }
                    lblStatTotal.setText("" + total);
                    lblStatOngoing.setText("" + ongoing);
                    lblStatPelanggarans.setText("" + vio);
                } catch (Exception e) {
                }
            }
        }.execute();
    }

    private void performAction(String action) {
        int r = sessionTable.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Pilih mahasiswa dari tabel monitoring terlebih dahulu!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object val = sessionModel.getValueAt(r, 0); // Ambil Session ID
        int sid;
        try {
            sid = Integer.parseInt(val.toString());
        } catch (Exception e) {
            return;
        }

        try {
            if ("FREEZE".equals(action)) {
                sessionRepository.updateStatus(sid, "LOCKED");
                JOptionPane.showMessageDialog(this, "Sesi mahasiswa telah DIBEKUKAN.", "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if ("UNFREEZE".equals(action)) {
                sessionRepository.updateStatus(sid, "ONGOING"); // Reset ke Ongoing
                JOptionPane.showMessageDialog(this, "Sesi mahasiswa telah DIBUKA KEMBALI.", "Sukses",
                        JOptionPane.INFORMATION_MESSAGE);
            } else if ("KICK".equals(action)) {
                int conf = JOptionPane.showConfirmDialog(this, "Keluarkan peserta ini dari ujian (Paksa Selesai)?",
                        "Konfirmasi Kick", JOptionPane.YES_NO_OPTION);
                if (conf == JOptionPane.YES_OPTION) {
                    sessionRepository.updateStatus(sid, "FINISHED");
                    JOptionPane.showMessageDialog(this, "Peserta telah DIKELUARKAN dari ujian.", "Sukses",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
            loadSessions(); // Refresh data
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal melakukan aksi: " + e.getMessage());
        }
    }

    private void previewUjian() {
        int r = examTable.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Pilih ujian dulu baru bisa lihat soal.");
            return;
        }
        int exId = (Integer) examModel.getValueAt(r, 0);
        new QuestionPreviewDialog(this, exId).setVisible(true);
    }

    private void sendBroadcast(int examId, String message) {
        try (Connection conn = id.ac.campus.antiexam.konfigurasi.KoneksiDatabase.getConnection()) {
            String sql = "UPDATE ujian SET broadcast_message = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, message);
                ps.setInt(2, examId);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Pesan Terkirim ke Semua Peserta: \n\"" + message + "\"");
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal kirim. Pastikan ujian aktif.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error Broadcast: " + e.getMessage());
        }
    }

    // === DIALOG PREVIEW SOAL ===
    private class QuestionPreviewDialog extends JDialog {
        public QuestionPreviewDialog(Frame owner, int examId) {
            super(owner, "Preview Soal Ujian", true);
            setSize(900, 600);
            setLocationRelativeTo(owner);
            setLayout(new BorderLayout());

            JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
            header.setBackground(Color.WHITE);
            header.setBorder(new EmptyBorder(15, 20, 15, 20));

            JLabel lbl = new JLabel("Daftar Soal Ujian (Mode Baca)");
            lbl.setFont(FONT_H2);
            header.add(lbl);
            add(header, BorderLayout.NORTH);

            DefaultTableModel model = new DefaultTableModel(
                    new Object[] { "No", "Pertanyaan", "Opsi A", "Opsi B", "Opsi C", "Opsi D", "Kunci" }, 0) {
                public boolean isCellEditable(int row, int col) {
                    return false;
                }
            };

            JTable table = new JTable(model);
            table.setRowHeight(35);
            table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            table.setShowGrid(true);

            table.getColumnModel().getColumn(0).setMaxWidth(40);
            table.getColumnModel().getColumn(6).setMaxWidth(60);

            // Fetch Soal
            try (Connection conn = id.ac.campus.antiexam.konfigurasi.KoneksiDatabase.getConnection()) {
                String sql = "SELECT * FROM soal_ujian WHERE id_ujian = ? ORDER BY id ASC";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, examId);
                ResultSet rs = ps.executeQuery();
                int no = 1;
                while (rs.next()) {
                    String pkt = rs.getString("paket_soal");
                    if (pkt == null)
                        pkt = "SEMUA";
                    model.addRow(new Object[] {
                            no++,
                            "[" + pkt + "] " + rs.getString("pertanyaan"),
                            rs.getString("option_a"),
                            rs.getString("option_b"),
                            rs.getString("option_c"),
                            rs.getString("option_d"),
                            rs.getString("kunci_jawaban")
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            add(new JScrollPane(table), BorderLayout.CENTER);

            NeoButton btnClose = new NeoButton("Tutup Preview", COL_DANGER, Color.WHITE);
            btnClose.setPreferredSize(new Dimension(150, 45));
            btnClose.addActionListener(e -> dispose());

            JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            footer.setOpaque(false);
            footer.setBorder(new EmptyBorder(10, 20, 10, 20));
            footer.add(btnClose);
            add(footer, BorderLayout.SOUTH);
        }
    }

    // === KOMPONEN KUSTOM ===
    private class NeoButton extends JButton {
        private Color bgColor, fgColor;

        public NeoButton(String t, Color bg, Color fg) {
            super(t);
            this.bgColor = bg;
            this.fgColor = fg;
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        public void setColors(Color bg, Color fg) {
            this.bgColor = bg;
            this.fgColor = fg;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            if (!getModel().isPressed()) {
                g2.setColor(Color.BLACK);
                g2.fillRect(4, 4, getWidth() - 4, getHeight() - 4);
            }
            int off = getModel().isPressed() ? 4 : 0;
            g2.setColor(bgColor);
            g2.fillRect(off, off, getWidth() - 4, getHeight() - 4);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(off, off, getWidth() - 6, getHeight() - 6);

            g2.setColor(fgColor);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2 + off / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent() + off / 2);
            g2.dispose();
        }
    }

    private void exportToPdf() {
        if (sessionTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data untuk diekspor!");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("Laporan_Monitoring_" + System.currentTimeMillis() + ".pdf"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                id.ac.campus.antiexam.layanan.EksporPdfService.exportTableToPdf(sessionTable.getModel(),
                        "Laporan Monitoring Ujian", fc.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Laporan berhasil diekspor ke PDF!");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal ekspor PDF: " + e.getMessage());
            }
        }
    }

    private class NeoPanel extends JPanel {
        public NeoPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setColor(Color.BLACK);
            g2.fillRect(6, 6, getWidth() - 6, getHeight() - 6);
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth() - 6, getHeight() - 6);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(0, 0, getWidth() - 7, getHeight() - 7);
            g2.dispose();
        }
    }
}
