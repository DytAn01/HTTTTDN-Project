package gui.form;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.toedter.calendar.JDateChooser;
import net.miginfocom.swing.MigLayout;

import bus.*;
import dto.*;
import gui.modal.ModalDialog;
import gui.modal.component.SimpleModalBorder;
import gui.modal.option.Location;
import gui.modal.option.Option;
import gui.table.TableHeaderAlignment;

public class formphieunhap extends JPanel {

    // ── Palette ───────────────────────────────────────────────────
    private static final Color ACCENT      = new Color( 99, 102, 241);
    private static final Color ACCENT_SOFT = new Color(238, 242, 255);
    private static final Color SUCCESS     = new Color( 34, 197,  94);
    private static final Color DANGER      = new Color(239,  68,  68);
    private static final Color WARNING     = new Color(234, 179,   8);
    private static final Color TEXT_MUTED  = new Color(100, 116, 139);

    // ── BUS ───────────────────────────────────────────────────────
    private final busphieunhap   buspn    = new busphieunhap();
    private final busctphieunhap busctpn  = new busctphieunhap();
    private final bussanpham     bussp    = new bussanpham();
    private final busnhacungcap  busncc   = new busnhacungcap();

    // ── State ─────────────────────────────────────────────────────
    private final int manv;
    private int selectedIndex = -1;
    private ArrayList<dtoctphieunhap> nhapHangList = new ArrayList<>();
    private AtomicBoolean dialogShown = new AtomicBoolean(false);

    // ── Nhập hàng tab fields ──────────────────────────────────────
    private JTextField   txtNCCid, txtSL, txtGiaNhap, txtGiaBan;
    private JTextField   txtSPid, txtTotal, txtLoiNhuan;
    private JTextArea    txtNote;
    private JComboBox<String> cbNCCname;
    private JDateChooser dateChooser;

    // ── Tables & models ───────────────────────────────────────────
    private JTable            generalTable;
    private DefaultTableModel modelNhapHang, additionalModel;
    private JButton           btnDetail;

    // ─────────────────────────────────────────────────────────────
    public formphieunhap(int manv) {
        this.manv = manv;
        busctphieunhap.updateEXP();
        buildUI();
    }

    // ─────────────────────────────────────────────────────────────
    //  BUILD
    // ─────────────────────────────────────────────────────────────
    private void buildUI() {
        setLayout(new MigLayout("fill, insets 16 20 16 20, wrap", "[fill]", "[shrink 0][fill,grow]"));
        add(buildHeader(), "growx");
        add(buildTabs(),   "grow");
    }

    // ── Page header ───────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new MigLayout("fillx, insets 0 0 12 0", "[fill]push[][]", "[]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        JLabel title = new JLabel("Quản lý Phiếu nhập");
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +6;");
        p.add(title);

        buspn.getlist();
        JLabel badge = new JLabel(buspn.dspn.size() + " phiếu nhập");
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

