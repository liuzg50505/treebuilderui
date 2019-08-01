package com.liuzg.flutteride.uitls;

import javafx.scene.control.Alert;

public class FxUtils {

    public static void alert(String text, String title) {
        Alert information = new Alert(Alert.AlertType.INFORMATION,text);
        information.setTitle(title);
        information.setHeaderText(title);
        information.show();
    }
}
