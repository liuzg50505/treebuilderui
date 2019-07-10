package com.liuzg.storage;

import com.liuzg.models.TreeNode;
import com.liuzg.models.Widget;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;
import java.util.Map;

public class TreeWriter {
    public Element widgetXml(Widget widget) {
        Element elem = treeNodeToElem(widget.getTreeNode());
        Element widgetelem = DocumentHelper.createElement("widget");
        widgetelem.addAttribute("controller", widget.getControllerName());
        widgetelem.add(elem);
        return widgetelem;
    }

    private Element treeNodeToElem(TreeNode treeNode) {
        Element elem = DocumentHelper.createElement(treeNode.getName());

        for(String prop: treeNode.getDefination().getPropertyTypeMap().keySet()) {
            elem.addAttribute(prop, (String) treeNode.getProperty(prop));
        }

        for(String prop: treeNode.getDefination().getMapProperties()) {
            Map mapvalue = (Map) treeNode.getProperty(prop);
            if(mapvalue!=null) {
                Element propelem = DocumentHelper.createElement(prop);
                for(Object key: mapvalue.keySet()) {
                    Object value = mapvalue.get(key);
                    Element itemelem = DocumentHelper.createElement("item");
                    itemelem.addAttribute("key", key.toString());
                    itemelem.addAttribute("value", value.toString());
                    propelem.add(itemelem);
                }
                elem.add(propelem);
            }
        }


        for(String prop: treeNode.getDefination().getNodeProperties()) {
            TreeNode subnode = (TreeNode) treeNode.getProperty(prop);
            if(subnode!=null) {
                Element subelem = treeNodeToElem(subnode);
                Element propelem = DocumentHelper.createElement(prop);
                propelem.add(subelem);
                elem.add(propelem);
            }
        }

        for(String prop: treeNode.getDefination().getNodesProperties()) {
            Element propelem = DocumentHelper.createElement(prop);
            List<TreeNode> subnodes = (List<TreeNode>) treeNode.getProperty(prop);
            if(subnodes!=null) {
                for(TreeNode subnode: subnodes) {
                    if(subnode!=null) {
                        Element subelem = treeNodeToElem(subnode);
                        propelem.add(subelem);
                    }
                }
                elem.add(propelem);
            }
        }

        return elem;
    }
}
