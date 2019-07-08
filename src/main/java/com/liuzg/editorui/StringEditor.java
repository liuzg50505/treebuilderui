package com.liuzg.editorui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class StringEditor extends BorderPane implements Initializable, ValueEditor {

    @FXML
    Label label;

    @FXML
    TextField editor;


    public StringEditor() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/ui/stringeditor.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        editor.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(!editorsetvalue.equals(getValue())) {
                    if(!newValue) notifyChanged();
                }
            }
        });
    }


    private List<OnEditorChangedCallback> callbacks = new ArrayList<>();
    @Override
    public void addListener(OnEditorChangedCallback callback) {
        if(callback!=null) {
            callbacks.add(callback);
        }
    }

    private void notifyChanged() {
        for (OnEditorChangedCallback callback: callbacks) {
            callback.onValueChanged(this, getValue());
        }
    }

    @Override
    public void setLabel(String text) {
        label.setText(text);
    }

    @Override
    public String getLabel() {
        return label.getText();
    }

    String editorsetvalue = "";

    @Override
    public void setValue(Object value) {
        if(value==null) {
            editor.setText("");
        }
        else{
            editor.setText(value.toString());
        }
        this.editorsetvalue = editor.getText();
    }

    @Override
    public Object getValue() {
        return editor.getText();
    }

    @Override
    public ValueEditor cloneEditor() {
        return new StringEditor();
    }

}
