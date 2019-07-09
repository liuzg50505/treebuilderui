package com.liuzg.storage;

import com.liuzg.models.TreeNode;
import com.liuzg.models.Widget;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;

public class TreeWriter {
    public Element widgetXml(Widget widget) {
        Element elem = treeNodeToElem(widget.getTreeNode());
        Element widgetelem = DocumentHelper.createElement("widget");
        widgetelem.attributeValue("controller", widget.getControllerName());
        widgetelem.add(elem);
        return elem;
    }

    private Element treeNodeToElem(TreeNode treeNode) {
        Element elem = DocumentHelper.createElement(treeNode.getName());

        for(String prop: treeNode.getDefination().getPropertyTypeMap().keySet()) {
            elem.attributeValue(prop, (String) treeNode.getProperty(prop));
        }

        for(String prop: treeNode.getDefination().getNodeProperties()) {
            TreeNode subnode = (TreeNode) treeNode.getProperty(prop);
            Element subelem = treeNodeToElem(subnode);
            Element propelem = DocumentHelper.createElement("prop");
            propelem.add(subelem);
            elem.add(propelem);
        }

        for(String prop: treeNode.getDefination().getNodesProperties()) {
            Element propelem = DocumentHelper.createElement("prop");
            List<TreeNode> subnodes = (List<TreeNode>) treeNode.getProperty(prop);
            for(TreeNode subnode: subnodes) {
                Element subelem = treeNodeToElem(subnode);
                propelem.add(subelem);
            }
            elem.add(propelem);
        }

        return elem;
    }
}
