package com.liuzg.flutteride.def.treeeditor;

import com.liuzg.flutteride.def.ConstructorInstance;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;


public class PropertyControl extends AnchorPane {
    private boolean isExpanded = true;
    private final Image imgExpanded;
    private final Image imgCollapsed;
    private int offsetX;
    private HBox hbox;
    private Label instanceLabel;
    private ImageView expandIcon;

    private List<TreeNodeControlExpandedHandler> expandedHandlers;

    public PropertyControl(ConstructorInstance instance, String property, int offsetX) {
        expandedHandlers = new ArrayList<>();
        this.offsetX = offsetX;
        imgExpanded = new Image(this.getClass().getResourceAsStream("/assets/treeeditor/expanded.png"), 16,16,false, true);
        imgCollapsed = new Image(this.getClass().getResourceAsStream("/assets/treeeditor/collapsed.png"), 16,16,false, true);
        expandIcon = new ImageView(imgExpanded);
        instanceLabel = new Label(property+":");
        hbox = new HBox(expandIcon, instanceLabel);
        hbox.setAlignment(Pos.CENTER_LEFT);
//        hbox.setSpacing(5);
        this.getChildren().add(hbox);
        AnchorPane.setLeftAnchor(hbox, (double) offsetX);
        AnchorPane.setRightAnchor(hbox, 0.0);
        AnchorPane.setTopAnchor(hbox, 0.0);
        AnchorPane.setBottomAnchor(hbox, 0.0);
        expandIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            isExpanded = !isExpanded;
            if(isExpanded) expandIcon.setImage(imgExpanded);
            else expandIcon.setImage(imgCollapsed);

            for (TreeNodeControlExpandedHandler expandedHandler: expandedHandlers){
                expandedHandler.onTreeNodeControlExpandedChanged(isExpanded);
            }
            event.consume();
        });

    }

    public void addExpandedHandler(TreeNodeControlExpandedHandler expandedHandler){
        if(expandedHandler!=null) {
            expandedHandlers.add(expandedHandler);
        }
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
        AnchorPane.setLeftAnchor(hbox, (double) offsetX);
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
        if(isExpanded) expandIcon.setImage(imgExpanded);
        else expandIcon.setImage(imgCollapsed);
    }

    public void setBackgroundColor(Color color) {
        this.setBackground(new Background(new BackgroundFill(color, null, null)));
    }
}
