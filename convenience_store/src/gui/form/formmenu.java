package gui.form;

import bus.busctphieunhap;
import bus.bussanpham;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;
import gui.layout.ResponsiveLayout;
import dto.dtocthoadon;
import dto.dtoctphieunhap;
import dto.dtodonhang;
import dto.dtonhacungcap;
import dto.dtophanloai;
import dto.dtophieunhap;
import dto.dtosanpham;
import gui.comp.MenuCard;
import gui.swing.dashboard.Form;
import gui.swing.dashboard.SystemForm;

import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.SQLException;

@SystemForm(name = "Menu", description = "Giao diện menu bán hàng", tags = {"card", "menu"})
public class formmenu extends Form {

    // ── Data ──────────────────────────────────────────────────────────────────
    private ArrayList<dtosanpham> list_Sp;
    private List<MenuCard> cards;
    private static ArrayList<dtodonhang> list_donhang = new ArrayList<>();
    private ArrayList<dtosanpham> list_SP_has_money = new ArrayList<>();
    private Map<Integer, Integer> productStockById = new HashMap<>();

    // ── BUS ───────────────────────────────────────────────────────────────────
    private bussanpham busSP;
    private busctphieunhap busctpn = new busctphieunhap();

    // ── UI ────────────────────────────────────────────────────────────────────
    private JPanel panelCard;
    private ResponsiveLayout responsiveLayout;
    private JLabel imageDisplayLabel;
    private JTextField quantityField;
    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JLabel cartTotalLabel;
    private JButton btnCheckoutCart;
    private int manv;

    // ── FlatLaf accent color ───────────────────────────────────────────────────
    // Matches whatever accent you set in FlatLaf; we only override where needed.
    private static final Color ACCENT   = new Color(99, 102, 241);
    private static final Color DANGER   = new Color(239, 68, 68);
    private static final Color SUCCESS  = new Color(34, 197, 94);
    private static final Color BG_CARD  = UIManager.getColor("Panel.background") != null
                                          ? UIManager.getColor("Panel.background")
                                          : new Color(248, 250, 252);

    // ─────────────────────────────────────────────────────────────────────────
    public formmenu(int ma_nv) throws SQLException {
        manv = ma_nv;
        init();
        formInit();
    }

    private void init() throws SQLException {
        busSP = new bussanpham();
        cards = new ArrayList<>();
        setLayout(new MigLayout("wrap, fill, insets 10 16 10 16", "[fill]", "[grow 0][fill]"));
        add(createHeaderAction());
        add(createMainArea());
    }

