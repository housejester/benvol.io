package io.benvol.util;

import java.util.Map;
import java.util.Objects;

public class KeyValuePair<K, V> {

    private K _key;
    private V _value;

    public KeyValuePair(K key, V value) {
        _key = key;
        _value = value;
    }

    public KeyValuePair(Map.Entry<K, V> entry) {
        _key = entry.getKey();
        _value = entry.getValue();
    }

    public K getKey() {
        return _key;
    }

    public V getValue() {
        return _value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_key, _value);
    }

    @Override
    public String toString() {
        return String.format("KeyValuePair(%s,%s)", _key, _value);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object obj) {
        if (obj instanceof KeyValuePair) {
            KeyValuePair pair = (KeyValuePair) obj;
            return Objects.equals(getKey(), pair.getKey())
                && Objects.equals(getValue(), pair.getValue());
        }
        return false;
    }

}
