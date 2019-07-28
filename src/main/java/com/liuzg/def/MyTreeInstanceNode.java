package com.liuzg.def;

import com.google.common.eventbus.EventBus;
import com.liuzg.def.events.MyTreeNodeClickEvent;
import com.liuzg.def.events.MyTreeNodeDecoratorClickEvent;
import com.liuzg.def.events.MyTreeNodeExpandEvent;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class MyTreeInstanceNode extends MyTreeNode {

    protected ConstructorInstance constructureInstance;
    protected ConstructorInstanceControl constructorInstanceControl;
    protected boolean isselected = false;

    public MyTreeInstanceNode(EventBus eventBus, ConstructorInstance constructureInstance) {
        super(eventBus);
        assert constructureInstance!=null;

        this.constructureInstance = constructureInstance;
        constructorInstanceControl = new ConstructorInstanceControl(constructureInstance, 0);
        constructorInstanceControl.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            eventBus.post(new MyTreeNodeClickEvent(this));
        });
        constructorInstanceControl.addExpandedHandler(expanded -> {
            eventBus.post(new MyTreeNodeExpandEvent(this));
        });
        constructorInstanceControl.addDecoratorClickListener(decoratorInstance -> {
            eventBus.post(new MyTreeNodeDecoratorClickEvent(this, constructureInstance, constructureInstance.decorators));
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
