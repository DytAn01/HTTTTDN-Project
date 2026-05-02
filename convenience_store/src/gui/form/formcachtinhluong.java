package gui.form;

import bus.busluong;
import bus.bushopdong;
import com.formdev.flatlaf.FlatClientProperties;
import dao.daoluong;
import dto.dtohopdong;
import dto.dtoluong;
import java.awt.Component;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;

public class formcachtinhluong extends JPanel {

    private final int maNhanVien;
    private final busluong busLuong = new busluong();
    private final bushopdong bushd = new bushopdong();

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
        JPanel panel = new JPanel(new MigLayout("insets 5 20 5 20", "[fill]push[][]", ""));

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

        panel.add(new JLabel("Chọn kỳ lương"));
        panel.add(cboMonth);
        panel.add(cboYear);
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
        Integer month = parseMonth((String) cboMonth.getSelectedItem());
        Integer year = parseYear((String) cboYear.getSelectedItem());
        dtoluong luong = findLuong(month, year);
        dtohopdong hd = bushd.gethdnhanvien(maNhanVien);
        double luongCoBan = hd != null ? hd.getLuongCoBan() : 0;

        if (luong == null) {
            setAllValues(luongCoBan, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            return;
        }

        double luongThucTe = luong.getLuongThucTe();
        double gioLam = (luongCoBan > 0) ? (luongThucTe / luongCoBan) : 0;
        double luongLamThem = luong.getLuongLamThem();
        double phuCap = luong.getPhuCap();
        double luongThuong = luong.getLuongThuong();
        double khoanBaoHiem = luong.getKhoanBaoHiem();
        double khoanTru = luong.getKhoanThue();
        double thue = luong.getKhoanThue();
        double thucLanh = luong.getThuclanh();

        setAllValues(luongCoBan, gioLam, luongLamThem / (luongCoBan > 0 ? luongCoBan : 1), luongThucTe, luongLamThem, phuCap, luongThuong, khoanBaoHiem, khoanTru, thue, thucLanh);
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
}
