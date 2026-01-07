package id.ac.campus.antiexam.ui.ux.dosen;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import id.ac.campus.antiexam.data.UjianData;
import id.ac.campus.antiexam.data.SesiUjianData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import id.ac.campus.antiexam.ui.icon.*;

import javax.swing.table.DefaultTableModel;

import id.ac.campus.antiexam.ui.ux.PilihPeranFrame;
import id.ac.campus.antiexam.ui.ux.ManualQuestionEditorDialog;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

/**
 * =============================================================================
 * HALAMAN DASHBOARD DOSEN
 * =============================================================================
 * Halaman utama untuk Dosen. Di sini Dosen bisa:
 * 1. Melihat semua ujian yang dia buat.
 * 2. Membuat jadwal ujian baru (Atur Ujian).
 * 3. Menambahkan soal (Editor Soal).
 * 4. Melihat hasil/nilai mahasiswa (Laporan).
 */
public class BerandaDosenFrame extends JFrame {

    private static final String APP_LOGO_PATH = "C:\\Users\\fikri\\Documents\\NetBeansProjects\\Appujian\\assets\\logo.png";

    // === DATA & REPOSITORY ===
    private final String lecturerUsername;
    private final UjianData examRepository = new UjianData();
    private final SesiUjianData sessionRepository = new SesiUjianData();

    private CardLayout contentCardLayout;
    private JPanel mainContentPanel;

    // Tombol Menu Sidebar
    private JButton btnMenuOverview;
    private JButton btnMenuSettings;
    private JButton btnMenuReport;
    private JButton btnMenuAccount;

    // Tabel Daftar Ujian
    private JTable examTable;
    private DefaultTableModel examModel;

    // Tabel Sesi Ujian (Monitoring & Laporan)
    private JTable sessionTable;
    private DefaultTableModel sessionModel;

    // Tabel Laporan Hasil
    private JTable reportTable;
    private DefaultTableModel reportModel;

    // Statistik Dashboard
    private JLabel lblStatTotal;
    private JLabel lblStatOngoing;
    private JLabel lblStatPelanggarans;

    // Form Atur Ujian
    private JComboBox<String> cmbSettingCourse;
    private JComboBox<String> cmbExamType;
    private JComboBox<String> cmbJenisSoal;
    private JSpinner spSettingDuration;
    private JComboBox<String> cmbSettingProctor;

    private int selectedExamId = -1;
    private String selectedSubjectCode;
    private String selectedType;
    private String selectedExamMode;
    private int selectedDuration;

    // Konfigurasi Warna (Neo-Brutalism Palette)
    private final Color COL_PRIMARY = new Color(88, 101, 242); // Biru Discord
    private final Color COL_SECONDARY = new Color(16, 185, 129); // Hijau Sukses
    private final Color COL_BG_MAIN = new Color(248, 250, 252); // Abu Putih
    private final Color COL_BG_SIDEBAR = new Color(88, 101, 242); // Sidebar Biru
    private final Color COL_TEXT_DARK = Color.BLACK;
    private final Color COL_TEXT_LIGHT = Color.DARK_GRAY;
    private final Color COL_DANGER = new Color(239, 68, 68); // Merah Bahaya
    private final Color COL_INFO = new Color(59, 130, 246); // Biru Info

