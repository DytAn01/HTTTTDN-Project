package raven.chart.pie;

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
}
