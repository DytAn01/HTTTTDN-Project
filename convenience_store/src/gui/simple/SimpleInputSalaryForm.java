package gui.simple;

import bus.busluong;
import bus.bushopdong;
import dao.daoluong;
import dao.daonhanvien;
import dto.dtoluong;
import dto.dtonhanvien;
import dto.dtohopdong;
import com.formdev.flatlaf.FlatClientProperties;
import com.toedter.calendar.JDateChooser;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SimpleInputSalaryForm extends JPanel {
    private int selectedMaNhanVien = 0;
    private JLabel lblTenNhanVien;
    private JTextField txtGioLam;
    private JTextField txtLuongCoBan;
    private JTextField txtPhuCap;
    private JTextField txtLuongThucTe;
    private JTextField txtLuongThuong;
    private JTextField txtKhoanBaoHiem;
    private JTextField txtKhoanThue;
    private JTextField txtLuongLamThem;
    private JTextField txtThucLanh;  // Thực lãnh
    private JDateChooser dateChooserNgayNhanLuong;
    private daoluong daoLuong;
    private daonhanvien daoNhanVien;
    private bushopdong busHD;

    public SimpleInputSalaryForm() throws SQLException {
        daoLuong = new daoluong(); 
        daoNhanVien = new daonhanvien();
        busHD = new bushopdong();
        init();
    }

    private void init() throws SQLException {
        setLayout(new MigLayout("fillx,wrap,insets 5 35 5 35,width 400", "[fill]", ""));

        lblTenNhanVien = new JLabel(" ");
        txtGioLam = new JTextField();
        txtLuongCoBan = new JTextField();
        txtLuongCoBan.setEnabled(false);
        txtPhuCap = new JTextField();
        txtLuongThucTe = new JTextField();
        txtLuongThucTe.setEnabled(false);  // Auto-calculate từ giờ làm
        txtLuongThuong = new JTextField();
        txtKhoanBaoHiem = new JTextField();
        txtKhoanThue = new JTextField();
        txtLuongLamThem = new JTextField();
        txtThucLanh = new JTextField();
        txtThucLanh.setEnabled(false);  // Read-only, tính tự động
        dateChooserNgayNhanLuong = new JDateChooser();
        dateChooserNgayNhanLuong.setDateFormatString("dd/MM/yyyy");

        createTitle("Thông tin lương");

        add(new JLabel("Tên nhân viên"), "gapy 5 0");
        add(lblTenNhanVien, "wrap");
        
        add(new JLabel("Lương cơ bản"), "gapy 5 0");
        add(txtLuongCoBan);
        
        add(new JLabel("Giờ làm việc"), "gapy 5 0");
        add(txtGioLam);
        // Thêm listener để tính lương thực tế
        txtGioLam.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { calculateLuongThucTe(); calculateThucLanh(); }
            @Override public void removeUpdate(DocumentEvent e) { calculateLuongThucTe(); calculateThucLanh(); }
            @Override public void changedUpdate(DocumentEvent e) { calculateLuongThucTe(); calculateThucLanh(); }
        });

        add(new JLabel("Phụ cấp"), "gapy 5 0");
        add(txtPhuCap);
        txtPhuCap.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { calculateThucLanh(); }
            @Override public void removeUpdate(DocumentEvent e) { calculateThucLanh(); }
            @Override public void changedUpdate(DocumentEvent e) { calculateThucLanh(); }
        });

        add(new JLabel("Lương thực tế"), "gapy 5 0");
        add(txtLuongThucTe);

        add(new JLabel("Lương thưởng"), "gapy 5 0");
        add(txtLuongThuong);
        txtLuongThuong.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { calculateThucLanh(); }
            @Override public void removeUpdate(DocumentEvent e) { calculateThucLanh(); }
            @Override public void changedUpdate(DocumentEvent e) { calculateThucLanh(); }
        });

        add(new JLabel("Khoản bảo hiểm"), "gapy 5 0");
        add(txtKhoanBaoHiem);
        txtKhoanBaoHiem.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { calculateThucLanh(); }
            @Override public void removeUpdate(DocumentEvent e) { calculateThucLanh(); }
            @Override public void changedUpdate(DocumentEvent e) { calculateThucLanh(); }
        });

        add(new JLabel("Khoản thuế"), "gapy 5 0");
        add(txtKhoanThue);
        txtKhoanThue.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { calculateThucLanh(); }
            @Override public void removeUpdate(DocumentEvent e) { calculateThucLanh(); }
            @Override public void changedUpdate(DocumentEvent e) { calculateThucLanh(); }
        });

        add(new JLabel("Lương làm thêm"), "gapy 5 0");
        add(txtLuongLamThem);
        txtLuongLamThem.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { calculateThucLanh(); }
            @Override public void removeUpdate(DocumentEvent e) { calculateThucLanh(); }
            @Override public void changedUpdate(DocumentEvent e) { calculateThucLanh(); }
        });
        
        add(new JLabel("Thực lãnh"), "gapy 5 0");
        add(txtThucLanh);

        add(new JLabel("Ngày nhận lương"), "gapy 5 0");
        add(dateChooserNgayNhanLuong);

        // Employee is set via `setDefaultValues`; name will be displayed in `lblTenNhanVien`.
    }
    
    private void calculateLuongThucTe() {
        try {
            String luongCoBasText = txtLuongCoBan.getText().trim();
            String gioLamText = txtGioLam.getText().trim();
            
            if (luongCoBasText.isEmpty() || gioLamText.isEmpty()) {
                txtLuongThucTe.setText("");
                return;
            }
            
            double luongCoBan = Double.parseDouble(luongCoBasText);
            double gioLam = Double.parseDouble(gioLamText);
            double luongThucTe = luongCoBan * gioLam;
            
            txtLuongThucTe.setText(String.valueOf(luongThucTe));
        } catch (NumberFormatException ex) {
            txtLuongThucTe.setText("");
        }
    }
    private void loadLuongCoBan(){
        try {
            int ma = selectedMaNhanVien;
            if (ma <= 0) return;
            dtohopdong hopDong = busHD.gethdnhanvien(ma);
            dtonhanvien nv = daoNhanVien.getNhanVienById(ma);
            if (nv != null) {
                lblTenNhanVien.setText(nv.getDropdownDisplay());
            } else {
                lblTenNhanVien.setText("Không tìm thấy");
            }
            if (hopDong != null) {
                txtLuongCoBan.setText(String.valueOf(hopDong.getLuongCoBan()));
                calculateLuongThucTe();
            }
        } catch (NumberFormatException ex) {
            // ignore invalid id while typing
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onMaNhanVienChanged() {
        // kept for compatibility but not used (employee id is set via setDefaultValues)
        loadLuongCoBan();
    }
    
    private void calculateThucLanh() {
        try {
            double luongThucTe = Double.parseDouble(txtLuongThucTe.getText().isEmpty() ? "0" : txtLuongThucTe.getText());
            double phuCap = Double.parseDouble(txtPhuCap.getText().isEmpty() ? "0" : txtPhuCap.getText());
            double luongThuong = Double.parseDouble(txtLuongThuong.getText().isEmpty() ? "0" : txtLuongThuong.getText());
            double khoanBaoHiem = Double.parseDouble(txtKhoanBaoHiem.getText().isEmpty() ? "0" : txtKhoanBaoHiem.getText());
            double khoanThue = Double.parseDouble(txtKhoanThue.getText().isEmpty() ? "0" : txtKhoanThue.getText());
            double luongLamThem = Double.parseDouble(txtLuongLamThem.getText().isEmpty() ? "0" : txtLuongLamThem.getText());
            
            // Thực lãnh = Lương thực tế + Phụ cấp + Lương thưởng + Lương làm thêm - Bảo hiểm - Thuế
            double thucLanh = luongThucTe + phuCap + luongThuong + luongLamThem - khoanBaoHiem - khoanThue;
            txtThucLanh.setText(String.valueOf(thucLanh));
        } catch (NumberFormatException ex) {
            txtThucLanh.setText("");
        }
    }

    

    public void addLuong() {
        try {
            // Regex patterns
            String numberPattern = "^\\d+(\\.\\d+)?$"; // Số thực
            String integerPattern = "^\\d+$"; // Số nguyên

            // Kiểm tra giá trị phụ cấp
            String phuCapText = txtPhuCap.getText().trim();
            if (phuCapText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Phụ cấp không được bỏ trống!");
                txtPhuCap.requestFocusInWindow();
                return;
            }
            if (!phuCapText.matches(numberPattern)) {
                JOptionPane.showMessageDialog(this, "Phụ cấp phải là số hợp lệ!");
                txtPhuCap.requestFocusInWindow();
                return;
            }
            double phuCap = Double.parseDouble(phuCapText);

            // Kiểm tra giá trị lương thực tế
            String luongThucTeText = txtLuongThucTe.getText().trim();
            if (luongThucTeText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lương thực tế không được bỏ trống!");
                txtLuongThucTe.requestFocusInWindow();
                return;
            }
            if (!luongThucTeText.matches(numberPattern)) {
                JOptionPane.showMessageDialog(this, "Lương thực tế phải là số hợp lệ!");
                txtLuongThucTe.requestFocusInWindow();
                return;
            }
            double luongThucTe = Double.parseDouble(luongThucTeText);

            // Kiểm tra giá trị lương thưởng
            String luongThuongText = txtLuongThuong.getText().trim();
            if (luongThuongText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lương thưởng không được bỏ trống!");
                txtLuongThuong.requestFocusInWindow();
                return;
            }
            if (!luongThuongText.matches(numberPattern)) {
                JOptionPane.showMessageDialog(this, "Lương thưởng phải là số hợp lệ!");
                txtLuongThuong.requestFocusInWindow();
                return;
            }
            double luongThuong = Double.parseDouble(luongThuongText);

            // Kiểm tra các khoản bảo hiểm
            String khoanBaoHiemText = txtKhoanBaoHiem.getText().trim();
            if (khoanBaoHiemText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Khoản bảo hiểm không được bỏ trống!");
                txtKhoanBaoHiem.requestFocusInWindow();
                return;
            }
            if (!khoanBaoHiemText.matches(numberPattern)) {
                JOptionPane.showMessageDialog(this, "Khoản bảo hiểm phải là số hợp lệ!");
                txtKhoanBaoHiem.requestFocusInWindow();
                return;
            }
            double khoanBaoHiem = Double.parseDouble(khoanBaoHiemText);

            // Kiểm tra khoản thuế
            String khoanThueText = txtKhoanThue.getText().trim();
            if (khoanThueText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Khoản thuế không được bỏ trống!");
                txtKhoanThue.requestFocusInWindow();
                return;
            }
            if (!khoanThueText.matches(numberPattern)) {
                JOptionPane.showMessageDialog(this, "Khoản thuế phải là số hợp lệ!");
                txtKhoanThue.requestFocusInWindow();
                return;
            }
            double khoanThue = Double.parseDouble(khoanThueText);

            // Kiểm tra lương làm thêm
            String luongLamThemText = txtLuongLamThem.getText().trim();
            if (luongLamThemText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lương làm thêm không được bỏ trống!");
                txtLuongLamThem.requestFocusInWindow();
                return;
            }
            if (!luongLamThemText.matches(integerPattern)) {
                JOptionPane.showMessageDialog(this, "Lương làm thêm phải là số nguyên hợp lệ!");
                txtLuongLamThem.requestFocusInWindow();
                return;
            }
            int luongLamThem = Integer.parseInt(luongLamThemText);

            // Kiểm tra ngày nhận lương
            Date ngayNhanLuongDate = dateChooserNgayNhanLuong.getDate();
            if (ngayNhanLuongDate == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày nhận lương!");
                dateChooserNgayNhanLuong.requestFocusInWindow();
                return;
            }
            // Lấy thông tin nhân viên từ internal state
            int maNhanVien = selectedMaNhanVien;
            if (maNhanVien <= 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên trước khi thêm lương!");
                return;
            }

            // Tạo DTO và lưu vào cơ sở dữ liệu (maChamCong = 0 vì không sử dụng)
            double thucLanh = luongThucTe + phuCap + luongThuong + luongLamThem - khoanBaoHiem - khoanThue;
            dtoluong luong = new dtoluong(0, phuCap, luongThucTe, luongThuong, khoanBaoHiem, khoanThue, thucLanh, luongLamThem, ngayNhanLuongDate, maNhanVien);
            daoLuong.add(luong);

            JOptionPane.showMessageDialog(this, "Thông tin lương đã được thêm thành công!");
            resetFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu nhập không hợp lệ!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void updateLuong(int maLuong) {
        try {
            // Regex patterns
            String numberPattern = "^\\d+(\\.\\d+)?$"; // Số thực
            String integerPattern = "^\\d+$"; // Số nguyên

            // Kiểm tra giá trị phụ cấp
            String phuCapText = txtPhuCap.getText().trim();
            if (phuCapText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Phụ cấp không được bỏ trống!");
                txtPhuCap.requestFocusInWindow();
                return;
            }
            if (!phuCapText.matches(numberPattern)) {
                JOptionPane.showMessageDialog(this, "Phụ cấp phải là số hợp lệ!");
                txtPhuCap.requestFocusInWindow();
                return;
            }
            double phuCap = Double.parseDouble(phuCapText);

            // Kiểm tra giá trị lương thực tế
            String luongThucTeText = txtLuongThucTe.getText().trim();
            if (luongThucTeText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lương thực tế không được bỏ trống!");
                txtLuongThucTe.requestFocusInWindow();
                return;
            }
            if (!luongThucTeText.matches(numberPattern)) {
                JOptionPane.showMessageDialog(this, "Lương thực tế phải là số hợp lệ!");
                txtLuongThucTe.requestFocusInWindow();
                return;
            }
            double luongThucTe = Double.parseDouble(luongThucTeText);

            // Kiểm tra giá trị lương thưởng
            String luongThuongText = txtLuongThuong.getText().trim();
            if (luongThuongText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lương thưởng không được bỏ trống!");
                txtLuongThuong.requestFocusInWindow();
                return;
            }
            if (!luongThuongText.matches(numberPattern)) {
                JOptionPane.showMessageDialog(this, "Lương thưởng phải là số hợp lệ!");
                txtLuongThuong.requestFocusInWindow();
                return;
            }
            double luongThuong = Double.parseDouble(luongThuongText);

            // Kiểm tra các khoản bảo hiểm
            String khoanBaoHiemText = txtKhoanBaoHiem.getText().trim();
            if (khoanBaoHiemText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Khoản bảo hiểm không được bỏ trống!");
                txtKhoanBaoHiem.requestFocusInWindow();
                return;
            }
            if (!khoanBaoHiemText.matches(numberPattern)) {
                JOptionPane.showMessageDialog(this, "Khoản bảo hiểm phải là số hợp lệ!");
                txtKhoanBaoHiem.requestFocusInWindow();
                return;
            }
            double khoanBaoHiem = Double.parseDouble(khoanBaoHiemText);

            // Kiểm tra khoản thuế
            String khoanThueText = txtKhoanThue.getText().trim();
            if (khoanThueText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Khoản thuế không được bỏ trống!");
                txtKhoanThue.requestFocusInWindow();
                return;
            }
            if (!khoanThueText.matches(numberPattern)) {
                JOptionPane.showMessageDialog(this, "Khoản thuế phải là số hợp lệ!");
                txtKhoanThue.requestFocusInWindow();
                return;
            }
            double khoanThue = Double.parseDouble(khoanThueText);

            // Kiểm tra lương làm thêm
            String luongLamThemText = txtLuongLamThem.getText().trim();
            if (luongLamThemText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Lương làm thêm không được bỏ trống!");
                txtLuongLamThem.requestFocusInWindow();
                return;
            }
            if (!luongLamThemText.matches(integerPattern)) {
                JOptionPane.showMessageDialog(this, "Lương làm thêm phải là số nguyên hợp lệ!");
                txtLuongLamThem.requestFocusInWindow();
                return;
            }
            double luongLamThem = Double.parseDouble(luongLamThemText);

            // Kiểm tra ngày nhận lương
            Date ngayNhanLuongDate = dateChooserNgayNhanLuong.getDate();
            if (ngayNhanLuongDate == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày nhận lương!");
                dateChooserNgayNhanLuong.requestFocusInWindow();
                return;
            }
            
            // Lấy thông tin nhân viên từ internal state
            int maNhanVien = selectedMaNhanVien;
            if (maNhanVien <= 0) {
                JOptionPane.showMessageDialog(this, "Mã nhân viên không hợp lệ!");
                return;
            }

            // Tạo DTO và cập nhật cơ sở dữ liệu (maChamCong = 0 vì không sử dụng)
            dtoluong luong = new dtoluong(maLuong, phuCap, luongThucTe, luongThuong, khoanBaoHiem, khoanThue, calculateThucLanhValue(luongThucTe, phuCap, luongThuong, khoanBaoHiem, khoanThue, luongLamThem), luongLamThem, ngayNhanLuongDate, maNhanVien);
            daoLuong.update(luong);

            JOptionPane.showMessageDialog(this, "Thông tin lương đã được cập nhật thành công!");
            resetFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu nhập không hợp lệ!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    private double calculateThucLanhValue(double luongThucTe, double phuCap, double luongThuong, 
                                         double khoanBaoHiem, double khoanThue, double luongLamThem) {
        return luongThucTe + phuCap + luongThuong + luongLamThem - khoanBaoHiem - khoanThue;
    }

    public void setDefaultValues(dtoluong luong) {
        // Set default values directly (no combo async)
        setDefaultValuesInternal(luong);
    }

    private void setDefaultValuesInternal(dtoluong luong) {
        // Set employee ID internally and load related info
        selectedMaNhanVien = luong.getMaNhanVien();
        dtonhanvien nv = daoNhanVien.getNhanVienById(selectedMaNhanVien);
        if (nv != null) lblTenNhanVien.setText(nv.getDropdownDisplay());
        else lblTenNhanVien.setText("Không tìm thấy");
        loadLuongCoBan();
        
        // Tính ngược lại giờ làm từ lương thực tế và lương cơ bản
        try {
            double luongCoBan = Double.parseDouble(txtLuongCoBan.getText().isEmpty() ? "0" : txtLuongCoBan.getText());
            if (luongCoBan > 0) {
                double gioLam = luong.getLuongThucTe() / luongCoBan;
                txtGioLam.setText(String.valueOf(gioLam));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        txtPhuCap.setText(String.valueOf(luong.getPhuCap()));
        txtLuongThucTe.setText(String.valueOf(luong.getLuongThucTe()));
        txtLuongThuong.setText(String.valueOf(luong.getLuongThuong()));
        txtKhoanBaoHiem.setText(String.valueOf(luong.getKhoanBaoHiem()));
        txtKhoanThue.setText(String.valueOf(luong.getKhoanThue()));
        txtLuongLamThem.setText(String.valueOf(luong.getLuongLamThem()));
        try {
            Date ngayNhanLuong = new SimpleDateFormat("yyyy-MM-dd").parse(luong.getNgayNhanLuong().toString());
            dateChooserNgayNhanLuong.setDate(ngayNhanLuong);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Tính thực lãnh
        calculateThucLanh();
    }

    private void resetFields() {
        selectedMaNhanVien = 0;
        lblTenNhanVien.setText(" ");
        txtGioLam.setText("");
        txtLuongCoBan.setText("");
        txtPhuCap.setText("");
        txtLuongThucTe.setText("");
        txtLuongThuong.setText("");
        txtKhoanBaoHiem.setText("");
        txtKhoanThue.setText("");
        txtLuongLamThem.setText("");
        txtThucLanh.setText("");
        dateChooserNgayNhanLuong.setDate(null);
    }

    private void createTitle(String title) {
        JLabel lb = new JLabel(title);
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
        add(lb, "gapy 5 0");
        add(new JSeparator(), "height 2!,gapy 0 0");
    }
    
     public static void main(String[] args) {
        // Thiết lập giao diện FlatLaf
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Chạy ứng dụng trong luồng Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Form Nhập Lương");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            try {
                // Tạo nội dung là form nhập lương
                SimpleInputSalaryForm form = new SimpleInputSalaryForm();
                frame.setContentPane(form);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
                return;
            }

            // Thiết lập kích thước và hiển thị cửa sổ
            frame.pack();
            frame.setLocationRelativeTo(null); // Hiển thị giữa màn hình
            frame.setVisible(true);
        });
    }
}
