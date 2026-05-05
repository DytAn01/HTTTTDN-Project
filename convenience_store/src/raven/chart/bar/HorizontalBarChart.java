package raven.chart.bar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JLabel;
import raven.chart.data.pie.DefaultPieDataset;

public class HorizontalBarChart extends JComponent {
    private JLabel header;
    private Color barColor;
    private DefaultPieDataset<?> dataset;

    public void setHeader(JLabel header) {
        this.header = header;
    }

    public JLabel getHeader() {
        return header;
    }

    public void setBarColor(Color barColor) {
        this.barColor = barColor;
    }

    public Color getBarColor() {
        return barColor;
    }

    public void setDataset(DefaultPieDataset<?> dataset) {
        this.dataset = dataset;
    }

    public DefaultPieDataset<?> getDataset() {
        return dataset;
    }

    public void startAnimation() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground() == null ? Color.WHITE : getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());

            if (header != null) {
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(header.getText(), 12, 18);
            }

            if (dataset == null || dataset.getValues().isEmpty()) {
                g2.setColor(new Color(120, 120, 120));
                g2.drawString("No data", 12, 36);
                return;
            }

            List<Object> labels = new ArrayList<>();
            List<Number> values = new ArrayList<>();
            for (Map.Entry<?, Number> entry : dataset.getValues().entrySet()) {
                labels.add(entry.getKey());
                values.add(entry.getValue());
            }

            double max = 0;
            for (Number value : values) {
                if (value != null) {
                    max = Math.max(max, value.doubleValue());
                }
            }
            if (max <= 0) {
                max = 1;
            }

            int left = 90;
            int top = 32;
            int right = 20;
            int bottom = 18;
            int plotW = Math.max(1, getWidth() - left - right);
            int plotH = Math.max(1, getHeight() - top - bottom);
            int rowH = Math.max(22, plotH / Math.max(1, labels.size()));
            int barH = Math.max(10, rowH - 8);

            g2.setColor(new Color(225, 228, 233));
            g2.drawLine(left, top, left, top + plotH);
            g2.drawLine(left, top + plotH, left + plotW, top + plotH);

            FontMetrics fm = g2.getFontMetrics();
            for (int i = 0; i < labels.size(); i++) {
                String text = String.valueOf(labels.get(i));
                Number value = values.get(i);
                int y = top + i * rowH + (rowH - barH) / 2;
                int barW = (int) Math.round(((value == null ? 0 : value.doubleValue()) / max) * (plotW - 12));
                g2.setColor(barColor == null ? new Color(66, 133, 244) : barColor);
                g2.fillRoundRect(left + 4, y, Math.max(2, barW), barH, 10, 10);
                g2.setColor(Color.DARK_GRAY);
                g2.drawString(text, 12, y + barH / 2 + fm.getAscent() / 2 - 2);
                g2.drawString(String.format("%,.0f", value == null ? 0 : value.doubleValue()), left + 10 + barW, y + barH / 2 + fm.getAscent() / 2 - 2);
            }
        } finally {
            g2.dispose();
        }
    }
}
