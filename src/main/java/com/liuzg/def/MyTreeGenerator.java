package com.liuzg.def;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyTreeGenerator {

    private InstanceNodePool pool;

    public MyTreeGenerator(InstanceNodePool pool) {
        this.pool = pool;
    }

    private MyTreeInstanceNode createOrGetTreeNode(ConstructorInstance instance) {
        MyTreeInstanceNode instanceNode;
        if(pool.containsInstance(instance)) {
            instanceNode = (MyTreeInstanceNode) pool.getTreeNode(instance);
            instanceNode.clearTreeNodes();
        }
        else instanceNode = new MyTreeInstanceNode(instance);
        return instanceNode;
    }

    private MyTreePropertyNode createOrGetTreeNode(ConstructorInstance instance, String property) {
        MyTreePropertyNode instanceNode;
        if(pool.containsInstanceProperty(instance, property)){
            instanceNode = (MyTreePropertyNode) pool.getTreeNode(instance, property);
            instanceNode.clearTreeNodes();
        }
        else
            instanceNode = new MyTreePropertyNode(instance, property);
        return instanceNode;
    }
    private MyTreePropertyInstanceNode createOrGetTreeNode(ConstructorInstance instance, String property, ConstructorInstance valueinstance) {
        MyTreePropertyInstanceNode instanceNode = null;
        if(pool.containsInstance(valueinstance)){
            instanceNode = (MyTreePropertyInstanceNode) pool.getTreeNode(valueinstance);
            instanceNode.clearTreeNodes();
        }
        else
            instanceNode = new MyTreePropertyInstanceNode(instance, property, valueinstance);
        return instanceNode;
    }

    public MyTreeNode generateTree(ConstructorInstance constructureInstance) {
        assert constructureInstance!=null;

        MyTreeInstanceNode instanceNode = createOrGetTreeNode(constructureInstance);
        generateSubTree(instanceNode);
        return instanceNode;
    }

    private void generateSubTree(MyTreeNode node) {
        if(node instanceof MyTreePropertyInstanceNode) {
            MyTreePropertyInstanceNode instanceNode = (MyTreePropertyInstanceNode) node;
            ConstructorInstance constructureInstance = instanceNode.getValueInstance();
            if(constructureInstance!=null) {
                ConstructorDefinition constructorDefinition = (ConstructorDefinition) constructureInstance.typeDefinition;
                for (ConstructorDefinition.ConstructorParam cparam: constructorDefinition.getParameters()){
                    if("Widget".equals(cparam.paramtypename)){
                        if(cparam.iscollection) {
                            MyTreePropertyNode propertyNode = createOrGetTreeNode(constructureInstance, cparam.paramname);
                            node.addTreeNode(propertyNode);
                            generateSubTree(propertyNode);
                        }else{
                            MyTreePropertyInstanceNode propertyInstanceNode = createOrGetTreeNode(constructureInstance, cparam.paramname, (ConstructorInstance) constructureInstance.getProperty(cparam.paramname));
                            node.addTreeNode(propertyInstanceNode);
                            generateSubTree(propertyInstanceNode);
                        }
//                    }
                    }
                }

            }

        }else if(node instanceof MyTreeInstanceNode) {
            MyTreeInstanceNode instanceNode = (MyTreeInstanceNode) node;
            ConstructorInstance constructureInstance = instanceNode.getConstructureInstance();
            ConstructorDefinition constructorDefinition = (ConstructorDefinition) constructureInstance.typeDefinition;
            for (ConstructorDefinition.ConstructorParam cparam: constructorDefinition.getParameters()){
                if("Widget".equals(cparam.paramtypename)){
                    if(cparam.iscollection) {
                        MyTreePropertyNode propertyNode = createOrGetTreeNode(constructureInstance, cparam.paramname);
                        node.addTreeNode(propertyNode);
                        generateSubTree(propertyNode);
                    }else{
                        MyTreePropertyInstanceNode propertyInstanceNode = createOrGetTreeNode(constructureInstance, cparam.paramname, (ConstructorInstance) constructureInstance.getProperty(cparam.paramname));
                        node.addTreeNode(propertyInstanceNode);
                        generateSubTree(propertyInstanceNode);
                    }
                }
            }
        }else if(node instanceof MyTreePropertyNode) {
            MyTreePropertyNode propertyNode = (MyTreePropertyNode) node;
            ConstructorInstance constructureInstance = propertyNode.getConstructureInstance();
            String property = propertyNode.getProperty();
            Object v = constructureInstance.getProperty(property);
            if (v instanceof Collection) {
                Collection subvalues = (Collection) v;
                for(Object subvalue: subvalues){
                    if(subvalue instanceof ConstructorInstance) {
                        MyTreeInstanceNode subnode = createOrGetTreeNode((ConstructorInstance) subvalue);
                        propertyNode.addTreeNode(subnode);
                        generateSubTree(subnode);
                    }
                }
            }else if(v instanceof ConstructorInstance) {
                MyTreeInstanceNode subnode = createOrGetTreeNode((ConstructorInstance) v);
                propertyNode.addTreeNode(subnode);
                generateSubTree(subnode);
            }
        }
    }

    public List<MyTreeNode> generateOrderedVisibleTreeNodes(MyTreeNode node) {
        assert node!=null;
        List<MyTreeNode> result = new ArrayList<>();
        if(node.parentNode!=null&&!node.parentNode.isExpanded()){
            // do nothing
        }else{
            result.add(node);
            for(MyTreeNode subnode: node.getSubTreeNodes()) {
                List<MyTreeNode> subresult = generateOrderedVisibleTreeNodes(subnode);
                result.addAll(subresult);
            }
        }
        return result;

    }

    public List<Node> generateControls(MyTreeNode node) {
        assert node!=null;
        List<Node> result = new ArrayList<>();
        if(node.parentNode!=null&&!node.parentNode.isExpanded()){
            // do nothing
        }else{
            result.add(node.getTreeNodeControl());
            for(MyTreeNode subnode: node.getSubTreeNodes()) {
                List<Node> subresult = generateControls(subnode);
                result.addAll(subresult);
            }
        }
        return result;
    }
}
