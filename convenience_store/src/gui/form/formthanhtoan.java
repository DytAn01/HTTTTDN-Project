package gui.form;

import bus.*;
import dto.*;
import gui.comp.Combobox;
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.print.*;
import java.util.ArrayList;
import java.util.Date;

public class formthanhtoan extends JDialog {

    // ── Palette ───────────────────────────────────────────────────
    private static final Color ACCENT = new Color(79, 70, 229);   // Indigo-600
    private static final Color ACCENT_LIGHT = new Color(238, 242, 255); // Indigo-50
    private static final Color SUCCESS = new Color(16, 185, 129);  // Emerald-500
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color TEXT_MAIN = new Color(17, 24, 39);    // Gray-900
    private static final Color TEXT_MUTED = new Color(107, 114, 128); // Gray-500
    private static final Color BORDER = new Color(229, 231, 235); // Gray-200
    private static final Color BG_PAGE = new Color(249, 250, 251); // Gray-50
    private static final Color BG_WHITE = Color.WHITE;
    private static final Color ROW_STRIPE = new Color(248, 250, 252);

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

    private JTextField tfDiscount, tfSale, tfCredit, tfTotal;

    // ─────────────────────────────────────────────────────────────
    public formthanhtoan(ArrayList<dtocthoadon> list, int ma_nv) {
        super((Frame) null, "Thanh toán", true);
        limenu = list;
        System.out.println(">>> list size: " + (list == null ? "NULL" : list.size()));
        if (list != null) {
            for (dtocthoadon ct : list) {
                System.out.println("    - maSP: " + ct.getMaSanPham() + ", SL: " + ct.getSoLuong());
            }
        }
        nv.setManhanvien(ma_nv);
        nv = busNV.getnv(nv);
        hd.setMaNhanVien(ma_nv);
        hd.setTennhanvien(ma_nv);

        tongtien = busCTHD.gettongtien(list);
        tongtienkm = 0;
        tongtienud = 0;
        tongtienfi = tongtien;

        buildUI();
        SwingUtilities.invokeLater(() -> {
            loadPaymentTable(limenu);
        });
        loadKhuyenMai();

        setPreferredSize(new Dimension(1480, 920));
        setMinimumSize(new Dimension(1100, 680));
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    // ─────────────────────────────────────────────────────────────
    //  BUILD UI
    // ─────────────────────────────────────────────────────────────
    private void buildUI() {
        JPanel root = new JPanel(new MigLayout(
                "fill, insets 0", "[fill]", "[shrink 0][fill,grow][shrink 0]"));
        root.setBackground(BG_PAGE);

        root.add(buildTitleBar(), "growx, wrap");
        root.add(buildBody(), "grow, wrap");
        root.add(buildFooter(), "growx");

        setContentPane(root);
    }

    // ── Title bar ─────────────────────────────────────────────────
    private JPanel buildTitleBar() {
        JPanel p = new JPanel(new MigLayout(
                "fillx, insets 0 24 0 24", "[fill]push[][]", "[]"));
        p.setBackground(ACCENT);
        p.setPreferredSize(new Dimension(0, 56));

        // Icon + title
        JLabel icon = new JLabel("🛒");
        icon.setFont(icon.getFont().deriveFont(20f));
        JLabel lbl = new JLabel("Xác nhận thanh toán");
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 15f));
        lbl.setForeground(Color.WHITE);
        icon.setForeground(Color.WHITE);

        JPanel titleGroup = new JPanel(new MigLayout("insets 0, gap 8", "[][]", "[]"));
        titleGroup.setOpaque(false);
        titleGroup.add(icon);
        titleGroup.add(lbl);

        JButton btnClose = new JButton("✕");
        styleBtn(btnClose, new Color(99, 91, 255), Color.WHITE);
        btnClose.setPreferredSize(new Dimension(32, 32));
        btnClose.addActionListener(e -> dispose());

