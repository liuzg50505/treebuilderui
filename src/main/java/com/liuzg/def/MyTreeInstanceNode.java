package com.liuzg.def;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class MyTreeInstanceNode extends MyTreeNode {

    private List<ExpandedChangedHandler> expandedChangedHandlers;

    protected ConstructorInstance constructureInstance;

    protected ConstructorInstanceControl constructorInstanceControl;

    public MyTreeInstanceNode(ConstructorInstance constructureInstance) {
        assert constructureInstance!=null;

        this.expandedChangedHandlers = new ArrayList<>();
        this.constructureInstance = constructureInstance;
        constructorInstanceControl = new ConstructorInstanceControl(constructureInstance, 0);
        constructorInstanceControl.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            System.out.println("clicked");
        });
        constructorInstanceControl.addExpandedHandler(expanded -> {
            for(ExpandedChangedHandler handler: expandedChangedHandlers){
                handler.onTreeNodeExpandedChanged(this);
            }
        });
        constructorInstanceControl.addDecoratorClickListener(decoratorInstance -> {
            System.out.println("decorator");
        });
    }

    public ConstructorInstance getConstructureInstance() {
        return constructureInstance;
    }

    @Override
    public String getNodeText() {
        return constructureInstance.typeDefinition.typeName;
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
