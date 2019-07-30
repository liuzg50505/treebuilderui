package com.liuzg.def;

import com.liuzg.models.TreeNode;
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

    private Map<Instance, MyTreeNode> instanceTreeNodeMap;
    private Map<InstanceProperty, MyTreeNode> instancePropertyTreeNodeMap;
    private Map<MyTreeNode, Instance> treeNodeInstanceMap;
    private Map<MyTreeNode, InstanceProperty> treeNodeInstancePropertyMap;

    public InstanceNodePool() {
        instanceTreeNodeMap = new HashMap<>();
        instancePropertyTreeNodeMap = new HashMap<>();
        treeNodeInstanceMap = new HashMap<>();
        treeNodeInstancePropertyMap = new HashMap<>();
    }

    public void addInstance(Instance instance, MyTreeNode treeNode){
        if(instance==null) return;
        if(treeNode==null) return;
        instanceTreeNodeMap.put(instance, treeNode);
        treeNodeInstanceMap.put(treeNode, instance);
    }

    public void addInstanceProperty(Instance instance, String property, MyTreeNode treeNode) {
        if(instance==null) return;
        if(treeNode==null) return;
        if(property==null||"".equals(property)) return;

        InstanceProperty t = new InstanceProperty(instance, property);
        instancePropertyTreeNodeMap.put(t, treeNode);
        treeNodeInstancePropertyMap.put(treeNode, t);

    }

    public void addTreeNode(MyTreeNode treeNode, Instance instance){
        if(instance==null) return;
        if(treeNode==null) return;
        instanceTreeNodeMap.put(instance, treeNode);
        treeNodeInstanceMap.put(treeNode, instance);
    }

    public void removeInstance(Instance instance) {
        if (instanceTreeNodeMap.containsKey(instance)) {
            MyTreeNode treeNode = instanceTreeNodeMap.get(instance);
            instanceTreeNodeMap.remove(instance);
            treeNodeInstanceMap.remove(treeNode);
        }
    }

    public void removeTreeNode(MyTreeNode treeNode) {
        if (treeNodeInstanceMap.containsKey(treeNode)) {
            Instance instance = treeNodeInstanceMap.get(treeNode);
            instanceTreeNodeMap.remove(instance);
            treeNodeInstanceMap.remove(treeNode);
        }
    }

    public boolean containsInstance(Instance instance) {
        return instanceTreeNodeMap.containsKey(instance);
    }

    public boolean containsInstanceProperty(Instance instance, String property) {
        return instancePropertyTreeNodeMap.containsKey(new InstanceProperty(instance, property));
    }

    public boolean containsTreeNode(MyTreeNode treeNode) {
        return treeNodeInstanceMap.containsKey(treeNode);
    }

    public MyTreeNode getTreeNode(Instance instance) {
        return instanceTreeNodeMap.get(instance);
    }

    public Instance getInstance(MyTreeNode treeNode) {
        return treeNodeInstanceMap.get(treeNode);
    }

    public MyTreeNode getTreeNode(Node control) {
        for (MyTreeNode treeNode: treeNodeInstanceMap.keySet()){
            if(treeNode.getTreeNodeControl()==control){
                return treeNode;
            }
        }
        return null;
    }

    public MyTreeNode getTreeNode(Instance instance, String property) {
        return instancePropertyTreeNodeMap.get(new InstanceProperty(instance, property));
    }

    public Set<MyTreeNode> getTreeNodes() {
        HashSet<MyTreeNode> t = new HashSet<>(instanceTreeNodeMap.values());
        t.addAll(instancePropertyTreeNodeMap.values());
        return t;
    }

}
