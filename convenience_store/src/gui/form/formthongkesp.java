package gui.form;

import bus.busphanloai;
import bus.bussanpham;
import bus.busthongke;
import com.formdev.flatlaf.FlatClientProperties;
import dto.dtophanloai;
import dto.thongke.thongkedoanhthuDTO;
import gui.comp.Combobox;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import raven.chart.ChartLegendRenderer;
import raven.chart.bar.HorizontalBarChart;
import raven.chart.data.category.DefaultCategoryDataset;
import raven.chart.data.pie.DefaultPieDataset;
import raven.chart.line.LineChart;
import raven.chart.pie.PieChart;
import gui.comp.SimpleForm;
import java.awt.event.ItemEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.UIManager;

/**
 *
 * @author Raven
 */
public class formthongkesp extends SimpleForm {

    

    public formthongkesp() throws SQLException {
        init();
    }

    @Override
    public void formRefresh() {
        lineChart.startAnimation();
        pieChart1.startAnimation();
        pieChart2.startAnimation();
        pieChart3.startAnimation();
        barChart1.startAnimation();
        barChart2.startAnimation();
    }

    @Override
    public void formInitAndOpen() {
        System.out.println("init and open");
    }

    @Override
    public void formOpen() {
        System.out.println("Open");
    }

    private void init() throws SQLException {
        setLayout(new MigLayout("wrap,fill,gap 10", "fill"));
         try {
                       UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
        bustk = new busthongke();
        int oldestYear = bustk.getOldestYear();
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        Integer[] years = new Integer[currentYear - oldestYear + 1];
        for (int i = 0; i < years.length; i++) {
            years[i] = oldestYear + i;
        }
        Combobox comboBoxYear = new Combobox();
        comboBoxYear.setModel(new DefaultComboBoxModel<>(years));
        comboBoxYear.setSelectedIndex(-1);
        comboBoxYear.setLabeText("Năm");

        comboBoxYear.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                int selectedYear = (int) e.getItem();
                try {
                    reloadData(selectedYear);
                    comboBoxYear.setFocusable(false);
                } catch (SQLException ex) {
                    Logger.getLogger(formthongkesp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        add(comboBoxYear, "align right, width 150!, wrap");
        createPieChart(currentYear);
        createLineChart(currentYear);
        createBarChart(currentYear);
        add(createProductStatsPanel(years), "span, grow, wrap");
    }

    private JPanel createProductStatsPanel(Integer[] years) {
        JPanel panel = new JPanel(new MigLayout("wrap, fillx, insets 5", "[grow]", "[]10[grow]"));
        panel.putClientProperty(FlatClientProperties.STYLE, "border:5,5,5,5,$Component.borderColor,,20");

        JPanel filterPanel = new JPanel(new MigLayout("insets 0", "[][grow][][grow][][grow]", "[]"));
        JLabel title = new JLabel("Thống kê sản phẩm");
        title.putClientProperty(FlatClientProperties.STYLE, "font:+1");

        filterModeCombo = new JComboBox<>(new String[]{"Ngày", "Tháng", "Năm", "Khoảng thời gian"});
        filterModeCombo.setSelectedIndex(0);

        dateSingleSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        dateSingleSpinner.setEditor(new JSpinner.DateEditor(dateSingleSpinner, "dd/MM/yyyy"));

        monthCombo = new JComboBox<>();
        monthCombo.addItem("Tháng");
        for (int i = 1; i <= 12; i++) {
            monthCombo.addItem(String.valueOf(i));
        }

        yearCombo = new JComboBox<>();
        yearCombo.addItem("Năm");
        for (Integer year : years) {
            yearCombo.addItem(String.valueOf(year));
        }

        dateFromSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        dateFromSpinner.setEditor(new JSpinner.DateEditor(dateFromSpinner, "dd/MM/yyyy"));
        dateToSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        dateToSpinner.setEditor(new JSpinner.DateEditor(dateToSpinner, "dd/MM/yyyy"));

        filterButton = new JButton("Lọc");
        filterButton.addActionListener(e -> applyProductFilter());
        exportButton = new JButton("Xuất Excel");
        exportButton.addActionListener(e -> exportProductStats());

        filterPanel.add(title, "span, wrap");
        filterPanel.add(new JLabel("Tiêu chí:"));
        filterPanel.add(filterModeCombo, "growx");
        filterPanel.add(new JLabel("Ngày:"));
        filterPanel.add(dateSingleSpinner, "growx");
        filterPanel.add(new JLabel("Tháng/Năm:"));

        JPanel monthYearPanel = new JPanel(new MigLayout("insets 0", "[grow][grow]", "[]"));
        monthYearPanel.add(monthCombo, "growx");
        monthYearPanel.add(yearCombo, "growx");
        filterPanel.add(monthYearPanel, "growx, wrap");

        filterPanel.add(new JLabel("Từ ngày:"));
        filterPanel.add(dateFromSpinner, "growx");
        filterPanel.add(new JLabel("Đến ngày:"));
        filterPanel.add(dateToSpinner, "growx");
        filterPanel.add(filterButton, "split 2");
        filterPanel.add(exportButton);

        panel.add(filterPanel, "growx");

        productTableModel = new DefaultTableModel(new String[]{"Mã SP", "Tên sản phẩm", "SL bán", "Doanh thu", "Chi phí", "Lợi nhuận"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(productTableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        panel.add(scrollPane, "grow, push");

        // load default current year range
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date from = cal.getTime();
        cal.set(Calendar.MONTH, 11);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        Date to = cal.getTime();
        loadProductStats(new java.sql.Date(from.getTime()), new java.sql.Date(to.getTime()));
        return panel;
    }

    private void createPieChart(int year) throws SQLException {
        pieChart1 = new PieChart();
        JLabel header1 = new JLabel("Product Income");
        header1.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:+1");
        pieChart1.setHeader(header1);
        pieChart1.getChartColor().addColor(Color.decode("#f87171"), Color.decode("#fb923c"), Color.decode("#fbbf24"), Color.decode("#a3e635"), Color.decode("#34d399"), Color.decode("#22d3ee"), Color.decode("#818cf8"), Color.decode("#c084fc"));
        pieChart1.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:5,5,5,5,$Component.borderColor,,20");
        pieChart1.setDataset(createPieData(year,"doanhThu"));
        add(pieChart1, "split 3,height 290");

        pieChart2 = new PieChart();
        JLabel header2 = new JLabel("Product Cost");
        header2.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:+1");
        pieChart2.setHeader(header2);
        pieChart2.getChartColor().addColor(Color.decode("#f87171"), Color.decode("#fb923c"), Color.decode("#fbbf24"), Color.decode("#a3e635"), Color.decode("#34d399"), Color.decode("#22d3ee"), Color.decode("#818cf8"), Color.decode("#c084fc"));
        pieChart2.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:5,5,5,5,$Component.borderColor,,20");
        pieChart2.setDataset(createPieData(year,"chiPhi"));
        add(pieChart2, "height 290");

        pieChart3 = new PieChart();
        JLabel header3 = new JLabel("Product Profit");
        header3.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:+1");
        pieChart3.setHeader(header3);
        pieChart3.getChartColor().addColor(Color.decode("#f87171"), Color.decode("#fb923c"), Color.decode("#fbbf24"), Color.decode("#a3e635"), Color.decode("#34d399"), Color.decode("#22d3ee"), Color.decode("#818cf8"), Color.decode("#c084fc"));
        pieChart3.setChartType(PieChart.ChartType.DONUT_CHART);
        pieChart3.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:5,5,5,5,$Component.borderColor,,20");
        pieChart3.setDataset(createPieData(year,"loiNhuan"));
        add(pieChart3, "height 290");
    }

   private void createLineChart(int currentYear) {
    lineChart = new LineChart();
    lineChart.setChartType(LineChart.ChartType.CURVE);

    JLabel header = new JLabel("Doanh thu sản phẩm của năm");
    header.putClientProperty(FlatClientProperties.STYLE, "font:+1;");

    JPanel panel = new JPanel(new MigLayout("wrap 1, insets 0", "[grow]", "[]10[grow]"));
    panel.putClientProperty(FlatClientProperties.STYLE, "border:5,5,5,5,$Component.borderColor,,20");

    panel.add(header, "growx");
    panel.add(lineChart, "grow");
    add(panel);
    createLineChartData(currentYear);
}

    private void createBarChart(int year) {
        // BarChart 1
        barChart1 = new HorizontalBarChart();
        JLabel header1 = new JLabel("Monthly Income");
        header1.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:+1;"
                + "border:0,0,5,0");
        barChart1.setHeader(header1);
        barChart1.setBarColor(Color.decode("#f97316"));
//        barChart1.setDataset(createData());
        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:5,5,5,5,$Component.borderColor,,20");
        panel1.add(barChart1);
        add(panel1, "split 2,gap 0 20");

        // BarChart 2
        barChart2 = new HorizontalBarChart();
        JLabel header2 = new JLabel("Monthly Expense");
        header2.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:+1;"
                + "border:0,0,5,0");
        barChart2.setHeader(header2);
        barChart2.setBarColor(Color.decode("#10b981"));
//        barChart2.setDataset(createData());
        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.putClientProperty(FlatClientProperties.STYLE, ""
                + "border:5,5,5,5,$Component.borderColor,,20");
        panel2.add(barChart2);
        add(panel2);
    }

    private DefaultPieDataset createData(int year) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        Random random = new Random();
        dataset.addValue("July (ongoing)", random.nextInt(100));
        dataset.addValue("June", random.nextInt(100));
        dataset.addValue("May", random.nextInt(100));
        dataset.addValue("April", random.nextInt(100));
        dataset.addValue("March", random.nextInt(100));
        dataset.addValue("February", random.nextInt(100));
        return dataset;
    }

    private DefaultPieDataset createPieData(int year, String type) throws SQLException {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        bussp = new bussanpham();
        bustk = new busthongke();
        buspl = new busphanloai();
        ArrayList<dtophanloai> listpl = buspl.getlist();
        for (dtophanloai phanloai : listpl) {
            ArrayList<thongkedoanhthuDTO> data = bustk.getDoanhThuChiPhiTheoNam(phanloai.getTenPhanLoai(), year);
             if (!data.isEmpty()) {
            thongkedoanhthuDTO item = data.get(0);

            // Lấy dữ liệu doanh thu, chi phí, lợi nhuận
            long doanhThu = item.getDoanhthu();
            long chiPhi = item.getChiphi();
            long loiNhuan = doanhThu-chiPhi;

                // Dựa vào loại dữ liệu (doanh thu, chi phí, lợi nhuận) để quyết định thêm vào dataset
                switch (type) {
                    case "doanhThu":
                        dataset.setValue(phanloai.getTenPhanLoai(), doanhThu); // Chỉ thêm doanh thu
                        break;
                    case "chiPhi":
                        dataset.setValue(phanloai.getTenPhanLoai(), chiPhi); // Chỉ thêm chi phí
                        break;
                    case "loiNhuan":
                        dataset.setValue(phanloai.getTenPhanLoai(), loiNhuan); // Chỉ thêm lợi nhuận
                        break;
                    default:
                        break;
                }
        }
        }
        return dataset;
    }

   private void createLineChartData(int year) {
    DefaultCategoryDataset<String, String> categoryDataset = new DefaultCategoryDataset<>();
    SimpleDateFormat df = new SimpleDateFormat("MMM yyyy"); // Định dạng hiển thị tháng và năm
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.MONTH, 0); // Bắt đầu từ tháng 1
    cal.set(Calendar.YEAR, year); // Thiết lập năm theo tham số truyền vào

    // Tạo dữ liệu cố định cho 12 tháng
    for (int i = 0; i < 12; i++) {
        String month = df.format(cal.getTime()); // Lấy tên tháng (ví dụ: Jan 2024)

        // Giá trị cố định (có thể thay đổi dựa trên logic kinh doanh)
        int income = (i + 1) * 100; // Thu nhập tăng dần theo tháng
        int expense = (i + 1) * 80; // Chi phí tăng dần theo tháng
        int profit = income - expense; // Lợi nhuận = Thu nhập - Chi phí

        categoryDataset.addValue(income, "Income", month);
        categoryDataset.addValue(expense, "Expense", month);
        categoryDataset.addValue(profit, "Profit", month);

        cal.add(Calendar.MONTH, 1); // Tăng tháng lên 1
    }

    // Thiết lập biểu đồ
    lineChart.setCategoryDataset(categoryDataset);
    lineChart.getChartColor().addColor(Color.decode("#38bdf8"), Color.decode("#fb7185"), Color.decode("#34d399"));
    // Hiển thị đầy đủ tất cả các tháng trong legend
    lineChart.setLegendRenderer(new ChartLegendRenderer() {
        @Override
        public Component getLegendComponent(Object legend, int index) {
            return super.getLegendComponent(legend, index); // Hiển thị tất cả các tháng
        }
    });
}   
   private busphanloai buspl;
    private busthongke bustk;
    private bussanpham bussp;
    private LineChart lineChart;
    private HorizontalBarChart barChart1;
    private HorizontalBarChart barChart2;
    private PieChart pieChart1;
    private PieChart pieChart2;
    private PieChart pieChart3;
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JComboBox<String> filterModeCombo;
    private JComboBox<String> monthCombo;
    private JComboBox<String> yearCombo;
    private JSpinner dateSingleSpinner;
    private JSpinner dateFromSpinner;
    private JSpinner dateToSpinner;
    private JButton filterButton;
    private JButton exportButton;

   private void reloadData(int selectedYear) throws SQLException {
    // Cập nhật lại dữ liệu cho từng biểu đồ

    
    // Cập nhật dữ liệu cho các Pie Chart
    pieChart1.setDataset(createPieData(selectedYear, "doanhThu"));
    pieChart2.setDataset(createPieData(selectedYear, "chiPhi"));
    pieChart3.setDataset(createPieData(selectedYear, "loiNhuan"));

    // Cập nhật dữ liệu cho Line Chart
    createLineChartData(selectedYear);

    // Cập nhật dữ liệu cho Bar Chart
    barChart1.setDataset(createData(selectedYear)); // Cập nhật dataset cho BarChart 1
    barChart2.setDataset(createData(selectedYear)); // Cập nhật dataset cho BarChart 2

    // Làm mới giao diện để hiển thị dữ liệu mới
    revalidate(); // Cập nhật lại bố cục
    repaint();    // Vẽ lại giao diện
}

    private void applyProductFilter() {
        try {
            String mode = (String) filterModeCombo.getSelectedItem();
            Calendar cal = Calendar.getInstance();
            Date from;
            Date to;

            if ("Ngày".equals(mode)) {
                Date selected = (Date) dateSingleSpinner.getValue();
                cal.setTime(selected);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                from = cal.getTime();
                cal.add(Calendar.DAY_OF_MONTH, 1);
                cal.add(Calendar.MILLISECOND, -1);
                to = cal.getTime();
            } else if ("Tháng".equals(mode)) {
                String monthStr = (String) monthCombo.getSelectedItem();
                String yearStr = (String) yearCombo.getSelectedItem();
                if ("Tháng".equals(monthStr) || "Năm".equals(yearStr)) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn tháng và năm");
                    return;
                }
                int month = Integer.parseInt(monthStr) - 1;
                int year = Integer.parseInt(yearStr);
                cal.set(year, month, 1, 0, 0, 0);
                cal.set(Calendar.MILLISECOND, 0);
                from = cal.getTime();
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                to = cal.getTime();
            } else if ("Năm".equals(mode)) {
                String yearStr = (String) yearCombo.getSelectedItem();
                if ("Năm".equals(yearStr)) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn năm");
                    return;
                }
                int year = Integer.parseInt(yearStr);
                cal.set(year, Calendar.JANUARY, 1, 0, 0, 0);
                cal.set(Calendar.MILLISECOND, 0);
                from = cal.getTime();
                cal.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
                cal.set(Calendar.MILLISECOND, 999);
                to = cal.getTime();
            } else {
                Date start = (Date) dateFromSpinner.getValue();
                Date end = (Date) dateToSpinner.getValue();
                if (start.after(end)) {
                    JOptionPane.showMessageDialog(this, "Từ ngày phải nhỏ hơn hoặc bằng đến ngày");
                    return;
                }
                cal.setTime(start);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                from = cal.getTime();
                cal.setTime(end);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                cal.set(Calendar.MILLISECOND, 999);
                to = cal.getTime();
            }

            loadProductStats(new java.sql.Date(from.getTime()), new java.sql.Date(to.getTime()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể lọc dữ liệu");
        }
    }

    private void loadProductStats(java.sql.Date from, java.sql.Date to) {
        try {
            ArrayList<dto.thongke.ThongKeSanPhamDTO> data = bustk.getThongKeSanPham(from, to);
            productTableModel.setRowCount(0);
            for (dto.thongke.ThongKeSanPhamDTO item : data) {
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
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu thống kê sản phẩm");
        }
    }

    private void exportProductStats() {
        if (productTableModel == null || productTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Xuất Excel (CSV)");
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) {
            file = new File(file.getAbsolutePath() + ".csv");
        }
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            writer.write("\uFEFF");
            int colCount = productTableModel.getColumnCount();
            for (int col = 0; col < colCount; col++) {
                writer.write(productTableModel.getColumnName(col));
                if (col < colCount - 1) writer.write(",");
            }
            writer.write("\n");
            for (int row = 0; row < productTableModel.getRowCount(); row++) {
                for (int col = 0; col < colCount; col++) {
                    Object value = productTableModel.getValueAt(row, col);
                    String text = value == null ? "" : String.valueOf(value);
                    text = text.replace("\"", "\"\"");
                    if (text.contains(",") || text.contains("\n")) {
                        text = "\"" + text + "\"";
                    }
                    writer.write(text);
                    if (col < colCount - 1) writer.write(",");
                }
                writer.write("\n");
            }
            JOptionPane.showMessageDialog(this, "Xuất file thành công: " + file.getName());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Xuất file thất bại");
        }
    }

}
