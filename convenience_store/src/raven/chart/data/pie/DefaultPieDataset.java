package raven.chart.data.pie;

import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultPieDataset<K> {
    private final Map<K, Number> values = new LinkedHashMap<>();

    public void addValue(K key, Number value) {
        setValue(key, value);
    }

    public void setValue(K key, Number value) {
        values.put(key, value);
    }

    public Map<K, Number> getValues() {
        return values;
    }
}
