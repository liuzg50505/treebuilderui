package com.liuzg.flutteride.def;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class ConstructorInstance extends Instance{

    protected Map<String, Object> propertyValueMap;
    protected List<ConstructorInstance> decorators;

    public ConstructorInstance(TypeDefinition typeDefinition) {
        super(typeDefinition);
        propertyValueMap = new HashMap<>();
        decorators = new ArrayList<>();
    }

    public void setProperty(String property, Object value) {
        propertyValueMap.put(property, value);
    }

    public Object getProperty(String property) {
        return propertyValueMap.get(property);
    }

    public void addDecorator(ConstructorInstance decorator) {
        decorators.add(decorator);
    }

    public List<ConstructorInstance> getDecorators() {
        return new ArrayList<>(decorators);
    }

    public void setDecorators(List<ConstructorInstance> decorators) {
        this.decorators.clear();
        this.decorators.addAll(decorators);
    }

    private String simpleTypeValue(Object value) {
        if(value==null) return "null";
        if(value instanceof String) {
            return String.format("\"%s\"", value);
        }
        return value.toString();
    }

    private String indentSpaces(int indentLevel){
        StringBuilder sb =new StringBuilder();
        for(int i=0;i<indentLevel;i++) {
            sb.append("  ");
        }
        return sb.toString();
    }

    private String indentCode(String code, int indentLevel) {
        BufferedReader br = new BufferedReader(new StringReader(code));
        List<String> lines = new ArrayList<>();
        try {
            String line = br.readLine();
            while (line!=null) {
                lines.add(indentSpaces(indentLevel)+line);
                line = br.readLine();
            }
        } catch (IOException e) {
        }
        return String.join("\n", lines);
    }

    private String generateParamCode(Object value) {
        if(value instanceof Instance) {
            Instance pinstance = (Instance) value;
            return indentCode(pinstance.generateCode(), 1);
        } else if(value instanceof Collection) {
            Collection collection = (Collection) value;
            List<String> itemcodes = new ArrayList<>();
            for(Object item: collection) {
                String itemcode = generateParamCode(item);
                if(item instanceof TemplateInstance) {
                    TemplateInstance templateInstance = (TemplateInstance) item;
                    TemplateDefinition templateDefinition = (TemplateDefinition) templateInstance.typeDefinition;
                    if(templateDefinition.isCollection()){
                        itemcodes.add("  ...?"+itemcode.trim());
                    }else{
                        itemcodes.add(itemcode);
                    }
                }else{
                    itemcodes.add(itemcode);
                }
            }
            String code = String.format("[\n%s\n]",  String.join(", \n", itemcodes));
            return indentCode(code, 1).trim();
        } else {
            return indentCode(simpleTypeValue(value), 1);
        }
    }

    private String genrateConstructorInstanceCode(ConstructorInstance decoratorInstance) {
        ConstructorDefinition classDefination = (ConstructorDefinition) decoratorInstance.getTypeDefinition();
        if(decoratorInstance==this&&!classDefination.isDecoratorConstructor()){
            return generateCodeWithoutDecorators();
        }

        String method = "";
        if(classDefination.constructorName==null||"".equals(classDefination.constructorName)) method = classDefination.typeName;
        else method = String.format("%s.%s", classDefination.typeName, classDefination.constructorName);
        List<String> r = new ArrayList<>();
        for (ConstructorDefinition.ConstructorParam param: classDefination.parameters) {
            Object value = decoratorInstance.getProperty(param.paramname);
            if(param.parameterType == ConstructorDefinition.ConstructorParamType.RequiredParameter||
                    param.parameterType == ConstructorDefinition.ConstructorParamType.OptionalPositionParameter) {
                if(value==null&&param.parameterType == ConstructorDefinition.ConstructorParamType.RequiredParameter) {
                    value = param.defaultvalue;
                }
                r.add(generateParamCode(value).trim());
            }else {
                if(value!=null){
                    r.add(String.format("%s: %s", param.paramname, generateParamCode(value).trim()));
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(method);
        if(r.size()>0) sb.append("(\n");
        else sb.append("(");
        for (int i=0;i<r.size();i++) {
            String item = r.get(i);
            sb.append(indentSpaces(1));
            sb.append(item);
            if(i!=r.size()-1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append(")");
        return sb.toString();
    }

    private String generateCodeWithoutDecorators() {
        ConstructorDefinition classDefination = (ConstructorDefinition) this.getTypeDefinition();
        String method = "";
        if(classDefination.constructorName==null||"".equals(classDefination.constructorName)) method = classDefination.typeName;
        else method = String.format("%s.%s", classDefination.typeName, classDefination.constructorName);
        List<String> r = new ArrayList<>();
        for (ConstructorDefinition.ConstructorParam param: classDefination.parameters) {
            Object value = this.getProperty(param.paramname);
            if(param.parameterType == ConstructorDefinition.ConstructorParamType.RequiredParameter||
                    param.parameterType == ConstructorDefinition.ConstructorParamType.OptionalPositionParameter) {
                if(value==null&&param.parameterType == ConstructorDefinition.ConstructorParamType.RequiredParameter) {
                    value = param.defaultvalue;
                }
                r.add(generateParamCode(value).trim());
            }else {
                if(value!=null){
                    r.add(String.format("%s: %s", param.paramname, generateParamCode(value).trim()));
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(method);
        if(r.size()>0) sb.append("(\n");
        else sb.append("(");
        for (int i=0;i<r.size();i++) {
            String item = r.get(i);
            sb.append(indentSpaces(1));
            sb.append(item);
            if(i!=r.size()-1) {
                sb.append(",");
            }
            sb.append("\n");
        }
        sb.append(")");
        return sb.toString();

    }

    @Override
    public String generateCode() {
        if(decorators.size()>0) {
            ConstructorInstance cur = decorators.get(0);
            for (int i=1; i< decorators.size();i++) {
                ConstructorDefinition constructorDefinition = (ConstructorDefinition) cur.typeDefinition;
                String decoratorProperty = constructorDefinition.getDecoratorProperty();
                cur.setProperty(decoratorProperty, decorators.get(i));
                cur = decorators.get(i);
            }

            ConstructorInstance thisclone = new ConstructorInstance(this.typeDefinition);
            thisclone.propertyValueMap = this.propertyValueMap;

            ConstructorDefinition constructorDefinition = (ConstructorDefinition) cur.typeDefinition;
            String decoratorProperty = constructorDefinition.getDecoratorProperty();
            cur.setProperty(decoratorProperty, thisclone);

            return genrateConstructorInstanceCode(decorators.get(0));
        }else{
            return genrateConstructorInstanceCode(this);
        }
    }
}
