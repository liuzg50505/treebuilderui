package com.liuzg.def;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class TemplateInstance extends Instance {

    protected Map<String, Object> propertyValueMap;

    public TemplateInstance(TypeDefinition typeDefinition) {
        super(typeDefinition);
        propertyValueMap = new HashMap<>();

    }

    public void setProperty(String property, Object value) {
        propertyValueMap.put(property, value);
    }

    public Object getProperty(String property) {
        return propertyValueMap.get(property);
    }

    @Override
    public String generateCode() {
        TemplateDefinition templateDefinition = (TemplateDefinition) typeDefinition;
        String templatestr = templateDefinition.getTemplateString();
        try {
            Template template = new Template("strTpl", templatestr, new Configuration(new Version("2.3.23")));
            StringWriter result = new StringWriter();
            template.process(propertyValueMap, result);
            return result.toString();
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        return "#ERROR#";
    }
}
