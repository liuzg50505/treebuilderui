package com.liuzg.models;

import com.liuzg.ui.ViewNode;
import com.liuzg.ui.ViewNodeType;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.*;

public class TreeNode {

    Map<String, Object> propertyMap = new HashMap<>();

    final TreeNodeDefinition defination;

    public TreeNode(TreeNodeDefinition defination) {
        this.defination = defination;
    }

    private static int iconsize = 24;

    private Node propertyIcon(Node icon) {
        ImageView propicon = new ImageView(
                new Image(TreeNode.class.getResourceAsStream("/assets/nodes/property.png"), 12, 12, true, true)
        );
        Pane pane = new Pane(icon, propicon);
        propicon.setX(12);
        propicon.setY(12);
        return pane;
    }

    private Node emptyPropertyIcon() {
        return new ImageView(
                new Image(TreeNode.class.getResourceAsStream("/assets/nodes/property.png"), iconsize, iconsize, true, true)
        );
    }


    private Node propertiesIcon() {
        return new ImageView(
                new Image(TreeNode.class.getResourceAsStream("/assets/nodes/properties.png"), iconsize, iconsize, true, true)
        );
    }

    private Node nodeIcon() {
        return new ImageView(
                new Image(TreeNode.class.getResourceAsStream(defination.getIcon()), iconsize, iconsize, true, true)
        );
    }

    public String getName() {
        return defination.getName();
    }

    public TreeNodeDefinition getDefination() {
        return defination;
    }

    public void setProperty(String prop, Object value) {
        if(defination.getNodesProperties().contains(prop)) {
            if(value==null) {
                propertyMap.put(prop, new TreeNodeCollection());
            }else{
                if(value instanceof TreeNodeCollection) {
                    propertyMap.put(prop, value);
                }else if(value instanceof Collection){
                    propertyMap.put(prop, new TreeNodeCollection((Collection)value));
                }else{
                    throw new RuntimeException("Invalid value type for node collection property!");
                }
            }
        }else{
            propertyMap.put(prop, value);
        }
    }

    public Object getProperty(String prop) {
        if(propertyMap.containsKey(prop)){
            return propertyMap.get(prop);
        }
        return null;
    }

    private String simplePropertyText() {
        List<String> items = new ArrayList<>();
        for (String prop: propertyMap.keySet()) {
            if(defination.getNodeProperties().contains(prop)||defination.getNodesProperties().contains(prop)){
                continue;
            }
            Object value = propertyMap.get(prop);
            if(value==null) {
                items.add(String.format("%s: %s", prop, "null"));
            }else{
                items.add(String.format("%s: %s", prop, value.toString()));
            }
        }
        return String.join(", ", items);
    }

    public TreeItem<ViewNode> buildTree() {
        String info = simplePropertyText().trim();
        String nodetxt = String.format("%s => %s",getName(), simplePropertyText());
        if(info.length()==0) nodetxt = getName();
        TreeItem<ViewNode> rootItem = new TreeItem<>(
                new ViewNode(this, ViewNodeType.NODE, nodetxt,nodeIcon()),
                nodeIcon());

        for (String prop : defination.getNodeProperties()) {
            TreeNode treeNode = (TreeNode) propertyMap.get(prop);
            if (treeNode != null) {
                TreeItem<ViewNode> subtreeitem = treeNode.buildTree();
                subtreeitem.setGraphic(propertyIcon(subtreeitem.getGraphic()));
                subtreeitem.getValue().setText(String.format("%s: %s => %s", prop, treeNode.getName(), treeNode.simplePropertyText()));
                subtreeitem.getValue().setIcon(propertyIcon(subtreeitem.getValue().getIcon()));
                subtreeitem.getValue().setProp(prop);
                subtreeitem.getValue().setType(ViewNodeType.NODE_PROPERTY);
                subtreeitem.getValue().setParentTreeNode(this);
                rootItem.getChildren().add(subtreeitem);
            }else{
                TreeItem<ViewNode> subtreeitem = new TreeItem<>(new ViewNode(
                        null, ViewNodeType.NODE_PROPERTY, String.format("%s: %s", prop, "null")
                ), emptyPropertyIcon());
                subtreeitem.getValue().setProp(prop);
                subtreeitem.getValue().setParentTreeNode(this);
                rootItem.getChildren().add(subtreeitem);

            }
        }

        for (String prop : defination.getNodesProperties()) {
            TreeNodeCollection treeNodes = (TreeNodeCollection) propertyMap.get(prop);
            int count = 0;
            if (treeNodes != null) count = treeNodes.size();
            else treeNodes = new TreeNodeCollection();
            TreeItem<ViewNode> propertyItem = new TreeItem<>(
                    new ViewNode(this, ViewNodeType.NODES_PROPERTY,
                            String.format("%s: %d elems", prop, count),
                            propertiesIcon()),
                    propertiesIcon());
            propertyItem.getValue().setProp(prop);
            propertyItem.getValue().setParentTreeNode(this);

            for (TreeNode subNode : treeNodes) {
                TreeItem<ViewNode> subtreeitem = subNode.buildTree();
                subtreeitem.getValue().setParentTreeNode(this);
                propertyItem.getChildren().add(subtreeitem);
            }
            rootItem.getChildren().add(propertyItem);
        }

        return rootItem;
    }

    public TreeNode cloneTreeNode() {
        TreeNode newnode = new TreeNode(defination);
        for (String prop: propertyMap.keySet()){
            Object value = propertyMap.get(prop);
            if(value instanceof TreeNode) {
                TreeNode valueTreeNode = (TreeNode) value;
                newnode.setProperty(prop, valueTreeNode.cloneTreeNode());
            }else if(value instanceof TreeNodeCollection) {
                TreeNodeCollection valueTreeNode = new TreeNodeCollection();
                for (TreeNode item: (TreeNodeCollection) value) {
                    valueTreeNode.add(item.cloneTreeNode());
                }
                newnode.setProperty(prop, valueTreeNode);
            }else{
                newnode.setProperty(prop, value);
            }
        }
        return newnode;
    }

    @Override
    public String toString() {
        return getName();
    }
}
