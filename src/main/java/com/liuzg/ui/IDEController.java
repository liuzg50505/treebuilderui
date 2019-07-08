package com.liuzg.ui;

import com.liuzg.editorui.ValueEditor;
import com.liuzg.editorui.ValueEditorFactory;
import com.liuzg.flutter.FlutterRunner;
import com.liuzg.models.TreeNode;
import com.liuzg.models.TreeNodeCollection;
import com.liuzg.models.Widget;
import com.liuzg.treenodes.TreeNodeDefinitionManager;
import com.liuzg.treereader.TreeReader;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class IDEController {
    @FXML
    VBox editorspane;

    @FXML
    TreeView<ViewNode> treeView;

    TreeNode clipboard = null;
    private TreeNode root;

    private Set<ViewNode> selectednodes;
    private Set<ViewNode> expandednodes;
    private ValueEditorFactory editorFactory;
    private TreeNodeDefinitionManager definationManager;
    private TreeItem<ViewNode> prevselectedtreeitem;
    private FlutterRunner flutterRunner;
    private TreeReader treeReader;

    public IDEController() {
        selectednodes = new HashSet<>();
        expandednodes = new HashSet<>();
    }

    public void loadDesignXml(String filepath) {
        treeReader.readFile(filepath);
    }

    public void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:treenode.xml");
        editorFactory = (ValueEditorFactory) context.getBean("editorFactory");
        definationManager = (TreeNodeDefinitionManager) context.getBean("definationManager");
        flutterRunner = (FlutterRunner) context.getBean("flutterRunner");
        treeReader = new TreeReader(definationManager);

        loadDesignXml("/Volumes/macdata/MyProjects/treebuildermvcprj/assets/design.xml");
        Widget widget = treeReader.getWidget("AddressBookController");

        root = widget.getTreeNode();

        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<ViewNode>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<ViewNode>> observable, TreeItem<ViewNode> oldValue, TreeItem<ViewNode> newValue) {
                onSelectedTreeItemChanged();
            }
        });

        Scene scene = treeView.getScene();
        scene.getWindow().setOnCloseRequest(event -> {
            flutterRunner.stopapp();
        });
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



        refreshTree(true);
    }


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
        rememberTreeNodeStates();
        TreeItem<ViewNode> rootViewNode = root.buildTree();
        treeView.setRoot(rootViewNode);

        if(expandall) expandTreeView(rootViewNode);
        else{
            recoverExpandState(rootViewNode);
        }
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

    public void onSelectedTreeItemChanged() {

        TreeItem<ViewNode> item = treeView.getSelectionModel().getSelectedItem();
        if(prevselectedtreeitem ==item) return;
        prevselectedtreeitem = item;
        if(item!=null) {
            TreeNode treenode = item.getValue().getTreeNode();

            if(item.getValue().getType()==ViewNodeType.NODES_PROPERTY) {
                editorspane.getChildren().clear();
                return;
            }
            editorspane.getChildren().clear();
            if(treenode!=null) {
                Map<String, String> proptypemap = treenode.getDefination().getPropertyTypeMap();
                for(Map.Entry<String, String> entry: proptypemap.entrySet()) {
                    String prop = entry.getKey();
                    String type = entry.getValue();

                    if (!treenode.getDefination().getNodeProperties().contains(prop) &&
                            !treenode.getDefination().getNodesProperties().contains(prop)) {
                        ValueEditor editor = editorFactory.createValueEditor(type);
                        if(editor!=null) {
                            editor.setLabel(prop + ":");
                            editor.setValue(treenode.getProperty(prop));
                            editor.addListener((curEditor, newValue) -> {
                                treenode.setProperty(prop, newValue);
                                refreshTree();
                            });
                            editorspane.getChildren().add((Node) editor);
                        }
                    }
                }
            }
        }
    }

    public void onCopyClick(ActionEvent actionEvent) {
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

    public void onCutClick(ActionEvent actionEvent) {
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

    public void onDeleteClick(ActionEvent actionEvent) {
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

    public void onPasteClick(ActionEvent actionEvent) {
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

    public void onDumplicatedClick(ActionEvent actionEvent) {
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


    public void onMoveDownClick(ActionEvent actionEvent) {
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

    public void onMoveUpClick(ActionEvent actionEvent) {
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

    public void onExpandClick(ActionEvent actionEvent) {
        TreeItem<ViewNode> item = treeView.getSelectionModel().getSelectedItem();
        if (item != null) {
            expandTreeView(item);
        }
        treeView.getSelectionModel().select(item);
    }

    public void onCollapseClick(ActionEvent actionEvent) {
        TreeItem<ViewNode> item = treeView.getSelectionModel().getSelectedItem();
        if (item != null) {
            collapseTreeView(item);
        }
        treeView.getSelectionModel().select(item);
    }

    public void onStartClick(ActionEvent actionEvent) {
        flutterRunner.setProjectPath("/Volumes/macdata/MyProjects/treebuildermvcprj");
        flutterRunner.startapp();
    }

    public void onHotRestartClick(ActionEvent actionEvent) {
        flutterRunner.hotRestart();
    }

    public void onHotReloadClick(ActionEvent actionEvent) {
        flutterRunner.hotload();
    }

    public void onStopClick(ActionEvent actionEvent) {
        flutterRunner.stopapp();
    }
}
