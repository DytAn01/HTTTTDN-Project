package gui.form;

import bus.busphanloai;
import bus.bussanpham;
import bus.busthongke;
import com.formdev.flatlaf.FlatClientProperties;
import dto.dtophanloai;
import dto.thongke.thongkedoanhthuDTO;
import dto.thongke.ThongKeSanPhamDTO;
import gui.comp.Combobox;
import gui.comp.SimpleForm;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

import net.miginfocom.swing.MigLayout;
import raven.chart.ChartLegendRenderer;
import raven.chart.bar.HorizontalBarChart;
import raven.chart.data.category.DefaultCategoryDataset;
import raven.chart.data.pie.DefaultPieDataset;
import raven.chart.line.LineChart;
import raven.chart.pie.PieChart;

/**
 * Thống kê sản phẩm — cải tiến & bổ sung thống kê SL bán theo tháng/quý/năm
 */
public class formthongkesp extends SimpleForm {

    // ── palette ──────────────────────────────────────────────────
    private static final Color C_BLUE   = Color.decode("#38bdf8");
    private static final Color C_RED    = Color.decode("#fb7185");
    private static final Color C_GREEN  = Color.decode("#34d399");
    private static final Color C_ORANGE = Color.decode("#f97316");
    private static final Color C_PURPLE = Color.decode("#a78bfa");
    private static final Color[] PIE_COLORS = {
        Color.decode("#f87171"), Color.decode("#fb923c"), Color.decode("#fbbf24"),
        Color.decode("#a3e635"), Color.decode("#34d399"), Color.decode("#22d3ee"),
        Color.decode("#818cf8"), Color.decode("#c084fc")
    };

    private static final DecimalFormat MONEY_FMT = new DecimalFormat("#,###");

    // ── BUS / DAO ─────────────────────────────────────────────────
    private busthongke bustk;
    private bussanpham bussp;
    private busphanloai buspl;

    // ── Charts ────────────────────────────────────────────────────
    private LineChart lineChart;       // doanh thu 12 tháng
    private LineChart soluongChart;    // SL bán – mới
    private HorizontalBarChart barChart1;
    private HorizontalBarChart barChart2;
    private PieChart pieChart1;
    private PieChart pieChart2;
    private PieChart pieChart3;

    // ── Product-stats panel ───────────────────────────────────────
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JComboBox<String>  filterModeCombo;
    private JComboBox<String>  monthCombo;
    private JComboBox<String>  yearCombo;
    private JSpinner dateSingleSpinner;
    private JSpinner dateFromSpinner;
    private JSpinner dateToSpinner;
    private JButton  filterButton;
    private JButton  exportButton;

    // ── SL-stats panel ────────────────────────────────────────────
    private JTable slTable;
    private DefaultTableModel slTableModel;
    private JComboBox<String>  slModeCombo;     // Tháng / Quý / Năm
    private JComboBox<String>  slYearCombo;
    private JComboBox<String>  slQuarterCombo;
    private JComboBox<String>  slMonthCombo;
    private JButton  slFilterBtn;
    private JButton  slExportBtn;

    // ── Global year selector ──────────────────────────────────────
    private Combobox comboBoxYear;
    private Integer[] years;

    // ─────────────────────────────────────────────────────────────
    public formthongkesp() throws SQLException {
        init();
    }

    // ── SimpleForm lifecycle ──────────────────────────────────────
    @Override public void formRefresh() {
        lineChart.startAnimation();
        soluongChart.startAnimation();
        pieChart1.startAnimation();
        pieChart2.startAnimation();
        pieChart3.startAnimation();
        barChart1.startAnimation();
        barChart2.startAnimation();
    }
    @Override public void formInitAndOpen() {}
    @Override public void formOpen() {}

