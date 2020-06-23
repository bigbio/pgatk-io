package io.github.bigbio.pgatk.io.properties;

import java.io.Serializable;

public class BinaryPropertyStorage implements Serializable {

    String key;
    String value;
    String propertyName;

    BinaryPropertyStorage(String key, String propertyName, String value) {
        this.key = key;
        this.value = value;
        this.propertyName = propertyName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
}

