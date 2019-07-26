package com.liuzg.def;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MyTreeEditor extends VBox {
    Instance rootInstance;
    private int levelindent = 20;
    private int itemheight = 30;
    private MyTreeNode rootnode;


    public MyTreeEditor(Instance instance) {
        rootInstance = instance;
        renderUI();
        this.setStyle("-fx-font-size:16; -fx-font-family: 'Times New Roman'");

    }

    public void renderUI() {
        this.getChildren().clear();

        MyTreeGenerator generator = new MyTreeGenerator();
        rootnode = generator.generateTree((ConstructorInstance) rootInstance);
        List<MyTreeNode> nodes = generator.generateOrderedVisibleTreeNodes(rootnode);
        for(MyTreeNode node: nodes) {
            this.getChildren().addAll(node.getTreeNodeControl());
            node.addExpandedChangedListener(treeNode -> {
                refreshUI();
            });
        }
    }

    public void refreshUI() {
        this.getChildren().clear();

        MyTreeGenerator generator = new MyTreeGenerator();
        List<MyTreeNode> nodes = generator.generateOrderedVisibleTreeNodes(rootnode);
        for(MyTreeNode node: nodes) {
            this.getChildren().addAll(node.getTreeNodeControl());
        }
    }

    private void renderItem(Instance instance, int level) {
        ConstructorInstanceControl itemcontrol = new ConstructorInstanceControl((ConstructorInstance) instance, level * levelindent);
        itemcontrol.setPrefHeight(itemheight);
        if (level==1) itemcontrol.setSelected(true);
        this.getChildren().add(itemcontrol);

        if(instance.typeDefinition instanceof ConstructorDefinition) {
            ConstructorInstance constructureInstance = (ConstructorInstance) instance;
            ConstructorDefinition definition = (ConstructorDefinition) instance.typeDefinition;
            Stream<ConstructorDefinition.ConstructorParam> params = definition.parameters.stream().filter(t -> t.paramtypename.equals("Widget"));
            for (ConstructorDefinition.ConstructorParam param: params.collect(Collectors.toList())) {
                String paramname = param.paramname;
                if(!param.iscollection) {
                    Instance subinstance = (Instance) constructureInstance.getProperty(paramname);
                    if(subinstance!=null) renderItem(subinstance, level+1);
                }else{
                    List<Instance> subinstances = (List<Instance>) constructureInstance.getProperty(paramname);
                    if(subinstances!=null) {
                        for (Instance subinstance: subinstances) {
                            renderItem(subinstance, level+1);
                        }
                    }
                }
            }
        }
    }

    private Pane renderInstance(Instance instance, int offsetx) {
        String typename = instance.typeDefinition.typeName;
        Label typeLabel = new Label(typename);

        Pane pane = new Pane(typeLabel);
        typeLabel.setLayoutX(5+ offsetx);
        typeLabel.setLayoutY(5);
        pane.setPrefHeight(itemheight);

        return pane;
    }
}
