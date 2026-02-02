package gui.form;

import bus.buschamcong;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import bus.buschitietchamcong;
import dto.dtochamcong;
import dto.dtochitietchamcong;
import gui.table.TableHeaderAlignment;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import net.miginfocom.swing.MigLayout;

public class formchamcongcuatoi extends JPanel {

    private final int maNhanVien;
    private final buschamcong buscc = new buschamcong();
    private final buschitietchamcong busctcc = new buschitietchamcong();
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> cboMonth;
    private JComboBox<String> cboYear;
    private JTextField txtSearch;

    public formchamcongcuatoi(int maNhanVien) {
        this.maNhanVien = maNhanVien;
        init();
    }

    private void init() {
        setLayout(new MigLayout("fillx,wrap,insets 7 15 7 15", "[fill]", "[][fill,grow]"));
        add(createInfo("Bảng chấm công của tôi", "Theo dõi bảng chấm công theo tháng của bạn.", 1));
        add(createTable(), "gapx 7 7");
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

    private Component createTable() {
        JPanel panel = new JPanel(new MigLayout("fillx,wrap,insets 10 0 10 0", "[fill]", "[][][]0[fill,grow]"));

        Object columns[] = new Object[]{"MÃ CHẤM CÔNG", "NGÀY", "THÁNG", "NĂM", "GIỜ LÀM THÊM", "TỔNG GIỜ LÀM", "TỔNG NGÀY LÀM", "CHI TIẾT"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        table.getTableHeader().setDefaultRenderer(new TableHeaderAlignment(table) {
            @Override
            protected int getAlignment(int column) {
                return SwingConstants.CENTER;
            }
        });

        table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, "height:30;hoverBackground:null;pressedBackground:null;separatorColor:$TableHeader.background;");
        table.putClientProperty(FlatClientProperties.STYLE, "rowHeight:30;showHorizontalLines:true;intercellSpacing:0,1;cellFocusColor:$TableHeader.hoverBackground;selectionBackground:$TableHeader.hoverBackground;selectionInactiveBackground:$TableHeader.hoverBackground;selectionForeground:$Table.foreground;");
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "trackArc:$ScrollBar.thumbArc;trackInsets:3,3,3,3;thumbInsets:3,3,3,3;background:$Table.background;");

        JLabel title = new JLabel("Bảng chấm công theo tháng");
        title.putClientProperty(FlatClientProperties.STYLE, "font:bold +2");
        panel.add(title, "gapx 20");

        panel.add(createHeader());
        JSeparator separator = new JSeparator();
        separator.putClientProperty(FlatClientProperties.STYLE, "foreground:$Table.gridColor;");
        panel.add(separator, "height 2");
        panel.add(scrollPane);

        loadData(null, null);
        return panel;
    }

    private Component createHeader() {
        JPanel panel = new JPanel(new MigLayout("insets 5 20 5 20", "[fill]push[][]", ""));

        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new java.awt.Dimension(250, 28));
        txtSearch.setMaximumSize(new java.awt.Dimension(260, 28));
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, new FlatSVGIcon("source/image/icon/search.svg", 0.4f));
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm theo tháng/năm");

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

        cboMonth.addActionListener(e -> refreshByFilter());
        cboYear.addActionListener(e -> refreshByFilter());

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                refreshByFilter();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                refreshByFilter();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                refreshByFilter();
            }
        });

        panel.add(txtSearch);
        panel.add(cboMonth);
        panel.add(cboYear);
        return panel;
    }

    private void refreshByFilter() {
        Integer month = parseMonth((String) cboMonth.getSelectedItem());
        Integer year = parseYear((String) cboYear.getSelectedItem());
        loadData(month, year);
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

    private void loadData(Integer month, Integer year) {
        model.setRowCount(0);
        String query = txtSearch != null ? txtSearch.getText().trim().toLowerCase() : "";
        buscc.getlist();
        busctcc.getlist();
        Map<Integer, Date> latestDateByChamCong = new HashMap<>();
        for (dtochitietchamcong detail : busctcc.dsctcc) {
            Date ngay = detail.getNgaychamcong();
            int maChamCong = detail.getMachamcong();
            Date current = latestDateByChamCong.get(maChamCong);
            if (ngay != null && (current == null || ngay.after(current))) {
                latestDateByChamCong.put(maChamCong, ngay);
            }
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        for (dtochamcong cc : buscc.dscc) {
            if (cc.getManhanvien() != maNhanVien) {
                continue;
            }
            if (month != null && cc.getThangchamcong() != month) {
                continue;
            }
            if (year != null && cc.getNamchamcong() != year) {
                continue;
            }
            if (!query.isEmpty()) {
                String monthText = String.valueOf(cc.getThangchamcong());
                String yearText = String.valueOf(cc.getNamchamcong());
                if (!monthText.contains(query) && !yearText.contains(query)) {
                    continue;
                }
            }
            Date latestDate = latestDateByChamCong.get(cc.getMachamcong());
            String ngayText = latestDate != null ? dateFormat.format(latestDate) : "";
            model.addRow(new Object[]{
                cc.getMachamcong(),
                ngayText,
                cc.getThangchamcong(),
                cc.getNamchamcong(),
                cc.getSogiolamthem(),
                cc.getSogiolamviec(),
                cc.getSongaylamviec(),
                cc.getChitiet()
            });
        }
    }

    private ArrayList<Integer> getAvailableYears() {
        Set<Integer> years = new LinkedHashSet<>();
        buscc.getlist();
        for (dtochamcong cc : buscc.dscc) {
            if (cc.getManhanvien() == maNhanVien) {
                years.add(cc.getNamchamcong());
            }
        }
        return new ArrayList<>(years);
    }
}
