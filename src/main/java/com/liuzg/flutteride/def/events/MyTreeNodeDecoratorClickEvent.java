package com.liuzg.flutteride.def.events;

import com.liuzg.flutteride.def.ConstructorInstance;
import com.liuzg.flutteride.def.treeeditor.TreeNode;

import java.util.List;

public class MyTreeNodeDecoratorClickEvent {
    public TreeNode treeNode;
    public ConstructorInstance constructorInstance;
    public List<ConstructorInstance> decorators;

    public MyTreeNodeDecoratorClickEvent(TreeNode treeNode, ConstructorInstance constructorInstance, List<ConstructorInstance> decorators) {
        this.treeNode = treeNode;
        this.constructorInstance = constructorInstance;
        this.decorators = decorators;
    }
}
