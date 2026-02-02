package gui.main;

import java.awt.*;
import javax.swing.*;

public class LoadingGlassPane extends JComponent {
    private final JLabel messageLabel;
    private final JProgressBar progressBar;
    private final JPanel content;

    public LoadingGlassPane() {
        setOpaque(false);
        setLayout(new GridBagLayout());

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(180, 8));

        messageLabel = new JLabel("Đang tải...");
        messageLabel.setForeground(Color.WHITE);

        content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        JPanel box = new JPanel();
        box.setOpaque(true);
        box.setBackground(new Color(0, 0, 0, 160));
        box.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        box.setLayout(new BorderLayout(0, 10));
        box.add(messageLabel, BorderLayout.NORTH);
        box.add(progressBar, BorderLayout.CENTER);

        content.add(box);
        add(content);

        addMouseListener(new java.awt.event.MouseAdapter() {});
        addKeyListener(new java.awt.event.KeyAdapter() {});
        setFocusTraversalKeysEnabled(false);
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(new Color(0, 0, 0, 120));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }
}