    // ── Tabs ─────────────────────────────────────────────────────
    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.putClientProperty(FlatClientProperties.STYLE, "tabType: card;");
        tabs.addTab("📦  Nhập hàng",         wrapTab(buildNhapHangTab()));
        tabs.addTab("📋  Danh sách phiếu nhập", wrapTab(buildGeneralTableTab()));
        return tabs;
    }

    private JPanel wrapTab(Component c) {
        JPanel p = new JPanel(new MigLayout("fill, insets 10 0 10 0", "[fill]", "[fill]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");
        p.add(c);
        return p;
    }

    // ─────────────────────────────────────────────────────────────
    //  TAB 1: NHẬP HÀNG
    // ─────────────────────────────────────────────────────────────
    private JPanel buildNhapHangTab() {
        JPanel p = new JPanel(new MigLayout(
                "fill, wrap, insets 0", "[fill]", "[shrink 0][fill,grow][shrink 0]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        p.add(buildNhapHangActionBar(), "growx");
        p.add(buildNhapHangMiddle(),    "grow");
        p.add(buildNhapHangTableCard(), "growx");
        return p;
    }

    // Action bar: title + confirm + clear
    private JPanel buildNhapHangActionBar() {
        JPanel p = new JPanel(new MigLayout("fillx, insets 0 0 8 0", "[fill]push[][]", "[]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        JLabel lbl = new JLabel("Tạo phiếu nhập hàng");
        lbl.putClientProperty(FlatClientProperties.STYLE, "font: bold +2;");
        p.add(lbl);

        JButton btnClear = outlineBtn("✕  Xóa tất cả");
        btnClear.addActionListener(e -> {
            if (confirm("Xác nhận xóa toàn bộ thông tin phiếu nhập?")) reset();
        });
        p.add(btnClear, "height 32!");

        JButton btnConfirm = accentBtn("✔  Xác nhận nhập hàng", SUCCESS);
        btnConfirm.addActionListener(e -> confirmNhapHang());
        p.add(btnConfirm, "height 32!");
        return p;
    }

    // Middle: NCC info (left) + product suggestion table (right)
    private JPanel buildNhapHangMiddle() {
        JPanel p = new JPanel(new MigLayout("fill, gap 16, insets 0", "[320!][fill,grow]", "[fill]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");
        p.add(buildNCCCard(),      "grow");
        p.add(buildSuggestionCard(),"grow");
        return p;
    }

    // NCC info card
    private JPanel buildNCCCard() {
        JPanel card = card("Thông tin nhà cung cấp");
        card.setLayout(new MigLayout("fillx, wrap, insets 12 14 12 14", "[fill]", "[]8[]8[]8[]"));

        txtNCCid = styledField("Nhập mã NCC...");
        txtNCCid.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) { if (!Character.isDigit(e.getKeyChar())) e.consume(); }
        });
        txtNCCid.addActionListener(e -> onNCCidEnter());

        cbNCCname = new JComboBox<>();
        cbNCCname.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
        cbNCCname.addItem("");
        for (dtonhacungcap ncc : busncc.list()) cbNCCname.addItem(ncc.getTenNhaCungCap());
        cbNCCname.addActionListener(e -> onNCCnameChanged());

        txtTotal = new JTextField("0");
        txtTotal.setEditable(false);
        txtTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        txtTotal.putClientProperty(FlatClientProperties.STYLE,
                "arc: 8; borderWidth: 1; background: $Panel.background; font: bold;");

        txtNote = new JTextArea(3, 0);
        txtNote.setWrapStyleWord(true);
        txtNote.setLineWrap(true);
        JScrollPane noteScroll = new JScrollPane(txtNote);
        noteScroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true));

        card.add(fieldLbl("Mã nhà cung cấp"));
        card.add(txtNCCid, "height 34!");
        card.add(fieldLbl("Tên nhà cung cấp"));
        card.add(cbNCCname, "height 34!");
        card.add(fieldLbl("Ghi chú"));
        card.add(noteScroll, "height 80!");
        card.add(fieldLbl("Tổng tiền nhập"));
        card.add(txtTotal, "height 36!");
        return card;
    }

    // Suggestion table card
    private JPanel buildSuggestionCard() {
        JPanel card = card("Sản phẩm cần nhập");
        card.setLayout(new MigLayout("fill, insets 0", "[fill]", "[shrink 0][1!][fill,grow]"));

        additionalModel = new DefaultTableModel(
                new Object[]{"Mã NCC", "Mã SP", "Tên", "Ngày hết hạn", "SL tồn kho"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = styledTable(additionalModel);
        JScrollPane scroll = scrollOf(tbl);

        JSeparator sep = new JSeparator();
        sep.putClientProperty(FlatClientProperties.STYLE, "foreground: $Table.gridColor;");

        card.add(new JLabel(), "height 0!");
        card.add(sep, "growx, height 1!");
        card.add(scroll, "grow");

        reloadAdditionalTable(null);
        return card;
    }

    // Bottom: nhap hang item list
    private JPanel buildNhapHangTableCard() {
        JPanel card = card("Danh sách sản phẩm nhập");
        card.setLayout(new MigLayout("fill, insets 0", "[fill]", "[shrink 0][1!][fill,grow]"));

        // Toolbar
        JPanel toolbar = new JPanel(new MigLayout("insets 8 14 8 14", "push[][][]", "[center]"));
        toolbar.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        JButton btnAdd    = accentBtn("+ Thêm SP", ACCENT);
        JButton btnEdit   = outlineBtn("✎ Sửa");
        JButton btnDelete = new JButton("✕ Xóa");
        btnDelete.putClientProperty(FlatClientProperties.STYLE,
                "background: " + hex(DANGER) + "; foreground:#ffffff; arc:8; borderWidth:0;");
        btnDelete.setFocusPainted(false);

        btnAdd.addActionListener(e -> {
            if (txtNCCid.getText().trim().isEmpty()) {
                warn("Vui lòng chọn nhà cung cấp trước!"); return;
            }
            showAddModal();
        });
        btnEdit.addActionListener(e -> {
            if (selectedIndex < 0) { warn("Vui lòng chọn sản phẩm để sửa!"); return; }
            showEditModal(nhapHangList.get(selectedIndex));
        });
        btnDelete.addActionListener(e -> deleteNhapHangRow());

        toolbar.add(btnAdd,    "height 32!");
        toolbar.add(btnEdit,   "height 32!");
        toolbar.add(btnDelete, "height 32!");

        modelNhapHang = new DefaultTableModel(
                new Object[]{"#", "Mã SP", "Tên sản phẩm", "Giá nhập", "Số lượng", "Ngày HH", "Giá bán"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = styledTable(modelNhapHang);
        tbl.getTableHeader().putClientProperty(FlatClientProperties.STYLE,
                "height:34; font:bold +1;");
        tbl.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                selectedIndex = tbl.getSelectedRow();
            }
        });

        JSeparator sep = new JSeparator();
        sep.putClientProperty(FlatClientProperties.STYLE, "foreground: $Table.gridColor;");

        card.add(toolbar, "growx");
        card.add(sep, "growx, height 1!");
        card.add(scrollOf(tbl), "grow, height 180!");
        return card;
    }

    // ─────────────────────────────────────────────────────────────
    //  TAB 2: DANH SÁCH PHIẾU NHẬP
    // ─────────────────────────────────────────────────────────────
    private JPanel buildGeneralTableTab() {
        JPanel card = new JPanel(new MigLayout(
                "fill, insets 0, wrap", "[fill]", "[shrink 0][1!][fill,grow]"));
        card.putClientProperty(FlatClientProperties.STYLE,
                "background: $Panel.background; border: 1,1,1,1,$Component.borderColor,,12;");

        card.add(buildGeneralToolbar(), "growx, gapx 16 16, gapy 12 10");

        JSeparator sep = new JSeparator();
        sep.putClientProperty(FlatClientProperties.STYLE, "foreground: $Table.gridColor;");
        card.add(sep, "growx, height 1!");
        card.add(buildGeneralTable(), "grow");
        return card;
    }

    private JPanel buildGeneralToolbar() {
        JPanel p = new JPanel(new MigLayout(
                "insets 0", "[][8!][][8!][][8!][]", "[center]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        JTextField txtSearch = new JTextField(22);
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
                "Tìm tên NV, tên NCC, mã phiếu...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE,
                "arc: 8; focusedBorderColor: #6366f1; borderWidth: 1;");
        p.add(txtSearch, "height 32!");

        p.add(new JSeparator(JSeparator.VERTICAL), "growy, width 1!");

        JButton btnSearch = accentBtn("Tìm", ACCENT);
        btnSearch.addActionListener(e -> searching(txtSearch.getText().trim()));
        p.add(btnSearch, "height 32!");

        JButton btnReload = outlineBtn("↺ Tải lại");
        btnReload.addActionListener(e -> { txtSearch.setText(""); reloadGeneralTable(); });
        p.add(btnReload, "height 32!");

        p.add(new JSeparator(JSeparator.VERTICAL), "growy, width 1!");

        btnDetail = accentBtn("📋  Xem chi tiết", new Color(79, 70, 229));
        btnDetail.setEnabled(false);
        btnDetail.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Hãy chọn một phiếu nhập để xem chi tiết"));
        p.add(btnDetail, "height 32!");

        txtSearch.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                searching(txtSearch.getText().trim());
            }
        });

        return p;
    }

    private JScrollPane buildGeneralTable() {
        Object[] cols = {"Mã phiếu", "Ngày nhập", "Nhà cung cấp", "Tổng tiền", "Tên NV", "Ghi chú"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        generalTable = styledTable(model);
        generalTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        int[] widths = {70, 140, 180, 120, 160, 180};
        for (int i = 0; i < widths.length; i++)
            generalTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i : new int[]{0, 1, 3})
            generalTable.getColumnModel().getColumn(i).setCellRenderer(center);

        buspn.getlist();
        for (dtophieunhap pn : buspn.dspn) model.addRow(pn.toTableRow());

        generalTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { onGeneralTableClick(); }
        });

        return scrollOf(generalTable);
    }

    // ─────────────────────────────────────────────────────────────
    //  ACTIONS – NCC
    // ─────────────────────────────────────────────────────────────
    private void onNCCidEnter() {
        String id = txtNCCid.getText().trim();
        if (id.isEmpty()) return;
        dtonhacungcap ncc = busncc.getById(Integer.parseInt(id));
        if (ncc == null) { warn("Không tìm thấy mã Nhà cung cấp!"); return; }
        cbNCCname.setSelectedItem(ncc.getTenNhaCungCap());
        reloadAdditionalTable(id);
    }

    private void onNCCnameChanged() {
        Object sel = cbNCCname.getSelectedItem();
        if (sel == null || sel.toString().isEmpty()) return;
        dtonhacungcap ncc = busncc.getByName(sel.toString());
        if (ncc == null) return;
        txtNCCid.setText(String.valueOf(ncc.getMaNhaCungCap()));
        reloadAdditionalTable(String.valueOf(ncc.getMaNhaCungCap()));
    }

    // ── Nhập hàng confirm / delete / reset ───────────────────────
    private void confirmNhapHang() {
        if (nhapHangList.isEmpty() || txtNCCid.getText().trim().isEmpty()) {
            warn("Vui lòng chọn nhà cung cấp và thêm sản phẩm!"); return;
        }
        dtophieunhap pn = new dtophieunhap(
                buspn.maxID() + 1,
                Timestamp.valueOf(LocalDateTime.now().withNano(
                        (LocalDateTime.now().getNano() / 1_000_000) * 1_000_000)),
                doubleVal(txtTotal), intVal(txtNCCid), manv, txtNote.getText());
        buspn.create(pn);
        for (dtoctphieunhap ct : nhapHangList) busctpn.create(ct);
        info("Nhập hàng thành công!");
        reset();
        reloadGeneralTable();
    }

    private void deleteNhapHangRow() {
        if (selectedIndex < 0) { warn("Vui lòng chọn sản phẩm để xóa!"); return; }
        if (!confirm("Xác nhận xóa sản phẩm này?")) return;
        nhapHangList.remove(selectedIndex);
        reloadNhapHangTable();
        if (nhapHangList.isEmpty()) { txtNCCid.setEditable(true); cbNCCname.setEnabled(true); }
    }

    private void reset() {
        txtNCCid.setText(""); cbNCCname.setSelectedItem("");
        txtNote.setText(""); txtTotal.setText("0");
        nhapHangList = new ArrayList<>();
        reloadNhapHangTable();
        txtNCCid.setEditable(true); cbNCCname.setEnabled(true);
        reloadAdditionalTable(null);
        selectedIndex = -1;
    }

    private void reloadNhapHangTable() {
        modelNhapHang.setRowCount(0);
        int i = 1; double total = 0;
        for (dtoctphieunhap ct : nhapHangList) {
            modelNhapHang.addRow(ct.toTableRow(i++));
            total += ct.getGiaNhap() * ct.getSoLuong();
        }
        txtTotal.setText(String.valueOf(total));
        selectedIndex = -1;
    }

    private void reloadAdditionalTable(String maNCC) {
        additionalModel.setRowCount(0);
        if (maNCC == null) {
            for (dtosanpham sp : bussp.needToFillList())
                additionalModel.addRow(sp.toAdditionalTableRow());
            busctpn.needToFillList();
            for (dtoctphieunhap ct : busctpn.dsctpn)
                additionalModel.addRow(ct.toAdditionalTableRow());
        } else {
            for (dtosanpham sp : bussp.needToFillList(Integer.valueOf(maNCC)))
                additionalModel.addRow(sp.toAdditionalTableRow());
            busctpn.needToFillList(Integer.valueOf(maNCC));
            for (dtoctphieunhap ct : busctpn.dsctpn)
                additionalModel.addRow(ct.toAdditionalTableRow());
        }
    }

    // ── General table actions ─────────────────────────────────────
    public void reloadGeneralTable() {
        generalTable.setRowSorter(null);
        DefaultTableModel m = (DefaultTableModel) generalTable.getModel();
        m.setRowCount(0);
        buspn.getlist();
        for (dtophieunhap pn : buspn.dspn) m.addRow(pn.toTableRow());
    }

    private void searching(String kw) {
        if (kw.isEmpty()) { reloadGeneralTable(); return; }
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(generalTable.getModel());
        generalTable.setRowSorter(sorter);
        int[] cols = kw.chars().allMatch(Character::isDigit) ? new int[]{0, 2} : new int[]{2, 4};
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + kw, cols));
        if (generalTable.getRowCount() == 0)
            JOptionPane.showMessageDialog(this, "Không có kết quả phù hợp.");
    }

    private void onGeneralTableClick() {
        int row = generalTable.getSelectedRow();
        if (row < 0) return;
        int id = (int) generalTable.getValueAt(row, 0);
        for (ActionListener al : btnDetail.getActionListeners()) btnDetail.removeActionListener(al);
        btnDetail.setEnabled(true);
        btnDetail.addActionListener(e -> showDetailDialog(id));
    }

    // ─────────────────────────────────────────────────────────────
    //  MODALS
    // ─────────────────────────────────────────────────────────────
    private void showAddModal() {
        Option opt = ModalDialog.createOption();
        opt.getLayoutOption().setSize(-1, 1f)
                .setLocation(Location.TRAILING, Location.TOP).setAnimateDistance(0.7f, 0);
        ModalDialog.showModal(this,
                new SimpleModalBorder(buildProductInputForm(null), "Nhập thông tin sản phẩm",
                        SimpleModalBorder.YES_NO_OPTION, (ctrl, action) -> {
                    if (action == SimpleModalBorder.YES_OPTION) {
                        if (checkInputEmpty()) return;
                        dtoctphieunhap ct = buildCT();
                        if (ct == null) return;
                        nhapHangList.add(ct);
                        reloadNhapHangTable();
                        txtNCCid.setEditable(false);
                        cbNCCname.setEnabled(false);
                    }
                    ctrl.close();
                }), opt);
    }

    private void showEditModal(dtoctphieunhap existing) {
        Option opt = ModalDialog.createOption();
        opt.getLayoutOption().setSize(-1, 1f)
                .setLocation(Location.TRAILING, Location.TOP).setAnimateDistance(0.7f, 0);
        ModalDialog.showModal(this,
                new SimpleModalBorder(buildProductInputForm(existing), "Chỉnh sửa sản phẩm",
                        SimpleModalBorder.YES_NO_OPTION, (ctrl, action) -> {
                    if (action == SimpleModalBorder.YES_OPTION) {
                        dtoctphieunhap ct = buildCT();
                        if (ct == null) return;
                        nhapHangList.set(selectedIndex, ct);
                        reloadNhapHangTable();
                    }
                    ctrl.close();
                }), opt);
    }

    private dtoctphieunhap buildCT() {
        try {
            LocalDate ld = dateChooser.getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            return new dtoctphieunhap(
                    busctpn.maxID() + 1, intVal(txtSL),
                    doubleVal(txtGiaNhap), buspn.maxID() + 1,
                    intVal(txtSPid), java.sql.Date.valueOf(ld),
                    intVal(txtSL), "", doubleVal(txtGiaBan));
        } catch (Exception ex) {
            Logger.getLogger(formphieunhap.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    // ── Product input form ────────────────────────────────────────
    private JPanel buildProductInputForm(dtoctphieunhap pn) {
        JPanel p = new JPanel(new MigLayout(
                "fillx, wrap, insets 16 20 16 20, width 440", "[fill]", "[]8[]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: $Panel.background;");

        ArrayList<dtosanpham> products = bussp.listByNhaCungCapID(
                Integer.parseInt(txtNCCid.getText().trim().isEmpty() ? "0" : txtNCCid.getText().trim()));

        txtSPid = styledField("Mã SP");
        JComboBox<String> cboSPname = new JComboBox<>();
        cboSPname.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
        cboSPname.addItem("");
        for (dtosanpham sp : products) cboSPname.addItem(sp.getTenSanPham());

        JLabel imgLabel = new JLabel();
        imgLabel.setPreferredSize(new Dimension(200, 180));
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true));

        // Sync
        txtSPid.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                String id = txtSPid.getText().trim();
                for (dtosanpham sp : products) {
                    if (String.valueOf(sp.getMaSanPham()).equals(id)) {
                        cboSPname.setSelectedItem(sp.getTenSanPham());
                        loadImg(imgLabel, sp.getImg()); return;
                    }
                }
            }
            @Override public void keyTyped(KeyEvent e) {
                if (!Character.isDigit(e.getKeyChar())) e.consume();
            }
        });
        cboSPname.addActionListener(e -> {
            String sel = (String) cboSPname.getSelectedItem();
            if (sel == null || sel.isEmpty()) return;
            dtosanpham sp = bussp.getByName(sel);
            if (sp == null) return;
            txtSPid.setText(String.valueOf(sp.getMaSanPham()));
            loadImg(imgLabel, sp.getImg());
        });

        // Quantity spinner
        txtSL = new JTextField("1");
        txtSL.setHorizontalAlignment(SwingConstants.CENTER);
        JButton btnPlus  = new JButton("+");
        JButton btnMinus = new JButton("−");
        btnPlus.putClientProperty(FlatClientProperties.STYLE, "arc:6; borderWidth:1;");
        btnMinus.putClientProperty(FlatClientProperties.STYLE, "arc:6; borderWidth:1;");
        btnPlus.addActionListener(e  -> adjSL(1));
        btnMinus.addActionListener(e -> adjSL(-1));
        JPanel slPanel = new JPanel(new MigLayout("insets 0", "[30!][fill,grow][30!]", "[]"));
        slPanel.putClientProperty(FlatClientProperties.STYLE, "background: null;");
        slPanel.add(btnMinus, "height 34!");
        slPanel.add(txtSL,    "height 34!");
        slPanel.add(btnPlus,  "height 34!");

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        ((JTextField) dateChooser.getDateEditor().getUiComponent()).setEditable(false);
        dateChooser.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
        dateChooser.getDateEditor().addPropertyChangeListener(ev -> {
            if ("date".equals(ev.getPropertyName())) {
                Date d = dateChooser.getDate();
                if (d != null && d.before(new Date())) {
                    warn("Ngày hết hạn không được là ngày trong quá khứ!");
                    dateChooser.setDate(null);
                }
            }
        });

        txtGiaNhap = styledField("Giá nhập");
        txtLoiNhuan = styledField("Lợi nhuận");
        txtGiaBan = styledField("Giá bán");
        txtGiaBan.setEditable(false);
        txtGiaBan.putClientProperty(FlatClientProperties.STYLE,
                "arc: 8; borderWidth: 1; background: $Panel.background;");

        JComboBox<String> cboUnit = new JComboBox<>(new String[]{"Đồng", "%"});
        cboUnit.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");

        KeyAdapter recalc = new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { calcGiaBan(cboUnit); }
        };
        txtGiaNhap.addKeyListener(recalc);
        txtLoiNhuan.addKeyListener(recalc);
        cboUnit.addActionListener(e -> calcGiaBan(cboUnit));

        // Pre-fill when editing
        if (pn != null) {
            txtSPid.setText(String.valueOf(pn.getMaSanPham()));
            txtSL.setText(String.valueOf(pn.getSoLuong()));
            txtGiaNhap.setText(String.valueOf(pn.getGiaNhap()));
            txtGiaBan.setText(String.valueOf(pn.getGiaBan()));
            txtLoiNhuan.setText(String.valueOf(pn.getGiaBan() - pn.getGiaNhap()));
            dateChooser.setDate(pn.getNgayhethan());
            dtosanpham sp = bussp.getById(pn.getMaSanPham());
            if (sp != null) { cboSPname.setSelectedItem(sp.getTenSanPham()); loadImg(imgLabel, sp.getImg()); }
        }

        p.add(fieldLbl("Mã SP"));    p.add(txtSPid,   "height 34!");
        p.add(fieldLbl("Tên SP"));   p.add(cboSPname, "height 34!");
        p.add(fieldLbl("Số lượng")); p.add(slPanel,   "growx");

        JPanel dateRow = new JPanel(new MigLayout("insets 0", "[fill,grow]", "[]"));
        dateRow.putClientProperty(FlatClientProperties.STYLE, "background: null;");
        dateRow.add(dateChooser, "height 34!, growx");
        p.add(fieldLbl("Ngày hết hạn")); p.add(dateRow, "growx");

        p.add(fieldLbl("Giá nhập"));    p.add(txtGiaNhap,  "height 34!");

        JPanel profitRow = new JPanel(new MigLayout("insets 0", "[fill,grow][8!][80!]", "[]"));
        profitRow.putClientProperty(FlatClientProperties.STYLE, "background: null;");
        profitRow.add(txtLoiNhuan, "height 34!");
        profitRow.add(cboUnit,     "height 34!");
        p.add(fieldLbl("Lợi nhuận")); p.add(profitRow, "growx");

        p.add(fieldLbl("Giá bán")); p.add(txtGiaBan, "height 34!");

        p.add(fieldLbl("Hình ảnh"));
        p.add(imgLabel, "height 180!, growx");

        return p;
    }

    private void calcGiaBan(JComboBox<String> unit) {
        try {
            double gn = txtGiaNhap.getText().isEmpty() ? 0 : Double.parseDouble(txtGiaNhap.getText());
            double ln = txtLoiNhuan.getText().isEmpty() ? 0 : Double.parseDouble(txtLoiNhuan.getText());
            String u = (String) unit.getSelectedItem();
            txtGiaBan.setText(String.valueOf("Đồng".equals(u) ? gn + ln : gn * (1 + ln / 100)));
        } catch (NumberFormatException ignored) {}
    }

    private void adjSL(int delta) {
        try {
            int v = Integer.parseInt(txtSL.getText()) + delta;
            if (v >= 1) txtSL.setText(String.valueOf(v));
        } catch (NumberFormatException e) { txtSL.setText("1"); }
    }

    private void loadImg(JLabel lbl, String path) {
        if (path == null || path.isEmpty()) { lbl.setIcon(null); return; }
        ImageIcon icon = new ImageIcon(System.getProperty("user.dir") + "/src/source/image/sanpham/" + path);
        lbl.setIcon(new ImageIcon(icon.getImage().getScaledInstance(200, 180, Image.SCALE_SMOOTH)));
    }

    private boolean checkInputEmpty() {
        if (txtSPid.getText().trim().isEmpty()) { warn("ID sản phẩm không hợp lệ!"); return true; }
        if (txtSL.getText().trim().isEmpty() || txtSL.getText().equals("0")) { warn("Số lượng không hợp lệ!"); return true; }
        if (txtGiaBan.getText().trim().isEmpty() || txtGiaBan.getText().equals("0.0")) { warn("Giá bán không hợp lệ!"); return true; }
        if (dateChooser.getDate() == null) { warn("Vui lòng chọn ngày hết hạn!"); return true; }
        return false;
    }

    // ── Detail dialog ─────────────────────────────────────────────
    private void showDetailDialog(int id) {
        JDialog dlg = new JDialog((JFrame) null, "Chi tiết phiếu nhập #" + id, true);
        dlg.setContentPane(buildDetailPanel(id));
        dlg.pack();
        dlg.setLocationRelativeTo(generalTable);
        dlg.setVisible(true);
    }

    private JPanel buildDetailPanel(int id) {
        JPanel p = new JPanel(new MigLayout(
                "fillx, wrap, insets 20 24 20 24, width 640", "[fill]", "[]8[]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: $Panel.background;");

        JLabel title = new JLabel("Chi tiết phiếu nhập #" + id);
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +3;");
        p.add(title, "gapy 0 12");

        dtophieunhap pn = buspn.get(id);

        p.add(infoRow2("Mã phiếu", String.valueOf(id), "Ngày nhập", String.valueOf(pn.getNgayNhap())));
        p.add(infoRow2("Mã NCC",   String.valueOf(pn.getMaNhaCungCap()), "Tên NCC", pn.getTenNCC(pn.getMaNhaCungCap())));
        p.add(infoRow2("Mã NV",    String.valueOf(pn.getMaNhanVien()),   "Tên NV",  pn.getTenNV(pn.getMaNhanVien())));
        p.add(infoRow2("Tổng tiền", String.valueOf(pn.getTongTien()),    "Ghi chú", pn.getGhiChu()));

        JSeparator sep = new JSeparator();
        sep.putClientProperty(FlatClientProperties.STYLE, "foreground: $Table.gridColor;");
        p.add(sep, "growx, height 1!, gapy 4 4");

        // Detail items table
        Object[] cols = {"#", "Mã SP", "Tên SP", "Giá nhập", "SL", "HSD", "Giá bán", "SL tồn"};
        DefaultTableModel dm = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tbl = styledTable(dm);
        tbl.setRowHeight(32);
        int[] ws = {30, 60, 140, 90, 50, 100, 90, 70};
        for (int i = 0; i < ws.length; i++)
            tbl.getColumnModel().getColumn(i).setPreferredWidth(ws[i]);

        busctpn.getlist(id);
        int i = 1;
        for (dtoctphieunhap ct : busctpn.dsctpn) dm.addRow(ct.toTableRow(i++));

        JScrollPane scroll = scrollOf(tbl);
        scroll.setPreferredSize(new Dimension(600, 200));
        p.add(scroll, "growx");

        JPanel btns = new JPanel(new MigLayout("insets 0", "push[]", "[]"));
        btns.putClientProperty(FlatClientProperties.STYLE, "background: null;");
        JButton btnClose = outlineBtn("Đóng");
        btnClose.addActionListener(e -> SwingUtilities.getWindowAncestor(btnClose).dispose());
        btns.add(btnClose, "height 34!, width 90!, gapy 8 0");
        p.add(btns, "growx");

        return p;
    }

    // ─────────────────────────────────────────────────────────────
    //  UI HELPERS
    // ─────────────────────────────────────────────────────────────
    private JPanel card(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.putClientProperty(FlatClientProperties.STYLE,
                "background: $Panel.background; border: 1,1,1,1,$Component.borderColor,,10;");
        JLabel lbl = new JLabel(title);
        lbl.putClientProperty(FlatClientProperties.STYLE, "font: bold +1;");
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 14, 8, 14));
        p.add(lbl, BorderLayout.NORTH);
        return p;
    }

    private JPanel infoRow2(String l1, String v1, String l2, String v2) {
        JPanel row = new JPanel(new MigLayout("fillx, insets 0", "[80!][fill][90!][fill]", "[]"));
        row.putClientProperty(FlatClientProperties.STYLE, "background: null;");
        for (String[] pair : new String[][]{{l1, v1}, {l2, v2}}) {
            JTextField tf = new JTextField(pair[1]);
            tf.setEditable(false);
            tf.putClientProperty(FlatClientProperties.STYLE,
                    "arc:8; borderWidth:1; background:$Panel.background;");
            row.add(fieldLbl(pair[0]));
            row.add(tf, "growx, height 32!");
        }
        return row;
    }

    private JTable styledTable(DefaultTableModel mdl) {
        JTable tbl = new JTable(mdl);
        tbl.setRowHeight(34);
        tbl.setShowGrid(false);
        tbl.setFillsViewportHeight(true);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbl.putClientProperty(FlatClientProperties.STYLE,
                "rowHeight:34; showHorizontalLines:true; intercellSpacing:0,1;" +
                "cellFocusColor:$TableHeader.hoverBackground;" +
                "selectionBackground:$TableHeader.hoverBackground;" +
                "selectionInactiveBackground:$TableHeader.hoverBackground;" +
                "selectionForeground:$Table.foreground;");
        tbl.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(tbl) {
            protected int getAlignment() { return SwingConstants.CENTER; }
        });
        tbl.getTableHeader().putClientProperty(FlatClientProperties.STYLE,
                "height:34; hoverBackground:null; pressedBackground:null;" +
                "separatorColor:$TableHeader.background; font:bold;");
        tbl.getTableHeader().setReorderingAllowed(false);
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

    private JTextField styledField(String placeholder) {
        JTextField tf = new JTextField();
        tf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        tf.putClientProperty(FlatClientProperties.STYLE,
                "arc: 8; borderWidth: 1; focusedBorderColor: #6366f1;");
        return tf;
    }

    private JLabel fieldLbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.PLAIN, 11f));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private JButton accentBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.putClientProperty(FlatClientProperties.STYLE,
                "background:" + hex(bg) + "; foreground:#ffffff; borderWidth:0; arc:8; focusWidth:0;");
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton outlineBtn(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc:8; borderWidth:1; focusWidth:0;");
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static String hex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private int intVal(JTextField tf) {
        return Integer.parseInt(tf.getText().trim());
    }
    private double doubleVal(JTextField tf) {
        return Double.parseDouble(tf.getText().trim().isEmpty() ? "0" : tf.getText().trim());
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