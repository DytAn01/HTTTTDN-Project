package gui.form;

import bus.buskhachhang;
import bus.busuudai;
import dto.dtokhachhang;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class formkhachhang extends JPanel {

    // ── Palette (dùng chung với các form khác) ────────────────────
    private static final Color ACCENT      = new Color( 99, 102, 241);
    private static final Color ACCENT_SOFT = new Color(238, 242, 255);
    private static final Color SUCCESS     = new Color( 34, 197,  94);
    private static final Color DANGER      = new Color(239,  68,  68);
    private static final Color BORDER      = new Color(226, 232, 240);
    private static final Color TEXT_MUTED  = new Color(100, 116, 139);

    // ── BUS ───────────────────────────────────────────────────────
    private final buskhachhang busKH    = new buskhachhang();
    private final busuudai     busUuDai = new busuudai();

    // ── Form fields ───────────────────────────────────────────────
    private JTextField   txtMaKH, txtTenKH, txtSDT, txtDiemTichLuy;
    private JComboBox<String> cboUuDai;

    // ── Search ────────────────────────────────────────────────────
    private JTextField   txtTimKiem;
    private JComboBox<String> cbTimKiem;

    // ── Table ─────────────────────────────────────────────────────
    private JTable            table;
    private DefaultTableModel tableModel;

    // ── Buttons ───────────────────────────────────────────────────
    private JButton btnThem, btnSua, btnReset;

    // ── State ─────────────────────────────────────────────────────
    private int selectedMaKH = -1;

    // ─────────────────────────────────────────────────────────────
    public formkhachhang() {
        setLayout(new MigLayout("fill, insets 16 20 16 20, wrap", "[fill]", "[shrink 0][fill,grow]"));
        add(buildHeader(),  "growx");
        add(buildContent(), "grow");
    }

    // ─────────────────────────────────────────────────────────────
    //  HEADER
    // ─────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new MigLayout("fillx, insets 0 0 12 0", "[fill]push[]", "[]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        JLabel title = new JLabel("Quản lý Khách hàng");
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +6;");
        p.add(title);

        busKH.getAllKhachHang(); // load để lấy size
        JLabel badge = new JLabel(busKH.getAllKhachHang().size() + " khách hàng");
        badge.setFont(badge.getFont().deriveFont(Font.BOLD, 12f));
        badge.setForeground(ACCENT);
        badge.setBackground(ACCENT_SOFT);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(199, 210, 254), 1, true),
            BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        p.add(badge);
        return p;
    }

    // ─────────────────────────────────────────────────────────────
    //  MAIN CONTENT  (form trái + bảng phải)
    // ─────────────────────────────────────────────────────────────
    private JPanel buildContent() {
        JPanel p = new JPanel(new MigLayout(
            "fill, insets 0, gap 16", "[300!][fill,grow]", "[fill]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");
        p.add(buildFormCard(), "grow");
        p.add(buildTableCard(), "grow");
        return p;
    }

    // ─────────────────────────────────────────────────────────────
    //  FORM CARD (trái)
    // ─────────────────────────────────────────────────────────────
    private JPanel buildFormCard() {
        JPanel card = new JPanel(new MigLayout(
            "fillx, wrap, insets 0", "[fill]", "[shrink 0][1!][fill,grow]"));
        card.putClientProperty(FlatClientProperties.STYLE,
            "background: $Panel.background; border: 1,1,1,1,$Component.borderColor,,12;");

        // Card header
        JPanel header = new JPanel(new MigLayout("fillx, insets 14 16 14 16", "[fill]push[]", "[]"));
        header.putClientProperty(FlatClientProperties.STYLE, "background: null;");
        JLabel lbl = new JLabel("Thông tin khách hàng");
        lbl.putClientProperty(FlatClientProperties.STYLE, "font: bold +1;");
        header.add(lbl);

        JLabel modeBadge = new JLabel("Thêm mới");
        modeBadge.setFont(modeBadge.getFont().deriveFont(Font.BOLD, 11f));
        modeBadge.setForeground(new Color(22, 163, 74));
        modeBadge.setBackground(new Color(220, 252, 231));
        modeBadge.setOpaque(true);
        modeBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(187, 247, 208), 1, true),
            BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        header.add(modeBadge);
        card.add(header, "growx");

        JSeparator sep = new JSeparator();
        sep.putClientProperty(FlatClientProperties.STYLE, "foreground: $Table.gridColor;");
        card.add(sep, "growx, height 1!");

        // Form fields
        JPanel form = new JPanel(new MigLayout(
            "fillx, wrap, insets 16 16 16 16", "[fill]", "[]8[]8[]8[]8[]8[]16[]"));
        form.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        // Mã KH
        form.add(fieldLabel("Mã khách hàng"), "");
        txtMaKH = styledField();
        txtMaKH.setEnabled(false);
        form.add(txtMaKH, "height 34!");

        // Tên KH
        form.add(fieldLabel("Họ tên khách hàng"), "");
        txtTenKH = styledField();
        txtTenKH.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập họ tên...");
        form.add(txtTenKH, "height 34!");

        // SĐT
        form.add(fieldLabel("Số điện thoại"), "");
        txtSDT = styledField();
        txtSDT.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "0xxxxxxxxx");
        form.add(txtSDT, "height 34!");

        // Điểm tích lũy
        form.add(fieldLabel("Điểm tích lũy"), "");
        txtDiemTichLuy = styledField();
        txtDiemTichLuy.setEnabled(false);
        txtDiemTichLuy.setText("0");
        form.add(txtDiemTichLuy, "height 34!");

        // Ưu đãi
        form.add(fieldLabel("Mã ưu đãi"), "");
        cboUuDai = new JComboBox<>();
        cboUuDai.putClientProperty(FlatClientProperties.STYLE,
            "arc: 8; borderWidth: 1; focusedBorderColor: #6366f1;");
        loadDanhSachUuDai();
        form.add(cboUuDai, "height 34!");

        // Separator nhỏ
        JSeparator sepForm = new JSeparator();
        sepForm.putClientProperty(FlatClientProperties.STYLE, "foreground: $Table.gridColor;");
        form.add(sepForm, "growx, height 1!");

        // Buttons
        JPanel btnPanel = new JPanel(new MigLayout("insets 0", "[]8[]push[]", "[]"));
        btnPanel.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        btnThem = accentButton("+ Thêm", ACCENT);
        btnSua  = accentButton("✎ Lưu sửa", new Color(79, 70, 229));
        btnSua.setEnabled(false);
        btnReset = outlineButton("↺ Reset");

        btnThem.addActionListener(e -> onAddCustomer(modeBadge));
        btnSua.addActionListener(e -> {
            try { onEditCustomer(modeBadge); }
            catch (SQLException ex) { Logger.getLogger(formkhachhang.class.getName()).log(Level.SEVERE, null, ex); }
        });
        btnReset.addActionListener(e -> onReset(modeBadge));

        btnPanel.add(btnThem,  "height 34!");
        btnPanel.add(btnSua,   "height 34!");
        btnPanel.add(btnReset, "height 34!");
        form.add(btnPanel, "growx");

        card.add(form, "grow");
        return card;
    }

    // ─────────────────────────────────────────────────────────────
    //  TABLE CARD (phải)
    // ─────────────────────────────────────────────────────────────
    private JPanel buildTableCard() {
        JPanel card = new JPanel(new MigLayout(
            "fill, insets 0, wrap", "[fill]", "[shrink 0][1!][fill,grow]"));
        card.putClientProperty(FlatClientProperties.STYLE,
            "background: $Panel.background; border: 1,1,1,1,$Component.borderColor,,12;");

        card.add(buildSearchToolbar(), "growx, gapx 16 16, gapy 12 10");

        JSeparator sep = new JSeparator();
        sep.putClientProperty(FlatClientProperties.STYLE, "foreground: $Table.gridColor;");
        card.add(sep, "growx, height 1!");

        card.add(buildTable(), "grow");
        return card;
    }

    private JPanel buildSearchToolbar() {
        JPanel p = new JPanel(new MigLayout(
            "fillx, insets 0", "[fill,grow][140!][8!][fill][8!][fill]push", "[center]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        txtTimKiem = new JTextField();
        txtTimKiem.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm tên, SĐT, mã KH...");
        txtTimKiem.putClientProperty(FlatClientProperties.STYLE,
            "arc: 8; focusedBorderColor: #6366f1; borderWidth: 1;");
        txtTimKiem.addActionListener(e -> onSearch());
        p.add(txtTimKiem, "height 32!");

        cbTimKiem = new JComboBox<>(new String[]{"Tất cả", "Mã khách hàng", "Tên khách hàng", "Số điện thoại"});
        cbTimKiem.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
        p.add(cbTimKiem, "height 32!");

        p.add(new JSeparator(JSeparator.VERTICAL), "growy, width 1!");

        JButton btnSearch = accentButton("Tìm kiếm", ACCENT);
        btnSearch.addActionListener(e -> onSearch());
        p.add(btnSearch, "height 32!");

        p.add(new JSeparator(JSeparator.VERTICAL), "growy, width 1!");

        JButton btnReload = outlineButton("↺ Tải lại");
        btnReload.addActionListener(e -> {
            txtTimKiem.setText("");
            loadDataToTable();
        });
        p.add(btnReload, "height 32!");

        return p;
    }

    private JScrollPane buildTable() {
        String[] cols = {"Mã KH", "SĐT", "Tên khách hàng", "Điểm tích lũy", "Mã ưu đãi"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.putClientProperty(FlatClientProperties.STYLE,
            "rowHeight:36; showHorizontalLines:true; intercellSpacing:0,1;" +
            "cellFocusColor:$TableHeader.hoverBackground;" +
            "selectionBackground:$TableHeader.hoverBackground;" +
            "selectionInactiveBackground:$TableHeader.hoverBackground;" +
            "selectionForeground:$Table.foreground;");
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE,
            "height:34; hoverBackground:null; pressedBackground:null;" +
            "separatorColor:$TableHeader.background; font:bold +1;");
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Column widths
        int[] widths = {70, 120, 200, 100, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Center renderer
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i : new int[]{0, 1, 3, 4})
            table.getColumnModel().getColumn(i).setCellRenderer(center);

        // Row selection → fill form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onRowSelected();
        });

        loadDataToTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
            "trackArc:$ScrollBar.thumbArc; thumbInsets:0,2,0,2; width:7;");
        return scroll;
    }

    // ─────────────────────────────────────────────────────────────
    //  DATA METHODS
    // ─────────────────────────────────────────────────────────────
    private void loadDataToTable() {
        tableModel.setRowCount(0);
        ArrayList<dtokhachhang> list = busKH.getAllKhachHang();
        for (dtokhachhang kh : list) {
            tableModel.addRow(new Object[]{
                kh.getMaKhachHang(),
                kh.getSDT(),
                kh.getTenKhachHang(),
                kh.getDiemTichLuy(),
                kh.getMaUudai()
            });
        }
    }

    private void loadDanhSachUuDai() {
        cboUuDai.removeAllItems();
        cboUuDai.addItem("1");
        ArrayList<String> list = busUuDai.layDanhSachMaUuDai();
        for (String ma : list) cboUuDai.addItem(ma);
    }

    private void onRowSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        selectedMaKH = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        txtMaKH.setText(tableModel.getValueAt(row, 0).toString());
        txtSDT.setText(tableModel.getValueAt(row, 1).toString());
        txtTenKH.setText(tableModel.getValueAt(row, 2).toString());
        txtDiemTichLuy.setText(tableModel.getValueAt(row, 3).toString());
        cboUuDai.setSelectedItem(tableModel.getValueAt(row, 4).toString());
        btnSua.setEnabled(true);
        btnThem.setEnabled(false);
    }

    private void onSearch() {
        String kw       = txtTimKiem.getText().trim();
        String criteria = (String) cbTimKiem.getSelectedItem();

        if (kw.isEmpty()) { loadDataToTable(); return; }

        ArrayList<dtokhachhang> results = new ArrayList<>();
        switch (criteria) {
            case "Mã khách hàng" -> {
                try {
                    dtokhachhang kh = busKH.getKhachHangById(Integer.parseInt(kw));
                    if (kh != null) results.add(kh);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Mã KH phải là số!");
                    return;
                }
            }
            case "Tên khách hàng" -> results = busKH.searchKhachHangByName(kw);
            case "Số điện thoại"  -> results = busKH.searchKhachHangBySDT(kw);
            default -> {
                // Tất cả — lọc theo tên
                results = busKH.searchKhachHangByName(kw);
            }
        }

        tableModel.setRowCount(0);
        for (dtokhachhang kh : results) {
            tableModel.addRow(new Object[]{
                kh.getMaKhachHang(), kh.getSDT(),
                kh.getTenKhachHang(), kh.getDiemTichLuy(), kh.getMaUudai()
            });
        }
        if (results.isEmpty())
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    // ─────────────────────────────────────────────────────────────
    //  CRUD
    // ─────────────────────────────────────────────────────────────
    private void onAddCustomer(JLabel modeBadge) {
        String ten  = txtTenKH.getText().trim();
        String sdt  = txtSDT.getText().trim();
        String maUD = cboUuDai.getSelectedItem() != null ? cboUuDai.getSelectedItem().toString() : "1";

        if (ten.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!"); return;
        }
        if (!sdt.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải là 10 chữ số!"); return;
        }
        if (busKH.checkSDTExist(sdt)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại đã tồn tại!"); return;
        }
        if (!ten.matches("[\\p{L}\\s]+")) {
            JOptionPane.showMessageDialog(this, "Tên chỉ chứa chữ cái và khoảng trắng!"); return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận thêm khách hàng?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            int maKH = busKH.getSoLuongKH() + 1;
            dtokhachhang kh = new dtokhachhang(maKH, sdt, ten, 0, Integer.parseInt(maUD));
            if (busKH.addKhachHang(kh)) {
                loadDataToTable();
                clearForm(modeBadge);
                JOptionPane.showMessageDialog(this, "✅ Thêm khách hàng thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm khách hàng!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã ưu đãi không hợp lệ!");
        }
    }

    private void onEditCustomer(JLabel modeBadge) throws SQLException {
        if (selectedMaKH < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để sửa!"); return;
        }
        String ten  = txtTenKH.getText().trim();
        String sdt  = txtSDT.getText().trim();
        String diem = txtDiemTichLuy.getText().trim();
        String maUD = cboUuDai.getSelectedItem() != null ? cboUuDai.getSelectedItem().toString() : "1";

        if (ten.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!"); return;
        }
        if (!sdt.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải là 10 chữ số!"); return;
        }
        dtokhachhang existing = busKH.getKhachHangById(selectedMaKH);
        if (existing != null && !existing.getSDT().equals(sdt) && busKH.checkSDTExist(sdt)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại đã tồn tại!"); return;
        }
        if (!ten.matches("[\\p{L}\\s]+")) {
            JOptionPane.showMessageDialog(this, "Tên chỉ chứa chữ cái và khoảng trắng!"); return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận lưu thay đổi?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            dtokhachhang kh = new dtokhachhang(
                selectedMaKH, sdt, ten,
                Integer.parseInt(diem.isEmpty() ? "0" : diem),
                Integer.parseInt(maUD));
            if (busKH.updateKhachHang(kh)) {
                loadDataToTable();
                clearForm(modeBadge);
                JOptionPane.showMessageDialog(this, "✅ Cập nhật thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!");
        }
    }

    private void onReset(JLabel modeBadge) {
        clearForm(modeBadge);
        txtTimKiem.setText("");
        loadDataToTable();
    }

    private void clearForm(JLabel modeBadge) {
        selectedMaKH = -1;
        txtMaKH.setText("");
        txtTenKH.setText("");
        txtSDT.setText("");
        txtDiemTichLuy.setText("0");
        if (cboUuDai.getItemCount() > 0) cboUuDai.setSelectedIndex(0);
        table.clearSelection();
        btnThem.setEnabled(true);
        btnSua.setEnabled(false);
        // Cập nhật badge mode
        if (modeBadge != null) {
            modeBadge.setText("Thêm mới");
            modeBadge.setForeground(new Color(22, 163, 74));
            modeBadge.setBackground(new Color(220, 252, 231));
            modeBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(187, 247, 208), 1, true),
                BorderFactory.createEmptyBorder(3, 10, 3, 10)));
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  UI HELPERS
    // ─────────────────────────────────────────────────────────────
    private JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 12f));
        lbl.setForeground(TEXT_MUTED);
        return lbl;
    }

    private JTextField styledField() {
        JTextField tf = new JTextField();
        tf.putClientProperty(FlatClientProperties.STYLE,
            "arc: 8; borderWidth: 1; focusedBorderColor: #6366f1;");
        return tf;
    }

    private JButton accentButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.putClientProperty(FlatClientProperties.STYLE,
            "background: " + colorHex(bg) + "; foreground: #ffffff;" +
            "borderWidth: 0; arc: 8; focusWidth: 0; innerFocusWidth: 0;");
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton outlineButton(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty(FlatClientProperties.STYLE,
            "arc: 8; borderWidth: 1; focusWidth: 0;");
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static String colorHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}