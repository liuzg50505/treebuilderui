package com.liuzg.def;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class WidgetInstance extends Instance{

    protected Map<String, Object> propertyValueMap;

    public WidgetInstance(TypeDefinition typeDefinition) {
        super(typeDefinition);
        propertyValueMap = new HashMap<>();
    }

    public void setProperty(String property, Object value) {
        propertyValueMap.put(property, value);
    }

    public Object getProperty(String property) {
        return propertyValueMap.get(property);
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
                itemcodes.add(itemcode);
            }
            String code = String.format("[\n%s\n]",  String.join(", \n", itemcodes));
            return indentCode(code, 1).trim();
        } else {
            return indentCode(simpleTypeValue(value), 1);
        }
    }

    @Override
    public String generateCode() {
        WidgetDefinition classDefination = (WidgetDefinition) typeDefinition;
        String method = "";
        if(classDefination.constructorName==null||"".equals(classDefination.constructorName)) method = classDefination.typeName;
        else method = String.format("%s.%s", classDefination.typeName, classDefination.constructorName);
        List<String> r = new ArrayList<>();
        for (WidgetDefinition.ConstructorParam param: classDefination.parameters) {
            Object value = getProperty(param.paramname);
            if(param.parameterType == WidgetDefinition.ConstructorParamType.RequiredParameter||
                    param.parameterType == WidgetDefinition.ConstructorParamType.OptionalPositionParameter) {
                if(value==null&&param.parameterType == WidgetDefinition.ConstructorParamType.RequiredParameter) {
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
}