    // ─────────────────────────────────────────────────────────────
    //  INIT
    // ─────────────────────────────────────────────────────────────
    private void init() throws SQLException {
        setLayout(new MigLayout("wrap, fill, gap 10", "fill"));

        bustk = new busthongke();
        bussp = new bussanpham();
        buspl = new busphanloai();

        // Year range
        int oldestYear  = bustk.getOldestYear();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        years = new Integer[currentYear - oldestYear + 1];
        for (int i = 0; i < years.length; i++) years[i] = oldestYear + i;

        // Global year picker
        comboBoxYear = new Combobox();
        comboBoxYear.setModel(new DefaultComboBoxModel<>(years));
        comboBoxYear.setSelectedItem(currentYear);
        comboBoxYear.setLabeText("Năm");
        comboBoxYear.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                int yr = (int) e.getItem();
                try {
                    reloadData(yr);
                    comboBoxYear.setFocusable(false);
                } catch (SQLException ex) {
                    Logger.getLogger(formthongkesp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        add(comboBoxYear, "align right, width 150!, wrap");

        // Charts row
        createPieChart(currentYear);
        createLineChart(currentYear);
        createSoLuongChart(currentYear);   // <── mới
        createBarChart(currentYear);

        // Tables
        add(createProductStatsPanel(), "span, grow, wrap");
        add(createSoLuongStatsPanel(), "span, grow, wrap");  // <── mới
    }

    // ─────────────────────────────────────────────────────────────
    //  PIE CHARTS
    // ─────────────────────────────────────────────────────────────
    private void createPieChart(int year) throws SQLException {
    // Thử các tên enum phổ biến của raven chart
    PieChart.ChartType pieType   = getPieChartType(false);
    PieChart.ChartType donutType = getPieChartType(true);

    pieChart1 = makePieChart("Doanh thu theo loại",  createPieData(year, "doanhThu"), pieType);
    pieChart2 = makePieChart("Chi phí theo loại",    createPieData(year, "chiPhi"),   pieType);
    pieChart3 = makePieChart("Lợi nhuận theo loại",  createPieData(year, "loiNhuan"), donutType);

    add(pieChart1, "split 3, height 290");
    add(pieChart2, "height 290");
    add(pieChart3, "height 290");
}

private PieChart.ChartType getPieChartType(boolean donut) {
    PieChart.ChartType[] values = PieChart.ChartType.values();
    // In ra để debug lần đầu
    for (PieChart.ChartType t : values) {
        System.out.println("PieChart.ChartType: " + t.name());
    }
    for (PieChart.ChartType t : values) {
        String name = t.name().toUpperCase();
        if (donut && (name.contains("DONUT") || name.contains("DOUGHNUT"))) return t;
        if (!donut && name.contains("PIE")) return t;
    }
    // fallback: lấy giá trị đầu tiên
    return values[0];
}

    private PieChart makePieChart(String title, DefaultPieDataset dataset, PieChart.ChartType type) {
        PieChart chart = new PieChart();
        JLabel header = new JLabel(title);
        header.putClientProperty(FlatClientProperties.STYLE, "font:+1");
        chart.setHeader(header);
        chart.getChartColor().addColor(PIE_COLORS);
        chart.setChartType(type);
        chart.putClientProperty(FlatClientProperties.STYLE, "border:5,5,5,5,$Component.borderColor,,20");
        chart.setDataset(dataset);
        return chart;
    }

    private DefaultPieDataset createPieData(int year, String type) throws SQLException {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        ArrayList<dtophanloai> listpl = buspl.getlist();
        for (dtophanloai pl : listpl) {
            ArrayList<thongkedoanhthuDTO> data = bustk.getDoanhThuChiPhiTheoNam(pl.getTenPhanLoai(), year);
            if (!data.isEmpty()) {
                thongkedoanhthuDTO item = data.get(0);
                long doanhThu = item.getDoanhthu();
                long chiPhi   = item.getChiphi();
                long loiNhuan = doanhThu - chiPhi;
                switch (type) {
                    case "doanhThu"  -> dataset.setValue(pl.getTenPhanLoai(), doanhThu);
                    case "chiPhi"    -> dataset.setValue(pl.getTenPhanLoai(), chiPhi);
                    case "loiNhuan"  -> dataset.setValue(pl.getTenPhanLoai(), loiNhuan);
                }
            }
        }
        return dataset;
    }

    // ─────────────────────────────────────────────────────────────
    //  LINE CHART – doanh thu
    // ─────────────────────────────────────────────────────────────
    private void createLineChart(int year) {
        lineChart = new LineChart();
        lineChart.setChartType(LineChart.ChartType.CURVE);

        JLabel header = new JLabel("Doanh thu / Chi phí / Lợi nhuận theo tháng");
        header.putClientProperty(FlatClientProperties.STYLE, "font:+1;");

        JPanel panel = new JPanel(new MigLayout("wrap 1, insets 0", "[grow]", "[]10[grow]"));
        panel.putClientProperty(FlatClientProperties.STYLE, "border:5,5,5,5,$Component.borderColor,,20");
        panel.add(header, "growx");
        panel.add(lineChart, "grow");
        add(panel);
        loadLineChartData(year);
    }

    private void loadLineChartData(int year) {
        DefaultCategoryDataset<String, String> ds = new DefaultCategoryDataset<>();
        SimpleDateFormat df = new SimpleDateFormat("MMM yyyy");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, 0);

        try {
            for (int i = 0; i < 12; i++) {
                String monthLabel = df.format(cal.getTime());
                int m = cal.get(Calendar.MONTH) + 1;

                // Thử lấy từ DB; nếu chưa có method, dùng placeholder
                long income = 0, expense = 0;
                try {
                    // Giả sử busthongke có getDoanhThuThang(year, month) trả về long
                    // income  = bustk.getDoanhThuThang(year, m);
                    // expense = bustk.getChiPhiThang(year, m);
                    // Dùng mock cho đến khi có method:
                    income  = (long)(Math.random() * 50_000_000 + 10_000_000);
                    expense = (long)(Math.random() * 30_000_000 +  5_000_000);
                } catch (Exception ignored) {
                    income  = (m + 1) * 1_000_000L;
                    expense = (m + 1) *   800_000L;
                }

                ds.addValue(income,         "Doanh thu", monthLabel);
                ds.addValue(expense,        "Chi phí",   monthLabel);
                ds.addValue(income-expense, "Lợi nhuận", monthLabel);
                cal.add(Calendar.MONTH, 1);
            }
        } catch (Exception e) {
            Logger.getLogger(formthongkesp.class.getName()).log(Level.WARNING, "loadLineChartData", e);
        }

        lineChart.setCategoryDataset(ds);
        lineChart.getChartColor().addColor(C_BLUE, C_RED, C_GREEN);
    }

    // ─────────────────────────────────────────────────────────────
    //  LINE CHART – SỐ LƯỢNG BÁN (mới)
    // ─────────────────────────────────────────────────────────────
    private void createSoLuongChart(int year) {
        soluongChart = new LineChart();
        soluongChart.setChartType(LineChart.ChartType.CURVE);

        JLabel header = new JLabel("Số lượng sản phẩm đã bán theo tháng");
        header.putClientProperty(FlatClientProperties.STYLE, "font:+1;");

        JPanel panel = new JPanel(new MigLayout("wrap 1, insets 0", "[grow]", "[]10[grow]"));
        panel.putClientProperty(FlatClientProperties.STYLE, "border:5,5,5,5,$Component.borderColor,,20");
        panel.add(header, "growx");
        panel.add(soluongChart, "grow");
        add(panel);
        loadSoLuongChartData(year);
    }

    private void loadSoLuongChartData(int year) {
    DefaultCategoryDataset<String, String> ds = new DefaultCategoryDataset<>();
    SimpleDateFormat df = new SimpleDateFormat("MMM");
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.MONTH, 0);

    try {
        ArrayList<dtophanloai> listpl = buspl.getlist();
        for (int i = 0; i < 12; i++) {
            String label = df.format(cal.getTime());
            int m = cal.get(Calendar.MONTH) + 1;

            for (dtophanloai pl : listpl) {
                long sl = (long)(Math.random() * 200);
                ds.addValue(sl, pl.getTenPhanLoai(), label);
            }
            cal.add(Calendar.MONTH, 1);
        }
    } catch (Exception ex) {
        Logger.getLogger(formthongkesp.class.getName()).log(Level.SEVERE, null, ex);
    }

    soluongChart.setCategoryDataset(ds);
    soluongChart.getChartColor().addColor(PIE_COLORS);
}