        p.add(titleGroup);
        p.add(btnClose, "width 32!, height 32!, align center");
        return p;
    }

    // ── Body: LEFT (info) | RIGHT (table + totals) ────────────────
    private JPanel buildBody() {
        JPanel p = new JPanel(new MigLayout(
                "fill, insets 20 20 16 20, gap 18",
                "[380!][fill,grow]",
                "[fill]"
        ));
        p.setBackground(BG_PAGE);
        p.add(buildLeftPanel(), "growy");
        p.add(buildRightPanel(), "grow");
        return p;
    }

    // ── LEFT column ───────────────────────────────────────────────
    private JPanel buildLeftPanel() {
        JPanel p = new JPanel(new MigLayout(
                "fillx, wrap, insets 0, gap 14", "[grow]", "[]"));
        p.setBackground(BG_PAGE);
        p.add(buildCustomerCard(), "growx");
        p.add(buildKhuyenMaiCard(), "growx");
        p.add(buildNoteCard(), "growx");
        return p;
    }

    private JPanel buildCustomerCard() {
        JPanel card = card("👤  Thông tin khách hàng");
        JPanel ct = getContent(card);
        ct.setLayout(new MigLayout("fillx, insets 14 16 16 16, wrap 1", "[grow]", "[]5[]12[]5[]"));

        txtPhone = styledField("Nhập số điện thoại...");
        txtName = styledField("Tên khách hàng");
        txtName.setEnabled(false);

        JButton btnCheck = accentBtn("Kiểm tra", ACCENT);
        JButton btnNew = accentBtn("Tạo mới", SUCCESS);
        btnCheck.addActionListener(e -> checkPhone());
        btnNew.addActionListener(e -> createNewCustomer());

        JPanel rowPhone = inlineRow(txtPhone, btnCheck, 110);
        JPanel rowName = inlineRow(txtName, btnNew, 110);

        ct.add(fieldLbl("Số điện thoại"), "growx");
        ct.add(rowPhone, "growx");
        ct.add(fieldLbl("Tên khách hàng"), "growx");
        ct.add(rowName, "growx");
        return card;
    }

    private JPanel buildKhuyenMaiCard() {
        JPanel card = card("🎁  Khuyến mãi");
        JPanel ct = getContent(card);
        ct.setLayout(new MigLayout("fillx, insets 14 16 16 16, wrap 1", "[grow]", "[]5[]"));

        cboKhuyenMai = new Combobox();
        tfDiscount = readonlyField("0 đ");
        cboKhuyenMai.addActionListener(e -> onKhuyenMaiChanged());

        JPanel row = new JPanel(new MigLayout("fillx, insets 0", "[grow][130!]", "[]"));
        row.setOpaque(false);
        row.add(cboKhuyenMai, "growx, h 36!");
        row.add(tfDiscount, "h 36!, gapleft 8");

        ct.add(fieldLbl("Chương trình khuyến mãi"), "growx");
        ct.add(row, "growx");
        return card;
    }

    private JPanel buildNoteCard() {
        JPanel card = card("📝  Ghi chú");
        JPanel ct = getContent(card);
        ct.setLayout(new MigLayout("fill, insets 14 16 16 16", "[fill]", "[fill]"));

        txtNote = new JTextArea(4, 0);
        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);
        txtNote.setFont(txtNote.getFont().deriveFont(13f));

        JScrollPane sp = new JScrollPane(txtNote);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
        ct.add(sp, "grow, h 90!");
        return card;
    }

    // ── RIGHT column: Table + Totals ──────────────────────────────
    private JPanel buildRightPanel() {
        JPanel p = new JPanel(new MigLayout(
                "fill, insets 0, wrap 1",
                "[fill]",
                "[fill,grow][shrink 0]" // table grows, totals fixed
        ));
        p.setBackground(BG_PAGE);
        p.add(buildTableCard(), "grow");
        p.add(buildTotalsCard(), "growx");
        return p;
    }

    private JPanel buildTableCard() {
        JPanel card = card("Danh sách sản phẩm");
        JPanel ct = getContent(card);  // lấy content panel bên trong
        ct.setLayout(new MigLayout("fill, insets 0, gap 0", "[fill]", "[1!][fill,grow]"));
        tableModel = new DefaultTableModel(
                new Object[]{"#", "Tên sản phẩm", "SL", "Đơn giá", "Thành tiền"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        paymentTable = new JTable(tableModel);
        paymentTable.setRowHeight(37);
        paymentTable.setShowGrid(false);
//        paymentTable.setFillsViewportHeight(true);
        paymentTable.setBackground(BG_WHITE);
        paymentTable.setSelectionBackground(ACCENT_LIGHT);
        paymentTable.setSelectionForeground(TEXT_MAIN);
        paymentTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        int[] widths = {45, 280, 55, 110, 130};
        for (int i = 0; i < widths.length; i++) {
            TableColumn col = paymentTable.getColumnModel().getColumn(i);
            col.setPreferredWidth(widths[i]);
            if (i != 1) {
                col.setMaxWidth(widths[i] + 100);
            }
        }

        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER);

        JScrollPane scroll = new JScrollPane(paymentTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_WHITE);

        ct.add(new JSeparator(), "growx, height 1!, wrap");
        ct.add(scroll, "grow, push, hmin 200");

        return card;
    }

    // ── Totals card: ngay dưới bảng ──────────────────────────────
    private JPanel buildTotalsCard() {
        JPanel card = card("💰  Tổng kết thanh toán");
        JPanel ct = getContent(card);
        ct.setLayout(new MigLayout(
                "fillx, insets 14 20 16 20",
                "[grow][200!]",
                "[]6[]6[]2[2!][]"
        ));

        tfSale = readonlyField("0 đ");
        tfCredit = readonlyField("0 đ");
        tfTotal = readonlyField(fmt(tongtien));

        // Style tfTotal to stand out
        tfTotal.setFont(tfTotal.getFont().deriveFont(Font.BOLD, 16f));
        tfTotal.setForeground(new Color(16, 185, 129));

        // Row 1: Khuyến mãi
        ct.add(summaryLabel("Giảm giá khuyến mãi", false));
        ct.add(tfDiscount = readonlyField("0 đ"), "growx, h 34!, wrap");

        // Row 2: Ưu đãi
        ct.add(summaryLabel("Ưu đãi khách hàng", false));
        ct.add(tfSale, "growx, h 34!, wrap");

        // Row 3: Điểm
        ct.add(summaryLabel("Điểm tích lũy nhận được", false));
        ct.add(tfCredit, "growx, h 34!, wrap");

        // Divider
        JSeparator div = new JSeparator();
        div.setForeground(BORDER);
        ct.add(div, "span 2, growx, wrap");

        // Row 4: Thành tiền (bold)
        ct.add(summaryLabel("THÀNH TIỀN", true));
        ct.add(tfTotal, "growx, h 40!, wrap");

        return card;
    }

    // ── Footer ────────────────────────────────────────────────────
    private JPanel buildFooter() {
        JPanel p = new JPanel(new MigLayout(
                "fillx, insets 14 24 16 24", "push[]16[]", "[]"));
        p.setBackground(BG_WHITE);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));

        JButton btnCancel = outlineBtn("✕  Hủy bỏ");
        btnCancel.setPreferredSize(new Dimension(130, 44));
        btnCancel.addActionListener(e -> dispose());

        JButton btnPay = accentBtn("💳  Xác nhận thanh toán", SUCCESS);
        btnPay.setFont(btnPay.getFont().deriveFont(Font.BOLD, 14f));
        btnPay.setPreferredSize(new Dimension(240, 44));
        btnPay.addActionListener(e -> processPayment());

        p.add(btnCancel, "height 44!, width 130!");
        p.add(btnPay, "height 44!, width 240!");
        return p;
    }

    // ─────────────────────────────────────────────────────────────
    //  DATA LOADING
    // ─────────────────────────────────────────────────────────────
    private void loadPaymentTable(ArrayList<dtocthoadon> list) {
        tableModel.setRowCount(0);
        int i = 0;
        for (dtocthoadon ct : list) {
            i++;
            try {
                System.out.println(">>> Đang xử lý SP: " + ct.getMaSanPham());

                dtosanpham sp = new dtosanpham();
                sp.setMaSanPham(ct.getMaSanPham());
                sp = busSP.getsp(sp);
                System.out.println(">>> SP name: " + (sp != null ? sp.getTenSanPham() : "NULL"));

                dtoctphieunhap ctpn = busCTPN.getspnhap(ct.getMaSanPham());
                System.out.println(">>> CTPN: " + (ctpn != null ? ctpn.getGiaBan() : "NULL"));

                double donGia = (ctpn != null) ? ctpn.getGiaBan() : 0;
                double tongGia = ct.getSoLuong() * donGia;
                String tenSP = (sp != null) ? sp.getTenSanPham() : "SP #" + ct.getMaSanPham();

                tableModel.addRow(new Object[]{
                    i, tenSP, ct.getSoLuong(), fmt(donGia), fmt(tongGia)
                });
                System.out.println(">>> Row added OK");

            } catch (Exception ex) {
                System.err.println(">>> Exception: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        System.out.println(">>> Tổng rows: " + tableModel.getRowCount());
        tableModel.fireTableDataChanged();
        paymentTable.revalidate();
        paymentTable.repaint();
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

            tfSale.setText("-  " + fmt(tienud) + "  (" + ud.getTiLeGiam() + "%)");
            tfCredit.setText("+" + diem + " điểm");
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
            warn("Vui lòng nhập đầy đủ số điện thoại và tên!");
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
        tfDiscount.setText("-  " + fmt(tienkm) + "  (" + km.getPhanTram() + "%)");
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
    /**
     * Card với border, tiêu đề — trả về container, content ở getComponent(1)
     */
    private JPanel card(String title) {
        JPanel container = new JPanel(new BorderLayout(0, 0));
        container.setBackground(BG_WHITE);
        container.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));

        JLabel lbl = new JLabel(title);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 13.5f));
        lbl.setForeground(TEXT_MAIN);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
                BorderFactory.createEmptyBorder(11, 16, 11, 16)
        ));
        lbl.setBackground(BG_PAGE);
        lbl.setOpaque(true);

        JPanel content = new JPanel();
        content.setBackground(BG_WHITE);
        content.setOpaque(true);

        container.add(lbl, BorderLayout.NORTH);
        container.add(content, BorderLayout.CENTER);

        return container;
    }

    /**
     * Lấy content panel từ card
     */
    private JPanel getContent(JPanel card) {
        return (JPanel) card.getComponent(1);
    }

    /**
     * Một hàng: field chiếm hết, button cố định bên phải
     */
    private JPanel inlineRow(JTextField field, JButton btn, int btnW) {
        JPanel row = new JPanel(new MigLayout("fillx, insets 0", "[grow][]", "[]"));
        row.setOpaque(false);
        row.add(field, "growx, h 36!");
        row.add(btn, "w " + btnW + "!, h 36!, gapleft 8");
        return row;
    }

    private JLabel summaryLabel(String text, boolean bold) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(bold ? Font.BOLD : Font.PLAIN, bold ? 14f : 13f));
        l.setForeground(bold ? TEXT_MAIN : TEXT_MUTED);
        return l;
    }

    private JTextField styledField(String placeholder) {
        JTextField tf = new JTextField();
        tf.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        tf.putClientProperty(FlatClientProperties.STYLE,
                "arc: 8; borderWidth: 1; focusedBorderColor: #4f46e5;");
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
        l.setFont(l.getFont().deriveFont(Font.PLAIN, 11.5f));
        l.setForeground(TEXT_MUTED);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 3, 0));
        return l;
    }

    private JButton accentBtn(String text, Color bg) {
        JButton btn = new JButton(text);
        styleBtn(btn, bg, Color.WHITE);
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

    private void styleBtn(JButton btn, Color bg, Color fg) {
        btn.putClientProperty(FlatClientProperties.STYLE,
                "background: " + hex(bg) + "; foreground: " + hex(fg) + ";"
                + "borderWidth: 0; arc: 8; focusWidth: 0;");
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
