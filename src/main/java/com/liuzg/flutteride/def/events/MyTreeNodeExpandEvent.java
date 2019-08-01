package com.liuzg.flutteride.def.events;

import com.liuzg.flutteride.def.treeeditor.TreeNode;

public class MyTreeNodeExpandEvent {
    public TreeNode treeNode;

    public MyTreeNodeExpandEvent(TreeNode treeNode) {
        this.treeNode = treeNode;
    }
}
