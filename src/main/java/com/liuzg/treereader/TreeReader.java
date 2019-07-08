package com.liuzg.treereader;

import com.liuzg.models.TreeNode;
import com.liuzg.models.TreeNodeDefination;
import com.liuzg.models.Widget;
import com.liuzg.treenodes.TreeNodeDefinitionManager;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TreeReader {
    List<Widget> widgets;
    TreeNodeDefinitionManager treeNodeDefinitionManager;

    public TreeReader(TreeNodeDefinitionManager treeNodeDefinitionManager) {
        widgets = new ArrayList<>();
        this.treeNodeDefinitionManager = treeNodeDefinitionManager;
    }

    public void readFile(String xmlfilepath) {
        SAXReader reader = new SAXReader();
        try{
            Document document = reader.read(new File(xmlfilepath));
            Element root = document.getRootElement();
            Element widgetselems = root.element("widgets");
            for(Object widgetnode: widgetselems.elements()) {
                Element widgetelem = (Element) widgetnode;
                if(widgetelem.elements().size()>0) {
                    Element roottreenodexml = (Element) widgetelem.elements().get(0);
                    String controllername = widgetelem.attribute("controller").getStringValue();
                    TreeNode treeNode = parseXml(roottreenodexml);
                    widgets.add(new Widget(treeNode, controllername));
                }

            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Widget getWidget(String controllername) {
        return widgets.stream()
                .filter(t -> t.getControllerName().equals(controllername))
                .findFirst().orElse(null);
    }

    public TreeNode parseXml(Element treenodelem) {
        String tag = treenodelem.getName();
        TreeNodeDefination defination = treeNodeDefinitionManager.getDefination(tag);
        if(defination==null) {
            System.out.println(String.format("Tag %s not found!", tag));
        }
        TreeNode treeNode = new TreeNode(defination);
        for (String propname: defination.getPropertyTypeMap().keySet()) {
            String type = defination.getPropertyTypeMap().get(propname);
            if("Map".equals(type)) continue; //TODO:
            Object value = getElemPropertyValue(treenodelem, propname);
            if(value==null) continue;
            treeNode.setProperty(propname, value);
        }

        for(String propname: defination.getNodeProperties()) {
            Element subelem = treenodelem.element(propname);
            if(subelem!=null){
                TreeNode subnode = parseXml((Element) subelem.elements().get(0));
                treeNode.setProperty(propname, subnode);
            }
        }

        for(String propname: defination.getNodesProperties()) {
            Element subelemroot = treenodelem.element(propname);
            if(subelemroot!=null){
                List<TreeNode> subtreenodes = new ArrayList<>();
                for(Object subelemobj: subelemroot.elements()) {
                    Element subelem = (Element) subelemobj;
                    TreeNode subnode = parseXml(subelem);
                    subtreenodes.add(subnode);
                }
                treeNode.setProperty(propname, subtreenodes);
            }
        }
        return treeNode;
    }

    private Object getElemPropertyValue(Element elem, String propertyName) {
        Attribute attr = elem.attribute(propertyName);
        if(attr==null) {
            Element subelem = elem.element(propertyName);
            if(subelem==null) {
                return null;
            }
            return subelem.getText();
        }else{
            return attr.getValue();
        }
    }

}
