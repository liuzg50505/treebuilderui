package com.liuzg.def.events;

import com.liuzg.def.MyTreeEditor;
import com.liuzg.def.MyTreeNode;

public class MyTreeEditorSelectionChangedEvent {
    public MyTreeEditor editor;
    public MyTreeNode treeNode;

    public MyTreeEditorSelectionChangedEvent(MyTreeEditor editor, MyTreeNode treeNode) {
        this.editor = editor;
        this.treeNode = treeNode;
    }
}
