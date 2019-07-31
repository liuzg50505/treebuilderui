package com.liuzg.storage;

import com.liuzg.def.DefinitionManager;
import com.liuzg.def.Instance;
import com.liuzg.def.WidgetBuilder;
import com.liuzg.models.Design;
import com.liuzg.models.Widget;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

public class TreeReader {
    List<Widget> widgets;
    DefinitionManager definitionManager;

    public TreeReader(DefinitionManager definitionManager) {
        widgets = new ArrayList<>();
        this.definitionManager = definitionManager;
    }

    public Widget readFile(String xmlfilepath) {
        try{
            SAXReader reader = new SAXReader();
            Document document = reader.read(new File(xmlfilepath));
            Element root = document.getRootElement();
            WidgetBuilder widgetBuilder = new WidgetBuilder(definitionManager);
            Instance instance = widgetBuilder.buildWidgetCode((Element) root.elements().get(0));
            if(instance==null) return null;
            Widget widget = new Widget(instance, root.attributeValue("name"));
            return widget;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Widget> getWidgets() {
        return new ArrayList<>(widgets);
    }

    private Instance parseXml(Element treenodelem) {
        WidgetBuilder widgetBuilder = new WidgetBuilder(definitionManager);
        Instance instance = widgetBuilder.buildWidgetCode(treenodelem);
        return instance;

    }

    private Object getElemPropertyValue(Element elem, String propertyName) {
        Attribute attr = elem.attribute(propertyName);
        if(attr==null) {
            Element subelem = elem.element(propertyName);
            if(subelem==null) {
                return null;
            }
            return subelem.getText();
        }else{
            return attr.getValue();
        }
    }

}
