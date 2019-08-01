package com.liuzg.flutteride.def;

import com.liuzg.flutteride.def.treeeditor.TreeNode;
import javafx.scene.Node;

import java.util.*;

public class InstanceNodePool {

    public static class InstanceProperty {
        private Instance instance;
        private String property;

        public InstanceProperty(Instance instance, String property) {
            this.instance = instance;
            this.property = property;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InstanceProperty that = (InstanceProperty) o;
            return Objects.equals(instance, that.instance) &&
                    Objects.equals(property, that.property);
        }

        @Override
        public int hashCode() {
            return Objects.hash(instance, property);
        }
    }

    private Map<Instance, TreeNode> instanceTreeNodeMap;
    private Map<InstanceProperty, TreeNode> instancePropertyTreeNodeMap;
    private Map<TreeNode, Instance> treeNodeInstanceMap;
    private Map<TreeNode, InstanceProperty> treeNodeInstancePropertyMap;
    private Set<TreeNode> treeNodes;

    public InstanceNodePool() {
        instanceTreeNodeMap = new HashMap<>();
        instancePropertyTreeNodeMap = new HashMap<>();
        treeNodeInstanceMap = new HashMap<>();
        treeNodeInstancePropertyMap = new HashMap<>();
        treeNodes = new HashSet<>();
    }

    public void addInstance(Instance instance, TreeNode treeNode){
        treeNodes.add(treeNode);
        if(instance==null) return;
        if(treeNode==null) return;
        instanceTreeNodeMap.put(instance, treeNode);
        treeNodeInstanceMap.put(treeNode, instance);
    }

    public void addInstanceProperty(Instance instance, String property, TreeNode treeNode) {
        treeNodes.add(treeNode);
        if(instance==null) return;
        if(treeNode==null) return;
        if(property==null||"".equals(property)) return;

        InstanceProperty t = new InstanceProperty(instance, property);
        instancePropertyTreeNodeMap.put(t, treeNode);
        treeNodeInstancePropertyMap.put(treeNode, t);

    }

    public void removeInstance(Instance instance) {
        if (instanceTreeNodeMap.containsKey(instance)) {
            TreeNode treeNode = instanceTreeNodeMap.get(instance);
            instanceTreeNodeMap.remove(instance);
            treeNodeInstanceMap.remove(treeNode);
            treeNodes.remove(treeNode);
        }
    }


    public boolean containsInstance(Instance instance) {
        return instanceTreeNodeMap.containsKey(instance);
    }

    public boolean containsInstanceProperty(Instance instance, String property) {
        return instancePropertyTreeNodeMap.containsKey(new InstanceProperty(instance, property));
    }

    public boolean containsTreeNode(TreeNode treeNode) {
        return treeNodes.contains(treeNode);
    }

    public TreeNode getTreeNode(Instance instance) {
        return instanceTreeNodeMap.get(instance);
    }

    public Instance getInstance(TreeNode treeNode) {
        return treeNodeInstanceMap.get(treeNode);
    }

    public TreeNode getTreeNode(Node control) {
        for (TreeNode treeNode: treeNodes){
            if(treeNode.getTreeNodeControl()==control){
                return treeNode;
            }
        }
        return null;
    }

    public TreeNode getTreeNode(Instance instance, String property) {
        return instancePropertyTreeNodeMap.get(new InstanceProperty(instance, property));
    }

    public Set<TreeNode> getTreeNodes() {
        return treeNodes;
    }

    public void clear() {
        instanceTreeNodeMap.clear();
        instancePropertyTreeNodeMap.clear();
        treeNodeInstanceMap.clear();
        treeNodeInstancePropertyMap.clear();
        treeNodes.clear();
    }

}
