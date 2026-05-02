package gui.form;

import bus.*;
import dto.*;
import gui.comp.Combobox;
import gui.comp.TableSorter;
import gui.comp.HintTextArea;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.util.ArrayList;
import java.util.Date;

public class formthanhtoan extends JDialog {

    // ── Palette ───────────────────────────────────────────────────
    private static final Color ACCENT = new Color(99, 102, 241);
    private static final Color ACCENT_SOFT = new Color(238, 242, 255);
    private static final Color SUCCESS = new Color(22, 163, 74);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color WARNING = new Color(234, 179, 8);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color BG_SUBTLE = new Color(248, 250, 252);

    // ── BUS ───────────────────────────────────────────────────────
    private final buskhuyenmai busKM = new buskhuyenmai();
    private final bushoadon busHD = new bushoadon();
    private final bussanpham busSP = new bussanpham();
    private final busuudai busUD = new busuudai();
    private final bustichdiem busTD = new bustichdiem();
    private final buskhachhang busKH = new buskhachhang();
    private final busnhanvien busNV = new busnhanvien();
    private final buscthoadon busCTHD = new buscthoadon();
    private final busctphieunhap busCTPN = new busctphieunhap();

    // ── DTO state ─────────────────────────────────────────────────
    private dtokhachhang kh = new dtokhachhang();
    private dtouudai ud = new dtouudai();
    private dtokhuyenmai km = new dtokhuyenmai();
    private dtotichdiem td = new dtotichdiem();
    private dtonhanvien nv = new dtonhanvien();
    private dtohoadon hd = new dtohoadon();

    private ArrayList<dtocthoadon> limenu;

    private double tongtien, tongtienkm, tongtienud, tongtienfi;
    private int diem;
    double bHeight = 0.0;

    // ── UI fields ─────────────────────────────────────────────────
    private JTextField txtPhone, txtName;
    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private Combobox cboKhuyenMai;
    private JTextArea txtNote;

    // Read-only summary fields
    private JTextField tfDiscount, tfSale, tfCredit, tfTotal;

