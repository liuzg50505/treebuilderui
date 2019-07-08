package com.liuzg.models;

public class Widget {
    private TreeNode treeNode;
    private String controllerName;

    public Widget(TreeNode treeNode, String controllerName) {
        this.treeNode = treeNode;
        this.controllerName = controllerName;
    }

    public TreeNode getTreeNode() {
        return treeNode;
    }

    public void setTreeNode(TreeNode treeNode) {
        this.treeNode = treeNode;
    }

    public String getControllerName() {
        return controllerName;
    }

    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }
}
