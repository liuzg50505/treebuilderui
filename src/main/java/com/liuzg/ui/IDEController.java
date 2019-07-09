package com.liuzg.ui;

import com.liuzg.editorui.ValueEditor;
import com.liuzg.editorui.ValueEditorFactory;
import com.liuzg.flutter.FlutterRunner;
import com.liuzg.models.TreeNode;
import com.liuzg.models.TreeNodeCollection;
import com.liuzg.models.TreeNodeDefinition;
import com.liuzg.models.Widget;
import com.liuzg.storage.TreeReader;
import com.liuzg.treenodes.TreeNodeDefinitionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class IDEController {
    @FXML
    TreeNodeEditor treenodeEditor;

    @FXML
    TitledPane defaulttab;

    @FXML
    VBox acc_controls;

    @FXML
    VBox editorspane;


    private ValueEditorFactory editorFactory;
    private TreeNodeDefinitionManager definationManager;
    private ViewNode prevselectedtreeitem;
    private FlutterRunner flutterRunner;
    private TreeReader treeReader;

    public IDEController() {
    }

    public void loadDesignXml(String filepath) {
        treeReader.readFile(filepath);
    }

    public void loadControlToolbox() {
        acc_controls.getChildren().clear();
        List<TreeNodeDefinition> definitions = definationManager.getTreeNodeDefinitions();

        for (TreeNodeDefinition definition : definitions.stream()
                .sorted(Comparator.comparing(TreeNodeDefinition::getName))
                .collect(Collectors.toList())) {
            AnchorPane pane = new AnchorPane();
            Button btn = new Button();
            btn.setText(definition.getName());
            btn.setOnAction(event -> {
                TreeNode treeNode = new TreeNode(definition);
                ViewNode viewnode = treenodeEditor.getSelectedNode();
                if (viewnode != null) {
                    TreeNode parent = viewnode.getParentTreeNode();
                    if (viewnode.getType() == ViewNodeType.NODE_PROPERTY) {
                        parent.setProperty(viewnode.getProp(), treeNode);
                    } else if (viewnode.getType() == ViewNodeType.NODES_PROPERTY) {
                        TreeNodeCollection curnodes = (TreeNodeCollection) parent.getProperty(viewnode.getProp());
                        if (curnodes == null) curnodes = new TreeNodeCollection();
                        TreeNodeCollection newnodes = new TreeNodeCollection(curnodes);
                        newnodes.add(treeNode);
                        parent.setProperty(viewnode.getProp(), newnodes);

                    }
                    treenodeEditor.expandViewNode(viewnode, true);
                    treenodeEditor.refresh();
                }

            });
            pane.getChildren().add(btn);
            AnchorPane.setLeftAnchor(btn, 0.0);
            AnchorPane.setRightAnchor(btn, 0.0);
            AnchorPane.setTopAnchor(btn, 0.0);
            AnchorPane.setBottomAnchor(btn, 0.0);
            acc_controls.getChildren().add(pane);
        }
    }

    public void init() {
        defaulttab.expandedProperty().setValue(true);
        defaulttab.setExpanded(true);
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:treenode.xml");
        editorFactory = (ValueEditorFactory) context.getBean("editorFactory");
        definationManager = (TreeNodeDefinitionManager) context.getBean("definationManager");
        flutterRunner = (FlutterRunner) context.getBean("flutterRunner");
        treeReader = new TreeReader(definationManager);

        loadControlToolbox();

        loadDesignXml("/Volumes/macdata/MyProjects/treebuildermvcprj/assets/design.xml");
        Widget widget = treeReader.getWidget("AddressBookController");


        TreeNode root = widget.getTreeNode();
        treenodeEditor.initialized();
        treenodeEditor.setRoot(root);
        treenodeEditor.refresh(true);
        treenodeEditor.addViewNodeSelectedListener(viewnode -> {
            onSelectedTreeItemChanged();
        });


        Scene scene = editorspane.getScene();
        scene.getWindow().setOnCloseRequest(event -> {
            flutterRunner.stopapp();
        });

    }


    public void onSelectedTreeItemChanged() {
        ViewNode item = treenodeEditor.getSelectedNode();
        if (prevselectedtreeitem == item) return;
        prevselectedtreeitem = item;
        if (item != null) {
            TreeNode treenode = item.getTreeNode();

            if (item.getType() == ViewNodeType.NODES_PROPERTY) {
                editorspane.getChildren().clear();
                return;
            }
            editorspane.getChildren().clear();
            if (treenode != null) {
                Map<String, String> proptypemap = treenode.getDefination().getPropertyTypeMap();
                for (Map.Entry<String, String> entry : proptypemap.entrySet()) {
                    String prop = entry.getKey();
                    String type = entry.getValue();

                    if (!treenode.getDefination().getNodeProperties().contains(prop) &&
                            !treenode.getDefination().getNodesProperties().contains(prop)) {
                        ValueEditor editor = editorFactory.createValueEditor(type);
                        if (editor != null) {
                            editor.setLabel(prop + ":");
                            editor.setValue(treenode.getProperty(prop));
                            editor.addListener((curEditor, newValue) -> {
                                treenode.setProperty(prop, newValue);
                                treenodeEditor.refresh();
                            });
                            editorspane.getChildren().add((Node) editor);
                        }
                    }
                }
            }
        }
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
