package gui.simple;

import dao.daochucvu; // DAO để load danh sách chức vụ
import dao.daochucnang; // DAO để load danh sách chức năng
import dao.daophanquyen; // DAO để lưu thông tin phân quyền
import dto.dtophanquyen; // DTO cho phân quyền
import dto.dtochucnang; // DTO cho chức năng
import dto.dtochucvu; // DTO cho chức vụ
import com.formdev.flatlaf.FlatClientProperties;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SimpleInputPermissionForm extends JPanel {
    public JComboBox<dtochucvu> cboChucVu; // ComboBox cho chức vụ
    public JPanel pnlChucNang; // Panel chứa các checkbox chức năng
    private daochucvu daoChucVu; // DAO cho chức vụ
    private daochucnang daoChucNang; // DAO cho chức năng
    private daophanquyen daoPhanQuyen; // DAO cho phân quyền
    private JLabel lblLoadingChucVu;
    private JLabel lblLoadingChucNang;

    public SimpleInputPermissionForm() throws SQLException {
        daoChucVu = new daochucvu(); // Khởi tạo DAO chức vụ
        daoChucNang = new daochucnang(); // Khởi tạo DAO chức năng
        daoPhanQuyen = new daophanquyen(); // Khởi tạo DAO phân quyền
        init();
    }

    private void init() throws SQLException {
        setLayout(new MigLayout("fillx,wrap,insets 5 35 5 35,width 400", "[fill]", ""));

        cboChucVu = new JComboBox<>();
        cboChucVu.setEnabled(false);
        cboChucVu.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            if (value != null) {
                label.setText(value.getDropdownDisplay()); // Hiển thị tên chức vụ
            } else if (cboChucVu.getItemCount() == 0) {
                label.setText("Đang tải...");
            }
            return label;
        });
        pnlChucNang = new JPanel(new MigLayout("wrap 2", "[fill]"));
        lblLoadingChucVu = new JLabel("Đang tải chức vụ...");
        lblLoadingChucNang = new JLabel("Đang tải chức năng...");
        pnlChucNang.add(lblLoadingChucNang, "span");

        // Thêm các thành phần vào panel
        createTitle("Thông tin phân quyền");

        // Tên chức vụ
        add(new JLabel("Chọn chức vụ"), "gapy 5 0");
        add(cboChucVu);

        // Danh sách chức năng
        add(new JLabel("Chọn chức năng"), "gapy 5 0");
        add(new JScrollPane(pnlChucNang), "h 200!");

        add(lblLoadingChucVu, "gapy 3 0");

        loadPermissionDataAsync();

        
        
    }

    private void loadPermissionDataAsync() {
        SwingWorker<PermissionData, Void> worker = new SwingWorker<PermissionData, Void>() {
            @Override
            protected PermissionData doInBackground() throws Exception {
                PermissionData data = new PermissionData();
                data.chucVuList = daoChucVu.getlist();
                data.chucNangList = daoChucNang.getList();
                return data;
            }

            @Override
            protected void done() {
                try {
                    PermissionData data = get();
                    applyChucVuList(data.chucVuList);
                    applyChucNangList(data.chucNangList);
                } catch (Exception ex) {
                    pnlChucNang.removeAll();
                    pnlChucNang.add(new JLabel("Không tải được danh sách chức năng."), "span");
                    pnlChucNang.revalidate();
                    pnlChucNang.repaint();
                    if (lblLoadingChucVu != null) {
                        lblLoadingChucVu.setText("Không tải được chức vụ");
                    }
                }
            }
        };
        worker.execute();
    }

    private void applyChucVuList(List<dtochucvu> chucVuList) {
        cboChucVu.removeAllItems();
        for (dtochucvu chucVu : chucVuList) {
            cboChucVu.addItem(chucVu);
        }
        cboChucVu.setEnabled(true);
        if (lblLoadingChucVu != null) {
            remove(lblLoadingChucVu);
            lblLoadingChucVu = null;
            revalidate();
            repaint();
        }
    }

    private void applyChucNangList(List<dtochucnang> chucNangList) {
        pnlChucNang.removeAll();
        for (dtochucnang chucNang : chucNangList) {
            JCheckBox chk = new JCheckBox(chucNang.getTenChucNang());
            chk.putClientProperty("maChucNang", chucNang.getMaChucNang()); // Gắn mã chức năng vào thuộc tính
            pnlChucNang.add(chk);
        }
        pnlChucNang.revalidate();
        pnlChucNang.repaint();
    }

    private static class PermissionData {
        private List<dtochucvu> chucVuList;
        private List<dtochucnang> chucNangList;
    }

    private void createTitle(String title) {
        JLabel lb = new JLabel(title);
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
        add(lb, "gapy 5 0");
        add(new JSeparator(), "height 2!,gapy 0 0");
    }
    public void updatePermission(int maChucVu) {
        try {
            // Lấy danh sách chức năng đã chọn
            List<dtophanquyen> permissions = new ArrayList<>();
            for (int i = 0; i < pnlChucNang.getComponentCount(); i++) {
                JCheckBox chk = (JCheckBox) pnlChucNang.getComponent(i);
                if (chk.isSelected()) {
                    int maChucNang = (int) chk.getClientProperty("maChucNang");
                    permissions.add(new dtophanquyen(0, maChucVu, maChucNang));
                }
            }

            // Xóa các quyền cũ và thêm quyền mới vào cơ sở dữ liệu
            daoPhanQuyen.deleteByChucVu(maChucVu); // Xóa tất cả quyền hiện có
            for (dtophanquyen permission : permissions) {
                daoPhanQuyen.add(permission); // Thêm quyền mới
            }

            JOptionPane.showMessageDialog(this, "Phân quyền đã được cập nhật thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi khi cập nhật phân quyền: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void savePermissions() {
        try {
            // Lấy chức vụ đã chọn
            dtochucvu selectedChucVu = (dtochucvu) cboChucVu.getSelectedItem();
            if (selectedChucVu == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn chức vụ!");
                return;
            }

            int maChucVu = selectedChucVu.getMachucvu();

            // Lấy danh sách chức năng đã chọn
            List<dtophanquyen> permissions = new ArrayList<>();
            for (int i = 0; i < pnlChucNang.getComponentCount(); i++) {
                JCheckBox chk = (JCheckBox) pnlChucNang.getComponent(i);
                if (chk.isSelected()) {
                    int maChucNang = (int) chk.getClientProperty("maChucNang");
                    permissions.add(new dtophanquyen(0, maChucVu, maChucNang)); // Tạo đối tượng phân quyền
                }
            }

            // Xóa các quyền cũ và thêm quyền mới vào cơ sở dữ liệu
            daoPhanQuyen.deleteByChucVu(maChucVu); // Giả sử có hàm này để xóa quyền cũ
            for (dtophanquyen permission : permissions) {
                daoPhanQuyen.add(permission);
            }

            JOptionPane.showMessageDialog(this, "Phân quyền đã được lưu thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Phân quyền chức năng");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            try {
                frame.setContentPane(new SimpleInputPermissionForm());
            } catch (SQLException e) {
                e.printStackTrace();
            }

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
