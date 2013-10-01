/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxtest;

import javafx.application.Application;
import javafx.beans.value.*;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

public class WhereIsHe extends Application {

    private static final double VIEWFINDER_WIDTH = 30;
    private static final double VIEWFINDER_HEIGHT = 80;
    public static final Color LENS_TINT = Color.YELLOW.deriveColor(0, 1, 1, 0.15);
    public static final Color MASK_TINT = Color.GRAY.deriveColor(0, 1, 1, 0.97);

    @Override
    public void start(Stage stage) {
        // Get an image and show it
        Image image = new Image("http://collider.com/wp-content/uploads/wheres-waldo2.jpg");
        ImageView background = new ImageView(image);
        
        StackPane layout = applyViewfinder(
                background, image.getWidth(), image.getHeight());
        stage.setScene(new Scene(layout));
        stage.show();
    }

    private StackPane applyViewfinder(Node background, double width, double height) {
        final Rectangle mask = new Rectangle(
                width,
                height);
        
        final Rectangle viewfinder = new Rectangle(
                VIEWFINDER_WIDTH,
                VIEWFINDER_HEIGHT,
                LENS_TINT);
        makeDraggable(viewfinder);
        
        final Pane viewpane = new Pane();
        viewpane.getChildren().addAll(
                new Group(),
                viewfinder);
        viewfinder.layoutXProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                System.out.println("X Changed");
                
            }
        });
        
        viewfinder.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observableValue, Bounds bounds, Bounds bounds2) {
                System.out.println("BOunds changed");
                applyStencil(
                        cutStencil(mask, viewfinder),
                        viewpane);
                
            }
        });
        viewfinder.relocate(
                width / 2 - VIEWFINDER_WIDTH / 2,
                height / 2 - VIEWFINDER_HEIGHT / 2);
        StackPane layout = new StackPane();
        layout.getChildren().setAll(
                background,
                viewpane);
        return layout;
    }

    private void applyStencil(Node stencil, Pane viewpane) {
        viewpane.getChildren().set(0, stencil);
    }

    private Node cutStencil(Rectangle mask, Rectangle viewfinder) {
        Shape stencil = Shape.subtract(mask, viewfinder);
        stencil.setFill(MASK_TINT);
        return stencil;
    }

    public static void makeDraggable(final Node node) {
        final Delta dragDelta = new Delta();
        node.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.  
                dragDelta.x = mouseEvent.getX();
                dragDelta.y = mouseEvent.getY();
            }
        });
        node.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                node.setCursor(Cursor.MOVE);
            }
        });
        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                node.translateXProperty().set(mouseEvent.getX() - dragDelta.x);
                node.translateYProperty().set(mouseEvent.getY() - dragDelta.y);
                
//                node.relocate(
//                        mouseEvent.getSceneX() - dragDelta.x,
//                        mouseEvent.getSceneY() - dragDelta.y);
                node.setCursor(Cursor.NONE);
            }
        });
        node.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    node.setCursor(Cursor.MOVE);
                }
            }
        });
        node.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (!mouseEvent.isPrimaryButtonDown()) {
                    node.setCursor(Cursor.DEFAULT);
                }
            }
        });
    }

    private static class Delta {

        double x, y;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
