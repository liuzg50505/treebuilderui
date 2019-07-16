package com.liuzg.def;


import java.util.ArrayList;
import java.util.List;

public class WidgetDefinition extends TypeDefinition {
    public static enum ConstructorParamType{
        OptionalNamedParameter,
        RequiredParameter,
        OptionalPositionParameter,
    }

    public static class ConstructorParam {
        public String paramname;
        public String paramtypename;
        public ConstructorParamType parameterType = ConstructorParamType.OptionalNamedParameter;
        public boolean iscollection = false;
        public String description;
        public String defaultvalue;
    }

    protected String constructorName = "";
    protected List<ConstructorParam> parameters = new ArrayList<>();

    public String getConstructorName() {
        return constructorName;
    }

    public void setConstructorName(String constructorName) {
        this.constructorName = constructorName;
    }

    public List<ConstructorParam> getParameters() {
        return parameters;
    }

    public void setParameters(List<ConstructorParam> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(ConstructorParam param) {
        parameters.add(param);
    }
}
