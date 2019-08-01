package com.liuzg.flutteride.def.treeeditor;

import com.google.common.eventbus.EventBus;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

public abstract class TreeNode {

    protected EventBus eventBus;

    protected List<TreeNode> subTreeNodes;
    protected TreeNode parentNode;

    public TreeNode(EventBus eventBus) {
        subTreeNodes = new ArrayList<>();
        this.eventBus = eventBus;
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public abstract String getNodeText();

    public void addTreeNode(TreeNode treeNode) {
        if (treeNode==null) return;
        treeNode.parentNode = this;
        subTreeNodes.add(treeNode);
    }

    public void removeTreeNode(TreeNode treeNode) {
        if (treeNode==null) return;
        if(subTreeNodes.contains(treeNode)) {
            treeNode.parentNode = null;
            subTreeNodes.remove(treeNode);
        }
    }

    public List<TreeNode> getSubTreeNodes() {
        return new ArrayList<>(subTreeNodes);
    }

    public void clearTreeNodes() {
        for (TreeNode subNode: subTreeNodes) {
            subNode.parentNode = null;
        }
        this.subTreeNodes.clear();
    }

    public TreeNode getParentNode() {
        return parentNode;
    }

    public abstract Node getTreeNodeControl();

    public abstract boolean isExpanded();

    public abstract void expandCurrent();

    public abstract void select();

    public abstract void unselect();

    public abstract boolean isSelected();

    private void expandRecursive(TreeNode node) {
        if(node==null) return;
        node.expandCurrent();
        for(TreeNode subnode: node.subTreeNodes){
            expandRecursive(subnode);
        }
    }

    public void expandAll() {
        expandRecursive(this);
    }

    public abstract void collapseCurrent();

    private void collapseRecursive(TreeNode node) {
        if(node==null) return;
        node.collapseCurrent();
        for(TreeNode subnode: node.subTreeNodes){
            collapseRecursive(subnode);
        }
    }

    public void collapseAll() {
        collapseRecursive(this);
    }

    public int parentCount() {
        int count = 0;
        TreeNode cur = this.parentNode;
        while (cur!=null) {
            count++;
            cur = cur.parentNode;
        }
        return count;
    }
}
