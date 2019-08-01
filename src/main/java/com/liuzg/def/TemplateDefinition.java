package com.liuzg.def;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TemplateDefinition extends TypeDefinition {

    public static enum TemplateParamType{
        OptionalNamedParameter,
        RequiredParameter,
        OptionalPositionParameter,
    }

    public static class TemplateParam implements Serializable {
        public String paramname;
        public String paramtypename;
        public TemplateParamType parameterType = TemplateParamType.OptionalNamedParameter;
        public boolean iscollection = false;
        public String description;
        public String defaultvalue;
    }

    protected String templateString = "";
    protected List<TemplateParam> parameters = new ArrayList<>();

    public TemplateDefinition() {
    }


    public List<TemplateParam> getParameters() {
        return parameters;
    }

    public void setParameters(List<TemplateParam> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(TemplateParam param) {
        parameters.add(param);
    }

    public boolean containsParameter(String paramname) {
        for (TemplateParam param: parameters) {
            if (Objects.equals(param.paramname, paramname)){
                return true;
            }
        }
        return false;
    }

    public String getTemplateString() {
        return templateString;
    }

    public void setTemplateString(String templateString) {
        this.templateString = templateString;
    }
}