    @Override
    public void formInit() {
        busSP = new bussanpham();
        panelCard.removeAll();
        list_Sp = busSP.listHidden();
        list_SP_has_money.clear();
        productStockById.clear();
        cards.clear();

        Map<Integer, dtoctphieunhap> stockMap = new HashMap<>();
        try {
            list_Sp.stream()
                .map(dtosanpham::getMaNCC).distinct()
                .flatMap(maNCC -> safeListPN(maNCC).stream())
                .flatMap(pn -> safeListCTPN(pn.getMaPhieuNhap()).stream())
                .forEach(ctpn -> stockMap.putIfAbsent(ctpn.getMaSanPham(), ctpn));
        } catch (Exception ex) {
            Logger.getLogger(formmenu.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (dtosanpham sp : list_Sp) {
            dtoctphieunhap info = stockMap.get(sp.getMaSanPham());
            if (info != null) {
                sp.setGiaBan(info.getGiaBan());
                sp.setSoLuong(info.getSoluongtonkho());
                productStockById.put(sp.getMaSanPham(), info.getSoluongtonkho());
            } else {
                sp.setSoLuong(0);
                productStockById.put(sp.getMaSanPham(), 0);
            }
            list_SP_has_money.add(sp);
            MenuCard card = new MenuCard(sp, createEventCard(), this::addProductToCart);
            cards.add(card);
            panelCard.add(card);
        }

        refreshCartTable();
        panelCard.repaint();
        panelCard.revalidate();
    }

    // ── Safe helpers ──────────────────────────────────────────────────────────
    private List<dtophieunhap> safeListPN(Integer maNCC) {
        try { return busSP.listPN(maNCC); }
        catch (SQLException ex) { return new ArrayList<>(); }
    }
    private List<dtoctphieunhap> safeListCTPN(Integer maPN) {
        try { return busSP.listCTPN(maPN); }
        catch (SQLException ex) { return new ArrayList<>(); }
    }

    // ── Header / search bar ───────────────────────────────────────────────────
    private Component createHeaderAction() throws SQLException {
        JPanel panel = new JPanel(new MigLayout(
            "insets 8 4 8 4, fillx",
            "[fill, 260][fill, 160][fill, 110][fill, 90]push",
            "[]"
        ));
        // FlatLaf: transparent panel background
        panel.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        // Search field – FlatLaf leading icon + placeholder
        JTextField txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm sản phẩm...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
            resizeIcon(new ImageIcon(getClass().getResource("/source/image/icon/search.png")), 16, 16));
        // Rounded outline
        txtSearch.putClientProperty(FlatClientProperties.STYLE,
            "arc: 8; focusedBorderColor: #6366f1; borderWidth: 1;");

        // Category combo
        JComboBox<String> comboMaPL = new JComboBox<>();
        comboMaPL.addItem("Tất cả danh mục");
        comboMaPL.putClientProperty(FlatClientProperties.STYLE,
            "arc: 8; borderWidth: 1; focusedBorderColor: #6366f1;");
        try {
            for (dtophanloai pl : busSP.listPhanloai()) comboMaPL.addItem(pl.getTenPhanLoai());
        } catch (Exception ignored) {}

        // Search button – accent fill
        JButton btnSearch = new JButton("Tìm kiếm");
        styleAccentButton(btnSearch, ACCENT);

        // Reset button – outlined
        JButton btnReset = new JButton("Làm mới");
        styleOutlineButton(btnReset);

        // Search logic
        btnSearch.addActionListener(e -> runSearch(txtSearch.getText().trim(), (String) comboMaPL.getSelectedItem()));
        txtSearch.addActionListener(e -> btnSearch.doClick());
        btnReset.addActionListener(e -> { txtSearch.setText(""); comboMaPL.setSelectedIndex(0); formInit(); });

        panel.add(txtSearch);
        panel.add(comboMaPL);
        panel.add(btnSearch);
        panel.add(btnReset);
        return panel;
    }

    private void runSearch(String keyword, String category) {
        panelCard.removeAll();
        cards.clear();
        boolean isCategoryAll = "Tất cả danh mục".equals(category);

        List<dtosanpham> pool = isCategoryAll ? list_SP_has_money : new ArrayList<>();
        if (!isCategoryAll) {
            int maPL = busSP.getMaPL(category);
            for (dtosanpham sp : list_SP_has_money)
                if (sp.getMaPhanLoai() == maPL) pool.add(sp);
        }

        if (!isCategoryAll && keyword.isEmpty()) {
            // show filtered by category only
        } else if (isCategoryAll && keyword.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập từ khóa tìm kiếm.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            panelCard.repaint(); panelCard.revalidate(); return;
        } else {
            pool.removeIf(sp -> !sp.getTenSanPham().toLowerCase().contains(keyword.toLowerCase()));
        }

        if (pool.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không tìm thấy sản phẩm.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (dtosanpham sp : pool) {
                MenuCard card = new MenuCard(sp, createEventCard(), this::addProductToCart);
                cards.add(card); panelCard.add(card);
            }
        }
        panelCard.repaint(); panelCard.revalidate();
    }

    // ── Main split area ───────────────────────────────────────────────────────
    private Component createMainArea() {
        // Products scroll
        responsiveLayout = new ResponsiveLayout(ResponsiveLayout.JustifyContent.FIT_CONTENT, new Dimension(-1, -1), 10, 10, 3);
        panelCard = new JPanel(responsiveLayout);
        panelCard.putClientProperty(FlatClientProperties.STYLE, "border: 10, 10, 10, 10;");

        JScrollPane productScroll = new JScrollPane(panelCard);
        productScroll.setBorder(null);
        styleScrollPane(productScroll);

        // Menu container (left)
        JPanel menuContainer = new JPanel(new BorderLayout());
        menuContainer.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        JLabel menuTitle = buildSectionTitle("THỰC ĐƠN");
        menuContainer.add(menuTitle, BorderLayout.NORTH);
        menuContainer.add(productScroll, BorderLayout.CENTER);

        // Cart panel (right)
        JPanel cartPanel = createCartPanel();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, menuContainer, cartPanel);
        split.setResizeWeight(0.72);
        split.setDividerSize(6);
        split.setEnabled(false);
        split.setBorder(null);
        return split;
    }

