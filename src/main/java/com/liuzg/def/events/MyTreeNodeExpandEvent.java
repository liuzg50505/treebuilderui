package com.liuzg.def.events;

import com.liuzg.def.MyTreeNode;

public class MyTreeNodeExpandEvent {
    public MyTreeNode treeNode;

    public MyTreeNodeExpandEvent(MyTreeNode treeNode) {
        this.treeNode = treeNode;
    }
}
