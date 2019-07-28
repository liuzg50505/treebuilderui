package com.liuzg.def.events;

import com.liuzg.def.ConstructorInstance;
import com.liuzg.def.Instance;
import com.liuzg.def.MyTreeNode;

import java.util.List;

public class MyTreeNodeDecoratorClickEvent {
    public MyTreeNode treeNode;
    public ConstructorInstance constructorInstance;
    public List<ConstructorInstance> decorators;

    public MyTreeNodeDecoratorClickEvent(MyTreeNode treeNode, ConstructorInstance constructorInstance, List<ConstructorInstance> decorators) {
        this.treeNode = treeNode;
        this.constructorInstance = constructorInstance;
        this.decorators = decorators;
    }
}
