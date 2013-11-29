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

	private Rectangle tl, tm, tr, r, br, bm, bl, l;
	private int anchorSize = 10;
	private Color anchorColor = Color.BLACK;
	private Rectangle rect;
	private double initY;
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


		l = RectangleBuilder.create().x(rect.getX() - anchorSize / 2)
				.y(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2)
				.width(anchorSize).height(anchorSize * 3).fill(anchorColor)
				.build();

		tl = RectangleBuilder.create().x(rect.getX() - anchorSize / 2)
				.y(rect.getY() - anchorSize / 2).width(anchorSize)
				.height(anchorSize).fill(anchorColor).build();

		bl = RectangleBuilder.create().x(rect.getX() - anchorSize / 2)
				.y(rect.getY() + rect.getHeight() - anchorSize / 2)
				.width(anchorSize).height(anchorSize).fill(anchorColor).build();

		tm = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2)
				.y(rect.getY() - anchorSize / 2).width(anchorSize * 3)
				.height(anchorSize).fill(anchorColor).build();

		bm = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2)
				.y(rect.getY() + rect.getHeight() - anchorSize / 2)
				.width(anchorSize * 3).height(anchorSize).fill(anchorColor)
				.build();

		tr = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth() - anchorSize / 2)
				.y(rect.getY() - anchorSize / 2).width(anchorSize)
				.height(anchorSize).fill(anchorColor).build();

		r = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth() - anchorSize / 2)
				.y(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2)
				.width(anchorSize).height(anchorSize * 3).fill(anchorColor)
				.build();

		br = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth() - anchorSize / 2)
				.y(rect.getY() + rect.getHeight() - anchorSize / 2)
				.width(anchorSize).height(anchorSize).fill(anchorColor).build();

		tl.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleTopLeftAnchorMouseEntered(event);
			}
		});

		tl.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleTopLeftAnchorMousePressed(event);
			}
		});

		tl.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleTopLeftAnchorMouseDragged(event);
			}
		});

		tm.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleTopMidAnchorMouseEntered(event);
			}
		});

		tm.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleTopMidAnchorMousePressed(event);
			}
		});

		tm.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleTopMidAnchorMouseDragged(event);
			}
		});

		tr.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleTopRightAnchorMouseEntered(event);
			}
		});

		tr.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleTopRightAnchorMousePressed(event);
			}
		});

		tr.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleTopRightAnchorMouseDragged(event);
			}
		});

		r.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleRightAnchorMouseEntered(event);
			}
		});

		r.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleRightAnchorMousePressed(event);
			}
		});

		r.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleRightAnchorMouseDragged(event);
			}
		});

		br.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleBottomRightAnchorMouseEntered(event);
			}
		});

		br.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleBottomRightAnchorMousePressed(event);
			}
		});

		br.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleBottomRightAnchorMouseDragged(event);
			}
		});

		bm.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleBottomMidAnchorMouseEntered(event);
			}
		});

		bm.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleBottomMidAnchorMousePressed(event);
			}
		});

		bm.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleBottomMidAnchorMouseDragged(event);
			}
		});

		bl.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleBottomLeftAnchorMouseEntered(event);
			}
		});

		bl.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleBottomLeftAnchorMousePressed(event);
			}
		});

		bl.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleBottomLeftAnchorMouseDragged(event);
			}
		});

		l.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleLeftAnchorMouseEntered(event);
			}
		});

		l.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleLeftAnchorMousePressed(event);
			}
		});

		l.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				handleLeftAnchorMouseDragged(event);
			}
		});

		tr.xProperty().bind(r.xProperty());
		br.xProperty().bind(r.xProperty());

		bl.yProperty().bind(bm.yProperty());
		br.yProperty().bind(bm.yProperty());

		tl.yProperty().bind(tm.yProperty());
		tr.yProperty().bind(tm.yProperty());

		tl.xProperty().bind(l.xProperty());
		bl.xProperty().bind(l.xProperty());
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
		tl.setCursor(new ImageCursor(NW_SE, 5, 5));
		event.consume();
	}

	public void handleTopLeftAnchorMouseDragged(MouseEvent event) {
		tm.setY(event.getY());
		l.setX(event.getX());
		rect.setY(tl.getY());
		rect.setX(tl.getX());
		rect.setWidth(r.getX() - event.getX());
		rect.setHeight(bm.getY() - event.getY());

		tm.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		tm.setY(rect.getY() - anchorSize / 2);

		l.setX(rect.getX() - anchorSize / 2);
		l.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);

		r.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);
		//
		bm.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		
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
		tm.setCursor(Cursor.V_RESIZE);
		event.consume();
	}

	public void handleTopMidAnchorMouseDragged(MouseEvent event) {
		tm.setY(event.getY());
		rect.setY(tm.getY());
		rect.setHeight(bl.getY() - tl.getY());

		tm.setY(rect.getY() - anchorSize / 2);
		r.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);
		l.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);
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
		tr.setCursor(new ImageCursor(NE_SW, 5, 5));
		event.consume();
	}

	public void handleTopRightAnchorMouseDragged(MouseEvent event) {
		tm.setY(event.getY());
		r.setX(event.getX());
		rect.setY(tr.getY());
		rect.setWidth(event.getX() - tl.getX());
		rect.setHeight(bl.getY() - event.getY());

		tm.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		tm.setY(rect.getY() - anchorSize / 2);

		r.setX(rect.getX() + rect.getWidth() - anchorSize / 2);
		r.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);

		l.setX(rect.getX() - anchorSize / 2);
		l.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);

		bm.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		
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
		r.setCursor(Cursor.H_RESIZE);
		event.consume();
	}

	public void handleRightAnchorMouseDragged(MouseEvent event) {
		r.setX(event.getX());
		rect.setWidth(rectWidth + r.getX() - initX);

		r.setX(rect.getX() + rect.getWidth() - anchorSize / 2);
		tm.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		bm.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
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
		br.setCursor(new ImageCursor(NW_SE, 5, 5));
		event.consume();
	}

	public void handleBottomRightAnchorMouseDragged(MouseEvent event) {
		bm.setY(event.getY());
		r.setX(event.getX());
		rect.setWidth(r.getX() - l.getX());
		rect.setHeight(bm.getY() - tm.getY());

		tm.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		tm.setY(rect.getY() - anchorSize / 2);

		bm.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		bm.setY(rect.getY() + rect.getHeight() - anchorSize / 2);

		l.setX(rect.getX() - anchorSize / 2);
		l.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);

		r.setX(rect.getX() + rect.getWidth() - anchorSize / 2);
		r.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);
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
		bm.setCursor(Cursor.V_RESIZE);
		event.consume();
	}

	public void handleBottomMidAnchorMouseDragged(MouseEvent event) {
		bm.setY(event.getY());
		rect.setHeight(rectHeight + bm.getY() - initY);

		bm.setY(rect.getY() + rect.getHeight() - anchorSize / 2);
		r.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);
		l.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);
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
		bl.setCursor(new ImageCursor(NE_SW, 5, 5));
		event.consume();
	}

	public void handleBottomLeftAnchorMouseDragged(MouseEvent event) {
		bm.setY(event.getY());
		l.setX(event.getX());
		rect.setX(tl.getX());
		rect.setWidth(r.getX() - l.getX());
		rect.setHeight(bm.getY() - tm.getY());

		tm.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		tm.setY(rect.getY() - anchorSize / 2);

		bm.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);

		l.setX(rect.getX() - anchorSize / 2);
		l.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);

		r.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);
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
		l.setCursor(Cursor.H_RESIZE);
		event.consume();
	}

	public void handleLeftAnchorMouseDragged(MouseEvent event) {
		l.setX(event.getX());
		rect.setX(l.getX());
		rect.setWidth(br.getX() - bl.getX());

		l.setX(rect.getX() - anchorSize / 2);
		tm.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		bm.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		resize();
		event.consume();
	}

	public ArrayList<Rectangle> getAnchors() {
		ArrayList<Rectangle> arrayList = new ArrayList<>();
		arrayList.add(tl);
		arrayList.add(tm);
		arrayList.add(tr);
		arrayList.add(r);
		arrayList.add(br);
		arrayList.add(bm);
		arrayList.add(bl);
		arrayList.add(l);
		return arrayList;
	}

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

	public void resize() {
		// generate the graph from the selection
		try {
			if (!selectedNodes.isEmpty()) {
				if (c != null) {
					Point2D point = new Point2D(selectionGroup.getRoot()
							.getBoundsInParent().getMinX(), selectionGroup
							.getRoot().getBoundsInParent().getMinY());

					generateLayoutFromSelection(selectedNodes, graph, c,
							selectionGroup.getBackground());
					

					selectionGroup.getRoot().relocate(point.getX(), point.getY());
					selectionGroup.getBackground().setWidth(r.getX() - l.getWidth());
					selectionGroup.getBackground().setHeight(bm.getY() - tm.getY());
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

		private Layout getLayoutFor(Class layoutClass, Graph graph)
				throws Exception {
			Object[] args = new Object[] { graph };
			@SuppressWarnings("unchecked")
			Constructor constructor = layoutClass
					.getConstructor(new Class[] { Graph.class });
			return (Layout) constructor.newInstance(args);
		}

}
