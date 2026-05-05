package raven.chart.pie;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JLabel;
import raven.chart.ChartColor;
import raven.chart.data.pie.DefaultPieDataset;

public class PieChart extends JComponent {
    public enum ChartType {
        PIE_CHART,
        DONUT_CHART
    }

    private final ChartColor chartColor = new ChartColor();
    private JLabel header;
    private DefaultPieDataset<?> dataset;
    private ChartType chartType = ChartType.PIE_CHART;

    public void setHeader(JLabel header) {
        this.header = header;
    }

    public JLabel getHeader() {
        return header;
    }

    public ChartColor getChartColor() {
        return chartColor;
    }

    public void setDataset(DefaultPieDataset<?> dataset) {
        this.dataset = dataset;
    }

    public DefaultPieDataset<?> getDataset() {
        return dataset;
    }

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    public ChartType getChartType() {
        return chartType;
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

            List<Object> labels = new ArrayList<>(dataset.getValues().keySet());
            List<Number> values = new ArrayList<>();
            double total = 0;
            for (Map.Entry<?, Number> entry : dataset.getValues().entrySet()) {
                Number value = entry.getValue();
                values.add(value);
                total += value == null ? 0 : value.doubleValue();
            }
            if (total <= 0) {
                total = 1;
            }

            int diameter = Math.max(80, Math.min(getWidth() - 160, getHeight() - 55));
            int pieX = 16;
            int pieY = 30;
            int cx = pieX + diameter / 2;
            int cy = pieY + diameter / 2;

            int startAngle = 90;
            for (int i = 0; i < values.size(); i++) {
                Number value = values.get(i);
                double percent = (value == null ? 0 : value.doubleValue()) / total;
                int angle = (int) Math.round(percent * 360.0);
                g2.setColor(pickColor(i));
                g2.fill(new Arc2D.Double(pieX, pieY, diameter, diameter, startAngle, -angle, Arc2D.PIE));
                startAngle -= angle;
            }

            if (chartType == ChartType.DONUT_CHART) {
                int hole = (int) (diameter * 0.56);
                g2.setColor(getBackground() == null ? Color.WHITE : getBackground());
                g2.fillOval(cx - hole / 2, cy - hole / 2, hole, hole);
            }

            g2.setColor(new Color(150, 155, 163));
            g2.setStroke(new BasicStroke(1.1f));
            g2.drawOval(pieX, pieY, diameter, diameter);

            drawLegend(g2, labels, values, total, pieX + diameter + 18, pieY + 8);
        } finally {
            g2.dispose();
        }
    }

    private void drawLegend(Graphics2D g2, List<Object> labels, List<Number> values, double total, int x, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int cursorY = y;
        for (int i = 0; i < labels.size(); i++) {
            Number value = values.get(i);
            double percent = (value == null ? 0 : value.doubleValue()) * 100.0 / total;
            g2.setColor(pickColor(i));
            g2.fillRect(x, cursorY, 10, 10);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(String.valueOf(labels.get(i)) + " - " + String.format("%.1f%%", percent), x + 14, cursorY + 10);
            cursorY += Math.max(16, fm.getHeight());
        }
    }

    private Color pickColor(int index) {
        List<Color> colors = chartColor.getColors();
        if (!colors.isEmpty()) {
            return colors.get(index % colors.size());
        }
        Color[] defaults = {
            new Color(66, 133, 244),
            new Color(219, 68, 55),
            new Color(15, 157, 88),
            new Color(244, 180, 0)
        };
        return defaults[index % defaults.length];
    }
}
