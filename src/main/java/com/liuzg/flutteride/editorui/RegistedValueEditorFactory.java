package com.liuzg.flutteride.editorui;

import java.util.HashMap;
import java.util.Map;

public class RegistedValueEditorFactory implements ValueEditorFactory {

    Map<String, ValueEditor> typeEditorMap = new HashMap<>();

    public void registEditor(String type, ValueEditor editorCreator) {
        typeEditorMap.put(type, editorCreator);
    }

    public void setTypeEditorMap(Map<String, ValueEditor> typeEditorMap) {
        this.typeEditorMap = typeEditorMap;
    }

    @Override
    public ValueEditor createValueEditor(String type) {
        if(typeEditorMap.containsKey(type)) {
            ValueEditor creator = typeEditorMap.get(type);
            return creator.cloneEditor();
        }
        return null;
    }
}
