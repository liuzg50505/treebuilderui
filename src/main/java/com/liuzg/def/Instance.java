package com.liuzg.def;

import java.io.Serializable;

public abstract class Instance implements Serializable {
    protected TypeDefinition typeDefinition;

    public Instance(TypeDefinition typeDefinition) {
        this.typeDefinition = typeDefinition;
    }

    public TypeDefinition getTypeDefinition() {
        return typeDefinition;
    }

    public void setTypeDefinition(TypeDefinition typeDefinition) {
        this.typeDefinition = typeDefinition;
    }

    public abstract String generateCode();
}
