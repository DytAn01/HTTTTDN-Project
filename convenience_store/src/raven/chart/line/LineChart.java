package raven.chart.line;

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
import raven.chart.ChartColor;
import raven.chart.ChartLegendRenderer;
import raven.chart.data.category.DefaultCategoryDataset;

public class LineChart extends JComponent {
    public enum ChartType {
        LINE,
        CURVE
    }

    private final ChartColor chartColor = new ChartColor();
    private ChartType chartType = ChartType.LINE;
    private DefaultCategoryDataset<?, ?> categoryDataset;
    private ChartLegendRenderer legendRenderer;

    public void setChartType(ChartType chartType) {
        this.chartType = chartType;
    }

    public ChartType getChartType() {
        return chartType;
    }

    public void setCategoryDataset(DefaultCategoryDataset<?, ?> categoryDataset) {
        this.categoryDataset = categoryDataset;
    }

    public DefaultCategoryDataset<?, ?> getCategoryDataset() {
        return categoryDataset;
    }

    public ChartColor getChartColor() {
        return chartColor;
    }

    public void setLegendRenderer(ChartLegendRenderer legendRenderer) {
        this.legendRenderer = legendRenderer;
    }

    public ChartLegendRenderer getLegendRenderer() {
        return legendRenderer;
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

            if (categoryDataset == null || categoryDataset.getValues().isEmpty()) {
                g2.setColor(new Color(120, 120, 120));
                g2.drawString("No data", 14, 20);
                return;
            }

            List<Object> seriesKeys = new ArrayList<>(categoryDataset.getValues().keySet());
            List<Object> labels = collectLabels();
            if (seriesKeys.isEmpty() || labels.isEmpty()) {
                g2.setColor(new Color(120, 120, 120));
                g2.drawString("No data", 14, 20);
                return;
            }

            int left = 56;
            int right = 20;
            int top = 28;
            int bottom = 34;
            int plotX = left;
            int plotY = top + 14;
            int plotW = Math.max(1, getWidth() - left - right);
            int plotH = Math.max(1, getHeight() - plotY - bottom);

            drawLegend(g2, seriesKeys, plotX, top);
            drawGrid(g2, plotX, plotY, plotW, plotH);
            drawAxes(g2, plotX, plotY, plotW, plotH);

            double max = getMaxValue();
            if (max <= 0) {
                max = 1;
            }

            for (int s = 0; s < seriesKeys.size(); s++) {
                Object seriesKey = seriesKeys.get(s);
                Color color = pickColor(s);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int prevX = -1;
                int prevY = -1;
                for (int i = 0; i < labels.size(); i++) {
                    Object label = labels.get(i);
                    Number value = getValue(seriesKey, label);
                    double numeric = value == null ? 0 : value.doubleValue();
                    int x = labels.size() == 1
                            ? plotX + plotW / 2
                            : (int) Math.round(plotX + (plotW * (double) i / (labels.size() - 1)));
                    int y = (int) Math.round(plotY + plotH - (numeric / max) * plotH);

                    if (prevX >= 0) {
                        g2.drawLine(prevX, prevY, x, y);
                    }
                    g2.fillOval(x - 3, y - 3, 6, 6);
                    prevX = x;
                    prevY = y;
                }
            }

            drawLabels(g2, labels, plotX, plotY, plotW, plotH, max);
        } finally {
            g2.dispose();
        }
    }

    private List<Object> collectLabels() {
        List<Object> labels = new ArrayList<>();
        for (Map<?, Number> row : categoryDataset.getValues().values()) {
            for (Object label : row.keySet()) {
                if (!labels.contains(label)) {
                    labels.add(label);
                }
            }
        }
        return labels;
    }

    private Number getValue(Object seriesKey, Object label) {
        Map<?, Number> row = categoryDataset.getValues().get(seriesKey);
        return row == null ? null : row.get(label);
    }

    private double getMaxValue() {
        double max = 0;
        for (Map<?, Number> row : categoryDataset.getValues().values()) {
            for (Number value : row.values()) {
                if (value != null) {
                    max = Math.max(max, value.doubleValue());
                }
            }
        }
        return max;
    }

    private void drawGrid(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(new Color(228, 232, 237));
        for (int i = 0; i <= 5; i++) {
            int yy = y + (h * i / 5);
            g2.drawLine(x, yy, x + w, yy);
        }
    }

    private void drawAxes(Graphics2D g2, int x, int y, int w, int h) {
        g2.setColor(new Color(175, 180, 188));
        g2.drawLine(x, y, x, y + h);
        g2.drawLine(x, y + h, x + w, y + h);
    }

    private void drawLegend(Graphics2D g2, List<Object> seriesKeys, int x, int y) {
        FontMetrics fm = g2.getFontMetrics();
        int cursor = x;
        for (int i = 0; i < seriesKeys.size(); i++) {
            String text = String.valueOf(seriesKeys.get(i));
            Color color = pickColor(i);
            g2.setColor(color);
            g2.fillRect(cursor, y + 3, 10, 10);
            g2.setColor(Color.DARK_GRAY);
            g2.drawString(text, cursor + 14, y + 12);
            cursor += 14 + fm.stringWidth(text) + 18;
        }
    }

    private void drawLabels(Graphics2D g2, List<Object> labels, int x, int y, int w, int h, double max) {
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(new Color(95, 101, 110));
        for (int i = 0; i <= 5; i++) {
            double value = max * (5 - i) / 5.0;
            int yy = y + (h * i / 5);
            g2.drawString(String.format("%,.0f", value), 6, yy + fm.getAscent() / 2 - 2);
        }

        for (int i = 0; i < labels.size(); i++) {
            String text = String.valueOf(labels.get(i));
            int xx = labels.size() == 1
                    ? x + w / 2
                    : (int) Math.round(x + (w * (double) i / (labels.size() - 1)));
            int textWidth = fm.stringWidth(text);
            g2.drawString(text, xx - textWidth / 2, y + h + 18);
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
