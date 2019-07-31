package com.liuzg.def;

import com.liuzg.ui.IDEController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MyTreeEditorLauncher extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    private DefinitionManager readDefinitionXml(String xmlfile) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = reader.read(new File(xmlfile));
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

    @Override
    public void start(Stage primaryStage) throws Exception {

        DefinitionManager definitionManager = readDefinitionXml("definition_auto.xml");

        SAXReader reader = new SAXReader();
        Document document = reader.read(new File("design.xml"));
        Element root = document.getRootElement();
        Element widgetroot = (Element) root.elements().get(0);

        WidgetBuilder widgetBuilder = new WidgetBuilder(definitionManager);
        Instance instance = widgetBuilder.buildWidgetCode(widgetroot);
        System.out.println(instance.generateCode());

        BorderPane borderPane = new BorderPane();
        MyTreeEditor editor = new MyTreeEditor(instance);
        borderPane.setCenter(editor);
        Button btn1 = new Button("generate");
        btn1.setOnAction(event -> {
            System.out.println(instance.generateCode());
        });
        Button btn2 = new Button("refresh tree");
        btn2.setOnAction(event -> {
            editor.renderUI();
        });

        borderPane.setTop(new HBox(btn1, btn2));

        Scene scene = new Scene(borderPane, 1400, 875);


        primaryStage.setTitle("My Tree Editor Demo");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
