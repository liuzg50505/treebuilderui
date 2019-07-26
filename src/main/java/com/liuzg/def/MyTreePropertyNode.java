package com.liuzg.def;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

public class MyTreePropertyNode extends MyTreeNode {

    private List<ExpandedChangedHandler> expandedChangedHandlers;
    private ConstructorInstance constructureInstance;
    private String property;

    private PropertyControl propertyControl;

    public MyTreePropertyNode(ConstructorInstance constructureInstance, String property) {
        assert constructureInstance!=null;
        assert property!=null;
        assert !"".equals(property);
        this.expandedChangedHandlers = new ArrayList<>();
        this.constructureInstance = constructureInstance;
        this.property = property;
        propertyControl = new PropertyControl(constructureInstance, property, 0);

        propertyControl.addExpandedHandler(expanded -> {
            for(ExpandedChangedHandler handler: expandedChangedHandlers){
                handler.onTreeNodeExpandedChanged(this);
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
    public void collapseCurrent() {
        propertyControl.setExpanded(false);
    }
}
