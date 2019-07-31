package com.liuzg.def;

import java.io.Serializable;

public abstract class TypeDefinition implements Serializable {
    protected String typeName;
    protected String importlib;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

}
