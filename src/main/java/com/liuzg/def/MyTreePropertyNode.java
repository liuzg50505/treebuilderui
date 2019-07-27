package com.liuzg.def;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MyTreePropertyNode extends MyTreeNode {

    private List<ExpandedChangedHandler> expandedChangedHandlers;
    protected List<Consumer<MyTreeNode>> clickedHandlers;
    private ConstructorInstance constructureInstance;
    private String property;
    private boolean isselected = false;

    private PropertyControl propertyControl;

    public MyTreePropertyNode(ConstructorInstance constructureInstance, String property) {
        assert constructureInstance!=null;
        assert property!=null;
        assert !"".equals(property);
        this.expandedChangedHandlers = new ArrayList<>();
        this.clickedHandlers = new ArrayList<>();
        this.constructureInstance = constructureInstance;
        this.property = property;
        propertyControl = new PropertyControl(constructureInstance, property, 0);

        propertyControl.addExpandedHandler(expanded -> {
            for(ExpandedChangedHandler handler: expandedChangedHandlers){
                handler.onTreeNodeExpandedChanged(this);
            }
        });
        propertyControl.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            for (Consumer<MyTreeNode> handler :clickedHandlers) {
                handler.accept(this);
            }
        });


    }

    public ConstructorInstance getConstructureInstance() {
        return constructureInstance;
    }

    public String getProperty() {
        return property;
    }

    @Override
    public String getNodeText() {
        return property;
    }

    @Override
    public Node getTreeNodeControl() {
        int level = parentCount();
        propertyControl.setOffsetX(level*20);
        return propertyControl;
    }

    @Override
    public boolean isExpanded() {
        return propertyControl.isExpanded();
    }

    @Override
    public void expandCurrent() {
        propertyControl.setExpanded(true);
    }

    @Override
    public void addExpandedChangedListener(ExpandedChangedHandler handler) {
        if(handler!=null) expandedChangedHandlers.add(handler);
    }

    @Override
    public void addClickedListener(Consumer<MyTreeNode> handler) {
        if(handler!=null) clickedHandlers.add(handler);
    }

    @Override
    public void select() {
        isselected = true;
        propertyControl.setBackgroundColor(Color.LIGHTBLUE);
    }

    @Override
    public void unselect() {
        isselected = false;
        propertyControl.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public boolean isSelected() {
        return isselected;
    }

    @Override
    public void collapseCurrent() {
        propertyControl.setExpanded(false);
    }
}
