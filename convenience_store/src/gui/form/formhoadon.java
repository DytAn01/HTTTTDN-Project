package gui.form;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.toedter.calendar.JDateChooser;
import bus.bushoadon;
import bus.buscthoadon;
import bus.buskhuyenmai;
import bus.bustichdiem;
import dto.dtohoadon;
import dto.dtocthoadon;
import net.miginfocom.swing.MigLayout;
import gui.table.TableHeaderAlignment;

public class formhoadon extends JPanel {

    private final bushoadon   bushd   = new bushoadon();
    private final buscthoadon buscthd = new buscthoadon();

    private JTable       generalTable;
    private JButton      btnDetail;
    private JTextField   txtSearch;
    private JDateChooser dateFrom;
    private JDateChooser dateTo;

    private static final Color ACCENT      = new Color( 99, 102, 241);
    private static final Color ACCENT_SOFT = new Color(238, 242, 255);
    private static final Color DANGER      = new Color(239,  68,  68);
    private static final Color BORDER      = new Color(226, 232, 240);
    private static final Color TEXT_MUTED  = new Color(100, 116, 139);

    public formhoadon() {
        setLayout(new MigLayout("fill, insets 16 20 16 20, wrap", "[fill]", "[shrink 0][fill, grow]"));
        add(buildHeader(),       "growx");
        add(buildTableSection(), "grow");
    }

    // ── Header ────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel p = new JPanel(new MigLayout("fillx, insets 0 0 12 0", "[fill]push[]", "[]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");
        JLabel title = new JLabel("Quản lý Hóa đơn");
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +6;");
        p.add(title);
        bushd.getlist();
        JLabel badge = new JLabel(bushd.dshd.size() + " hóa đơn");
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

    // ── Card ──────────────────────────────────────────────────────
    private JPanel buildTableSection() {
        JPanel card = new JPanel(new MigLayout(
            "fill, insets 0, wrap", "[fill]", "[shrink 0][1!][fill, grow]"));
        card.putClientProperty(FlatClientProperties.STYLE,
            "background: $Panel.background; border: 1,1,1,1,$Component.borderColor,12;");
        card.add(buildToolbar(), "growx, gapx 16 16, gapy 14 10");
        JSeparator sep = new JSeparator();
        sep.putClientProperty(FlatClientProperties.STYLE, "foreground: $Table.gridColor;");
        card.add(sep, "growx, height 1!");
        card.add(buildTable(), "grow");
        return card;
    }

