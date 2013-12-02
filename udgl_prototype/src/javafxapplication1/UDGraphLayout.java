package javafxapplication1;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineBuilder;
import javafx.stage.Stage;

import com.javafx.experiments.scenicview.ScenicView;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.graph.util.TestGraphs;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationModel;

public class UDGraphLayout extends Application {

	private static final int CIRCLE_SIZE = 15;
	private Color defaultNodeColor = Color.web("#5C4B51");
	private Color selectedNodeColor = Color.web("F06060");
	private UDGraphLayoutController controller;

	@Override
	public void start(Stage stage) throws Exception {
		// Visualizing graph 1 using Jung
		Graph<String, Number> graph = TestGraphs.getDemoGraph();
		Layout<String, Number> layout = new CircleLayout<String, Number>(graph);
		final UndirectedSparseMultigraph<Circle, Line> newGraph = convertGraph(
				graph, layout);

		// Create container for the UI
		VBox container = new VBox();
		ToolBar toolBar = new ToolBar();
		final Pane canvas = new Pane();

		// create a reference to the controller
		controller = new UDGraphLayoutController(newGraph, canvas);

		// Draw the default graph on the canvas
		drawGraph(newGraph, canvas);

		// Add the toolbar and the canvas to the layout
		container.getChildren().addAll(toolBar, canvas);

		// Create the scene
		Scene scene = new Scene(container, 1200, 800, Color.WHITE);
		scene.addEventFilter(KeyEvent.KEY_PRESSED,
				new EventHandler<KeyEvent>() {
					@Override
					public void handle(KeyEvent event) {
						controller.handleKeyPressed(event);
					}
				});

		// Add events to handle mouse selection
		canvas.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				controller.handleMousePressed(event);

			}
		});

		canvas.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				controller.handleMouseDragged(event);
			}
		});
		canvas.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				controller.handleMouseReleased(event);
			}
		});

		scene.setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				System.out.println(event.getText());

			}
		});

		// Add a button for each layout type
		for (Class c : controller.getLayoutClasses()) {
			Button btn = new Button(c.getSimpleName());
			btn.setUserData(c);
			btn.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					controller.handleLayoutButtonAction(event);
				}
			});
			toolBar.getItems().add(btn);
		}
		
		// Add a slider to control the layout size
		Slider layoutSize = new Slider(10, 500, 100);
		layoutSize.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				controller.handleResizing(newValue);
			}
		});
		toolBar.getItems().add(layoutSize);

		// Setup the stage for display
		stage.setTitle("User Definable Graph Layout Demo");
		stage.setScene(scene);
		stage.show();

		// ScenicView scenegraph debugger
		ScenicView.show(scene);
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * Converts String and Number graph to Circle and Line graph.
	 * 
	 * @param graph
	 *            to layout
	 * @param layout
	 *            to position nodes
	 * @return Circle, Line graph with the given layout
	 */
	public UndirectedSparseMultigraph<Circle, Line> convertGraph(
			Graph<String, Number> graph, Layout<String, Number> layout) {
		// look up the circle object used for the string vertex
		Map<String, Circle> toCircle = new HashMap<>();

		// look up the line object used for the number edge
		Map<Number, Line> toLine = new HashMap<>();

		// new graph with JavaFX objects in it.
		UndirectedSparseMultigraph<Circle, Line> undirectGraph = new UndirectedSparseMultigraph<>();

		// Do the layout to get coords for the circles.
		@SuppressWarnings("unused")
		VisualizationModel<String, Number> vm1 = new DefaultVisualizationModel<>(
				layout, new Dimension(400, 400));

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

			Line line = LineBuilder.create().stroke(defaultNodeColor).build();
			line.startXProperty().bind(first.centerXProperty());
			line.startYProperty().bind(first.centerYProperty());
			line.endXProperty().bind(second.centerXProperty());
			line.endYProperty().bind(second.centerYProperty());

			toLine.put(n, line);

			undirectGraph.addEdge(line, first, second);
		}

		return undirectGraph;

	}

	/**
	 * Draws the circles and attaches eventhandler for the circles.
	 * 
	 * @param layout
	 *            to be drawn
	 * @param vert
	 *            is String to be converted into Circle
	 * @return the Circle
	 */
	public Circle createCircleVertex(Layout<String, Number> layout, String vert) {
		// get the xy of the vertex.
		Point2D p = layout.transform(vert);
		Circle circle = CircleBuilder.create().centerX(p.getX())
				.centerY(p.getY()).radius(CIRCLE_SIZE).fill(defaultNodeColor)
				.build();

		circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
			// Clicked the circle to select and changes its color to dark red.
			@Override
			public void handle(MouseEvent t) {
				Circle c = (Circle) t.getSource();
				c.setFill(selectedNodeColor);
				if (!controller.getSelectedNodes().contains(c)) {
					controller.getSelectedNodes().add(c);
				}
				System.out.println(controller.getSelectedNodes());
			}
		});

		circle.setOnMousePressed(new EventHandler<MouseEvent>() {
			// Change the color to Rosybrown when pressed.
			@Override
			public void handle(MouseEvent t) {
				Circle c = (Circle) t.getSource();
				c.setFill(Color.ROSYBROWN);
				t.consume();
			}
		});

		circle.setOnMouseReleased(new EventHandler<MouseEvent>() {
			// Change it back to default color when released.
			@Override
			public void handle(MouseEvent t) {
				Circle c = (Circle) t.getSource();
				c.setFill(defaultNodeColor);
				t.consume();
			}
		});

		circle.setOnMouseDragged(new EventHandler<MouseEvent>() {
			// Update the circle's position when it's dragged.
			@Override
			public void handle(MouseEvent t) {
				Circle c = (Circle) t.getSource();
				c.setCenterX(t.getX());
				c.setCenterY(t.getY());
				t.consume();
			}
		});
		return circle;
	}

	/**
	 * Draws a graph in a group
	 * 
	 * @param graph
	 *            to draw
	 * @param group
	 *            to draw the graph in
	 */
	public void drawGraph(Graph<Circle, Line> graph, Pane group) {
		for (Circle c : graph.getVertices()) {
			if (!group.getChildren().contains(c)) {
				group.getChildren().add(c);
			}

		}

		for (Line l : graph.getEdges()) {
			if (!group.getChildren().contains(l)) {
				group.getChildren().add(l);
			}

		}
	}

}
