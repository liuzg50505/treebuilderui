package com.liuzg.def;

import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class DecoratorLabel extends AnchorPane {

    private Label label;
    private Color color = Color.TRANSPARENT;

    public DecoratorLabel() {
        label = new Label();
        this.getChildren().add(label);
        AnchorPane.setTopAnchor(label, 5.0);
        AnchorPane.setBottomAnchor(label, 5.0);
        AnchorPane.setLeftAnchor(label, 10.0);
        AnchorPane.setRightAnchor(label, 10.0);

    }

    public DecoratorLabel(String label) {
        this();
        setText(label);
    }

    public void setColor(Color color) {
        this.color = color;
        this.setBorder(new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(1))));
//        this.setBackground(new Background(new BackgroundFill(color, new CornerRadii(5), null)));
    }

    public Color getColor() {
        return color;
    }

    public String getText() {
        return label.getText();
    }

    public void setText(String text) {
        label.setText(text);
    }



}