    // Font
    private final Font FONT_H1 = new Font("Segoe UI", Font.BOLD, 24);
    private final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 18);
    private final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    public BerandaDosenFrame(String lecturerUsername) {
        this.lecturerUsername = lecturerUsername;
        setTitle("SiUjian - Dashboard Dosen");
        setSize(1440, 900);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
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

        // Default ke tampilan Overview
        switchView("VIEW_OVERVIEW", btnMenuOverview);

        // Timer Auto-Refresh Data Dashboard (Setiap 5 detik)
        Timer dashboardTimer = new Timer(5000, e -> loadDashboardStats());
        dashboardTimer.start();
    }

    private void initComponents() {
        // 1. Sidebar Kiri
        JPanel sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        // 2. Konten Kanan
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(COL_BG_MAIN);

        // Header Atas
        JPanel headerPanel = createTopHeader();
        rightPanel.add(headerPanel, BorderLayout.NORTH);

        // Konten Utama (Ganti-ganti halaman)
        contentCardLayout = new CardLayout();
        mainContentPanel = new JPanel(contentCardLayout);
        mainContentPanel.setBackground(COL_BG_MAIN);
        mainContentPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        mainContentPanel.add(createOverviewView(), "VIEW_OVERVIEW");
        mainContentPanel.add(createSettingsView(), "VIEW_SETTINGS");
        mainContentPanel.add(createReportView(), "VIEW_REPORT");

        rightPanel.add(mainContentPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(COL_BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(280, getHeight()));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 4, Color.BLACK)); // Garis Kanan Tebal

        // Logo
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 18));
        brandPanel.setOpaque(false);

        JLabel lblLogo = new JLabel("SiUjian");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Panel Dosen");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(224, 231, 255));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(lblLogo);
        textPanel.add(lblSubtitle);
        brandPanel.add(textPanel);

        // Menu Tombol
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(20, 16, 20, 16));

        btnMenuOverview = createSidebarButton("Dashboard Utama", new IconDashboard(20, Color.BLACK));
        btnMenuSettings = createSidebarButton("Atur Jadwal Ujian", new IconEdit(20, Color.BLACK));
        btnMenuReport = createSidebarButton("Laporan Nilai", new IconUjian(20, Color.BLACK));
        btnMenuAccount = createSidebarButton("Profil Saya", new IconInfo(20, Color.BLACK));

        // Aksi Tombol Sidebar
        btnMenuOverview.addActionListener(e -> switchView("VIEW_OVERVIEW", btnMenuOverview));
        btnMenuSettings.addActionListener(e -> {
            updateSettingsForm(); // Reset form dulu
            switchView("VIEW_SETTINGS", btnMenuSettings);
        });
        btnMenuReport.addActionListener(e -> {
            switchView("VIEW_REPORT", btnMenuReport);
            loadReportData();
        });
        btnMenuAccount.addActionListener(e -> openAccountDialog());

        menuPanel.add(btnMenuOverview);
        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(btnMenuSettings);
        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(btnMenuReport);
        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(btnMenuAccount);

        // Profil Bawah
        JPanel profilePanel = createProfilePanel();
        sidebar.add(brandPanel, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        sidebar.add(profilePanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setOpaque(false);
        profilePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblName = new JLabel(lecturerUsername);
        lblName.setFont(FONT_BOLD);
        lblName.setForeground(Color.WHITE);

        JLabel lblRole = new JLabel("Dosen / Pengajar");
        lblRole.setFont(FONT_SMALL);
        lblRole.setForeground(new Color(224, 231, 255));

        JPanel textInfo = new JPanel(new GridLayout(2, 1));
        textInfo.setOpaque(false);
        textInfo.add(lblName);
        textInfo.add(lblRole);

        profilePanel.add(textInfo, BorderLayout.CENTER);
        return profilePanel;
    }

    // Helper membuat tombol sidebar
    private JButton createSidebarButton(String text, Icon icon) {
        NeoButton btn = new NeoButton(text, Color.WHITE, Color.BLACK);
        btn.setIcon(icon);
        return btn;
    }

    private JPanel createTopHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(getWidth(), 70));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, Color.BLACK));
        header.setBorder(new EmptyBorder(0, 32, 0, 32));

        JLabel lblTitle = new JLabel("Ruang Kerja Dosen");
        lblTitle.setFont(FONT_H2);
        lblTitle.setForeground(COL_TEXT_DARK);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        JLabel lblBadge = new JLabel("SISTEM ONLINE");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBadge.setOpaque(true);
        lblBadge.setBackground(new Color(16, 185, 129));
        lblBadge.setForeground(Color.BLACK);
        lblBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                new EmptyBorder(5, 10, 5, 10)));

        NeoButton btnLogout = new NeoButton("Keluar (Logout)", COL_DANGER, Color.WHITE);
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin mau keluar?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new PilihPeranFrame().setVisible(true);
                dispose();
            }
        });

        rightPanel.add(lblBadge);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(btnLogout);

        header.add(lblTitle, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    // === TAMPILAN 1: DASHBOARD UTAMA ===
    private JPanel createOverviewView() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        // Panel Statistik Atas
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 120));

        lblStatTotal = new JLabel("0");
        lblStatOngoing = new JLabel("0");
        lblStatPelanggarans = new JLabel("0");

        statsPanel.add(createStatCard("Total Mahasiswa", lblStatTotal, COL_INFO));
        statsPanel.add(createStatCard("Sedang Ujian", lblStatOngoing, COL_SECONDARY));
        statsPanel.add(createStatCard("Terindikasi Curang", lblStatPelanggarans, COL_DANGER));

        // Daftar Ujian
        NeoPanel examListCard = createExamListCard();

        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(examListCard, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accent) {
        NeoPanel card = new NeoPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_BOLD);
        lblTitle.setForeground(COL_TEXT_LIGHT);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(accent);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private NeoPanel createExamListCard() {
        NeoPanel card = new NeoPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("Daftar Ujian yang Anda Buat");
        lblTitle.setFont(FONT_H2);
        lblTitle.setForeground(COL_TEXT_DARK);

        NeoButton btnRefresh = new NeoButton("Muat Ulang Data", Color.WHITE, Color.BLACK);
        btnRefresh.setPreferredSize(new Dimension(150, 40));
        btnRefresh.addActionListener(e -> loadExams());

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        examModel = new DefaultTableModel(
                new Object[] { "ID", "Kode Ujian", "Kelas", "Mata Kuliah", "Judul Ujian", "Tipe", "Durasi", "Status",
                        "Jenis Soal" },
                0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        examTable = createStyledTable(examModel);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(new JScrollPane(examTable), BorderLayout.CENTER);

        // Panel Tombol Aksi Bawah
        NeoButton btnManageQuestions = new NeoButton("Atur Soal (Input Soal)", COL_PRIMARY, Color.WHITE);
        btnManageQuestions.addActionListener(e -> {
            int row = examTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Silakan pilih salah satu ujian di tabel terlebih dahulu!",
                        "Pilih Ujian", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int examId = (Integer) examModel.getValueAt(row, 0);
            // Buka Editor Dialog
            new ManualQuestionEditorDialog(this, examId).setVisible(true);
        });

        NeoButton btnAssignProctor = new NeoButton("Kirim ke Pengawas", COL_SECONDARY, Color.WHITE);
        btnAssignProctor.addActionListener(e -> {
            int row = examTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih ujian dulu!");
                return;
            }
            int examId = (Integer) examModel.getValueAt(row, 0);
            assignToProctor(examId);
            JOptionPane.showMessageDialog(this, "Berhasil ditugaskan ke Pengawas!");
        });

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        footerPanel.add(btnAssignProctor);
        footerPanel.add(Box.createHorizontalStrut(10));
        footerPanel.add(btnManageQuestions);

        card.add(footerPanel, BorderLayout.SOUTH);

        return card;
    }

    // === TAMPILAN 2: ATUR UJIAN (SETTINGS) ===
    private JPanel createSettingsView() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        NeoPanel card = new NeoPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        card.setPreferredSize(new Dimension(650, 650));

        JLabel lblTitle = new JLabel("Buat / Atur Jadwal Ujian Baru");
        lblTitle.setFont(FONT_H1);
        lblTitle.setForeground(COL_PRIMARY);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblTitle, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(30, 0, 30, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        cmbSettingCourse = new JComboBox<>();
        populateSubjectCombo();
        styleComboBox(cmbSettingCourse);

        cmbExamType = new JComboBox<>(new String[] { "UTS", "UAS", "KUIS" });
        styleComboBox(cmbExamType);

        cmbJenisSoal = new JComboBox<>(new String[] { "PG" });
        styleComboBox(cmbJenisSoal);

        spSettingDuration = new JSpinner(new SpinnerNumberModel(90, 10, 300, 5));
        spSettingDuration.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        cmbSettingProctor = new JComboBox<>();
        styleComboBox(cmbSettingProctor);
        populateProctorCombo(cmbSettingProctor);

        addFormRow(form, gbc, "Mata Kuliah:", cmbSettingCourse);
        addFormRow(form, gbc, "Tipe Ujian:", cmbExamType);
        addFormRow(form, gbc, "Jenis Soal:", cmbJenisSoal);
        addFormRow(form, gbc, "Durasi Waktu (Menit):", spSettingDuration);
        addFormRow(form, gbc, "Pilih Pengawas Ruangan:", cmbSettingProctor);

        // Tombol Shortcut Editor Soal
        gbc.gridy++;
        form.add(new JLabel(" "), gbc); // Spacer

        gbc.gridy++;
        NeoButton btnManual = new NeoButton("\u270F\uFE0F Buka Editor Soal (Input Soal)", new Color(245, 158, 11),
                Color.BLACK);
        btnManual.setPreferredSize(new Dimension(0, 50));
        btnManual.addActionListener(e -> {
            if (selectedExamId == -1) {
                JOptionPane.showMessageDialog(this,
                        "Anda harus menyimpan ujian ini dulu sebelum input soal, atau pilih ujian yang sudah ada.");
                return;
            }
            new ManualQuestionEditorDialog(this, selectedExamId).setVisible(true);
        });
        form.add(btnManual, gbc);

        card.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        NeoButton btnBack = new NeoButton("Batal Kembali", Color.WHITE, Color.BLACK);
        NeoButton btnSave = new NeoButton("Simpan Perubahan", COL_PRIMARY, Color.WHITE);

        btnBack.addActionListener(e -> switchView("VIEW_OVERVIEW", btnMenuOverview));
        btnSave.addActionListener(e -> saveSettings());

        actions.add(btnBack);
        actions.add(btnSave);
        card.add(actions, BorderLayout.SOUTH);

        panel.add(card);
        return panel;
    }

    // === TAMPILAN 3: LAPORAN NILAI ===
    private JPanel createReportView() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        NeoPanel card = new NeoPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lblTitle = new JLabel("Laporan & Nilai Mahasiswa");
        lblTitle.setFont(FONT_H2);
        header.add(lblTitle, BorderLayout.WEST);

        NeoButton btnRefresh = new NeoButton("Refresh Data", COL_INFO, Color.WHITE);
        btnRefresh.addActionListener(e -> loadReportData());
        header.add(btnRefresh, BorderLayout.EAST);

        reportModel = new DefaultTableModel(
                new Object[] { "ID Sesi", "ID Ujian", "Nama Mahasiswa", "Status", "Waktu Mulai", "Pelanggaran",
                        "Nilai Akhir" },
                0);
        reportTable = createStyledTable(reportModel);

        card.add(header, BorderLayout.NORTH);
        card.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        NeoButton btnExport = new NeoButton("Export Excel", new Color(34, 197, 94), Color.WHITE);
        btnExport.addActionListener(e -> exportReportToExcel());

        NeoButton btnExportPdf = new NeoButton("Export PDF", new Color(220, 38, 38), Color.WHITE);
        btnExportPdf.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new java.io.File("Laporan_Nilai_Ujian.pdf"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    id.ac.campus.antiexam.layanan.EksporPdfService.exportTableToPdf(reportModel, "Laporan Nilai Ujian",
                            fc.getSelectedFile());
                    JOptionPane.showMessageDialog(this, "Ekspor PDF Berhasil!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Gagal Export PDF: " + ex.getMessage());
                }
            }
        });

        NeoButton btnDelete = new NeoButton("Hapus Log", COL_DANGER, Color.WHITE);
        btnDelete.addActionListener(e -> deleteSelectedSession());

        footer.add(btnExport);
        footer.add(btnExportPdf);
        footer.add(btnDelete);

        card.add(footer, BorderLayout.SOUTH);
        panel.add(card);
        return panel;
    }

    // === Helper UI & Logika ===

    private void styleComboBox(JComboBox box) {
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }

    // Fungsi Styling Tabel biar ganteng
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(FONT_BODY);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(COL_BG_SIDEBAR);
        table.getTableHeader().setForeground(Color.WHITE); // Header Putih
        table.setShowGrid(true);
        table.setGridColor(new Color(226, 232, 240));
        return table;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String label, JComponent comp) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(COL_TEXT_DARK);
        panel.add(lbl, gbc);

        gbc.gridy++;
        comp.setPreferredSize(new Dimension(0, 40));
        comp.setFont(FONT_BODY);
        panel.add(comp, gbc);

        gbc.gridy++;
    }

    // --- LOGIKA DATABASE ---

    private void populateSubjectCombo() {
        cmbSettingCourse.removeAllItems();
        try (Connection conn = KoneksiDatabase.getConnection()) {
            // Ambil matkul yang diajar dosen ini
            String sql = "SELECT kode_matkul, name FROM mata_kuliah WHERE username_dosen = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, lecturerUsername);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String item = rs.getString("kode_matkul") + " - " + rs.getString("name");
                cmbSettingCourse.addItem(item);
            }

            // Fallback kalau kosong, ambil semua
            if (cmbSettingCourse.getItemCount() == 0) {
                sql = "SELECT kode_matkul, name FROM mata_kuliah";
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();
                while (rs.next()) {
                    String item = rs.getString("kode_matkul") + " - " + rs.getString("name");
                    cmbSettingCourse.addItem(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateProctorCombo(JComboBox<String> cmb) {
        cmb.removeAllItems();
        try (Connection conn = KoneksiDatabase.getConnection()) {
            String sql = "SELECT username, nama_lengkap FROM pengawas";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cmb.addItem(rs.getString("username"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadExams() {
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                // Return: ID, code, targetKelas, title, matkul, type, duration, status,
                // questionType
                return examRepository.listExamsForLecturer(lecturerUsername);
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> list = get();
                    examModel.setRowCount(0);
                    for (Object[] row : list) {
                        // Susunan kolom tabel: ID, Kode, Kelas, Matkul, Judul, Tipe, Durasi, Status,
                        // Jenis Soal
                        examModel.addRow(new Object[] {
                                row[0], row[1], row[2], row[4], row[3], row[5], row[6], row[7], row[8]
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    private void loadDashboardStats() {
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                return sessionRepository.listAllSessionsForLecturer(lecturerUsername);
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> list = get();
                    int total = list.size();
                    int ongoing = 0;
                    int violations = 0;

                    for (Object[] row : list) {
                        String status = (String) row[3];
                        int vCount = (Integer) row[5]; // Violation count

                        if ("ONGOING".equals(status) || "LOCKED".equals(status)) {
                            ongoing++;
                        }
                        if (vCount > 0) {
                            violations++;
                        }
                    }

                    if (lblStatTotal != null)
                        lblStatTotal.setText(String.valueOf(total));
                    if (lblStatOngoing != null)
                        lblStatOngoing.setText(String.valueOf(ongoing));
                    if (lblStatPelanggarans != null)
                        lblStatPelanggarans.setText(String.valueOf(violations));

                } catch (Exception e) {
                }
            }
        }.execute();
    }

    private void loadReportData() {
        reportModel.setRowCount(0);
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                return sessionRepository.listAllSessionsForLecturer(lecturerUsername);
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> data = get();
                    for (Object[] row : data) {
                        reportModel.addRow(row);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void saveSettings() {
        if (cmbSettingCourse.getSelectedItem() == null)
            return;

        String courseItem = (String) cmbSettingCourse.getSelectedItem();
        String subjectCode = courseItem.split(" - ")[0]; // Ambil kode matkul

        // Coba cari nama kelas dari matkul ini biar otomatis
        String targetClass = "IF-21-A"; // Default fallback
        try (Connection conn = KoneksiDatabase.getConnection()) {
            String sql = "SELECT nama_kelas FROM mata_kuliah WHERE kode_matkul = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, subjectCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                targetClass = rs.getString("nama_kelas");
            }
        } catch (Exception e) {
        }

        String type = (String) cmbExamType.getSelectedItem();
        String qType = (String) cmbJenisSoal.getSelectedItem();
        int duration = (Integer) spSettingDuration.getValue();
        String proctor = (String) cmbSettingProctor.getSelectedItem();
        if (proctor == null)
            proctor = "";

        // Atur Jadwal (Default hari ini + 1 jam biar cepet)
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String jadwal = sdf.format(new java.util.Date());

        // Judul Ujian Otomatis
        String title = type + " - " + courseItem;

        try {
            if (selectedExamId != -1) {
                // UPDATE EXAM
                examRepository.updateExamWithFile(selectedExamId, type, qType, duration, subjectCode, "");
                // Update proctor juga
                try (Connection conn = KoneksiDatabase.getConnection()) {
                    String sql = "UPDATE ujian SET username_pengawas = ? WHERE id = ?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setString(1, proctor);
                    ps.setInt(2, selectedExamId);
                    ps.executeUpdate();
                }
                JOptionPane.showMessageDialog(this, "Pengaturan ujian berhasil disimpan (UPDATE)!");
            } else {
                // CREATE NEW EXAM
                String code = "EXM-" + System.currentTimeMillis() / 1000; // Unik Code
                examRepository.createUjian(code, targetClass, title, subjectCode, type, jadwal, 2025, duration,
                        lecturerUsername, proctor, "Lab Komputer 1");
                JOptionPane.showMessageDialog(this, "Jadwal Ujian Baru BERHASIL Dibuat!\nKode: " + code);
            }

            loadExams(); // Refresh tabel
            switchView("VIEW_OVERVIEW", btnMenuOverview);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal Simpan: " + e.getMessage());
        }
    }

    private void assignToProctor(int examId) {
        // Logika menugaskan ke pengawas (misal update kolom username_pengawas)
        // Di demo ini kita skip dulu atau update dummy
    }

    private void deleteSelectedSession() {
        int row = reportTable.getSelectedRow();
        if (row != -1) {
            try {
                int sessionId = (Integer) reportModel.getValueAt(row, 0);
                sessionRepository.deleteSession(sessionId);
                loadReportData();
                JOptionPane.showMessageDialog(this, "Log ujian berhasil dihapus.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih baris yang mau dihapus.");
        }
    }

    private void exportReportToExcel() {
        JOptionPane.showMessageDialog(this, "Fitur Export Excel akan segera hadir! Gunakan PDF dulu ya.");
    }

    private void openAccountDialog() {
        JOptionPane.showMessageDialog(this, "Anda Logged in sebagai: " + lecturerUsername + "\nRole: Dosen");
    }

    private void switchView(String viewName, JButton activeBtn) {
        contentCardLayout.show(mainContentPanel, viewName);
        resetSidebarButtons();
        ((NeoButton) activeBtn).setColors(Color.WHITE, COL_PRIMARY); // Highlight tombol aktif
    }

    private void resetSidebarButtons() {
        // Reset warna tombol sidebar
        ((NeoButton) btnMenuOverview).setColors(Color.WHITE, Color.BLACK);
        ((NeoButton) btnMenuSettings).setColors(Color.WHITE, Color.BLACK);
        ((NeoButton) btnMenuReport).setColors(Color.WHITE, Color.BLACK);
        ((NeoButton) btnMenuAccount).setColors(Color.WHITE, Color.BLACK);
    }

    private void updateSettingsForm() {
        populateSubjectCombo();
        if (selectedExamId != -1) {
            // Kalau lagi edit mode, panggil data lama (logic belum full di demo)
        }
    }

    // Custom Button Class (Neo Brutalism)
    private class NeoButton extends JButton {
        private Color bgColor;
        private Color fgColor;

        public NeoButton(String text, Color bg, Color fg) {
            super(text);
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

            // Shadow
            if (!getModel().isPressed()) {
                g2.setColor(Color.BLACK);
                g2.fillRect(4, 4, getWidth() - 4, getHeight() - 4);
            }

            // Body
            int offset = getModel().isPressed() ? 4 : 0;
            g2.setColor(bgColor);
            g2.fillRect(offset, offset, getWidth() - 4, getHeight() - 4);

            // Border
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(offset, offset, getWidth() - 6, getHeight() - 6);

            // Text
            g2.setColor(fgColor);
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2 + (offset / 2);
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() + (offset / 2);
            g2.drawString(getText(), x, y);

            g2.dispose();
        }
    }

    // Custom Panel (Neo Brutalism Container)
    private class NeoPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            // super.paintComponent(g); // skip, we draw manual
            Graphics2D g2 = (Graphics2D) g.create();

            // Shadow
            g2.setColor(Color.BLACK);
            g2.fillRect(6, 6, getWidth() - 6, getHeight() - 6);

            // Body White
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth() - 6, getHeight() - 6);

            // Border
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(0, 0, getWidth() - 6, getHeight() - 6);

            g2.dispose();
        }
    }
}
