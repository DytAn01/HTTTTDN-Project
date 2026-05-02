package gui.form;

import bus.busnhanvien;
import bus.bustaikhoan;
import net.miginfocom.swing.MigLayout;
import dto.dtonhanvien;
import dto.dtotaikhoan;
import gui.comp.RoundedBorder;
import gui.swing.dashboard.Form;
import gui.swing.dashboard.SystemForm;
import java.util.ArrayList;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;

@SystemForm(name = "Thông tin cá nhân", description = "Hiển thị thông tin cá nhân của nhân viên", tags = {"personal"})
public class formthongtincanhan extends Form {

    private int maNhanVien;
    private busnhanvien busNV;
    private bustaikhoan busTK;
    private dtonhanvien nhanVien;
    private dtotaikhoan taikhoan;

    // Fields
    private JTextField tfMaNV, tfTenNV, tfNgaySinh, tfSDT, tfDiaChi, tfEmail, tfTaiKhoan;
    private JPasswordField pfMatKhau;
    private JComboBox<String> cbGioiTinh;
    private JLabel lblChucVu;

    // Avatar
    private JLabel lblAvatar;
    private String currentImagePath = "";

    // Colors
    private static final Color PRIMARY   = new Color(99, 102, 241);   // indigo
    private static final Color BG_PAGE   = new Color(245, 247, 250);
    private static final Color BG_CARD   = Color.WHITE;
    private static final Color TEXT_HEAD = new Color(30, 41, 59);
    private static final Color TEXT_SUB  = new Color(100, 116, 139);
    private static final Color BORDER_C  = new Color(226, 232, 240);
    private static final Color HOVER_BTN = new Color(79, 82, 221);

    public formthongtincanhan(int maNhanVien) throws SQLException {
        this.maNhanVien = maNhanVien;
        init();
        formInit();
    }

    private void init() {
        setBackground(BG_PAGE);
        setLayout(new MigLayout("fill, insets 24 28 24 28", "[fill]", "[fill]"));
        add(buildCard());
    }

    @Override
    public void formInit() {
        try {
            busNV = new busnhanvien();
            busNV.list();
            for (dtonhanvien nv : busNV.getList()) {
                if (nv.getManhanvien() == maNhanVien) { nhanVien = nv; break; }
            }
            busTK = new bustaikhoan();
            for (dtotaikhoan tk : busTK.getlist()) {
                if (tk.getManhanvien() == maNhanVien) { taikhoan = tk; break; }
            }
            if (nhanVien != null) updateFields();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ── Card container ────────────────────────────────────────────────────────
    private JPanel buildCard() {
        JPanel card = new JPanel(new MigLayout(
            "fill, insets 32 32 32 32, gapy 0",
            "[220!][32!][fill]",
            "[grow 0][32!][fill]"
        ));
        card.setBackground(BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // ── Header row ────────────────────────────────────────────────────────
        JPanel header = buildHeader();
        card.add(header, "span 3, growx, wrap");

        // Divider
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_C);
        card.add(sep, "span 3, growx, gaptop 0, gapbottom 24, wrap");

        // ── Left: avatar panel ────────────────────────────────────────────────
        card.add(buildAvatarPanel(), "growy 0, top");

        // Spacer column
        card.add(new JLabel(), "");

        // ── Right: fields panel ───────────────────────────────────────────────
        card.add(buildFieldsPanel(), "grow");

        return card;
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new MigLayout("insets 0, fillx", "[fill][]"));
        p.setOpaque(false);

        JLabel title = new JLabel("Thông tin cá nhân");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_HEAD);

        JLabel sub = new JLabel("Xem và cập nhật thông tin của bạn");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_SUB);

        JPanel texts = new JPanel(new MigLayout("insets 0, flowy", "[fill]", "[]2[]"));
        texts.setOpaque(false);
        texts.add(title, "growx");
        texts.add(sub,  "growx");

