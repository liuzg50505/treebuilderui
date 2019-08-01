package com.liuzg.flutteride.models;

import com.liuzg.flutteride.def.Instance;

public class Widget {
    private Instance instance;
    private String name;
    private Design design;

    public Widget(Instance instance, String name) {
        this.instance = instance;
        this.name = name;
    }

    public Widget(Instance instance, String name, Design design) {
        this.instance = instance;
        this.name = name;
        this.design = design;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Design getDesign() {
        return design;
    }

    public void setDesign(Design design) {
        this.design = design;
    }
}
