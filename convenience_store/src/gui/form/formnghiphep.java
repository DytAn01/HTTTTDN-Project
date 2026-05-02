package gui.form;

import bus.busdonxinnghiep;
import bus.busloaidonxinnghiep;
import bus.busnhanvien;
import com.formdev.flatlaf.FlatClientProperties;
import com.toedter.calendar.JDateChooser;
import dto.dtoloaidonxinnghiep;
import dto.dtodonxinnghiep;
import dto.dtonhanvien;
import gui.modal.ModalDialog;
import gui.modal.component.SimpleModalBorder;
import gui.modal.option.Location;
import gui.modal.option.Option;
import gui.table.TableHeaderAlignment;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;

public class formnghiphep extends JPanel {
    private static final Color ACCENT = new Color(99, 102, 241);
    private static final Color ACCENT_SOFT = new Color(238, 242, 255);
    private static final Color SUCCESS = new Color(34, 197, 94);
    private static final Color DANGER = new Color(239, 68, 68);

    private final busdonxinnghiep busDon = new busdonxinnghiep();
    private final busloaidonxinnghiep busLoai = new busloaidonxinnghiep();
    private final busnhanvien busNhanVien = new busnhanvien();
    private final int currentEmployeeId;
    private final boolean adminMode;

    private JTable tableDon;
    private DefaultTableModel modelDon;
    private JTextField txtSearchDon;

    private JTable tableMyDon;
    private DefaultTableModel modelMyDon;
    private JTextField txtSearchMyDon;

    private JTable tableLoai;
    private DefaultTableModel modelLoai;
    private JTextField txtSearchLoai;

    public formnghiphep() {
        this(0, true);
    }

    public formnghiphep(boolean adminMode) {
        this(0, adminMode);
    }

