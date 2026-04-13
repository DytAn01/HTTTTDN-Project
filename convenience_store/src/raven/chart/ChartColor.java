package raven.chart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChartColor {
    private final List<Color> colors = new ArrayList<>();

    public void addColor(Color... newColors) {
        if (newColors == null) {
            return;
        }
        for (Color c : newColors) {
            if (c != null) {
                colors.add(c);
            }
        }
    }

    public List<Color> getColors() {
        return Collections.unmodifiableList(colors);
    }
}
