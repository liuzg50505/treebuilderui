package com.liuzg.def;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class MyTreePropertyInstanceNode extends MyTreeNode {

    private List<ExpandedChangedHandler> expandedChangedHandlers;
    private ConstructorInstance constructureInstance;
    private String property;
    private ConstructorInstance valueInstance;

    private ConstructorInstanceControl constructorInstanceControl;

    public MyTreePropertyInstanceNode(ConstructorInstance constructureInstance, String property, ConstructorInstance valueInstance) {
        assert constructureInstance!=null;
        assert property!=null;
        assert !"".equals(property);

        this.expandedChangedHandlers = new ArrayList<>();
        this.constructureInstance = constructureInstance;
        this.property = property;
        this.valueInstance = valueInstance;
        constructorInstanceControl = new ConstructorInstanceControl(valueInstance, 0);
        constructorInstanceControl.setText(getNodeText());

        constructorInstanceControl.addExpandedHandler(expanded -> {
            for(ExpandedChangedHandler handler: expandedChangedHandlers){
                handler.onTreeNodeExpandedChanged(this);
            }
        });
        constructorInstanceControl.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println("clicked");
        });
        constructorInstanceControl.addDecoratorClickListener(decoratorInstance -> {
            System.out.println("decorator");
        });

    }

    public ConstructorInstance getConstructureInstance() {
        return constructureInstance;
    }

    public String getProperty() {
        return property;
    }

    public ConstructorInstance getValueInstance() {
        return valueInstance;
    }

    @Override
    public String getNodeText() {
        if(valueInstance==null) return String.format("%s:", property);
        return String.format("%s: %s", property, valueInstance.typeDefinition.typeName);
    }

    @Override
    public Node getTreeNodeControl() {
        int level = parentCount();
        constructorInstanceControl.setOffsetX(level*20);
        return constructorInstanceControl;
    }

    @Override
    public boolean isExpanded() {
        return constructorInstanceControl.isExpanded();
    }

    @Override
    public void expandCurrent() {
        constructorInstanceControl.setExpanded(true);
    }

    @Override
    public void addExpandedChangedListener(ExpandedChangedHandler handler) {
        if(handler!=null) expandedChangedHandlers.add(handler);
    }

    @Override
    public void collapseCurrent() {
        constructorInstanceControl.setExpanded(false);
    }
}
