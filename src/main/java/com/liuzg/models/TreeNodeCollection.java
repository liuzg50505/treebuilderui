package com.liuzg.models;

import java.util.ArrayList;
import java.util.Collection;

public class TreeNodeCollection extends ArrayList<TreeNode> {
    public TreeNodeCollection() {
    }

    public TreeNodeCollection(Collection<? extends TreeNode> c) {
        super(c);
    }

    public void setParent(TreeNode parent) {
        for(TreeNode node: this) {
            //node.setParent(parent);
        }
    }
}