    public formnghiphep(int currentEmployeeId, boolean adminMode) {
        this.currentEmployeeId = currentEmployeeId;
        this.adminMode = adminMode;
        setLayout(new MigLayout("fill, insets 16 20 16 20, wrap", "[fill]", "[shrink 0][fill,grow]"));
        add(buildHeader(), "growx");
        add(buildTabs(), "grow");
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new MigLayout("fillx, insets 0 0 12 0", "[fill]push[]push[]", "[]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        JLabel title = new JLabel("Quản lý nghỉ phép");
        title.putClientProperty(FlatClientProperties.STYLE, "font: bold +6;");
        p.add(title);

        p.add(countBadge(getVisibleRequests().size() + " đơn", ACCENT, ACCENT_SOFT));
        p.add(countBadge(busLoai.getList().size() + " loại đơn", new Color(5, 150, 105), new Color(220, 252, 231)));
        return p;
    }

    private JLabel countBadge(String text, Color fg, Color bg) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 12f));
        lbl.setForeground(fg);
        lbl.setBackground(bg);
        lbl.setOpaque(true);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(199, 210, 254), 1, true),
                BorderFactory.createEmptyBorder(4, 12, 4, 12)));
        return lbl;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.putClientProperty(FlatClientProperties.STYLE, "tabType: card;");
        if (adminMode) {
            tabs.addTab("📄 Danh sách duyệt", wrapTab(buildDonCard()));
            tabs.addTab("🏷 Loại đơn", wrapTab(buildLoaiCard()));
        } else {
            tabs.addTab("📝 Làm đơn nghỉ phép", wrapTab(buildEmployeeCard()));
            tabs.addTab("📋 Đơn của tôi", wrapTab(buildMyDonCard()));
        }
        return tabs;
    }

    private JPanel buildEmployeeCard() {
        JPanel card = new JPanel(new MigLayout("fill, insets 0, wrap", "[fill]", "[fill][shrink 0]"));
        card.putClientProperty(FlatClientProperties.STYLE,
                "background: $Panel.background; border: 1,1,1,1,$Component.borderColor,12;");
        LeaveRequestInputForm form = new LeaveRequestInputForm(null, false);
        card.add(form, "grow");

        JPanel actions = new JPanel(new MigLayout("insets 10 16 14 16, fillx", "push[][pref!]", "[]"));
        actions.putClientProperty(FlatClientProperties.STYLE, "background: null;");
        JButton btnSubmit = accentButton("Nộp đơn", SUCCESS);
        btnSubmit.addActionListener(e -> {
            if (form.save()) {
                loadMyDon();
            }
        });
        actions.add(btnSubmit);
        card.add(actions, "growx");
        return card;
    }

    private JPanel buildMyDonCard() {
        JPanel card = new JPanel(new MigLayout("fill, insets 0, wrap", "[fill]", "[shrink 0][1!][fill,grow]"));
        card.putClientProperty(FlatClientProperties.STYLE,
                "background: $Panel.background; border: 1,1,1,1,$Component.borderColor,12;");
        card.add(buildMyDonToolbar(), "growx, gapx 16 16, gapy 12 10");

        JSeparator sep = new JSeparator();
        sep.putClientProperty(FlatClientProperties.STYLE, "foreground: $Table.gridColor;");
        card.add(sep, "growx, height 1!");

        card.add(buildMyDonTable(), "grow");
        return card;
    }

    private JPanel wrapTab(Component c) {
        JPanel p = new JPanel(new MigLayout("fill, insets 10 0 10 0", "[fill]", "[fill]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");
        p.add(c);
        return p;
    }

    private JPanel buildDonCard() {
        JPanel card = new JPanel(new MigLayout("fill, insets 0, wrap", "[fill]", "[shrink 0][1!][fill,grow]"));
        card.putClientProperty(FlatClientProperties.STYLE,
                "background: $Panel.background; border: 1,1,1,1,$Component.borderColor,12;");
        card.add(buildDonToolbar(), "growx, gapx 16 16, gapy 12 10");

        JSeparator sep = new JSeparator();
        sep.putClientProperty(FlatClientProperties.STYLE, "foreground: $Table.gridColor;");
        card.add(sep, "growx, height 1!");

        card.add(buildDonTable(), "grow");
        return card;
    }

    private JPanel buildLoaiCard() {
        JPanel card = new JPanel(new MigLayout("fill, insets 0, wrap", "[fill]", "[shrink 0][1!][fill,grow]"));
        card.putClientProperty(FlatClientProperties.STYLE,
                "background: $Panel.background; border: 1,1,1,1,$Component.borderColor,12;");
        card.add(buildLoaiToolbar(), "growx, gapx 16 16, gapy 12 10");

        JSeparator sep = new JSeparator();
        sep.putClientProperty(FlatClientProperties.STYLE, "foreground: $Table.gridColor;");
        card.add(sep, "growx, height 1!");

        card.add(buildLoaiTable(), "grow");
        return card;
    }

    private JPanel buildDonToolbar() {
        JPanel p = new JPanel(new MigLayout("insets 8 16 8 16, fillx", "[260!][100!]push[][pref!][pref!][pref!]", "[center]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        txtSearchDon = new JTextField();
        txtSearchDon.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm theo nhân viên, loại đơn, trạng thái...");
        txtSearchDon.putClientProperty(FlatClientProperties.STYLE, "arc: 8; focusedBorderColor: #6366f1; borderWidth: 1;");
        txtSearchDon.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterDon(); }
            @Override public void removeUpdate(DocumentEvent e) { filterDon(); }
            @Override public void changedUpdate(DocumentEvent e) { filterDon(); }
        });

        JButton btnSearch = accentButton("Tìm kiếm", ACCENT);
        btnSearch.addActionListener(e -> filterDon());

        JButton btnReload = outlineButton("↺ Tải lại");
        btnReload.addActionListener(e -> loadDon());

        JButton btnAdd = accentButton("+ Thêm", SUCCESS);
        btnAdd.addActionListener(e -> openDonModal("Thêm đơn nghỉ phép", null));

        JButton btnEdit = outlineButton("✎ Sửa");
        btnEdit.addActionListener(e -> editSelectedDon());

        JButton btnDelete = dangerButton("✕ Xóa");
        btnDelete.addActionListener(e -> deleteSelectedDon());

        p.add(txtSearchDon, "growx, height 32!");
        p.add(btnSearch, "height 32!");
        p.add(btnReload, "height 32!");
        p.add(btnAdd, "height 32!");
        p.add(btnEdit, "height 32!");
        p.add(btnDelete, "height 32!");
        return p;
    }

    private JPanel buildMyDonToolbar() {
        JPanel p = new JPanel(new MigLayout("insets 8 16 8 16, fillx", "[260!][100!]push[]", "[center]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        txtSearchMyDon = new JTextField();
        txtSearchMyDon.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm theo loại đơn, trạng thái...");
        txtSearchMyDon.putClientProperty(FlatClientProperties.STYLE, "arc: 8; focusedBorderColor: #6366f1; borderWidth: 1;");
        txtSearchMyDon.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterMyDon(); }
            @Override public void removeUpdate(DocumentEvent e) { filterMyDon(); }
            @Override public void changedUpdate(DocumentEvent e) { filterMyDon(); }
        });

        JButton btnSearch = accentButton("Tìm kiếm", ACCENT);
        btnSearch.addActionListener(e -> filterMyDon());

        JButton btnReload = outlineButton("↺ Tải lại");
        btnReload.addActionListener(e -> loadMyDon());

        p.add(txtSearchMyDon, "growx, height 32!");
        p.add(btnSearch, "height 32!");
        p.add(btnReload, "height 32!");
        return p;
    }

    private JPanel buildLoaiToolbar() {
        JPanel p = new JPanel(new MigLayout("insets 8 16 8 16, fillx", "[260!][100!]push[][pref!][pref!]", "[center]"));
        p.putClientProperty(FlatClientProperties.STYLE, "background: null;");

        txtSearchLoai = new JTextField();
        txtSearchLoai.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm loại đơn...");
        txtSearchLoai.putClientProperty(FlatClientProperties.STYLE, "arc: 8; focusedBorderColor: #6366f1; borderWidth: 1;");
        txtSearchLoai.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { filterLoai(); }
            @Override public void removeUpdate(DocumentEvent e) { filterLoai(); }
            @Override public void changedUpdate(DocumentEvent e) { filterLoai(); }
        });

        JButton btnSearch = accentButton("Tìm kiếm", ACCENT);
        btnSearch.addActionListener(e -> filterLoai());

        JButton btnReload = outlineButton("↺ Tải lại");
        btnReload.addActionListener(e -> loadLoai());

        JButton btnAdd = accentButton("+ Thêm", SUCCESS);
        btnAdd.addActionListener(e -> openLoaiModal("Thêm loại đơn", null));

        JButton btnEdit = outlineButton("✎ Sửa");
        btnEdit.addActionListener(e -> editSelectedLoai());

        JButton btnDelete = dangerButton("✕ Xóa");
        btnDelete.addActionListener(e -> deleteSelectedLoai());

        p.add(txtSearchLoai, "growx, height 32!");
        p.add(btnSearch, "height 32!");
        p.add(btnReload, "height 32!");
        p.add(btnAdd, "height 32!");
        p.add(btnEdit, "height 32!");
        p.add(btnDelete, "height 32!");
        return p;
    }

    private JScrollPane buildDonTable() {
        Object[] cols = {"Mã đơn", "Nhân viên", "Loại đơn", "Bắt đầu", "Kết thúc", "Số ngày", "Hành động", "Ghi chú"};
        modelDon = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableDon = new JTable(modelDon);
        tableDon.setRowHeight(34);
        tableDon.getTableHeader().setReorderingAllowed(false);
        tableDon.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(tableDon));
        tableDon.putClientProperty(FlatClientProperties.STYLE, "rowHeight: 34; showHorizontalLines: true;");
        if (adminMode) {
            tableDon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int row = tableDon.rowAtPoint(e.getPoint());
                    int col = tableDon.columnAtPoint(e.getPoint());
                    if (row < 0 || col != 6) {
                        return;
                    }
                    int maDonXin = (int) modelDon.getValueAt(row, 0);
                    handleAdminStatusAction(maDonXin);
                }
            });
        }
        loadDon();
        return new JScrollPane(tableDon);
    }

    private JScrollPane buildLoaiTable() {
        Object[] cols = {"Mã loại", "Tên loại đơn", "Mô tả"};
        modelLoai = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableLoai = new JTable(modelLoai);
        tableLoai.setRowHeight(34);
        tableLoai.getTableHeader().setReorderingAllowed(false);
        tableLoai.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(tableLoai));
        tableLoai.putClientProperty(FlatClientProperties.STYLE, "rowHeight: 34; showHorizontalLines: true;");
        loadLoai();
        return new JScrollPane(tableLoai);
    }

    private JScrollPane buildMyDonTable() {
        Object[] cols = {"Mã đơn", "Loại đơn", "Bắt đầu", "Kết thúc", "Số ngày", "Trạng thái", "Ghi chú"};
        modelMyDon = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tableMyDon = new JTable(modelMyDon);
        tableMyDon.setRowHeight(34);
        tableMyDon.getTableHeader().setReorderingAllowed(false);
        tableMyDon.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(tableMyDon));
        tableMyDon.putClientProperty(FlatClientProperties.STYLE, "rowHeight: 34; showHorizontalLines: true;");
        loadMyDon();
        return new JScrollPane(tableMyDon);
    }

    private void loadDon() {
        modelDon.setRowCount(0);
        for (dtodonxinnghiep don : busDon.getList()) {
            modelDon.addRow(toDonRow(don));
        }
    }

    private void loadMyDon() {
        if (modelMyDon == null) {
            return;
        }
        modelMyDon.setRowCount(0);
        for (dtodonxinnghiep don : getVisibleRequests()) {
            modelMyDon.addRow(toMyDonRow(don));
        }
    }

    private void loadLoai() {
        modelLoai.setRowCount(0);
        for (dtoloaidonxinnghiep loai : busLoai.getList()) {
            modelLoai.addRow(new Object[] {loai.getMaLoaiDon(), loai.getTenLoaiDon(), loai.getMoTa()});
        }
    }

    private Object[] toDonRow(dtodonxinnghiep don) {
        return new Object[] {
            don.getMaDonXin(),
            safe(don.getTenNhanVien(), employeeName(don.getMaNhanVien())),
            safe(don.getTenLoaiDon(), leaveTypeName(don.getMaLoaiDon())),
            formatDate(don.getNgayBatDau()),
            formatDate(don.getNgayKetThuc()),
            don.getSoNgayNghi(),
            safe(don.getTrangThai(), ""),
            safe(don.getGhiChu(), "")
        };
    }

    private void filterDon() {
        String kw = txtSearchDon.getText().trim().toLowerCase();
        modelDon.setRowCount(0);
        for (dtodonxinnghiep don : busDon.getList()) {
            String text = String.join(" ",
                    String.valueOf(don.getMaDonXin()),
                    safe(don.getTenNhanVien(), employeeName(don.getMaNhanVien())),
                    safe(don.getTenLoaiDon(), leaveTypeName(don.getMaLoaiDon())),
                    safe(don.getTrangThai(), ""),
                    safe(don.getGhiChu(), "")).toLowerCase();
            if (kw.isEmpty() || text.contains(kw)) {
                modelDon.addRow(toDonRow(don));
            }
        }
    }

    private void filterMyDon() {
        if (modelMyDon == null || txtSearchMyDon == null) {
            return;
        }
        String kw = txtSearchMyDon.getText().trim().toLowerCase();
        modelMyDon.setRowCount(0);
        for (dtodonxinnghiep don : getVisibleRequests()) {
            String text = String.join(" ",
                    String.valueOf(don.getMaDonXin()),
                    safe(don.getTenLoaiDon(), leaveTypeName(don.getMaLoaiDon())),
                    safe(don.getTrangThai(), ""),
                    safe(don.getGhiChu(), "")).toLowerCase();
            if (kw.isEmpty() || text.contains(kw)) {
                modelMyDon.addRow(toMyDonRow(don));
            }
        }
    }

    private void filterLoai() {
        String kw = txtSearchLoai.getText().trim().toLowerCase();
        modelLoai.setRowCount(0);
        for (dtoloaidonxinnghiep loai : busLoai.getList()) {
            String text = String.join(" ", String.valueOf(loai.getMaLoaiDon()), safe(loai.getTenLoaiDon(), ""), safe(loai.getMoTa(), "")).toLowerCase();
            if (kw.isEmpty() || text.contains(kw)) {
                modelLoai.addRow(new Object[] {loai.getMaLoaiDon(), loai.getTenLoaiDon(), loai.getMoTa()});
            }
        }
    }

    private void editSelectedDon() {
        int row = tableDon.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn nghỉ phép để sửa.");
            return;
        }
        int maDonXin = (int) modelDon.getValueAt(row, 0);
        openDonModal("Chỉnh sửa đơn nghỉ phép", busDon.getById(maDonXin));
    }

    private void deleteSelectedDon() {
        int row = tableDon.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một đơn nghỉ phép để xóa.");
            return;
        }
        int maDonXin = (int) modelDon.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa đơn nghỉ phép này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            busDon.delete(maDonXin);
            loadDon();
        }
    }

    private void editSelectedLoai() {
        int row = tableLoai.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một loại đơn để sửa.");
            return;
        }
        int maLoaiDon = (int) modelLoai.getValueAt(row, 0);
        openLoaiModal("Chỉnh sửa loại đơn", busLoai.getById(maLoaiDon));
    }

    private void deleteSelectedLoai() {
        int row = tableLoai.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một loại đơn để xóa.");
            return;
        }
        int maLoaiDon = (int) modelLoai.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa loại đơn này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            busLoai.delete(maLoaiDon);
            loadLoai();
        }
    }

    private void openDonModal(String title, dtodonxinnghiep edit) {
        LeaveRequestInputForm form = new LeaveRequestInputForm(edit, true);
        Option option = ModalDialog.createOption();
        option.getLayoutOption().setSize(-1, 1f).setLocation(Location.TRAILING, Location.TOP);
        ModalDialog.showModal(this, new SimpleModalBorder(form, title, SimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
            if (action == SimpleModalBorder.YES_OPTION) {
                if (form.save()) {
                    controller.close();
                    loadDon();
                    loadMyDon();
                }
            } else {
                controller.close();
            }
        }), option);
    }

    private void openLoaiModal(String title, dtoloaidonxinnghiep edit) {
        LeaveTypeInputForm form = new LeaveTypeInputForm(edit);
        Option option = ModalDialog.createOption();
        option.getLayoutOption().setSize(-1, 1f).setLocation(Location.TRAILING, Location.TOP);
        ModalDialog.showModal(this, new SimpleModalBorder(form, title, SimpleModalBorder.YES_NO_OPTION, (controller, action) -> {
            if (action == SimpleModalBorder.YES_OPTION) {
                if (form.save()) {
                    controller.close();
                    loadLoai();
                }
            } else {
                controller.close();
            }
        }), option);
    }

    private String employeeName(int maNhanVien) {
        return busNhanVien.gettennvbymanv(maNhanVien);
    }

    private String leaveTypeName(int maLoaiDon) {
        dtoloaidonxinnghiep loai = busLoai.getById(maLoaiDon);
        return loai == null ? "" : loai.getTenLoaiDon();
    }

    private String formatDate(Date date) {
        return date == null ? "" : new java.text.SimpleDateFormat("dd/MM/yyyy").format(date);
    }

    private String safe(String value, String fallback) {
        return value == null ? fallback : value;
    }

    private java.util.List<dtodonxinnghiep> getVisibleRequests() {
        if (adminMode) {
            return busDon.getList();
        }
        if (currentEmployeeId <= 0) {
            return busDon.getList();
        }
        return busDon.getListByEmployee(currentEmployeeId);
    }

    private Object[] toMyDonRow(dtodonxinnghiep don) {
        return new Object[] {
            don.getMaDonXin(),
            safe(don.getTenLoaiDon(), leaveTypeName(don.getMaLoaiDon())),
            formatDate(don.getNgayBatDau()),
            formatDate(don.getNgayKetThuc()),
            don.getSoNgayNghi(),
            safe(don.getTrangThai(), "Chờ duyệt"),
            safe(don.getGhiChu(), "")
        };
    }

    private void handleAdminStatusAction(int maDonXin) {
        Object[] options = {"Duyệt", "Không duyệt", "Hủy"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Chọn hành động cho đơn nghỉ phép.",
                "Cập nhật trạng thái",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        if (choice == 0) {
            if (busDon.updateStatus(maDonXin, "Đã duyệt")) {
                loadDon();
                loadMyDon();
            }
        } else if (choice == 1) {
            if (busDon.updateStatus(maDonXin, "Từ chối")) {
                loadDon();
                loadMyDon();
            }
        }
    }

    private JButton accentButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.putClientProperty(FlatClientProperties.STYLE,
                "background: " + toHex(color) + "; foreground: #ffffff; arc: 8; borderWidth: 0;");
        btn.setFocusPainted(false);
        return btn;
    }

    private JButton outlineButton(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 1;");
        return btn;
    }

    private JButton dangerButton(String text) {
        JButton btn = new JButton(text);
        btn.putClientProperty(FlatClientProperties.STYLE,
                "background: " + toHex(DANGER) + "; foreground: #ffffff; arc: 8; borderWidth: 0;");
        btn.setFocusPainted(false);
        return btn;
    }

    private String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private void createTitle(String title) {
        JLabel lb = new JLabel(title);
        lb.putClientProperty(FlatClientProperties.STYLE, "font:+2");
        add(lb, "gapy 5 0");
        add(new JSeparator(), "height 2!,gapy 0 0");
    }

    private class LeaveRequestInputForm extends JPanel {
        private final JComboBox<dtonhanvien> cboNhanVien = new JComboBox<>();
        private final JComboBox<dtoloaidonxinnghiep> cboLoaiDon = new JComboBox<>();
        private final JComboBox<String> cboTrangThai = new JComboBox<>(new String[] {"Chờ duyệt", "Đã duyệt", "Từ chối"});
        private final JDateChooser dateBatDau = new JDateChooser();
        private final JDateChooser dateKetThuc = new JDateChooser();
        private final JTextField txtSoNgay = new JTextField();
        private final JTextArea txtGhiChu = new JTextArea(3, 20);
        private final dtodonxinnghiep editData;
        private final boolean fullForm;

        private LeaveRequestInputForm(dtodonxinnghiep editData, boolean fullForm) {
            this.editData = editData;
            this.fullForm = fullForm;
            setLayout(new MigLayout("fillx,wrap,insets 8 24 8 24,width 460", "[fill]", ""));
            createTitle("Thông tin đơn nghỉ phép");
            build();
            loadCombos();
            if (editData != null) {
                setDefaultValues();
            }
        }

        private void build() {
            txtSoNgay.setEnabled(false);
            txtGhiChu.setLineWrap(true);
            txtGhiChu.setWrapStyleWord(true);
            dateBatDau.setDateFormatString("dd/MM/yyyy");
            dateKetThuc.setDateFormatString("dd/MM/yyyy");

            if (fullForm) {
                add(new JLabel("Nhân viên"), "gapy 5 0");
                add(cboNhanVien);
            }
            add(new JLabel("Loại đơn"), "gapy 5 0");
            add(cboLoaiDon);
            add(new JLabel("Ngày bắt đầu"), "gapy 5 0");
            add(dateBatDau);
            add(new JLabel("Ngày kết thúc"), "gapy 5 0");
            add(dateKetThuc);
            add(new JLabel("Số ngày nghỉ"), "gapy 5 0");
            add(txtSoNgay);
            add(new JLabel("Ghi chú"), "gapy 5 0");
            add(new JScrollPane(txtGhiChu), "h 70!");

            dateBatDau.addPropertyChangeListener("date", e -> updateSoNgay());
            dateKetThuc.addPropertyChangeListener("date", e -> updateSoNgay());
        }

        private void loadCombos() {
            if (fullForm) {
                for (dtonhanvien nv : busNhanVien.getNhanVienList()) {
                    cboNhanVien.addItem(nv);
                }
                cboNhanVien.setRenderer((list, value, index, isSelected, cellHasFocus) -> makeComboLabel(value == null ? "" : value.getDropdownDisplay()));
            }
            for (dtoloaidonxinnghiep loai : busLoai.getList()) {
                cboLoaiDon.addItem(loai);
            }
            cboLoaiDon.setRenderer((list, value, index, isSelected, cellHasFocus) -> makeComboLabel(value == null ? "" : value.getDropdownDisplay()));
        }

        private JLabel makeComboLabel(String text) {
            return new JLabel(text);
        }

        private void ensureNhanVienInCombo(int maNhanVien, JComboBox<dtonhanvien> combo) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                if (combo.getItemAt(i).getManhanvien() == maNhanVien) {
                    return;
                }
            }
            dtonhanvien probe = new dtonhanvien();
            probe.setManhanvien(maNhanVien);
            dtonhanvien nv = busNhanVien.getnv(probe);
            if (nv != null && nv.getManhanvien() == maNhanVien) {
                combo.addItem(nv);
            }
        }

        private void ensureLoaiInCombo(int maLoaiDon) {
            for (int i = 0; i < cboLoaiDon.getItemCount(); i++) {
                if (cboLoaiDon.getItemAt(i).getMaLoaiDon() == maLoaiDon) {
                    return;
                }
            }
            dtoloaidonxinnghiep loai = busLoai.getById(maLoaiDon);
            if (loai != null) {
                cboLoaiDon.addItem(loai);
            }
        }

        private void setDefaultValues() {
            if (fullForm) {
                ensureNhanVienInCombo(editData.getMaNhanVien(), cboNhanVien);
                selectNhanVien(editData.getMaNhanVien());
            }
            ensureLoaiInCombo(editData.getMaLoaiDon());
            selectLoaiDon(editData.getMaLoaiDon());
            dateBatDau.setDate(editData.getNgayBatDau());
            dateKetThuc.setDate(editData.getNgayKetThuc());
            cboTrangThai.setSelectedItem(safe(editData.getTrangThai(), "Chờ duyệt"));
            txtGhiChu.setText(safe(editData.getGhiChu(), ""));
            updateSoNgay();
        }

        private void selectNhanVien(int maNhanVien) {
            for (int i = 0; i < cboNhanVien.getItemCount(); i++) {
                if (cboNhanVien.getItemAt(i).getManhanvien() == maNhanVien) {
                    cboNhanVien.setSelectedIndex(i);
                    return;
                }
            }
        }

        private void selectLoaiDon(int maLoaiDon) {
            for (int i = 0; i < cboLoaiDon.getItemCount(); i++) {
                if (cboLoaiDon.getItemAt(i).getMaLoaiDon() == maLoaiDon) {
                    cboLoaiDon.setSelectedIndex(i);
                    return;
                }
            }
        }

        private void updateSoNgay() {
            Date start = dateBatDau.getDate();
            Date end = dateKetThuc.getDate();
            if (start == null || end == null) {
                txtSoNgay.setText("");
                return;
            }
            long diff = end.getTime() - start.getTime();
            int days = (int) (diff / (1000L * 60 * 60 * 24)) + 1;
            txtSoNgay.setText(String.valueOf(Math.max(days, 0)));
        }

        private boolean save() {
            try {
                dtonhanvien nv = fullForm ? (dtonhanvien) cboNhanVien.getSelectedItem() : null;
                dtoloaidonxinnghiep loai = (dtoloaidonxinnghiep) cboLoaiDon.getSelectedItem();
                if ((fullForm && nv == null) || loai == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên và loại đơn.");
                    return false;
                }
                if (dateBatDau.getDate() == null || dateKetThuc.getDate() == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày bắt đầu và ngày kết thúc.");
                    return false;
                }
                int soNgay = Integer.parseInt(txtSoNgay.getText().trim().isEmpty() ? "0" : txtSoNgay.getText().trim());
                if (soNgay <= 0) {
                    JOptionPane.showMessageDialog(this, "Số ngày nghỉ không hợp lệ.");
                    return false;
                }

                dtodonxinnghiep don = editData == null ? new dtodonxinnghiep() : editData;
                don.setMaNhanVien(fullForm ? nv.getManhanvien() : currentEmployeeId);
                don.setMaLoaiDon(loai.getMaLoaiDon());
                don.setNgayBatDau(dateBatDau.getDate());
                don.setNgayKetThuc(dateKetThuc.getDate());
                don.setSoNgayNghi(soNgay);
                don.setTrangThai(fullForm ? (String) cboTrangThai.getSelectedItem() : "Chờ duyệt");
                don.setNgayDuyet(null);
                don.setGhiChu(txtGhiChu.getText().trim());

                boolean ok = editData == null ? busDon.add(don) : busDon.update(don);
                if (!ok) {
                    JOptionPane.showMessageDialog(this, "Không thể lưu đơn nghỉ phép.");
                    return false;
                }
                JOptionPane.showMessageDialog(this, editData == null ? "Đã thêm đơn nghỉ phép." : "Đã cập nhật đơn nghỉ phép.");
                return true;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
                return false;
            }
        }
    }

    private class LeaveTypeInputForm extends JPanel {
        private final JTextField txtTenLoaiDon = new JTextField();
        private final JTextArea txtMoTa = new JTextArea(4, 20);
        private final dtoloaidonxinnghiep editData;

        private LeaveTypeInputForm(dtoloaidonxinnghiep editData) {
            this.editData = editData;
            setLayout(new MigLayout("fillx,wrap,insets 8 24 8 24,width 420", "[fill]", ""));
            createTitle("Thông tin loại đơn");
            add(new JLabel("Tên loại đơn"), "gapy 5 0");
            add(txtTenLoaiDon);
            add(new JLabel("Mô tả"), "gapy 5 0");
            txtMoTa.setLineWrap(true);
            txtMoTa.setWrapStyleWord(true);
            add(new JScrollPane(txtMoTa), "h 100!");
            if (editData != null) {
                txtTenLoaiDon.setText(safe(editData.getTenLoaiDon(), ""));
                txtMoTa.setText(safe(editData.getMoTa(), ""));
            }
        }

        private boolean save() {
            String ten = txtTenLoaiDon.getText().trim();
            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên loại đơn không được bỏ trống.");
                return false;
            }
            dtoloaidonxinnghiep loai = editData == null ? new dtoloaidonxinnghiep() : editData;
            loai.setTenLoaiDon(ten);
            loai.setMoTa(txtMoTa.getText().trim());
            boolean ok = editData == null ? busLoai.add(loai) : busLoai.update(loai);
            if (!ok) {
                JOptionPane.showMessageDialog(this, "Không thể lưu loại đơn.");
                return false;
            }
            JOptionPane.showMessageDialog(this, editData == null ? "Đã thêm loại đơn." : "Đã cập nhật loại đơn.");
            return true;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Quản lý nghỉ phép");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new formnghiphep());
            frame.setSize(1200, 800);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}