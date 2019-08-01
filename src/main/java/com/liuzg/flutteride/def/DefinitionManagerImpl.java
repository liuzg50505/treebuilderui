package com.liuzg.flutteride.def;

import java.util.ArrayList;
import java.util.List;

public class DefinitionManagerImpl extends DefinitionManager {

    private List<TypeDefinition> definitions;

    public DefinitionManagerImpl(List<TypeDefinition> definitions) {
        this.definitions = definitions;
    }

    @Override
    public TypeDefinition getTypeByName(String name) {
        for(TypeDefinition typeDefinition: definitions) {
            if(name.equals(typeDefinition.getTypeName())){
                return typeDefinition;
            }
        }
        return null;
    }

    @Override
    public List<TypeDefinition> getTypes() {
        return new ArrayList<>(definitions);
    }
}
