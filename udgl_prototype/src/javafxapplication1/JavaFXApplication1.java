/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxapplication1;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
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

import com.javafx.experiments.scenicview.ScenicView;
import com.sun.javafx.scene.web.skin.ColorPicker;
import com.sun.webpane.platform.BackForwardList.Entry;

import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
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
import javafx.scene.control.Dialogs;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineBuilder;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.util.Duration;

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
	 * @param args
	 *            the command line arguments
	 */
	private static final int CIRCLE_SIZE = 15;
	private static final ArrayList<Circle> PICKED = new ArrayList<Circle>(); // Circles
																				// that
																				// are
																				// selected
	private static final Color LENS_TINT = Color.WHITE.deriveColor(0, 1, 1,
			0.15);
	private static final Color MASK_TINT = Color.GREY
			.deriveColor(0, 1, 1, 0.97);

	@SuppressWarnings("rawtypes")
	private static Class[] layoutClasses = new Class[] { CircleLayout.class,
			SpringLayout.class, FRLayout.class, KKLayout.class,
			StaticLayout.class };

	private double posX = 0;
	private double posY = 0;
	private int sublayoutDimension = 200;
	BorderPane pane;
	Rectangle rect;
	SimpleDoubleProperty rectinitX = new SimpleDoubleProperty();
	SimpleDoubleProperty rectinitY = new SimpleDoubleProperty();
	SimpleDoubleProperty rectX = new SimpleDoubleProperty();
	SimpleDoubleProperty rectY = new SimpleDoubleProperty();
	Rectangle lensRect;
	Node stencil;
	int unit = 5;
	Color selectedCol = Color.web("F06060"); // Color for node when selected
	Color defaultCol = Color.web("#5C4B51"); // Color for node as default
	Color selectionToolCol = Color.web("#21AA9A"); // Color for the selection
													// tool (stroke)
	Color selectionToolColwO = Color.web("#21AA9A", 0.1); // Color for the
															// selection tool
															// (fill)
	Color lensFCol = Color.web("#8E3448"); // Color for the target selection
											// lens
	Color lensSCol = Color.web("#FF883E"); // Color for the selection lens
	Group subGroup;
	Pane subPane;

	double mPx, mPy;

	Text bullseye;

	Text posText;

	@SuppressWarnings("rawtypes")
	Class selectedClass;

	MoveContext moveContext = new MoveContext();

	double lensLayoutX, lensLayoutY = 0;

	Group moveableGroup;

	Group canvas;

	ArrayList<Map<Rectangle, UndirectedSparseMultigraph<Circle, Line>>> hashMapArray = new ArrayList<>();

	public static void main(String[] args) {
		launch(args);

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void start(final Stage stage) throws Exception {

		final BorderPane root = new BorderPane();

		canvas = new Group();

		final Scene scene = new Scene(root, 1200, 800, Color.WHITE);

		// Vbox to contain the UI controls
		final VBox vbox = new VBox();
		vbox.setPadding(new Insets(15, 12, 15, 12));
		vbox.setSpacing(10);
		vbox.setStyle("-fx-background-color: #736B68");

		root.setRight(vbox);
		root.setLeft(new Group(canvas));
		root.setMinWidth(Double.MIN_VALUE);
		root.setMaxWidth(Double.MAX_VALUE);
		root.setPrefWidth(1200);
		root.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		// Visualizing graph 1 using Jung
		Graph<String, Number> graph = TestGraphs.getDemoGraph();
		Layout<String, Number> layout = new CircleLayout<String, Number>(graph);
		final UndirectedSparseMultigraph<Circle, Line> newGraph = convertGraph(
				graph, layout);
		drawGraph(newGraph, canvas);

		// Help Button
		Button helpBtn = new Button("HELP");
		helpBtn.setPrefWidth(150);
		helpBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {
				Dialogs.showInformationDialog(
						stage,
						"- Press 'Esc' to deselect\n"
								+ "- Press 'S' to enable the Selection Lens\n"
								+ "- Press 'F' to enable the Target Selection Lens\n"
								+ "(only ONE selection lens can be operated at one time)\n\n"
								+ "There are two selection types:\n"
								+ "- Selection Lens (S): captures the nodes within its selection box.\n"
								+ "- Target Selection Lens (F): defines the size and the location of the sublayout would be made.\n\n"
								+ "Background filter can only be applied to Target Selection Lens",
						"How to control selection tool", "Help");
			}
		});

		// Control for lens width and height
		Image upIcon = new Image(getClass().getResourceAsStream(
				"resources/up.png"));
		Image downIcon = new Image(getClass().getResourceAsStream(
				"resources/down.png"));

		final Text lensWidth = TextBuilder.create().text("Lens Width: ")
				.font(Font.font("Verdana", 12)).fill(Color.WHITE).build();

		final Button lensWidthUp = new Button("", new ImageView(upIcon));
		lensWidthUp.setPrefWidth(25);

		final Button lensWidthDown = new Button("", new ImageView(downIcon));
		lensWidthDown.setPrefWidth(25);

		final Text lensHeight = TextBuilder.create().text("Lens Height: ")
				.font(Font.font("Verdana", 12)).fill(Color.WHITE).build();

		final Button lensHeightUp = new Button("", new ImageView(upIcon));
		lensHeightUp.setPrefWidth(25);

		final Button lensHeightDown = new Button("", new ImageView(downIcon));
		lensHeightDown.setPrefWidth(25);

		// Control for defining the sublayout size
		// The slider is used to control both width and height (Square)
		final Text subLayout = TextBuilder.create()
				.text("SubLayout: " + sublayoutDimension)
				.font(Font.font("Verdana", 12)).fill(Color.WHITE).build();

		final Slider slider = new Slider();
		slider.setMin(10);
		slider.setMax(1000);
		slider.setValue(200);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setBlockIncrement(5);
		slider.setSnapToTicks(true);

		slider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> ov, Number t,
					Number t1) {
				sublayoutDimension = t1.intValue();
				subLayout.setText("SubLayout: " + sublayoutDimension);

				@SuppressWarnings("unused")
				UndirectedSparseMultigraph<Circle, Line> genGraph = null;

				// Generate an undirected sparse multigraph with
				// selected layout
				try {
					genGraph = generateGraph(PICKED, newGraph, selectedClass,
							new Dimension(sublayoutDimension,
									sublayoutDimension));
				} catch (Exception e) {
					e.printStackTrace();
				}

				lensRect.setWidth(sublayoutDimension);
				lensRect.setHeight(sublayoutDimension);
			}
		});

		// Button to turn on background filter. Grey out the background except
		// for the lens.
		final ToggleButton backgroundFilter = new ToggleButton(
				"Background Filter");
		backgroundFilter.setPrefWidth(150);
		backgroundFilter.selectedProperty().addListener(
				new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> ov,
							Boolean t, Boolean t1) {
						if (t1) {
							final Rectangle mask = RectangleBuilder.create()
									.x(0).y(0).width(1200).height(800)
									.fill(Color.BLACK).build();

							lensRect.boundsInParentProperty().addListener(
									new ChangeListener<Bounds>() {
										@Override
										public void changed(
												ObservableValue<? extends Bounds> ov,
												Bounds t, Bounds t1) {
											applyStencil(
													cutStencil(mask, lensRect),
													pane);
										}
									});
						}
					}
				});

		// Color picker to controlt the stroke color of the lens
		final javafx.scene.control.ColorPicker colorPicker = new javafx.scene.control.ColorPicker();
		colorPicker.setPrefWidth(150);
		colorPicker.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub
				changeLensColor(lensRect, colorPicker.getValue());
			}
		});

		// sublayout target position
		posText = TextBuilder
				.create()
				.text("Pos X: " + Math.round(posX) + ", Pos Y: "
						+ Math.round(posY)).font(Font.font("Verdana", 12))
				.fill(Color.WHITE).build();

		bullseye = TextBuilder.create().text("X").fill(Color.GREY).build();

		// Lens selection toggle button
		Image selectionIcon = new Image(getClass().getResourceAsStream(
				"resources/select.png"));
		final ToggleButton selectionTool = new ToggleButton("", new ImageView(
				selectionIcon));
		selectionTool.setPrefWidth(150);
		selectionTool.setTooltip(new Tooltip("Press Esc to deselect"));
		selectionTool.selectedProperty().addListener(
				new ChangeListener<Boolean>() {
					@Override
					public void changed(ObservableValue<? extends Boolean> ov,
							Boolean t, Boolean t1) {
						if (!t) {

							pane = new BorderPane();

							scene.setOnMouseDragged(mouseSelection);
							scene.setOnMousePressed(mouseSelection);
							scene.setOnMouseReleased(mouseSelection);

							scene.addEventFilter(KeyEvent.KEY_PRESSED,
									new EventHandler<KeyEvent>() {
										@Override
										public void handle(KeyEvent t) {
											if (t.getCode() == KeyCode.ESCAPE) {
												System.out
														.println("ESC is pressed");

												for (Circle c : PICKED) {
													FillTransition ft = new FillTransition(
															Duration.millis(2000),
															c, selectedCol,
															defaultCol);
													ft.play();
												}

												selectionTool
														.setSelected(false);
												pane.getChildren().clear();

												// Remove eventhandlers from the
												// scene to reset the scene.
												scene.setOnMousePressed(null);
												scene.setOnMouseDragged(null);

												PICKED.clear();

												slider.setDisable(false);

												if (lensRect != null) {
													lensRect.setOpacity(0);
												}
												lensRect = null;

												backgroundFilter
														.setSelected(false);

											}
										}
									});

							rect = getNewRectangle();
							rect.widthProperty()
									.bind(rectX.subtract(rectinitX));
							rect.heightProperty().bind(
									rectY.subtract(rectinitY));
							pane.getChildren().add(rect);

							root.getChildren().add(pane);

						}
					}
				});

		scene.addEventFilter(KeyEvent.KEY_PRESSED,
				new EventHandler<KeyEvent>() {
					@Override
					public void handle(KeyEvent t) {

						if (t.getCode() == KeyCode.S) {

							selectionTool.setSelected(false);

							// Release eventhandler
							scene.setOnMouseDragged(null);
							scene.setOnMousePressed(null);

							lensRect = (Rectangle) pane.getChildren().get(1);
							drawSelectionLens(lensRect);

							checkCollision(lensRect, canvas);

							// The program would use the lens as a location for
							// the sublayout
							// unless specified anything else by right-clicking
							// on the canvas.

//							useLensPos();
							System.err.println(posX);
							System.err.println(posY);

							// Control the size of the lens

							lensWidth.setText("Lens Width: "
									+ lensRect.getWidth());
							lensHeight.setText("Lens Height: "
									+ lensRect.getHeight());

							// lensRect.setOnMouseEntered(new
							// EventHandler<MouseEvent>() {
							// @Override
							// public void handle(MouseEvent t) {
							//
							// if (!PICKED.isEmpty()) {
							// for (Circle c : PICKED) {
							// FillTransition ft = new FillTransition(
							// Duration.millis(1000), c,
							// defaultCol, selectedCol);
							// ft.play();
							// }
							// }
							//
							// if (lensRect.opacityProperty().get() != 1) {
							// fadeTransition(lensRect, 1000,
							// lensRect.getOpacity(), 1.0);
							// }
							// }
							// });

							// lensRect.setOnMouseExited(new
							// EventHandler<MouseEvent>() {
							// @Override
							// public void handle(MouseEvent t) {
							// if (lensRect.opacityProperty().get() != 0) {
							// fadeTransition(lensRect, 1000,
							// lensRect.getOpacity(), 0.0);
							// }
							//
							// if (!PICKED.isEmpty()) {
							// for (Circle c : PICKED) {
							// FillTransition ft = new FillTransition(
							// Duration.millis(1000), c,
							// selectedCol, defaultCol);
							// ft.play();
							// }
							// }
							// }
							// });

							lensWidthUp
									.setOnAction(new EventHandler<ActionEvent>() {
										@Override
										public void handle(ActionEvent t) {
											lensRect.setWidth(lensRect
													.getWidth() + unit);
											lensWidth.setText("Lens Width: "
													+ lensRect.getWidth());

											checkCollision(newGraph, lensRect);
										}
									});

							lensWidthDown
									.setOnAction(new EventHandler<ActionEvent>() {
										@Override
										public void handle(ActionEvent t) {
											lensRect.setWidth(lensRect
													.getWidth() - unit);
											lensWidth.setText("Lens Width: "
													+ lensRect.getWidth());

											checkCollision(newGraph, lensRect);
										}
									});

							lensHeightUp
									.setOnAction(new EventHandler<ActionEvent>() {
										@Override
										public void handle(ActionEvent t) {
											lensRect.setHeight(lensRect
													.getHeight() + unit);
											lensHeight.setText("Lens Height: "
													+ lensRect.getHeight());

											checkCollision(newGraph, lensRect);
										}
									});

							lensHeightDown
									.setOnAction(new EventHandler<ActionEvent>() {
										@Override
										public void handle(ActionEvent t) {
											lensRect.setHeight(lensRect
													.getHeight() - unit);
											lensHeight.setText("Lens Height: "
													+ lensRect.getHeight());

											checkCollision(newGraph, lensRect);
										}
									});

							// Move the selected nodes simultaneously
							moveableGroup = createMovableGroup(newGraph);

							ArrayList<Line> internalEdges = getInternalEdges(newGraph);
							for (Line l : internalEdges) {
								moveableGroup.getChildren().add(l);
							}

							canvas.getChildren().add(moveableGroup);

							System.out.println(moveableGroup.getChildren());

						}

						if (t.getCode() == KeyCode.F) {

							selectionTool.setSelected(false);

							// Release eventhandler
							scene.setOnMouseDragged(null);
							scene.setOnMousePressed(null);
							
							if (pane.getChildren().get(1) != null) {
								lensRect = (Rectangle) pane.getChildren().get(1);
								drawTargetSelectionLens(lensRect);
							}
							

							slider.setDisable(true);

							useLensPos();

							sublayoutDimension = (int) (lensRect.getWidth() / 1.5);

							lensRect.setOnMousePressed(new EventHandler<MouseEvent>() {
								@Override
								public void handle(MouseEvent t) {
									lensRect.setFill(Color.web("purple", 0.1));
								}
							});

							lensRect.setOnMouseDragged(new EventHandler<MouseEvent>() {
								@Override
								public void handle(MouseEvent t) {
									lensRect.setX(t.getX());
									lensRect.setY(t.getY());
								}
							});

							lensRect.setOnMouseReleased(new EventHandler<MouseEvent>() {
								@Override
								public void handle(MouseEvent t) {
									lensRect.setFill(Color.web("purple", 0));

									posX = lensRect.getX()
											+ lensRect.getWidth() / 2;
									posY = lensRect.getY()
											+ lensRect.getHeight() / 2;

									sublayoutDimension = (int) (lensRect
											.getWidth() / 1.5);
								}
							});

							// Control the size of the lens

							lensWidth.setText("Lens Width: "
									+ lensRect.getWidth());
							lensHeight.setText("Lens Height: "
									+ lensRect.getHeight());

							lensWidthUp
									.setOnAction(new EventHandler<ActionEvent>() {
										@Override
										public void handle(ActionEvent t) {
											lensRect.setWidth(lensRect
													.getWidth() + unit);
											lensWidth.setText("Lens Width: "
													+ lensRect.getWidth());
										}
									});

							lensWidthDown
									.setOnAction(new EventHandler<ActionEvent>() {
										@Override
										public void handle(ActionEvent t) {
											lensRect.setWidth(lensRect
													.getWidth() - unit);
											lensWidth.setText("Lens Width: "
													+ lensRect.getWidth());
										}
									});

							lensHeightUp
									.setOnAction(new EventHandler<ActionEvent>() {
										@Override
										public void handle(ActionEvent t) {
											lensRect.setHeight(lensRect
													.getHeight() + unit);
											lensHeight.setText("Lens Height: "
													+ lensRect.getHeight());
										}
									});

							lensHeightDown
									.setOnAction(new EventHandler<ActionEvent>() {
										@Override
										public void handle(ActionEvent t) {
											lensRect.setHeight(lensRect
													.getHeight() - unit);
											lensHeight.setText("Lens Height: "
													+ lensRect.getHeight());
										}
									});

						}
					}
				});

		// A button to clear the selected vertices from the PICKED arraylist
		Button clearPickedBtn = new Button("Clear Selections");
		clearPickedBtn.setPrefWidth(150);
		clearPickedBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {

				lensRect = null;

				for (Circle c : PICKED) {
					FillTransition ft = new FillTransition(Duration
							.millis(2000), c, selectedCol, defaultCol);
					ft.play();
				}

				PICKED.clear();

				System.out.println(PICKED);
			}
		});

		// Use the mid position of the lens as a target position for the
		// sublayout
		Button useLensPos = new Button("Use Lens Position");
		useLensPos.setPrefWidth(150);
		useLensPos.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {
				useLensPos();
			}
		});

		// Separators in the vbox
		Separator separator1 = new Separator(Orientation.HORIZONTAL);
		separator1.setStyle("-fx-background-color: WHITE");
		Separator separator2 = new Separator(Orientation.HORIZONTAL);
		separator2.setStyle("-fx-background-color: WHITE");
		Separator separator3 = new Separator(Orientation.HORIZONTAL);
		separator3.setStyle("-fx-background-color: WHITE");
		Separator separator4 = new Separator(Orientation.HORIZONTAL);
		separator4.setStyle("-fx-background-color: WHITE");

		vbox.getChildren().addAll(helpBtn, selectionTool, colorPicker,
				useLensPos, clearPickedBtn, separator1);

		// Creates buttons for all the layouts in the layoutClasses array
		for (final Class c : layoutClasses) {
			Button button = new Button(c.getSimpleName());
			button.setPrefWidth(150);
			button.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent t) {
					selectedClass = c;

					if (lensRect != null) {
						lensRect.setWidth(sublayoutDimension);
						lensRect.setHeight(sublayoutDimension);
					}

					UndirectedSparseMultigraph<Circle, Line> genGraph = null;

					try {

						subPane = new Pane();

						// Generate an undirected sparse multigraph with
						// selected layout
						genGraph = generateGraph(PICKED, newGraph, c,
								new Dimension((int) lensRect.getWidth(),
										(int) lensRect.getHeight()));

					} catch (Exception ex) {
						Logger.getLogger(JavaFXApplication1.class.getName())
								.log(Level.SEVERE, null, ex);
					}

					// Remove the vertices and edges from the group before
					// adding the new ones to avoid
					// "duplication of children" error
					for (Circle c : genGraph.getVertices()) {
						for (Line l : genGraph.getEdges()) {
							canvas.getChildren().remove(l);
							canvas.getChildren().remove(c);
						}
					}

					drawGraph(genGraph, moveableGroup);
					System.out.println(moveableGroup.getChildren());

					for (Circle c : PICKED) {
						// FillTransition fillT = new FillTransition(Duration
						// .millis(2000), c, selectedCol, defaultCol);
						// fillT.play();
						// moveableGroup.getChildren().add(c);
					}

				}
			});
			vbox.getChildren().add(button);
		}

		vbox.getChildren().addAll(separator2, posText, subLayout, slider,
				separator3, lensWidth, lensWidthUp, lensWidthDown, lensHeight,
				lensHeightUp, lensHeightDown, separator4, backgroundFilter);

		root.getChildren().addAll(bullseye);

