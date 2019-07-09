package com.liuzg.ui;

import com.liuzg.models.Design;
import com.liuzg.models.Project;
import com.liuzg.models.Widget;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.FileHandler;

public class ProjectTreeView extends TreeView<ProjectTreeView.Node> {
    public static interface WidgetSelectedListener {
        void onWidgetSelected(Widget widget);
    }

    public static enum NodeType {
        ROOT,
        DESIGN,
        WIDGET
    }

    public static class Node {
        public NodeType nodeType = NodeType.DESIGN;
        public Widget widget;
        public Design design;
        public String projectName;

        @Override
        public String toString() {
            if(nodeType==NodeType.DESIGN) {
                return design.getRelativePath();
            }else if(nodeType==NodeType.WIDGET) {
                return widget.getControllerName();
            }else if(nodeType==NodeType.ROOT) {
                return projectName;
            }
            return "";
        }
    }

    private Project project;
    private List<WidgetSelectedListener> widgetSelectedListeners;

    public ProjectTreeView() {
        widgetSelectedListeners = new ArrayList<>();

        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            for(WidgetSelectedListener listener: widgetSelectedListeners) {
                if(listener!=null) {
                    try{
                        listener.onWidgetSelected(newValue.getValue().widget);
                    }catch (Exception e) {}
                }
            }
        });

    }

    // getters and setters
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    // outlets

    public void addWidgetChangedListener(WidgetSelectedListener listener) {
        if(listener==null) return;
        widgetSelectedListeners.add(listener);
    }

    public void refreshView() {
        project.scanFile();
        List<Design> designs = project.getDesigns();
        designs.sort(Comparator.comparing(Design::getRelativePath));

        Node rootNode = new Node();
        rootNode.nodeType = NodeType.ROOT;
        rootNode.projectName = new File(project.getProjectPath()).getName();
        TreeItem<Node> rootItem = new TreeItem<>(rootNode);

        this.setRoot(rootItem);

        for(Design design: designs) {
            Node designNode = new Node();
            designNode.nodeType = NodeType.DESIGN;
            designNode.design = design;
            TreeItem<Node> designItem = new TreeItem<>(designNode);
            rootItem.getChildren().add(designItem);

            for(Widget widget: design.getWidgets()) {
                Node widgetNode = new Node();
                widgetNode.nodeType = NodeType.WIDGET;
                widgetNode.widget = widget;
                TreeItem<Node> widgetItem = new TreeItem<>(widgetNode);
                designItem.getChildren().add(widgetItem);
            }
        }

        expandTreeView(this.getRoot());
    }

    public Object getSelectedItem() {
        TreeItem<Node> item = getSelectionModel().getSelectedItem();
        if(item!=null) {
            Node node = item.getValue();
            if(node.nodeType==NodeType.DESIGN) return node.design;
            if(node.nodeType==NodeType.WIDGET) return node.widget;
        }
        return null;
    }

    // utils methods

    private static void expandTreeView(TreeItem<?> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(true);
            for (TreeItem<?> child : item.getChildren()) {
                expandTreeView(child);
            }
        }
    }


}
