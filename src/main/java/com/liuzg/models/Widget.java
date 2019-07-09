package com.liuzg.models;

public class Widget {
    private TreeNode treeNode;
    private String controllerName;
    private Design design;

    public Widget(TreeNode treeNode, String controllerName) {
        this.treeNode = treeNode;
        this.controllerName = controllerName;
    }

    public Widget(TreeNode treeNode, String controllerName, Design design) {
        this.treeNode = treeNode;
        this.controllerName = controllerName;
        this.design = design;
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

    public Design getDesign() {
        return design;
    }

    public void setDesign(Design design) {
        this.design = design;
    }
}
