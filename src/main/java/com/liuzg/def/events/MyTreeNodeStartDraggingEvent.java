package com.liuzg.def.events;

import com.liuzg.def.MyTreeNode;

public class MyTreeNodeStartDraggingEvent {
    public MyTreeNode treeNode;

    public MyTreeNodeStartDraggingEvent(MyTreeNode treeNode) {
        this.treeNode = treeNode;
    }
}
