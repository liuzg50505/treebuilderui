package com.liuzg.def;

public abstract class Instance {
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
