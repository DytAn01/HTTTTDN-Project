package raven.chart;

import java.awt.Component;
import javax.swing.JLabel;

public class ChartLegendRenderer {
    public Component getLegendComponent(Object legend, int index) {
        return new JLabel(String.valueOf(legend));
    }
}
