package gui.form;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.toedter.calendar.JDateChooser;
import net.miginfocom.swing.MigLayout;

import bus.bushopdong;
import bus.busnhanvien;
import dto.dtohopdong;
import dto.dtonhanvien;
import gui.table.TableHeaderAlignment;

public class formhopdong extends JPanel {

    // ── Palette ───────────────────────────────────────────────────
    private static final Color ACCENT      = new Color( 99, 102, 241);
    private static final Color ACCENT_SOFT = new Color(238, 242, 255);
    private static final Color SUCCESS     = new Color( 34, 197,  94);
    private static final Color DANGER      = new Color(239,  68,  68);
    private static final Color TEXT_MUTED  = new Color(100, 116, 139);

    // ── BUS ───────────────────────────────────────────────────────
    private final bushopdong  bushd  = new bushopdong();
    private final busnhanvien busnv  = new busnhanvien();

    // ── Table ─────────────────────────────────────────────────────
    private JTable            table;
    private DefaultTableModel model;
    private final Object[]    cols = {
        "Mã HĐ", "Từ ngày", "Đến ngày", "Lương cơ bản", "Tên nhân viên"
    };

    // ── Search ────────────────────────────────────────────────────
    private JComboBox<String> cbSearchBy;

    // ── Dialog fields ─────────────────────────────────────────────
    private JTextField   txtMaHD, txtLuongCoBan;
    private JDateChooser dateTuNgay, dateDenNgay;
    private JComboBox<String> comboMaNV, comboTenNV;

    // ─────────────────────────────────────────────────────────────
    public formhopdong() throws SQLException {
        setLayout(new MigLayout("fill, insets 16 20 16 20, wrap",
                "[fill]", "[shrink 0][fill,grow]"));
        add(buildHeader(), "growx");
        add(buildCard(),   "grow");
    }

    // ─────────────────────────────────────────────────────────────
    //  HEADER
    // ─────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new MigLayout("fillx, insets 0 0 12 0",
                "[fill]push[]", "[]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        JLabel title = new JLabel("Quản lý Hợp đồng");
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +6;");
        p.add(title);

        JLabel badge = new JLabel(bushd.getlist().size() + " hợp đồng");
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
    //  CARD
    // ─────────────────────────────────────────────────────────────
    private JPanel buildCard() {
        JPanel card = new JPanel(new MigLayout(
                "fill, insets 0, wrap", "[fill]", "[shrink 0][1!][fill,grow]"));
        card.putClientProperty(FlatClientProperties.STYLE,
                "background: $Panel.background; border: 1,1,1,1,$Component.borderColor,,12;");

        card.add(buildToolbar(), "growx, gapx 16 16, gapy 12 10");

        JSeparator sep = new JSeparator();
        sep.putClientProperty(FlatClientProperties.STYLE, "foreground: $Table.gridColor;");
        card.add(sep, "growx, height 1!");

        card.add(buildTable(), "grow");
        return card;
    }

