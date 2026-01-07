package id.ac.campus.antiexam.ui.ux;

import id.ac.campus.antiexam.data.AuthData;
import id.ac.campus.antiexam.ui.ux.admin.KelolaJadwalUjianFrame;
import id.ac.campus.antiexam.ui.ux.dosen.BerandaDosenFrame;
import id.ac.campus.antiexam.ui.ux.mahasiswa.BerandaMahasiswaFrame;
import id.ac.campus.antiexam.ui.ux.pengawas.BerandaPengawasFrame;
import id.ac.campus.antiexam.ui.icon.IconGraduation;
import id.ac.campus.antiexam.ui.icon.IconEye;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * =============================================================================
 * HALAMAN LOGIN UTAMA (PILIH PERAN)
 * =============================================================================
 * Ini adalah layar pertama yang muncul. User memilih peran (Admin/Dosen/dll),
 * lalu memasukkan username & password.
 * 
 * Logic login-nya memanggil 'AuthData.java'.
 */
public class PilihPeranFrame extends JFrame {

    private final AuthData authRepository = new AuthData();

    // =========================================================================
    // KONFIGURASI WARNA & FONT DI SINI
    // =========================================================================
    private final Color CLR_BLUE = new Color(88, 101, 242); // Warna Tombol Utama (Biru Discord)
    private final Color CLR_BLACK = Color.BLACK;
    private final Color CLR_WHITE = Color.WHITE;
    private final Color CLR_BG = new Color(248, 250, 252); // Warna Background Belakang (Abu Muda)

