package com.liuzg.treenodes;

import com.liuzg.models.TreeNodeDefination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegistedTreeNodeDefinationManager implements TreeNodeDefinitionManager {

    Map<String, TreeNodeDefination> definationMap = new HashMap<>();

    public void setDefinations(List<TreeNodeDefination> definations) {
        for (TreeNodeDefination defination: definations) {
            definationMap.put(defination.getName(), defination);
        }
    }

    @Override
    public List<TreeNodeDefination> getTreeNodeDefinitions() {
        return new ArrayList<>(definationMap.values());
    }

    @Override
    public TreeNodeDefination getDefination(String nodetype) {
        if(definationMap.containsKey(nodetype)) {
            return definationMap.get(nodetype);
        }
        return null;
    }
}
