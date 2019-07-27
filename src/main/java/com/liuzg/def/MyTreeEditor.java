package com.liuzg.def;

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


    public MyTreeEditor(Instance instance) {
        selectionChangedHandlers = new ArrayList<>();
        rootInstance = instance;
        pool = new InstanceNodePool();
        renderUI();
        this.setStyle("-fx-font-size:16; -fx-font-family: 'Times New Roman'");

    }

    public void renderUI() {
        this.getChildren().clear();

        MyTreeGenerator generator = new MyTreeGenerator(pool);
        rootnode = generator.generateTree((ConstructorInstance) rootInstance);
        List<MyTreeNode> nodes = generator.generateOrderedVisibleTreeNodes(rootnode);
        for(MyTreeNode node: nodes) {
            node.addClickedListener(treeNode -> {
                if(selectedNode!=treeNode) {
                    selectedNode = treeNode;
                    for(Consumer<MyTreeNode> handler: selectionChangedHandlers){
                        handler.accept(treeNode);
                    }
                    refreshSelected();
                }
            });
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
            node.addExpandedChangedListener(treeNode -> {
                refreshExpand();
            });
        }
    }

    private void refreshSelected() {
//        Set<MyTreeNode> nodes = pool.getTreeNodes();
        MyTreeGenerator generator = new MyTreeGenerator(pool);
        List<MyTreeNode> nodes = generator.generateOrderedVisibleTreeNodes(rootnode);

        for(MyTreeNode node: nodes) {
            node.unselect();
        }
        if(selectedNode!=null) {
            selectedNode.select();
        }
    }

    public void refreshExpand() {
        this.getChildren().clear();

        MyTreeGenerator generator = new MyTreeGenerator(pool);
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

}