//		scene.addEventHandler(MouseEvent.MOUSE_CLICKED,
//				new EventHandler<MouseEvent>() {
//					@Override
//					public void handle(MouseEvent e) {
//						if (e.getButton() == MouseButton.SECONDARY) {
//							posX = e.getX() + (sublayoutDimension/2);
//							posY = e.getY() + (sublayoutDimension/2);
//							posText.setText("Pos X: " + posX + ", Pos Y: "
//									+ posY);
//							bullseye.setX(posX);
//							bullseye.setY(posY);
//						}
//					}
//				});

		Group subGroup = new Group();
		for (Circle c : PICKED) {
			subGroup.getChildren().add(c);
		}

		// Show the scene.
		stage.setTitle("Simple Graph");
		stage.setScene(scene);
		stage.setResizable(true);
		stage.show();

		 ScenicView.show(scene);

	}

	EventHandler<MouseEvent> mouseSelection = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent t) {

			if (t.getEventType() == MouseEvent.MOUSE_PRESSED) {
				rect.setX(t.getX());
				rect.setY(t.getY());
				rectinitX.set(t.getX());
				rectinitY.set(t.getY());
			} else if (t.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				rectX.set(t.getX());
				rectY.set(t.getY());
			} else if (t.getEventType() == MouseEvent.MOUSE_RELEASED) {

				Rectangle r = getNewRectangle();
				r.setX(rect.getX());
				r.setY(rect.getY());
				r.setWidth(rect.getWidth());
				r.setHeight(rect.getHeight());
				pane.getChildren().remove(r);
				pane.getChildren().add(r);

				rectX.set(0);
				rectY.set(0);

				rectinitX.set(0);
				rectinitY.set(0);
			}
		}
	};

	private Rectangle getNewRectangle() {
		Rectangle r = new Rectangle();
		r.setFill(selectionToolColwO);
		r.setStroke(selectionToolCol);
		r.setArcHeight(40);
		r.setArcWidth(40);
		return r;
	}

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

			Line line = LineBuilder.create().stroke(defaultCol).build();
			line.startXProperty().bind(first.centerXProperty());
			line.startYProperty().bind(first.centerYProperty());
			line.endXProperty().bind(second.centerXProperty());
			line.endYProperty().bind(second.centerYProperty());

			toLine.put(n, line);

			undirectGraph.addEdge(line, first, second);
		}

		return undirectGraph;

	}

	// Generate a graph for the picked vertices
	public UndirectedSparseMultigraph<Circle, Line> generateGraph(
			ArrayList<Circle> picked,
			UndirectedSparseMultigraph<Circle, Line> graph,
			@SuppressWarnings("rawtypes") Class layout, Dimension dimension)
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

		@SuppressWarnings("unused")
		BasicVisualizationServer<Circle, Line> vv = new BasicVisualizationServer<Circle, Line>(
				subLayout, dimension);

		Collection<Circle> vertices = subGraph.getVertices();

		for (Circle c : vertices) {
			c.setCenterX(subLayout.transform(c).getX() + posX
					- sublayoutDimension / 2);
			c.setCenterY(subLayout.transform(c).getY() + posY
					- sublayoutDimension / 2);
		}

		return subGraph;
	}

	@SuppressWarnings("rawtypes")
	private Layout getLayoutFor(Class layoutClass, Graph graph)
			throws Exception {
		Object[] args = new Object[] { graph };
		@SuppressWarnings("unchecked")
		Constructor constructor = layoutClass
				.getConstructor(new Class[] { Graph.class });
		return (Layout) constructor.newInstance(args);
	}

	public void drawGraph(Graph<Circle, Line> graph, Group group) {
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

	public void drawGraph(Graph<Circle, Line> graph, Pane pane) {
		for (Circle c : graph.getVertices()) {
			pane.getChildren().add(c);
		}

		for (Line l : graph.getEdges()) {
			pane.getChildren().add(l);
		}
	}

	public Circle createCircleVertex(Layout<String, Number> layout, String vert) {
		// get the xy of the vertex.
		Point2D p = layout.transform(vert);
		Circle circle = CircleBuilder.create().centerX(p.getX())
				.centerY(p.getY()).radius(CIRCLE_SIZE).fill(defaultCol).build();

		circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
			// Clicked the circle to select and changes its color to dark red.
			@Override
			public void handle(MouseEvent t) {
				Circle c = (Circle) t.getSource();
				c.setFill(selectedCol);
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
			// Change it back to default color when released.
			@Override
			public void handle(MouseEvent t) {
				Circle c = (Circle) t.getSource();
				c.setFill(defaultCol);
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

				node.relocate(mouseEvent.getSceneX() - dragDelta.x,
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
		});
	}

	private static class Delta {

		double x, y;
	}

	private void applyStencil(Node stencil, Pane viewpane) {
		viewpane.getChildren().set(0, stencil);
	}

	private Node cutStencil(Rectangle mask, Rectangle viewfinder) {
		Shape stencil = Shape.subtract(mask, viewfinder);
		stencil.setFill(MASK_TINT);
		return stencil;
	}

	// Check if the nodes are in the lens and change the node's color
	// appropriately
	public void checkCollision(UndirectedSparseMultigraph<Circle, Line> graph,
			Rectangle lens) {
		PICKED.clear();
		for (Circle c : graph.getVertices()) {

			if (moveableGroup != null) {
				Bounds result = moveableGroup.localToScene(c.getLayoutBounds());
				if (lens.intersects(result)) {
					c.setFill(selectedCol);
					moveableGroup.sceneToLocal(c.getLayoutBounds());
					PICKED.add(c);
				} else {
					c.setFill(defaultCol);
				}
			} else {
				if (lens.intersects(c.getLayoutBounds())) {
					c.setFill(selectedCol);
					PICKED.add(c);
				} else {
					c.setFill(defaultCol);
				}
			}

		}
	}

	// Method to fade out the shape
	public void fadeTransition(Shape shape, int time, double startVal,
			double endValue) {
		FadeTransition ft = new FadeTransition(Duration.millis(time), shape);
		ft.setFromValue(startVal);
		ft.setToValue(endValue);
		ft.play();
	}

	// Use the center point of the lens as a position for the sublayout
	public void useLensPos() {
		posX = lensRect.getX() + (lensRect.getWidth() / 2);
		posY = lensRect.getY() + (lensRect.getHeight() / 2);

		posText.setText("Pos X: " + Math.round(posX) + ", Pos Y: "
				+ Math.round(posY));

		bullseye.setX(posX + lensLayoutX);
		bullseye.setY(posY + lensLayoutY);
	}

	// Draw a selection lens (orange lens)
	// This lens select all the nodes within it
	public void drawSelectionLens(Rectangle lens) {
		lens.setFill(LENS_TINT);
		lens.setFill(Color.web("blue", 0));
		lens.getStrokeDashArray().addAll(10d);
		lens.setStroke(lensSCol);
		lens.setStrokeWidth(3);
	}

	// Draw a target selection lens (purple lens)
	// This lens defines the location for the sublayout on the canvas
	public void drawTargetSelectionLens(Rectangle lens) {
		lens.setFill(LENS_TINT);
		lens.setFill(Color.web("blue", 0));
		lens.getStrokeDashArray().addAll(10d);
		lens.setStroke(lensFCol);
		lens.setStrokeWidth(3);
	}

	public void changeLensColor(Rectangle lens, Color color) {
		lens.setFill(LENS_TINT);
		lens.setFill(Color.web("blue", 0));
		lens.getStrokeDashArray().addAll(10d);
		lens.setStroke(color);
		lens.setStrokeWidth(3);
	}

	public Paint getLensStroke(Rectangle lens) {
		return lens.getStroke();
	}

	EventHandler<MouseEvent> groupMoveEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
				Node node = (Node) event.getSource();

				moveContext.setMouseAnchorX(event.getSceneX());
				moveContext.setMouseAnchorY(event.getSceneY());
				moveContext.setInitialTranslateX(node.getLayoutX());
				moveContext.setInitialTranslateY(node.getLayoutY());
				event.consume();
			} else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
				Node node = (Node) event.getSource();

				lensLayoutX = node.getLayoutX();
				lensLayoutY = node.getLayoutY();

				node.setLayoutX(moveContext.getDragDestX(event.getSceneX()));
				node.setLayoutY(moveContext.getDragDestY(event.getSceneY()));
				event.consume();

			} else if (event.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
//				useLensPos();
				event.consume();
			}
		}
	};

	public Group createMovableGroup(UndirectedSparseMultigraph<Circle, Line> newGraph) {
		Group moveableGroup = new Group();
		moveableGroup.getChildren().add(lensRect);

		for (Circle c : PICKED) {
			moveableGroup.getChildren().add(c);
		}

		moveableGroup.setOnMousePressed(groupMoveEventHandler);
		moveableGroup.setOnMouseDragged(groupMoveEventHandler);
		moveableGroup.setOnMouseReleased(groupMoveEventHandler);
		
		Circle c = CircleBuilder.create()
				.centerX(0)
				.centerY(0)
				.radius(100)
				.build();
		
		moveableGroup.getChildren().add(c);

		for (Line l : newGraph.getEdges()) {

			Pair<Circle> endpoints = newGraph.getEndpoints(l);
			Circle start = endpoints.getFirst();
			Circle end = endpoints.getSecond();

			if (PICKED.contains(start) && PICKED.contains(end)) {
				l.startXProperty().bind(start.centerXProperty());
				l.startYProperty().bind(start.centerYProperty());
				l.endXProperty().bind(end.centerXProperty());
				l.endYProperty().bind(end.centerYProperty());
			} else {
				if (PICKED.contains(start)) {
					l.startXProperty().bind(
							start.centerXProperty().add(
									moveableGroup.layoutXProperty()));
					l.startYProperty().bind(
							start.centerYProperty().add(
									moveableGroup.layoutYProperty()));
				}
				if (PICKED.contains(end)) {
					l.endXProperty().bind(
							end.centerXProperty().add(
									moveableGroup.layoutXProperty()));
					l.endYProperty().bind(
							end.centerYProperty().add(
									moveableGroup.layoutYProperty()));
				}
			}

		}

		return moveableGroup;
	}

	public ArrayList<Line> getInternalEdges(
			UndirectedSparseMultigraph<Circle, Line> newGraph) {
		ArrayList<Line> result = new ArrayList<>();
		for (Line l : newGraph.getEdges()) {

			Pair<Circle> endpoints = newGraph.getEndpoints(l);
			Circle start = endpoints.getFirst();
			Circle end = endpoints.getSecond();

			if (PICKED.contains(start) && PICKED.contains(end)) {
				result.add(l);
			}

		}
		return result;
	}

	public void checkCollision(Rectangle lens, Group root) {
		ObservableList<Node> children = root.getChildren();
		for (Node n : children) {
			Bounds result = root.localToScene(n.getBoundsInParent());
			if (lens.intersects(result)) {
				if (n.getClass().getSimpleName().equalsIgnoreCase("Group")) {
					checkCollision(lens, (Group) n);
				} else {
					if (n.getClass().getSimpleName().equalsIgnoreCase("Circle")) {
						((Circle) n).setFill(selectedCol);
						PICKED.add((Circle) n);
						System.out.println("TEST");
					}
				}
			}
		}
	}

}
