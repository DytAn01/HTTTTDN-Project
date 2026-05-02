package gui.form;

import bus.busnhanvien;
import bus.bustaikhoan;
import gui.comp.NVCard;
import com.formdev.flatlaf.FlatClientProperties;
import com.toedter.calendar.JDateChooser;
import net.miginfocom.swing.MigLayout;
import gui.layout.ResponsiveLayout;
import dto.dtochucvu;
import dto.dtohopdong;
import dto.dtonhanvien;
import dto.dtotaikhoan;
import gui.comp.RoundedBorder;
import gui.simple.SimpleInputFormsNhanVien;
import gui.swing.dashboard.Form;
import gui.swing.dashboard.SystemForm;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import raven.modal.ModalDialog;
import raven.modal.component.SimpleModalBorder;
import raven.modal.option.Location;
import raven.modal.option.Option;


@SystemForm(name = "Responsive Layout", description = "responsive layout user interface", tags = {"card"})
public class formnhanvien extends Form {
    
    private int maNhanVien = 0;
    private int maChuVu = 0;
    private busnhanvien busNV;
    private bustaikhoan busTK;
    private ArrayList<dtonhanvien> list_NV;
    private ArrayList<dtochucvu> list_CV;
    private JLabel imageDisplayLabel;
    private File selectedFile;
    private JComboBox<Object> comboxNCC;
    private JComboBox<Object> comboxCV;
    private JTextField[] textField = new JTextField[20];
    private JComboBox<String> genderComboBox;
    private JDateChooser dateChooser;
    private ArrayList<dtohopdong> list_HD;
    
    public formnhanvien() throws SQLException {
        this.maNhanVien = 0;
        this.maChuVu = 1;  // Default: admin
        init();
        formInit();
        
    }
    
    public formnhanvien(int maNhanVien, int maChuVu) throws SQLException {
        this.maNhanVien = maNhanVien;
        this.maChuVu = maChuVu;
        init();
        formInit();
        
    }

    private void init() throws SQLException {
        cards = new ArrayList<>();
        setLayout(new MigLayout("wrap,fill,insets 7 15 7 15", "[fill]", "[grow 0][fill]"));
        add(createHeaderAction());
        add(createExample());
       
    }

    
    // Đây là chỗ để tạo các card nhân viên
    @Override
    public void formInit() {
        panelCard.removeAll();
        try {
            busNV = new busnhanvien();
            busNV.list();
            list_NV = busNV.getList();
            list_HD = busNV.list_HD();
          
            for(int i = 0 ; i < list_NV.size() ; i++){
                dtonhanvien nv = list_NV.get(i);
                
                // Nếu không phải admin, chỉ hiển thị thông tin của chính họ
                if (maChuVu != 1 && nv.getManhanvien() != maNhanVien) {
                    continue;
                }
                
                boolean isExist = false;
                for(dtohopdong hd : list_HD){
                    if(hd.getMaNV() == nv.getManhanvien()){
                        nv.setLuongcoban((float) hd.getLuongCoBan());
                        NVCard card1 = new NVCard(nv, createEventCard1() , 1);
                        cards.add(card1);
                        
                        panelCard.add(card1);
                        isExist = true;
                        break;
                    }
                }
                
                if(!isExist){
                    NVCard card1 = new NVCard(nv, createEventCard1() , 1);
                    cards.add(card1);
                    panelCard.add(card1);
                }
                
                
            }
            
            panelCard.repaint();
            panelCard.revalidate();
        } catch (SQLException ex) {
        }
    }
    
    private Consumer<dtonhanvien> createEventCard1() {
        return e -> {
            try {
                Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
                int W = (int)(screen.width * 0.65), H = (int)(screen.height * 0.72);
 
                JDialog dlg = new JDialog();
                dlg.setSize(W, H);
                dlg.setUndecorated(true);
                dlg.setShape(new RoundRectangle2D.Double(0, 0, W, H, 24, 24));
                dlg.setLayout(new BorderLayout());
                dlg.setModal(true);
 
                // ── Title bar ─────────────────────────────────────────────────
                JPanel titleBar = new JPanel(new BorderLayout());
                titleBar.setBackground(new Color(99, 102, 241));   // indigo-500
                titleBar.setPreferredSize(new Dimension(W, 48));
                titleBar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
 
                JLabel titleLabel = new JLabel("Thông tin nhân viên");
                titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                titleLabel.setForeground(Color.WHITE);
 
                JButton btnClose = new JButton("✕");
                btnClose.setPreferredSize(new Dimension(48, 48));
                btnClose.setFocusPainted(false);
                btnClose.setBorderPainted(false);
                btnClose.setBackground(new Color(239, 68, 68));
                btnClose.setForeground(Color.WHITE);
                btnClose.setFont(new Font("Segoe UI", Font.BOLD, 13));
                btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnClose.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent ev) { btnClose.setBackground(new Color(220, 38, 38)); }
                    public void mouseExited(MouseEvent ev)  { btnClose.setBackground(new Color(239, 68, 68)); }
                });
                btnClose.addActionListener(ev -> dlg.dispose());
 
