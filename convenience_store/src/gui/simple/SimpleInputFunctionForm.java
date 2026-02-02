package gui.simple;

import bus.buschucnang;
import dao.daochucnang; // Thay thế buschucnang bằng daochucnang
import dao.daodanhmuc; // Thay thế busdanhmuc bằng daodanhmuc
import dto.dtochucnang; // DTO của chức năng
import dto.dtodanhmuc; // DTO của danh mục
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class SimpleInputFunctionForm extends JPanel {
    public JTextField txtTenChucNang; // Trường nhập tên chức năng
    public JComboBox<dtodanhmuc> cboDanhMuc; // ComboBox cho danh mục
    private daochucnang daoChucNang; // Lớp DAO chức năng
    private daodanhmuc daoDanhMuc; // Lớp DAO danh mục
    private JLabel lblLoadingDanhMuc;

    public SimpleInputFunctionForm() throws SQLException {
        daoChucNang = new daochucnang(); // Khởi tạo DAO chức năng
        daoDanhMuc = new daodanhmuc(); // Khởi tạo DAO danh mục
        init();
    }

    private void init() throws SQLException {
        setLayout(new MigLayout("fillx,wrap,insets 5 35 5 35,width 400", "[fill]", ""));
        
        txtTenChucNang = new JTextField();
        cboDanhMuc = new JComboBox<>();
        cboDanhMuc.setEnabled(false);
        cboDanhMuc.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            if (value != null) {
                label.setText(value.getDropdownDisplay());
            } else if (cboDanhMuc.getItemCount() == 0) {
                label.setText("Đang tải...");
            }
            return label;
        });
        lblLoadingDanhMuc = new JLabel("Đang tải danh mục...");
        
        // Thêm các thành phần vào panel
        createTitle("Thông tin chức năng");

        // Tên chức năng
        add(new JLabel("Tên chức năng"), "gapy 5 0");
        add(txtTenChucNang);

        // ComboBox danh mục
        add(new JLabel("Chọn danh mục"), "gapy 5 0");
        add(cboDanhMuc);
        add(lblLoadingDanhMuc, "gapy 3 0");

        loadDanhMucAsync();
    }

    private void loadDanhMucAsync() {
        SwingWorker<List<dtodanhmuc>, Void> worker = new SwingWorker<List<dtodanhmuc>, Void>() {
            @Override
            protected List<dtodanhmuc> doInBackground() throws Exception {
                return daoDanhMuc.getlist();
            }

            @Override
            protected void done() {
                try {
                    List<dtodanhmuc> danhMucList = get();
                    applyDanhMucList(danhMucList);
                } catch (Exception ex) {
                    if (lblLoadingDanhMuc != null) {
                        lblLoadingDanhMuc.setText("Không tải được danh mục");
                    }
                }
            }
        };
        worker.execute();
    }

    private void applyDanhMucList(List<dtodanhmuc> danhMucList) {
        cboDanhMuc.removeAllItems();
        for (dtodanhmuc danhMuc : danhMucList) {
            cboDanhMuc.addItem(danhMuc);
        }
        cboDanhMuc.setEnabled(true);
        if (lblLoadingDanhMuc != null) {
            remove(lblLoadingDanhMuc);
            revalidate();
            repaint();
            lblLoadingDanhMuc = null;
        }
    }

    // Tạo tiêu đề cho form
    private void createTitle(String title) {
        JLabel lb = new JLabel(title);
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
        add(lb, "gapy 5 0");
        add(new JSeparator(), "height 2!,gapy 0 0");
    }

    public void addChucNang() {
    try {
        if (!cboDanhMuc.isEnabled() || cboDanhMuc.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Danh mục đang tải, vui lòng đợi...");
            return;
        }
        String tenChucNang = txtTenChucNang.getText().trim();
        dtodanhmuc selectedDanhMuc = (dtodanhmuc) cboDanhMuc.getSelectedItem();
        if (selectedDanhMuc == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn danh mục!");
            return;
        }

        // Kiểm tra trùng lặp
        if (daoChucNang.isChucNangExists(tenChucNang, selectedDanhMuc.getMaDanhMuc())) {
            JOptionPane.showMessageDialog(this, "Chức năng đã tồn tại trong danh mục này!");
            return;
        }

        dtochucnang chucNang = new dtochucnang(0, tenChucNang, selectedDanhMuc.getMaDanhMuc(), 1);
        daoChucNang.add(chucNang);
        JOptionPane.showMessageDialog(this, "Chức năng đã được thêm thành công!");
        resetFields();
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        ex.printStackTrace();
    }
}

    public void setDefaultValues(dtochucnang chucNang) {
        // Điền tên chức năng vào trường txtTenChucNang
        txtTenChucNang.setText(chucNang.getTenChucNang());

        // Tìm danh mục từ danh sách và chọn danh mục tương ứng
        for (int i = 0; i < cboDanhMuc.getItemCount(); i++) {
            dtodanhmuc danhMuc = cboDanhMuc.getItemAt(i);
            if (danhMuc.getMaDanhMuc() == chucNang.getMaDanhMuc()) {
                cboDanhMuc.setSelectedItem(danhMuc);
                break;
            }
        }
    }
    public void updateChucNang(int maChucNang) {
    try {
        // Lấy giá trị từ các trường nhập liệu
        String tenChucNang = txtTenChucNang.getText().isEmpty() ? "" : txtTenChucNang.getText();
        
        // Lấy danh mục từ combo box
        dtodanhmuc selectedDanhMuc = (dtodanhmuc) cboDanhMuc.getSelectedItem();
        int maDanhMuc = selectedDanhMuc != null ? selectedDanhMuc.getMaDanhMuc() : 0;

        // Tạo đối tượng chức năng
        dtochucnang chucNang = new dtochucnang(maChucNang, tenChucNang, maDanhMuc,0);

        // Cập nhật chức năng qua bus
        buschucnang busChucNang = new buschucnang();
        busChucNang.update(chucNang); // Giả sử busChucNang có hàm update này

        JOptionPane.showMessageDialog(this, "Thông tin chức năng đã được cập nhật thành công!");
        resetFields(); // Xóa các trường nhập liệu
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không xác định: " + ex.getMessage());
        ex.printStackTrace();
    }
}


    private void resetFields() {
        txtTenChucNang.setText("");
        if (cboDanhMuc.getItemCount() > 0) {
            cboDanhMuc.setSelectedIndex(0);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Chức năng quản lý chức năng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            try {
                frame.setContentPane(new SimpleInputFunctionForm());
            } catch (SQLException e) {
                e.printStackTrace();
            }

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
