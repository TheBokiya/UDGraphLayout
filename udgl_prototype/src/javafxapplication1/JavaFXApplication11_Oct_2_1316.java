/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.TestGraphs;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.picking.PickedState;
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
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBuilder;
import static javafxtest.WhereIsHe.makeDraggable;

/**
 *
 * @author timheng
 */
public class JavaFXApplication11_Oct_2_1316 extends Application {

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
        final Group group3 = new Group();

        //Visualizing graph 1 using Jung
        Graph<String, Number> graph1 = TestGraphs.getOneComponentGraph();
        Layout<String, Number> layout1 = new CircleLayout<String, Number>(graph1);
        final UndirectedSparseMultigraph<Circle, Line> newGraph1 = convertGraph(graph1, layout1);
        drawGraph(newGraph1, group1);

        for (Circle circle : newGraph1.getVertices()) {
            Text t = TextBuilder.create()
                    .text(circle.toString().substring(7))
                    .x(circle.getCenterX())
                    .y(circle.getCenterY() - (CIRCLE_SIZE + 5))
                    .fill(Color.BLUEVIOLET)
                    .font(Font.font("Verdana", 10))
                    .textAlignment(TextAlignment.CENTER)
                    .build();
            root.getChildren().add(t);
        }


        // Visualizing graph 2
        Graph<String, Number> graph2 = TestGraphs.getOneComponentGraph();
        Layout<String, Number> layout2 = new SpringLayout<String, Number>(graph2);
        VisualizationModel<String, Number> vm2 = new DefaultVisualizationModel<>(layout2, new Dimension(400, 400));
        UndirectedSparseMultigraph<Circle, Line> newGraph2 = convertGraph(graph2, layout2);
        drawGraph(newGraph2, group2);

        root.getChildren().addAll(group1, group2);


        root.translateYProperty().set(50);
        // Shift group 2 to the right by 400px
        group2.translateXProperty().set(500);

        Button clearPickedBtn = new Button("Clear");
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
        
        clearPickedBtn.translateYProperty().set(50);

        root.getChildren().add(clearPickedBtn);

        Button subLayoutBtn = new Button("FRLayout");
        subLayoutBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {
                System.out.println("FRLayout is pressed!");
                UndirectedSparseMultigraph<Circle, Line> generateGraph = null;
                try {
                    generateGraph = generateGraph(PICKED, newGraph1);
                } catch (Exception ex) {
                    Logger.getLogger(JavaFXApplication11_Oct_2_1316.class.getName()).log(Level.SEVERE, null, ex);
                }
                drawGraph(generateGraph, group3);
            }
        });
        
        group3.translateXProperty().set(1000);

        root.getChildren().add(group3);
        root.getChildren().add(subLayoutBtn);



//        final ToggleButton lensBtn = new ToggleButton("Lens");
//        lensBtn.selectedProperty().addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
//                if (t1) {
//                    System.out.println("Lens enabled");
//                    StackPane lensLayout = applyViewfinder(root, 1000, 500);
//                    Scene scene = new Scene(lensLayout);
//                    stage.setScene(scene);
//                } else {
//                    System.out.println("Lens disabled");
//                    Scene scene = new Scene(root, 1100, 500, Color.WHITE);
//                    stage.setScene(scene);
//                }
//            }
//        });

        // Uncomment these to enable the lens
//        final StackPane lensLayout = applyViewfinder(root, 1100, 500, newGraph1);
//        final Scene scene = new Scene(lensLayout);


        // Uncomment this to disable the lens
        Scene scene = new Scene(root, 1000, 500, Color.WHITE);

//        lensBtn.translateXProperty().set(1000);
//        lensBtn.translateYProperty().set(100);
//        root.getChildren().add(lensBtn);


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
    
    public UndirectedSparseMultigraph<Circle, Line> generateGraph(ArrayList<Circle> picked, UndirectedSparseMultigraph<Circle, Line> graph) throws Exception {
        UndirectedSparseMultigraph<Circle, Line> subGraph = new UndirectedSparseMultigraph<>();
        
        int count = graph.getVertexCount();
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
                Layout<Circle, Line> subLayout = getLayoutFor(CircleLayout.class, subGraph);
                BasicVisualizationServer<Circle, Line> vv = new BasicVisualizationServer<Circle, Line>(subLayout);
                vv.setPreferredSize(new Dimension(250, 250));
        Collection<Circle> vertices = subGraph.getVertices();
        
        for (Circle c : vertices) {
            c.setCenterX(subLayout.transform(c).getX());
            c.setCenterY(subLayout.transform(c).getY());
        }
        
        return subGraph;
    }
    
    private Layout getLayoutFor(Class layoutClass, Graph graph) throws Exception {
    	Object[] args = new Object[]{graph};
    	Constructor constructor = layoutClass.getConstructor(new Class[] {Graph.class});
    	return  (Layout)constructor.newInstance(args);
    }

    public Layout<Circle, Line> convertLayout(UndirectedSparseMultigraph<Circle, Line> graph, String layoutType) {
        Layout<Circle, Line> resultLayout = null;
        System.out.println(graph.getVertexCount());

        if (layoutType.equalsIgnoreCase("FR_LAYOUT")) {
            resultLayout.setGraph(graph);
        }

        return resultLayout;
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
                PICKED.add(c);
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
        final JavaFXApplication11_Oct_2_1316.Delta dragDelta = new JavaFXApplication11_Oct_2_1316.Delta();
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
                System.out.println(VIEWFINDER_WIDTH);
                System.err.println(VIEWFINDER_HEIGHT);
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