    private final Font FONT_BOLD_L = new Font("Segoe UI", Font.BOLD, 36);
    private final Font FONT_BOLD_S = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 14);

    // Komponen UI
    private JComboBox<String> cmbRole;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblLabel1, lblLabel2; // Label dinamis (bisa berubah jadi "NIM" atau "USERNAME")

    // Konstanta Nama Peran
    private final String ROLE_MAHASISWA = "Mahasiswa";
    private final String ROLE_DOSEN = "Dosen";
    private final String ROLE_PENGAWAS = "Pengawas";
    private final String ROLE_ADMIN = "Admin";

    private boolean isPasswordVisible = false; // Status mata password (kelihatan/nggak)

    public PilihPeranFrame() {
        setTitle("SiUjian - Masuk Aplikasi");
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
        setLocationRelativeTo(null); // Posisi tengah layar
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 1. SETUP BACKGROUND (Pola Titik-Titik)
        JPanel validContentPane = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(CLR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Gambar titik-titik (dot pattern)
                g2.setColor(new Color(226, 232, 240));
                for (int x = 0; x < getWidth(); x += 30) {
                    for (int y = 0; y < getHeight(); y += 30) {
                        g2.fillOval(x, y, 3, 3);
                    }
                }
            }
        };
        setContentPane(validContentPane);

        // 2. KARTU LOGIN UTAMA (Kotak Tengah)
        JPanel mainCard = new JPanel(null);
        mainCard.setPreferredSize(new Dimension(900, 550)); // Ukuran kotak login
        mainCard.setOpaque(false);

        // Konten dalam kartu (Kiri: Info, Kanan: Form)
        JPanel cardContent = new JPanel(new GridLayout(1, 2));
        cardContent.setBounds(0, 0, 900, 550);
        cardContent.setBorder(BorderFactory.createLineBorder(CLR_BLACK, 4)); // Border tebal ala Neo-Brutalis

        // === PANEL KIRI (BIRU) ===
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(CLR_BLUE);
        leftPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Judul Kiri
        JLabel lblTitleLeft = new JLabel(
                "<html><div style='text-align:center;'>SiUjian: Sistem<br>Ujian Online<br>Kampus</div></html>");
        lblTitleLeft.setFont(FONT_BOLD_L);
        lblTitleLeft.setForeground(CLR_WHITE);
        lblTitleLeft.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Deskripsi Kiri
        JLabel lblDescLeft = new JLabel(
                "<html><div style='text-align:center; width:280px;'>Silakan masuk sesuai peran Anda: Admin, Pengawas, Mahasiswa, atau Dosen.</div></html>");
        lblDescLeft.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblDescLeft.setForeground(new Color(224, 231, 255));
        lblDescLeft.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Ikon Toga
        JLabel lblIcon = new JLabel(new IconGraduation(120, CLR_BLACK));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(lblTitleLeft);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(lblDescLeft);
        leftPanel.add(Box.createVerticalStrut(30));
        leftPanel.add(lblIcon);
        leftPanel.add(Box.createVerticalGlue());

        // === PANEL KANAN (PUTIH - FORM LOGIN) ===
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(CLR_WHITE);
        rightPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);
        formContainer.setPreferredSize(new Dimension(350, 450));

        JLabel lblTitleRight = new JLabel("MASUK AKUN");
        lblTitleRight.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitleRight.setForeground(CLR_BLACK);
        lblTitleRight.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubRight = new JLabel("Masukkan kredensial Anda untuk melanjutkan.");
        lblSubRight.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubRight.setForeground(Color.GRAY);
        lblSubRight.setAlignmentX(Component.LEFT_ALIGNMENT);

        // [Pilihan Peran]
        JLabel lblRole = new JLabel("Login Sebagai:");
        lblRole.setFont(FONT_BOLD_S);
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);

        cmbRole = new JComboBox<>(new String[] { ROLE_DOSEN, ROLE_ADMIN, ROLE_MAHASISWA, ROLE_PENGAWAS });
        cmbRole.setFont(FONT_PLAIN);
        cmbRole.setBackground(CLR_WHITE);
        cmbRole.setBorder(BorderFactory.createLineBorder(CLR_BLACK, 2));
        cmbRole.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cmbRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbRole.addActionListener(e -> updateLabels()); // Update label saat peran diganti

        // [Input 1: Username/NIM]
        lblLabel1 = new JLabel("Username");
        lblLabel1.setFont(FONT_BOLD_S);
        lblLabel1.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = new JTextField();
        txtUsername.setFont(FONT_PLAIN);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BLACK, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        // [Input 2: Password]
        JPanel pwdLabelPanel = new JPanel(new BorderLayout());
        pwdLabelPanel.setOpaque(false);
        pwdLabelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        pwdLabelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblLabel2 = new JLabel("Password");
        lblLabel2.setFont(FONT_BOLD_S);

        pwdLabelPanel.add(lblLabel2, BorderLayout.WEST);

        JPanel pwdContainer = new JPanel(new BorderLayout());
        pwdContainer.setBorder(BorderFactory.createLineBorder(CLR_BLACK, 2));
        pwdContainer.setBackground(CLR_WHITE);
        pwdContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pwdContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        txtPassword.setBorder(new EmptyBorder(8, 10, 8, 0));
        txtPassword.setFont(FONT_PLAIN);

        // Tombol Mata (Lihat Password)
        JButton btnEye = new JButton(new IconEye(20, CLR_BLACK));
        btnEye.setBorderPainted(false);
        btnEye.setContentAreaFilled(false);
        btnEye.setFocusPainted(false);
        btnEye.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEye.addActionListener(e -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible)
                txtPassword.setEchoChar('\0'); // Lihat
            else
                txtPassword.setEchoChar('\u2022'); // Sembunyi
        });

        pwdContainer.add(txtPassword, BorderLayout.CENTER);
        pwdContainer.add(btnEye, BorderLayout.EAST);

        // Checkbox Ingat Saya (Hiasan)
        JCheckBox chkRemember = new JCheckBox("Ingat akun saya");
        chkRemember.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkRemember.setOpaque(false);
        chkRemember.setIcon(createCheckBoxIcon(false));
        chkRemember.setSelectedIcon(createCheckBoxIcon(true));
        chkRemember.setAlignmentX(Component.LEFT_ALIGNMENT);

        // [TOMBOL LOGIN UTAMA]
        btnLogin = new NeoButton("MASUK SEKARANG", CLR_BLUE, CLR_WHITE);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> performLogin()); // Panggil fungsi login

        // Susun Form (Layouting)
        formContainer.add(lblTitleRight);
        formContainer.add(Box.createVerticalStrut(5));
        formContainer.add(lblSubRight);
        formContainer.add(Box.createVerticalStrut(25));

        formContainer.add(lblRole);
        formContainer.add(Box.createVerticalStrut(5));
        formContainer.add(cmbRole);
        formContainer.add(Box.createVerticalStrut(15));

        formContainer.add(lblLabel1);
        formContainer.add(Box.createVerticalStrut(5));
        formContainer.add(txtUsername);
        formContainer.add(Box.createVerticalStrut(15));

        formContainer.add(pwdLabelPanel);
        formContainer.add(Box.createVerticalStrut(5));
        formContainer.add(pwdContainer);
        formContainer.add(Box.createVerticalStrut(10));

        formContainer.add(chkRemember);
        formContainer.add(Box.createVerticalStrut(20));

        formContainer.add(btnLogin);
        formContainer.add(Box.createVerticalStrut(20));

        rightPanel.add(formContainer);

        cardContent.add(leftPanel);
        cardContent.add(rightPanel);

        // Efek Bayangan Hitam (Shadow Panel)
        JPanel shadowPanel = new JPanel();
        shadowPanel.setBackground(CLR_BLACK);
        shadowPanel.setBounds(10, 10, 900, 550); // offset 10px biar geser dikit

        mainCard.add(cardContent); // Layer Atas
        mainCard.add(shadowPanel); // Layer Bawah
        mainCard.setComponentZOrder(cardContent, 0);
        mainCard.setComponentZOrder(shadowPanel, 1);

        add(mainCard);

        // Setel label awal
        updateLabels();
    }

    // --- LOGIKA MENGUBAH LABEL SESUAI ROLE ---
    private void updateLabels() {
        String role = (String) cmbRole.getSelectedItem();
        txtUsername.setText("");
        txtPassword.setText("");

        if (ROLE_ADMIN.equals(role)) {
            lblLabel1.setText("USERNAME");
            lblLabel2.setText("PASSWORD");
        } else if (ROLE_MAHASISWA.equals(role)) {
            lblLabel1.setText("NIM");
            lblLabel2.setText("PASSWORD (Optional)");
        } else if (ROLE_DOSEN.equals(role)) {
            lblLabel1.setText("NIDN / Username");
            lblLabel2.setText("PASSWORD");
        } else if (ROLE_PENGAWAS.equals(role)) {
            lblLabel1.setText("USERNAME");
            lblLabel2.setText("PASSWORD");
        }

        // Reset password field
        txtPassword.setEchoChar('\u2022');
        isPasswordVisible = false;
    }

    // --- LOGIKA UTAMA SAAT TOMBOL DIKLIK ---
    private void performLogin() {
        String role = (String) cmbRole.getSelectedItem();
        String u = txtUsername.getText().trim();
        String p = new String(txtPassword.getPassword()).trim();

        // Validasi input kosong
        if (u.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon isi Username/NIM!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean success = false;

            // 1. Cek Login Mahasiswa
            if (ROLE_MAHASISWA.equals(role)) {
                String[] data = authRepository.getStudentDetails(p, u);
                if (data != null) {
                    success = true;
                    int studentId = Integer.parseInt(data[0]);
                    new BerandaMahasiswaFrame(studentId).setVisible(true); // Buka Dashboard Mhs
                }

                // 2. Cek Login Dosen
            } else if (ROLE_DOSEN.equals(role)) {
                // Perbaikan: Sekarang cek password yang benar, bukan nama
                if (authRepository.loginLecturer(u, p)) {
                    success = true;
                    new BerandaDosenFrame(u).setVisible(true); // Buka Dashboard Dosen
                }

                // 3. Cek Login Pengawas
            } else if (ROLE_PENGAWAS.equals(role)) {
                if (authRepository.loginProctor(u, p)) {
                    success = true;
                    new BerandaPengawasFrame(u).setVisible(true); // Buka Dashboard Pengawas
                }

                // 4. Cek Login Admin
            } else if (ROLE_ADMIN.equals(role)) {
                if (authRepository.loginAdmin(u, p)) {
                    success = true;
                    new KelolaJadwalUjianFrame(u).setVisible(true); // Buka Dashboard Admin
                }
            }

            // Hasil Login
            if (success) {
                dispose(); // Tutup jendela login
            } else {
                JOptionPane.showMessageDialog(this,
                        "Login Gagal!\nUsername atau Password salah.",
                        "Akses Ditolak", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Terjadi Error Database:\n" + e.getMessage());
        }
    }

    // --- KOMPONEN BUTTON CUSTOM (NEO BRUTALISM) ---
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

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            // Efek Shadow Hitam (Saat tidak dipencet)
            if (!getModel().isPressed()) {
                g2.setColor(CLR_BLACK);
                g2.fillRect(4, 4, getWidth() - 4, getHeight() - 4);
            }

            // Body Tombol
            int offset = getModel().isPressed() ? 4 : 0; // Efek pencet
            g2.setColor(bgColor);
            g2.fillRect(offset, offset, getWidth() - 4, getHeight() - 4);

            // Garis Pinggir (Border)
            g2.setColor(CLR_BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(offset, offset, getWidth() - 6, getHeight() - 6);

            // Teks Tombol (Tengah)
            g2.setColor(fgColor);
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2 + (offset / 2);
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() + (offset / 2);
            g2.drawString(getText(), x, y);

            g2.dispose();
        }
    }

    // --- ICON CHECKBOX CUSTOM ---
    private Icon createCheckBoxIcon(boolean selected) {
        return new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(2));
                g2.setColor(CLR_BLACK);
                g2.drawRect(x, y, 16, 16); // Kotak Luar
                if (selected) {
                    g2.fillRect(x + 4, y + 4, 9, 9); // Kotak Dalam (Hitam)
                }
            }

            public int getIconWidth() {
                return 18;
            }

            public int getIconHeight() {
                return 18;
            }
        };
    }

    // Main Method untuk testing tampilan
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        new PilihPeranFrame().setVisible(true);
    }
}
