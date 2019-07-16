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
            }else if("Widget".equals(element.getName())) {
                String type = element.attributeValue("type");

                WidgetDefinition widgetDefinition = new WidgetDefinition();
                widgetDefinition.setTypeName(type);
                for(Object itemElemobj: element.elements()) {
                    Element itemElem = (Element) itemElemobj;
                    String name = itemElem.attributeValue("name");
                    String paramtype = itemElem.attributeValue("type");
                    String parametertype = itemElem.attributeValue("parametertype");
                    String collection = itemElem.attributeValue("collection");
                    String description = itemElem.attributeValue("description");
                    String defaultvalue = itemElem.attributeValue("defaultvalue");

                    WidgetDefinition.ConstructorParam param = new WidgetDefinition.ConstructorParam();
                    param.paramname = name;
                    param.paramtypename = paramtype;
                    param.description = description;
                    param.description = defaultvalue;
                    param.iscollection = collection!=null && collection.toLowerCase().equals("true");
                    param.parameterType = "".equals(parametertype)||parametertype==null? WidgetDefinition.ConstructorParamType.OptionalNamedParameter: WidgetDefinition.ConstructorParamType.valueOf(parametertype);
                    widgetDefinition.addParameter(param);
                }

                definitions.add(widgetDefinition);
            }
        }
    }
}