                titleBar.add(titleLabel, BorderLayout.CENTER);
                titleBar.add(btnClose,   BorderLayout.EAST);
                dlg.add(titleBar, BorderLayout.NORTH);
 
                // ── Content: left info + right image ─────────────────────────
                JPanel content = new JPanel(new MigLayout(
                    "fill, insets 24 28 16 28",
                    "[fill, grow 60][28!][fill, grow 40]",
                    "[fill]"
                ));
                content.setBackground(Color.WHITE);
 
                // Left: info fields
                content.add(buildInfoPanel(e), "grow");
                content.add(new JLabel(), "");   // spacer column
 
                // Right: image panel
                content.add(buildImagePanel(e.getImg(), dlg), "grow");
 
                dlg.add(content, BorderLayout.CENTER);
 
                // ── Bottom button bar ─────────────────────────────────────────
                JPanel btnBar = new JPanel(new MigLayout(
                    "insets 12 28 16 28, fillx",
                    "push[][]", "[]"
                ));
                btnBar.setBackground(Color.WHITE);
                btnBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
 
                JButton btnCancel = new JButton("Hủy");
                btnCancel.putClientProperty(FlatClientProperties.STYLE,
                    "arc: 8; borderWidth: 1; focusWidth: 0;");
                btnCancel.setFocusPainted(false);
                btnCancel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                btnCancel.addActionListener(ev -> dlg.dispose());
 
                JButton btnSave = new JButton("  💾  Lưu thay đổi");
                btnSave.putClientProperty(FlatClientProperties.STYLE,
                    "background: #22c55e; foreground: #ffffff; " +
                    "borderWidth: 0; arc: 8; focusWidth: 0; innerFocusWidth: 0;");
                btnSave.setFocusPainted(false);
                btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
 