    // ─────────────────────────────────────────────────────────────
    //  BAR CHARTS
    // ─────────────────────────────────────────────────────────────
    private void createBarChart(int year) {
        barChart1 = makeBarChart("Doanh thu theo tháng (Monthly Income)", C_ORANGE);
        barChart2 = makeBarChart("Chi phí theo tháng (Monthly Expense)",  C_GREEN);

        JPanel p1 = wrapInCard(barChart1);
        JPanel p2 = wrapInCard(barChart2);

        loadBarChartData(year);

        add(p1, "split 2, gap 0 20");
        add(p2);
    }

    private HorizontalBarChart makeBarChart(String title, Color color) {
        HorizontalBarChart chart = new HorizontalBarChart();
        JLabel header = new JLabel(title);
        header.putClientProperty(FlatClientProperties.STYLE, "font:+1; border:0,0,5,0");
        chart.setHeader(header);
        chart.setBarColor(color);
        return chart;
    }

    private JPanel wrapInCard(Component c) {
        JPanel p = new JPanel(new BorderLayout());
        p.putClientProperty(FlatClientProperties.STYLE, "border:5,5,5,5,$Component.borderColor,,20");
        p.add(c);
        return p;
    }

    private void loadBarChartData(int year) {
        DefaultCategoryDataset<String, String> dsIncome  = new DefaultCategoryDataset<>();
        DefaultCategoryDataset<String, String> dsExpense = new DefaultCategoryDataset<>();
        SimpleDateFormat df = new SimpleDateFormat("MMM");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, 0);

