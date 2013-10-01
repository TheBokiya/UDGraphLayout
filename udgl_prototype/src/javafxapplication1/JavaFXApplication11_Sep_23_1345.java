/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.SetHypergraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.TestGraphs;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationModel;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineBuilder;
import javafx.stage.Stage;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableDoubleValue;

/**
 *
 * @author timheng
 */
public class JavaFXApplication11_Sep_23_1345 extends Application {

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    //This version has nodes and edges connected. Click to change color is also implemented.
    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage stage) throws Exception {

        //Initializing the root group, scene, and the 2 sub groups.
        //Root -> Scene -> group1, group2
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 500, Color.WHITE);
        final Group group1 = new Group();
        Group group2 = new Group();

        int circleSize = 15;
        final ArrayList<Circle> picked = new ArrayList<Circle>();       //Circles that are selected

        //Visualizing graph 1 using Jung
        Graph<String, Number> graph1 = TestGraphs.getOneComponentGraph();
        Layout<String, Number> layout1 = new CircleLayout<String, Number>(graph1);
        VisualizationModel<String, Number> vm1 = new DefaultVisualizationModel<>(layout1, new Dimension(400, 400));

        ArrayList<Circle> myCircles = new ArrayList<>();
        final ArrayList<Line> myLines = new ArrayList<>();

        //Get the location point from the layout and draw circles base on those points.
        for (String v : graph1.getVertices()) {

            Point2D p = layout1.transform(v);
            Circle circle = CircleBuilder.create()
                    .centerX(p.getX())
                    .centerY(p.getY())
                    .radius(circleSize)
                    .build();
            myCircles.add(circle);

            circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
                //Clicked the circle to select and changes its color to dark red.
                @Override
                public void handle(MouseEvent t) {
                    Circle c = (Circle) t.getSource();
                    c.setFill(Color.DARKRED);
                    picked.add(c);
                    System.out.println(picked);
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
            group1.getChildren().add(circle);

        }

        // Draw the edges from graph 1
        for (Number n : graph1.getEdges()) {
            Pair<String> endpoints = graph1.getEndpoints(n);
            Point2D pStart = layout1.transform(endpoints.getFirst());
            Point2D pEnd = layout1.transform(endpoints.getSecond());
            Line line = LineBuilder.create()
                    .startX(pStart.getX())
                    .startY(pStart.getY())
                    .endX(pEnd.getX())
                    .endY(pEnd.getY())
                    .fill(Color.AQUAMARINE)
                    .build();
            myLines.add(line);

//            for (int i = 0; i < myCircles.size(); i++) {
//                System.out.println("Circle "+myCircles.get(i).getCenterX()+", "+myCircles.get(i).getCenterY());
//                if (line.getStartX() == myCircles.get(i).getCenterX()) {
////                    line.setStartX(myCircles.get(i).getCenterX());
//                    System.out.println("StartX: "+line.getStartX());
//                    
//                } else if (line.getStartY() == myCircles.get(i).getCenterY()) {
////                    line.setStartY(myCircles.get(i).getCenterY());
//                    System.out.println("StartY: "+line.getStartY());
//                    
//                } else if (line.getEndX() == myCircles.get(i).getCenterX()) {
////                    line.setEndX(myCircles.get(i).getCenterX());
//                    System.out.println("EndX: "+line.getEndX());
//                    
//                } else if (line.getEndY() == myCircles.get(i).getCenterY()) {
////                    line.setEndY(myCircles.get(i).getCenterY());
//                    System.err.println("EndY: "+line.getEndY());
//                }
//            }

            group1.getChildren().add(line);
        }

        for (int i = 0; i < myCircles.size(); i++) {
            for (int j = 0; j < myLines.size(); j++) {
                if (myCircles.get(i).getCenterX() == myLines.get(j).getStartX()) {
                    
                    myLines.get(j).startXProperty().bind(myCircles.get(i).centerXProperty());
                    
                } 
                 
                if (myCircles.get(i).getCenterX() == myLines.get(j).getEndX()) {
                    
                    myLines.get(j).endXProperty().bind(myCircles.get(i).centerXProperty());

                } 
                
                if (myCircles.get(i).getCenterY()== myLines.get(j).getStartY()) {
                    
                    myLines.get(j).startYProperty().bind(myCircles.get(i).centerYProperty());
                    
                } 
                
                if (myCircles.get(i).getCenterY() == myLines.get(j).getEndY()) {
                    
                    myLines.get(j).endYProperty().bind(myCircles.get(i).centerYProperty());
                    
                }
                

            }
        }

        // Visualizing graph 2
        Graph<String, Number> graph2 = TestGraphs.getOneComponentGraph();
        Layout<String, Number> layout2 = new SpringLayout<String, Number>(graph2);
        VisualizationModel<String, Number> vm2 = new DefaultVisualizationModel<>(layout2, new Dimension(400, 400));

        for (String v : graph2.getVertices()) {
            Point2D p = layout2.transform(v);
            Circle circle = CircleBuilder.create()
                    .centerX(p.getX())
                    .centerY(p.getY())
                    .radius(circleSize)
                    .build();

            circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
                // Change color to darkred and add to picked arraylist when clicked.
                @Override
                public void handle(MouseEvent t) {
                    Circle c = (Circle) t.getSource();
                    c.setFill(Color.DARKRED);
                    picked.add(c);
//                    System.out.println(picked);
                }
            });

            circle.setOnMousePressed(new EventHandler<MouseEvent>() {
                // Change the color to rosybrown when pressed
                @Override
                public void handle(MouseEvent t) {
                    Circle c = (Circle) t.getSource();
                    c.setFill(Color.ROSYBROWN);
                }
            });

            circle.setOnMouseReleased(new EventHandler<MouseEvent>() {
                // Change color to black when released.
                @Override
                public void handle(MouseEvent t) {
                    Circle c = (Circle) t.getSource();
                    c.setFill(Color.BLACK);
                }
            });

            circle.setOnMouseDragged(new EventHandler<MouseEvent>() {
                // Change circle's position when dragged.
                @Override
                public void handle(MouseEvent t) {
                    Circle c = (Circle) t.getSource();
                    c.setCenterX(t.getX());
                    c.setCenterY(t.getY());
                }
            });

            group2.getChildren().add(circle);

        }

        // Draw edges for graph 2
        for (Number n : graph2.getEdges()) {
            Pair<String> endpoints = graph2.getEndpoints(n);
            Point2D pStart = layout2.transform(endpoints.getFirst());
            Point2D pEnd = layout2.transform(endpoints.getSecond());
            Line line = LineBuilder.create()
                    .startX(pStart.getX())
                    .startY(pStart.getY())
                    .endX(pEnd.getX())
                    .endY(pEnd.getY())
                    .build();
            group2.getChildren().add(line);
        }

        // Shift group 2 to the right by 400px
        group2.translateXProperty().set(500);

        // Add two groups to the root group
        root.getChildren().add(group1);
        root.getChildren().add(group2);


        // Show the scene.
        stage.setTitle("Simple Graph");
        stage.setScene(scene);
        stage.show();

    }
}