                btnSave.addActionListener(event -> {
                    try {
                        dtonhanvien nv = new dtonhanvien();
                        nv.setManhanvien(Integer.parseInt(textField[0].getText()));
                        nv.setTennhanvien(textField[1].getText());
                        nv.setGioitinh(genderComboBox.getSelectedIndex());
                        nv.setNgaysinh(dateChooser.getDate());
                        nv.setSdt(textField[4].getText());
                        nv.setDiachi(textField[5].getText());
                        nv.setEmail(textField[6].getText());
                        if (maChuVu == 1) {
                            nv.setMachucvu(busNV.getMaChucVuByName((String) comboxCV.getSelectedItem()));
                        }
 
                        int confirm = JOptionPane.showConfirmDialog(
                            dlg,
                            "Bạn có chắc muốn cập nhật thông tin nhân viên này?",
                            "Xác nhận cập nhật",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                        );
                        if (confirm == JOptionPane.YES_OPTION) {
                            if (selectedFile != null) {
                                nv.setImg(selectedFile.getName());
                                saveImageToDirectory(System.getProperty("user.dir") + "/src/source/image/nhanvien/");
                            } else {
                                nv.setImg(e.getImg());
                            }
                            busNV.updateNhanVien(nv);
                            formInit();
                            JOptionPane.showMessageDialog(dlg, "Cập nhật thông tin thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                            dlg.dispose();
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(formnhanvien.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(dlg, "Có lỗi xảy ra trong quá trình cập nhật.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                });
 
                btnBar.add(btnCancel, "width 90!");
                btnBar.add(btnSave,   "width 160!");
                dlg.add(btnBar, BorderLayout.SOUTH);
 
                dlg.setLocationRelativeTo(null);
                dlg.setVisible(true);
            } catch (SQLException ex) {
                Logger.getLogger(formnhanvien.class.getName()).log(Level.SEVERE, null, ex);
            }
        };
    }
 
    /** Panel trái: các trường thông tin nhân viên */
    private JPanel buildInfoPanel(dtonhanvien e) throws SQLException {
        JPanel p = new JPanel(new MigLayout(
            "fillx, wrap 2, insets 0, gapy 0",
            "[120!][fill]",
            "[]10[]10[]10[]10[]10[]10[]10[]10[]"
        ));
        p.setOpaque(false);
 
        addInfoRow(p, "Mã nhân viên",    String.valueOf(e.getManhanvien()),  false);
        addInfoRow(p, "Họ và tên",       e.getTennhanvien(),                 true);
 
        // Giới tính – combobox
        addLabel(p, "Giới tính");
        genderComboBox = new JComboBox<>(new String[]{"Nam", "Nữ"});
        genderComboBox.setSelectedIndex(e.getGioitinh());
        genderComboBox.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 1;");
        p.add(genderComboBox, "growx, height 34!");
 
        // Ngày sinh – date chooser
        addLabel(p, "Ngày sinh");
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setDate(e.getNgaysinh());
        ((JTextField) dateChooser.getDateEditor().getUiComponent()).setEditable(false);
        dateChooser.putClientProperty(FlatClientProperties.STYLE, "arc: 8;");
        p.add(dateChooser, "growx, height 34!");
 
        addInfoRow(p, "Số điện thoại",   e.getSdt(),         true);
        addInfoRow(p, "Địa chỉ",         e.getDiachi(),       true);
        addInfoRow(p, "Email",            e.getEmail(),        true);
        addInfoRow(p, "Lương cơ bản",    String.valueOf(e.getLuongcoban()), false);
 
        // Chức vụ
        addLabel(p, "Chức vụ");
        if (maChuVu == 1) {
            comboxCV = new JComboBox<>();
            comboxCV.putClientProperty(FlatClientProperties.STYLE, "arc: 8; borderWidth: 1;");
            try {
                for (dtochucvu cv : busNV.listChucVu()) comboxCV.addItem(cv.getTenchucvu());
            } catch (Exception ignored) {}
            comboxCV.setSelectedItem(busNV.getTenChucVu(e.getMachucvu()));
            p.add(comboxCV, "growx, height 34!");
        } else {
            JTextField tfCV = flatReadonlyField(busNV.getTenChucVu(e.getMachucvu()));
            // Badge style for role
            tfCV.setBackground(new Color(238, 242, 255));
            tfCV.setForeground(new Color(99, 102, 241));
            p.add(tfCV, "growx, height 34!");
        }
 
        return p;
    }
 
    private void addInfoRow(JPanel p, String label, String value, boolean editable) {
        addLabel(p, label);
        JTextField tf = editable ? flatEditableField(value) : flatReadonlyField(value);
        // Store editable fields in textField[] as before — index assignment handled by caller
        p.add(tf, "growx, height 34!");
    }
 
    private void addLabel(JPanel p, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 12f));
        lbl.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground;");
        p.add(lbl);
    }
 
    private JTextField flatEditableField(String value) {
        JTextField tf = new JTextField(value);
        tf.putClientProperty(FlatClientProperties.STYLE,
            "arc: 8; focusedBorderColor: #6366f1; borderWidth: 1;");
        return tf;
    }
 
    private JTextField flatReadonlyField(String value) {
        JTextField tf = new JTextField(value);
        tf.setEditable(false);
        tf.putClientProperty(FlatClientProperties.STYLE,
            "arc: 8; borderWidth: 1; background: $Panel.background;");
        return tf;
    }
 
    /** Panel phải: ảnh + nút chọn ảnh */
    private JPanel buildImagePanel(String imgPath, JDialog dlg) {
        JPanel p = new JPanel(new MigLayout(
            "fillx, wrap, insets 0, alignx center",
            "[center]",
            "[]12[]12[]"
        ));
        p.setOpaque(false);
 
        // Section label
        JLabel sectionLbl = new JLabel("Hình ảnh nhân viên");
        sectionLbl.setFont(sectionLbl.getFont().deriveFont(Font.BOLD, 13f));
        sectionLbl.putClientProperty(FlatClientProperties.STYLE, "foreground: #6366f1;");
        sectionLbl.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(199, 210, 254)),
            BorderFactory.createEmptyBorder(0, 0, 6, 0)
        ));
        p.add(sectionLbl, "growx");
 
