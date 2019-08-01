package com.liuzg.flutteride.models;

import java.util.ArrayList;
import java.util.List;

public class EnumType {
    private String typeName;
    private List<String> values = new ArrayList<>();

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
