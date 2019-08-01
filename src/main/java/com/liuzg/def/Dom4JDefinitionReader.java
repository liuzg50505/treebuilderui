package com.liuzg.def;

import org.dom4j.Element;

import java.util.List;

public class Dom4JDefinitionReader extends DefinitionReader {
    private List<Element> definitionelems;

    public Dom4JDefinitionReader(List<Element> definitionelems) {
        this.definitionelems = definitionelems;
    }


    @Override
    public void read() {
        for(Element element: definitionelems) {
            if("Enum".equals(element.getName())){
                String type = element.attributeValue("type");
                EnumDefinition enumDefinition = new EnumDefinition();
                enumDefinition.setTypeName(type);
                for(Object itemElemobj: element.elements()) {
                    Element itemElem = (Element) itemElemobj;
                    String key = itemElem.attributeValue("key");
                    String code = itemElem.attributeValue("code");
                    String description = itemElem.attributeValue("description");
                    EnumDefinition.EnumItem enumItem = new EnumDefinition.EnumItem();
                    enumItem.key = key;
                    enumItem.code = code;
                    enumItem.description = description;
                    enumDefinition.addEnumItem(enumItem);
                }
                definitions.add(enumDefinition);
            }else if("Class".equals(element.getName())) {
                String type = element.attributeValue("type");
                String constructorname = element.attributeValue("constructorname");
                String decoratorproperty = element.attributeValue("decoratorproperty");

                ConstructorDefinition constructorDefinition = new ConstructorDefinition();
                constructorDefinition.setTypeName(type);
                constructorDefinition.setConstructorName(constructorname);
                constructorDefinition.setDecoratorProperty(decoratorproperty);
                for(Object itemElemobj: element.elements()) {
                    Element itemElem = (Element) itemElemobj;
                    String name = itemElem.attributeValue("name");
                    String paramtype = itemElem.attributeValue("type");
                    String parametertype = itemElem.attributeValue("parametertype");
                    String collection = itemElem.attributeValue("collection");
                    String description = itemElem.attributeValue("description");
                    String defaultvalue = itemElem.attributeValue("defaultvalue");

                    ConstructorDefinition.ConstructorParam param = new ConstructorDefinition.ConstructorParam();
                    param.paramname = name;
                    param.paramtypename = paramtype;
                    param.description = description;
                    param.description = defaultvalue;
                    param.iscollection = collection!=null && collection.toLowerCase().equals("true");
                    param.parameterType = "".equals(parametertype)||parametertype==null? ConstructorDefinition.ConstructorParamType.OptionalNamedParameter: ConstructorDefinition.ConstructorParamType.valueOf(parametertype);
                    constructorDefinition.addParameter(param);
                }
                definitions.add(constructorDefinition);
            }else if("Template".equals(element.getName())){
                String type = element.attributeValue("type");
                String iscollectionstr = element.attributeValue("iscollection");

                TemplateDefinition templateDefinition = new TemplateDefinition();
                templateDefinition.setTypeName(type);
                templateDefinition.setCollection("true".equals(iscollectionstr));
                for(Object itemElemobj: element.elements()) {
                    Element itemElem = (Element) itemElemobj;
                    if(itemElem.getName().equals("property")){
                        String name = itemElem.attributeValue("name");
                        String paramtype = itemElem.attributeValue("type");
                        String parametertype = itemElem.attributeValue("parametertype");
                        String collection = itemElem.attributeValue("collection");
                        String description = itemElem.attributeValue("description");
                        String defaultvalue = itemElem.attributeValue("defaultvalue");

                        ConstructorDefinition.ConstructorParam param = new ConstructorDefinition.ConstructorParam();
                        param.paramname = name;
                        param.paramtypename = paramtype;
                        param.description = description;
                        param.description = defaultvalue;
                        param.iscollection = collection!=null && collection.toLowerCase().equals("true");
                        param.parameterType = "".equals(parametertype)||parametertype==null? ConstructorDefinition.ConstructorParamType.OptionalNamedParameter: ConstructorDefinition.ConstructorParamType.valueOf(parametertype);
                        templateDefinition.addParameter(param);
                    }else if(itemElem.getName().equals("template")){
                        templateDefinition.setTemplateString(itemElem.getText().trim());
                    }
                }
                definitions.add(templateDefinition);
            }
        }
    }
}