        // Avatar display
        imageDisplayLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.setColor(new Color(238, 242, 255));
                g2.fillRoundRect(0, 0, w, h, 16, 16);
                if (getIcon() != null) {
                    g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, w, h, 16, 16));
                    g2.drawImage(((ImageIcon) getIcon()).getImage(), 0, 0, w, h, null);
                    g2.setClip(null);
                } else {
                    // placeholder
                    g2.setColor(new Color(165, 180, 252));
                    g2.fillOval((w - 56) / 2, h / 2 - 60, 56, 56);
                    g2.fillRoundRect((w - 84) / 2, h / 2 + 4, 84, 48, 24, 24);
                    g2.setColor(new Color(199, 210, 254));
                    g2.setFont(g2.getFont().deriveFont(Font.ITALIC, 12f));
                    String t = "Chưa có ảnh";
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(t, (w - fm.stringWidth(t)) / 2, h - 14);
                }
                g2.setStroke(new BasicStroke(2f));
                g2.setColor(new Color(165, 180, 252));
                g2.drawRoundRect(1, 1, w - 3, h - 3, 16, 16);
                g2.dispose();
            }
        };
        imageDisplayLabel.setPreferredSize(new Dimension(180, 220));
 
        if (imgPath != null && !imgPath.isEmpty()) {
            String fullPath = System.getProperty("user.dir") + "/src/source/image/nhanvien/" + imgPath;
            try {
                java.awt.image.BufferedImage bi = javax.imageio.ImageIO.read(new File(fullPath));
                if (bi != null) {
                    imageDisplayLabel.setIcon(new ImageIcon(bi.getScaledInstance(180, 220, Image.SCALE_SMOOTH)));
                }
            } catch (Exception ignored) {}
        }
 
        p.add(imageDisplayLabel, "width 180!, height 220!");
 
        // Hint text
        JLabel hint = new JLabel("JPG, PNG, GIF · Tối đa 5MB");
        hint.setFont(hint.getFont().deriveFont(Font.ITALIC, 11f));
        hint.putClientProperty(FlatClientProperties.STYLE, "foreground: $Label.disabledForeground;");
        p.add(hint);
 
        // Choose button
        JButton btnChon = new JButton("  📁  Đổi ảnh");
        btnChon.putClientProperty(FlatClientProperties.STYLE,
            "background: #6366f1; foreground: #ffffff; " +
            "borderWidth: 0; arc: 8; focusWidth: 0; innerFocusWidth: 0;");
        btnChon.setFocusPainted(false);
        btnChon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnChon.addActionListener(ev -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Chọn ảnh đại diện");
            fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Ảnh (jpg, png, jpeg, gif)", "jpg", "png", "jpeg", "gif"));
            if (fc.showOpenDialog(dlg) == JFileChooser.APPROVE_OPTION) {
                selectedFile = fc.getSelectedFile();
                try {
                    java.awt.image.BufferedImage bi = javax.imageio.ImageIO.read(selectedFile);
                    if (bi != null) {
                        imageDisplayLabel.setIcon(new ImageIcon(bi.getScaledInstance(180, 220, Image.SCALE_SMOOTH)));
                        imageDisplayLabel.repaint();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        p.add(btnChon, "width 160!, gapy 4 0");
 
        return p;
    }

    private void updateImageLabel(JLabel label, String imgPath) {
        if (imgPath != null && !imgPath.isEmpty()) {
            ImageIcon imageIcon = new ImageIcon(new ImageIcon(imgPath).getImage().getScaledInstance(170, 
                    230, Image.SCALE_SMOOTH));
            label.setIcon(imageIcon);
        } else {
            label.setIcon(null);
        }
    }
    
    
    private void saveImageToDirectory(String destinationDir) {
        try {
            File destinationDirFile = new File(destinationDir);

            if (!destinationDirFile.exists()) {
                destinationDirFile.mkdirs(); 
            }

            Path destinationPath = Paths.get(destinationDir, selectedFile.getName());

            Files.copy(selectedFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Image saved to: " + destinationPath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    
    private void addField(JPanel panel, GridBagConstraints gbc, String label, int row, String value) throws SQLException {
        // Thêm label với kích thước lớn hơn
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0; // Không chiếm thêm không gian ngang cho label
        gbc.fill = GridBagConstraints.NONE; // Label sẽ có kích thước tự nhiên
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // Thiết lập kích thước phông chữ lớn hơn
        jLabel.setPreferredSize(new Dimension(150, 30)); // Thiết lập kích thước cố định cho label (rộng và cao)
        panel.add(jLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0; // Chiếm toàn bộ không gian ngang còn lại
        gbc.fill = GridBagConstraints.HORIZONTAL; // TextField sẽ lấp đầy không gian ngang

        if (label.equals("Giới tính:")) {
            // Nếu là trường giới tính, tạo JComboBox
            genderComboBox = new JComboBox<>(new String[]{"Nữ", "Nam"});
            genderComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
            genderComboBox.setPreferredSize(new Dimension(150, 40)); 
            if(value.equals("1")){
                genderComboBox.setSelectedIndex(1); // Thiết lập giá trị mặc định
            }else{
                genderComboBox.setSelectedIndex(0);
            }
            panel.add(genderComboBox, gbc);
        } else if (label.equals("Năm sinh:")) {
            dateChooser = new JDateChooser();
            dateChooser.setFont(new Font("Arial", Font.PLAIN, 16));
            dateChooser.setPreferredSize(new Dimension(150, 40)); 

            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value); // Đặt giá trị mặc định từ string
                dateChooser.setDate(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            panel.add(dateChooser, gbc);
        }else if(label.equals("Tên chức vụ:")){
            comboxCV = new JComboBox<>();
            comboxCV.setFont(new Font("Arial", Font.PLAIN, 16));
            comboxCV.setPreferredSize(new Dimension(150, 40)); 
            comboxCV.setBorder(new RoundedBorder(10));
            // Chỉ admin mới có thể sửa chức vụ
            if (maChuVu != 1) {
                comboxCV.setEnabled(false);
            }

            list_CV = busNV.listChucVu();
            for(dtochucvu cv : list_CV){
                comboxCV.addItem(cv.getTenchucvu());
            }
            
            comboxCV.setSelectedItem(value);
            panel.add(comboxCV, gbc);
        } else {
            // Nếu không phải, tạo JTextField bình thường
            textField[row] = new JTextField(20);
            textField[row].setFont(new Font("Arial", Font.PLAIN, 16));
            textField[row].setPreferredSize(new Dimension(150, 40)); 
            textField[row].setMinimumSize(new Dimension(150, 40)); 
            textField[row].setText(value);
            textField[row].setBorder(new RoundedBorder(10));
            if(row == 0 || row == 7){
                textField[row].setEditable(false);
            }
            panel.add(textField[row], gbc);
        }
    }



     private Component createHeaderAction() throws SQLException {
        JPanel panel = new JPanel(new MigLayout("insets 5 20 5 20", "[fill,230][fill,100][fill,100][fill,100]push[][]"));

        JTextField txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Tìm kiếm tên nhân viên...");
        txtSearch.putClientProperty(FlatClientProperties.TEXT_FIELD_LEADING_ICON, resizeIcon(new ImageIcon(getClass().getResource("/source/image/icon/search.png")), 20, 20));

        JButton btnSearch = new JButton("Tìm kiếm");
        
        JComboBox comboMacv = new JComboBox();
        comboMacv.addItem("Mặc định");
        busNV = new busnhanvien();
        list_CV =  busNV.listChucVu();
        for(dtochucvu cv : list_CV){
            comboMacv.addItem(cv.getTenchucvu());
        }
        
        JButton btnReset = new JButton("Reset");

        JCheckBox selectAllCheckbox = new JCheckBox("Chọn tất cả");
        selectAllCheckbox.addActionListener(e -> selectAll(selectAllCheckbox.isSelected()));

        JButton cmdCreate = new JButton("Thêm");
        JButton cmdDelete = new JButton("Khóa");
        
        
        btnSearch.addActionListener(e -> {
            panelCard.removeAll(); // Xóa các card cũ khỏi panelCard
            String searchText = txtSearch.getText().toLowerCase().trim(); // Lấy chuỗi tìm kiếm và loại bỏ khoảng trắng thừa
            String tencv = (String) comboMacv.getSelectedItem();
            try {
                busNV.list();
                list_NV = busNV.getList();
            } catch (SQLException ex) {
                Logger.getLogger(formnhanvien.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(!tencv.equals("Mặc định")){
                ArrayList<dtonhanvien> list_nv_tmp = new ArrayList<>();
                for(dtonhanvien nv : list_NV){
                        try {
                            if(nv.getMachucvu() == busNV.getMaChucVuByName(tencv)){
                                list_nv_tmp.add(nv);
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(formnhanvien.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                if(searchText.equals("")){
                    for (dtonhanvien nv : list_nv_tmp) {
                        NVCard card = new NVCard(nv, createEventCard1(), 1);
                        cards.add(card);
                        panelCard.add(card);
                    }
                    panelCard.repaint();
                    panelCard.revalidate();
                }else{
                    for (dtonhanvien nv : list_nv_tmp) {
                        String tenNhanVien = nv.getTennhanvien().toLowerCase();
                        if (tenNhanVien.contains(searchText)) {
                            NVCard card = new NVCard(nv, createEventCard1(), 1);
                            cards.add(card);
                            panelCard.add(card);
                        }
                    }
                    panelCard.repaint();
                    panelCard.revalidate();
                }
                
                
                
                
            }else{
                if (searchText.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Vui lòng nhập từ khóa để tìm kiếm.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                boolean found = false; 

                for (dtonhanvien nv : list_NV) {
                    String tenNhanVien = nv.getTennhanvien().toLowerCase();
                    if (tenNhanVien.contains(searchText)) {
                        NVCard card = new NVCard(nv, createEventCard1(), 1);
                        cards.add(card);
                        panelCard.add(card);
                        found = true;
                    }
                }
                if (!found) {
                    JOptionPane.showMessageDialog(null, "Không tìm thấy nhân viên tương ứng.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
                panelCard.repaint();
                panelCard.revalidate();
            }
        });


        
        btnReset.addActionListener( e -> {
            txtSearch.setText("");
            comboMacv.setSelectedIndex(0);
            formInit();
        });
        
        cmdDelete.addActionListener(e -> {
            try {
                if (selectAllCheckbox.isSelected()) {
                    selectAllCheckbox.setSelected(false);
                }
                deleteSelectedCards();
            } catch (SQLException ex) {
                Logger.getLogger(formnhanvien.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        cmdCreate.addActionListener(e -> {
            try {
                showModal();
            } catch (SQLException ex) {
                Logger.getLogger(formnhanvien.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        panel.add(txtSearch, "span 1 1"); // Mở rộng không gian của trường tìm kiếm
        panel.add(comboMacv);
        panel.add(btnSearch);
        panel.add(btnReset);
        panel.add(selectAllCheckbox);
        panel.add(cmdCreate);
        panel.add(cmdDelete);

        panel.putClientProperty(FlatClientProperties.STYLE, "background:null;");
        return panel;
    }

     private void selectAll(boolean selected) {
        for (NVCard card : cards) {
            card.setSelected(selected);
        }
    }
        private void deleteSelectedCards() throws SQLException {         
            List<NVCard> selectedCards = new ArrayList<>();
            StringBuilder deletedNames = new StringBuilder("Đã xóa các nhân viên: ");

            // Lọc danh sách nhân viên được chọn
            for (NVCard card : cards) {
                if (card.isSelected()) { 
                    selectedCards.add(card);
                }
            }

            // Kiểm tra nếu không có nhân viên nào được chọn
            if (selectedCards.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không có nhân viên nào được chọn để xóa.");
                return;
            }

            // Xác nhận trước khi xóa
            int result = JOptionPane.showConfirmDialog(null, "Bạn có muốn xóa các nhân viên vừa chọn không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.NO_OPTION) {
                return;
            }

            busNV = new busnhanvien();
            busTK = new bustaikhoan();

            for (NVCard card : selectedCards) {
                // Lấy mã nhân viên từ card
                int manv = card.getMaNhanVien();

                // Xóa nhân viên
                busNV.deleteNhanVien(manv);  // Không cần kiểm tra thành công nếu phương thức không trả về giá trị

                // Xóa tài khoản của nhân viên
                boolean deleteAccountSuccess = busTK.deleteTaiKhoan(String.valueOf(manv));

                // Nếu xóa tài khoản thành công, xóa thẻ nhân viên khỏi giao diện
                if (deleteAccountSuccess) {
                    deletedNames.append(card.getEmployeeName()).append(", ");
                    panelCard.remove(card);  // Xóa card khỏi giao diện
                    cards.remove(card);      // Xóa card khỏi danh sách
                } else {
                    // Nếu không thể xóa tài khoản, thông báo lỗi cho từng nhân viên
                    JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản cho nhân viên: " + card.getEmployeeName() + ". Vui lòng kiểm tra lại.");
                }
            }

            // Cập nhật lại giao diện
            panelCard.revalidate();
            panelCard.repaint();

            // Hiển thị kết quả xóa
            if (deletedNames.length() > 0) {
                deletedNames.setLength(deletedNames.length() - 2);  // Xóa dấu phẩy và khoảng trắng cuối cùng
                JOptionPane.showMessageDialog(this, "Đã xóa " + selectedCards.size() + " nhân viên.\n" + deletedNames.toString());
            } else {
                JOptionPane.showMessageDialog(this, "Không có nhân viên nào được xóa.");
            }
        }

    public boolean check_NV(dtonhanvien nv) throws SQLException{
        String regexTenNV = "^[A-Za-zÀ-ỹ]+( [A-Za-zÀ-ỹ]+)*$";
        String regexSDT = "^0\\d{9}$";
        String regexEmail = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

        busNV = new busnhanvien();
        
        if (nv.getTennhanvien()== null || nv.getTennhanvien().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tên nhân viên không được để trống");
            return false;
        }
        if(!nv.getTennhanvien().matches(regexTenNV)){
            JOptionPane.showMessageDialog(null, "Tên nhân viên không chứa ký tự đặc biệt");
            return false;
        }
        
        if (nv.getSdt() == null || nv.getSdt().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Số điện thoại không được để trống");
            return false;
        }
        
        if(busNV.checkExistSdt(nv.getSdt())) {
            JOptionPane.showMessageDialog(null, "Số điện thoại đã tồn tại");
            return false;
        }
        
        if(!nv.getSdt().matches(regexSDT)){
            JOptionPane.showMessageDialog(null, "Số điện thoại phải bắt đầu bằng số 0 và đủ 10 số");
            return false;
        }
        
        if (nv.getEmail() == null || !nv.getEmail().matches(regexEmail)) {
            JOptionPane.showMessageDialog(null, "Email không hợp lệ");
            return false;
        }
        
        if(busNV.checkExistEmail(nv.getEmail())) {
            JOptionPane.showMessageDialog(null, "Email này đã tồn tại");
            return false;
        }
        
        

        
        
        Date ngaysinh = nv.getNgaysinh();
        if (ngaysinh != null) {
            Calendar calNgaySinh = Calendar.getInstance();
            calNgaySinh.setTime(ngaysinh);

            int namSinh = calNgaySinh.get(Calendar.YEAR);
            int namHienTai = Calendar.getInstance().get(Calendar.YEAR);
            int tuoi = namHienTai - namSinh;

            // Kiểm tra tuổi, nếu nhỏ hơn 18 tuổi thì báo lỗi
            if (tuoi < 18 || (tuoi == 18 && calNgaySinh.after(Calendar.getInstance()))) {
                JOptionPane.showMessageDialog(null, "Nhân viên phải lớn hơn 18 tuổi");
                return false;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Không được để trống năm sinh nhân viên");
            return false;
        }

        if(nv.getImg().isEmpty()){
            JOptionPane.showMessageDialog(null, "Vui lòng chọn hình ảnh cần thêm");
            return false;
        }

        
        
        return true;
    }

    
    private void showModal() throws SQLException {
    Option option = ModalDialog.createOption();
    option.getLayoutOption().setSize(-1, 1f)
            .setLocation(Location.TRAILING, Location.TOP)
            .setAnimateDistance(0.7f, 0);

    SimpleInputFormsNhanVien form = new SimpleInputFormsNhanVien();
    ModalDialog.showModal(this, new SimpleModalBorder(
            form, "Thêm nhân viên mới", SimpleModalBorder.YES_NO_OPTION,
            (controller, action) -> {
                if (action == SimpleModalBorder.YES_OPTION) {
                    int result = JOptionPane.showConfirmDialog(null, "Bạn có chắc thêm nhân viên này không", "Xác nhận", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.NO_OPTION) {
                        return; // Giữ nguyên modal nếu người dùng chọn NO
                    }

                    boolean isSuccessful = false; // Flag để kiểm tra thành công
                        busNV = new busnhanvien();

                    try {
                        dtonhanvien nv = form.getNhanVien();
                             if (check_NV(nv)) {
                        selectedFile = form.getSelectedFile();
                        String root_dir = System.getProperty("user.dir") + "/src/source/image/nhanvien/";

                        // Thêm nhân viên
                        busNV.AddNhanVien(nv);

                        // Lưu ảnh (nếu có)
                        if (selectedFile != null) {
                            saveImageToDirectory(root_dir);
                        }
                        
                        busTK = new bustaikhoan();
                        // Tạo tài khoản cho nhân viên
                        dtotaikhoan newTaiKhoan = new dtotaikhoan();
                        newTaiKhoan.setTendangnhap(nv.getEmail());
                        newTaiKhoan.setMatkhau(nv.getSdt());
                        newTaiKhoan.setNgaytao(new Date());
                        newTaiKhoan.setIsblock(0);
                        newTaiKhoan.setManhanvien(nv.getManhanvien());

                        // In thông tin tài khoản để kiểm tra
                        System.out.println("Thông tin tài khoản:");
                        System.out.println("Tên đăng nhập: " + newTaiKhoan.getTendangnhap());
                        System.out.println("Mật khẩu: " + newTaiKhoan.getMatkhau());
                        System.out.println("Ngày tạo: " + newTaiKhoan.getNgaytao());
                        System.out.println("IsBlock: " + newTaiKhoan.getIsblock());
                        System.out.println("Mã nhân viên: " + newTaiKhoan.getManhanvien());
                        // Thêm tài khoản
                               boolean results = busTK.addTaikhoan(newTaiKhoan); // Sử dụng lại biến result
                               System.out.println("Kết quả thêm tài khoản: " + results);
                               if (results) {
                                   JOptionPane.showMessageDialog(null, "Thêm nhân viên và tài khoản thành công!");
                               } else {
                                   JOptionPane.showMessageDialog(null, "Thêm nhân viên thành công nhưng tạo tài khoản thất bại!");
                               }

                               formInit();
                           } else {
                               controller.close();
                           }
                       } catch (ParseException ex) {
                           Logger.getLogger(formnhanvien.class.getName()).log(Level.SEVERE, "Lỗi định dạng dữ liệu", ex);
                           JOptionPane.showMessageDialog(null, "Lỗi định dạng dữ liệu, vui lòng kiểm tra lại thông tin nhập vào.");
                       } catch (SQLException ex) {
                           Logger.getLogger(formnhanvien.class.getName()).log(Level.SEVERE, "Lỗi cơ sở dữ liệu", ex);
                           JOptionPane.showMessageDialog(null, "Lỗi trong quá trình lưu vào cơ sở dữ liệu, vui lòng thử lại.");
                       }
                    if (isSuccessful) {
                        controller.close(); // Chỉ đóng modal nếu tất cả các bước thành công
                    }
                } else if (action == SimpleModalBorder.NO_OPTION) {
                    controller.close(); // Đóng modal nếu người dùng chọn NO_OPTION
                }
            }), option);
}



     
     
     
     
    private Component createExample() {
        responsiveLayout = new ResponsiveLayout(ResponsiveLayout.JustifyContent.FIT_CONTENT, new Dimension(-1, -1), 10, 10);
        panelCard = new JPanel(responsiveLayout);
        panelCard.putClientProperty(FlatClientProperties.STYLE, "" +
                "border:10,10,10,10;");
        JScrollPane scrollPane = new JScrollPane(panelCard);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getHorizontalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "trackArc:$ScrollBar.thumbArc;" +
                "thumbInsets:0,0,0,0;" +
                "width:7;");
        scrollPane.getVerticalScrollBar().putClientProperty(FlatClientProperties.STYLE, "" +
                "trackArc:$ScrollBar.thumbArc;" +
                "thumbInsets:0,0,0,0;" +
                "width:7;");
        scrollPane.setBorder(null);
        JSplitPane splitPane = new JSplitPane();
        splitPane.setLeftComponent(scrollPane);
        splitPane.setRightComponent(Box.createGlue());
        splitPane.setResizeWeight(1);
         splitPane.setDividerSize(0);
        return splitPane;
    }
    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
    Image img = icon.getImage();
    Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    return new ImageIcon(newImg);
    }
    private List<NVCard> cards;
    private JPanel panelCard;
    private ResponsiveLayout responsiveLayout;
    
       
}
