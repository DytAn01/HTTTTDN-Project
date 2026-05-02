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

import bus.buskhuyenmai;
import bus.busuudai;
import dto.dtokhuyenmai;
import dto.dtouudai;
import gui.table.TableHeaderAlignment;

public class formuudaivakhuyenmai extends JPanel {

    // ── Palette ───────────────────────────────────────────────────
    private static final Color ACCENT      = new Color( 99, 102, 241);
    private static final Color ACCENT_SOFT = new Color(238, 242, 255);
    private static final Color SUCCESS     = new Color( 34, 197,  94);
    private static final Color DANGER      = new Color(239,  68,  68);
    private static final Color WARNING     = new Color(234, 179,   8);
    private static final Color TEXT_MUTED  = new Color(100, 116, 139);

    // ── BUS ───────────────────────────────────────────────────────
    private final buskhuyenmai buskm    = new buskhuyenmai();
    private final busuudai     busud    = new busuudai();

    // ── Khuyến mãi table ─────────────────────────────────────────
    private JTable            tableKM;
    private DefaultTableModel modelKM;
    private final Object[]    colsKM = {
        "Mã KM", "Tên khuyến mãi", "Ngày bắt đầu", "Ngày hết hạn",
        "Số lượng", "% Giảm", "Đã dùng"
    };
    private JComboBox<String> cbSearchKM;

    // ── Ưu đãi table ─────────────────────────────────────────────
    private JTable            tableUD;
    private DefaultTableModel modelUD;
    private final Object[]    colsUD = {"Mã ưu đãi", "Mốc ưu đãi", "Tỉ lệ giảm"};

    // ── Dialog fields – KM ────────────────────────────────────────
    private JTextField  txtMaKM, txtTenKM, txtSoLuong, txtPhanTramGiam;
    private JDateChooser dateNgayBD, dateNgayHetHan;

    // ── Dialog fields – UD ────────────────────────────────────────
    private JTextField txtMaUuDai, txtMocUuDai, txtTiLeGiam;

    // ─────────────────────────────────────────────────────────────
    public formuudaivakhuyenmai() {
        setLayout(new MigLayout("fill, insets 16 20 16 20, wrap", "[fill]",
                "[shrink 0][fill,grow]"));
        add(buildHeader(), "growx");
        add(buildTabs(),   "grow");
    }

    // ─────────────────────────────────────────────────────────────
    //  HEADER
    // ─────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new MigLayout("fillx, insets 0 0 12 0",
                "[fill]push[][]", "[]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        JLabel title = new JLabel("Ưu đãi & Khuyến mãi");
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +6;");
        p.add(title);

