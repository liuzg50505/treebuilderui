package com.liuzg.def.events;

import com.liuzg.def.MyTreeNode;

public class MyTreeNodeClickEvent {
    public MyTreeNode treeNode;

    public MyTreeNodeClickEvent(MyTreeNode treeNode) {
        this.treeNode = treeNode;
    }
}
