package com.liuzg.ui;

import com.liuzg.editorui.ValueEditor;
import com.liuzg.editorui.ValueEditorFactory;
import com.liuzg.flutter.FlutterRunner;
import com.liuzg.models.*;
import com.liuzg.storage.TreeWriter;
import com.liuzg.treenodes.TreeNodeDefinitionManager;
import com.liuzg.uitls.FxUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileWriter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class IDEController {
    @FXML
    TextArea txtOutput;

    @FXML
    TitledPane editorPane;

    @FXML
    ProjectTreeView projectTreeView;

    @FXML
    Accordion accordion;

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
    private Widget selectedProjectWidget;

    private Project currentProject;
    private Widget currentWidget;
    private Scene scene;

    public IDEController() {
    }

    // getters and setters

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    // outlets

    public void init() {
        accordion.setExpandedPane(defaulttab);
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:treenode.xml");
        editorFactory = (ValueEditorFactory) context.getBean("editorFactory");
        definationManager = (TreeNodeDefinitionManager) context.getBean("definationManager");
        flutterRunner = (FlutterRunner) context.getBean("flutterRunner");

        loadControlToolbox();

        treenodeEditor.addViewNodeSelectedListener(viewnode -> {
            onSelectedTreeItemChanged();
        });

        treenodeEditor.addDataChangedListener(() -> {
            onWidgetTreeChanged();
        });

        projectTreeView.addWidgetChangedListener(selectedWidget -> {
            IDEController.this.selectedProjectWidget = selectedWidget;
        });

        flutterRunner.addOutputListener(outputstr -> {
            onFlutterOutput(outputstr);
        });

        scene.getWindow().setOnCloseRequest(event -> {
            flutterRunner.stopapp();
        });

    }



    // methods

    private void loadControlToolbox() {
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
                    onWidgetTreeChanged();
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


    // event handlers

    // event handlers - flutter runner
    public void onStartClick(ActionEvent actionEvent) {
        flutterRunner.setProjectPath(currentProject.getProjectPath());
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

    private void onFlutterOutput(String outputstr) {
        Platform.runLater(()->{
            txtOutput.appendText(outputstr);
            txtOutput.appendText("\n");
            txtOutput.setScrollTop(Double.MAX_VALUE);
        });
    }

    // event handlers - projecttreeview
    public void onProjectDoubleClick(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount()>1) {
            if(selectedProjectWidget!=null) {
                if(treenodeEditor.getRoot()!=null) {
                    if(treenodeEditor.isDirty()){
                        selectedProjectWidget.getDesign().setDirty(true);
                    }
                }
                editorPane.setText(String.format("Editor: %s(%s)", selectedProjectWidget.getControllerName(),selectedProjectWidget.getDesign().getRelativePath()));

                this.currentWidget = selectedProjectWidget;
                treenodeEditor.setRoot(selectedProjectWidget.getTreeNode());
                treenodeEditor.initialize();
                treenodeEditor.refresh(true);
            }
        }
    }

    public void onProjectOpenClick(ActionEvent actionEvent) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showDialog(treenodeEditor.getScene().getWindow());
        if(file!=null) {
            // load project
            currentProject = new Project();
            currentProject.setProjectPath(file.getAbsolutePath());
            currentProject.setTreeNodeDefinitionManager(definationManager);
            currentProject.scanFile();
            // update project view
            projectTreeView.setProject(currentProject);
            projectTreeView.refreshView();
        }
    }

    // event handlers - IDE main toolbar

    public void onProjectAddClick(ActionEvent actionEvent) {
    }

    public void onSaveProjectClick(ActionEvent actionEvent) {
        for(Design design: currentProject.getDesigns()) {
            if(design.isDirty()) {
                design.setDirty(false);
                File file = new File(design.getProject().getProjectPath(), design.getRelativePath());
                SAXReader reader = new SAXReader();
                try{
                    Document document = reader.read(file);
                    Element root = document.getRootElement();
                    Element widgetselems = root.element("widgets");
                    widgetselems.clearContent();
                    for (Widget widget: design.getWidgets()) {
                        TreeWriter treeWriter = new TreeWriter();
                        Element elem = treeWriter.widgetXml(widget);
                        widgetselems.add(elem);
                    }

                    OutputFormat format = OutputFormat.createPrettyPrint();
                    format.setEncoding("UTF-8");
                    XMLWriter write = new XMLWriter(new FileWriter(file), format);
                    write.write(document);
                    write.flush();
                    write.close();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        FxUtils.alert("Project Saved","Hint");
        projectTreeView.refreshView();
    }


    // event handlers - solution explorer
    public void onAddProjectItemClick(ActionEvent actionEvent) {
        if(currentProject==null) return;
        //TODO
    }

    public void onRefreshProjectClick(ActionEvent actionEvent) {
        if(currentProject==null) return;
        projectTreeView.refreshView();
    }

    public void onDuplicateProjectTreeItemClick(ActionEvent actionEvent) {
        if(currentProject==null) return;
        //TODO
    }

    public void onDeleteProjectItemClick(ActionEvent actionEvent) {
        if(currentProject==null) return;
        //TODO
    }

    // event handlers - treenodeeditor
    private void onSelectedTreeItemChanged() {
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
                                onWidgetTreeChanged();
                                treenodeEditor.refresh();
                            });
                            editorspane.getChildren().add((Node) editor);
                        }
                    }
                }
            }
        }
    }

    private void onWidgetTreeChanged() {
        currentWidget.getDesign().setDirty(true);
        projectTreeView.refreshView();
    }

    public void onCopyClick(ActionEvent actionEvent) {
        treenodeEditor.onCopyClick(actionEvent);
    }

    public void onCutClick(ActionEvent actionEvent) {
        treenodeEditor.onCutClick(actionEvent);
    }

    public void onPasteClick(ActionEvent actionEvent) {
        treenodeEditor.onPasteClick(actionEvent);
    }

    public void onDuplicateClick(ActionEvent actionEvent) {
        treenodeEditor.onDuplicateClick(actionEvent);
    }

    public void onMoveDownClick(ActionEvent actionEvent) {
        treenodeEditor.onMoveDownClick(actionEvent);
    }

    public void onMoveUpClick(ActionEvent actionEvent) {
        treenodeEditor.onMoveUpClick(actionEvent);
    }

    public void onCollapseClick(ActionEvent actionEvent) {
        treenodeEditor.onCollapseClick(actionEvent);
    }

    public void onExpandClick(ActionEvent actionEvent) {
        treenodeEditor.onExpandClick(actionEvent);
    }

    public void onDeleteClick(ActionEvent actionEvent) {
        treenodeEditor.onDeleteClick(actionEvent);
    }

}