        p.add(countBadge(buskm.getlist().size() + " khuyến mãi",
                ACCENT, ACCENT_SOFT, new Color(199, 210, 254)));
        p.add(countBadge(busud.getList().size() + " ưu đãi",
                new Color(5, 150, 105), new Color(209, 250, 229), new Color(167, 243, 208)));
        return p;
    }

    private JLabel countBadge(String text, Color fg, Color bg, Color border) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 12f));
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border, 1, true),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        return lbl;
    }

    // ─────────────────────────────────────────────────────────────
    //  TABS
    // ─────────────────────────────────────────────────────────────
    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.putClientProperty(FlatClientProperties.STYLE, "tabType: card;");
        tabs.addTab("🏷  Khuyến mãi", wrapTab(buildKMCard()));
        tabs.addTab("⭐  Ưu đãi",     wrapTab(buildUDCard()));
        return tabs;
    }

    private JPanel wrapTab(Component c) {
        JPanel p = new JPanel(new MigLayout("fill, insets 10 0 10 0", "[fill]", "[fill]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");
        p.add(c);
        return p;
    }

    // ─────────────────────────────────────────────────────────────
    //  KHUYẾN MÃI CARD
    // ─────────────────────────────────────────────────────────────
    private JPanel buildKMCard() {
        JPanel card = new JPanel(new MigLayout(
                "fill, insets 0, wrap", "[fill]", "[shrink 0][1!][fill,grow]"));
        card.putClientProperty(FlatClientProperties.STYLE,
                "background: $Panel.background; border: 1,1,1,1,$Component.borderColor,,12;");

        card.add(buildKMToolbar(), "growx, gapx 16 16, gapy 12 10");

        JSeparator sep = new JSeparator();
        sep.putClientProperty(FlatClientProperties.STYLE, "foreground: $Table.gridColor;");
        card.add(sep, "growx, height 1!");

        card.add(buildKMTable(), "grow");
        return card;
    }

   private JPanel buildKMToolbar() {
    JPanel p = new JPanel(new MigLayout(
            "insets 8 16 8 16, fillx, wrap",
            "[grow][pref!][grow 0][pref][pref][pref][pref][pref]",
            "[center]"));
    p.putClientProperty(FlatClientProperties.STYLE, "background: null;");

    JTextField txtSearch = new JTextField();
    txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm mã, tên, ngày...");
    txtSearch.putClientProperty(FlatClientProperties.STYLE,
            "arc: 8; focusedBorderColor: #6366f1; borderWidth: 1;");

    cbSearchKM = new JComboBox<>(new String[]{"Mã khuyến mãi", "Tên khuyến mãi", "Ngày"});
    cbSearchKM.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");

    JButton btnSearch = accentBtn("Tìm", ACCENT);
    btnSearch.addActionListener(e -> searchKM(txtSearch.getText().trim()));

    JButton btnReset = outlineBtn("↺ Tải lại");
    btnReset.addActionListener(e -> { txtSearch.setText(""); loadKM(); });

    JButton btnAdd  = accentBtn("+ Thêm", SUCCESS);
    btnAdd.addActionListener(e -> openKMDialog("Thêm khuyến mãi", -1));

    JButton btnEdit = outlineBtn("✎ Sửa");
    btnEdit.addActionListener(e -> editKM());

    JButton btnDel  = new JButton("✕ Xóa");
    btnDel.putClientProperty(FlatClientProperties.STYLE,
            "background: " + hex(DANGER) + "; foreground: #ffffff; arc: 8; borderWidth: 0;");
    btnDel.setFocusPainted(false);
    btnDel.addActionListener(e -> deleteKM());

    txtSearch.addKeyListener(new KeyAdapter() {
        @Override public void keyReleased(KeyEvent e) {
            searchKM(txtSearch.getText().trim());
        }
    });

    p.add(txtSearch,  "growx, height 32!");
    p.add(cbSearchKM, "w 140!, height 32!");
    p.add(btnSearch,  "height 32!");
    p.add(btnReset,   "height 32!");
    p.add(btnAdd,     "height 32!");
    p.add(btnEdit,    "height 32!");
    p.add(btnDel,     "height 32!");

    return p;
}


    private JScrollPane buildKMTable() {
        modelKM = new DefaultTableModel(colsKM, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableKM = styledTable(modelKM);
        int[] w = {60, 180, 110, 110, 80, 80, 90};
        for (int i = 0; i < w.length; i++)
            tableKM.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        // Center all except tên
        DefaultTableCellRenderer center = centerRenderer();
        for (int i : new int[]{0, 2, 3, 4, 5, 6})
            tableKM.getColumnModel().getColumn(i).setCellRenderer(center);

        loadKM();
        return scrollOf(tableKM);
    }

    private void loadKM() {
        modelKM.setRowCount(0);
        for (dtokhuyenmai km : buskm.getlist())
            modelKM.addRow(kmRow(km));
    }

    private void searchKM(String kw) {
        String criteria = (String) cbSearchKM.getSelectedItem();
        modelKM.setRowCount(0);
        ArrayList<dtokhuyenmai> list;
        if (kw.isEmpty()) {
            list = buskm.getlist();
        } else {
            list = switch (criteria) {
                case "Mã khuyến mãi" -> {
                    try { yield buskm.getListByInt("maKhuyenMai", Integer.parseInt(kw)); }
                    catch (NumberFormatException ex) { yield new ArrayList<>(); }
                }
                case "Tên khuyến mãi" -> buskm.getListByString("tenKhuyenMai", kw);
                default               -> buskm.getListByDate(kw);
            };
        }
        for (dtokhuyenmai km : list) modelKM.addRow(kmRow(km));
    }

    private Object[] kmRow(dtokhuyenmai km) {
        return new Object[]{
            km.getMaKhuyenMai(), km.getTenKhuyenMai(),
            km.getNgayBatDau(), km.getNgayHetHan(),
            km.getSoLuong(), km.getPhanTram(), km.getSoLuongDaDung()
        };
    }

    private void editKM() {
        int row = tableKM.getSelectedRow();
        if (row < 0) { warn("Bạn chưa chọn khuyến mãi để sửa!"); return; }
        openKMDialog("Sửa khuyến mãi", row);
    }

    private void deleteKM() {
        int row = tableKM.getSelectedRow();
        if (row < 0) { warn("Bạn chưa chọn khuyến mãi để xóa!"); return; }
        if (confirm("Xác nhận xóa khuyến mãi này?")) {
            int ma = (int) tableKM.getValueAt(row, 0);
            if (buskm.Deleted(ma)) { loadKM(); info("Xóa thành công!"); }
        }
    }

    private void openKMDialog(String title, int selectedRow) {
        JDialog dlg = new JDialog((JFrame) null, title, true);
        dlg.setContentPane(buildKMForm(title, selectedRow, dlg));
        dlg.pack();
        dlg.setLocationRelativeTo(tableKM);
        dlg.setVisible(true);
    }

    private JPanel buildKMForm(String mode, int selectedRow, JDialog dlg) {
        JPanel p = new JPanel(new MigLayout(
                "fillx, wrap, insets 20 24 20 24, width 420", "[fill]", "[]8[]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: $Panel.background;");

        // Title bar
        JLabel lbl = new JLabel(mode);
        lbl.putClientProperty(FlatClientProperties.STYLE, "font: bold +3;");
        p.add(lbl, "gapy 0 12");

        txtMaKM = disabledField(String.valueOf(buskm.getMaxMaKhuyenMai() + 1));
        txtTenKM = new JTextField();
        dateNgayBD = makeDateChooser();
        dateNgayHetHan = makeDateChooser();
        txtSoLuong = new JTextField();
        txtPhanTramGiam = new JTextField();

        applyFieldStyle(txtTenKM, txtSoLuong, txtPhanTramGiam);

        p.add(fieldLbl("Mã khuyến mãi"));  p.add(txtMaKM,        "height 34!");
        p.add(fieldLbl("Tên khuyến mãi")); p.add(txtTenKM,       "height 34!");
        p.add(fieldLbl("Ngày bắt đầu"));   p.add(dateNgayBD,     "height 34!");
        p.add(fieldLbl("Ngày hết hạn"));   p.add(dateNgayHetHan, "height 34!");
        p.add(fieldLbl("Số lượng"));       p.add(txtSoLuong,     "height 34!");
        p.add(fieldLbl("Phần trăm giảm")); p.add(txtPhanTramGiam,"height 34!");

        // Pre-fill when editing
        if (selectedRow >= 0) {
            txtMaKM.setText(tableKM.getValueAt(selectedRow, 0).toString());
            txtTenKM.setText(tableKM.getValueAt(selectedRow, 1).toString());
            txtSoLuong.setText(tableKM.getValueAt(selectedRow, 4).toString());
            txtPhanTramGiam.setText(tableKM.getValueAt(selectedRow, 5).toString());
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                dateNgayBD.setDate(sdf.parse(tableKM.getValueAt(selectedRow, 2).toString()));
                dateNgayHetHan.setDate(sdf.parse(tableKM.getValueAt(selectedRow, 3).toString()));
            } catch (ParseException ex) { /* ignore */ }
        }

        JSeparator sep = new JSeparator();
        sep.putClientProperty(FlatClientProperties.STYLE, "foreground: $Table.gridColor;");
        p.add(sep, "growx, height 1!, gapy 8 8");

        JPanel btns = new JPanel(new MigLayout("insets 0", "push[]8[]", "[]"));
        btns.putClientProperty(FlatClientProperties.STYLE, "background: null;");
        JButton btnOK  = accentBtn("Xác nhận", ACCENT);
        JButton btnCan = outlineBtn("Hủy bỏ");
        btnOK.addActionListener(e  -> submitKMForm(mode, dlg));
        btnCan.addActionListener(e -> { if (confirm("Hủy bỏ thao tác?")) dlg.dispose(); });
        btns.add(btnOK,  "height 34!, width 110!");
        btns.add(btnCan, "height 34!, width 90!");
        p.add(btns, "growx");
        return p;
    }

    private void submitKMForm(String mode, JDialog dlg) {
        String ten = txtTenKM.getText().trim();
        if (ten.isEmpty()) { warn("Vui lòng nhập tên khuyến mãi!"); return; }
        if (dateNgayBD.getDate() == null || dateNgayHetHan.getDate() == null) {
            warn("Vui lòng chọn đầy đủ ngày!"); return;
        }
        if (dateNgayBD.getDate().after(dateNgayHetHan.getDate())) {
            warn("Ngày bắt đầu phải nhỏ hơn ngày hết hạn!"); return;
        }
        int soLuong; double phanTram;
        try {
            soLuong = Integer.parseInt(txtSoLuong.getText().trim());
            if (soLuong <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) { warn("Số lượng phải là số nguyên dương!"); return; }
        try {
            phanTram = Double.parseDouble(txtPhanTramGiam.getText().trim());
            if (phanTram <= 0 || phanTram > 100) throw new NumberFormatException();
        } catch (NumberFormatException e) { warn("Phần trăm giảm phải từ 1 đến 100!"); return; }

        int ma = Integer.parseInt(txtMaKM.getText());
        dtokhuyenmai km = new dtokhuyenmai(ma, ten,
                dateNgayBD.getDate(), dateNgayHetHan.getDate(), soLuong, phanTram);

        boolean ok;
        if (mode.equals("Thêm khuyến mãi")) {
            for (dtokhuyenmai t : buskm.getlist()) {
                if (t.getTenKhuyenMai().equals(ten)) { warn("Tên đã tồn tại!"); return; }
            }
            ok = buskm.addKhuyenMai(km);
            if (ok) {
                info("Thêm thành công!");
                txtMaKM.setText((buskm.getMaxMaKhuyenMai() + 1) + "");
                txtTenKM.setText(""); txtSoLuong.setText("");
                txtPhanTramGiam.setText("");
                dateNgayBD.setDate(null); dateNgayHetHan.setDate(null);
            }
        } else {
            for (dtokhuyenmai t : buskm.getlist()) {
                if (t.getTenKhuyenMai().equals(ten) && t.getMaKhuyenMai() != ma) {
                    warn("Tên đã tồn tại!"); return;
                }
            }
            ok = buskm.Update(km);
            if (ok) { info("Sửa thành công!"); dlg.dispose(); }
        }
        if (ok) loadKM();
    }

    // ─────────────────────────────────────────────────────────────
    //  ƯU ĐÃI CARD
    // ─────────────────────────────────────────────────────────────
    private JPanel buildUDCard() {
        JPanel card = new JPanel(new MigLayout(
                "fill, insets 0, wrap", "[fill]", "[shrink 0][1!][fill,grow]"));
        card.putClientProperty(FlatClientProperties.STYLE,
                "background: $Panel.background; border: 1,1,1,1,$Component.borderColor,,12;");

        card.add(buildUDToolbar(), "growx, gapx 16 16, gapy 12 10");

        JSeparator sep = new JSeparator();
        sep.putClientProperty(FlatClientProperties.STYLE, "foreground: $Table.gridColor;");
        card.add(sep, "growx, height 1!");

        card.add(buildUDTable(), "grow");
        return card;
    }

   private JPanel buildUDToolbar() {
    JPanel p = new JPanel(new MigLayout(
            "insets 8 16 8 16, fillx, wrap",
            "[grow][pref][pref][pref][pref][pref]",
            "[center]"));
    p.putClientProperty(FlatClientProperties.STYLE, "background: null;");

    JTextField txtSearch = new JTextField();
    txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm mốc ưu đãi...");
    txtSearch.putClientProperty(FlatClientProperties.STYLE,
            "arc: 8; focusedBorderColor: #6366f1; borderWidth: 1;");

    JButton btnSearch = accentBtn("Tìm", ACCENT);
    btnSearch.addActionListener(e -> searchUD(txtSearch.getText().trim()));

    JButton btnReset = outlineBtn("↺ Tải lại");
    btnReset.addActionListener(e -> { txtSearch.setText(""); loadUD(); });

    JButton btnAdd  = accentBtn("+ Thêm", SUCCESS);
    btnAdd.addActionListener(e -> openUDDialog("Thêm ưu đãi", -1));

    JButton btnEdit = outlineBtn("✎ Sửa");
    btnEdit.addActionListener(e -> editUD());

    JButton btnDel  = new JButton("✕ Xóa");
    btnDel.putClientProperty(FlatClientProperties.STYLE,
            "background: " + hex(DANGER) + "; foreground: #ffffff; arc: 8; borderWidth: 0;");
    btnDel.setFocusPainted(false);
    btnDel.addActionListener(e -> deleteUD());

    txtSearch.addKeyListener(new KeyAdapter() {
        @Override public void keyReleased(KeyEvent e) {
            searchUD(txtSearch.getText().trim());
        }
    });

    p.add(txtSearch, "growx, height 32!");
    p.add(btnSearch, "height 32!");
    p.add(btnReset,  "height 32!");
    p.add(btnAdd,    "height 32!");
    p.add(btnEdit,   "height 32!");
    p.add(btnDel,    "height 32!");

    return p;
}

    private JScrollPane buildUDTable() {
        modelUD = new DefaultTableModel(colsUD, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableUD = styledTable(modelUD);
        int[] w = {100, 180, 150};
        for (int i = 0; i < w.length; i++)
            tableUD.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        DefaultTableCellRenderer center = centerRenderer();
        for (int i = 0; i < 3; i++)
            tableUD.getColumnModel().getColumn(i).setCellRenderer(center);

        loadUD();
        return scrollOf(tableUD);
    }

    private void loadUD() {
        modelUD.setRowCount(0);
        for (dtouudai ud : busud.getList())
            modelUD.addRow(new Object[]{ud.getMaUuDai(), ud.getMocUuDai(), ud.getTiLeGiam()});
    }

    private void searchUD(String kw) {
        modelUD.setRowCount(0);
        ArrayList<dtouudai> list = kw.isEmpty() ? busud.getList() : busud.getListByCondition(kw);
        for (dtouudai ud : list)
            modelUD.addRow(new Object[]{ud.getMaUuDai(), ud.getMocUuDai(), ud.getTiLeGiam()});
    }

    private void editUD() {
        int row = tableUD.getSelectedRow();
        if (row < 0) { warn("Bạn chưa chọn ưu đãi để sửa!"); return; }
        openUDDialog("Sửa ưu đãi", row);
    }

    private void deleteUD() {
        int row = tableUD.getSelectedRow();
        if (row < 0) { warn("Bạn chưa chọn ưu đãi để xóa!"); return; }
        if (confirm("Xác nhận xóa ưu đãi này?")) {
            int ma = (int) tableUD.getValueAt(row, 0);
            if (busud.Deleted(ma)) { loadUD(); info("Xóa thành công!"); }
        }
    }

    private void openUDDialog(String title, int selectedRow) {
        JDialog dlg = new JDialog((JFrame) null, title, true);
        dlg.setContentPane(buildUDForm(title, selectedRow, dlg));
        dlg.pack();
        dlg.setLocationRelativeTo(tableUD);
        dlg.setVisible(true);
    }

    private JPanel buildUDForm(String mode, int selectedRow, JDialog dlg) {
        JPanel p = new JPanel(new MigLayout(
                "fillx, wrap, insets 20 24 20 24, width 380", "[fill]", "[]8[]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: $Panel.background;");

        JLabel lbl = new JLabel(mode);
        lbl.putClientProperty(FlatClientProperties.STYLE, "font: bold +3;");
        p.add(lbl, "gapy 0 12");

        txtMaUuDai  = disabledField(String.valueOf(busud.getMaxMaUuDai() + 1));
        txtMocUuDai = new JTextField();
        txtTiLeGiam = new JTextField();
        applyFieldStyle(txtMocUuDai, txtTiLeGiam);

        p.add(fieldLbl("Mã ưu đãi"));  p.add(txtMaUuDai,  "height 34!");
        p.add(fieldLbl("Mốc ưu đãi")); p.add(txtMocUuDai, "height 34!");
        p.add(fieldLbl("Tỉ lệ giảm")); p.add(txtTiLeGiam, "height 34!");

        if (selectedRow >= 0) {
            txtMaUuDai.setText(tableUD.getValueAt(selectedRow, 0).toString());
            txtMocUuDai.setText(tableUD.getValueAt(selectedRow, 1).toString());
            txtTiLeGiam.setText(tableUD.getValueAt(selectedRow, 2).toString());
        }

        JSeparator sep = new JSeparator();
        sep.putClientProperty(FlatClientProperties.STYLE, "foreground: $Table.gridColor;");
        p.add(sep, "growx, height 1!, gapy 8 8");

        JPanel btns = new JPanel(new MigLayout("insets 0", "push[]8[]", "[]"));
        btns.putClientProperty(FlatClientProperties.STYLE, "background: null;");
        JButton btnOK  = accentBtn("Xác nhận", ACCENT);
        JButton btnCan = outlineBtn("Hủy bỏ");
        btnOK.addActionListener(e  -> submitUDForm(mode, dlg));
        btnCan.addActionListener(e -> { if (confirm("Hủy bỏ thao tác?")) dlg.dispose(); });
        btns.add(btnOK,  "height 34!, width 110!");
        btns.add(btnCan, "height 34!, width 90!");
        p.add(btns, "growx");
        return p;
    }

    private void submitUDForm(String mode, JDialog dlg) {
        int ma, moc, tiLe;
        try {
            ma = Integer.parseInt(txtMaUuDai.getText().trim());
            if (ma <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) { warn("Mã ưu đãi phải là số nguyên dương!"); return; }
        try {
            moc = Integer.parseInt(txtMocUuDai.getText().trim());
            if (moc < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) { warn("Mốc ưu đãi phải là số nguyên không âm!"); return; }
        try {
            tiLe = Integer.parseInt(txtTiLeGiam.getText().trim());
            if (tiLe <= 0 || tiLe > 100) throw new NumberFormatException();
        } catch (NumberFormatException e) { warn("Tỉ lệ giảm phải từ 1 đến 100!"); return; }

        dtouudai ud = new dtouudai(ma, moc, tiLe, 0);
        boolean ok;
        if (mode.equals("Thêm ưu đãi")) {
            ok = busud.addUuDai(ud);
            if (ok) {
                info("Thêm thành công!");
                txtMaUuDai.setText((busud.getMaxMaUuDai() + 1) + "");
                txtMocUuDai.setText(""); txtTiLeGiam.setText("");
            }
        } else {
            ok = busud.Update(ud);
            if (ok) { info("Sửa thành công!"); dlg.dispose(); }
        }
        if (ok) loadUD();
    }

    // ─────────────────────────────────────────────────────────────
    //  UI HELPERS
    // ─────────────────────────────────────────────────────────────
    private JTable styledTable(DefaultTableModel mdl) {
        JTable tbl = new JTable(mdl);
        tbl.setRowHeight(36);
        tbl.setShowGrid(false);
        tbl.setFillsViewportHeight(true);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbl.putClientProperty(FlatClientProperties.STYLE,
                "rowHeight:36; showHorizontalLines:true; intercellSpacing:0,1;" +
                "cellFocusColor:$TableHeader.hoverBackground;" +
                "selectionBackground:$TableHeader.hoverBackground;" +
                "selectionInactiveBackground:$TableHeader.hoverBackground;" +
                "selectionForeground:$Table.foreground;");
        tbl.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(tbl) {
            protected int getAlignment() { return SwingConstants.CENTER; }
        });
        tbl.getTableHeader().putClientProperty(FlatClientProperties.STYLE,
                "height:34; hoverBackground:null; pressedBackground:null;" +
                "separatorColor:$TableHeader.background; font:bold +1;");
        tbl.getTableHeader().setReorderingAllowed(false);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return tbl;
    }

    private JScrollPane scrollOf(JTable tbl) {
        JScrollPane sp = new JScrollPane(tbl);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
                "trackArc:$ScrollBar.thumbArc; thumbInsets:0,2,0,2; width:7;");
        sp.getHorizontalScrollBar().putClientProperty(FlatClientProperties.STYLE,
                "trackArc:$ScrollBar.thumbArc; thumbInsets:0,2,0,2; width:7;");
        return sp;
    }

    private DefaultTableCellRenderer centerRenderer() {
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(SwingConstants.CENTER);
        return r;
    }

    private JTextField disabledField(String val) {
        JTextField tf = new JTextField(val);
        tf.setEnabled(false);
        tf.putClientProperty(FlatClientProperties.STYLE,
                "arc: 8; borderWidth: 1;");
        return tf;
    }

    private void applyFieldStyle(JTextField... fields) {
        for (JTextField tf : fields)
            tf.putClientProperty(FlatClientProperties.STYLE,
                    "arc: 8; borderWidth: 1; focusedBorderColor: #6366f1;");
    }

    private JDateChooser makeDateChooser() {
        JDateChooser dc = new JDateChooser();
        dc.setDateFormatString("yyyy-MM-dd");
        ((JTextField) dc.getDateEditor().getUiComponent()).setEditable(false);
        dc.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
        return dc;
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