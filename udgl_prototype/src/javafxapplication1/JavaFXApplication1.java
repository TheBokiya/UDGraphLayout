/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.TestGraphs;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationModel;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineBuilder;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import static javafxtest.WhereIsHe.makeDraggable;

/**
 *
 * @author timheng
 */
public class JavaFXApplication1 extends Application {

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    private static final int CIRCLE_SIZE = 15;
    private static final ArrayList<Circle> PICKED = new ArrayList<Circle>();       //Circles that are selected
    private static final Color LENS_TINT = Color.WHITE.deriveColor(0, 1, 1, 0.15);
    private static final Color MASK_TINT = Color.GREY.deriveColor(0, 1, 1, 0.97);
    private static double VIEWFINDER_WIDTH = 200;
    private static double VIEWFINDER_HEIGHT = 200;
    
    private static Class[] layoutClasses = new Class[]{CircleLayout.class, SpringLayout.class, FRLayout.class, KKLayout.class, StaticLayout.class, ISOMLayout.class};
    private double posX = 0;
    private double posY = 0;
    private int sublayoutDimension = 200;
    
    public static void main(String[] args) {
        launch(args);
        
    }
    
    @Override
    public void start(final Stage stage) throws Exception {

        //Initializing the root group, scene, and the 2 sub groups.
        //Root -> Scene -> group1, group2
        final Group root = new Group();
        
        final Group group1 = new Group();
        final Group group2 = new Group();
//        final Group group3 = new Group();
        
        final HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #EA6045");
        hbox.translateYProperty().setValue(0);

        //Visualizing graph 1 using Jung
        Graph<String, Number> graph1 = TestGraphs.getDemoGraph();
        Layout<String, Number> layout1 = new CircleLayout<String, Number>(graph1);
        final UndirectedSparseMultigraph<Circle, Line> newGraph1 = convertGraph(graph1, layout1);
        drawGraph(newGraph1, group1);
        
        root.getChildren().addAll(group1);
        
        // Shift group 2 to the right by 1000px
        group2.translateXProperty().set(1000);
        
        group1.translateYProperty().set(100);
        group2.translateYProperty().set(100);
        
        // A button to clear the selected vertices from the PICKED arraylist
        Button clearPickedBtn = new Button("Clear Selections");
        clearPickedBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                PICKED.clear();
                for (Circle c : newGraph1.getVertices()) {
                    c.setFill(Color.BLACK);
                }
                System.out.println(PICKED);
            }
        });
        
        hbox.getChildren().add(clearPickedBtn);
        
        // Creates buttons for all the layouts in the layoutClasses array
        for (final Class c : layoutClasses) {
            Button button = new Button(c.getSimpleName());
            button.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent t) {
                    UndirectedSparseMultigraph<Circle, Line> genGraph = null;
                    try {
                        
                        // Generate an undirected sparse multigraph with selected layout
                        genGraph = generateGraph(PICKED, newGraph1, c, new Dimension(sublayoutDimension, sublayoutDimension));
                        
                    } catch (Exception ex) {
                        Logger.getLogger(JavaFXApplication1.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    // Remove the vertices and edges from the group before adding the new ones to avoid
                    // "duplication of children" error
                    for (Circle c : genGraph.getVertices()) {
                        for (Line l : genGraph.getEdges())
                        {
                            group1.getChildren().remove(l);
                            group1.getChildren().remove(c);
                        }
                    }
                    drawGraph(genGraph, group1);
                    
                    
                }
            });
            hbox.getChildren().add(button);
        }
        
        final Text posText = TextBuilder.create()
                .text("Pos X: " + posX + ", Pos Y: " + posY)
                .font(Font.font("Verdana", 12))
                .fill(Color.WHITE)
                .build();
        
        final Text bullseye = TextBuilder.create()
                .text("X")
                .fill(Color.BLUE)
                .build();
        
        
        hbox.getChildren().addAll(posText);
        
        Separator seperator = new Separator(Orientation.VERTICAL);
        
        final Text subLayoutWidth = TextBuilder.create()
                .text("SubLayout Width: " + sublayoutDimension)
                .font(Font.font("Verdana", 12))
                .fill(Color.WHITE)
                .build();
        
        final Text subLayoutHeight = TextBuilder.create()
                .text("SubLayout Height: " + sublayoutDimension)
                .font(Font.font("Verdana", 12))
                .fill(Color.WHITE)
                .build();
        
        Slider slider = new Slider();
        slider.setMin(10);
        slider.setMax(500);
        slider.setValue(200);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setBlockIncrement(5);
        slider.setSnapToTicks(true);
        
        slider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                sublayoutDimension = t1.intValue();
                subLayoutWidth.setText("SubLayout Width: " + sublayoutDimension);
                subLayoutHeight.setText("SubLayout Height: " + sublayoutDimension);
            }
        });
        
        
        
        hbox.getChildren().addAll(seperator, subLayoutWidth, subLayoutHeight, slider);

        root.getChildren().addAll(hbox, bullseye);

        // Uncomment this to disable the lens
        Scene scene = new Scene(root, 1500, 600, Color.WHITE);
        
        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent e) {
                if (e.getButton() == MouseButton.SECONDARY) {
                    posX = e.getX();
                    posY = e.getY();
                    posText.setText("Pos X: " + posX + ", Pos Y: " + posY);
                    bullseye.setX(posX);
                    bullseye.setY(posY);
                }
            }
        });


        // Show the scene.
        stage.setTitle("Simple Graph");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
        
    }
    
    public UndirectedSparseMultigraph<Circle, Line> convertGraph(Graph<String, Number> graph, Layout<String, Number> layout) {
        // look up the circle object used for the string vertex
        Map<String, Circle> toCircle = new HashMap<>();

        // look up the line object used for the number edge
        Map<Number, Line> toLine = new HashMap<>();

        // new graph with JavaFX objects in it.
        UndirectedSparseMultigraph<Circle, Line> undirectGraph = new UndirectedSparseMultigraph<>();

        // Do the layout to get coords for the circles.
        VisualizationModel<String, Number> vm1 = new DefaultVisualizationModel<>(layout, new Dimension(400, 400));

        // Draw the edges from graph 1
        for (Number n : graph.getEdges()) {
            // get the endpoint verts
            Pair<String> endpoints = graph.getEndpoints(n);

            // create the circles
            Circle first = null;
            Circle second = null;
            
            if (!toCircle.containsKey(endpoints.getFirst())) {
                first = createCircleVertex(layout, endpoints.getFirst());
                toCircle.put(endpoints.getFirst(), first);
            } else {
                first = toCircle.get(endpoints.getFirst());
            }
            
            if (!toCircle.containsKey(endpoints.getSecond())) {
                second = createCircleVertex(layout, endpoints.getSecond());
                toCircle.put(endpoints.getSecond(), second);
            } else {
                second = toCircle.get(endpoints.getSecond());
            }
            
            Line line = LineBuilder.create()
                    .build();
            line.startXProperty().bind(first.centerXProperty());
            line.startYProperty().bind(first.centerYProperty());
            line.endXProperty().bind(second.centerXProperty());
            line.endYProperty().bind(second.centerYProperty());
            
            toLine.put(n, line);
            
            undirectGraph.addEdge(line, first, second);
        }
        
        System.out.println(layout.toString());
        
        return undirectGraph;
        
    }

    // Generate a graph for the picked vertices
    public UndirectedSparseMultigraph<Circle, Line> generateGraph(ArrayList<Circle> picked, UndirectedSparseMultigraph<Circle, Line> graph, Class layout, Dimension dimension) throws Exception {
        
        UndirectedSparseMultigraph<Circle, Line> subGraph = new UndirectedSparseMultigraph<>();
        
        for (Circle c : picked) {
            subGraph.addVertex(c);
            Collection<Line> incidentEdges = graph.getIncidentEdges(c);
            for (Line l : incidentEdges) {
                Pair<Circle> endpoints = graph.getEndpoints(l);
                if (picked.containsAll(endpoints)) {
                    subGraph.addEdge(l, endpoints.getFirst(), endpoints.getSecond());
                }
            }
        }
        
        
        
        Layout<Circle, Line> subLayout = getLayoutFor(layout, subGraph);
        BasicVisualizationServer<Circle, Line> vv = new BasicVisualizationServer<Circle, Line>(subLayout, dimension);
        Collection<Circle> vertices = subGraph.getVertices();
        
        for (Circle c : vertices) {
            c.setCenterX(subLayout.transform(c).getX()+posX-90);
            c.setCenterY(subLayout.transform(c).getY()+posY-210);
        }
        
        return subGraph;
    }
    
    private Layout getLayoutFor(Class layoutClass, Graph graph) throws Exception {
        Object[] args = new Object[]{graph};
        Constructor constructor = layoutClass.getConstructor(new Class[]{Graph.class});
        return (Layout) constructor.newInstance(args);
    }
    
    public void drawGraph(Graph<Circle, Line> graph, Group group) {
        for (Circle c : graph.getVertices()) {
            group.getChildren().add(c);
        }
        
        for (Line l : graph.getEdges()) {
            group.getChildren().add(l);
        }
    }
    
    public Circle createCircleVertex(Layout<String, Number> layout, String vert) {
        // get the xy of the vertex.
        Point2D p = layout.transform(vert);
        Circle circle = CircleBuilder.create()
                .centerX(p.getX())
                .centerY(p.getY())
                .radius(CIRCLE_SIZE)
                .build();
        
        circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
            //Clicked the circle to select and changes its color to dark red.
            @Override
            public void handle(MouseEvent t) {
                Circle c = (Circle) t.getSource();
                c.setFill(Color.DARKRED);
                if (!PICKED.contains(c)) {
                    PICKED.add(c);
                }
                System.out.println(PICKED);
            }
        });
        
        circle.setOnMousePressed(new EventHandler<MouseEvent>() {
            // Change the color to Rosybrown when pressed.
            @Override
            public void handle(MouseEvent t) {
                Circle c = (Circle) t.getSource();
                c.setFill(Color.ROSYBROWN);
            }
        });
        
        circle.setOnMouseReleased(new EventHandler<MouseEvent>() {
            // Change it back to black when released.
            @Override
            public void handle(MouseEvent t) {
                Circle c = (Circle) t.getSource();
                c.setFill(Color.BLACK);
            }
        });
        
        circle.setOnMouseDragged(new EventHandler<MouseEvent>() {
            // Update the circle's position when it's dragged.
            @Override
            public void handle(MouseEvent t) {
                Circle c = (Circle) t.getSource();
                c.setCenterX(t.getX());
                c.setCenterY(t.getY());
            }
        });
        return circle;
    }
    
    public static void makeDraggable(final Node node) {
        final JavaFXApplication1.Delta dragDelta = new JavaFXApplication1.Delta();
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
                
                node.relocate(
                        mouseEvent.getSceneX() - dragDelta.x,
                        mouseEvent.getSceneY() - dragDelta.y);
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

        // Scale the lens with mouse scroll
        node.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent t) {
                node.setScaleX(t.getDeltaY() / 50);
                node.setScaleY(t.getDeltaY() / 50);
            }
        ;
    }
    
    );
    }

    private static class Delta {
        
        double x, y;
    }
    
    private StackPane applyViewfinder(Node background, double width, double height, final UndirectedSparseMultigraph<Circle, Line> graph) {
        
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
        
        viewfinder.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observableValue, Bounds bounds, Bounds bounds2) {
                applyStencil(
                        cutStencil(mask, viewfinder),
                        viewpane);
            }
        });

        // Add the circles that are in the viewfinder to the PICKED arraylist
        viewfinder.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                for (Circle c : graph.getVertices()) {
                    if (viewfinder.getBoundsInParent().intersects(c.getBoundsInParent())) {
//                    if ( (c.getCenterX() > viewfinder.getLayoutX()) && (c.getCenterX() < viewfinder.getLayoutX()+VIEWFINDER_WIDTH)
//                            && (c.getCenterY() > viewfinder.getLayoutY()) && (c.getCenterY() < viewfinder.getLayoutY()+VIEWFINDER_HEIGHT)) {
                        System.out.println(c.toString().substring(7) + ": TRUE");
                        
                        PICKED.add(c);
                    } else {
                        System.out.println(c.toString().substring(7) + ": FALSE");
                    }
                }
//                System.out.println("ViewfinderX " + viewfinder.getLayoutX());
//                System.err.println("ViewfinderY " + viewfinder.getLayoutY());
                System.out.println(PICKED);
            }
        });

        // When the viewfinder is clicked, clear the PICKED arraylist
        viewfinder.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                PICKED.clear();
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
}