    // ─────────────────────────────────────────────────────────────
    //  TOOLBAR
    // ─────────────────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel p = new JPanel(new MigLayout(
        "insets 0, fillx, wrap",
        "[grow][pref][pref][pref][pref][pref][pref]",
        "[center]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        JTextField txtSearch = new JTextField(20);
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
                "Tìm mã HĐ, mã NV...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE,
                "arc: 8; focusedBorderColor: #6366f1; borderWidth: 1;");

        cbSearchBy = new JComboBox<>(new String[]{"Mã hợp đồng", "Mã nhân viên"});
        cbSearchBy.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");

        JButton btnSearch = accentBtn("Tìm", ACCENT);
        btnSearch.addActionListener(e -> doSearch(txtSearch.getText().trim()));

        JButton btnReset = outlineBtn("↺ Tải lại");
        btnReset.addActionListener(e -> { txtSearch.setText(""); loadTable(); });

        JButton btnAdd  = accentBtn("+ Thêm", SUCCESS);
        btnAdd.addActionListener(e -> openDialog("Thêm hợp đồng", -1));

        JButton btnEdit = outlineBtn("✎ Sửa");
        btnEdit.addActionListener(e -> editRow());

        JButton btnDel  = new JButton("✕ Xóa");
        btnDel.putClientProperty(FlatClientProperties.STYLE,
                "background: " + hex(DANGER) + "; foreground: #ffffff;" +
                "arc: 8; borderWidth: 0; focusWidth: 0;");
        btnDel.setFocusPainted(false);
        btnDel.addActionListener(e -> deleteRow());

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                doSearch(txtSearch.getText().trim());
            }
        });

        p.add(txtSearch,  "height 32!");
        p.add(cbSearchBy, "height 32!");
        p.add(new JSeparator(JSeparator.VERTICAL), "growy, width 1!");
        p.add(btnSearch,  "height 32!");
        p.add(btnReset,   "height 32!");
        p.add(new JSeparator(JSeparator.VERTICAL), "growy, width 1!");
        p.add(btnAdd,     "height 32!");
        p.add(btnEdit,    "height 32!");
        p.add(btnDel,     "height 32!");
        return p;
    }

    // ─────────────────────────────────────────────────────────────
    //  TABLE
    // ─────────────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
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
        table.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(table) {
            protected int getAlignment() { return SwingConstants.CENTER; }
        });
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE,
                "height:34; hoverBackground:null; pressedBackground:null;" +
                "separatorColor:$TableHeader.background; font:bold +1;");
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        int[] widths = {70, 120, 120, 140, 200};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i : new int[]{0, 1, 2, 3})
            table.getColumnModel().getColumn(i).setCellRenderer(center);

        loadTable();

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
                "trackArc:$ScrollBar.thumbArc; thumbInsets:0,2,0,2; width:7;");
        scroll.getHorizontalScrollBar().putClientProperty(FlatClientProperties.STYLE,
                "trackArc:$ScrollBar.thumbArc; thumbInsets:0,2,0,2; width:7;");
        return scroll;
    }

    // ─────────────────────────────────────────────────────────────
    //  DATA
    // ─────────────────────────────────────────────────────────────
    private void loadTable() {
        model.setRowCount(0);
        for (dtohopdong hd : bushd.getlist()) {
            String tenNV = busnv.gettennvbymanv(hd.getMaNV());
            model.addRow(new Object[]{
                hd.getMaHopDong(), hd.getTuNgay(), hd.getDenNgay(),
                hd.getLuongCoBan(), tenNV != null ? tenNV : "(NV " + hd.getMaNV() + ")"
            });
        }
    }

    private void doSearch(String kw) {
        String by = (String) cbSearchBy.getSelectedItem();
        model.setRowCount(0);
        ArrayList<dtohopdong> list;
        if (kw.isEmpty()) {
            list = bushd.getlist();
        } else {
            list = "Mã hợp đồng".equals(by)
                    ? bushd.getlistConditon("mahopdong", kw)
                    : bushd.getlistConditon("maNhanVien", kw);
        }
        for (dtohopdong hd : list) {
            String tenNV = busnv.gettennvbymanv(hd.getMaNV());
            model.addRow(new Object[]{
                hd.getMaHopDong(), hd.getTuNgay(), hd.getDenNgay(),
                hd.getLuongCoBan(), tenNV != null ? tenNV : "(NV " + hd.getMaNV() + ")"
            });
        }
    }

    private void editRow() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Bạn chưa chọn hàng để sửa!"); return; }
        openDialog("Sửa hợp đồng", row);
    }

    private void deleteRow() {
        int row = table.getSelectedRow();
        if (row < 0) { warn("Bạn chưa chọn hàng để xóa!"); return; }
        if (!confirm("Xác nhận xóa hợp đồng này?")) return;
        int ma = (int) table.getValueAt(row, 0);
        if (bushd.Deleted(ma)) { info("Xóa thành công!"); loadTable(); }
    }

    // ─────────────────────────────────────────────────────────────
    //  DIALOG
    // ─────────────────────────────────────────────────────────────
    private void openDialog(String title, int selectedRow) {
        JDialog dlg = new JDialog((JFrame) null, title, true);
        try {
            dlg.setContentPane(buildForm(title, selectedRow, dlg));
        } catch (SQLException ex) {
            Logger.getLogger(formhopdong.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        dlg.pack();
        dlg.setLocationRelativeTo(table);
        dlg.setVisible(true);
    }

    private JPanel buildForm(String mode, int selectedRow, JDialog dlg) throws SQLException {
        JPanel p = new JPanel(new MigLayout(
                "fillx, wrap, insets 20 24 20 24, width 420", "[fill]", "[]8[]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: $Panel.background;");

        JLabel lbl = new JLabel(mode);
        lbl.putClientProperty(FlatClientProperties.STYLE, "font: bold +3;");
        p.add(lbl, "gapy 0 12");

        // Fields
        txtMaHD = disabledField(String.valueOf(bushd.getMaxMaHopDong() + 1));
        txtLuongCoBan = new JTextField();
        dateTuNgay  = makeDateChooser();
        dateDenNgay = makeDateChooser();
        applyFieldStyle(txtLuongCoBan);
        txtLuongCoBan.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập lương cơ bản");

        // ComboBox NV
        comboMaNV  = new JComboBox<>();
        comboTenNV = new JComboBox<>();
        comboMaNV.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
        comboTenNV.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");

        comboMaNV.addItem("");
        comboTenNV.addItem("");
        for (String ma : bushd.getListMaNV()) comboMaNV.addItem(ma);
        for (dtonhanvien nv : busnv.list_nv) comboTenNV.addItem(nv.getTennhanvien());

        // Sync combos
        comboTenNV.addActionListener(e -> {
            String ten = comboTenNV.getSelectedItem() + "";
            for (dtonhanvien nv : busnv.list_nv) {
                if (ten.equals(nv.getTennhanvien())) {
                    comboMaNV.setSelectedItem(nv.getManhanvien() + ""); return;
                }
            }
            comboMaNV.setSelectedItem("");
        });
        comboMaNV.addActionListener(e -> {
            String ma = comboMaNV.getSelectedItem() + "";
            if (ma.isEmpty()) { comboTenNV.setSelectedItem(""); return; }
            try {
                String ten = busnv.gettennvbymanv(Integer.parseInt(ma));
                comboTenNV.setSelectedItem(ten != null ? ten : "");
            } catch (NumberFormatException ignored) {}
        });

        p.add(fieldLbl("Mã hợp đồng")); p.add(txtMaHD,       "height 34!");
        p.add(fieldLbl("Tên nhân viên")); p.add(comboTenNV,   "height 34!");
        p.add(fieldLbl("Mã nhân viên")); p.add(comboMaNV,     "height 34!");
        p.add(fieldLbl("Từ ngày"));      p.add(dateTuNgay,    "height 34!");
        p.add(fieldLbl("Đến ngày"));     p.add(dateDenNgay,   "height 34!");
        p.add(fieldLbl("Lương cơ bản")); p.add(txtLuongCoBan, "height 34!");

        // Pre-fill khi sửa
        if (selectedRow >= 0) {
            txtMaHD.setText(table.getValueAt(selectedRow, 0).toString());
            txtLuongCoBan.setText(table.getValueAt(selectedRow, 3).toString());
            String tenNV = table.getValueAt(selectedRow, 4).toString();
            comboTenNV.setSelectedItem(tenNV);
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                dateTuNgay.setDate(sdf.parse(table.getValueAt(selectedRow, 1).toString()));
                dateDenNgay.setDate(sdf.parse(table.getValueAt(selectedRow, 2).toString()));
            } catch (ParseException ignored) {}
        }

        JSeparator sep = new JSeparator();
        sep.putClientProperty(FlatClientProperties.STYLE, "foreground: $Table.gridColor;");
        p.add(sep, "growx, height 1!, gapy 8 8");

        // Buttons
        JPanel btns = new JPanel(new MigLayout("insets 0", "push[]8[]", "[]"));
        btns.putClientProperty(FlatClientProperties.STYLE, "background: null;");
        JButton btnOK  = accentBtn("Xác nhận", ACCENT);
        JButton btnCan = outlineBtn("Hủy bỏ");
        btnOK.addActionListener(e  -> submitForm(mode, dlg));
        btnCan.addActionListener(e -> { if (confirm("Hủy bỏ thao tác?")) dlg.dispose(); });
        btns.add(btnOK,  "height 34!, width 110!");
        btns.add(btnCan, "height 34!, width 90!");
        p.add(btns, "growx");
        return p;
    }

    private void submitForm(String mode, JDialog dlg) {
        if (dateTuNgay.getDate() == null || dateDenNgay.getDate() == null) {
            warn("Vui lòng chọn đầy đủ ngày!"); return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String tuNgay  = sdf.format(dateTuNgay.getDate());
        String denNgay = sdf.format(dateDenNgay.getDate());

        if (!kiemTraThoiGian(tuNgay, denNgay)) {
            warn("Từ ngày phải nhỏ hơn hoặc bằng đến ngày!"); return;
        }
        if (txtLuongCoBan.getText().trim().isEmpty()) {
            warn("Vui lòng nhập lương cơ bản!"); return;
        }
        if (!kiemTraLuongCoBan(txtLuongCoBan.getText().trim())) {
            warn("Lương cơ bản phải là số dương!"); return;
        }
        String maNVStr = comboMaNV.getSelectedItem() + "";
        if (maNVStr.isEmpty()) { warn("Vui lòng chọn nhân viên!"); return; }

        int maHD = Integer.parseInt(txtMaHD.getText());
        float luong = Float.parseFloat(txtLuongCoBan.getText().trim());
        int maNV = Integer.parseInt(maNVStr);
        dtohopdong hd = new dtohopdong(maHD, tuNgay, denNgay, luong, maNV, 0);

        if ("Thêm hợp đồng".equals(mode)) {
            if (bushd.addHopDong(hd)) {
                info("Thêm hợp đồng thành công!");
                txtMaHD.setText((bushd.getMaxMaHopDong() + 1) + "");
                txtLuongCoBan.setText("");
                dateTuNgay.setDate(null); dateDenNgay.setDate(null);
                comboMaNV.setSelectedIndex(0); comboTenNV.setSelectedIndex(0);
                loadTable();
            }
        } else {
            if (bushd.Update(hd)) {
                info("Cập nhật thành công!"); dlg.dispose(); loadTable();
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  VALIDATION
    // ─────────────────────────────────────────────────────────────
    private boolean kiemTraLuongCoBan(String s) {
        try { return Double.parseDouble(s) > 0; }
        catch (NumberFormatException e) { return false; }
    }

    private boolean kiemTraThoiGian(String tu, String den) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        try { return !sdf.parse(tu).after(sdf.parse(den)); }
        catch (ParseException e) { return false; }
    }

    // ─────────────────────────────────────────────────────────────
    //  UI HELPERS
    // ─────────────────────────────────────────────────────────────
    private JDateChooser makeDateChooser() {
        JDateChooser dc = new JDateChooser();
        dc.setDateFormatString("yyyy-MM-dd");
        ((JTextField) dc.getDateEditor().getUiComponent()).setEditable(false);
        dc.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
        return dc;
    }

    private JTextField disabledField(String val) {
        JTextField tf = new JTextField(val);
        tf.setEnabled(false);
        tf.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 1;");
        return tf;
    }

    private void applyFieldStyle(JTextField... fields) {
        for (JTextField tf : fields)
            tf.putClientProperty(FlatClientProperties.STYLE,
                    "arc: 8; borderWidth: 1; focusedBorderColor: #6366f1;");
    }

    private JLabel fieldLbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.PLAIN, 12f));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private JButton accentBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.putClientProperty(FlatClientProperties.STYLE,
                "background: " + hex(bg) + "; foreground: #ffffff;" +
                "borderWidth: 0; arc: 8; focusWidth: 0;");
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton outlineBtn(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty(FlatClientProperties.STYLE,
                "arc: 8; borderWidth: 1; focusWidth: 0;");
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static String hex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
    private boolean confirm(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}