    // ─────────────────────────────────────────────────────────────
    public formthanhtoan(ArrayList<dtocthoadon> list, int ma_nv) {
        super((Frame) null, "Thanh toán", true);
        limenu = list;

        nv.setManhanvien(ma_nv);
        nv = busNV.getnv(nv);
        hd.setMaNhanVien(ma_nv);
        hd.setTennhanvien(ma_nv);

        tongtien = busCTHD.gettongtien(list);
        tongtienkm = 0;
        tongtienud = 0;
        tongtienfi = tongtien;

        buildUI();
        loadPaymentTable(list);
        loadKhuyenMai();

        setPreferredSize(new Dimension(2000, 750));
        pack();
        setLocationRelativeTo(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    // ─────────────────────────────────────────────────────────────
    //  BUILD UI
    // ─────────────────────────────────────────────────────────────
    private void buildUI() {
        JPanel root = new JPanel(new MigLayout(
                "fill, insets 0", "[fill]", "[shrink 0][fill,grow][shrink 0]"));
        root.setBackground(Color.WHITE);

        root.add(buildTitleBar(), "growx, wrap");
        root.add(buildBody(), "grow, wrap");
        root.add(buildFooter(), "growx");

        setContentPane(root);
    }

    // ── Title bar ─────────────────────────────────────────────────
    private JPanel buildTitleBar() {
        JPanel p = new JPanel(new MigLayout("fillx, insets 14 20 14 20", "[fill]push[]", "[]"));
        p.setBackground(ACCENT);

        JLabel lbl = new JLabel("Thanh toán đơn hàng");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 16f));
        lbl.setForeground(Color.WHITE);
        p.add(lbl);

        JButton btnClose = new JButton("✕");
        btnClose.putClientProperty(FlatClientProperties.STYLE,
                "background: #7c3aed; foreground: #ffffff; arc: 8; borderWidth: 0; focusWidth: 0;");
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dispose());
        p.add(btnClose, "width 32!, height 32!");
        return p;
    }

    // ── Body (2 columns) ──────────────────────────────────────────
    private JPanel buildBody() {
        JPanel p = new JPanel(new MigLayout(
                "fill, insets 16 20 8 20, gap 20",
                "[grow 35][grow 65]", // LEFT nhỏ hơn, RIGHT to ra
                "[fill]"
        ));
        p.setBackground(Color.WHITE);

        p.add(buildLeftPanel(), "grow");
        p.add(buildRightPanel(), "grow");

        return p;
    }

    // ── LEFT: Khách hàng + Khuyến mãi + Tóm tắt ─────────────────
    private JPanel buildLeftPanel() {
        JPanel p = new JPanel(new MigLayout(
                "fillx, wrap, insets 0, gap 16",
                "[grow]",
                "[]"
        ));
        p.setBackground(Color.WHITE);

        p.add(buildCustomerCard(), "growx");
        p.add(buildKhuyenMaiCard(), "growx");
        p.add(buildSummaryCard(), "growx");

        return p;
    }

    private JPanel buildCustomerCard() {
        JPanel card = card("Thông tin khách hàng");
        JPanel content = (JPanel) card.getComponent(1);

        content.setLayout(new MigLayout(
                "fillx, wrap",
                "[grow]",
                "[]8[]8[]8[]"
        ));

        txtPhone = styledField("Nhập số điện thoại...");
        txtName = styledField("Tên khách hàng");
        txtName.setEnabled(false);

        JButton btnCheck = accentBtn("Kiểm tra", ACCENT);
        JButton btnNew = accentBtn("Tạo mới", ACCENT);

        content.add(fieldLbl("Số điện thoại"));
        content.add(txtPhone, "growx, h 35!");
        content.add(btnCheck, "w 100!, h 35!");

        content.add(fieldLbl("Tên khách hàng"));
        content.add(txtName, "growx, h 35!");
        content.add(btnNew, "w 100!, h 35!");

        return card;
    }

    private JPanel buildKhuyenMaiCard() {
        JPanel card = card("Khuyến mãi");
        JPanel content = (JPanel) card.getComponent(1);

        content.setLayout(new MigLayout(
                "fillx, wrap",
                "[grow][120!]",
                "[]8[]"
        ));

        cboKhuyenMai = new Combobox();
        tfDiscount = readonlyField("0 đ");

        content.add(fieldLbl("Chương trình giảm giá"), "span");
        content.add(cboKhuyenMai, "growx, h 35!");
        content.add(tfDiscount, "h 35!");

        return card;
    }

    private JPanel buildSummaryCard() {
        JPanel card = card("Tóm tắt thanh toán");
        JPanel content = (JPanel) card.getComponent(1);

        content.setLayout(new MigLayout(
                "fillx, wrap",
                "[grow][120!]",
                "[]8[]8[]"
        ));

        tfSale = readonlyField("0 đ");
        tfCredit = readonlyField("0 đ");
        tfTotal = readonlyField(fmt(tongtien));

        content.add(new JLabel("Ưu đãi khách hàng"));
        content.add(tfSale);

        content.add(new JLabel("Điểm tích lũy"));
        content.add(tfCredit);

        content.add(new JLabel("Thành tiền"));
        content.add(tfTotal);

        return card;
    }

    // ── RIGHT: Bảng sản phẩm + Ghi chú ──────────────────────────
    private JPanel buildRightPanel() {
        JPanel p = new JPanel(new MigLayout(
                "fill, wrap, insets 0", "[fill]", "[fill,grow][8!][shrink 0]"));
        p.setBackground(Color.WHITE);

        // Table card
        JPanel tblCard = card("Danh sách sản phẩm");
        tblCard.setLayout(new MigLayout("fill, insets 0", "[fill]", "[shrink 0][1!][fill,grow]"));

        tableModel = new DefaultTableModel(
                new Object[]{"#", "Tên sản phẩm", "SL", "Đơn giá", "Thành tiền"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        paymentTable = new JTable(tableModel);
        paymentTable.setRowHeight(34);
        paymentTable.setShowGrid(false);
        paymentTable.setFillsViewportHeight(true);
        paymentTable.putClientProperty(FlatClientProperties.STYLE,
                "rowHeight:34; showHorizontalLines:true; intercellSpacing:0,1;"
                + "selectionBackground:$TableHeader.hoverBackground;"
                + "selectionForeground:$Table.foreground;");
        paymentTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE,
                "height:32; hoverBackground:null; pressedBackground:null;"
                + "separatorColor:$TableHeader.background; font:bold;");
        paymentTable.getTableHeader().setReorderingAllowed(false);

        int[] widths = {30, 140, 40, 90, 90};
        for (int i = 0; i < widths.length; i++) {
            paymentTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        paymentTable.getColumnModel().getColumn(0).setCellRenderer(center);
        paymentTable.getColumnModel().getColumn(2).setCellRenderer(center);
        paymentTable.getColumnModel().getColumn(3).setCellRenderer(right);
        paymentTable.getColumnModel().getColumn(4).setCellRenderer(right);

        JScrollPane scroll = new JScrollPane(paymentTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
                "trackArc:$ScrollBar.thumbArc; thumbInsets:0,2,0,2; width:7;");

        JSeparator tblSep = new JSeparator();
        tblSep.putClientProperty(FlatClientProperties.STYLE, "foreground: $Table.gridColor;");

        tblCard.add(new JLabel(), "height 0!");  // spacer for card header padding
        tblCard.add(tblSep, "growx, height 1!");
        tblCard.add(scroll, "grow");

        p.add(tblCard, "grow, push");

        // Note card
        JPanel noteCard = card("Ghi chú");
        noteCard.setLayout(new MigLayout("fill, insets 12 14 12 14", "[fill]", "[fill]"));
        txtNote = new JTextArea();
        txtNote.setRows(3);
        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);
        txtNote.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
        JScrollPane noteScroll = new JScrollPane(txtNote);
        noteScroll.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
        noteScroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1, true));
        noteCard.add(noteScroll, "grow, height 80!");
        p.add(noteCard, "growx");

        return p;
    }

    // ── Footer ────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel p = new JPanel(new MigLayout(
                "fillx, insets 12 20 16 20", "push[]16[]", "[]"));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        JButton btnCancel = outlineBtn("Hủy");
        btnCancel.addActionListener(e -> dispose());

        JButton btnPay = accentBtn("💳  Xác nhận thanh toán", SUCCESS);
        btnPay.setFont(btnPay.getFont().deriveFont(Font.BOLD, 13f));
        btnPay.addActionListener(e -> processPayment());

        p.add(btnCancel, "height 38!, width 100!");
        p.add(btnPay, "height 38!, width 200!");
        return p;
    }

    // ─────────────────────────────────────────────────────────────
    //  DATA
    // ─────────────────────────────────────────────────────────────
    private void loadPaymentTable(ArrayList<dtocthoadon> list) {
        tableModel.setRowCount(0);
        int i = 0;
        for (dtocthoadon ct : list) {
            i++;
            dtosanpham sp = new dtosanpham();
            sp.setMaSanPham(ct.getMaSanPham());
            sp = busSP.getsp(sp);
            dtoctphieunhap ctpn = busCTPN.getspnhap(ct.getMaSanPham());
            double donGia = ctpn.getGiaBan();
            double tongGia = ct.getSoLuong() * donGia;
            tableModel.addRow(new Object[]{
                i, sp.getTenSanPham(), ct.getSoLuong(),
                fmt(donGia), fmt(tongGia)
            });
        }
    }

    private void loadKhuyenMai() {
        cboKhuyenMai.removeAllItems();
        for (dtokhuyenmai item : busKM.getkhuyenmaitoday()) {
            cboKhuyenMai.addItem(item.getTenKhuyenMai());
        }
        cboKhuyenMai.setSelectedIndex(-1);
    }

    // ─────────────────────────────────────────────────────────────
    //  ACTIONS
    // ─────────────────────────────────────────────────────────────
    private void checkPhone() {
        String phone = txtPhone.getText().trim();
        if (phone.isEmpty()) {
            warn("Chưa nhập số điện thoại!");
            return;
        }
        if (!busKH.checkphone(phone)) {
            kh = busKH.getkhbyphone(phone);
            hd.setMaKhachHang(kh.getMaKhachHang());
            txtName.setText(kh.getTenKhachHang());

            ud.setMaUuDai(kh.getMaUudai());
            ud = busUD.getud(ud);
            double tienud = (tongtien * ud.getTiLeGiam()) / 100;
            tongtienud = tienud;
            tongtienfi = tongtien - tongtienkm - tongtienud;

            td = busTD.gettdbytien(tongtien);
            hd.setMaTichDiem(td.getMaTichDiem());
            diem = td.getDiemTichLuy();

            tfSale.setText("- " + fmt(tienud) + "  (" + ud.getTiLeGiam() + "%)");
            tfCredit.setText(diem + " điểm");
            tfTotal.setText(fmt(tongtienfi));
            info("Đã tìm thấy khách hàng: " + kh.getTenKhachHang());
        } else {
            warn("Không tìm thấy số điện thoại trong hệ thống!");
        }
    }

    private void createNewCustomer() {
        String phone = txtPhone.getText().trim();
        String name = txtName.getText().trim();
        if (phone.isEmpty() || name.isEmpty()) {
            warn("Vui lòng nhập số điện thoại và tên khách hàng!");
            return;
        }
        dtokhachhang khmoi = new dtokhachhang(1, phone, name, 0, 1);
        if (busKH.addKhachHang(khmoi)) {
            info("Thêm khách hàng mới thành công!");
        }
    }

    private void onKhuyenMaiChanged() {
        Object sel = cboKhuyenMai.getSelectedItem();
        if (sel == null) {
            return;
        }
        String ten = sel.toString();
        if (ten.equals("Bỏ chọn")) {
            tongtienkm = 0;
            tongtienfi = tongtien - tongtienud;
            tfDiscount.setText("0 đ");
            tfTotal.setText(fmt(tongtienfi));
            cboKhuyenMai.setSelectedIndex(-1);
            loadKhuyenMai();
            return;
        }
        km = busKM.getkmbyname(ten);
        hd.setMaKhuyenMai(km.getMaKhuyenMai());
        double tienkm = (tongtien * km.getPhanTram()) / 100;
        tongtienkm = tienkm;
        tongtienfi = tongtien - tongtienkm - tongtienud;
        tfDiscount.setText("- " + fmt(tienkm));
        tfTotal.setText(fmt(tongtienfi));
        if (!"Bỏ chọn".equals(cboKhuyenMai.getItemAt(cboKhuyenMai.getItemCount() - 1))) {
            cboKhuyenMai.addItem("Bỏ chọn");
        }
    }

    private void processPayment() {
        hd.setGhiChu(txtNote.getText());
        hd.setNgayMua(new java.sql.Timestamp(new Date().getTime()));
        hd.setTongTien(tongtienfi);
        hd.setIsHidden(0);
        int mahd = busHD.maxID() + 1;
        hd.setMaHoaDon(mahd);

        if (hd.getMaKhachHang() == 0 && hd.getMaKhuyenMai() == 0) {
            busHD.addhdnokmkh(hd);
        } else if (hd.getMaKhachHang() == 0) {
            busHD.addhdnokh(hd);
            updateKM();
        } else if (hd.getMaKhuyenMai() == 0) {
            busHD.addhdnokm(hd);
            updateKH();
        } else {
            busHD.add(hd);
            updateKM();
            updateKH();
        }

        busHD.getlist();
        hd.setMaHoaDon(mahd);
        for (dtocthoadon ct : limenu) {
            ct.setMaHoaDon(mahd);
            dtoctphieunhap ctpn = busCTPN.getspnhap(ct.getMaSanPham());
            ct.setDonGia(ctpn.getGiaBan());
            busCTHD.add(ct);
            ctpn.setSoluongtonkho(ctpn.getSoluongtonkho() - ct.getSoLuong());
            busCTPN.update(ctpn);
        }

        info("Thanh toán thành công! Đang in hóa đơn...");
        bHeight = limenu.size();
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setPrintable(new BillPrintable(), getPageFormat(pj));
        try {
            pj.print();
        } catch (PrinterException ex) {
            ex.printStackTrace();
        }
        dispose();
    }

    private void updateKM() {
        km.setSoLuongDaDung(km.getSoLuongDaDung() + 1);
        busKM.updatekhuyenmai(km);
    }

    private void updateKH() {
        kh.setDiemTichLuy(kh.getDiemTichLuy() + diem);
        kh.setMaUudai(busUD.setudbydiem(kh.getDiemTichLuy()).getMaUuDai());
        busKH.updatediemtichluy(kh);
    }

    // ─────────────────────────────────────────────────────────────
    //  PRINT
    // ─────────────────────────────────────────────────────────────
    public PageFormat getPageFormat(PrinterJob pj) {
        PageFormat pf = pj.defaultPage();
        Paper paper = pf.getPaper();
        double w = cm_to_pp(14.5), h = cm_to_pp(14.8);
        paper.setSize(w, h);
        paper.setImageableArea(0, 5, w, h);
        pf.setOrientation(PageFormat.PORTRAIT);
        pf.setPaper(paper);
        return pf;
    }

    protected static double cm_to_pp(double cm) {
        return toPPI(cm * 0.393600787);
    }

    protected static double toPPI(double inch) {
        return inch * 72d;
    }

    public class BillPrintable implements Printable {

        @Override
        public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException {
            if (pageIndex != 0) {
                return NO_SUCH_PAGE;
            }
            Graphics2D g2 = (Graphics2D) g;
            g2.translate((int) pf.getImageableX(), (int) pf.getImageableY());
            g2.setFont(new Font("Times New Roman", Font.PLAIN, 9));
            int a = 125, y = 20, ys = 10, rh = 15;
            g2.drawString("-------------------------------------------------------------", a, y);
            y += ys;
            g2.drawString("            CỬA HÀNG TIỆN LỢI ABC", a, y);
            y += ys;
            g2.drawString("       Ngày mua: " + hd.getNgayMua(), a, y);
            y += ys;
            g2.drawString("       Mã HĐ: " + hd.getMaHoaDon(), a, y);
            y += ys;
            g2.drawString("       Nhân viên: " + nv.getTennhanvien(), a, y);
            y += ys;
            g2.drawString("       Khách hàng: " + kh.getTenKhachHang(), a, y);
            y += ys;
            g2.drawString("-------------------------------------------------------------", a, y);
            y += rh;
            g2.drawString("   Tên sản phẩm                        Tổng tiền (VND)", a, y);
            y += ys;
            g2.drawString("-------------------------------------------------------------", a, y);
            y += rh;
            int stt = 0;
            for (dtocthoadon i : limenu) {
                stt++;
                dtoctphieunhap ctpn = busCTPN.getspnhap(i.getMaSanPham());
                g2.drawString("  " + stt + " " + i.getTensanpham(), a, y);
                y += rh;
                g2.drawString("      " + ctpn.getGiaBan() + " x " + i.getSoLuong() + " = " + i.getSoLuong() * ctpn.getGiaBan(), a, y);
                y += ys;
            }
            g2.drawString("-------------------------------------------------------------", a, y);
            y += ys;
            g2.drawString("    Tổng tiền:       " + tongtien, a, y);
            y += ys;
            g2.drawString("    Khuyến mãi:    - " + tongtienkm, a, y);
            y += ys;
            g2.drawString("    Ưu đãi KH:     - " + tongtienud, a, y);
            y += ys;
            g2.drawString("    Thành tiền:      " + tongtienfi, a, y);
            y += ys;
            g2.drawString("-------------------------------------------------------------", a, y);
            y += ys;
            g2.drawString("                    CẢM ƠN QUÝ KHÁCH", a, y);
            return PAGE_EXISTS;
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  UI HELPERS
    // ─────────────────────────────────────────────────────────────
    private JPanel card(String title) {
        JPanel container = new JPanel(new BorderLayout());

        container.putClientProperty(FlatClientProperties.STYLE,
                "background: $Panel.background; border: 1,1,1,1,$Component.borderColor,,10;");

        JLabel lbl = new JLabel(title);
        lbl.setBorder(BorderFactory.createEmptyBorder(10, 14, 8, 14));

        JPanel content = new JPanel();
        content.setOpaque(false);

        container.add(lbl, BorderLayout.NORTH);
        container.add(content, BorderLayout.CENTER);

        return container;
    }

    private JPanel summaryRow(String label, JTextField valueField) {
        JPanel row = new JPanel(new MigLayout("fillx, insets 0", "[fill,grow][130!]", "[]"));
        row.setBackground(Color.WHITE);
        JLabel lbl = new JLabel(label);
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 12f));
        lbl.setForeground(TEXT_MUTED);
        row.add(lbl);
        row.add(valueField, "height 34!");
        return row;
    }

    private JTextField styledField(String placeholder) {
        JTextField tf = new JTextField();
        tf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        tf.putClientProperty(FlatClientProperties.STYLE,
                "arc: 8; borderWidth: 1; focusedBorderColor: #6366f1;");
        return tf;
    }

    private JTextField readonlyField(String val) {
        JTextField tf = new JTextField(val);
        tf.setEditable(false);
        tf.setHorizontalAlignment(SwingConstants.RIGHT);
        tf.putClientProperty(FlatClientProperties.STYLE,
                "arc: 8; borderWidth: 1; background: $Panel.background;");
        return tf;
    }

    private JLabel fieldLbl(String text) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.PLAIN, 11f));
        l.setForeground(TEXT_MUTED);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        return l;
    }

    private JButton accentBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.putClientProperty(FlatClientProperties.STYLE,
                "background: " + hex(bg) + "; foreground: #ffffff;"
                + "borderWidth: 0; arc: 8; focusWidth: 0;");
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton outlineBtn(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 1; focusWidth: 0;");
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static String hex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    private static String fmt(double val) {
        return String.format("%,.0f đ", val);
    }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
