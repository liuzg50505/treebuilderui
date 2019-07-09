package com.liuzg.treenodes;

import com.liuzg.models.TreeNodeDefinition;

import java.util.List;

public interface TreeNodeDefinitionManager {
    List<TreeNodeDefinition> getTreeNodeDefinitions();

    TreeNodeDefinition getDefination(String nodetype);
}
