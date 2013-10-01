/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.TestGraphs;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationModel;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineBuilder;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 *
 * @author timheng
 */
public class JavaFXApplication11_Sep_24_1007 extends Application {

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    
    private static final int CIRCLE_SIZE = 15;
    
    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {

        //Initializing the root group, scene, and the 2 sub groups.
        //Root -> Scene -> group1, group2
        Group root = new Group();
        
//        HBox containerLayout = new HBox();
        final Group group1 = new Group();
        final Group group2 = new Group();

        
        final ArrayList<Circle> picked = new ArrayList<Circle>();       //Circles that are selected

        //Visualizing graph 1 using Jung
        Graph<String, Number> graph1 = TestGraphs.getOneComponentGraph();
        Layout<String, Number> layout1 = new CircleLayout<String, Number>(graph1);
        UndirectedSparseMultigraph<Circle, Line> newGraph1 = convertGraph(graph1, layout1);
        drawGraph(newGraph1, group1);

        // Visualizing graph 2
        Graph<String, Number> graph2 = TestGraphs.getOneComponentGraph();
        Layout<String, Number> layout2 = new SpringLayout<String, Number>(graph2);
        VisualizationModel<String, Number> vm2 = new DefaultVisualizationModel<>(layout2, new Dimension(400, 400));
        UndirectedSparseMultigraph<Circle, Line> newGraph2 = convertGraph(graph2, layout2);
        drawGraph(newGraph2, group2);

        root.getChildren().addAll(group1,group2);
        
        // Shift group 2 to the right by 400px
        group2.translateXProperty().set(500);


        Scene scene = new Scene(root, 1000, 500, Color.WHITE);
       
        // Clipping Lens
        final Rectangle lens = new Rectangle(100, 100, 200, 200);
        root.setClip(lens);
        
        scene.setOnMouseMoved(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent t) {
                lens.setX(t.getX());
                lens.setY(t.getY());
            }
        });
        
        // Show the scene.
        stage.setTitle("Simple Graph");
        stage.setScene(scene);
        stage.show();

    }

    public UndirectedSparseMultigraph<Circle, Line> convertGraph(Graph<String, Number> graph, Layout<String, Number> layout) {
        // look up the circle object used for the string vertex
        Map<String, Circle> toCircle = new HashMap<>();
        
        // look up the line object used for the number edge
        Map<Number, Line> toLine = new HashMap<>();
        
        // new graph with JavaFX objects in it.
        UndirectedSparseMultigraph<Circle, Line> undirectGraph 
                = new UndirectedSparseMultigraph<>();
        
        // Do the layout to get coords for the circles.
        VisualizationModel<String, Number> vm1 
                = new DefaultVisualizationModel<>(layout, new Dimension(400, 400)); 
        
         // Draw the edges from graph 1
        for (Number n : graph.getEdges()) {
            // get the endpoint verts
            Pair<String> endpoints = graph.getEndpoints(n);
            
            // create the circles
            Circle first = null;
            Circle second = null;
            
            if(!toCircle.containsKey(endpoints.getFirst())){
                first = createCircleVertex(layout, endpoints.getFirst());
                toCircle.put(endpoints.getFirst(), first);
            }else{
                first = toCircle.get(endpoints.getFirst());
            }
            
            if(!toCircle.containsKey(endpoints.getSecond())){
                second = createCircleVertex(layout, endpoints.getSecond());
                toCircle.put(endpoints.getSecond(), second);
            }else{
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

        return undirectGraph;

    }
    
    public void drawGraph(Graph<Circle, Line> graph, Group group){
       for(Circle c: graph.getVertices()){
           group.getChildren().add(c);
       }
       
       for(Line l: graph.getEdges()){
           group.getChildren().add(l);
       }
    }
    
    public Circle createCircleVertex(Layout<String, Number> layout, String vert){
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
//                    picked.add(c);
//                    System.out.println(picked);
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
}
