package com.liuzg.flutteride.def.treeeditor;

import com.google.common.eventbus.EventBus;
import com.liuzg.flutteride.def.ConstructorInstance;
import com.liuzg.flutteride.def.events.MyTreeNodeClickEvent;
import com.liuzg.flutteride.def.events.MyTreeNodeDecoratorClickEvent;
import com.liuzg.flutteride.def.events.MyTreeNodeExpandEvent;
import com.liuzg.flutteride.def.events.MyTreeNodeStartDraggingEvent;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;

public class TreeInstanceNode extends TreeNode {

    protected ConstructorInstance constructorInstance;
    protected ConstructorInstanceControl constructorInstanceControl;
    protected boolean isselected = false;

    public TreeInstanceNode(EventBus eventBus, ConstructorInstance constructorInstance) {
        super(eventBus);
        assert constructorInstance !=null;

        this.constructorInstance = constructorInstance;
        constructorInstanceControl = new ConstructorInstanceControl(constructorInstance, 0);
        constructorInstanceControl.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            eventBus.post(new MyTreeNodeClickEvent(this));
        });
        constructorInstanceControl.addExpandedHandler(expanded -> {
            eventBus.post(new MyTreeNodeExpandEvent(this));
        });
        constructorInstanceControl.addDecoratorClickListener(decoratorInstance -> {
            eventBus.post(new MyTreeNodeDecoratorClickEvent(this, constructorInstance, constructorInstance.getDecorators()));
        });
        constructorInstanceControl.setOnDragDetected(event -> {

            Dragboard dragboard = constructorInstanceControl.startDragAndDrop(TransferMode.ANY);
            dragboard.setDragView(constructorInstanceControl.snapshot(null, null));

            ClipboardContent content = new ClipboardContent();
            content.putString(constructorInstanceControl.getText());
            dragboard.setContent(content);

            TreeEditor.draggingnode = this;
            if(event.isSecondaryButtonDown()) TreeEditor.iscopydragging = true;
            else TreeEditor.iscopydragging = false;

            collapseCurrent();
            eventBus.post(new MyTreeNodeStartDraggingEvent(this));
        });
    }

    public ConstructorInstance getConstructorInstance() {
        return constructorInstance;
    }

    @Override
    public String getNodeText() {
        return constructorInstance.getTypeDefinition().getTypeName();
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
