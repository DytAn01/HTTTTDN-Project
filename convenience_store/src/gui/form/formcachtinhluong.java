package gui.form;

import bus.busluong;
import bus.bushopdong;
import com.formdev.flatlaf.FlatClientProperties;
import dao.daoluong;
import dao.daonhanvien;
import dto.dtohopdong;
import dto.dtoluong;
import java.awt.Component;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.miginfocom.swing.MigLayout;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class formcachtinhluong extends JPanel {

    private final int maNhanVien;
    private final busluong busLuong = new busluong();
    private final bushopdong bushd = new bushopdong();
    private final daonhanvien daoNhanVien = new daonhanvien();

    private JComboBox<String> cboMonth;
    private JComboBox<String> cboYear;

    private JTextField txtLuongCoBan;
    private JTextField txtGioLam;
    private JTextField txtGioLamThem;
    private JTextField txtLuongThucTe;
    private JTextField txtLuongLamThem;
    private JTextField txtPhuCap;
    private JTextField txtLuongThuong;
    private JTextField txtKhoanBaoHiem;
    private JTextField txtKhoanTru;
    private JTextField txtThue;
    private JTextField txtThucLanh;

    private Integer selectedMonth;
    private Integer selectedYear;
    private double currentLuongCoBan;
    private double currentGioLam;
    private double currentGioLamThem;
    private double currentLuongThucTe;
    private double currentLuongLamThem;
    private double currentPhuCap;
    private double currentLuongThuong;
    private double currentKhoanBaoHiem;
    private double currentKhoanTru;
    private double currentThue;
    private double currentThucLanh;

    public formcachtinhluong(int maNhanVien) {
        this.maNhanVien = maNhanVien;
        init();
    }

    private void init() {
        setLayout(new MigLayout("fillx,wrap,insets 7 15 7 15", "[fill]", "[][fill,grow]"));
        add(createInfo("Cách tính lương", "Xem cách tính lương dựa trên dữ liệu lương của bạn.", 1));
        add(createBody(), "gapx 7 7");
    }

    private JPanel createInfo(String title, String description, int level) {
        JPanel panel = new JPanel(new MigLayout("fillx,wrap", "[fill]"));
        JLabel lbTitle = new JLabel(title);
        JTextArea text = new JTextArea(description);
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setBorder(BorderFactory.createEmptyBorder());
        lbTitle.putClientProperty(FlatClientProperties.STYLE, "font:bold +" + (4 - level));
        panel.add(lbTitle);
        panel.add(text);
        return panel;
    }

    private Component createBody() {
        JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 10 0 10 0", "[fill]", "[][][]"));

        JLabel title = new JLabel("Chi tiết cách tính lương");
        title.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");
        panel.add(title, "gapx 20");

        panel.add(createHeader());
        JSeparator separator = new JSeparator();
        separator.putClientProperty(FlatClientProperties.STYLE, "foreground:$Table.gridColor;");
        panel.add(separator, "height 2");

        JPanel form = new JPanel(new MigLayout("wrap 2,fillx", "[fill]15[fill]", ""));
        txtLuongCoBan = createField(form, "Lương cơ bản");
        txtGioLam = createField(form, "Tổng giờ làm");
        txtGioLamThem = createField(form, "Giờ làm thêm");
        txtLuongThucTe = createField(form, "Lương thực tế");
        txtLuongLamThem = createField(form, "Lương làm thêm");
        txtPhuCap = createField(form, "Phụ cấp");
        txtLuongThuong = createField(form, "Lương thưởng");
        txtKhoanBaoHiem = createField(form, "Khoản bảo hiểm");
        txtKhoanTru = createField(form, "Khoản trừ");
        txtThue = createField(form, "Thuế TNCN");
        txtThucLanh = createField(form, "Thực lãnh");

        JLabel formula = new JLabel("Công thức: Thực lãnh = Lương thực tế + Lương làm thêm + Phụ cấp + Lương thưởng - Khoản bảo hiểm - Khoản trừ - Thuế");
        formula.putClientProperty(FlatClientProperties.STYLE, "foreground:$Label.disabledForeground;");
        panel.add(form);
        panel.add(formula, "gapx 20");

        refreshCalculation();
        return panel;
    }

    private Component createHeader() {
        JPanel panel = new JPanel(new MigLayout("insets 5 20 5 20", "[fill]push[][][]", ""));

        cboMonth = new JComboBox<>();
        cboMonth.addItem("Tất cả tháng");
        for (int i = 1; i <= 12; i++) {
            cboMonth.addItem("Tháng " + i);
        }

        cboYear = new JComboBox<>();
        cboYear.addItem("Tất cả năm");
        for (Integer year : getAvailableYears()) {
            cboYear.addItem("Năm " + year);
        }

        cboMonth.addActionListener(e -> refreshCalculation());
        cboYear.addActionListener(e -> refreshCalculation());

        JButton btnExport = new JButton("Xuất Excel");
        btnExport.addActionListener(e -> exportToExcel());

        panel.add(new JLabel("Chọn kỳ lương"));
        panel.add(cboMonth);
        panel.add(cboYear);
        panel.add(btnExport);
        return panel;
    }

    private JTextField createField(JPanel panel, String label) {
        JLabel lb = new JLabel(label);
        JTextField field = new JTextField();
        field.setEditable(false);
        panel.add(lb);
        panel.add(field, "growx");
        return field;
    }

    private void refreshCalculation() {
        selectedMonth = parseMonth((String) cboMonth.getSelectedItem());
        selectedYear = parseYear((String) cboYear.getSelectedItem());
        dtoluong luong = findLuong(selectedMonth, selectedYear);
        dtohopdong hd = bushd.gethdnhanvien(maNhanVien);
        currentLuongCoBan = hd != null ? hd.getLuongCoBan() : 0;

        if (luong == null) {
            currentGioLam = 0;
            currentGioLamThem = 0;
            currentLuongThucTe = 0;
            currentLuongLamThem = 0;
            currentPhuCap = 0;
            currentLuongThuong = 0;
            currentKhoanBaoHiem = 0;
            currentKhoanTru = 0;
            currentThue = 0;
            currentThucLanh = 0;
            setAllValues(currentLuongCoBan, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            return;
        }

        currentLuongThucTe = luong.getLuongThucTe();
        currentGioLam = (currentLuongCoBan > 0) ? (currentLuongThucTe / currentLuongCoBan) : 0;
        currentLuongLamThem = luong.getLuongLamThem();
        currentGioLamThem = currentLuongCoBan > 0 ? currentLuongLamThem / currentLuongCoBan : 0;
        currentPhuCap = luong.getPhuCap();
        currentLuongThuong = luong.getLuongThuong();
        currentKhoanBaoHiem = luong.getKhoanBaoHiem();
        currentKhoanTru = luong.getKhoanThue();
        currentThue = luong.getKhoanThue();
        currentThucLanh = luong.getThuclanh();

        setAllValues(currentLuongCoBan, currentGioLam, currentGioLamThem, currentLuongThucTe, currentLuongLamThem, currentPhuCap, currentLuongThuong, currentKhoanBaoHiem, currentKhoanTru, currentThue, currentThucLanh);
    }

    private void setAllValues(double luongCoBan, double gioLam, double gioLamThem, double luongThucTe,
                              double luongLamThem, double phuCap, double luongThuong, double khoanBaoHiem,
                              double khoanTru, double thue, double thucLanh) {
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        txtLuongCoBan.setText(nf.format(luongCoBan));
        txtGioLam.setText(nf.format(gioLam));
        txtGioLamThem.setText(nf.format(gioLamThem));
        txtLuongThucTe.setText(nf.format(luongThucTe));
        txtLuongLamThem.setText(nf.format(luongLamThem));
        txtPhuCap.setText(nf.format(phuCap));
        txtLuongThuong.setText(nf.format(luongThuong));
        txtKhoanBaoHiem.setText(nf.format(khoanBaoHiem));
        txtKhoanTru.setText(nf.format(khoanTru));
        txtThue.setText(nf.format(thue));
        txtThucLanh.setText(nf.format(thucLanh));
    }

    private dtoluong findLuong(Integer month, Integer year) {
        daoluong daoL = new daoluong();
        ArrayList<dtoluong> listLuong = daoL.getList();
        dtoluong selected = null;
        
        for (dtoluong luong : listLuong) {
            if (luong.getMaNhanVien() != maNhanVien) {
                continue;
            }
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(luong.getNgayNhanLuong());
            int luongMonth = cal.get(Calendar.MONTH) + 1;
            int luongYear = cal.get(Calendar.YEAR);
            
            if (month != null && luongMonth != month) {
                continue;
            }
            if (year != null && luongYear != year) {
                continue;
            }
            if (selected == null
                     || luongYear > cal.get(Calendar.YEAR)
                     || (luongYear == cal.get(Calendar.YEAR) && luongMonth > cal.get(Calendar.MONTH) + 1)) {
                selected = luong;
            }
        }
        return selected;
    }

    private Integer parseMonth(String value) {
        if (value == null || value.startsWith("Tất cả")) {
            return null;
        }
        return Integer.parseInt(value.replace("Tháng", "").trim());
    }

    private Integer parseYear(String value) {
        if (value == null || value.startsWith("Tất cả")) {
            return null;
        }
        return Integer.parseInt(value.replace("Năm", "").trim());
    }

    private ArrayList<Integer> getAvailableYears() {
        Set<Integer> years = new LinkedHashSet<>();
        daoluong daoL = new daoluong();
        ArrayList<dtoluong> listLuong = daoL.getList();
        
        for (dtoluong luong : listLuong) {
            if (luong.getMaNhanVien() == maNhanVien) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(luong.getNgayNhanLuong());
                years.add(cal.get(Calendar.YEAR));
            }
        }
        return new ArrayList<>(years);
    }

    private void exportToExcel() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu Excel");
        chooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        String path = chooser.getSelectedFile().getAbsolutePath();
        if (!path.toLowerCase().endsWith(".xlsx")) {
            path += ".xlsx";
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Cach tinh luong");

            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));

            String tenNV = daoNhanVien.getNhanVienById(maNhanVien) != null
                    ? daoNhanVien.getNhanVienById(maNhanVien).getTennhanvien()
                    : "";

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Cách tính lương");
            titleCell.setCellStyle(headerStyle);

            int rowIndex = 2;
            rowIndex = addLabelValue(sheet, rowIndex, "Mã nhân viên", String.valueOf(maNhanVien));
            rowIndex = addLabelValue(sheet, rowIndex, "Tên nhân viên", tenNV);
            rowIndex = addLabelValue(sheet, rowIndex, "Tháng", selectedMonth == null ? "Tất cả" : String.valueOf(selectedMonth));
            rowIndex = addLabelValue(sheet, rowIndex, "Năm", selectedYear == null ? "Tất cả" : String.valueOf(selectedYear));
            rowIndex = addLabelValue(sheet, rowIndex, "Lương cơ bản", currentLuongCoBan, moneyStyle);
            rowIndex = addLabelValue(sheet, rowIndex, "Tổng giờ làm", currentGioLam);
            rowIndex = addLabelValue(sheet, rowIndex, "Giờ làm thêm", currentGioLamThem);
            rowIndex = addLabelValue(sheet, rowIndex, "Lương thực tế", currentLuongThucTe, moneyStyle);
            rowIndex = addLabelValue(sheet, rowIndex, "Lương làm thêm", currentLuongLamThem, moneyStyle);
            rowIndex = addLabelValue(sheet, rowIndex, "Phụ cấp", currentPhuCap, moneyStyle);
            rowIndex = addLabelValue(sheet, rowIndex, "Lương thưởng", currentLuongThuong, moneyStyle);
            rowIndex = addLabelValue(sheet, rowIndex, "Khoản bảo hiểm", currentKhoanBaoHiem, moneyStyle);
            rowIndex = addLabelValue(sheet, rowIndex, "Khoản trừ", currentKhoanTru, moneyStyle);
            rowIndex = addLabelValue(sheet, rowIndex, "Thuế TNCN", currentThue, moneyStyle);
            addLabelValue(sheet, rowIndex, "Thực lãnh", currentThucLanh, moneyStyle);

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);

            try (FileOutputStream fos = new FileOutputStream(path)) {
                workbook.write(fos);
            }
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, "Lỗi khi xuất Excel: " + ex.getMessage(), "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private int addLabelValue(Sheet sheet, int rowIndex, String label, String value) {
        Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
        return rowIndex + 1;
    }

    private int addLabelValue(Sheet sheet, int rowIndex, String label, double value, CellStyle style) {
        Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(label);
        Cell cell = row.createCell(1);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        return rowIndex + 1;
    }

    private int addLabelValue(Sheet sheet, int rowIndex, String label, double value) {
        Row row = sheet.createRow(rowIndex);
        row.createCell(0).setCellValue(label);
        row.createCell(1).setCellValue(value);
        return rowIndex + 1;
    }
}
