package com.liuzg.def;

import java.util.ArrayList;
import java.util.List;

public abstract class DefinitionReader {
    protected List<TypeDefinition> definitions = new ArrayList<>();

    public List<TypeDefinition> getDefinitions() {
        return new ArrayList<>(definitions);
    }

    public abstract void read();
}
