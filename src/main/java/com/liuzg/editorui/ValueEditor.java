package com.liuzg.editorui;

public interface ValueEditor {

    void addListener(OnEditorChangedCallback callback);

    void setLabel(String text);

    String getLabel();

    void setValue(Object value);

    Object getValue();

    ValueEditor cloneEditor();

}
