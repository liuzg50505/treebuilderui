package com.liuzg.treenodes;

import com.liuzg.models.TreeNodeDefination;

import java.util.List;

public interface TreeNodeDefinitionManager {
    List<TreeNodeDefination> getTreeNodeDefinitions();

    TreeNodeDefination getDefination(String nodetype);
}
