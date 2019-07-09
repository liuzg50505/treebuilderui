package com.liuzg.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TreeNodeDefinition {
    String name;
    String icon = "/assets/node.png";
    Map<String, String> propertyTypeMap = new HashMap<>();
    Set<String> nodeProperties = new HashSet<>();
    Set<String> nodesProperties = new HashSet<>();

    public TreeNodeDefinition() {
    }

    public TreeNodeDefinition(String name) {
        this.name = name;
    }

    public void defineNodeProperty(String nodeProperty) {
        nodeProperties.add(nodeProperty);
    }

    public void defineNodesProperties(String nodesProperty) {
        nodesProperties.add(nodesProperty);
    }

    public void defineProperty(String prop, String type) {
        propertyTypeMap.put(prop, type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Map<String, String> getPropertyTypeMap() {
        return propertyTypeMap;
    }

    public void setPropertyTypeMap(Map<String, String> propertyTypeMap) {
        this.propertyTypeMap = propertyTypeMap;
    }

    public Set<String> getNodeProperties() {
        return nodeProperties;
    }

    public void setNodeProperties(Set<String> nodeProperties) {
        this.nodeProperties = nodeProperties;
    }

    public Set<String> getNodesProperties() {
        return nodesProperties;
    }

    public void setNodesProperties(Set<String> nodesProperties) {
        this.nodesProperties = nodesProperties;
    }
}
