package com.liuzg.ui;

import com.liuzg.models.TreeNode;
import javafx.scene.Node;

import java.util.Objects;

public class ViewNode {
    private TreeNode treeNode;
    private TreeNode parentTreeNode;
    private ViewNodeType type;
    private String text;
    private Node icon;
    private String prop;

    public ViewNode(TreeNode treeNode, ViewNodeType type, String text) {
        this.treeNode = treeNode;
        this.type = type;
        this.text = text;
    }

    public ViewNode(TreeNode treeNode, ViewNodeType type, String text, Node icon) {
        this.treeNode = treeNode;
        this.type = type;
        this.text = text;
        this.icon = icon;
    }

    public TreeNode getTreeNode() {
        return treeNode;
    }

    public void setTreeNode(TreeNode treeNode) {
        this.treeNode = treeNode;
    }

    public TreeNode getParentTreeNode() {
        return parentTreeNode;
    }

    public void setParentTreeNode(TreeNode parentTreeNode) {
        this.parentTreeNode = parentTreeNode;
    }

    public ViewNodeType getType() {
        return type;
    }

    public void setType(ViewNodeType type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public Node getIcon() {
        return icon;
    }

    public void setIcon(Node icon) {
        this.icon = icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViewNode viewNode = (ViewNode) o;
        return Objects.equals(treeNode, viewNode.treeNode) &&
                Objects.equals(parentTreeNode, viewNode.parentTreeNode) &&
                type == viewNode.type &&
                Objects.equals(prop, viewNode.prop);
    }

    @Override
    public int hashCode() {
        return Objects.hash(treeNode, parentTreeNode, type, prop);
    }

    @Override
    public String toString() {
        return text;
    }
}
