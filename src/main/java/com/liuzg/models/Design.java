package com.liuzg.models;

import java.util.ArrayList;
import java.util.List;

public class Design {
    private String relativePath;
    private List<Widget> widgets;
    private Project project;
    private boolean dirty;

    public Design() {
        widgets = new ArrayList<>();
        relativePath = "";
    }

    // getters and setters
    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public List<Widget> getWidgets() {
        return widgets;
    }

    public void setWidgets(List<Widget> widgets) {
        this.widgets = widgets;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