        p.add(texts, "grow");
        return p;
    }

    // ── Avatar panel ──────────────────────────────────────────────────────────
    private JPanel buildAvatarPanel() {
        JPanel p = new JPanel(new MigLayout("insets 0, flowy, fillx, alignx center", "[center]", "[]12[]12[]"));
        p.setOpaque(false);

        // Circle avatar label
        lblAvatar = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                // Shadow
                g2.setColor(new Color(0, 0, 0, 18));
                g2.fillOval(4, 6, w - 4, h - 4);
                // Clip circle
                g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, w, h));
                if (getIcon() != null) {
                    Image img = ((ImageIcon) getIcon()).getImage();
                    g2.drawImage(img, 0, 0, w, h, null);
                } else {
                    g2.setColor(new Color(226, 232, 240));
                    g2.fillOval(0, 0, w, h);
                    g2.setColor(TEXT_SUB);
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 48));
                    FontMetrics fm = g2.getFontMetrics();
                    String init = "?";
                    g2.drawString(init, (w - fm.stringWidth(init)) / 2, (h + fm.getAscent()) / 2 - 4);
                }
                // Border ring
                g2.setClip(null);
                g2.setStroke(new BasicStroke(3));
                g2.setColor(PRIMARY);
                g2.drawOval(1, 1, w - 3, h - 3);
                g2.dispose();
            }
        };
        lblAvatar.setPreferredSize(new Dimension(160, 160));
        lblAvatar.setMinimumSize(new Dimension(160, 160));
        p.add(lblAvatar, "width 160!, height 160!");

        // Name label under avatar
        JLabel nameUnder = new JLabel(" ");
        nameUnder.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameUnder.setForeground(TEXT_HEAD);
        nameUnder.setName("nameUnder");
        p.add(nameUnder, "growx");

        // Change avatar button
        JButton btnChange = buildSecondaryButton("Đổi ảnh đại diện");
        btnChange.addActionListener(e -> chooseAvatar(nameUnder));
        p.add(btnChange, "growx");

        return p;
    }

    private void chooseAvatar(JLabel nameUnder) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Chọn ảnh đại diện");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Ảnh (jpg, png, jpeg, gif)", "jpg", "png", "jpeg", "gif"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File chosen = fc.getSelectedFile();
            try {
                // Copy to project image folder
                String destDir = System.getProperty("user.dir") + "/src/source/image/nhanvien/";
                String ext     = chosen.getName().substring(chosen.getName().lastIndexOf('.'));
                String newName = "nv_" + maNhanVien + "_" + System.currentTimeMillis() + ext;
                Path dest = Paths.get(destDir + newName);
                new File(destDir).mkdirs();
                Files.copy(chosen.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
                currentImagePath = newName;

                // Update avatar display
                loadAvatar(destDir + newName);

                // Persist to DB (update dtonhanvien.img and save)
                if (nhanVien != null) {
                    nhanVien.setImg(newName);
                    busNV.updateNhanVien(nhanVien); // make sure busnhanvien has an update method
                }
                JOptionPane.showMessageDialog(this, "Cập nhật ảnh đại diện thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Không thể cập nhật ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadAvatar(String fullPath) {
        try {
            BufferedImage bi = ImageIO.read(new File(fullPath));
            if (bi != null) {
                // Crop to square from center
                int side = Math.min(bi.getWidth(), bi.getHeight());
                int x    = (bi.getWidth()  - side) / 2;
                int y    = (bi.getHeight() - side) / 2;
                BufferedImage cropped = bi.getSubimage(x, y, side, side);
                Image scaled = cropped.getScaledInstance(160, 160, Image.SCALE_SMOOTH);
                lblAvatar.setIcon(new ImageIcon(scaled));
            }
        } catch (Exception ex) { /* show placeholder */ }
        lblAvatar.repaint();
    }

    // ── Fields panel ──────────────────────────────────────────────────────────
    private JPanel buildFieldsPanel() {
        JPanel p = new JPanel(new MigLayout(
            "insets 0, fillx, wrap 2",
            "[120!][fill, grow]",
            "[]14[]14[]14[]14[]14[]14[]14[]14[]"
        ));
        p.setOpaque(false);

        tfMaNV     = makeField(); tfMaNV.setEnabled(false);
        tfTenNV    = makeField();
        cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        styleCombo(cbGioiTinh);
        tfNgaySinh = makeField();
        tfSDT      = makeField();
        tfDiaChi   = makeField();
        tfEmail    = makeField();
        lblChucVu  = new JLabel();
        lblChucVu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblChucVu.setForeground(TEXT_HEAD);
        // Badge style
        lblChucVu.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(199, 210, 254), 1, true),
            BorderFactory.createEmptyBorder(3, 10, 3, 10)
        ));
        lblChucVu.setBackground(new Color(238, 242, 255));
        lblChucVu.setOpaque(true);

        tfTaiKhoan = makeField();
        pfMatKhau  = new JPasswordField(); styleField(pfMatKhau);

        addRow(p, "Mã nhân viên",   tfMaNV);
        addRow(p, "Họ và tên",      tfTenNV);
        addRow(p, "Giới tính",      cbGioiTinh);
        addRow(p, "Ngày sinh",      tfNgaySinh);
        addRow(p, "Số điện thoại",  tfSDT);
        addRow(p, "Địa chỉ",        tfDiaChi);
        addRow(p, "Email",          tfEmail);
        addRow(p, "Chức vụ",        lblChucVu);
        addRow(p, "Tên tài khoản",  tfTaiKhoan);
        addRow(p, "Mật khẩu",       pfMatKhau);

        // Save button
        JButton btnSave = buildPrimaryButton("Lưu thay đổi");
        btnSave.addActionListener(e -> saveChanges());
        p.add(new JLabel(), "");
        p.add(btnSave, "width 160!, gaptop 8");

        return p;
    }

    private void addRow(JPanel p, String labelText, JComponent field) {
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(TEXT_SUB);
        p.add(lbl);
        p.add(field, "growx, height 36!");
    }

    // ── Save ─────────────────────────────────────────────────────────────────
    private void saveChanges() {
        if (nhanVien == null) return;
        try {
            nhanVien.setTennhanvien(tfTenNV.getText().trim());
            nhanVien.setGioitinh(cbGioiTinh.getSelectedIndex());
            nhanVien.setSdt(tfSDT.getText().trim());
            nhanVien.setDiachi(tfDiaChi.getText().trim());
            nhanVien.setEmail(tfEmail.getText().trim());
            busNV.updateNhanVien(nhanVien);
            
            // Cập nhật tài khoản và mật khẩu
            String newUsername = tfTaiKhoan.getText().trim();
            String newPassword = new String(pfMatKhau.getPassword()).trim();
            if (taikhoan != null) {
                busTK.updateTaiKhoan(maNhanVien, newUsername, newPassword, taikhoan.getIsblock());
                taikhoan.setTendangnhap(newUsername);
                taikhoan.setMatkhau(newPassword);
            }
            
            JOptionPane.showMessageDialog(this, "Lưu thông tin thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Populate fields ───────────────────────────────────────────────────────
    private void updateFields() throws Exception {
        tfMaNV.setText(String.valueOf(nhanVien.getManhanvien()));
        tfTenNV.setText(nhanVien.getTennhanvien());
        cbGioiTinh.setSelectedIndex(nhanVien.getGioitinh());
        if (nhanVien.getNgaysinh() != null)
            tfNgaySinh.setText(new SimpleDateFormat("dd/MM/yyyy").format(nhanVien.getNgaysinh()));
        tfSDT.setText(nhanVien.getSdt());
        tfDiaChi.setText(nhanVien.getDiachi());
        tfEmail.setText(nhanVien.getEmail());
        lblChucVu.setText(busNV.getTenChucVu(nhanVien.getMachucvu()));

        if (nhanVien.getImg() != null && !nhanVien.getImg().isEmpty()) {
            String path = System.getProperty("user.dir") + "/src/source/image/nhanvien/" + nhanVien.getImg();
            loadAvatar(path);
            currentImagePath = nhanVien.getImg();
        }

        if (taikhoan != null) {
            tfTaiKhoan.setText(taikhoan.getTendangnhap());
            pfMatKhau.setText(taikhoan.getMatkhau());
        }

        // Update name under avatar
        for (Component c : ((JPanel) lblAvatar.getParent()).getComponents()) {
            if (c instanceof JLabel && "nameUnder".equals(c.getName()))
                ((JLabel) c).setText(nhanVien.getTennhanvien());
        }
    }

    // ── Component helpers ─────────────────────────────────────────────────────
    private JTextField makeField() {
        JTextField f = new JTextField();
        styleField(f);
        return f;
    }

    private void styleField(JTextComponent f) {
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setForeground(TEXT_HEAD);
        f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_C, 1, true),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)
        ));
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(PRIMARY, 1, true), BorderFactory.createEmptyBorder(4, 10, 4, 10))); }
            public void focusLost(FocusEvent e)   { f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(BORDER_C, 1, true), BorderFactory.createEmptyBorder(4, 10, 4, 10))); }
        });
    }

    private void styleCombo(JComboBox<String> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cb.setBackground(Color.WHITE);
        cb.setForeground(TEXT_HEAD);
        cb.setBorder(new LineBorder(BORDER_C, 1, true));
    }

    private JButton buildPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(PRIMARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(HOVER_BTN); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(PRIMARY); }
        });
        return btn;
    }

    private JButton buildSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(PRIMARY);
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(PRIMARY, 1, true),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(new Color(238, 242, 255)); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(Color.WHITE); }
        });
        return btn;
    }
}