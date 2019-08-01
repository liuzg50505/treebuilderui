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

public class TreePropertyInstanceNode extends TreeNode {

    private String property;
    private ConstructorInstance valueInstance;
    protected ConstructorInstance constructorInstance;
    protected ConstructorInstanceControl constructorInstanceControl;
    protected boolean isselected = false;



    public TreePropertyInstanceNode(EventBus eventBus, ConstructorInstance constructorInstance, String property, ConstructorInstance valueInstance) {
        super(eventBus);

        assert property!=null;
        assert !"".equals(property);

        this.constructorInstance = constructorInstance;
        constructorInstanceControl = new ConstructorInstanceControl(valueInstance, 0);
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
            if(this.valueInstance==null) return;
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


        this.property = property;
        this.valueInstance = valueInstance;
        constructorInstanceControl.setText(getNodeText());

    }

    public ConstructorInstance getConstructorInstance() {
        return constructorInstance;
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
        return String.format("%s: %s", property, valueInstance.getTypeDefinition().getTypeName());
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
