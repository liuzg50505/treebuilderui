package com.liuzg.flutter;

public abstract class FlutterRunner {
    protected String projectPath;

    public FlutterRunner() {
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public abstract void startapp();

    public abstract void hotload();

    public abstract void hotRestart();

    public abstract void stopapp();
}
