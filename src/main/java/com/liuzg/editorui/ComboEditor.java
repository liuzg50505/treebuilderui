package com.liuzg.editorui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ComboEditor extends BorderPane implements Initializable, ValueEditor {

    @FXML
    Label label;

    @FXML
    ChoiceBox<String> editor;

    public ComboEditor() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/ui/comboeditor.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

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
    public void initialize(URL location, ResourceBundle resources) {
        editor.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(!editorsetvalue.equals(getValue())) {
                    notifyChanged();
                }

            }
        });
    }

    public void setChoices(List<String> choices) {
        editor.setItems(FXCollections.observableArrayList(choices));
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
        if(value==null)
            editor.setValue("");
        else
            editor.setValue(value.toString());
        this.editorsetvalue = editor.getValue();
    }

    @Override
    public Object getValue() {
        return editor.getValue();
    }

    @Override
    public ValueEditor cloneEditor() {
        ComboEditor neweditor = new ComboEditor();
        neweditor.setChoices(editor.getItems());
        return neweditor;
    }

}