    // ── Cart panel ────────────────────────────────────────────────────────────
    private JPanel createCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.putClientProperty(FlatClientProperties.STYLE, "background: $Panel.background;");

        // Title
        panel.add(buildSectionTitle("GIỎ HÀNG"), BorderLayout.NORTH);

        // Table
        cartTable = new JTable();
        cartTable.setRowHeight(36);
        cartTable.setShowGrid(false);
        cartTable.setIntercellSpacing(new Dimension(0, 0));
        cartTable.putClientProperty(FlatClientProperties.STYLE,
            "rowHeight: 36; showHorizontalLines: true;");
        cartTable.setGridColor(UIManager.getColor("TableHeader.separatorColor"));
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(cartTable);
        scroll.setBorder(null);
        styleScrollPane(scroll);
        panel.add(scroll, BorderLayout.CENTER);

        // Bottom bar
        JPanel bottom = new JPanel(new MigLayout("insets 10 12 10 12, fillx", "[fill]push[][]", "[]"));
        bottom.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        cartTotalLabel = new JLabel("Tổng tiền: 0 ₫");
        cartTotalLabel.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 14f));
        cartTotalLabel.putClientProperty(FlatClientProperties.STYLE, "foreground: #ef4444;");

        btnCheckoutCart = new JButton("Thanh toán");
        styleAccentButton(btnCheckoutCart, SUCCESS);
        btnCheckoutCart.addActionListener(e -> checkoutFromCartPanel());

        JButton btnClear = new JButton("Xóa tất cả");
        styleOutlineButton(btnClear);
        btnClear.addActionListener(e -> { list_donhang.clear(); refreshCartTable(); });

        bottom.add(cartTotalLabel);
        bottom.add(btnClear);
        bottom.add(btnCheckoutCart);
        panel.add(bottom, BorderLayout.SOUTH);

        setupCartTableModel();
        return panel;
    }

    private void setupCartTableModel() {
        String[] cols = {"Sản phẩm", "SL", "Đơn giá", ""};
        cartModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 1 || c == 3; }
            @Override public Class<?> getColumnClass(int c) {
                if (c == 1) return Integer.class;
                if (c == 2) return Double.class;
                return Object.class;
            }
        };
        cartTable.setModel(cartModel);

        TableColumnModel cm = cartTable.getColumnModel();
        cm.getColumn(0).setPreferredWidth(160);
        cm.getColumn(1).setPreferredWidth(80);
        cm.getColumn(2).setPreferredWidth(90);
        cm.getColumn(3).setPreferredWidth(36);
        cm.getColumn(3).setMaxWidth(40);

        // Center renderer for all columns
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        cm.getColumn(1).setCellRenderer(center);
        cm.getColumn(2).setCellRenderer(center);

        // Qty +/- editor/renderer
        cm.getColumn(1).setCellRenderer(new QuantityCellRenderer());
        cm.getColumn(1).setCellEditor(new QuantityCellEditor());

        // Delete button
        cm.getColumn(3).setCellRenderer(new DeleteButtonRenderer());
        cm.getColumn(3).setCellEditor(new DeleteButtonEditor());

        refreshCartTable();
    }

    private void refreshCartTable() {
        if (cartModel == null) return;
        cartModel.setRowCount(0);
        for (dtodonhang dh : list_donhang) {
            cartModel.addRow(new Object[]{ dh.getTen(), dh.getSl(), dh.getTt(), "🗑" });
        }
        if (cartTotalLabel != null)
            cartTotalLabel.setText("Tổng: " + formatMoney(calculateTotal()));
    }

    // ── Product detail dialog ─────────────────────────────────────────────────
    private Consumer<dtosanpham> createEventCard() {
        return sp -> {
            try {
                Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                int w = (int)(screen.width * 0.62), h = (int)(screen.height * 0.65);

                JDialog dlg = new JDialog();
                dlg.setSize(w, h);
                dlg.setUndecorated(true);
                dlg.setShape(new RoundRectangle2D.Double(0, 0, w, h, 24, 24));
                dlg.setModal(true);
                dlg.setLayout(new BorderLayout());

                // Title bar
                JPanel titleBar = new JPanel(new BorderLayout());
                titleBar.putClientProperty(FlatClientProperties.STYLE,
                    "background: #6366f1; arc: 0;");
                titleBar.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));
                JLabel titleLbl = new JLabel("Chi tiết sản phẩm");
                titleLbl.setForeground(Color.WHITE);
                titleLbl.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 15f));
                titleLbl.setPreferredSize(new Dimension(w, 46));
                JButton btnClose = flatCloseButton();
                btnClose.addActionListener(e -> dlg.dispose());
                titleBar.add(titleLbl, BorderLayout.CENTER);
                titleBar.add(btnClose, BorderLayout.EAST);
                dlg.add(titleBar, BorderLayout.NORTH);

                // Content
                JPanel content = new JPanel(new MigLayout(
                    "insets 24 28 24 28, fillx",
                    "[300!][32!][fill]", "[fill]"
                ));
                content.putClientProperty(FlatClientProperties.STYLE, "background: $Panel.background;");

                // Left: image + qty
                JPanel imgPanel = buildProductImagePanel(sp, dlg);
                content.add(imgPanel, "growy 0, top");
                content.add(new JLabel(), "");  // spacer

                // Right: info fields
                content.add(buildProductInfoPanel(sp), "grow");
                dlg.add(content, BorderLayout.CENTER);

                dlg.setLocationRelativeTo(null);
                dlg.setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(formmenu.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
    }

    private JPanel buildProductImagePanel(dtosanpham sp, JDialog dlg) {
        JPanel p = new JPanel(new MigLayout("insets 0, flowy, fillx, alignx center", "[center]", "[]12[]12[]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        imageDisplayLabel = new JLabel();
        imageDisplayLabel.setPreferredSize(new Dimension(180, 220));
        imageDisplayLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageDisplayLabel.putClientProperty(FlatClientProperties.STYLE,
            "border: 1, 1, 1, 1, $Component.borderColor, 12;");

        if (sp.getImg() != null && !sp.getImg().isEmpty()) {
            ImageIcon icon = new ImageIcon(System.getProperty("user.dir") + "/src/source/image/sanpham/" + sp.getImg());
            imageDisplayLabel.setIcon(new ImageIcon(icon.getImage().getScaledInstance(180, 220, Image.SCALE_SMOOTH)));
        }
        p.add(imageDisplayLabel, "width 180!, height 220!");

        // Qty stepper
        JLabel qtyLabel = new JLabel("Số lượng");
        qtyLabel.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 13f));
        p.add(qtyLabel);

        JPanel stepper = new JPanel(new BorderLayout(4, 0));
        stepper.putClientProperty(FlatClientProperties.STYLE, "background: null;");
        quantityField = new JTextField("1");
        quantityField.setHorizontalAlignment(JTextField.CENTER);
        quantityField.putClientProperty(FlatClientProperties.STYLE, "arc: 6;");
        JButton btnMinus = new JButton("−"); styleStepperButton(btnMinus);
        JButton btnPlus  = new JButton("+"); styleStepperButton(btnPlus);
        btnMinus.addActionListener(e -> {
            int v = Integer.parseInt(quantityField.getText());
            if (v > 1) quantityField.setText(String.valueOf(v - 1));
        });
        btnPlus.addActionListener(e -> quantityField.setText(String.valueOf(Integer.parseInt(quantityField.getText()) + 1)));
        stepper.add(btnMinus, BorderLayout.WEST);
        stepper.add(quantityField, BorderLayout.CENTER);
        stepper.add(btnPlus, BorderLayout.EAST);
        p.add(stepper, "growx");

        JButton btnAdd = new JButton("Thêm vào giỏ");
        styleAccentButton(btnAdd, ACCENT);
        btnAdd.addActionListener(ev -> {
            dtodonhang dh = new dtodonhang();
            dh.setMa(sp.getMaSanPham()); dh.setTen(sp.getTenSanPham());
            dh.setTt(sp.getGiaBan()); dh.setSl(Integer.parseInt(quantityField.getText()));
            addListGioHang(dh, sp.getSoLuong());
            refreshCartTable();
            dlg.dispose();
        });
        p.add(btnAdd, "growx");
        return p;
    }

    private JPanel buildProductInfoPanel(dtosanpham sp) throws SQLException {
        JPanel p = new JPanel(new MigLayout("insets 0, fillx, wrap 2", "[130!][fill]", "[]12[]12[]12[]12[]12[]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        String tenpl = "", tenncc = "";
        for (dtophanloai pl : busSP.listPhanloai())
            if (pl.getMaPhanLoai() == sp.getMaPhanLoai()) { tenpl = pl.getTenPhanLoai(); break; }
        for (dtonhacungcap ncc : busSP.listNCC())
            if (ncc.getMaNhaCungCap() == sp.getMaNCC()) { tenncc = ncc.getTenNhaCungCap(); break; }

        addInfoRow(p, "Tên sản phẩm",  sp.getTenSanPham());
        addInfoRow(p, "Giá bán",        formatMoney(sp.getGiaBan()));
        addInfoRow(p, "Tồn kho",        String.valueOf(sp.getSoLuong()));
        addInfoRow(p, "Ngày thêm",      String.valueOf(sp.getNgayThem()));
        addInfoRow(p, "Phân loại",      tenpl);
        addInfoRow(p, "Nhà cung cấp",   tenncc);
        return p;
    }

    private void addInfoRow(JPanel p, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 13f));
        lbl.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground;");

        JTextField tf = new JTextField(value);
        tf.setEditable(false);
        tf.putClientProperty(FlatClientProperties.STYLE,
            "arc: 6; background: $TextField.background; borderWidth: 1;");
        p.add(lbl);
        p.add(tf, "growx, height 34!");
    }

    // ── Cart helpers ──────────────────────────────────────────────────────────
    public void addListGioHang(dtodonhang dh, Integer sl) {
        if (sl == 0) { JOptionPane.showMessageDialog(null, "Sản phẩm đã hết hàng"); return; }
        if (sl < dh.getSl()) { JOptionPane.showMessageDialog(null, "Vượt quá số lượng tồn kho"); return; }
        for (dtodonhang e : list_donhang) {
            if (e.getMa() == dh.getMa()) {
                if (sl < e.getSl() + dh.getSl()) { JOptionPane.showMessageDialog(null, "Số lượng trong giỏ đã vượt tồn kho"); return; }
                e.setSl(e.getSl() + dh.getSl());
                JOptionPane.showMessageDialog(null, "Cập nhật giỏ hàng thành công");
                return;
            }
        }
        list_donhang.add(dh);
        JOptionPane.showMessageDialog(null, "Thêm vào giỏ hàng thành công");
    }

    public boolean addListGioHang1(dtodonhang dh, Integer sl) {
        if (sl == 0) { JOptionPane.showMessageDialog(null, "Sản phẩm đã hết hàng"); return false; }
        if (sl < dh.getSl()) { JOptionPane.showMessageDialog(null, "Vượt quá số lượng tồn kho"); return false; }
        for (dtodonhang e : list_donhang) {
            if (e.getMa().equals(dh.getMa())) {
                if (sl < e.getSl() + dh.getSl()) { JOptionPane.showMessageDialog(null, "Số lượng trong giỏ đã vượt tồn kho"); return false; }
                e.setSl(e.getSl() + dh.getSl()); return true;
            }
        }
        list_donhang.add(dh); return true;
    }

    private void addProductToCart(dtosanpham sp) {
        Integer stock = productStockById.getOrDefault(sp.getMaSanPham(), 0);
        if (stock <= 0) { JOptionPane.showMessageDialog(this, "Sản phẩm đã hết hàng"); return; }
        for (dtodonhang dh : list_donhang) {
            if (dh.getMa() == sp.getMaSanPham()) {
                if (dh.getSl() + 1 > stock) { JOptionPane.showMessageDialog(this, "Số lượng vượt tồn kho"); return; }
                dh.setSl(dh.getSl() + 1); refreshCartTable(); return;
            }
        }
        dtodonhang dh = new dtodonhang();
        dh.setMa(sp.getMaSanPham()); dh.setTen(sp.getTenSanPham());
        dh.setTt(sp.getGiaBan()); dh.setSl(1);
        list_donhang.add(dh); refreshCartTable();
    }

    private Integer changeQuantityAtRow(int row, int delta) {
        if (row < 0 || row >= list_donhang.size()) return null;
        dtodonhang dh = list_donhang.get(row);
        int newQty = dh.getSl() + delta;
        if (newQty < 1) return null;
        if (newQty > productStockById.getOrDefault(dh.getMa(), Integer.MAX_VALUE)) {
            JOptionPane.showMessageDialog(this, "Số lượng vượt quá tồn kho"); return null;
        }
        dh.setSl(newQty);
        if (cartModel != null && row < cartModel.getRowCount()) cartModel.setValueAt(newQty, row, 1);
        if (cartTotalLabel != null) cartTotalLabel.setText("Tổng: " + formatMoney(calculateTotal()));
        if (cartTable != null && row < cartTable.getRowCount()) cartTable.setRowSelectionInterval(row, row);
        return newQty;
    }

    private void removeCartRow(int row) {
        if (row < 0 || row >= list_donhang.size()) return;
        list_donhang.remove(row); refreshCartTable();
    }

    public Double calculateTotal() {
        return list_donhang.stream().mapToDouble(dh -> dh.getSl() * dh.getTt()).sum();
    }

    private void checkoutFromCartPanel() {
        if (list_donhang.isEmpty()) { JOptionPane.showMessageDialog(this, "Giỏ hàng rỗng"); return; }
        ArrayList<dtocthoadon> list = new ArrayList<>();
        for (dtodonhang i : list_donhang) {
            dtocthoadon a = new dtocthoadon();
            a.setMaSanPham(i.getMa()); a.setSoLuong(i.getSl()); a.setTensanpham(i.getTen()); list.add(a);
        }
        formthanhtoan formth = new formthanhtoan(list, manv);
        formth.setSize(510, 750); formth.setResizable(false); formth.setLocationRelativeTo(null);
        list_donhang.clear(); refreshCartTable();
        formth.setVisible(true); formth.toFront();
    }

    // ── Cell editors / renderers ──────────────────────────────────────────────
    private class QuantityCellRenderer extends JPanel implements TableCellRenderer {
        private final JButton m = new JButton("−"), p = new JButton("+");
        private final JLabel lbl = new JLabel("0", SwingConstants.CENTER);
        public QuantityCellRenderer() {
            setLayout(new BorderLayout(2, 0)); setOpaque(false);
            m.setEnabled(false); p.setEnabled(false);
            styleStepperButton(m); styleStepperButton(p);
            add(m, BorderLayout.WEST); add(lbl, BorderLayout.CENTER); add(p, BorderLayout.EAST);
        }
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            lbl.setText(String.valueOf(v)); setBackground(sel ? t.getSelectionBackground() : t.getBackground()); return this;
        }
    }

    private class QuantityCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel = new JPanel(new BorderLayout(2, 0));
        private final JButton minus = new JButton("−"), plus = new JButton("+");
        private final JLabel lbl = new JLabel("0", SwingConstants.CENTER);
        private int editRow = -1;
        public QuantityCellEditor() {
            panel.setOpaque(false); styleStepperButton(minus); styleStepperButton(plus);
            panel.add(minus, BorderLayout.WEST); panel.add(lbl, BorderLayout.CENTER); panel.add(plus, BorderLayout.EAST);
            minus.setFocusable(false); plus.setFocusable(false);
            minus.addActionListener(e -> { Integer nq = changeQuantityAtRow(editRow, -1); if (nq != null) lbl.setText(String.valueOf(nq)); stopCellEditing(); });
            plus.addActionListener(e  -> { Integer nq = changeQuantityAtRow(editRow,  1); if (nq != null) lbl.setText(String.valueOf(nq)); stopCellEditing(); });
        }
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) { editRow = r; lbl.setText(String.valueOf(v)); return panel; }
        @Override public Object getCellEditorValue() { return lbl.getText(); }
    }

    private class DeleteButtonRenderer extends JButton implements TableCellRenderer {
        public DeleteButtonRenderer() {
            setText("✕"); setFocusPainted(false); setBorderPainted(false);
            setForeground(DANGER); setContentAreaFilled(false); setFont(getFont().deriveFont(Font.BOLD, 13f));
        }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) { return this; }
    }

    private class DeleteButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton btn = new JButton("✕");
        private int editRow = -1;
        public DeleteButtonEditor() {
            btn.setFocusPainted(false); btn.setBorderPainted(false);
            btn.setForeground(DANGER); btn.setContentAreaFilled(false); btn.setFont(btn.getFont().deriveFont(Font.BOLD, 13f));
            btn.addActionListener(e -> { removeCartRow(editRow); stopCellEditing(); });
        }
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) { editRow = r; return btn; }
        @Override public Object getCellEditorValue() { return "✕"; }
    }

    // ── UI helpers ────────────────────────────────────────────────────────────
    private JLabel buildSectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD, 14f));
        lbl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIManager.getColor("Component.borderColor")),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        return lbl;
    }

    private void styleAccentButton(JButton btn, Color color) {
        btn.putClientProperty(FlatClientProperties.STYLE,
            "background: " + toHex(color) + "; " +
            "foreground: #ffffff; " +
            "borderWidth: 0; " +
            "arc: 8; " +
            "focusWidth: 0; " +
            "innerFocusWidth: 0;");
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleOutlineButton(JButton btn) {
        btn.putClientProperty(FlatClientProperties.STYLE,
            "arc: 8; " +
            "borderWidth: 1; " +
            "focusWidth: 0;");
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleStepperButton(JButton btn) {
        btn.putClientProperty(FlatClientProperties.STYLE,
            "arc: 6; " +
            "borderWidth: 1; " +
            "focusWidth: 0; " +
            "margin: 2, 8, 2, 8;");
        btn.setFocusPainted(false);
    }

    private JButton flatCloseButton() {
        JButton btn = new JButton("✕");
        btn.putClientProperty(FlatClientProperties.STYLE,
            "background: #ef4444; " +
            "foreground: #ffffff; " +
            "borderWidth: 0; " +
            "arc: 0; " +
            "focusWidth: 0;");
        btn.setPreferredSize(new Dimension(48, 46));
        btn.setFocusPainted(false);
        btn.setFont(btn.getFont().deriveFont(Font.BOLD, 13f));
        return btn;
    }

    private void styleScrollPane(JScrollPane sp) {
        sp.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
            "trackArc: $ScrollBar.thumbArc; thumbInsets: 0, 0, 0, 0; width: 7;");
        sp.getHorizontalScrollBar().putClientProperty(FlatClientProperties.STYLE,
            "trackArc: $ScrollBar.thumbArc; thumbInsets: 0, 0, 0, 0; width: 7;");
        sp.getVerticalScrollBar().setUnitIncrement(12);
        sp.getHorizontalScrollBar().setUnitIncrement(12);
    }

    private String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private String formatMoney(double amount) {
        return String.format("%,.0f ₫", amount);
    }

    private ImageIcon resizeIcon(ImageIcon icon, int w, int h) {
        return new ImageIcon(icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }
}