package javafxapplication1;

import java.awt.Dimension;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Scale;
import javafx.util.Duration;
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

	private Pane canvas;
	private Rectangle selectionRectangle;
	private double anchorX, anchorY;
	private Color selectionStrokeColor = Color.web("#FF883E");
	private Color selectionFillColor = Color.web("#FF883E", 0.1);
	private ArrayList<Circle> selectedNodes = new ArrayList<>();
	private static final Class[] layoutClasses = new Class[] { CircleLayout.class,
			SpringLayout.class, FRLayout.class, KKLayout.class,
			StaticLayout.class };
	private UndirectedSparseMultigraph<Circle, Line> graph;
	private Scale scaleX, scaleY;
	private Group selectedGroup;
	private Color defaultNodeColor = Color.web("#5C4B51");
	private Color selectedNodeColor = Color.web("F06060");
	private Selection selectionGroup;

	public UDGraphLayoutController(
			UndirectedSparseMultigraph<Circle, Line> graph, Pane canvas) {
		this.canvas = canvas;
		anchorX = 0;
		anchorY = 0;
		selectionRectangle = null;
		selectedNodes = new ArrayList<>();
		this.graph = graph;
	}

	/**
	 * Get the list of selected circles
	 * @return
	 */
	public ArrayList<Circle> getSelectedNodes() {
		return selectedNodes;
	}

	/**
	 * Get the list of layout classes
	 * @return
	 */
	public static Class[] getLayoutClasses() {
		return layoutClasses;
	}

	/**
	 * Handle a KEY_PRESSED event.
	 * @param e the {@code KeyEvent} to be handled
	 */
	public void handleKeyPressed(KeyEvent e) {
		// Clear the selection of ESC is pressed.
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

		// Prepare scale objects in case boundary is opening to the left or up
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
	}

	public void handleMouseReleased(MouseEvent e) {
		if (!selectedNodes.isEmpty() && selectionRectangle != null) {
			
			selectionGroup = createSelectionGroup(selectedNodes,
					selectionRectangle, graph);
			
			
			// attach the group to the canvas
			selectedGroup = selectionGroup.getRoot();
			canvas.getChildren().add(0, selectedGroup);
			
			// save the position of the group in parent
			Point2D point = new Point2D(selectedGroup.getBoundsInParent().getMinX(),
			selectedGroup.getBoundsInParent().getMinY());
						
			// correct the circles' position
			selectionGroup.correctCirclePositions();
			
			// correct the position of the group
			selectedGroup.setLayoutX(point.getX());
			selectedGroup.setLayoutY(point.getY());
			
			System.out.println("LayoutX/Y after creation:" + selectedGroup.getLayoutX() + ", " + selectedGroup.getLayoutY());
			canvas.getChildren().remove(selectionRectangle);
		}
	}

	/**
	 * Handle layout button action
	 * @param e ActionEvent to handle
	 */
	public void handleLayoutButtonAction(ActionEvent e) {
		Button b = (Button) e.getSource();
		Class c = (Class) b.getUserData();

		// generate the graph from the selection
		try {
			if (!selectedNodes.isEmpty()) {
				Point2D point = new Point2D(selectionGroup.getRoot().getBoundsInParent().getMinX(), 
						selectionGroup.getRoot().getBoundsInParent().getMinY() );
				
				generateLayoutFromSelection(selectedNodes, graph, c, selectionGroup.getBackground());
				
				selectionGroup.getRoot().relocate(point.getX(), point.getY());
				
				canvas.getChildren().remove(selectionRectangle);
			}

		} catch (Exception ex) {
			Logger.getLogger(JavaFXApplication1.class.getName()).log(
					Level.SEVERE, null, ex);
		}
	}

	/**
	 * Generate the layout for a group of selected nodes
	 * @param picked nodes
	 * @param graph to create the subgraph of selected nodes to layout
	 * @param layout type to use use
	 * @param selectedRect to use as layout dimension
	 * @return
	 * @throws Exception
	 */
	public UndirectedSparseMultigraph<Circle, Line> generateLayoutFromSelection(
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

		Layout<Circle, Line> subLayout = getLayoutFor(layout, subGraph);

		int size = (int) Math.min(selectedRect.getWidth(),
				selectedRect.getHeight());
		Dimension d = new Dimension(size, size);
		BasicVisualizationServer<Circle, Line> vv = new BasicVisualizationServer<Circle, Line>(
				subLayout, d);

		Collection<Circle> vertices = subGraph.getVertices();

		for (Circle c : vertices) {
			c.setCenterX(subLayout.transform(c).getX());
			c.setCenterY(subLayout.transform(c).getY());
		}

		return subGraph;
	}

	/**
	 * Instantiate the given layout
	 * @param layoutClass to instantiate
	 * @param graph to pass into the layout constructor
	 * @return the layout
	 * @throws Exception
	 */
	private Layout getLayoutFor(Class layoutClass, Graph graph)
			throws Exception {
		Object[] args = new Object[] { graph };
		@SuppressWarnings("unchecked")
		Constructor constructor = layoutClass
				.getConstructor(new Class[] { Graph.class });
		return (Layout) constructor.newInstance(args);
	}

	public Selection createSelectionGroup(final ArrayList<Circle> selectedNode,
			Rectangle selectionRect,
			UndirectedSparseMultigraph<Circle, Line> graph) {
		// Create a group to hold the selection and background
		Group g = new Group();
		
		// create group for the graph nodes
		final Group graphNodes = new Group();

		// draw a background for the selection
		final Rectangle bg = RectangleBuilder.create().x(0)
				.y(0).width(selectionRect.getWidth())
				.height(selectionRect.getHeight()).fill(selectionFillColor)
				.stroke(selectionStrokeColor).opacity(1).build();

		// add the background to the selection group
		g.getChildren().add(bg);
		// add the graph nodes to the selection group
		g.getChildren().add(graphNodes);

		bg.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				fadeTransition(bg, 250, bg.getOpacity(), 1);
				for (Circle c : getNodes(graphNodes)) {
					fillTransition(c, 250, (Color) c.getFill(), selectedNodeColor);
				}
			}
		});

		bg.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				fadeTransition(bg, 500, bg.getOpacity(), 0);
				for (Circle c : getNodes(graphNodes)) {
					fillTransition(c, 500, (Color) c.getFill(), defaultNodeColor);
				}
			}
		});

		// drive the background's size with the bounds of the graphnodes
		graphNodes.boundsInParentProperty().addListener(
				new ChangeListener<Bounds>() {
					@Override
					public void changed(
							ObservableValue<? extends Bounds> observable,
							Bounds oldValue, Bounds newValue) {

						bg.setX(newValue.getMinX());
						bg.setY(newValue.getMinY());

						bg.setWidth(newValue.getWidth());
						bg.setHeight(newValue.getHeight());

					}
				});
		
		// Convert the points from the canvas coordinate system to the group coords
		// The parent of g is the canvas.
		for (Circle c : selectedNode) {
			if (c.getParent() == canvas) {
				Point2D parentToLocal = g.parentToLocal(c.getCenterX(),
						c.getCenterY());
				c.setCenterX(parentToLocal.getX());
				c.setCenterY(parentToLocal.getY());
			}else{
				Parent circleParent = c.getParent();
				Point2D localToScene = circleParent.localToScene(c.getCenterX(), c.getCenterY());
				Point2D sceneToLocal = g.sceneToLocal(localToScene);
				c.setCenterX(sceneToLocal.getX());
				c.setCenterY(sceneToLocal.getY());
			}
		};
		
		// move the graph nodes to the group
		canvas.getChildren().removeAll(selectedNodes);
		graphNodes.getChildren().addAll(selectedNode);

		addInternalEdges(graph, graphNodes);

		// make the group draggable
		GroupMoveHandler groupMoveEventHandler = new GroupMoveHandler();
		g.setOnMousePressed(groupMoveEventHandler);
		g.setOnMouseDragged(groupMoveEventHandler);
		g.setOnMouseReleased(groupMoveEventHandler);

		for (Line l : graph.getEdges()) {

			Pair<Circle> endpoints = graph.getEndpoints(l);
			Circle start = endpoints.getFirst();
			Circle end = endpoints.getSecond();

			// between two vertexes in the group
			if (selectedNodes.contains(start) && selectedNodes.contains(end)) {
				l.startXProperty().bind(start.centerXProperty());
				l.startYProperty().bind(start.centerYProperty());
				l.endXProperty().bind(end.centerXProperty());
				l.endYProperty().bind(end.centerYProperty());
			} else {
				// determine if one end of the line is connected
				if (selectedNodes.contains(start)) {
					
					if(l.getParent() != canvas){
						Group parent = (Group) l.getParent();
						parent.getChildren().remove(l);
						canvas.getChildren().add(l);	
						
						Parent endParent = end.getParent().getParent();
						
						System.out.println(endParent.layoutXProperty().toString());
						l.endXProperty().bind(
								end.centerXProperty().add(endParent.layoutXProperty()));
						l.endYProperty().bind(
								end.centerYProperty().add(endParent.layoutYProperty()));
						
					}
					
					l.startXProperty().bind(
							start.centerXProperty().add(g.layoutXProperty()));
					l.startYProperty().bind(
							start.centerYProperty().add(g.layoutYProperty()));
				}
				if (selectedNodes.contains(end)) {
					
					if(l.getParent() != canvas){
						Group parent = (Group) l.getParent();
						parent.getChildren().remove(l);
						canvas.getChildren().add(l);
						
						Parent startParent = start.getParent().getParent();
						
						System.out.println(startParent.layoutXProperty().toString());
						l.startXProperty().bind(
								start.centerXProperty().add(startParent.layoutXProperty()));
						l.startYProperty().bind(
								start.centerYProperty().add(startParent.layoutYProperty()));
						
					}
					
					l.endXProperty().bind(
							end.centerXProperty().add(g.layoutXProperty()));
					l.endYProperty().bind(
							end.centerYProperty().add(g.layoutYProperty()));
				}
			}
		}

		Selection selection = new Selection(g, graphNodes, bg, selectedNodes);

		return selection;
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

	public void checkCollision(Rectangle rect, Parent root) {
		ObservableList<Node> children = root.getChildrenUnmodifiable();
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

	// Method to fade out the shape
	public void fadeTransition(Shape shape, int time, double startOpacity,
			double endOpacity) {
		FadeTransition ft = new FadeTransition(Duration.millis(time), shape);
		ft.setFromValue(startOpacity);
		ft.setToValue(endOpacity);
		ft.play();
	}

	// Method to animate the color of the nodes
	public void fillTransition(Shape s, int time, Color initialColor, Color finalColor) {
		FillTransition ft = new FillTransition(Duration.millis(time), s,
				initialColor, finalColor);
		ft.play();
	}
	
	// Get all the circles in a group.
	public ArrayList<Circle> getNodes (Group g) {
		ArrayList<Circle> returnedArray = new ArrayList<>();
		ObservableList<Node> children = g.getChildren();
		for (Node n : children) {
			if (n.getClass().getSimpleName().equalsIgnoreCase("Group")) {
				getNodes((Group) n);
			} else {
				if (n.getClass().getSimpleName().equalsIgnoreCase("Circle")) {
					returnedArray.add((Circle) n);
				}
			}
		}
		return returnedArray;
	}

	/**
	 * Reset the selectNodes colors and clear the selected items list
	 */
	public void resetSelection() {
		for (Circle c : selectedNodes) {
			paintDefaultSelection(c);
		}
		selectedNodes.clear();
	}

	/**
	 * Paint a circle with the selected color
	 * @param c
	 */
	public void paintSelection(Circle c) {
		c.setFill(selectedNodeColor);
	}

	/**
	 * Paint a circle the default color
	 * @param c
	 */
	public void paintDefaultSelection(Circle c) {
		c.setFill(defaultNodeColor);
	}

	public Group getSelectedGroup() {
		return selectedGroup;
	}

}
