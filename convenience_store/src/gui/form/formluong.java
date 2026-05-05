package gui.form;

import bus.busnhanvien;
import bus.bushopdong;
import bus.busluong;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import dao.daoluong;
import dto.dtohopdong;
import dto.dtoluong;
import dto.dtonhanvien;
import gui.modal.ModalDialog;
import gui.modal.component.SimpleModalBorder;
import gui.modal.option.Location;
import gui.modal.option.Option;
import gui.simple.SimpleInputSalaryForm;
import gui.table.CheckBoxTableHeaderRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.miginfocom.swing.MigLayout;
import java.awt.Color;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class formluong extends JPanel {

    // ─── Constants ────────────────────────────────────────────────
    private static final Color COLOR_PRIMARY    = new Color(67, 97, 238);
    private static final Color COLOR_SUCCESS    = new Color(34, 197, 94);
    private static final Color COLOR_WARNING    = new Color(251, 191, 36);
    private static final Color COLOR_DANGER     = new Color(239, 68, 68);
    private static final Color COLOR_CARD_BG    = UIManager.getColor("Panel.background");

    private static final DecimalFormat MONEY_FMT = new DecimalFormat("#,###");

    // ─── Fields ───────────────────────────────────────────────────
    private JTable table;
    private DefaultTableModel model;
    private busluong busLuong;
    private busnhanvien busnv = new busnhanvien();
    private bushopdong  bushd = new bushopdong();
    private JComboBox<String> cboMonth;
    private JComboBox<Integer> cboYear;
    private int lastSelectedMonth = -1;  // Track month change for auto-creation
    private int lastSelectedYear = -1;   // Track year change for auto-creation

    // Stat labels (updated after load)
    private JLabel lblTotalSalary, lblTotalBonus, lblTotalTax, lblRowCount;

    public formluong() {
        busLuong = new busluong();
        init();
    }

    // ─────────────────────────────────────────────────────────────
    //  INIT
    // ─────────────────────────────────────────────────────────────
    private void init() {
        setLayout(new BorderLayout());
        putClientProperty(FlatClientProperties.STYLE, "background:$Panel.background;");

        JPanel mainPanel = new JPanel(new MigLayout(
                "fillx, wrap, insets 20 24 20 24", "[fill]", "[]12[]12[]0[fill,grow]"));
        mainPanel.putClientProperty(FlatClientProperties.STYLE, "background:$Panel.background;");

        mainPanel.add(buildPageHeader());
        mainPanel.add(buildStatCards());
        mainPanel.add(buildToolbar());
        mainPanel.add(buildTablePanel());

        add(mainPanel, BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────
    //  PAGE HEADER
    // ─────────────────────────────────────────────────────────────
    private JPanel buildPageHeader() {
        JPanel p = new JPanel(new MigLayout("fillx, insets 0", "[]push[]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background:null;");

        JLabel title = new JLabel("Quản lý Bảng Lương");
        title.putClientProperty(FlatClientProperties.STYLE,
                "font:bold +8; foreground:$Label.foreground;");

        // Breadcrumb
        JLabel breadcrumb = new JLabel("Trang chủ  /  Nhân sự  /  Bảng lương");
        breadcrumb.putClientProperty(FlatClientProperties.STYLE,
                "font:-1; foreground:$Label.disabledForeground;");

        JPanel left = new JPanel(new MigLayout("fillx, wrap, insets 0", "[fill]", "[]4[]"));
        left.putClientProperty(FlatClientProperties.STYLE, "background:null;");
        left.add(title);
        left.add(breadcrumb);

        // Badge tháng hiện tại
        Calendar now = Calendar.getInstance();
        JLabel badge = new JLabel(String.format("  Tháng %02d / %d  ",
                now.get(Calendar.MONTH) + 1, now.get(Calendar.YEAR)));
        badge.putClientProperty(FlatClientProperties.STYLE,
                "font:bold -1; background:" + colorHex(COLOR_PRIMARY)
            + "; foreground:#FFFFFF;");
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(4, 12, 4, 12));

        p.add(left);
        p.add(badge, "aligny center");
        return p;
    }

    // ─────────────────────────────────────────────────────────────
    //  STAT CARDS
    // ─────────────────────────────────────────────────────────────
    private JPanel buildStatCards() {
        JPanel p = new JPanel(new MigLayout("fillx, insets 0", "[fill,25%][fill,25%][fill,25%][fill,25%]", "[]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background:null;");

        lblRowCount    = new JLabel("0");
        lblTotalSalary = new JLabel("0 ₫");
        lblTotalBonus  = new JLabel("0 ₫");
        lblTotalTax    = new JLabel("0 ₫");

        p.add(buildCard("Số nhân viên",  lblRowCount,    "👤", COLOR_PRIMARY), "gapright 12");
        p.add(buildCard("Tổng thực lãnh", lblTotalSalary, "💰", COLOR_SUCCESS), "gapright 12");
        p.add(buildCard("Tổng thưởng",    lblTotalBonus,  "🏆", COLOR_WARNING), "gapright 12");
        p.add(buildCard("Tổng thuế",      lblTotalTax,    "📋", COLOR_DANGER));

        return p;
    }

    private JPanel buildCard(String title, JLabel valueLabel, String icon, Color accent) {
        JPanel card = new JPanel(new MigLayout("fillx, wrap, insets 16 20 16 20", "[fill]", "[]6[]6[]"));
        card.putClientProperty(FlatClientProperties.STYLE,
                "background:$Table.background; arc:12; "
                + "border:2,2,2,2,$Table.gridColor;");

        JLabel iconLabel = new JLabel(icon + "  " + title);
        iconLabel.putClientProperty(FlatClientProperties.STYLE,
                "font:-1; foreground:$Label.disabledForeground;");

        valueLabel.putClientProperty(FlatClientProperties.STYLE,
                "font:bold +4; foreground:" + colorHex(accent) + ";");

        // Accent bar at top
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(0, 3));
        accentBar.putClientProperty(FlatClientProperties.STYLE, "arc:6;");

        card.add(accentBar, "height 3!, gapbottom 8");
        card.add(iconLabel);
        card.add(valueLabel);

        return card;
    }

    // ─────────────────────────────────────────────────────────────
    //  TOOLBAR
    // ─────────────────────────────────────────────────────────────
    private JPanel buildToolbar() {
        JPanel p = new JPanel(new MigLayout("fillx, insets 12 16 12 16", "[fill,220]16[]16[]push[]8[]8[]", "[]"));
        p.putClientProperty(FlatClientProperties.STYLE,
                "background:$Table.background; arc:12;"
                + "border:1,1,1,1,$Table.gridColor;");

        // Search
        JTextField txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm theo mã hoặc tên nhân viên...");
//        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON,
//                new FlatSVGIcon("source/image/icon/search.svg", 0.4f));
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "arc:8;");

        // Month combo
        cboMonth = new JComboBox<>();
        for (int i = 1; i <= 12; i++) cboMonth.addItem(String.format("Tháng %02d", i));
        // Set to current month
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        cboMonth.setSelectedIndex(currentMonth - 1);
        lastSelectedMonth = currentMonth;
        cboMonth.putClientProperty(FlatClientProperties.STYLE, "arc:8;");
        cboMonth.setPreferredSize(new Dimension(110, 32));

        // Year combo
        cboYear = new JComboBox<>();
        int yr = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = yr - 5; i <= yr + 1; i++) cboYear.addItem(i);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        cboYear.setSelectedItem(currentYear);
        lastSelectedYear = currentYear;
        cboYear.putClientProperty(FlatClientProperties.STYLE, "arc:8;");
        cboYear.setPreferredSize(new Dimension(80, 32));

        // Buttons
        JButton btnCreate = createButton("＋ Tạo bảng lương", COLOR_PRIMARY, Color.WHITE);
        JButton btnEdit   = createButton("✏ Chỉnh sửa", null, null);
        JButton btnExport = createButton("⬇ Xuất Excel", COLOR_SUCCESS, Color.WHITE);

        // Events
        cboMonth.addActionListener(e -> {
            try {
                handleMonthYearChange();
            } catch (SQLException ex) {
                Logger.getLogger(formluong.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        cboYear.addActionListener(e -> {
            try {
                handleMonthYearChange();
            } catch (SQLException ex) {
                Logger.getLogger(formluong.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filterByText(txtSearch.getText()); }
            public void removeUpdate(DocumentEvent e)  { filterByText(txtSearch.getText()); }
            public void changedUpdate(DocumentEvent e) { filterByText(txtSearch.getText()); }
        });

        btnCreate.addActionListener(e -> {
            try { create(); } catch (SQLException ex) {
                showError("Lỗi khi tạo bảng lương: " + ex.getMessage());
            }
        });

        btnEdit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) { showWarning("Vui lòng chọn dòng cần chỉnh sửa."); return; }
            int maLuong = (int) model.getValueAt(table.convertRowIndexToModel(row), 1);
            int maNhanVien = (int) model.getValueAt(table.convertRowIndexToModel(row), 11); // MNV column
            try { showEditModal(maLuong, maNhanVien); } catch (SQLException ex) {
                Logger.getLogger(formluong.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        btnExport.addActionListener(e -> exportToExcel());

        p.add(txtSearch);
        p.add(cboMonth);
        p.add(cboYear);
        p.add(btnCreate);
        p.add(btnEdit);
        p.add(btnExport);

        return p;
    }

    private JButton createButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        if (bg != null) {
            btn.setBackground(bg);
            btn.setForeground(fg);
            btn.putClientProperty(FlatClientProperties.STYLE,
                    "background:" + colorHex(bg) + "; foreground:" + colorHex(fg)
                    + "; arc:8; font:bold;");
        } else {
            btn.putClientProperty(FlatClientProperties.STYLE, "arc:8;");
        }
        return btn;
    }

    // ─────────────────────────────────────────────────────────────
    //  TABLE PANEL
    // ─────────────────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.putClientProperty(FlatClientProperties.STYLE,
                "background:$Table.background; arc:12; "
                + "border:1,1,1,1,$Table.gridColor;");

        // Model
        Object[] columns = {
            "☑", "Mã lương", "Tên nhân viên", "Phụ cấp",
            "Lương thực tế", "Lương thưởng", "Bảo hiểm",
            "Thuế", "Thực lãnh", "Làm thêm", "Ngày nhận lương",
            "MNV" // hidden
        };

        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 0; }
            @Override public Class<?> getColumnClass(int c) {
                return c == 0 ? Boolean.class : super.getColumnClass(c);
            }
        };

        table = new JTable(model);
        styleTable();
        loadDataToTable();
        
        // Double-click listener để sửa
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int maLuong = (int) model.getValueAt(table.convertRowIndexToModel(row), 1);
                        int maNhanVien = (int) model.getValueAt(table.convertRowIndexToModel(row), 11); // MNV column
                        try {
                            showEditModal(maLuong, maNhanVien);
                        } catch (SQLException ex) {
                            Logger.getLogger(formluong.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().putClientProperty(
                FlatClientProperties.STYLE, "trackArc:$ScrollBar.thumbArc;");

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private void styleTable() {
        // Column widths - chỉ set nếu column tồn tại
        try {
            table.getColumnModel().getColumn(0).setMaxWidth(40);
            table.getColumnModel().getColumn(0).setMinWidth(40);
            table.getColumnModel().getColumn(1).setPreferredWidth(80);
            if (table.getColumnCount() > 2) 
                table.getColumnModel().getColumn(2).setPreferredWidth(160);
            
            // Hide columns 11, 12
            if (table.getColumnCount() > 11) {
                table.getColumnModel().getColumn(11).setMinWidth(0);
                table.getColumnModel().getColumn(11).setMaxWidth(0);
                table.getColumnModel().getColumn(11).setPreferredWidth(0);
            }
            if (table.getColumnCount() > 12) {
                table.getColumnModel().getColumn(12).setMinWidth(0);
                table.getColumnModel().getColumn(12).setMaxWidth(0);
                table.getColumnModel().getColumn(12).setPreferredWidth(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // FlatLaf styles
        table.putClientProperty(FlatClientProperties.STYLE,
                "rowHeight:52; showHorizontalLines:true; "
                + "selectionBackground:$Table.selectionBackground;");
        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE,
                "height:38; hoverBackground:null; font:bold;");
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Money formatter for numeric columns
        DefaultTableCellRenderer moneyRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean focus, int row, int col) {
                if (val instanceof Number) {
                    val = MONEY_FMT.format(((Number) val).doubleValue()) + " ₫";
                }
                super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                setHorizontalAlignment(RIGHT);
                return this;
            }
        };
        for (int col : new int[]{3, 4, 5, 6, 7, 8, 9}) {
            if (col < table.getColumnCount()) {
                table.getColumnModel().getColumn(col).setCellRenderer(moneyRenderer);
            }
        }

        // Checkbox header
        if (table.getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setHeaderRenderer(
                    new CheckBoxTableHeaderRenderer(table, 0));
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  DATA LOADING
    // ─────────────────────────────────────────────────────────────
    private void loadDataToTable() {
        model.setRowCount(0);
        daoluong dao = new daoluong();
        ArrayList<dtoluong> list = dao.getList();

        double sumSalary = 0, sumBonus = 0, sumTax = 0;

        for (dtoluong l : list) {
            String tenNV = dao.getTenNhanVienById(l.getMaNhanVien());
            model.addRow(new Object[]{
                false,
                l.getMaLuong(),
                tenNV,
                l.getPhuCap(),
                l.getLuongThucTe(),
                l.getLuongThuong(),
                l.getKhoanBaoHiem(),
                l.getKhoanThue(),
                l.getThuclanh(),
                l.getLuongLamThem(),
                l.getNgayNhanLuong(),
                l.getMaNhanVien()
            });
            sumSalary += l.getThuclanh();
            sumBonus  += l.getLuongThuong();
            sumTax    += l.getKhoanThue();
        }

        // Update stat cards
        lblRowCount.setText(String.valueOf(list.size()));
        lblTotalSalary.setText(MONEY_FMT.format(sumSalary) + " ₫");
        lblTotalBonus.setText(MONEY_FMT.format(sumBonus) + " ₫");
        lblTotalTax.setText(MONEY_FMT.format(sumTax) + " ₫");
    }

    // ─────────────────────────────────────────────────────────────
    //  AUTO-CREATE SALARY TABLE FOR NEW MONTH
    // ─────────────────────────────────────────────────────────────
    private void handleMonthYearChange() throws SQLException {
        String monthStr = (String) cboMonth.getSelectedItem();
        int selectedMonth = Integer.parseInt(monthStr.replace("Tháng", "").trim());
        int selectedYear = (Integer) cboYear.getSelectedItem();

        // Check if month/year actually changed
        if (selectedMonth != lastSelectedMonth || selectedYear != lastSelectedYear) {
            lastSelectedMonth = selectedMonth;
            lastSelectedYear = selectedYear;

            // Filter by selected month/year
            filterByMonthYear(monthStr, selectedYear);

            // Auto-create salary table if it doesn't exist for this month/year
            Calendar cal = Calendar.getInstance();
            cal.set(selectedYear, selectedMonth - 1, 1);
            Date selectedDate = cal.getTime();

            if (!busLuong.isExist(selectedDate)) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bảng lương cho tháng " + selectedMonth + "/" + selectedYear + " chưa tồn tại.\nCó muốn tạo mới không?",
                        "Tạo bảng lương",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    create();
                }
            }
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  FILTERING
    // ─────────────────────────────────────────────────────────────
    private void filterByMonthYear(String monthStr, Integer year) {
        if (monthStr == null || year == null) return;
        int month;
        try { month = Integer.parseInt(monthStr.split(" ")[1]); }
        catch (Exception e) { return; }

        // Tạo sorter mới trên model hiện tại
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                // Truy cập column 10 (Ngày nhận lương)
                Object dateObj = entry.getModel().getValueAt(entry.getIdentifier(), 10);
                if (dateObj == null) return false;
                
                String date = dateObj.toString();
                if (date.isEmpty()) return false;
                
                try {
                    String[] p = date.split("-");
                    if (p.length < 2) return false;
                    return Integer.parseInt(p[0]) == year && Integer.parseInt(p[1]) == month;
                } catch (Exception e) { return false; }
            }
        });
    }

    private void filterByText(String query) {
        if (query == null || query.trim().isEmpty()) {
            table.setRowSorter(null);
            return;
        }
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        // Column 2 = Tên nhân viên, Column 1 = Mã lương
        filters.add(RowFilter.regexFilter("(?i)" + query, 2));
        filters.add(RowFilter.regexFilter("(?i)" + query, 1));
        
        sorter.setRowFilter(RowFilter.orFilter(filters));
    }

    // ─────────────────────────────────────────────────────────────
    //  CREATE
    // ─────────────────────────────────────────────────────────────
    private void create() throws SQLException {
        Date day = new Date();

        if (busLuong.isExist(day)) {
            showWarning("Bảng lương tháng này đã tồn tại!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tạo bảng lương cho tháng hiện tại?", "Xác nhận tạo bảng lương",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        int count = busLuong.countLuong();

        // Lấy danh sách nhân viên còn hoạt động
        ArrayList<dtonhanvien> listNV = busnv.getList();
        
        Date today = new Date();
        for (dtonhanvien nv : listNV) {
            // Chỉ tính lương cho nhân viên còn hoạt động
            if (nv.getIsdelete() == 0 && (nv.getNgayketthuc() == null || nv.getNgayketthuc().after(today))) {
                int manv = nv.getManhanvien();
                dtohopdong hd = bushd.gethdnhanvien(manv);
                if (hd != null) {
                    double luongCoBan  = hd.getLuongCoBan();
                    double phuCap      = 0;
                    double luongThucTe = luongCoBan;  // Mặc định: lương thực tế = lương cơ bản
                    double luongLamThem = 0;  // Mặc định: 0
                    double luongThuong = 0;  // Mặc định: 0
                    double khoanBH = 0;  // Mặc định: 0
                    double khoanTru = 0;  // Mặc định: 0
                    double khoanThue   = busLuong.calculateThue(luongThucTe, phuCap, luongThuong, khoanBH, luongLamThem, khoanTru);
                    double thucLanh    = busLuong.calculateLuong(luongThucTe, phuCap, luongThuong, khoanBH, luongLamThem, khoanTru) - khoanThue;

                    dtoluong luong = new dtoluong(count++, phuCap,
                            luongThucTe, luongThuong, khoanBH, khoanThue,
                            thucLanh, luongLamThem, day, 0);
                    busLuong.addLuong(luong);
                }
            }
        }

        JOptionPane.showMessageDialog(this, "✅ Bảng lương đã được tạo thành công!",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
        loadDataToTable();
    }

    // ─────────────────────────────────────────────────────────────
    //  EDIT MODAL
    // ─────────────────────────────────────────────────────────────
    private void showEditModal(int maLuong, int maNhanVien) throws SQLException {
        dtoluong luong = busLuong.getLuongByIdAndEmployee(maLuong, maNhanVien);
        if (luong == null) {
            showError("Không tìm thấy bản ghi lương #" + maLuong + " của nhân viên #" + maNhanVien);
            return;
        }

        Option option = ModalDialog.createOption();
        option.getLayoutOption().setSize(-1, 1f).setLocation(Location.TRAILING, Location.TOP);

        SimpleInputSalaryForm form = new SimpleInputSalaryForm();
        form.setDefaultValues(luong);

        ModalDialog.showModal(this, new SimpleModalBorder(
                form, "Chỉnh sửa lương – " + maLuong, SimpleModalBorder.YES_NO_OPTION,
                (controller, action) -> {
                    if (action == SimpleModalBorder.YES_OPTION) {
                        int res = JOptionPane.showConfirmDialog(null,
                                "Xác nhận lưu thay đổi?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                        if (res == JOptionPane.YES_OPTION) {
                            try {
                                form.updateLuong(maLuong);
                                loadDataToTable();
                            } catch (Exception ex) {
                                showError("Lỗi khi cập nhật: " + ex.getMessage());
                            }
                        }
                    } else {
                        controller.close();
                    }
                }), option);
    }

    // ─────────────────────────────────────────────────────────────
    //  EXPORT EXCEL
    // ─────────────────────────────────────────────────────────────
    private void exportToExcel() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Lưu file Excel");
        fc.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));

        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        String path = fc.getSelectedFile().getAbsolutePath();
        if (!path.endsWith(".xlsx")) path += ".xlsx";

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Bảng lương");

            // Collect visible columns (hide checkbox, MCC, MNV)
            Set<String> hiddenCols = Set.of("☑", "MCC", "MNV");
            List<Integer> visCols = new ArrayList<>();
            for (int c = 0; c < table.getColumnCount(); c++) {
                if (!hiddenCols.contains(table.getColumnName(c))) visCols.add(c);
            }

            // Header style
            CellStyle headerStyle = wb.createCellStyle();
            org.apache.poi.ss.usermodel.Font hFont = wb.createFont();
            hFont.setBold(true);
            headerStyle.setFont(hFont);
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row hRow = sheet.createRow(0);
            int col = 0;
            for (int vc : visCols) {
                Cell cell = hRow.createCell(col++);
                cell.setCellValue(table.getColumnName(vc));
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            for (int r = 0; r < table.getRowCount(); r++) {
                Row dRow = sheet.createRow(r + 1);
                col = 0;
                for (int vc : visCols) {
                    Object val = table.getValueAt(r, vc);
                    Cell cell = dRow.createCell(col++);
                    if (val instanceof Number) cell.setCellValue(((Number) val).doubleValue());
                    else if (val != null) cell.setCellValue(val.toString());
                }
            }

            for (int c = 0; c < visCols.size(); c++) sheet.autoSizeColumn(c);

            try (FileOutputStream fos = new FileOutputStream(path)) { wb.write(fos); }
            JOptionPane.showMessageDialog(this,
                    "✅ Xuất file thành công:\n" + path, "Thành công", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            showError("Lỗi khi xuất Excel: " + ex.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────────────────────────
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    /** Convert Color to CSS hex string for FlatLaf style strings. */
    private static String colorHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    // ─────────────────────────────────────────────────────────────
    //  MAIN (dev preview)
    // ─────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { com.formdev.flatlaf.FlatLightLaf.setup(); } catch (Exception ignored) {}
            JFrame frame = new JFrame("Quản lý lương");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1280, 800);
            frame.setLocationRelativeTo(null);
            frame.add(new formluong());
            frame.setVisible(true);
        });
    }
}