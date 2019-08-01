package com.liuzg.flutteride.flutter;

public abstract class FlutterRunner {
    public static interface OutputListener{
        void onOutput(String outputstr);
    }

    protected String projectPath;

    public FlutterRunner() {
    }



    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public abstract void addOutputListener(OutputListener listener);

    public abstract String name();

    public abstract void startapp();

    public abstract void hotload();

    public abstract void hotRestart();

    public abstract void stopapp();

    @Override
    public String toString() {
        return name();
    }
}
