package javafxapplication1;

import java.awt.Dimension;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

public class SelectionRectangle {

	private Rectangle topLeft, topMid, topRight, right, bottomRight, bottomMid, bottomLeft, left;
	private int anchorSize = 10;
	private Color anchorColor = Color.BLACK;
	private double initY;
	private Rectangle rect;
	private Color selectionStrokeColor = Color.web("#FF883E");
	private Color selectionFillColor = Color.web("#FF883E", 0.1);
	private double initX;
	private double rectWidth;
	private double rectHeight;
	private Image NE_SW = new Image(getClass().getResourceAsStream(
			"resources/ne_sw_corner.png"));
	private Image NW_SE = new Image(getClass().getResourceAsStream(
			"resources/nw_se_corner.png"));
	private Group graphNodes;
	private Color selectedNodeColor = Color.web("F06060");
	private ArrayList<Circle> selectedNodes;
	private Selection selectionGroup;
	private UndirectedSparseMultigraph<Circle, Line> graph;
	private Class c;

	public SelectionRectangle(Rectangle rect, Group graphNodes, ArrayList<Circle> selectedNodes, Selection selectionGroup, UndirectedSparseMultigraph<Circle, Line> graph, Class c, Pane canvas) {

		this.rect = rect;
		this.graphNodes = graphNodes;
		this.selectedNodes = selectedNodes;
		this.selectionGroup = selectionGroup;
		this.graph = graph;
		this.c = c;

		// Creating the 8 anchor points
		left = RectangleBuilder.create().x(rect.getX() - anchorSize / 2)
				.y(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2)
				.width(anchorSize).height(anchorSize * 3).fill(anchorColor)
				.build();

		topLeft = RectangleBuilder.create().x(rect.getX() - anchorSize / 2)
				.y(rect.getY() - anchorSize / 2).width(anchorSize)
				.height(anchorSize).fill(anchorColor).build();

		bottomLeft = RectangleBuilder.create().x(rect.getX() - anchorSize / 2)
				.y(rect.getY() + rect.getHeight() - anchorSize / 2)
				.width(anchorSize).height(anchorSize).fill(anchorColor).build();

		topMid = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2)
				.y(rect.getY() - anchorSize / 2).width(anchorSize * 3)
				.height(anchorSize).fill(anchorColor).build();

