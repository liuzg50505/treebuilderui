package com.liuzg.treenodes;

import com.liuzg.models.TreeNodeDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistedTreeNodeDefinitionManager implements TreeNodeDefinitionManager {

    Map<String, TreeNodeDefinition> definationMap = new HashMap<>();

    public void setDefinations(List<TreeNodeDefinition> definations) {
        for (TreeNodeDefinition defination: definations) {
            definationMap.put(defination.getName(), defination);
        }
    }

    @Override
    public List<TreeNodeDefinition> getTreeNodeDefinitions() {
        return new ArrayList<>(definationMap.values());
    }

    @Override
    public TreeNodeDefinition getDefination(String nodetype) {
        if(definationMap.containsKey(nodetype)) {
            return definationMap.get(nodetype);
        }
        return null;
    }
}
