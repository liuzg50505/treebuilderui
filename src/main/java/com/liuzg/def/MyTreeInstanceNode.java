package com.liuzg.def;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class MyTreeInstanceNode extends MyTreeNode {

    protected List<ExpandedChangedHandler> expandedChangedHandlers;
    protected List<Consumer<MyTreeNode>> clickedHandlers;
    protected ConstructorInstance constructureInstance;
    protected ConstructorInstanceControl constructorInstanceControl;
    protected boolean isselected = false;

    public MyTreeInstanceNode(ConstructorInstance constructureInstance) {
        assert constructureInstance!=null;

        this.expandedChangedHandlers = new ArrayList<>();
        this.clickedHandlers = new ArrayList<>();
        this.constructureInstance = constructureInstance;
        constructorInstanceControl = new ConstructorInstanceControl(constructureInstance, 0);
        constructorInstanceControl.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            for (Consumer<MyTreeNode> handler :clickedHandlers) {
                handler.accept(this);
            }
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
    public void addClickedListener(Consumer<MyTreeNode> handler) {
        if(handler!=null) clickedHandlers.add(handler);
    }


    @Override
    public void select() {
        isselected = true;
        constructorInstanceControl.setBackgroundColor(Color.LIGHTBLUE);
    }

    @Override
    public void unselect() {
        isselected = false;
        constructorInstanceControl.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public boolean isSelected() {
        return isselected;
    }

    @Override
    public void collapseCurrent() {
        constructorInstanceControl.setExpanded(false);
    }
}