		bottomMid = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2)
				.y(rect.getY() + rect.getHeight() - anchorSize / 2)
				.width(anchorSize * 3).height(anchorSize).fill(anchorColor)
				.build();

		topRight = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth() - anchorSize / 2)
				.y(rect.getY() - anchorSize / 2).width(anchorSize)
				.height(anchorSize).fill(anchorColor).build();

		right = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth() - anchorSize / 2)
				.y(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2)
				.width(anchorSize).height(anchorSize * 3).fill(anchorColor)
				.build();

		bottomRight = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth() - anchorSize / 2)
				.y(rect.getY() + rect.getHeight() - anchorSize / 2)
				.width(anchorSize).height(anchorSize).fill(anchorColor).build();
		
		// Add eventhandlers to the anchor points
		topLeft.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleTopLeftAnchorMouseEntered(event);
			}
		});

		topLeft.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleTopLeftAnchorMousePressed(event);
			}
		});

		topLeft.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleTopLeftAnchorMouseDragged(event);
			}
		});

		topMid.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleTopMidAnchorMouseEntered(event);
			}
		});

		topMid.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleTopMidAnchorMousePressed(event);
			}
		});

		topMid.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleTopMidAnchorMouseDragged(event);
			}
		});

		topRight.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleTopRightAnchorMouseEntered(event);
			}
		});

		topRight.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleTopRightAnchorMousePressed(event);
			}
		});

		topRight.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleTopRightAnchorMouseDragged(event);
			}
		});

		right.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleRightAnchorMouseEntered(event);
			}
		});

		right.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleRightAnchorMousePressed(event);
			}
		});

		right.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleRightAnchorMouseDragged(event);
			}
		});

		bottomRight.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleBottomRightAnchorMouseEntered(event);
			}
		});

		bottomRight.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleBottomRightAnchorMousePressed(event);
			}
		});

		bottomRight.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleBottomRightAnchorMouseDragged(event);
			}
		});

		bottomMid.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleBottomMidAnchorMouseEntered(event);
			}
		});

		bottomMid.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleBottomMidAnchorMousePressed(event);
			}
		});

		bottomMid.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleBottomMidAnchorMouseDragged(event);
			}
		});

		bottomLeft.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleBottomLeftAnchorMouseEntered(event);
			}
		});

		bottomLeft.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleBottomLeftAnchorMousePressed(event);
			}
		});

		bottomLeft.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleBottomLeftAnchorMouseDragged(event);
			}
		});

		left.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleLeftAnchorMouseEntered(event);
			}
		});

		left.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleLeftAnchorMousePressed(event);
			}
		});

		left.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleLeftAnchorMouseDragged(event);
			}
		});

		// Bind the anchor points
		topRight.xProperty().bind(right.xProperty());
		bottomRight.xProperty().bind(right.xProperty());

		bottomLeft.yProperty().bind(bottomMid.yProperty());
		bottomRight.yProperty().bind(bottomMid.yProperty());

		topLeft.yProperty().bind(topMid.yProperty());
		topRight.yProperty().bind(topMid.yProperty());

		topLeft.xProperty().bind(left.xProperty());
		bottomLeft.xProperty().bind(left.xProperty());
	}

	// Top left anchor eventhandler
	public void handleTopLeftAnchorMousePressed(MouseEvent event) {
		initY = event.getY();
		rectWidth = rect.getWidth();
		rectHeight = rect.getHeight();
		event.consume();
	}

	public void handleTopLeftAnchorMouseEntered(MouseEvent event) {
		animate();
		topLeft.setCursor(new ImageCursor(NW_SE, 5, 5));
		event.consume();
	}

	public void handleTopLeftAnchorMouseDragged(MouseEvent event) {
		topMid.setY(event.getY());
		left.setX(event.getX());
		rect.setY(topLeft.getY());
		rect.setX(topLeft.getX());
		rect.setWidth(right.getX() - event.getX());
		rect.setHeight(bottomMid.getY() - event.getY());

		topMid.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		topMid.setY(rect.getY() - anchorSize / 2);

		left.setX(rect.getX() - anchorSize / 2);
		left.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);

		right.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);
		
		bottomMid.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		
		resize();

		event.consume();
	}

	// Top mid anchor eventhandler
	public void handleTopMidAnchorMousePressed(MouseEvent event) {
		initY = event.getY();
		rectHeight = rect.getHeight();
		event.consume();
	}

	public void handleTopMidAnchorMouseEntered(MouseEvent event) {
		animate();
		topMid.setCursor(Cursor.V_RESIZE);
		event.consume();
	}

	public void handleTopMidAnchorMouseDragged(MouseEvent event) {
		topMid.setY(event.getY());
		rect.setY(topMid.getY());
		rect.setHeight(bottomLeft.getY() - topLeft.getY());

		topMid.setY(rect.getY() - anchorSize / 2);
		right.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);
		left.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);
		resize();
		event.consume();
	}

	// Top right anchor eventhandler
	public void handleTopRightAnchorMousePressed(MouseEvent event) {
		initY = event.getY();
		rectHeight = rect.getHeight();
		event.consume();
	}

	public void handleTopRightAnchorMouseEntered(MouseEvent event) {
		animate();
		topRight.setCursor(new ImageCursor(NE_SW, 5, 5));
		event.consume();
	}

	public void handleTopRightAnchorMouseDragged(MouseEvent event) {
		topMid.setY(event.getY());
		right.setX(event.getX());
		rect.setY(topRight.getY());
		rect.setWidth(event.getX() - topLeft.getX());
		rect.setHeight(bottomLeft.getY() - event.getY());

		topMid.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		topMid.setY(rect.getY() - anchorSize / 2);

		right.setX(rect.getX() + rect.getWidth() - anchorSize / 2);
		right.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);

		left.setX(rect.getX() - anchorSize / 2);
		left.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);

		bottomMid.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		
		resize();
		event.consume();
	}

	// Right anchor eventhandler
	public void handleRightAnchorMousePressed(MouseEvent event) {
		initX = event.getX();
		rectWidth = rect.getWidth();
		event.consume();
	}

	public void handleRightAnchorMouseEntered(MouseEvent event) {
		animate();
		right.setCursor(Cursor.H_RESIZE);
		event.consume();
	}

	public void handleRightAnchorMouseDragged(MouseEvent event) {
		right.setX(event.getX());
		rect.setWidth(rectWidth + right.getX() - initX);

		right.setX(rect.getX() + rect.getWidth() - anchorSize / 2);
		topMid.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		bottomMid.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		resize();
		event.consume();
	}

	// Bottom right anchor eventhandler
	public void handleBottomRightAnchorMousePressed(MouseEvent event) {
		initY = event.getY();
		rectHeight = rect.getHeight();
		event.consume();
	}

	public void handleBottomRightAnchorMouseEntered(MouseEvent event) {
		animate();
		bottomRight.setCursor(new ImageCursor(NW_SE, 5, 5));
		event.consume();
	}

	public void handleBottomRightAnchorMouseDragged(MouseEvent event) {
		bottomMid.setY(event.getY());
		right.setX(event.getX());
		rect.setWidth(right.getX() - left.getX());
		rect.setHeight(bottomMid.getY() - topMid.getY());

		topMid.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		topMid.setY(rect.getY() - anchorSize / 2);

		bottomMid.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		bottomMid.setY(rect.getY() + rect.getHeight() - anchorSize / 2);

		left.setX(rect.getX() - anchorSize / 2);
		left.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);

		right.setX(rect.getX() + rect.getWidth() - anchorSize / 2);
		right.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);
		resize();
		event.consume();
	}

	// Bottom mid anchor eventhandler
	public void handleBottomMidAnchorMousePressed(MouseEvent event) {
		initY = event.getY();
		rectHeight = rect.getHeight();
		event.consume();
	}

	public void handleBottomMidAnchorMouseEntered(MouseEvent event) {
		animate();
		bottomMid.setCursor(Cursor.V_RESIZE);
		event.consume();
	}

	public void handleBottomMidAnchorMouseDragged(MouseEvent event) {
		bottomMid.setY(event.getY());
		rect.setHeight(rectHeight + bottomMid.getY() - initY);

		bottomMid.setY(rect.getY() + rect.getHeight() - anchorSize / 2);
		right.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);
		left.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);
		resize();
		event.consume();
	}

	// Bottom left anchor eventhandler
	public void handleBottomLeftAnchorMousePressed(MouseEvent event) {
		initY = event.getY();
		rectHeight = rect.getHeight();
		event.consume();
	}

	public void handleBottomLeftAnchorMouseEntered(MouseEvent event) {
		animate();
		bottomLeft.setCursor(new ImageCursor(NE_SW, 5, 5));
		event.consume();
	}

	public void handleBottomLeftAnchorMouseDragged(MouseEvent event) {
		bottomMid.setY(event.getY());
		left.setX(event.getX());
		rect.setX(topLeft.getX());
		rect.setWidth(right.getX() - left.getX());
		rect.setHeight(bottomMid.getY() - topMid.getY());

		topMid.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		topMid.setY(rect.getY() - anchorSize / 2);

		bottomMid.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);

		left.setX(rect.getX() - anchorSize / 2);
		left.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);

		right.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);
		resize();
		event.consume();
	}

	// Left anchor eventhandler
	public void handleLeftAnchorMousePressed(MouseEvent event) {
		initY = event.getY();
		event.consume();
	}

	public void handleLeftAnchorMouseEntered(MouseEvent event) {
		animate();
		left.setCursor(Cursor.H_RESIZE);
		event.consume();
	}

	public void handleLeftAnchorMouseDragged(MouseEvent event) {
		left.setX(event.getX());
		rect.setX(left.getX());
		rect.setWidth(bottomRight.getX() - bottomLeft.getX());

		left.setX(rect.getX() - anchorSize / 2);
		topMid.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		bottomMid.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		resize();
		event.consume();
	}

	// Get all the anchor points in an ArrayList
	public ArrayList<Rectangle> getAnchors() {
		ArrayList<Rectangle> arrayList = new ArrayList<>();
		arrayList.add(topLeft);
		arrayList.add(topMid);
		arrayList.add(topRight);
		arrayList.add(right);
		arrayList.add(bottomRight);
		arrayList.add(bottomMid);
		arrayList.add(bottomLeft);
		arrayList.add(left);
		return arrayList;
	}

	// Animate the anchor points to make sure they're not hidden
	public void animate() {
		fadeTransition(rect, 250, rect.getOpacity(), 1);
		for (Circle c : getNodes(graphNodes)) {
			fillTransition(c, 250, (Color) c.getFill(), selectedNodeColor);
		}
		for (Rectangle r : getAnchors()) {
			fadeTransition(r, 250, r.getOpacity(), 1);
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
	public void fillTransition(Shape s, int time, Color initialColor,
			Color finalColor) {
		FillTransition ft = new FillTransition(Duration.millis(time), s,
				initialColor, finalColor);
		ft.play();
	}

	// Get all the circles in a group.
	public ArrayList<Circle> getNodes(Group g) {
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

	// Update the sublayout
	public void resize() {
		try {
			if (!selectedNodes.isEmpty()) {
				if (c != null) {
					Point2D point = new Point2D(selectionGroup.getRoot()
							.getBoundsInParent().getMinX(), selectionGroup
							.getRoot().getBoundsInParent().getMinY());

					generateLayoutFromSelection(selectedNodes, graph, c,
							rect);
					

					selectionGroup.getRoot().relocate(point.getX(), point.getY());
					rect.setWidth(right.getX() - left.getWidth());
					rect.setHeight(bottomMid.getY() - topMid.getY());
				}
			}

		} catch (Exception ex) {
			Logger.getLogger(JavaFXApplication1.class.getName()).log(
					Level.SEVERE, null, ex);
		}
	}
	
	// Generate a graph for the picked vertices
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

			@SuppressWarnings("unchecked")
			Layout<Circle, Line> subLayout = getLayoutFor(layout, subGraph);

			int size = (int) Math.min(selectedRect.getWidth(),
					selectedRect.getHeight());
			Dimension d = new Dimension(size, size);
			new BasicVisualizationServer<Circle, Line>(
					subLayout, d);

			Collection<Circle> vertices = subGraph.getVertices();

			for (Circle c : vertices) {
				c.setCenterX(subLayout.transform(c).getX());
				c.setCenterY(subLayout.transform(c).getY());
			}

			return subGraph;
		}

		// Get layout for a given graph
		private Layout getLayoutFor(Class layoutClass, Graph graph)
				throws Exception {
			Object[] args = new Object[] { graph };
			@SuppressWarnings("unchecked")
			Constructor constructor = layoutClass
					.getConstructor(new Class[] { Graph.class });
			return (Layout) constructor.newInstance(args);
		}

}
