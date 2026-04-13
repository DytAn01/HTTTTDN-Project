package raven.chart.line;

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
}
