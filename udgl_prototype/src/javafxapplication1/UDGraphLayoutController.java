package javafxapplication1;

import java.awt.Dimension;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.transform.Scale;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public class UDGraphLayoutController {

	private Group canvas;
	private Rectangle selectionRectangle;
	private double anchorX, anchorY;
	private Color selectionStrokeColor = Color.web("#FF883E");
	private Color selectionFillColor = Color.web("#FF883E", 0.1);
	private ArrayList<Circle> selectedNodes = new ArrayList<>();
	private static Class[] layoutClasses = new Class[] { CircleLayout.class,
			SpringLayout.class, FRLayout.class, KKLayout.class,
			StaticLayout.class };
	private UndirectedSparseMultigraph<Circle, Line> graph;
	private Scale scaleX, scaleY;
	private Group selectedGroup;
	private Color defaultNodeColor = Color.web("#5C4B51");
	private Color selectedNodeColor = Color.web("F06060");
	private Rectangle selectionBg;

	public UDGraphLayoutController(
			UndirectedSparseMultigraph<Circle, Line> graph, Group canvas) {
		this.canvas = canvas;
		anchorX = 0;
		anchorY = 0;
		selectionRectangle = null;
		selectedNodes = new ArrayList<>();
		this.graph = graph;
	}

	public ArrayList<Circle> getSelectedNodes() {
		return selectedNodes;
	}

	public static Class[] getLayoutClasses() {
		return layoutClasses;
	}

	public void handleKeyPressed(KeyEvent e) {
		if (e.getCode() == KeyCode.ESCAPE) {
			canvas.getChildren().remove(selectionRectangle);
			resetSelection();
		}
	}

	public void handleMousePressed(MouseEvent e) {
		resetSelection();

		anchorX = e.getX();
		anchorY = e.getY();
		if (selectionRectangle == null) {
			selectionRectangle = drawRectangle();
			canvas.getChildren().add(selectionRectangle);
		} else {
			canvas.getChildren().remove(selectionRectangle);
			selectionRectangle = drawRectangle();
			canvas.getChildren().add(selectionRectangle);
		}

		scaleX = new Scale(-1, 1, anchorX, anchorY);
		scaleY = new Scale(1, -1, anchorX, anchorY);

	}

	public void handleMouseDragged(MouseEvent e) {
		if (e.getX() - anchorX > 0) {
			selectionRectangle.setWidth(e.getX() - anchorX);
			if (selectionRectangle.getTransforms().contains(scaleX)) {
				selectionRectangle.getTransforms().remove(scaleX);

			}
		} else {
			selectionRectangle.setWidth(Math.abs(e.getX() - anchorX));
			if (!selectionRectangle.getTransforms().contains(scaleX)) {
				selectionRectangle.getTransforms().add(scaleX);
			}
		}

		if (e.getY() - anchorY > 0) {
			selectionRectangle.setHeight(e.getY() - anchorY);
			if (selectionRectangle.getTransforms().contains(scaleY)) {
				selectionRectangle.getTransforms().remove(scaleY);
			}
		} else {
			selectionRectangle.setHeight(Math.abs(e.getY() - anchorY));
			if (!selectionRectangle.getTransforms().contains(scaleY)) {
				selectionRectangle.getTransforms().add(scaleY);
			}
		}

		selectedNodes.clear();
		checkCollision(selectionRectangle, canvas);

		// System.out.println(selectedNodes);
	}

	public void handleMouseReleased(MouseEvent e) {
		if (!selectedNodes.isEmpty() && selectionRectangle != null) {
			canvas.getChildren().removeAll(selectedNodes);
			selectedGroup = createSelectionGroup(selectedNodes,
					selectionRectangle, graph);

			selectedGroup.setOnMouseEntered(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					handleSelectionRectangleMouseEntered(event);
				}
			});

			selectedGroup.setOnMouseExited(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					handleSelectionRectangleMouseExited(event);
				}
			});
			addInternalEdges(graph, selectedGroup);
			canvas.getChildren().add(selectedGroup);
			canvas.getChildren().remove(selectionRectangle);
		}
	}

	public void handleSelectionRectangleMouseEntered(MouseEvent e) {
		if (!selectedGroup.getChildren().isEmpty()) {
			selectedGroup.setStyle("-fx-border-color: hsba(46, 68%, 94%, 1);");
			selectedGroup
					.setStyle("-fx-background-color: hsba(46, 68%, 94%, 0.1);");
		}
	}

	public void handleSelectionRectangleMouseExited(MouseEvent e) {
		if (!selectedGroup.getChildren().isEmpty()) {
			selectedGroup.setStyle("-fx-border-color: hsba(46, 68%, 94%, 0);");
			selectedGroup
					.setStyle("-fx-background-color: hsba(46, 68%, 94%, 0);");
		}
	}

	public void handleLayoutButtonAction(ActionEvent e) {
		Button b = (Button) e.getSource();
		Class c = (Class) b.getUserData();

		// generate the graph from the selection
		try {
			if (!selectedNodes.isEmpty()) {
				generateGraph(selectedNodes, graph, c, selectionRectangle);
				canvas.getChildren().remove(selectionRectangle);
			}

		} catch (Exception ex) {
			Logger.getLogger(JavaFXApplication1.class.getName()).log(
					Level.SEVERE, null, ex);
		}
	}

	// Generate a graph for the picked vertices
	public UndirectedSparseMultigraph<Circle, Line> generateGraph(
			ArrayList<Circle> picked,
			UndirectedSparseMultigraph<Circle, Line> graph,
			@SuppressWarnings("rawtypes") Class layout, Rectangle selectedRect)
			throws Exception {

		UndirectedSparseMultigraph<Circle, Line> subGraph = new UndirectedSparseMultigraph<>();

		for (Circle c : picked) {
			subGraph.addVertex(c);
			Collection<Line> incidentEdges = graph.getIncidentEdges(c);
			for (Line l : incidentEdges) {
				Pair<Circle> endpoints = graph.getEndpoints(l);
				if (picked.containsAll(endpoints)) {
					subGraph.addEdge(l, endpoints.getFirst(),
							endpoints.getSecond());
				}
			}
		}

		@SuppressWarnings("unchecked")
		Layout<Circle, Line> subLayout = getLayoutFor(layout, subGraph);

		int size = (int) Math.min(selectedRect.getWidth(),
				selectedRect.getHeight());
		Dimension d = new Dimension(size, size);
		BasicVisualizationServer<Circle, Line> vv = new BasicVisualizationServer<Circle, Line>(
				subLayout, d);

		Collection<Circle> vertices = subGraph.getVertices();

		System.out.println(selectedRect);
		
		for (Circle c : vertices) {
			c.setCenterX(subLayout.transform(c).getX());
			c.setCenterY(subLayout.transform(c).getY());
		}

		return subGraph;
	}

	private Layout getLayoutFor(Class layoutClass, Graph graph)
			throws Exception {
		Object[] args = new Object[] { graph };
		@SuppressWarnings("unchecked")
		Constructor constructor = layoutClass
				.getConstructor(new Class[] { Graph.class });
		return (Layout) constructor.newInstance(args);
	}

	public Group createSelectionGroup(ArrayList<Circle> selection,
			Rectangle selectionRect,
			UndirectedSparseMultigraph<Circle, Line> graph) {
		// add selected nodes to the group
		// add background
		Group g = new Group();
		final Rectangle bg = RectangleBuilder.create().x(selectionRect.getX())
				.y(selectionRect.getY()).width(selectionRect.getWidth())
				.height(selectionRect.getHeight()).fill(Color.YELLOW)
				.opacity(1).build();

		canvas.getChildren().add(bg);
		
		bg.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> observable,
					Bounds oldValue, Bounds newValue) {
				System.out.println("old: " + oldValue);
				System.out.println("new: " + newValue);
				
				bg.setX(newValue.getMinX());
				bg.setY(newValue.getMinY());
				
				bg.setWidth(newValue.getWidth() - Math.abs(newValue.getWidth() - oldValue.getWidth()));
				bg.setHeight(newValue.getHeight());
				
				
			}
		});
		
		for (Circle c : selection) {
			Point2D parentToLocal = g.parentToLocal(c.getCenterX(), c.getCenterY());
			c.setCenterX(parentToLocal.getX());
			c.setCenterY(parentToLocal.getY());
		}
		g.getChildren().addAll(selection);
		

		// make the group draggable
		GroupMoveHandler groupMoveEventHandler = new GroupMoveHandler();
		g.setOnMousePressed(groupMoveEventHandler);
		g.setOnMouseDragged(groupMoveEventHandler);
		g.setOnMouseReleased(groupMoveEventHandler);

		for (Line l : graph.getEdges()) {

			Pair<Circle> endpoints = graph.getEndpoints(l);
			Circle start = endpoints.getFirst();
			Circle end = endpoints.getSecond();

			if (selectedNodes.contains(start) && selectedNodes.contains(end)) {
				l.startXProperty().bind(start.centerXProperty());
				l.startYProperty().bind(start.centerYProperty());
				l.endXProperty().bind(end.centerXProperty());
				l.endYProperty().bind(end.centerYProperty());
			} else {
				if (selectedNodes.contains(start)) {
					l.startXProperty().bind(
							start.centerXProperty().add(g.layoutXProperty()));
					l.startYProperty().bind(
							start.centerYProperty().add(g.layoutYProperty()));
				}
				if (selectedNodes.contains(end)) {
					l.endXProperty().bind(
							end.centerXProperty().add(g.layoutXProperty()));
					l.endYProperty().bind(
							end.centerYProperty().add(g.layoutYProperty()));
				}
			}

		}
		// System.out.println("Group: " + g.getChildren());

		return g;
	}

	public void addInternalEdges(
			UndirectedSparseMultigraph<Circle, Line> graph, Group selectedGroup) {
		for (Line l : graph.getEdges()) {

			Pair<Circle> endpoints = graph.getEndpoints(l);
			Circle start = endpoints.getFirst();
			Circle end = endpoints.getSecond();

			if (selectedNodes.contains(start) && selectedNodes.contains(end)) {
				selectedGroup.getChildren().add(l);
			}
		}
	}

	public Rectangle drawRectangle() {
		return RectangleBuilder.create().x(anchorX).y(anchorY)
				.stroke(selectionStrokeColor).fill(selectionFillColor).build();
	}

	public Rectangle getSelectionRectangle() {
		return selectionRectangle;
	}

	public void checkCollision(Rectangle rect, Group root) {
		ObservableList<Node> children = root.getChildren();
		for (Node n : children) {
			Bounds result = root.localToScene(n.getBoundsInParent());
			Bounds rectBounds = rect.getBoundsInParent();
			if (rectBounds.intersects(result)) {
				if (n.getClass().getSimpleName().equalsIgnoreCase("Group")) {
					checkCollision(rect, (Group) n);
				} else {
					if (n.getClass().getSimpleName().equalsIgnoreCase("Circle")) {
						selectedNodes.add((Circle) n);
						paintSelection((Circle) n);
					}
				}
			} else {
				if (n.getClass().getSimpleName().equalsIgnoreCase("Circle")) {
					paintDefaultSelection((Circle) n);
				}
			}
		}
	}

	public void resetSelection() {
		for (Circle c : selectedNodes) {
			paintDefaultSelection(c);
		}
		selectedNodes.clear();
	}

	public void paintSelection(Circle c) {
		c.setFill(selectedNodeColor);
	}

	public void paintDefaultSelection(Circle c) {
		c.setFill(defaultNodeColor);
	}

	public Group getSelectedGroup() {
		return selectedGroup;
	}

}
