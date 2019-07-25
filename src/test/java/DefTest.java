import com.liuzg.def.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefTest {
    @Test
    public void test1() throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File("definition.xml"));
        Element root = document.getRootElement();

        List<Element> elementList = new ArrayList<>();
        for(Object subelemobj: root.elements()) {
            Element subelem = (Element) subelemobj;
            elementList.add(subelem);
        }

        DefinitionReader definitionReader = new Dom4JDefinitionReader(elementList);
        definitionReader.read();
        System.out.println(definitionReader);

    }

    @Test
    public void test2() throws DocumentException {
        // read definitions
        DefinitionManager manager = readDefinitionXml("definition.xml");

        // test start
        // build widget tree
        EnumDefinition enumDefinition = (EnumDefinition) manager.getTypeByName("MainAxisAlignment");

        ConstructorInstance column = new ConstructorInstance(manager.getTypeByName("Column"));
        ConstructorInstance column2 = new ConstructorInstance(manager.getTypeByName("Column"));
        ConstructorInstance column3 = new ConstructorInstance(manager.getTypeByName("Column"));
        ConstructorInstance column4 = new ConstructorInstance(manager.getTypeByName("Column"));
        column.setProperty("children", Arrays.asList(
                column2, column3
        ));
        column2.setProperty("children", Arrays.asList(column4));
        EnumInstance mainAxis = new EnumInstance(manager.getTypeByName("Column"), enumDefinition.parse("baseline"));
        column.setProperty("mainAxis", mainAxis);

        // generate code
        System.out.println(column.generateCode());

    }

    private DefinitionManager readDefinitionXml(String xmlfile) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File("definition.xml"));
        Element root = document.getRootElement();

        List<Element> elementList = new ArrayList<>();
        for(Object subelemobj: root.elements()) {
            Element subelem = (Element) subelemobj;
            elementList.add(subelem);
        }

        DefinitionReader definitionReader = new Dom4JDefinitionReader(elementList);
        definitionReader.read();
        List<TypeDefinition> definitions = definitionReader.getDefinitions();
        DefinitionManager manager = new DefinitionManagerImpl(definitions);
        return manager;
    }


    @Test
    public void test3() throws DocumentException {
        // read definitions
        DefinitionManager definitionManager = readDefinitionXml("definition.xml");

        SAXReader reader = new SAXReader();
        Document document = reader.read(new File("design.xml"));
        Element root = document.getRootElement();
        Element widgetroot = (Element) root.elements().get(0);

        WidgetBuilder widgetBuilder = new WidgetBuilder(definitionManager);
        Instance instance = widgetBuilder.buildWidgetCode(widgetroot);
        System.out.println(instance.generateCode());

    }
}
