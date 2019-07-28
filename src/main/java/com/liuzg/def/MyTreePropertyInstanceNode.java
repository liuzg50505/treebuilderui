package com.liuzg.def;

import com.google.common.eventbus.EventBus;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class MyTreePropertyInstanceNode extends MyTreeInstanceNode {

    private ConstructorInstance instance;
    private String property;
    private ConstructorInstance valueInstance;


    public MyTreePropertyInstanceNode(EventBus eventBus, ConstructorInstance constructureInstance, String property, ConstructorInstance valueInstance) {
        super(eventBus, valueInstance);

        assert property!=null;
        assert !"".equals(property);

        this.instance = constructureInstance;
        this.property = property;
        this.valueInstance = valueInstance;
        constructorInstanceControl.setText(getNodeText());

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

}
