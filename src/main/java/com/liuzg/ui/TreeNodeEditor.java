package com.liuzg.ui;

import com.liuzg.models.TreeNode;
import com.liuzg.models.TreeNodeCollection;
import com.liuzg.uitls.ScrollBarState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TreeNodeEditor extends AnchorPane {

    public static interface ViewNodeSelectedListener {
        void onViewNodeSelected(ViewNode treeNode);
    }

    private List<ViewNodeSelectedListener> viewNodeSelectedListeners;

    private TreeView<ViewNode> treeView;
    private TreeNode clipboard = null; // clipboard
    private TreeNode root; // root treenode of this editor
    private Set<ViewNode> selectednodes; // selected treenodes
    private Set<ViewNode> expandednodes; // expanded treenodes

    ScrollBarState vscrollBarState;

    public TreeNodeEditor() {
        treeView = new TreeView<>();
        viewNodeSelectedListeners = new ArrayList<>();

        selectednodes = new HashSet<>();
        expandednodes = new HashSet<>();

        this.getChildren().add(treeView);
        AnchorPane.setTopAnchor(treeView, 0.0);
        AnchorPane.setBottomAnchor(treeView, 0.0);
        AnchorPane.setLeftAnchor(treeView, 0.0);
        AnchorPane.setRightAnchor(treeView, 0.0);

    }

    // initialize treenode editor
    public void initialized() {

        vscrollBarState = new ScrollBarState(treeView, Orientation.VERTICAL);

        Scene scene = this.getScene();
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY),
                () -> {
                    if(treeView.isFocused()) onCopyClick(null);
                }
        );

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_ANY),
                () -> {
                    if(treeView.isFocused()) onCutClick(null);
                }
        );

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_ANY),
                () -> {
                    if(treeView.isFocused()) onPasteClick(null);
                }
        );

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_ANY),
                () -> {
                    if(treeView.isFocused()) onDumplicatedClick(null);
                }
        );

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.DELETE),
                () -> {
                    if(treeView.isFocused()) onDeleteClick(null);
                }
        );

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.DOWN, KeyCombination.CONTROL_ANY),
                () -> {
                    if(treeView.isFocused()) onMoveDownClick(null);
                }
        );

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.UP, KeyCombination.CONTROL_ANY),
                () -> {
                    if(treeView.isFocused()) onMoveUpClick(null);
                }
        );

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.LEFT, KeyCombination.CONTROL_ANY),
                () -> {
                    if(treeView.isFocused()) onCollapseClick(null);
                }
        );

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.RIGHT, KeyCombination.CONTROL_ANY),
                () -> {
                    if(treeView.isFocused()) onExpandClick(null);
                }
        );

        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<ViewNode>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<ViewNode>> observable, TreeItem<ViewNode> oldValue, TreeItem<ViewNode> newValue) {
//                Integer idx = treeView.getSelectionModel().getSelectedIndices().get(0);
//                treeView.getFocusModel().focus(idx);
                for(ViewNodeSelectedListener listener: viewNodeSelectedListeners) {
                    if(listener!=null) {
                        try{
                            listener.onViewNodeSelected(newValue.getValue());
                        }catch (Exception e) {}
                    }
                }
            }
        });

    }

    // event callbacks

    public void addViewNodeSelectedListener(ViewNodeSelectedListener listener) {
        if(listener==null) return;
        viewNodeSelectedListeners.add(listener);
    }

    // getter and setters
    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    public TreeNode getClipboard() {
        return clipboard;
    }

    public void setClipboard(TreeNode clipboard) {
        this.clipboard = clipboard;
    }

    // outlets

    public ViewNode getSelectedNode() {
        TreeItem<ViewNode> item = treeView.getSelectionModel().getSelectedItem();
        if(item==null) return null;
        return item.getValue();
    }

    public void refresh() {
        this.refreshTree();
    }

    public void refresh(boolean expandall) {
        this.refreshTree(expandall);
    }

    public void expandViewNode(ViewNode viewNode, boolean expanded) {
        if(treeView.getRoot()==null) return;
        expandViewNode(viewNode, treeView.getRoot(), expanded);
    }

    private void expandViewNode(ViewNode viewNode, TreeItem<ViewNode> item, boolean expanded) {
        if(item.getValue()==viewNode){
            item.setExpanded(expanded);
        }
        for(TreeItem<ViewNode> child: item.getChildren()){
            expandViewNode(viewNode, child, expanded);
        }
    }

    // utils functions
    private void rememberTreeNodeStates(){
        selectednodes.clear();
        expandednodes.clear();
        for (TreeItem<ViewNode> item: treeView.getSelectionModel().getSelectedItems()){
            selectednodes.add(item.getValue());
        }
        rememberTreeNodeStates(treeView.getRoot());
    }

    private void rememberTreeNodeStates(TreeItem<ViewNode> treeItem) {
        if(treeItem==null) return;
        if(treeItem.isExpanded()) {
            expandednodes.add(treeItem.getValue());
        }
        for(TreeItem<ViewNode> item: treeItem.getChildren()) {
            rememberTreeNodeStates(item);
        }
    }

    private void refreshTree() {
        refreshTree(false);
    }

    private void refreshTree(boolean expandall) {
        vscrollBarState.save();

        rememberTreeNodeStates();
        TreeItem<ViewNode> rootViewNode = root.buildTree();
        treeView.setRoot(rootViewNode);

        if(expandall) expandTreeView(rootViewNode);
        else{
            recoverExpandState(rootViewNode);
        }

        vscrollBarState.restore();
    }

    private void recoverExpandState(TreeItem<ViewNode> item) {
        if(expandednodes.contains(item.getValue())){
            item.setExpanded(true);
        }else{
            item.setExpanded(false);
        }
        if(selectednodes.contains(item.getValue())) {
            treeView.getSelectionModel().select(item);
        }

        for(TreeItem<ViewNode> subitem: item.getChildren()) {
            recoverExpandState(subitem);
        }
    }

    private void expandTreeView(TreeItem<?> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(true);
            for (TreeItem<?> child : item.getChildren()) {
                expandTreeView(child);
            }
        }
    }

    private void collapseTreeView(TreeItem<?> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(false);
            for (TreeItem<?> child : item.getChildren()) {
                collapseTreeView(child);
            }
        }
    }


    // event callbacks

    private void onCopyClick(ActionEvent actionEvent) {
        TreeItem<ViewNode> item = treeView.getSelectionModel().getSelectedItem();
        if (item != null && (item.getValue().getType() == ViewNodeType.NODE_PROPERTY || item.getValue().getType() == ViewNodeType.NODE)) {
            ViewNode viewnode = item.getValue();
            if (viewnode.getType() == ViewNodeType.NODE_PROPERTY) {
                TreeNode parent = viewnode.getParentTreeNode();
                clipboard = ((TreeNode) parent.getProperty(viewnode.getProp())).cloneTreeNode();
            } else if (viewnode.getType() == ViewNodeType.NODE) {
                clipboard = viewnode.getTreeNode().cloneTreeNode();
            }
        }
    }

    private void onCutClick(ActionEvent actionEvent) {
        TreeItem<ViewNode> item = treeView.getSelectionModel().getSelectedItem();
        if (item != null && (item.getValue().getType() == ViewNodeType.NODE_PROPERTY || item.getValue().getType() == ViewNodeType.NODE)) {
            ViewNode viewnode = item.getValue();

            TreeItem<ViewNode> parentitem = item.getParent();
            if (parentitem != null) {
                if (viewnode.getType() == ViewNodeType.NODE_PROPERTY) {
                    TreeNode parent = viewnode.getParentTreeNode();
                    clipboard = ((TreeNode) parent.getProperty(viewnode.getProp())).cloneTreeNode();
                    parent.setProperty(viewnode.getProp(), null);
                } else if (viewnode.getType() == ViewNodeType.NODE) {
                    TreeNode parent = viewnode.getParentTreeNode();
                    clipboard = viewnode.getTreeNode().cloneTreeNode();
                    String prop = parentitem.getValue().getProp();
                    TreeNodeCollection collection = (TreeNodeCollection) parent.getProperty(prop);
                    collection.remove(viewnode.getTreeNode());
                }
            } else {
                root = null;
            }
        }
        refreshTree();
    }

    private void onDeleteClick(ActionEvent actionEvent) {
        TreeItem<ViewNode> item = treeView.getSelectionModel().getSelectedItem();
        if (item != null && (item.getValue().getType() == ViewNodeType.NODE_PROPERTY || item.getValue().getType() == ViewNodeType.NODE)) {
            ViewNode viewnode = item.getValue();

            TreeItem<ViewNode> parentitem = item.getParent();
            if (parentitem != null) {
                if (viewnode.getType() == ViewNodeType.NODE_PROPERTY) {
                    TreeNode parent = viewnode.getParentTreeNode();
                    parent.setProperty(viewnode.getProp(), null);
                    treeView.getSelectionModel().select(item.getParent());
                } else if (viewnode.getType() == ViewNodeType.NODE) {
                    ObservableList<TreeItem<ViewNode>> items = item.getParent().getChildren();
                    int idx = items.indexOf(item);
                    TreeItem<ViewNode> nextselected = null;
                    if(items.size()>1){
                        if(items.size()==idx+1) {
                            nextselected = items.get(idx-1);
                        }else{
                            nextselected = items.get(idx+1);
                        }
                    }else{
                        nextselected = item.getParent();
                    }

                    TreeNode parent = viewnode.getParentTreeNode();
                    TreeNode t = viewnode.getTreeNode();
                    String prop = parentitem.getValue().getProp();
                    TreeNodeCollection collection = (TreeNodeCollection) parent.getProperty(prop);
                    collection.remove(t);

                    treeView.getSelectionModel().select(nextselected);
                }
            } else {
                root = null;
            }
        }
        refreshTree();
    }

    private void onPasteClick(ActionEvent actionEvent) {
        if (clipboard != null) {
            TreeNode pastenode = clipboard.cloneTreeNode();
            TreeItem<ViewNode> item = treeView.getSelectionModel().getSelectedItem();
            if (item != null) {
                ViewNode viewnode = item.getValue();
                TreeNode parent = viewnode.getParentTreeNode();
                if (viewnode.getType() == ViewNodeType.NODE_PROPERTY) {
                    parent.setProperty(viewnode.getProp(), pastenode);
                } else if (viewnode.getType() == ViewNodeType.NODES_PROPERTY) {
                    TreeNodeCollection curnodes = (TreeNodeCollection) parent.getProperty(viewnode.getProp());
                    if (curnodes == null) curnodes = new TreeNodeCollection();
                    TreeNodeCollection newnodes = new TreeNodeCollection(curnodes);
                    newnodes.add(pastenode);
                    parent.setProperty(viewnode.getProp(), newnodes);

                }
                item.setExpanded(true);
                refreshTree();
            }
        }
    }

    private void onDumplicatedClick(ActionEvent actionEvent) {
        TreeItem<ViewNode> item = treeView.getSelectionModel().getSelectedItem();
        if (item != null && (item.getValue().getType() == ViewNodeType.NODE)) {
            TreeNode cur = item.getValue().getTreeNode();
            TreeNode parent = item.getValue().getParentTreeNode();
            String prop = item.getParent().getValue().getProp();
            TreeNodeCollection collection = (TreeNodeCollection) parent.getProperty(prop);
            TreeNode curDump = cur.cloneTreeNode();
            int idx = collection.indexOf(cur);
            collection.add(idx+1, curDump);
            refreshTree();
        }
    }

    private void onMoveDownClick(ActionEvent actionEvent) {
        TreeItem<ViewNode> item = treeView.getSelectionModel().getSelectedItem();
        if (item != null) {
            ViewNode viewnode = item.getValue();
            if(viewnode.getType()==ViewNodeType.NODE) {
                TreeNode cur = viewnode.getTreeNode();
                TreeNode parent = viewnode.getParentTreeNode();
                String prop = item.getParent().getValue().getProp();
                TreeNodeCollection nodes = (TreeNodeCollection) parent.getProperty(prop);
                int idx = nodes.indexOf(cur);
                if(idx>-1&&idx<nodes.size()-1) {
                    nodes.remove(cur);
                    nodes.add(idx+1, cur);
                    refreshTree();
                }
            }
        }
    }

    private void onMoveUpClick(ActionEvent actionEvent) {
        TreeItem<ViewNode> item = treeView.getSelectionModel().getSelectedItem();
        if (item != null) {
            ViewNode viewnode = item.getValue();
            if(viewnode.getType()==ViewNodeType.NODE) {
                TreeNode cur = viewnode.getTreeNode();
                TreeNode parent = viewnode.getParentTreeNode();
                String prop = item.getParent().getValue().getProp();
                TreeNodeCollection nodes = (TreeNodeCollection) parent.getProperty(prop);
                int idx = nodes.indexOf(cur);
                if(idx>0) {
                    nodes.remove(cur);
                    nodes.add(idx-1, cur);
                    refreshTree();
                }
            }
        }
    }

    private void onExpandClick(ActionEvent actionEvent) {
        TreeItem<ViewNode> item = treeView.getSelectionModel().getSelectedItem();
        if (item != null) {
            expandTreeView(item);
        }
        treeView.getSelectionModel().select(item);
    }

    private void onCollapseClick(ActionEvent actionEvent) {
        TreeItem<ViewNode> item = treeView.getSelectionModel().getSelectedItem();
        if (item != null) {
            collapseTreeView(item);
        }
        treeView.getSelectionModel().select(item);
    }



}