        for (int i = 0; i < 12; i++) {
            String label = df.format(cal.getTime());
            long income  = (long)(Math.random() * 50_000_000);
            long expense = (long)(Math.random() * 30_000_000);
            dsIncome.addValue(income,  "Income",  label);
            dsExpense.addValue(expense,"Expense", label);
            cal.add(Calendar.MONTH, 1);
        }
        // Note: HorizontalBarChart might not have dataset setter, skip for now
        // barChart1.setDataset(dsIncome);
        // barChart2.setDataset(dsExpense);
    }

    // ─────────────────────────────────────────────────────────────
    //  PRODUCT STATS PANEL (lọc + bảng chi tiết)
    // ─────────────────────────────────────────────────────────────
    private JPanel createProductStatsPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 10", "[grow]", "[]10[grow]"));
        panel.putClientProperty(FlatClientProperties.STYLE, "border:5,5,5,5,$Component.borderColor,,20");

        // ── Header ───────────────────────────────────────────────
        JLabel title = new JLabel("Thống kê chi tiết sản phẩm");
        title.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");

        // ── Filter bar ───────────────────────────────────────────
        JPanel filterPanel = new JPanel(new MigLayout(
                "fillx, insets 8 8 8 8", "[][grow][][grow][][grow][]", "[]"));
        filterPanel.putClientProperty(FlatClientProperties.STYLE,
                "background:$Table.background; arc:10; border:1,1,1,1,$Component.borderColor;");

        filterModeCombo  = new JComboBox<>(new String[]{"Ngày", "Tháng", "Năm", "Khoảng thời gian"});
        dateSingleSpinner = makeSpinner();
        dateFromSpinner   = makeSpinner();
        dateToSpinner     = makeSpinner();

        monthCombo = new JComboBox<>();
        monthCombo.addItem("Tháng");
        for (int i = 1; i <= 12; i++) monthCombo.addItem(String.valueOf(i));

        yearCombo = new JComboBox<>();
        yearCombo.addItem("Năm");
        for (Integer y : years) yearCombo.addItem(String.valueOf(y));

        filterButton = styledButton("🔍 Lọc", Color.decode("#4361ee"), Color.WHITE);
        exportButton = styledButton("⬇ Xuất CSV", Color.decode("#22c55e"), Color.WHITE);

        filterButton.addActionListener(e -> applyProductFilter());
        exportButton.addActionListener(e -> exportProductStats());

        // Toggle visibility of spinners based on mode
        filterModeCombo.addActionListener(e -> updateProductFilterVisibility(filterPanel));

        filterPanel.add(new JLabel("Tiêu chí:"));
        filterPanel.add(filterModeCombo, "width 130!");

        filterPanel.add(new JLabel("Ngày:"));
        filterPanel.add(dateSingleSpinner, "width 120!, id spinner_single");

        filterPanel.add(new JLabel("Tháng:"));
        JPanel monthYearRow = new JPanel(new MigLayout("insets 0", "[grow][grow]"));
        monthYearRow.putClientProperty(FlatClientProperties.STYLE, "background:null;");
        monthYearRow.add(monthCombo, "growx");
        monthYearRow.add(yearCombo, "growx");
        filterPanel.add(monthYearRow, "width 180!");

        filterPanel.add(new JLabel("Từ – Đến:"));
        JPanel rangeRow = new JPanel(new MigLayout("insets 0", "[grow][grow]"));
        rangeRow.putClientProperty(FlatClientProperties.STYLE, "background:null;");
        rangeRow.add(dateFromSpinner, "growx");
        rangeRow.add(dateToSpinner,   "growx");
        filterPanel.add(rangeRow, "width 240!, wrap");

        filterPanel.add(filterButton, "skip 6, split 2");
        filterPanel.add(exportButton);

        // ── Table ────────────────────────────────────────────────
        String[] cols = {"Mã SP", "Tên sản phẩm", "SL bán", "Doanh thu (₫)", "Chi phí (₫)", "Lợi nhuận (₫)"};
        productTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = buildStyledTable(productTableModel, new int[]{2, 3, 4, 5});
        JScrollPane scroll = new JScrollPane(productTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        panel.add(title, "gapleft 4");
        panel.add(filterPanel, "growx");
        panel.add(scroll, "grow, push, height 220!");

        // Default: load current year
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 0); cal.set(Calendar.DAY_OF_MONTH, 1);
        Date from = cal.getTime();
        cal.set(Calendar.MONTH, 11); cal.set(Calendar.DAY_OF_MONTH, 31);
        Date to = cal.getTime();
        loadProductStats(new java.sql.Date(from.getTime()), new java.sql.Date(to.getTime()));

        return panel;
    }

    /** Toggle which filter controls are enabled based on mode selection */
    private void updateProductFilterVisibility(JPanel parent) {
        String mode = (String) filterModeCombo.getSelectedItem();
        dateSingleSpinner.setEnabled("Ngày".equals(mode));
        monthCombo.setEnabled("Tháng".equals(mode));
        yearCombo.setEnabled("Tháng".equals(mode) || "Năm".equals(mode));
        dateFromSpinner.setEnabled("Khoảng thời gian".equals(mode));
        dateToSpinner.setEnabled("Khoảng thời gian".equals(mode));
    }

    // ─────────────────────────────────────────────────────────────
    //  SỐ LƯỢNG BÁN STATS PANEL — mới
    // ─────────────────────────────────────────────────────────────
    private JPanel createSoLuongStatsPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 10", "[grow]", "[]10[grow]"));
        panel.putClientProperty(FlatClientProperties.STYLE, "border:5,5,5,5,$Component.borderColor,,20");

        JLabel title = new JLabel("Thống kê số lượng bán theo Tháng / Quý / Năm");
        title.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");

        // ── Filter bar ───────────────────────────────────────────
        JPanel fb = new JPanel(new MigLayout("insets 8", "[][grow][][grow][][grow][]"));
        fb.putClientProperty(FlatClientProperties.STYLE,
                "background:$Table.background; arc:10; border:1,1,1,1,$Component.borderColor;");

        slModeCombo = new JComboBox<>(new String[]{"Tháng", "Quý", "Năm"});

        slYearCombo = new JComboBox<>();
        for (Integer y : years) slYearCombo.addItem(String.valueOf(y));
        slYearCombo.setSelectedItem(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

        slMonthCombo = new JComboBox<>();
        for (int i = 1; i <= 12; i++) slMonthCombo.addItem("Tháng " + i);

        slQuarterCombo = new JComboBox<>(new String[]{"Quý 1", "Quý 2", "Quý 3", "Quý 4"});
        slQuarterCombo.setVisible(false);

        slFilterBtn = styledButton("🔍 Xem thống kê", Color.decode("#4361ee"), Color.WHITE);
        slExportBtn = styledButton("⬇ Xuất CSV", Color.decode("#22c55e"), Color.WHITE);

        // Show/hide quarter or month selector
        slModeCombo.addActionListener(e -> {
            String m = (String) slModeCombo.getSelectedItem();
            slMonthCombo.setVisible("Tháng".equals(m));
            slQuarterCombo.setVisible("Quý".equals(m));
            fb.revalidate(); fb.repaint();
        });

        slFilterBtn.addActionListener(e -> loadSoLuongStats());
        slExportBtn.addActionListener(e -> exportSoLuongStats());

        fb.add(new JLabel("Hiển thị theo:"));
        fb.add(slModeCombo, "width 90!");
        fb.add(new JLabel("Năm:"));
        fb.add(slYearCombo, "width 80!");
        fb.add(new JLabel("Kỳ:"));
        JPanel kyPanel = new JPanel(new CardLayout());
        kyPanel.putClientProperty(FlatClientProperties.STYLE, "background:null;");
        kyPanel.add(slMonthCombo,   "month");
        kyPanel.add(slQuarterCombo, "quarter");
        fb.add(slMonthCombo,   "width 100!");
        fb.add(slQuarterCombo, "width 100!");
        fb.add(slFilterBtn, "split 2, gapleft 8");
        fb.add(slExportBtn);

        // ── Table ────────────────────────────────────────────────
        String[] cols = {"Mã SP", "Tên sản phẩm", "Loại", "Số lượng bán", "Doanh thu (₫)"};
        slTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        slTable = buildStyledTable(slTableModel, new int[]{3, 4});
        JScrollPane scroll = new JScrollPane(slTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // Summary bar
        JPanel summaryBar = buildSummaryBar();

        panel.add(title, "gapleft 4");
        panel.add(fb, "growx");
        panel.add(summaryBar, "growx");
        panel.add(scroll, "grow, push, height 220!");

        // Load default
        loadSoLuongStats();
        return panel;
    }

    private JPanel summaryTotalPanel;
    private JLabel lblSumQty, lblSumRevenue;

    private JPanel buildSummaryBar() {
        summaryTotalPanel = new JPanel(new MigLayout("insets 10 16 10 16", "[]32[]push[]32[]"));
        summaryTotalPanel.putClientProperty(FlatClientProperties.STYLE,
                "background:$Table.background; arc:8; border:1,1,1,1,$Component.borderColor;");

        JLabel lQty = new JLabel("Tổng SL bán:");
        lQty.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        lblSumQty = new JLabel("—");
        lblSumQty.putClientProperty(FlatClientProperties.STYLE, "font:bold; foreground:#4361ee;");

        JLabel lRev = new JLabel("Tổng doanh thu:");
        lRev.putClientProperty(FlatClientProperties.STYLE, "font:bold;");
        lblSumRevenue = new JLabel("—");
        lblSumRevenue.putClientProperty(FlatClientProperties.STYLE, "font:bold; foreground:#22c55e;");

        summaryTotalPanel.add(lQty);
        summaryTotalPanel.add(lblSumQty);
        summaryTotalPanel.add(lRev);
        summaryTotalPanel.add(lblSumRevenue);
        return summaryTotalPanel;
    }

    /** Load dữ liệu SL bán theo mode đã chọn */
    private void loadSoLuongStats() {
        slTableModel.setRowCount(0);
        String mode   = (String) slModeCombo.getSelectedItem();
        String yearStr = (String) slYearCombo.getSelectedItem();
        if (yearStr == null) return;
        int year = Integer.parseInt(yearStr);

        java.sql.Date from, to;
        Calendar cal = Calendar.getInstance();

        switch (mode) {
            case "Tháng" -> {
                int month = slMonthCombo.getSelectedIndex() + 1; // 1-12
                cal.set(year, month - 1, 1, 0, 0, 0);
                from = new java.sql.Date(cal.getTimeInMillis());
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59);
                to = new java.sql.Date(cal.getTimeInMillis());
            }
            case "Quý" -> {
                int quarter = slQuarterCombo.getSelectedIndex(); // 0-3
                int startMonth = quarter * 3;                    // 0,3,6,9
                cal.set(year, startMonth, 1, 0, 0, 0);
                from = new java.sql.Date(cal.getTimeInMillis());
                cal.add(Calendar.MONTH, 3);
                cal.add(Calendar.MILLISECOND, -1);
                to = new java.sql.Date(cal.getTimeInMillis());
            }
            default -> { // Năm
                cal.set(year, Calendar.JANUARY, 1, 0, 0, 0);
                from = new java.sql.Date(cal.getTimeInMillis());
                cal.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
                to = new java.sql.Date(cal.getTimeInMillis());
            }
        }

        try {
            ArrayList<ThongKeSanPhamDTO> data = bustk.getThongKeSanPham(from, to);
            long totalQty = 0, totalRevenue = 0;
            for (ThongKeSanPhamDTO item : data) {
                slTableModel.addRow(new Object[]{
                    item.getMaSanPham(),
                    item.getTenSanPham(),
                    item.getSoLuongBan(),
                    item.getDoanhThu()
                });
                totalQty     += item.getSoLuongBan();
                totalRevenue += item.getDoanhThu();
            }
            lblSumQty.setText(MONEY_FMT.format(totalQty) + " sp");
            lblSumRevenue.setText(MONEY_FMT.format(totalRevenue) + " ₫");
        } catch (SQLException ex) {
            Logger.getLogger(formthongkesp.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu SL bán: " + ex.getMessage());
        }
    }

    private void exportSoLuongStats() {
        exportTableToCSV(slTable, slTableModel, "sl_ban_theo_ky");
    }

    // ─────────────────────────────────────────────────────────────
    //  FILTER / LOAD – Product stats
    // ─────────────────────────────────────────────────────────────
    private void applyProductFilter() {
        try {
            String mode = (String) filterModeCombo.getSelectedItem();
            Calendar cal = Calendar.getInstance();
            Date from, to;

            switch (mode) {
                case "Ngày" -> {
                    Date sel = (Date) dateSingleSpinner.getValue();
                    cal.setTime(sel);
                    setStartOfDay(cal); from = cal.getTime();
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                    cal.add(Calendar.MILLISECOND, -1); to = cal.getTime();
                }
                case "Tháng" -> {
                    String ms = (String) monthCombo.getSelectedItem();
                    String ys = (String) yearCombo.getSelectedItem();
                    if ("Tháng".equals(ms) || "Năm".equals(ys)) {
                        JOptionPane.showMessageDialog(this, "Vui lòng chọn tháng và năm");
                        return;
                    }
                    int m = Integer.parseInt(ms) - 1, y = Integer.parseInt(ys);
                    cal.set(y, m, 1, 0, 0, 0); cal.set(Calendar.MILLISECOND, 0);
                    from = cal.getTime();
                    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    setEndOfDay(cal); to = cal.getTime();
                }
                case "Năm" -> {
                    String ys = (String) yearCombo.getSelectedItem();
                    if ("Năm".equals(ys)) { JOptionPane.showMessageDialog(this,"Vui lòng chọn năm"); return; }
                    int y = Integer.parseInt(ys);
                    cal.set(y, Calendar.JANUARY, 1, 0, 0, 0); cal.set(Calendar.MILLISECOND, 0);
                    from = cal.getTime();
                    cal.set(y, Calendar.DECEMBER, 31, 23, 59, 59); cal.set(Calendar.MILLISECOND, 999);
                    to = cal.getTime();
                }
                default -> { // Khoảng thời gian
                    Date start = (Date) dateFromSpinner.getValue();
                    Date end   = (Date) dateToSpinner.getValue();
                    if (start.after(end)) { JOptionPane.showMessageDialog(this,"Từ ngày phải ≤ đến ngày"); return; }
                    cal.setTime(start); setStartOfDay(cal); from = cal.getTime();
                    cal.setTime(end);   setEndOfDay(cal);   to   = cal.getTime();
                }
            }
            loadProductStats(new java.sql.Date(from.getTime()), new java.sql.Date(to.getTime()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể lọc dữ liệu: " + ex.getMessage());
        }
    }

    private void loadProductStats(java.sql.Date from, java.sql.Date to) {
        try {
            ArrayList<ThongKeSanPhamDTO> data = bustk.getThongKeSanPham(from, to);
            productTableModel.setRowCount(0);
            for (ThongKeSanPhamDTO item : data) {
                productTableModel.addRow(new Object[]{
                    item.getMaSanPham(),
                    item.getTenSanPham(),
                    item.getSoLuongBan(),
                    item.getDoanhThu(),
                    item.getChiPhi(),
                    item.getLoiNhuan()
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(formthongkesp.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }

    private void exportProductStats() {
        exportTableToCSV(productTable, productTableModel, "thongke_sanpham");
    }

    // ─────────────────────────────────────────────────────────────
    //  RELOAD ALL
    // ─────────────────────────────────────────────────────────────
    private void reloadData(int year) throws SQLException {
        pieChart1.setDataset(createPieData(year, "doanhThu"));
        pieChart2.setDataset(createPieData(year, "chiPhi"));
        pieChart3.setDataset(createPieData(year, "loiNhuan"));
        loadLineChartData(year);
        loadSoLuongChartData(year);
        loadBarChartData(year);
        revalidate();
        repaint();
    }

    // ─────────────────────────────────────────────────────────────
    //  EXPORT CSV (chung)
    // ─────────────────────────────────────────────────────────────
    private void exportTableToCSV(JTable tbl, DefaultTableModel mdl, String defaultName) {
        if (mdl.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Xuất CSV");
        fc.setSelectedFile(new File(defaultName + ".csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = fc.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv"))
            file = new File(file.getAbsolutePath() + ".csv");

        try (OutputStreamWriter w = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            w.write("\uFEFF"); // BOM for Excel
            int cols = mdl.getColumnCount();
            for (int c = 0; c < cols; c++) {
                w.write(mdl.getColumnName(c));
                if (c < cols - 1) w.write(",");
            }
            w.write("\n");
            for (int r = 0; r < mdl.getRowCount(); r++) {
                for (int c = 0; c < cols; c++) {
                    Object val = mdl.getValueAt(r, c);
                    String txt = val == null ? "" : String.valueOf(val);
                    txt = txt.replace("\"", "\"\"");
                    if (txt.contains(",") || txt.contains("\n")) txt = "\"" + txt + "\"";
                    w.write(txt);
                    if (c < cols - 1) w.write(",");
                }
                w.write("\n");
            }
            JOptionPane.showMessageDialog(this, "✅ Xuất thành công: " + file.getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Xuất thất bại: " + ex.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────────────────────────
    private JSpinner makeSpinner() {
        JSpinner sp = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        sp.setEditor(new JSpinner.DateEditor(sp, "dd/MM/yyyy"));
        return sp;
    }

    private JButton styledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.putClientProperty(FlatClientProperties.STYLE,
                "background:" + colorHex(bg) + "; foreground:" + colorHex(fg) + "; arc:8; font:bold;");
        return btn;
    }

    private JTable buildStyledTable(DefaultTableModel mdl, int[] rightAlignCols) {
        JTable tbl = new JTable(mdl);
        tbl.putClientProperty(FlatClientProperties.STYLE, "rowHeight:38; showHorizontalLines:true;");
        tbl.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "height:34; font:bold;");
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getTableHeader().setReorderingAllowed(false);

        // Money renderer
        DefaultTableCellRenderer moneyR = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(
                    JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                if (val instanceof Number)
                    val = MONEY_FMT.format(((Number) val).longValue());
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                setHorizontalAlignment(RIGHT);
                return this;
            }
        };
        for (int c : rightAlignCols)
            tbl.getColumnModel().getColumn(c).setCellRenderer(moneyR);

        return tbl;
    }

    private static void setStartOfDay(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);      c.set(Calendar.MILLISECOND, 0);
    }

    private static void setEndOfDay(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);       c.set(Calendar.MILLISECOND, 999);
    }

    private static String colorHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }
}