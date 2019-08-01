package com.liuzg.flutteride.def.events;

import com.liuzg.flutteride.def.treeeditor.TreeNode;

public class MyTreeNodeStartDraggingEvent {
    public TreeNode treeNode;

    public MyTreeNodeStartDraggingEvent(TreeNode treeNode) {
        this.treeNode = treeNode;
    }
}