    // ── Toolbar ───────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel p = new JPanel(new MigLayout("fillx, insets 0, wrap", "[fill]", "[]8[]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        // Row 1: search + date range
        JPanel row1 = new JPanel(new MigLayout(
            "insets 0", "[fill,grow][86!][20!][fill][4!][130!][8!][fill][4!][130!]", "[center]"));  

        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm mã HĐ, tên KH, nhân viên...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
            new FlatSVGIcon("source/image/icon/search.svg", 0.4f));
        txtSearch.putClientProperty(FlatClientProperties.STYLE,
            "arc: 8; focusedBorderColor: #6366f1; borderWidth: 1;");
        txtSearch.addActionListener(e -> applyFilters());
        row1.add(txtSearch, "height 32!");

        JButton btnSearch = accentButton("Tìm kiếm", ACCENT);
        btnSearch.addActionListener(e -> applyFilters());
        row1.add(btnSearch, "height 32!");

        row1.add(new JSeparator(JSeparator.VERTICAL), "growy, width 1!");

        row1.add(makeLabel("Từ ngày:"), "");
        dateFrom = makeDateChooser(null);   // null = chưa chọn → không lọc đầu
        dateFrom.addPropertyChangeListener("date", evt -> applyFilters());
        row1.add(dateFrom, "height 32!");

        row1.add(makeLabel("Đến:"), "");
        dateTo = makeDateChooser(new Date()); // mặc định hôm nay
        dateTo.addPropertyChangeListener("date", evt -> applyFilters());
        row1.add(dateTo, "height 32!");
        p.add(row1, "growx");

        // Row 2: action buttons
        JPanel row2 = new JPanel(new MigLayout("insets 0", "[]8[]push[]", "[center]"));
        row2.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        JButton btnReload = outlineButton("↺  Tải lại");
        btnReload.addActionListener(e -> reloadTable());
        row2.add(btnReload, "height 32!");

        JButton btnClear = outlineButton("✕  Xóa lọc ngày");
        btnClear.addActionListener(e -> { dateFrom.setDate(null); dateTo.setDate(new Date()); applyFilters(); });
        row2.add(btnClear, "height 32!");

        btnDetail = accentButton("📋  Xem chi tiết", new Color(79, 70, 229));
        btnDetail.setEnabled(false);
        btnDetail.addActionListener(e -> JOptionPane.showMessageDialog(this, "Hãy chọn một hóa đơn để xem chi tiết"));
        row2.add(btnDetail, "height 32!");
        p.add(row2, "growx");
        return p;
    }

    // ── Table ─────────────────────────────────────────────────────
    private JScrollPane buildTable() {
        Object[] cols = {"Mã HĐ","Ngày mua","Tên khách hàng","Mã KM","Tổng tiền","Nhân viên","Ghi chú"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        generalTable = new JTable(model);
        generalTable.setRowHeight(36);
        generalTable.setShowGrid(false);
        generalTable.setFillsViewportHeight(true);
        generalTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        generalTable.putClientProperty(FlatClientProperties.STYLE,
            "rowHeight:36;showHorizontalLines:true;intercellSpacing:0,1;" +
            "cellFocusColor:$TableHeader.hoverBackground;" +
            "selectionBackground:$TableHeader.hoverBackground;" +
            "selectionInactiveBackground:$TableHeader.hoverBackground;" +
            "selectionForeground:$Table.foreground;");
        generalTable.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(generalTable) {
            protected int getAlignment() { return SwingConstants.CENTER; }
        });
        generalTable.getTableHeader().putClientProperty(FlatClientProperties.STYLE,
            "height:34;hoverBackground:null;pressedBackground:null;" +
            "separatorColor:$TableHeader.background;font:bold +1;");

        int[] widths = {60,110,180,70,120,155,180};
        for (int i = 0; i < widths.length; i++)
            generalTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < cols.length - 1; i++)
            generalTable.getColumnModel().getColumn(i).setCellRenderer(center);

        for (dtohoadon hd : bushd.dshd) model.addRow(hd.toTableRow());

        generalTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { onRowClick(); }
        });

        JScrollPane scroll = new JScrollPane(generalTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
            "trackArc:$ScrollBar.thumbArc;thumbInsets:0,2,0,2;width:7;");
        scroll.getHorizontalScrollBar().putClientProperty(FlatClientProperties.STYLE,
            "trackArc:$ScrollBar.thumbArc;thumbInsets:0,2,0,2;width:7;");
        return scroll;
    }

    // ── Detail dialog ─────────────────────────────────────────────
    private void showDetailDialog(int hdId) {
        JDialog dlg = new JDialog((JFrame)null, "Chi tiết Hóa đơn #" + hdId, true);
        dlg.setUndecorated(true);
        dlg.setSize(880, 680);
        dlg.setShape(new RoundRectangle2D.Double(0,0,880,680,24,24));
        dlg.setLayout(new BorderLayout());

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(ACCENT);
        titleBar.setPreferredSize(new Dimension(880, 48));
        titleBar.setBorder(BorderFactory.createEmptyBorder(0,20,0,0));
        JLabel lbl = new JLabel("Chi tiết Hóa đơn  #" + hdId);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 15f));
        lbl.setForeground(Color.WHITE);
        JButton btnX = closeButton();
        btnX.addActionListener(e -> dlg.dispose());
        titleBar.add(lbl, BorderLayout.CENTER);
        titleBar.add(btnX, BorderLayout.EAST);
        dlg.add(titleBar, BorderLayout.NORTH);

        JPanel content = new JPanel(new MigLayout(
            "fill, insets 20 24 20 24", "[fill,grow 42][20!][fill,grow 58]", "[fill]"));
        content.setBackground(Color.WHITE);
        content.add(buildInfoPanel(hdId), "grow");
        content.add(new JLabel(), "");
        content.add(buildDetailTablePanel(hdId), "grow");
        dlg.add(content, BorderLayout.CENTER);

        JPanel bot = new JPanel(new MigLayout("insets 10 24 14 24,fillx","push[]","[]"));
        bot.setBackground(Color.WHITE);
        bot.setBorder(BorderFactory.createMatteBorder(1,0,0,0,BORDER));
        JButton btnClose = outlineButton("Đóng");
        btnClose.addActionListener(e -> dlg.dispose());
        bot.add(btnClose, "width 100!, height 32!");
        dlg.add(bot, BorderLayout.SOUTH);

        dlg.setLocationRelativeTo(null);
        dlg.setVisible(true);
    }

    private JPanel buildInfoPanel(int hdId) {
        dtohoadon hd = bushd.get(hdId);
        JPanel p = new JPanel(new MigLayout("fillx,wrap,insets 0","[fill]","[]12[]12[]12[]12[]12[]12[]"));
        p.setOpaque(false);
        p.add(sectionHeader("Thông tin hóa đơn"));
        p.add(infoRow("Mã hóa đơn", String.valueOf(hdId), false));
        p.add(infoRow("Ngày mua",   String.valueOf(hd.getNgayMua()), false));
        p.add(sectionHeader("Khách hàng"));
        p.add(splitRow("Mã KH",String.valueOf(hd.getMaKhachHang()),"Họ tên",String.valueOf(hd.getTenkhachhang())));
        p.add(sectionHeader("Nhân viên"));
        p.add(splitRow("Mã NV",String.valueOf(hd.getMaNhanVien()),"Họ tên",String.valueOf(hd.getTennhanvien())));
        p.add(sectionHeader("Khuyến mãi & Tích điểm"));
        int kmId = hd.getMaKhuyenMai();
        String kmName = (kmId==0)?"—":new buskhuyenmai().getkmbyid(kmId).getTenKhuyenMai();
        p.add(splitRow("Mã KM",String.valueOf(kmId==0?"—":kmId),"Tên KM",kmName));
        int tdId = hd.getMaTichDiem();
        String diem = (tdId==0)?"—":String.valueOf(new bustichdiem().get(tdId).getDiemTichLuy());
        p.add(splitRow("Mã TD",String.valueOf(tdId==0?"—":tdId),"Điểm tích lũy",diem));
        p.add(sectionHeader("Ghi chú"));
        JTextArea note = new JTextArea(String.valueOf(hd.getGhiChu()));
        note.setEditable(false); note.setWrapStyleWord(true); note.setLineWrap(true);
        p.add(new JScrollPane(note), "height 70!, growx");
        return p;
    }

    private JPanel buildDetailTablePanel(int hdId) {
        JPanel p = new JPanel(new MigLayout("fill,insets 0,wrap","[fill]","[shrink 0][fill,grow][shrink 0]"));
        p.setOpaque(false);
        p.add(sectionHeader("Chi tiết sản phẩm"), "growx");
        Object[] cols = {"Mã SP","Tên sản phẩm","Đơn giá","SL","Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(cols,0){
            @Override public boolean isCellEditable(int r,int c){return false;}
        };
        JTable tbl = new JTable(model);
        tbl.setRowHeight(32); tbl.setFillsViewportHeight(true);
        tbl.putClientProperty(FlatClientProperties.STYLE,
            "rowHeight:32;showHorizontalLines:true;intercellSpacing:0,1;" +
            "selectionBackground:$TableHeader.hoverBackground;selectionForeground:$Table.foreground;");
        tbl.getTableHeader().putClientProperty(FlatClientProperties.STYLE,
            "height:32;separatorColor:$TableHeader.background;font:bold;");
        int[] ws={55,165,95,50,110};
        for(int i=0;i<ws.length;i++) tbl.getColumnModel().getColumn(i).setPreferredWidth(ws[i]);
        DefaultTableCellRenderer center=new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i:new int[]{0,2,3,4}) tbl.getColumnModel().getColumn(i).setCellRenderer(center);
        buscthd.getlistbyhoadon(hdId);
        for(dtocthoadon ct:buscthd.dscthd) model.addRow(ct.toTableRow());
        double total=0;
        for(int i=0;i<model.getRowCount();i++){
            Object val=model.getValueAt(i,4);
            if(val!=null){try{total+=Double.parseDouble(val.toString().replaceAll("[^\\d.]",""));}catch(NumberFormatException ignored){}}
        }
        JScrollPane scroll=new JScrollPane(tbl);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER,1,true));
        scroll.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE,
            "trackArc:$ScrollBar.thumbArc;thumbInsets:0,2,0,2;width:7;");
        p.add(scroll,"grow");
        JLabel lblTotal=new JLabel(String.format("Tổng cộng:  %,.0f \u20ab",total));
        lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD,14f));
        lblTotal.setForeground(DANGER);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        p.add(lblTotal,"growx,gapy 8 0");
        return p;
    }

    // ── Filter logic ──────────────────────────────────────────────
    private void applyFilters() {
        String kw    = txtSearch.getText().trim();
        Date   dFrom = dateFrom.getDate();
        Date   dTo   = dateTo.getDate() != null ? dateTo.getDate() : new Date();

        // Normalize dateTo to end-of-day
        Calendar cal = Calendar.getInstance();
        cal.setTime(dTo);
        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59); cal.set(Calendar.SECOND, 59);
        final Date finalTo   = cal.getTime();
        final Date finalFrom = dFrom;

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(generalTable.getModel());
        generalTable.setRowSorter(sorter);
        List<RowFilter<TableModel,Object>> filters = new ArrayList<>();

        if (!kw.isEmpty()) {
            filters.add(kw.chars().allMatch(Character::isDigit)
                ? RowFilter.regexFilter(kw, 0)
                : RowFilter.regexFilter("(?i)" + kw, 2, 5));
        }

        // Date filter — chỉ bật khi dateFrom đã được chọn
        if (finalFrom != null) {
            filters.add(new RowFilter<TableModel,Object>() {
                @Override public boolean include(Entry<? extends TableModel,? extends Object> entry) {
                    Object val = entry.getValue(1);
                    Date rowDate = null;
                    if (val instanceof Date) {
                        rowDate = (Date) val;
                    } else if (val != null) {
                        for (String fmt : new String[]{"yyyy-MM-dd","dd/MM/yyyy","MM/dd/yyyy"}) {
                            try { rowDate = new java.text.SimpleDateFormat(fmt).parse(val.toString().substring(0,10)); break; }
                            catch (Exception ignored) {}
                        }
                    }
                    if (rowDate == null) return false;
                    return !rowDate.before(finalFrom) && !rowDate.after(finalTo);
                }
            });
        }

        sorter.setRowFilter(filters.isEmpty() ? null
            : filters.size() == 1 ? filters.get(0)
            : RowFilter.andFilter(filters));

        if (generalTable.getRowCount() == 0 && (!kw.isEmpty() || finalFrom != null))
            JOptionPane.showMessageDialog(this,"Không tìm thấy kết quả phù hợp.","Thông báo",JOptionPane.INFORMATION_MESSAGE);
    }

    private void onRowClick() {
        int row = generalTable.getSelectedRow();
        if (row < 0) return;
        int hdId = (int) generalTable.getValueAt(row, 0);
        for (ActionListener al : btnDetail.getActionListeners()) btnDetail.removeActionListener(al);
        btnDetail.setEnabled(true);
        btnDetail.addActionListener(e -> showDetailDialog(hdId));
    }

    private void reloadTable() {
        txtSearch.setText(""); dateFrom.setDate(null); dateTo.setDate(new Date());
        generalTable.setRowSorter(null);
        DefaultTableModel m = (DefaultTableModel) generalTable.getModel();
        m.setRowCount(0); bushd.getlist();
        for (dtohoadon hd : bushd.dshd) m.addRow(hd.toTableRow());
        btnDetail.setEnabled(false);
    }

    // ── UI helpers ────────────────────────────────────────────────
    private JDateChooser makeDateChooser(Date initial) {
        JDateChooser dc = new JDateChooser();
        dc.setDateFormatString("dd/MM/yyyy"); dc.setDate(initial);
        ((JTextField) dc.getDateEditor().getUiComponent()).setEditable(false);
        dc.putClientProperty(FlatClientProperties.STYLE, "arc:8;");
        return dc;
    }

    private JLabel makeLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.PLAIN,12f)); l.setForeground(TEXT_MUTED); return l;
    }

    private JLabel sectionHeader(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD,12f)); lbl.setForeground(ACCENT);
        lbl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0,0,1,0,new Color(199,210,254)),
            BorderFactory.createEmptyBorder(0,0,4,0)));
        return lbl;
    }

    private JPanel infoRow(String label, String value, boolean editable) {
        JPanel p = new JPanel(new MigLayout("fillx,insets 0","[110!][fill]","[]")); p.setOpaque(false);
        JTextField tf = new JTextField(value); tf.setEditable(editable);
        tf.putClientProperty(FlatClientProperties.STYLE,
            "arc:8;borderWidth:1;"+(editable?" focusedBorderColor:#6366f1;":" background:$Panel.background;"));
        p.add(makeLabel(label)); p.add(tf,"growx,height 32!"); return p;
    }

    private JPanel splitRow(String l1,String v1,String l2,String v2) {
        JPanel p = new JPanel(new MigLayout("fillx,insets 0","[80!][fill][90!][fill]","[]")); p.setOpaque(false);
        for (String[] pair : new String[][]{{l1,v1},{l2,v2}}) {
            JTextField tf = new JTextField(pair[1]); tf.setEditable(false);
            tf.putClientProperty(FlatClientProperties.STYLE,"arc:8;borderWidth:1;background:$Panel.background;");
            p.add(makeLabel(pair[0])); p.add(tf,"growx,height 32!");
        }
        return p;
    }

    private JButton accentButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.putClientProperty(FlatClientProperties.STYLE,
            "background:"+String.format("#%02x%02x%02x",bg.getRed(),bg.getGreen(),bg.getBlue())+
            ";foreground:#ffffff;borderWidth:0;arc:8;focusWidth:0;innerFocusWidth:0;");
        btn.setFocusPainted(false); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return btn;
    }

    private JButton outlineButton(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty(FlatClientProperties.STYLE,"arc:8;borderWidth:1;focusWidth:0;");
        btn.setFocusPainted(false); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return btn;
    }

    private JButton closeButton() {
        JButton btn = new JButton("✕");
        btn.setPreferredSize(new Dimension(48,48));
        btn.putClientProperty(FlatClientProperties.STYLE,
            "background:#ef4444;foreground:#ffffff;borderWidth:0;arc:0;focusWidth:0;");
        btn.setFont(btn.getFont().deriveFont(Font.BOLD,13f));
        btn.setFocusPainted(false); btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){btn.setBackground(new Color(220,38,38));}
            public void mouseExited(MouseEvent e) {btn.setBackground(new Color(239,68,68));}
        });
        return btn;
    }
}