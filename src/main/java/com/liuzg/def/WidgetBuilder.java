package com.liuzg.def;

import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class WidgetBuilder {

    private DefinitionManager definitionManager;

    public WidgetBuilder(DefinitionManager definitionManager) {
        this.definitionManager = definitionManager;
    }


    public Instance buildWidgetCode(Element widgetroot) {
        String widgettypename = widgetroot.getName();
        TypeDefinition widgetDef = definitionManager.getTypeByName(widgettypename);
        if(widgetDef instanceof ConstructorDefinition) {
            ConstructorDefinition definition = (ConstructorDefinition) widgetDef;
            ConstructorInstance constructureInstance = new ConstructorInstance(definition);
            for(ConstructorDefinition.ConstructorParam constructorParam: definition.parameters){
                if(widgetroot.attribute(constructorParam.paramname)!=null) {
                    TypeDefinition t =  definitionManager.getTypeByName(constructorParam.paramtypename);
                    if(t instanceof EnumDefinition) {
                        EnumDefinition tdefinition = (EnumDefinition) t;
                        EnumDefinition.EnumItem v = tdefinition.parse(widgetroot.attributeValue(constructorParam.paramname));
                        EnumInstance instance = new EnumInstance(tdefinition, v);
                        constructureInstance.setProperty(constructorParam.paramname, instance);
                    }else{
                        constructureInstance.setProperty(constructorParam.paramname, widgetroot.attributeValue(constructorParam.paramname));
                    }
                }else if(widgetroot.element(constructorParam.paramname)!=null) {
                    Element propelem = widgetroot.element(constructorParam.paramname);
                    if(constructorParam.iscollection) {
                        List<Instance> subvalues = new ArrayList<>();
                        for(Object subelemobj: propelem.elements()){
                            Element subelem = (Element) subelemobj;
                            Instance subvalueinstance = buildWidgetCode(subelem);
                            subvalues.add(subvalueinstance);
                        }
                        constructureInstance.setProperty(constructorParam.paramname, subvalues);
                    }else {
                        if(propelem.elements().stream().findFirst().isPresent()){
                            Instance valueinstance = buildWidgetCode((Element) propelem.elements().stream().findFirst().get());
                            constructureInstance.setProperty(constructorParam.paramname, valueinstance);
                        }
                    }
                }
            }
            Element decoratorrootelem = widgetroot.element("decorateby");
            if(decoratorrootelem!=null) {
                for(Object decoratorelemobj: decoratorrootelem.elements()){
                    Element decoratorelem = (Element) decoratorelemobj;
                    Instance decoratorInstance = buildWidgetCode(decoratorelem);
                    constructureInstance.addDecorator((ConstructorInstance) decoratorInstance);
                }
            }
            return constructureInstance;
        }
        return null;
    }
}
