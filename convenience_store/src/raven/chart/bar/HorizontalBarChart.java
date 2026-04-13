package raven.chart.bar;

import java.awt.Color;
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
}
