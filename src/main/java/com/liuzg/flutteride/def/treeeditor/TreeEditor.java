package com.liuzg.flutteride.def.treeeditor;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.liuzg.flutteride.def.ConstructorInstance;
import com.liuzg.flutteride.def.DefUtils;
import com.liuzg.flutteride.def.Instance;
import com.liuzg.flutteride.def.InstanceNodePool;
import com.liuzg.flutteride.def.events.MyTreeEditorSelectionChangedEvent;
import com.liuzg.flutteride.def.events.MyTreeNodeClickEvent;
import com.liuzg.flutteride.def.events.MyTreeNodeExpandEvent;
import com.liuzg.flutteride.def.events.MyTreeNodeStartDraggingEvent;
import javafx.scene.Node;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TreeEditor extends VBox {
    public static TreeNode draggingnode;
    public static boolean iscopydragging;

    protected Instance rootInstance;
    protected TreeNode rootnode;
    protected TreeNode selectedNode;
    protected List<Consumer<TreeNode>> selectionChangedHandlers;

    protected InstanceNodePool pool;
    protected List<TreeNode> currentnodes;

    protected EventBus eventBus;
    protected boolean isdirty = false;

    protected int lineHeight = 30;

    public TreeEditor() {
        eventBus = new EventBus();
        eventBus.register(this);
        currentnodes = new ArrayList<>();
        selectionChangedHandlers = new ArrayList<>();
        pool = new InstanceNodePool();

        this.setOnDragEntered(event -> {
        });
        this.setOnDragExited(event -> {
        });
        this.setOnDragOver(event -> {
            if (iscopydragging) event.acceptTransferModes(TransferMode.COPY);
            else event.acceptTransferModes(TransferMode.MOVE);
        });
        this.setFocused(true);
    }

    public TreeEditor(Instance instance) {
        this();
        setRootIntance(instance);
    }

    // getter and setters

    public boolean isDirty() {
        return this.isdirty;
    }

    public int getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
    }

    // event handlers

    @Subscribe
    public void on(MyTreeNodeClickEvent event){
        TreeNode treeNode = event.treeNode;
        if(selectedNode!=treeNode) {
            selectedNode = treeNode;
            eventBus.post(new MyTreeEditorSelectionChangedEvent(this, treeNode));
            refreshSelected();
        }
    }

    @Subscribe
    public void on(MyTreeNodeExpandEvent event){
        refreshExpand();
    }

    @Subscribe
    public void on(MyTreeNodeStartDraggingEvent event) {
        refreshExpand();
    }


    // private methods

    private void refreshSelected() {
//        Set<TreeNode> nodes = pool.getTreeNodes();
//        TreeGenerator generator = new TreeGenerator(pool);
//        List<TreeNode> nodes = generator.generateOrderedVisibleTreeNodes(rootnode);

        for(TreeNode node: currentnodes) {
            node.unselect();
        }
        if(selectedNode!=null) {
            selectedNode.select();
        }
    }

    private void refreshExpand() {
        this.getChildren().clear();

        TreeGenerator generator = new TreeGenerator(eventBus, pool);
        currentnodes = generator.generateOrderedVisibleTreeNodes(rootnode);
        for(TreeNode node: currentnodes) {
            this.getChildren().addAll(node.getTreeNodeControl());
        }
    }

    private <T> void notifyHandlers(List<Consumer<T>> handlers, T target){
        for(Consumer<T> handler: handlers){
            handler.accept(target);
        }
    }

    // public outlets

    public void renderUI() {
        this.getChildren().clear();

        TreeGenerator generator = new TreeGenerator(eventBus, pool);
        rootnode = generator.generateTree((ConstructorInstance) rootInstance);
        List<TreeNode> nodes = generator.generateOrderedVisibleTreeNodes(rootnode);
        currentnodes = nodes;
        for(TreeNode node: nodes) {
            node.setEventBus(this.eventBus);
            if(node instanceof TreePropertyInstanceNode) {
                TreePropertyInstanceNode tnode = (TreePropertyInstanceNode) node;
                pool.addInstance(tnode.getValueInstance(), tnode);
            }else if(node instanceof TreeInstanceNode) {
                TreeInstanceNode tnode = (TreeInstanceNode) node;
                pool.addInstance(tnode.getConstructorInstance(), tnode);
            }else if(node instanceof TreePropertyNode) {
                TreePropertyNode tnode = (TreePropertyNode) node;
                pool.addInstanceProperty(tnode.getConstructureInstance(), tnode.getProperty(), tnode);
            }
            try{
                Node child = node.getTreeNodeControl();
                Region region = (Region) child;
                if(region!=null) {
                    region.setMinHeight(lineHeight);
                    region.setPrefHeight(lineHeight);
                    region.setMaxHeight(lineHeight);
                }
                this.getChildren().add(child);

                child.setOnDragDropped(event -> {

                    if(draggingnode==null) return;
                    TreeNode targetTreenode = pool.getTreeNode(child);
                    if(draggingnode==targetTreenode) return;
                    if(!iscopydragging) {
                        if(draggingnode instanceof TreeInstanceNode) {
                            TreeInstanceNode treeInstanceNode = (TreeInstanceNode) draggingnode;
                            removeTreeNode(treeInstanceNode);
                            addInstance(targetTreenode, treeInstanceNode.getConstructorInstance());

                        }else if(draggingnode instanceof TreePropertyInstanceNode) {
                            TreePropertyInstanceNode propertyInstanceNode = (TreePropertyInstanceNode) draggingnode;
                            removeTreeNode(propertyInstanceNode);
                            addInstance(targetTreenode, propertyInstanceNode.getValueInstance());
                        }
                    }else{
                        if(draggingnode instanceof TreeInstanceNode) {
                            TreeInstanceNode treeInstanceNode = (TreeInstanceNode) draggingnode;
                            ConstructorInstance instance = treeInstanceNode.getConstructorInstance();
                            ConstructorInstance newinstance = DefUtils.getCopyObj(instance);
                            addInstance(targetTreenode, newinstance);

                        }else if(draggingnode instanceof TreePropertyInstanceNode) {
                            TreePropertyInstanceNode propertyInstanceNode = (TreePropertyInstanceNode) draggingnode;
                            ConstructorInstance instance = propertyInstanceNode.getValueInstance();
                            ConstructorInstance newinstance = DefUtils.getCopyObj(instance);
                            addInstance(targetTreenode, newinstance);
                        }
                    }
                    draggingnode = null;
                });

            }catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

    public void selectInstance(Instance instance) {
        TreeNode treenode = pool.getTreeNode(instance);
        if(treenode!=null) {
            selectTreeNode(treenode);
        }
    }

    public TreeNode getSelectedTreeNode() {
        return selectedNode;
    }

    public void selectTreeNode(TreeNode treeNode) {
        if(pool.containsTreeNode(treeNode)) {
            selectedNode = treeNode;
            refreshSelected();
            selectedNode.getTreeNodeControl().requestFocus();
        }
    }

    public void removeTreeNode(TreeNode treeNode) {
        if(treeNode instanceof TreePropertyInstanceNode) {
            TreePropertyInstanceNode current = (TreePropertyInstanceNode) treeNode;
            ConstructorInstance parent = current.getConstructorInstance();
            String property = current.getProperty();
            parent.setProperty(property, null);
            pool.removeInstance(current.getValueInstance());
            renderUI();
        }else if(treeNode instanceof TreeInstanceNode) {
            TreeInstanceNode current = (TreeInstanceNode) treeNode;
            TreePropertyNode propertyNode = (TreePropertyNode) current.parentNode;
            ConstructorInstance parent = propertyNode.getConstructureInstance();
            String property = propertyNode.getProperty();
            List value = (List) parent.getProperty(property);
            value.remove(current.getConstructorInstance());
            pool.removeInstance(current.getConstructorInstance());
            renderUI();
        }
    }

    public void addInstance(TreeNode treeNode, Instance instance) {
        if(treeNode instanceof TreePropertyInstanceNode) {
            TreePropertyInstanceNode current = (TreePropertyInstanceNode) treeNode;
            ConstructorInstance parent = current.getConstructorInstance();
            String property = current.getProperty();
            parent.setProperty(property, instance);
            renderUI();
        }else if(treeNode instanceof TreePropertyNode) {
            TreePropertyNode current = (TreePropertyNode) treeNode;
            ConstructorInstance parent = current.getConstructureInstance();
            String property = current.getProperty();
            List value = (List) parent.getProperty(property);
            if(value==null) {
                value = new ArrayList();
                parent.setProperty(property, value);
            }
            value.add(instance);
            renderUI();
        }else if(treeNode instanceof TreeInstanceNode) {
            TreeInstanceNode current = (TreeInstanceNode) treeNode;
            TreeNode parent = current.getParentNode();
            if(parent instanceof TreePropertyNode) {
                TreePropertyNode propertyParent = (TreePropertyNode) parent;
                String property = propertyParent.getProperty();
                List value = (List) propertyParent.getConstructureInstance().getProperty(property);
                int idx = value.indexOf(current.getConstructorInstance());
                value.add(idx, instance);
                renderUI();
            }
        }
    }

    public void setRootIntance(Instance instance){
        pool.clear();
        rootInstance = instance;
        renderUI();
    }

    public Instance getRootInstance(){
        return rootInstance;
    }


    // event outlets

    public void selectionUp() {
        if(selectedNode==null) {
            if(currentnodes.size()>0) {
                selectTreeNode(currentnodes.get(0));
            }
        }else{
            if(currentnodes.size()>0) {
                int idx = currentnodes.indexOf(selectedNode);
                if(idx-1>=0){
                    selectTreeNode(currentnodes.get(idx-1));
                }
            }
        }
    }

    public void selectionDown() {
        if(selectedNode==null) {
            if(currentnodes.size()>0) {
                selectTreeNode(currentnodes.get(0));
            }
        }else{
            if(currentnodes.size()>0) {
                int idx = currentnodes.indexOf(selectedNode);
                if(idx+1<currentnodes.size()){
                    selectTreeNode(currentnodes.get(idx+1));
                }
            }
        }

    }

    public void removeSelected() {
        TreeNode node = getSelectedTreeNode();
        if(node!=null) {
            removeTreeNode(node);
            renderUI();
        }
    }

    public void moveDownSelected() {
        TreeNode node = getSelectedTreeNode();
        if(node instanceof TreeInstanceNode) {
            TreeInstanceNode currentNode = (TreeInstanceNode) node;
            TreePropertyNode parentNode = (TreePropertyNode) node.getParentNode();
            ConstructorInstance parent = parentNode.getConstructureInstance();
            String property = parentNode.getProperty();
            List value = (List) parent.getProperty(property);
            if(value!=null) {
                int idx = value.indexOf(currentNode.getConstructorInstance());
                if (idx+1<value.size()) {
                    Object a = value.get(idx);
                    Object b = value.get(idx+1);
                    value.set(idx, b);
                    value.set(idx+1, a);
                    renderUI();
                }
            }
        }

    }

    public void moveUpSelected() {
        TreeNode node = getSelectedTreeNode();
        if(node instanceof TreeInstanceNode) {
            TreeInstanceNode currentNode = (TreeInstanceNode) node;
            TreePropertyNode parentNode = (TreePropertyNode) node.getParentNode();
            ConstructorInstance parent = parentNode.getConstructureInstance();
            String property = parentNode.getProperty();
            List value = (List) parent.getProperty(property);
            if(value!=null) {
                int idx = value.indexOf(currentNode.getConstructorInstance());
                if (idx-1>=0) {
                    Object a = value.get(idx);
                    Object b = value.get(idx-1);
                    value.set(idx, b);
                    value.set(idx-1, a);
                    renderUI();
                }
            }
        }
    }

    public void duplicateSelected() {
        TreeNode node = getSelectedTreeNode();
        if(node instanceof TreeInstanceNode) {
            TreeInstanceNode currentNode = (TreeInstanceNode) node;
            TreePropertyNode parentNode = (TreePropertyNode) node.getParentNode();
            ConstructorInstance parent = parentNode.getConstructureInstance();
            String property = parentNode.getProperty();
            List value = (List) parent.getProperty(property);
            if(value!=null) {
                int idx = value.indexOf(currentNode.getConstructorInstance());
                ConstructorInstance newinstance = DefUtils.getCopyObj(currentNode.getConstructorInstance());
                value.add(idx, newinstance);
                renderUI();
            }
        }
    }

    public void expandSelected() {
        TreeNode node = getSelectedTreeNode();
        if(node!=null) {
            node.expandCurrent();
            refreshExpand();
        }
    }

    public void collapseSelected() {
        TreeNode node = getSelectedTreeNode();
        if(node!=null) {
            node.collapseCurrent();
            refreshExpand();
        }
    }

    public void expandAllSelected() {
        TreeNode node = getSelectedTreeNode();
        if(node!=null) {
            node.expandAll();
            refreshExpand();
        }
    }

    public void collapseAllSelected() {
        TreeNode node = getSelectedTreeNode();
        if(node!=null) {
            node.collapseAll();
            refreshExpand();
        }
    }

}
