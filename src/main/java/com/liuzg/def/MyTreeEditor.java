package com.liuzg.def;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.liuzg.def.events.MyTreeEditorSelectionChangedEvent;
import com.liuzg.def.events.MyTreeNodeClickEvent;
import com.liuzg.def.events.MyTreeNodeExpandEvent;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

public class MyTreeEditor extends VBox {
    private Instance rootInstance;
    private int levelindent = 20;
    private int itemheight = 30;
    private MyTreeNode rootnode;
    private MyTreeNode selectedNode;
    private List<Consumer<MyTreeNode>> selectionChangedHandlers;

    private InstanceNodePool pool;
    private List<MyTreeNode> currentnodes;

    private EventBus eventBus;


    public MyTreeEditor(Instance instance) {
        eventBus = new EventBus();
        eventBus.register(this);
        currentnodes = new ArrayList<>();
        selectionChangedHandlers = new ArrayList<>();
        rootInstance = instance;
        pool = new InstanceNodePool();
        renderUI();
        this.setStyle("-fx-font-size:16; -fx-font-family: 'Times New Roman'");

    }

    private <T> void notifyHandlers(List<Consumer<T>> handlers, T target){
        for(Consumer<T> handler: handlers){
            handler.accept(target);
        }
    }

    @Subscribe
    public void onTreeNodeClicked(MyTreeNodeClickEvent event){
        MyTreeNode treeNode = event.treeNode;
        if(selectedNode!=treeNode) {
            selectedNode = treeNode;
            eventBus.post(new MyTreeEditorSelectionChangedEvent(this, treeNode));
            refreshSelected();
        }
    }

    @Subscribe
    public void onTreeNodeExpanded(MyTreeNodeExpandEvent event){
        refreshExpand();
    }

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
                pool.addInstance(tnode.getConstructureInstance(), tnode);
            }else if(node instanceof MyTreePropertyNode) {
                MyTreePropertyNode tnode = (MyTreePropertyNode) node;
                pool.addInstanceProperty(tnode.getConstructureInstance(), tnode.getProperty(), tnode);
            }
            try{
                this.getChildren().addAll(node.getTreeNodeControl());
            }catch (Exception ee) {
                ee.printStackTrace();
            }
        }
    }

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
        List<MyTreeNode> nodes = generator.generateOrderedVisibleTreeNodes(rootnode);
        for(MyTreeNode node: nodes) {
            this.getChildren().addAll(node.getTreeNodeControl());
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
            ConstructorInstance parent = current.getConstructureInstance();
            String property = current.getProperty();
            parent.setProperty(property, null);
            renderUI();
        }else if(treeNode instanceof MyTreeInstanceNode) {
            MyTreeInstanceNode current = (MyTreeInstanceNode) treeNode;
            MyTreePropertyNode propertyNode = (MyTreePropertyNode) current.parentNode;
            ConstructorInstance parent = propertyNode.getConstructureInstance();
            String property = propertyNode.getProperty();
            List value = (List) parent.getProperty(property);
            value.remove(current.getConstructureInstance());
            renderUI();
        }
    }

    public void addInstance(MyTreeNode treeNode, Instance instance) {
        if(treeNode instanceof MyTreePropertyInstanceNode) {
            MyTreePropertyInstanceNode current = (MyTreePropertyInstanceNode) treeNode;
            ConstructorInstance parent = current.getConstructureInstance();
            String property = current.getProperty();
            parent.setProperty(property, instance);
            renderUI();
        }else if(treeNode instanceof MyTreeInstanceNode) {
            MyTreeInstanceNode current = (MyTreeInstanceNode) treeNode;
            MyTreePropertyNode propertyNode = (MyTreePropertyNode) current.parentNode;
            ConstructorInstance parent = propertyNode.getConstructureInstance();
            String property = propertyNode.getProperty();
            List value = (List) parent.getProperty(property);
            value.add(instance);
            renderUI();
        }
    }



}
