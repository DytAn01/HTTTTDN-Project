package raven.chart.data.category;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultCategoryDataset<R, C> {
    private final Map<R, Map<C, Number>> values = new LinkedHashMap<>();

    public void addValue(Number value, R rowKey, C columnKey) {
        values.computeIfAbsent(rowKey, k -> new LinkedHashMap<>()).put(columnKey, value);
    }

    public Map<R, Map<C, Number>> getValues() {
        return values;
    }
}
