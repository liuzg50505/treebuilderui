package com.liuzg.flutteride.def.events;

import com.liuzg.flutteride.def.treeeditor.TreeNode;

public class MyTreeNodeClickEvent {
    public TreeNode treeNode;

    public MyTreeNodeClickEvent(TreeNode treeNode) {
        this.treeNode = treeNode;
    }
}
