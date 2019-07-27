package com.liuzg.def;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class MyTreeNode {

    public static interface ExpandedChangedHandler {
        void onTreeNodeExpandedChanged(MyTreeNode treeNode);
    }

    protected List<MyTreeNode> subTreeNodes;
    protected MyTreeNode parentNode;

    public MyTreeNode() {
        subTreeNodes = new ArrayList<>();
    }

    public abstract String getNodeText();

    public void addTreeNode(MyTreeNode treeNode) {
        if (treeNode==null) return;
        treeNode.parentNode = this;
        subTreeNodes.add(treeNode);
    }

    public void removeTreeNode(MyTreeNode treeNode) {
        if (treeNode==null) return;
        if(subTreeNodes.contains(treeNode)) {
            treeNode.parentNode = null;
            subTreeNodes.remove(treeNode);
        }
    }

    public List<MyTreeNode> getSubTreeNodes() {
        return new ArrayList<>(subTreeNodes);
    }

    public void clearTreeNodes() {
        for (MyTreeNode subNode: subTreeNodes) {
            subNode.parentNode = null;
        }
        this.subTreeNodes.clear();
    }

    public abstract Node getTreeNodeControl();

    public abstract boolean isExpanded();

    public abstract void expandCurrent();

    public abstract void addExpandedChangedListener(ExpandedChangedHandler handler);

    public abstract void addClickedListener(Consumer<MyTreeNode> handler);

    public abstract void select();

    public abstract void unselect();

    public abstract boolean isSelected();

    private void expandRecursive(MyTreeNode node) {
        if(node==null) return;
        node.expandCurrent();
        for(MyTreeNode subnode: node.subTreeNodes){
            expandRecursive(subnode);
        }
    }

    public void expandAll() {
        expandRecursive(this);
    }

    public abstract void collapseCurrent();

    private void collapseRecursive(MyTreeNode node) {
        if(node==null) return;
        node.collapseCurrent();
        for(MyTreeNode subnode: node.subTreeNodes){
            collapseRecursive(subnode);
        }
    }

    public void collapseAll() {
        collapseRecursive(this);
    }

    public int parentCount() {
        int count = 0;
        MyTreeNode cur = this.parentNode;
        while (cur!=null) {
            count++;
            cur = cur.parentNode;
        }
        return count;
    }
}
