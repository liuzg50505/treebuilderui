package com.liuzg.def;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.liuzg.def.events.MyTreeEditorSelectionChangedEvent;
import com.liuzg.def.events.MyTreeNodeClickEvent;
import com.liuzg.def.events.MyTreeNodeExpandEvent;
import com.liuzg.def.events.MyTreeNodeStartDraggingEvent;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static sun.awt.image.PixelConverter.UshortGray.instance;

public class MyTreeEditor extends VBox {
    public static MyTreeNode draggingnode;
    public static boolean iscopydragging;

    protected Instance rootInstance;
    protected MyTreeNode rootnode;
    protected MyTreeNode selectedNode;
    protected List<Consumer<MyTreeNode>> selectionChangedHandlers;

    protected InstanceNodePool pool;
    protected List<MyTreeNode> currentnodes;

    protected EventBus eventBus;

    public MyTreeEditor(Instance instance) {
        eventBus = new EventBus();
        eventBus.register(this);
        currentnodes = new ArrayList<>();
        selectionChangedHandlers = new ArrayList<>();
        rootInstance = instance;
        pool = new InstanceNodePool();
        renderUI();
        this.setStyle("-fx-font-size:16; ");

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


    // event handlers

    @Subscribe
    public void on(MyTreeNodeClickEvent event){
        MyTreeNode treeNode = event.treeNode;
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
//        Set<MyTreeNode> nodes = pool.getTreeNodes();
//        MyTreeGenerator generator = new MyTreeGenerator(pool);
//        List<MyTreeNode> nodes = generator.generateOrderedVisibleTreeNodes(rootnode);

        for(MyTreeNode node: currentnodes) {
            node.unselect();
        }
        if(selectedNode!=null) {
            selectedNode.select();
        }
    }

    private void refreshExpand() {
        this.getChildren().clear();

        MyTreeGenerator generator = new MyTreeGenerator(eventBus, pool);
        currentnodes = generator.generateOrderedVisibleTreeNodes(rootnode);
        for(MyTreeNode node: currentnodes) {
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

        MyTreeGenerator generator = new MyTreeGenerator(eventBus, pool);
        rootnode = generator.generateTree((ConstructorInstance) rootInstance);
        List<MyTreeNode> nodes = generator.generateOrderedVisibleTreeNodes(rootnode);
        currentnodes = nodes;
        for(MyTreeNode node: nodes) {
            node.setEventBus(this.eventBus);
            if(node instanceof MyTreePropertyInstanceNode) {
                MyTreePropertyInstanceNode tnode = (MyTreePropertyInstanceNode) node;
                pool.addInstance(tnode.getValueInstance(), tnode);
            }else if(node instanceof MyTreeInstanceNode) {
                MyTreeInstanceNode tnode = (MyTreeInstanceNode) node;
                pool.addInstance(tnode.getConstructorInstance(), tnode);
            }else if(node instanceof MyTreePropertyNode) {
                MyTreePropertyNode tnode = (MyTreePropertyNode) node;
                pool.addInstanceProperty(tnode.getConstructureInstance(), tnode.getProperty(), tnode);
            }
            try{
                Node child = node.getTreeNodeControl();
                Region region = (Region) child;
                if(region!=null) {
                    region.setPrefHeight(40);
                }
                this.getChildren().add(child);

                child.setOnDragDropped(event -> {

                    if(draggingnode==null) return;
                    MyTreeNode targetTreenode = pool.getTreeNode(child);
                    if(draggingnode==targetTreenode) return;
                    if(!iscopydragging) {
                        if(draggingnode instanceof MyTreeInstanceNode) {
                            MyTreeInstanceNode treeInstanceNode = (MyTreeInstanceNode) draggingnode;
                            removeTreeNode(treeInstanceNode);
                            addInstance(targetTreenode, treeInstanceNode.getConstructorInstance());

                        }else if(draggingnode instanceof MyTreePropertyInstanceNode) {
                            MyTreePropertyInstanceNode propertyInstanceNode = (MyTreePropertyInstanceNode) draggingnode;
                            removeTreeNode(propertyInstanceNode);
                            addInstance(targetTreenode, propertyInstanceNode.getValueInstance());
                        }
                    }else{
                        if(draggingnode instanceof MyTreeInstanceNode) {
                            MyTreeInstanceNode treeInstanceNode = (MyTreeInstanceNode) draggingnode;
                            ConstructorInstance instance = treeInstanceNode.getConstructorInstance();
                            ConstructorInstance newinstance = DefUtils.getCopyObj(instance);
                            addInstance(targetTreenode, newinstance);

                        }else if(draggingnode instanceof MyTreePropertyInstanceNode) {
                            MyTreePropertyInstanceNode propertyInstanceNode = (MyTreePropertyInstanceNode) draggingnode;
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
        MyTreeNode treenode = pool.getTreeNode(instance);
        if(treenode!=null) {
            selectTreeNode(treenode);
        }
    }

    public MyTreeNode getSelectedTreeNode() {
        return selectedNode;
    }

    public void selectTreeNode(MyTreeNode treeNode) {
        if(pool.containsTreeNode(treeNode)) {
            selectedNode = treeNode;
            refreshSelected();
        }
    }

    public void removeTreeNode(MyTreeNode treeNode) {
        if(treeNode instanceof MyTreePropertyInstanceNode) {
            MyTreePropertyInstanceNode current = (MyTreePropertyInstanceNode) treeNode;
            ConstructorInstance parent = current.getConstructorInstance();
            String property = current.getProperty();
            parent.setProperty(property, null);
            pool.removeInstance(current.getValueInstance());
            renderUI();
        }else if(treeNode instanceof MyTreeInstanceNode) {
            MyTreeInstanceNode current = (MyTreeInstanceNode) treeNode;
            MyTreePropertyNode propertyNode = (MyTreePropertyNode) current.parentNode;
            ConstructorInstance parent = propertyNode.getConstructureInstance();
            String property = propertyNode.getProperty();
            List value = (List) parent.getProperty(property);
            value.remove(current.getConstructorInstance());
            pool.removeInstance(current.getConstructorInstance());
            renderUI();
        }
    }

    public void addInstance(MyTreeNode treeNode, Instance instance) {
        if(treeNode instanceof MyTreePropertyInstanceNode) {
            MyTreePropertyInstanceNode current = (MyTreePropertyInstanceNode) treeNode;
            ConstructorInstance parent = current.getConstructorInstance();
            String property = current.getProperty();
            parent.setProperty(property, instance);
            renderUI();
        }else if(treeNode instanceof MyTreePropertyNode) {
            MyTreePropertyNode current = (MyTreePropertyNode) treeNode;
            ConstructorInstance parent = current.getConstructureInstance();
            String property = current.getProperty();
            List value = (List) parent.getProperty(property);
            if(value==null) {
                value = new ArrayList();
                parent.setProperty(property, value);
            }
            value.add(instance);
            renderUI();
        }else if(treeNode instanceof MyTreeInstanceNode) {
            MyTreeInstanceNode current = (MyTreeInstanceNode) treeNode;
            MyTreeNode parent = current.getParentNode();
            if(parent instanceof MyTreePropertyNode) {
                MyTreePropertyNode propertyParent = (MyTreePropertyNode) parent;
                String property = propertyParent.getProperty();
                List value = (List) propertyParent.getConstructureInstance().getProperty(property);
                int idx = value.indexOf(current.getConstructorInstance());
                value.add(idx, instance);
                renderUI();
            }
        }
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
        MyTreeNode node = getSelectedTreeNode();
        if(node!=null) {
            removeTreeNode(node);
            renderUI();
        }
    }

    public void moveDown() {
        MyTreeNode node = getSelectedTreeNode();
        if(node instanceof MyTreeInstanceNode) {
            MyTreeInstanceNode currentNode = (MyTreeInstanceNode) node;
            MyTreePropertyNode parentNode = (MyTreePropertyNode) node.getParentNode();
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

    public void moveUp() {
        MyTreeNode node = getSelectedTreeNode();
        if(node instanceof MyTreeInstanceNode) {
            MyTreeInstanceNode currentNode = (MyTreeInstanceNode) node;
            MyTreePropertyNode parentNode = (MyTreePropertyNode) node.getParentNode();
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

    public void expandSelected() {
        MyTreeNode node = getSelectedTreeNode();
        if(node!=null) {
            node.expandCurrent();
            refreshExpand();
        }
    }

    public void collapseSelected() {
        MyTreeNode node = getSelectedTreeNode();
        if(node!=null) {
            node.collapseCurrent();
            refreshExpand();
        }
    }

    public void expandAllSelected() {
        MyTreeNode node = getSelectedTreeNode();
        if(node!=null) {
            node.expandAll();
            refreshExpand();
        }
    }

    public void collapseAllSelected() {
        MyTreeNode node = getSelectedTreeNode();
        if(node!=null) {
            node.collapseAll();
            refreshExpand();
        }
    }

}
