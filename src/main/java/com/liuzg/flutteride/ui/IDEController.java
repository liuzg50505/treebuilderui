package com.liuzg.flutteride.ui;

import com.liuzg.flutteride.def.reader.DefinitionReader;
import com.liuzg.flutteride.def.reader.Dom4JDefinitionReader;
import com.liuzg.flutteride.def.treeeditor.TreeEditor;
import com.liuzg.flutteride.def.treeeditor.TreeInstanceNode;
import com.liuzg.flutteride.def.treeeditor.TreeNode;
import com.liuzg.flutteride.def.treeeditor.TreePropertyInstanceNode;
import com.liuzg.flutteride.editorui.ValueEditorFactory;
import com.liuzg.flutteride.flutter.FlutterRunner;
import com.liuzg.flutteride.def.*;
import com.liuzg.flutteride.models.Design;
import com.liuzg.flutteride.models.Project;
import com.liuzg.flutteride.models.Widget;
import com.liuzg.flutteride.storage.TreeWriter;
import com.liuzg.flutteride.uitls.FxUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class IDEController {
    @FXML
    ChoiceBox<FlutterRunner> choiceRunner;

    @FXML
    TextArea txtOutput;

    @FXML
    TitledPane editorPane;

    @FXML
    ProjectTreeView projectTreeView;

    @FXML
    Accordion accordion;

    @FXML
    TreeEditor treenodeEditor;

    @FXML
    TitledPane defaulttab;

    @FXML
    VBox acc_controls;

    @FXML
    VBox editorspane;


    private ValueEditorFactory editorFactory;
    private DefinitionManager definitionManager;
    private List<FlutterRunner> flutterRunners;
    private Widget selectedProjectWidget;

    private Project currentProject;
    private Widget currentWidget;
    private Scene scene;

    private Instance clipboardInstance;

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
        definitionManager = readDefinitionXml("definition_auto.xml");
        flutterRunners = (List<FlutterRunner>) context.getBean("flutterRunners");

        choiceRunner.setItems(FXCollections.observableArrayList(flutterRunners));
        choiceRunner.getSelectionModel().select(0);

        loadControlToolbox();

//        treenodeEditor.addViewNodeSelectedListener(viewnode -> {
//            onSelectedTreeItemChanged();
//        });
//
//        treenodeEditor.addDataChangedListener(() -> {
//            onWidgetTreeChanged();
//        });

        projectTreeView.addWidgetChangedListener(selectedWidget -> {
            IDEController.this.selectedProjectWidget = selectedWidget;
        });

        for (FlutterRunner flutterRunner: flutterRunners) {
            flutterRunner.addOutputListener(outputstr -> {
                onFlutterOutput(outputstr);
            });
        }

        scene.getWindow().setOnCloseRequest(event -> {
            for (FlutterRunner flutterRunner: flutterRunners) {
                flutterRunner.stopapp();
            }
        });

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode()== KeyCode.UP) {
                if(event.isControlDown()) {
                    treenodeEditor.moveUpSelected();
                }else{
                    treenodeEditor.selectionUp();
                }
            }else if(event.getCode()==KeyCode.DOWN) {
                if(event.isControlDown()) {
                    treenodeEditor.moveDownSelected();
                }else{
                    treenodeEditor.selectionDown();
                }
            }else if(event.getCode()==KeyCode.DELETE) {
                treenodeEditor.removeSelected();
            }else if(event.getCode()==KeyCode.LEFT) {
                if(event.isControlDown()) {
                    treenodeEditor.collapseAllSelected();
                }else{
                    treenodeEditor.collapseSelected();
                }
            }else if(event.getCode()==KeyCode.RIGHT) {
                if(event.isControlDown()) {
                    treenodeEditor.expandAllSelected();
                }else{
                    treenodeEditor.expandSelected();
                }
            }else if(event.getCode()==KeyCode.D) {
                if(event.isControlDown()) {
                    treenodeEditor.duplicateSelected();
                }
            }else if(event.getCode()==KeyCode.C) {
                if(event.isControlDown()) {
                    TreeNode node = treenodeEditor.getSelectedTreeNode();
                    if(node instanceof TreeInstanceNode) {
                        TreeInstanceNode instancenode = (TreeInstanceNode) node;
                        clipboardInstance =  instancenode.getConstructorInstance();
                    }else if(node instanceof TreePropertyInstanceNode) {
                        TreePropertyInstanceNode propertyInstanceNode = (TreePropertyInstanceNode) node;
                        clipboardInstance =  propertyInstanceNode.getValueInstance();
                    }
                }
            }else if(event.getCode()==KeyCode.X) {
                if(event.isControlDown()) {
                    TreeNode node = treenodeEditor.getSelectedTreeNode();
                    if(node instanceof TreeInstanceNode) {
                        TreeInstanceNode instancenode = (TreeInstanceNode) node;
                        clipboardInstance =  instancenode.getConstructorInstance();
                        treenodeEditor.removeSelected();
                    }else if(node instanceof TreePropertyInstanceNode) {
                        TreePropertyInstanceNode propertyInstanceNode = (TreePropertyInstanceNode) node;
                        clipboardInstance =  propertyInstanceNode.getValueInstance();
                        treenodeEditor.removeSelected();
                    }
                }
            }else if(event.getCode()==KeyCode.V) {
                if(event.isControlDown()) {
                    TreeNode node = treenodeEditor.getSelectedTreeNode();
                    if(clipboardInstance!=null) {
                        Instance newinstance = DefUtils.getCopyObj(clipboardInstance);
                        treenodeEditor.addInstance(node, newinstance);
                        treenodeEditor.renderUI();
                    }
                }
            }
        });
    }



    // methods

    private DefinitionManager readDefinitionXml(String xmlfile)  {
        try{
            SAXReader reader = new SAXReader();
            Document document = reader.read(new File(xmlfile));
            Element root = document.getRootElement();

            List<Element> elementList = new ArrayList<>();
            for(Object subelemobj: root.elements()) {
                Element subelem = (Element) subelemobj;
                elementList.add(subelem);
            }

            DefinitionReader definitionReader = new Dom4JDefinitionReader(elementList);
            definitionReader.read();
            List<TypeDefinition> definitions = definitionReader.getDefinitions();
            DefinitionManager manager = new DefinitionManagerImpl(definitions);
            return manager;
        }catch (Exception e){
        }
        return null;
    }


    private void loadControlToolbox() {
        acc_controls.getChildren().clear();

        List<TypeDefinition> definitions = definitionManager.getTypes();

        for (TypeDefinition definition : definitions.stream()
                .sorted(Comparator.comparing(TypeDefinition::getTypeName))
                .filter(typeDefinition -> typeDefinition instanceof ConstructorDefinition)
                .collect(Collectors.toList())) {
            AnchorPane pane = new AnchorPane();
            Button btn = new Button();
            btn.setText(definition.getTypeName());
            btn.setOnAction(event -> {
                TreeNode selectednode = treenodeEditor.getSelectedTreeNode();
                if (selectednode != null) {
                    ConstructorInstance newinstance = new ConstructorInstance(definition);
                    treenodeEditor.addInstance(selectednode, newinstance);
                    onWidgetTreeChanged();
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
        if(currentProject==null) return;
        FlutterRunner flutterRunner = choiceRunner.getSelectionModel().getSelectedItem();
        flutterRunner.setProjectPath(currentProject.getProjectPath());
        flutterRunner.startapp();
    }

    public void onHotRestartClick(ActionEvent actionEvent) {
        if(currentProject==null) return;
        FlutterRunner flutterRunner = choiceRunner.getSelectionModel().getSelectedItem();
        flutterRunner.hotRestart();
    }

    public void onHotReloadClick(ActionEvent actionEvent) {
        if(currentProject==null) return;
        FlutterRunner flutterRunner = choiceRunner.getSelectionModel().getSelectedItem();
        flutterRunner.hotload();
    }

    public void onStopClick(ActionEvent actionEvent) {
        if(currentProject==null) return;
        FlutterRunner flutterRunner = choiceRunner.getSelectionModel().getSelectedItem();
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
                if(treenodeEditor.getRootInstance()!=null) {
                    if(treenodeEditor.isDirty()){
                        selectedProjectWidget.getDesign().setDirty(true);
                    }
                }
                editorPane.setText(String.format("Editor: %s(%s)", selectedProjectWidget.getName(),selectedProjectWidget.getDesign().getRelativePath()));

                this.currentWidget = selectedProjectWidget;
                treenodeEditor.setRootIntance(selectedProjectWidget.getInstance());
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
            currentProject.setTreeNodeDefinitionManager(definitionManager);
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
                    Widget widget =design.getWidget();
                    // TODO: SAVE
                    TreeWriter treeWriter = new TreeWriter();
                    Element elem = treeWriter.widgetXml(widget);
                    widgetselems.add(elem);

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
//        ViewNode item = treenodeEditor.getSelectedNode();
//        if (prevselectedtreeitem == item) return;
//        prevselectedtreeitem = item;
//        if (item != null) {
//            TreeNode treenode = item.getTreeNode();
//
//            if (item.getType() == ViewNodeType.NODES_PROPERTY) {
//                editorspane.getChildren().clear();
//                return;
//            }
//            editorspane.getChildren().clear();
//            if (treenode != null) {
//                Map<String, String> proptypemap = treenode.getDefination().getPropertyTypeMap();
//                for (Map.Entry<String, String> entry : proptypemap.entrySet()) {
//                    String prop = entry.getKey();
//                    String type = entry.getValue();
//
//                    if (!treenode.getDefination().getNodeProperties().contains(prop) &&
//                            !treenode.getDefination().getNodesProperties().contains(prop)) {
//                        ValueEditor editor = editorFactory.createValueEditor(type);
//                        if (editor != null) {
//                            editor.setLabel(prop + ":");
//                            editor.setValue(treenode.getProperty(prop));
//                            editor.addListener((curEditor, newValue) -> {
//                                treenode.setProperty(prop, newValue);
//                                onWidgetTreeChanged();
//                                treenodeEditor.refresh();
//                            });
//                            editorspane.getChildren().add((Node) editor);
//                        }
//                    }
//                }
//            }
//        }
    }

    private void onWidgetTreeChanged() {
        currentWidget.getDesign().setDirty(true);
        projectTreeView.refreshView();
    }

    public void onCopyClick(ActionEvent actionEvent) {
    }

    public void onCutClick(ActionEvent actionEvent) {
    }

    public void onPasteClick(ActionEvent actionEvent) {
    }

    public void onDuplicateClick(ActionEvent actionEvent) {
        treenodeEditor.duplicateSelected();
    }

    public void onMoveDownClick(ActionEvent actionEvent) {
        treenodeEditor.moveDownSelected();
    }

    public void onMoveUpClick(ActionEvent actionEvent) {
        treenodeEditor.moveUpSelected();
    }

    public void onCollapseClick(ActionEvent actionEvent) {
        treenodeEditor.collapseAllSelected();
    }

    public void onExpandClick(ActionEvent actionEvent) {
        treenodeEditor.expandAllSelected();
    }

    public void onDeleteClick(ActionEvent actionEvent) {
        treenodeEditor.removeSelected();
    }

}
