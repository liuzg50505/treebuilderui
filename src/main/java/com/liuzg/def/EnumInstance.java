package com.liuzg.def;

public class EnumInstance extends Instance{
    EnumDefinition.EnumItem value;

    public EnumInstance(TypeDefinition typeDefinition, EnumDefinition.EnumItem value) {
        super(typeDefinition);
        this.value = value;
    }

    @Override
    public String generateCode() {
        if(value==null) return "";
        return value.code;
    }
}
