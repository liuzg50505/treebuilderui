package com.liuzg.flutteride.ui;

import com.liuzg.flutteride.models.Design;
import com.liuzg.flutteride.models.Project;
import com.liuzg.flutteride.models.Widget;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
        public boolean isDirty;

        @Override
        public String toString() {
            String result = "";
            if(nodeType==NodeType.DESIGN) {
                result = design.getRelativePath();
            }else if(nodeType==NodeType.WIDGET) {
                result = widget.getName();
            }else if(nodeType==NodeType.ROOT) {
                result = projectName;
            }
            if(isDirty) result+="*";
            return result;
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
            designNode.isDirty = design.isDirty();
            TreeItem<Node> designItem = new TreeItem<>(designNode);
            rootItem.getChildren().add(designItem);

            Widget widget = design.getWidget();
            Node widgetNode = new Node();
            widgetNode.nodeType = NodeType.WIDGET;
            widgetNode.widget = widget;
            TreeItem<Node> widgetItem = new TreeItem<>(widgetNode);
            designItem.getChildren().add(widgetItem);
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
