package com.liuzg.flutteride;

import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class TestDrag extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Label label1 = new Label("label1");
        Label label2 = new Label("label2");
        Label label3 = new Label("label3");
        HBox hBox = new HBox(label1, label2, label3);
        label1.setOnDragDetected(event -> {
            Dragboard dragboard = label1.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(label1.getText());
            dragboard.setContent(content);
        });
        hBox.setOnDragEntered(event -> {
            label1.setTextFill(Color.RED);
        });
        hBox.setOnDragExited(event -> {
            label1.setTextFill(Color.BLACK);
        });
        hBox.setOnDragOver(event -> {
                event.acceptTransferModes(TransferMode.MOVE);
        });
        hBox.setOnDragDropped(event -> {
            double x = event.getSceneX();
            Node dragitem = label1;
            System.out.println(x);
            Node targetelem = null;
            double minx = 0;
            double maxx = 0;
            for (int i=0;i<hBox.getChildren().size();i++) {
                Node child = hBox.getChildren().get(i);
                Bounds bounds = child.getLayoutBounds();
                Bounds t = child.localToParent(bounds);
                if(i==0) {
                    minx = t.getMinX();
                }
                if(i==hBox.getChildren().size()-1) {
                    maxx = t.getMaxX();
                }
                if(x>=t.getMinX()&&x<=t.getMaxX()){
                    targetelem = child;
                    break;
                }
            }
            if(targetelem!=null) {
                hBox.getChildren().remove(dragitem);
                int idx = hBox.getChildren().indexOf(targetelem);
                hBox.getChildren().add(idx, dragitem);
            }else{
                if(x<minx) {
                    hBox.getChildren().remove(dragitem);
                    hBox.getChildren().add(0, dragitem);
                }else if(x>maxx){
                    hBox.getChildren().remove(dragitem);
                    hBox.getChildren().add(dragitem);
                }
            }
        });

        Scene scene = new Scene(hBox, 200, 30);
        primaryStage.setTitle("My Tree Editor Demo");
        primaryStage.setScene(scene);
        primaryStage.show();

    }


}
