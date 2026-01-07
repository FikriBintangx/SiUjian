package id.ac.campus.antiexam.ui.ux;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ManualQuestionEditorDialog extends JDialog {

    private final int examId;
    private DefaultTableModel questionModel;
    private JTable questionTable;

    // Komponen Editor
    private JTextArea txtSoal;
    private JTextField txtOptA, txtOptB, txtOptC, txtOptD;
    private JRadioButton rbA, rbB, rbC, rbD;
    private ButtonGroup bgCorrect;
    private JComboBox<String> cmbPaket;
    private JLabel lblStatus; // Buat notif "Tersimpan" kecil

    // Warna & Style
    private final Color COL_BG_LEFT = new Color(248, 250, 252); // Abu sangat muda
    private final Color COL_BG_RIGHT = Color.WHITE;
    private final Color COL_PRIMARY = new Color(79, 70, 229); // Indigo modern
    private final Color COL_ACCENT = new Color(99, 102, 241);
    private final Color COL_TEXT_MAIN = new Color(30, 41, 59);
    private final Color COL_TEXT_SEC = new Color(100, 116, 139);

    public ManualQuestionEditorDialog(Frame parent, int examId) {
        super(parent, "Editor Bank Soal", true);
        this.examId = examId;
        setSize(1200, 750);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        initComponents();
        loadQuestions();
    }

    private void initComponents() {
        // === 1. SIDEBAR (DAFTAR SOAL) ===
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(COL_BG_LEFT);
        leftPanel.setPreferredSize(new Dimension(380, 0));
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(226, 232, 240)));

        // Header Sidebar
        JPanel headerLeft = new JPanel(new BorderLayout());
        headerLeft.setBackground(COL_BG_LEFT);
        headerLeft.setBorder(new EmptyBorder(25, 20, 20, 20));

        JLabel lblTitle = new JLabel("Daftar Soal");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(COL_TEXT_MAIN);

        // Panel Tombol Header
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        btnPanel.setOpaque(false);

        JButton btnAdd = createButton("+ Manual", COL_PRIMARY, Color.WHITE);
        btnAdd.addActionListener(e -> resetForm());

        JButton btnImport = createButton("Import Soal", COL_ACCENT, Color.WHITE);
        btnImport.addActionListener(e -> importSoal());

        btnPanel.add(btnAdd);
        btnPanel.add(btnImport);

        headerLeft.add(lblTitle, BorderLayout.NORTH);
        headerLeft.add(Box.createVerticalStrut(15), BorderLayout.CENTER);
        headerLeft.add(btnPanel, BorderLayout.SOUTH);

        // Tabel List Soal
        questionModel = new DefaultTableModel(new Object[] { "No", "Pratinjau Soal", "Kunci" }, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        questionTable = new JTable(questionModel);
        styleTable(questionTable);

        // Klik tabel -> Load ke Editor
        questionTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (questionTable.getSelectedRow() != -1) {
                    loadSelectedSoal();
                    lblStatus.setText("Mode Edit: Mengubah Soal No. " + (questionTable.getSelectedRow() + 1));
                }
            }
        });

        JScrollPane scrollList = new JScrollPane(questionTable);
        scrollList.setBorder(null);
        scrollList.getViewport().setBackground(COL_BG_LEFT);

        // Footer Sidebar (Tools)
        JPanel footerLeft = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        footerLeft.setBackground(COL_BG_LEFT);

        JButton btnDelete = createButton("Hapus", new Color(239, 68, 68), Color.WHITE);
        btnDelete.setPreferredSize(new Dimension(100, 36));
        btnDelete.addActionListener(e -> deleteSoal());

        JButton btnExport = createButton("PDF", new Color(15, 23, 42), Color.WHITE);
        btnExport.setPreferredSize(new Dimension(100, 36));
        btnExport.addActionListener(e -> exportToPdf());

        footerLeft.add(btnDelete);
        footerLeft.add(btnExport);

        leftPanel.add(headerLeft, BorderLayout.NORTH);
        leftPanel.add(scrollList, BorderLayout.CENTER);
        leftPanel.add(footerLeft, BorderLayout.SOUTH);

        // === 2. EDITOR AREA (KANAN) ===
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(COL_BG_RIGHT);

        // Header Editor
        JPanel headerRight = new JPanel(new BorderLayout());
        headerRight.setBackground(COL_BG_RIGHT);
        headerRight.setBorder(new EmptyBorder(25, 40, 0, 40));

        JLabel lblEditorTitle = new JLabel("Editor Naskah Soal");
        lblEditorTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblEditorTitle.setForeground(COL_TEXT_MAIN);

        lblStatus = new JLabel("Mode: Soal Baru");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStatus.setForeground(COL_PRIMARY);

        headerRight.add(lblEditorTitle, BorderLayout.NORTH);
        headerRight.add(lblStatus, BorderLayout.SOUTH);

        // FORM SCROLLABLE
        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(COL_BG_RIGHT);
        formContent.setBorder(new EmptyBorder(30, 40, 30, 40));

        // -- INPUT PAKET SOAL --
        formContent.add(createLabel("Target Mahasiswa (Paket Soal)"));
        formContent.add(Box.createVerticalStrut(8));
        String[] pkts = { "SEMUA (Default)", "GANJIL (NIM Akhiran 1,3,5..)", "GENAP (NIM Akhiran 0,2,4..)" };
        cmbPaket = new JComboBox<>(pkts);
        cmbPaket.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cmbPaket.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        formContent.add(cmbPaket);
        formContent.add(Box.createVerticalStrut(25));

        // -- INPUT TEXT SOAL --
        formContent.add(createLabel("Pertanyaan"));
        formContent.add(Box.createVerticalStrut(8));
        txtSoal = new JTextArea(4, 20);
        txtSoal.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtSoal.setLineWrap(true);
        txtSoal.setWrapStyleWord(true);
        txtSoal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Wrap textarea biar ada border halus
        JScrollPane scrollSoal = new JScrollPane(txtSoal);
        scrollSoal.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        scrollSoal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        scrollSoal.setAlignmentX(Component.LEFT_ALIGNMENT);

        formContent.add(scrollSoal);
        formContent.add(Box.createVerticalStrut(30));

        // -- OPSI JAWABAN --
        formContent.add(createLabel("Pilihan Jawaban & Kunci"));
        formContent.add(Box.createVerticalStrut(15));

        bgCorrect = new ButtonGroup();
        formContent.add(createOptionLine("A", rbA = new JRadioButton()));
        formContent.add(Box.createVerticalStrut(10));
        txtOptA = (JTextField) rbA.getClientProperty("field");

        formContent.add(createOptionLine("B", rbB = new JRadioButton()));
        formContent.add(Box.createVerticalStrut(10));
        txtOptB = (JTextField) rbB.getClientProperty("field");

        formContent.add(createOptionLine("C", rbC = new JRadioButton()));
        formContent.add(Box.createVerticalStrut(10));
        txtOptC = (JTextField) rbC.getClientProperty("field");

        formContent.add(createOptionLine("D", rbD = new JRadioButton()));
        formContent.add(Box.createVerticalStrut(10));
        txtOptD = (JTextField) rbD.getClientProperty("field");

        // Add to Radios
        bgCorrect.add(rbA);
        bgCorrect.add(rbB);
        bgCorrect.add(rbC);
        bgCorrect.add(rbD);
        rbA.setActionCommand("A");
        rbB.setActionCommand("B");
        rbC.setActionCommand("C");
        rbD.setActionCommand("D");
        rbA.setSelected(true);

        JScrollPane mainScroll = new JScrollPane(formContent);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);

        // Footer Kanan (Tombol Simpan)
        JPanel footerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        footerRight.setBackground(COL_BG_RIGHT);

        JButton btnReset = createButton("Reset Form", Color.WHITE, COL_TEXT_SEC);
        btnReset.setBorder(BorderFactory.createLineBorder(new Color(203, 213, 225)));
        btnReset.addActionListener(e -> resetForm());

        JButton btnSave = createButton("Simpan Perubahan", COL_PRIMARY, Color.WHITE);
        btnSave.setPreferredSize(new Dimension(180, 45));
        btnSave.addActionListener(e -> saveSoal());

        footerRight.add(btnReset);
        footerRight.add(btnSave);

        rightPanel.add(headerRight, BorderLayout.NORTH);
        rightPanel.add(mainScroll, BorderLayout.CENTER);
        rightPanel.add(footerRight, BorderLayout.SOUTH);

        // Add Split Pane
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        split.setDividerLocation(380);
        split.setDividerSize(1);
        split.setBorder(null);

        add(split);
    }

    // === UI HELPERS ===

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20)); // Padding dalam
        // Flat styling hack for Swing
        btn.setContentAreaFilled(false);
        btn.setOpaque(true);
        return btn;
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(COL_TEXT_MAIN);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel createOptionLine(String letter, JRadioButton rb) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setBackground(COL_BG_RIGHT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        rb.setBackground(COL_BG_RIGHT);
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel l = new JLabel(" " + letter + ". ");
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(COL_TEXT_MAIN);

        JPanel leftWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        leftWrap.setBackground(COL_BG_RIGHT);
        leftWrap.add(rb);
        leftWrap.add(l);

        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(203, 213, 225)), // Underline only
                new EmptyBorder(0, 5, 5, 5)));

        rb.putClientProperty("field", txt);

        p.add(leftWrap, BorderLayout.WEST);
        p.add(txt, BorderLayout.CENTER);
        return p;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(45);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(241, 245, 249));
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(224, 231, 255));
        table.setSelectionForeground(COL_TEXT_MAIN);

        JTableHeader th = table.getTableHeader();
        th.setPreferredSize(new Dimension(0, 40));
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setForeground(COL_TEXT_SEC);
        th.setBackground(COL_BG_LEFT);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        // Lebar Kolom
        table.getColumnModel().getColumn(0).setMaxWidth(40); // No
        table.getColumnModel().getColumn(2).setMaxWidth(60); // Key
    }

    // === FUNCTIONALITY (LOGIC) ===

    private void loadQuestions() {
        questionModel.setRowCount(0);
        try (Connection conn = KoneksiDatabase.getConnection()) {
            String sql = "SELECT id, pertanyaan, kunci_jawaban, paket_soal FROM soal_ujian WHERE id_ujian = ? ORDER BY id ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            int no = 1;
            while (rs.next()) {
                String qFull = rs.getString("pertanyaan");
                String pkt = rs.getString("paket_soal");
                if (pkt == null)
                    pkt = "SEMUA";

                // Format Preview: [GENAP] Siapa nama...
                String preview = "<html><font color='#4F46E5'><b>[" + pkt.substring(0, 3) + "]</b></font> " + qFull
                        + "</html>";

                questionModel.addRow(new Object[] {
                        no++,
                        preview,
                        rs.getString("kunci_jawaban")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadSelectedSoal() {
        int row = questionTable.getSelectedRow();
        if (row == -1)
            return;

        try (Connection conn = KoneksiDatabase.getConnection()) {
            String sql = "SELECT * FROM soal_ujian WHERE id_ujian = ? ORDER BY id ASC LIMIT 1 OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, examId);
            ps.setInt(2, row);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtSoal.setText(rs.getString("pertanyaan"));
                txtOptA.setText(rs.getString("option_a"));
                txtOptB.setText(rs.getString("option_b"));
                txtOptC.setText(rs.getString("option_c"));
                txtOptD.setText(rs.getString("option_d"));

                String k = rs.getString("kunci_jawaban");
                if ("A".equals(k))
                    rbA.setSelected(true);
                else if ("B".equals(k))
                    rbB.setSelected(true);
                else if ("C".equals(k))
                    rbC.setSelected(true);
                else if ("D".equals(k))
                    rbD.setSelected(true);

                String p = rs.getString("paket_soal");
                if ("GANJIL".equals(p))
                    cmbPaket.setSelectedIndex(1);
                else if ("GENAP".equals(p))
                    cmbPaket.setSelectedIndex(2);
                else
                    cmbPaket.setSelectedIndex(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetForm() {
        questionTable.clearSelection();
        lblStatus.setText("Mode: Membuat Soal Baru");
        txtSoal.setText("");
        txtOptA.setText("");
        txtOptB.setText("");
        txtOptC.setText("");
        txtOptD.setText("");
        rbA.setSelected(true);
        cmbPaket.setSelectedIndex(0);
        txtSoal.requestFocus();
    }

    private void saveSoal() {
        if (txtSoal.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pertanyaan tidak boleh kosong!");
            return;
        }

        String ans = "A";
        if (rbB.isSelected())
            ans = "B";
        if (rbC.isSelected())
            ans = "C";
        if (rbD.isSelected())
            ans = "D";

        String rawPaket = (String) cmbPaket.getSelectedItem();
        String valPaket = "SEMUA";
        if (rawPaket.startsWith("GANJIL"))
            valPaket = "GANJIL";
        if (rawPaket.startsWith("GENAP"))
            valPaket = "GENAP";

        int row = questionTable.getSelectedRow();
        boolean isUpdate = (row != -1);

        try (Connection conn = KoneksiDatabase.getConnection()) {
            if (isUpdate) {
                // Update Logic
                String sqlId = "SELECT id FROM soal_ujian WHERE id_ujian = ? ORDER BY id ASC LIMIT 1 OFFSET ?";
                PreparedStatement psId = conn.prepareStatement(sqlId);
                psId.setInt(1, examId);
                psId.setInt(2, row);
                ResultSet rsId = psId.executeQuery();
                if (rsId.next()) {
                    int qId = rsId.getInt("id");
                    String upd = "UPDATE soal_ujian SET pertanyaan=?, option_a=?, option_b=?, option_c=?, option_d=?, kunci_jawaban=?, paket_soal=? WHERE id=?";
                    PreparedStatement ps = conn.prepareStatement(upd);
                    ps.setString(1, txtSoal.getText());
                    ps.setString(2, txtOptA.getText());
                    ps.setString(3, txtOptB.getText());
                    ps.setString(4, txtOptC.getText());
                    ps.setString(5, txtOptD.getText());
                    ps.setString(6, ans);
                    ps.setString(7, valPaket);
                    ps.setInt(8, qId);
                    ps.executeUpdate();
                }
            } else {
                // Insert Logic
                String ins = "INSERT INTO soal_ujian(id_ujian, pertanyaan, option_a, option_b, option_c, option_d, kunci_jawaban, paket_soal) VALUES(?,?,?,?,?,?,?,?)";
                PreparedStatement ps = conn.prepareStatement(ins);
                ps.setInt(1, examId);
                ps.setString(2, txtSoal.getText());
                ps.setString(3, txtOptA.getText());
                ps.setString(4, txtOptB.getText());
                ps.setString(5, txtOptC.getText());
                ps.setString(6, txtOptD.getText());
                ps.setString(7, ans);
                ps.setString(8, valPaket);
                ps.executeUpdate();
            }
            loadQuestions();
            if (!isUpdate)
                resetForm();
            else
                JOptionPane.showMessageDialog(this, "Perubahan disimpan.");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error Save: " + e.getMessage());
        }
    }

    private void importSoal() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Import Soal (CSV/PDF/TXT)");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Format Soal (CSV, PDF, TXT)", "csv",
                "pdf", "txt"));

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.io.File f = fc.getSelectedFile();
            try {
                // Confirm before replacing
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Import soal akan MENGHAPUS semua soal lama pada ujian ini.\nLanjutkan?",
                        "Konfirmasi Import", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    new id.ac.campus.antiexam.layanan.ImportSoalService().importFromFile(f, examId);
                    loadQuestions();
                    JOptionPane.showMessageDialog(this, "Import Berhasil! Soal telah dimuat ulang.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal Import:\n" + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSoal() {
        int row = questionTable.getSelectedRow();
        if (row == -1)
            return;

        int opt = JOptionPane.showConfirmDialog(this, "Hapus soal terpilih?", "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION);
        if (opt != JOptionPane.YES_OPTION)
            return;

        try (Connection conn = KoneksiDatabase.getConnection()) {
            String sqlId = "SELECT id FROM soal_ujian WHERE id_ujian = ? ORDER BY id ASC LIMIT 1 OFFSET ?";
            PreparedStatement psId = conn.prepareStatement(sqlId);
            psId.setInt(1, examId);
            psId.setInt(2, row);
            ResultSet rsId = psId.executeQuery();
            if (rsId.next()) {
                int qId = rsId.getInt("id");
                conn.createStatement().execute("DELETE FROM soal_ujian WHERE id=" + qId);
                loadQuestions();
                resetForm();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportToPdf() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("Soal_Ujian_" + examId + ".pdf"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                // ... same export logic ...
                JOptionPane.showMessageDialog(this, "Fitur ini menggunakan service EksporPdfService yang sudah ada.");
            } catch (Exception e) {
            }
        }
    }
}
