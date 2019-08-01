package com.liuzg.flutteride.def.treeeditor;

import com.google.common.eventbus.EventBus;
import com.liuzg.flutteride.def.ConstructorInstance;
import com.liuzg.flutteride.def.events.MyTreeNodeClickEvent;
import com.liuzg.flutteride.def.events.MyTreeNodeExpandEvent;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class TreePropertyNode extends TreeNode {

    private ConstructorInstance constructureInstance;
    private String property;
    private boolean isselected = false;

    private PropertyControl propertyControl;

    public TreePropertyNode(EventBus eventBus, ConstructorInstance constructureInstance, String property) {
        super(eventBus);
        assert constructureInstance!=null;
        assert property!=null;
        assert !"".equals(property);
        this.constructureInstance = constructureInstance;
        this.property = property;
        propertyControl = new PropertyControl(constructureInstance, property, 0);

        propertyControl.addExpandedHandler(expanded -> {
            eventBus.post(new MyTreeNodeExpandEvent(this));
        });
        propertyControl.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            eventBus.post(new MyTreeNodeClickEvent(this));
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
