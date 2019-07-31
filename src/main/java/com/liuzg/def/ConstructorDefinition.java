package com.liuzg.def;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConstructorDefinition extends TypeDefinition {
    public static enum ConstructorParamType{
        OptionalNamedParameter,
        RequiredParameter,
        OptionalPositionParameter,
    }

    public static class ConstructorParam implements Serializable {
        public String paramname;
        public String paramtypename;
        public ConstructorParamType parameterType = ConstructorParamType.OptionalNamedParameter;
        public boolean iscollection = false;
        public String description;
        public String defaultvalue;
    }

    protected String constructorName = "";
    protected String decoratorProperty = "";
    protected List<ConstructorParam> parameters = new ArrayList<>();

    public String getConstructorName() {
        return constructorName;
    }

    public void setConstructorName(String constructorName) {
        this.constructorName = constructorName;
    }

    public String getDecoratorProperty() {
        return decoratorProperty;
    }

    public void setDecoratorProperty(String decoratorProperty) {
        this.decoratorProperty = decoratorProperty;
    }

    public boolean isDecoratorConstructor() {
        return this.decoratorProperty != null && !"".equals(this.decoratorProperty);
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

    public boolean containsParameter(String paramname) {
        for (ConstructorParam param: parameters) {
            if (Objects.equals(param.paramname, paramname)){
                return true;
            }
        }
        return false;
    }
}
