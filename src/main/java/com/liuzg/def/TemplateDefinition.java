package com.liuzg.def;

public class TemplateDefinition extends ConstructorDefinition {


    protected String templateString = "";
    protected boolean isCollection = false;

    public TemplateDefinition() {
    }


    public String getTemplateString() {
        return templateString;
    }

    public void setTemplateString(String templateString) {
        this.templateString = templateString;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }
}
