package com.liuzg.flutteride.def.treeeditor;

import com.google.common.eventbus.EventBus;
import com.liuzg.flutteride.def.ConstructorDefinition;
import com.liuzg.flutteride.def.ConstructorInstance;
import com.liuzg.flutteride.def.InstanceNodePool;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TreeGenerator {

    private InstanceNodePool pool;
    private EventBus eventBus;

    public TreeGenerator(EventBus eventBus, InstanceNodePool pool) {
        this.pool = pool;
        this.eventBus = eventBus;
    }

    private TreeInstanceNode createOrGetTreeNode(ConstructorInstance instance) {
        TreeInstanceNode instanceNode;
        if(pool.containsInstance(instance)) {
            instanceNode = (TreeInstanceNode) pool.getTreeNode(instance);
            instanceNode.clearTreeNodes();
        }
        else instanceNode = new TreeInstanceNode(eventBus, instance);
        return instanceNode;
    }

    private TreePropertyNode createOrGetTreeNode(ConstructorInstance instance, String property) {
        TreePropertyNode instanceNode;
        if(pool.containsInstanceProperty(instance, property)){
            instanceNode = (TreePropertyNode) pool.getTreeNode(instance, property);
            instanceNode.clearTreeNodes();
        }
        else
            instanceNode = new TreePropertyNode(eventBus, instance, property);
        return instanceNode;
    }
    private TreePropertyInstanceNode createOrGetTreeNode(ConstructorInstance instance, String property, ConstructorInstance valueinstance) {
        TreePropertyInstanceNode instanceNode = null;
        if(pool.containsInstance(valueinstance)){
            instanceNode = (TreePropertyInstanceNode) pool.getTreeNode(valueinstance);
            instanceNode.clearTreeNodes();
        }
        else
            instanceNode = new TreePropertyInstanceNode(eventBus, instance, property, valueinstance);
        return instanceNode;
    }

    public TreeNode generateTree(ConstructorInstance constructureInstance) {
        assert constructureInstance!=null;

        TreeInstanceNode instanceNode = createOrGetTreeNode(constructureInstance);
        generateSubTree(instanceNode);
        return instanceNode;
    }

    private void generateSubTree(TreeNode node) {
        if(node instanceof TreePropertyInstanceNode) {
            TreePropertyInstanceNode instanceNode = (TreePropertyInstanceNode) node;
            ConstructorInstance constructureInstance = instanceNode.getValueInstance();
            if(constructureInstance!=null) {
                ConstructorDefinition constructorDefinition = (ConstructorDefinition) constructureInstance.getTypeDefinition();
                for (ConstructorDefinition.ConstructorParam cparam: constructorDefinition.getParameters()){
                    if("Widget".equals(cparam.paramtypename)){
                        if(cparam.iscollection) {
                            TreePropertyNode propertyNode = createOrGetTreeNode(constructureInstance, cparam.paramname);
                            node.addTreeNode(propertyNode);
                            generateSubTree(propertyNode);
                        }else{
                            TreePropertyInstanceNode propertyInstanceNode = createOrGetTreeNode(constructureInstance, cparam.paramname, (ConstructorInstance) constructureInstance.getProperty(cparam.paramname));
                            node.addTreeNode(propertyInstanceNode);
                            generateSubTree(propertyInstanceNode);
                        }
//                    }
                    }
                }

            }

        }else if(node instanceof TreeInstanceNode) {
            TreeInstanceNode instanceNode = (TreeInstanceNode) node;
            ConstructorInstance constructureInstance = instanceNode.getConstructorInstance();
            ConstructorDefinition constructorDefinition = (ConstructorDefinition) constructureInstance.getTypeDefinition();
            for (ConstructorDefinition.ConstructorParam cparam: constructorDefinition.getParameters()){
                if("Widget".equals(cparam.paramtypename)){
                    if(cparam.iscollection) {
                        TreePropertyNode propertyNode = createOrGetTreeNode(constructureInstance, cparam.paramname);
                        node.addTreeNode(propertyNode);
                        generateSubTree(propertyNode);
                    }else{
                        TreePropertyInstanceNode propertyInstanceNode = createOrGetTreeNode(constructureInstance, cparam.paramname, (ConstructorInstance) constructureInstance.getProperty(cparam.paramname));
                        node.addTreeNode(propertyInstanceNode);
                        generateSubTree(propertyInstanceNode);
                    }
                }
            }
        }else if(node instanceof TreePropertyNode) {
            TreePropertyNode propertyNode = (TreePropertyNode) node;
            ConstructorInstance constructureInstance = propertyNode.getConstructureInstance();
            String property = propertyNode.getProperty();
            Object v = constructureInstance.getProperty(property);
            if (v instanceof Collection) {
                Collection subvalues = (Collection) v;
                for(Object subvalue: subvalues){
                    if(subvalue instanceof ConstructorInstance) {
                        TreeInstanceNode subnode = createOrGetTreeNode((ConstructorInstance) subvalue);
                        propertyNode.addTreeNode(subnode);
                        generateSubTree(subnode);
                    }
                }
            }else if(v instanceof ConstructorInstance) {
                TreeInstanceNode subnode = createOrGetTreeNode((ConstructorInstance) v);
                propertyNode.addTreeNode(subnode);
                generateSubTree(subnode);
            }
        }
    }

    public List<TreeNode> generateOrderedVisibleTreeNodes(TreeNode node) {
        assert node!=null;
        List<TreeNode> result = new ArrayList<>();
        if(node.parentNode!=null&&!node.parentNode.isExpanded()){
            // do nothing
        }else{
            result.add(node);
            for(TreeNode subnode: node.getSubTreeNodes()) {
                List<TreeNode> subresult = generateOrderedVisibleTreeNodes(subnode);
                result.addAll(subresult);
            }
        }
        return result;

    }

    public List<Node> generateControls(TreeNode node) {
        assert node!=null;
        List<Node> result = new ArrayList<>();
        if(node.parentNode!=null&&!node.parentNode.isExpanded()){
            // do nothing
        }else{
            result.add(node.getTreeNodeControl());
            for(TreeNode subnode: node.getSubTreeNodes()) {
                List<Node> subresult = generateControls(subnode);
                result.addAll(subresult);
            }
        }
        return result;
    }
}
