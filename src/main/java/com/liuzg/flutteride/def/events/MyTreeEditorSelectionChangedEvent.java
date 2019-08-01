package com.liuzg.flutteride.def.events;

import com.liuzg.flutteride.def.treeeditor.TreeEditor;
import com.liuzg.flutteride.def.treeeditor.TreeNode;

public class MyTreeEditorSelectionChangedEvent {
    public TreeEditor editor;
    public TreeNode treeNode;

    public MyTreeEditorSelectionChangedEvent(TreeEditor editor, TreeNode treeNode) {
        this.editor = editor;
        this.treeNode = treeNode;
    }
}
