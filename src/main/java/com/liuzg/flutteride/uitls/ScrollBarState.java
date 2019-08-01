package com.liuzg.flutteride.uitls;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;

public class ScrollBarState {
    private final Node targetControl;
    private final Orientation orientation;
    private ScrollBar scrollBar;

    private double min;
    private double max;
    private double value;
    private double blockIncrement;
    private double unitIncrement;

    public ScrollBarState(Node targetControl, Orientation orientation) {
        this.targetControl = targetControl;
        this.orientation = orientation;
    }

    public void reset() {
        scrollBar = null;
    }


    public void save() {
        if (scrollBar == null && !setScrollBar())
            return;

        this.min = scrollBar.getMin();
        this.max = scrollBar.getMax();
        this.value = scrollBar.getValue();
        this.blockIncrement = scrollBar.getBlockIncrement();
        this.unitIncrement = scrollBar.getUnitIncrement();
    }


    public void restore() {
        if (scrollBar == null)
            return;

        scrollBar.setMin(min);
        scrollBar.setMax(max);
        scrollBar.setValue(value);
        scrollBar.setUnitIncrement(unitIncrement);
        scrollBar.setBlockIncrement(blockIncrement);
    }


    private boolean setScrollBar() {
        for (Node node : targetControl.lookupAll(".scroll-bar")){
            if (node instanceof ScrollBar
                    && ((ScrollBar) node).getOrientation().equals(orientation)) {
                scrollBar = (ScrollBar) node;
                return true;
            }
        }
        return false;
    }
}
