package com.liuzg.ui;

import com.liuzg.models.Project;
import com.liuzg.treenodes.TreeNodeDefinitionManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        URL location = getClass().getResource("/ui/ideui.fxml");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(location);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        Parent root = loader.load();
        Scene scene = new Scene(root, 1400, 875);
//        scene.setUserAgentStylesheet("/css/ide.css");

        IDEController controller = loader.getController();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(scene);
        controller.setScene(scene);
        controller.init();
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException {
//        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:treenode.xml");
//        TreeNodeDefinitionManager definationManager = (TreeNodeDefinitionManager) context.getBean("definationManager");
//
//        Project project = new Project();
//        project.setTreeNodeDefinitionManager(definationManager);
//        project.setProjectPath("/Volumes/macdata/MyProjects/treebuildermvcprj");
//        project.scanFile();
        launch(args);
    }
}
