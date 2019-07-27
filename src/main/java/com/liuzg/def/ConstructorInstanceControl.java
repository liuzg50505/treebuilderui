package com.liuzg.def;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ConstructorInstanceControl extends AnchorPane {
    public static interface DecoratorClickHandler {
        void onDecoratorClick(ConstructorInstance decoratorInstance);
    }

    private final BorderPane borderPane;
    private boolean isExpanded = true;
    private boolean isSelected = false;
    private final Image imgExpanded;
    private final Image imgCollapsed;
    private ConstructorInstance instance;
    private int offsetX;
    private HBox hboxDecorators;
    private HBox hbox;
    private Label instanceLabel;
    private ImageView expandIcon;

    private Map<Node, ConstructorInstance> nodeDecoratorMap;
    private Node dragitem;

    private List<DecoratorClickHandler> decoratorClickHandlers;
    private List<MyTreeNodeControlExpandedHandler> expandedHandlers;

    public ConstructorInstanceControl(ConstructorInstance instance, int offsetX) {

        nodeDecoratorMap = new HashMap<>();
        decoratorClickHandlers = new ArrayList<>();
        expandedHandlers = new ArrayList<>();
        this.instance = instance;
        this.offsetX = offsetX;
        imgExpanded = new Image(this.getClass().getResourceAsStream("/assets/treeeditor/expanded.png"), 16,16,false, true);
        imgCollapsed = new Image(this.getClass().getResourceAsStream("/assets/treeeditor/collapsed.png"), 16,16,false, true);
        expandIcon = new ImageView(imgExpanded);
        if(instance==null) instanceLabel = new Label();
        else instanceLabel = new Label(instance.typeDefinition.typeName);
        hboxDecorators = new HBox();
        hboxDecorators.setAlignment(Pos.CENTER_LEFT);
        hboxDecorators.setSpacing(5);
        hbox = new HBox(expandIcon, instanceLabel);
        hbox.setAlignment(Pos.CENTER_LEFT);
        borderPane = new BorderPane();
        borderPane.setLeft(hbox);
        borderPane.setCenter(hboxDecorators);
        this.getChildren().add(borderPane);
        AnchorPane.setLeftAnchor(borderPane, (double) offsetX);
        AnchorPane.setRightAnchor(borderPane, 0.0);
        AnchorPane.setTopAnchor(borderPane, 0.0);
        AnchorPane.setBottomAnchor(borderPane, 0.0);
        expandIcon.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            isExpanded = !isExpanded;
            if(isExpanded) expandIcon.setImage(imgExpanded);
            else expandIcon.setImage(imgCollapsed);

            for (MyTreeNodeControlExpandedHandler expandedHandler: expandedHandlers){
                expandedHandler.onTreeNodeControlExpandedChanged(isExpanded);
            }
            event.consume();
        });

        nodeDecoratorMap.clear();
        dragitem = null;
        if(instance!=null) {
            for (ConstructorInstance decorator: instance.decorators) {
                DecoratorLabel decoratorLabel = new DecoratorLabel(decorator.typeDefinition.typeName);
                if(instance.decorators.indexOf(decorator)==0) {
                    decoratorLabel.setColor(Color.RED);
                }

                nodeDecoratorMap.put(decoratorLabel, decorator);
                decoratorLabel.setOnDragDetected(event -> {
                    Dragboard dragboard = decoratorLabel.startDragAndDrop(TransferMode.ANY);
                    dragboard.setDragView(decoratorLabel.snapshot(null, null));

                    ClipboardContent content = new ClipboardContent();
                    content.putString(decoratorLabel.getText());
                    dragboard.setContent(content);
                    dragitem = decoratorLabel;
                });

                hboxDecorators.getChildren().add(decoratorLabel);
                HBox.setMargin(decoratorLabel, new Insets(0,0,0,15));
                decoratorLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    for(Node node: hboxDecorators.getChildren()) {
                        DecoratorLabel label = (DecoratorLabel) node;
                        label.setColor(Color.TRANSPARENT);
                    }
                    decoratorLabel.setColor(Color.RED);

                    for (DecoratorClickHandler handler: decoratorClickHandlers){
                        if(handler!=null) handler.onDecoratorClick(decorator);
                    }
                    event.consume();
                });
            }
        }

        hboxDecorators.setOnDragEntered(event -> {
        });
        hboxDecorators.setOnDragExited(event -> {
        });
        hboxDecorators.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
        });
        hboxDecorators.setOnDragDropped(event -> {
            if(dragitem==null) return;
            double x = event.getSceneX();
            Node targetelem = null;
            double minx = 0;
            double maxx = 0;
            for (int i = 0; i< hboxDecorators.getChildren().size(); i++) {
                Node child = hboxDecorators.getChildren().get(i);
                Bounds bounds = child.getLayoutBounds();
                Bounds t = child.localToParent(bounds);
                if(i==0) {
                    minx = t.getMinX();
                }
                if(i== hboxDecorators.getChildren().size()-1) {
                    maxx = t.getMaxX();
                }
                if(x>=t.getMinX()&&x<=t.getMaxX()){
                    targetelem = child;
                    break;
                }
            }
            if(targetelem!=null) {
                int idx = hboxDecorators.getChildren().indexOf(targetelem);
                if(idx!=-1){
                    hboxDecorators.getChildren().remove(dragitem);
                    hboxDecorators.getChildren().add(idx, dragitem);
                }
            }else{
                if(x<minx) {
                    hboxDecorators.getChildren().remove(dragitem);
                    hboxDecorators.getChildren().add(0, dragitem);
                }else if(x>maxx){
                    hboxDecorators.getChildren().remove(dragitem);
                    hboxDecorators.getChildren().add(dragitem);
                }
            }
            dragitem = null;
            List<ConstructorInstance> ordered = hboxDecorators.getChildren().stream().map(t -> nodeDecoratorMap.get(t)).collect(Collectors.toList());
            instance.setDecorators(ordered);
        });

    }

    public void addDecoratorClickListener(DecoratorClickHandler handler) {
        if(handler!=null) {
            decoratorClickHandlers.add(handler);
        }
    }

    public void addExpandedHandler(MyTreeNodeControlExpandedHandler expandedHandler){
        if(expandedHandler!=null) {
            expandedHandlers.add(expandedHandler);
        }
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
        AnchorPane.setLeftAnchor(borderPane, (double) offsetX);
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
        if(isExpanded) expandIcon.setImage(imgExpanded);
        else expandIcon.setImage(imgCollapsed);
    }

    public void setBackgroundColor(Color color) {
        this.setBackground(new Background(new BackgroundFill(color, null, null)));
    }

    public void setText(String text) {
        instanceLabel.setText(text);
    }

    public String getText() {
        return instanceLabel.getText();
    }


}
