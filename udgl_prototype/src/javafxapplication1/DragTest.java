package javafxapplication1;

import com.javafx.experiments.scenicview.ScenicView;

import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineBuilder;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.stage.Stage;

public class DragTest extends Application {
	final MoveContext moveContext = new MoveContext();
	Rectangle lens;
	Group moveableGroup;

	@Override
	public void start(Stage stage) throws Exception {
		Group root = new Group();

		Circle c1 = createCircle();
		c1.setCenterX(100);
		c1.setCenterY(100);
		c1.setOnMousePressed(circleMoveEventHandler);
		c1.setOnMouseDragged(circleMoveEventHandler);

		Circle c2 = createCircle();
		c2.setCenterX(100);
		c2.setCenterY(500);
		c2.setOnMousePressed(circleMoveEventHandler);
		c2.setOnMouseDragged(circleMoveEventHandler);
		
		Circle c3 = createCircle();
		c3.setCenterX(500);
		c3.setCenterY(100);
		c3.setOnMousePressed(circleMoveEventHandler);
		c3.setOnMouseDragged(circleMoveEventHandler);

		Circle c4 = createCircle();
		c4.setCenterX(500);
		c4.setCenterY(500);
		c4.setOnMousePressed(circleMoveEventHandler);
		c4.setOnMouseDragged(circleMoveEventHandler);

		Line c1c2 = createLine();
		c1c2.startXProperty().bind(c1.centerXProperty());
		c1c2.startYProperty().bind(c1.centerYProperty());
		c1c2.endXProperty().bind(c2.centerXProperty());
		c1c2.endYProperty().bind(c2.centerYProperty());
		
		Rectangle r = RectangleBuilder.create().x(50).y(75).width(100)
				.height(100).fill(Color.LIGHTCORAL).opacity(0.5).build();
		Group leftGroup = new Group(r, c1, c2, c1c2);
		leftGroup.setOnMousePressed(groupMoveEventHandler);
		leftGroup.setOnMouseDragged(groupMoveEventHandler);
		
		
		Line c3c4 = createLine();
		c3c4.startXProperty().bind(c3.centerXProperty());
		c3c4.startYProperty().bind(c3.centerYProperty());
		c3c4.endXProperty().bind(c4.centerXProperty());
		c3c4.endYProperty().bind(c4.centerYProperty());

		Line c1c4 = createLine();
//		c1c4.startXProperty().bind(c1.centerXProperty());
//		c1c4.startYProperty().bind(c1.centerYProperty());

		moveableGroup = new Group();
		Rectangle r1 = RectangleBuilder.create().x(450).y(75).width(100)
				.height(100).fill(Color.LIGHTGRAY).opacity(0.5).build();

		lens = RectangleBuilder.create().x(300).y(300).width(100).height(100)
				.fill(Color.WHITE).opacity(0.5).stroke(Color.BLUE)
				.strokeWidth(2).build();

		moveableGroup.getChildren().addAll(r1, c3, c4, c3c4);
		moveableGroup.setOnMousePressed(groupMoveEventHandler);
		moveableGroup.setOnMouseDragged(groupMoveEventHandler);

		c1c4.startXProperty().bind(
				c1.centerXProperty().add(leftGroup.layoutXProperty()));
		c1c4.startYProperty().bind(
				c1.centerYProperty().add(leftGroup.layoutYProperty()));
		
		c1c4.endXProperty().bind(
				c4.centerXProperty().add(moveableGroup.layoutXProperty()));
		c1c4.endYProperty().bind(
				c4.centerYProperty().add(moveableGroup.layoutYProperty()));

		root.getChildren().addAll(leftGroup, c1c4, moveableGroup);
		Scene scene = new Scene(root, 600, 600);
		stage.setScene(scene);
		stage.setTitle("Drag Test");
		stage.show();
		ScenicView.show(scene);
	}

	public Circle createCircle() {
		return CircleBuilder.create().radius(25).fill(Color.AQUAMARINE).build();
	}

	public Line createLine() {
		return LineBuilder.create().stroke(Color.BLACK).strokeWidth(2).build();
	}

	public static void main(String[] args) {
		launch(args);

	}

	EventHandler<MouseEvent> circleMoveEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
				Circle node = (Circle) event.getSource();

				moveContext.setMouseAnchorX(event.getX());
				moveContext.setMouseAnchorY(event.getY());
				moveContext.setInitialTranslateX(node.getCenterX());
				moveContext.setInitialTranslateY(node.getCenterY());
				event.consume();
			} else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
				Circle node = (Circle) event.getSource();

				node.setCenterX(moveContext.getDragDestX(event.getX()));
				node.setCenterY(moveContext.getDragDestY(event.getY()));

				System.out.println("Center X: " + node.getCenterX());
				System.out.println("Center Y: " + node.getCenterY());
				
				checkCollision(lens, node);
				
				System.out.println(node.getBoundsInParent());

				event.consume();

			}

		}
	};

	EventHandler<MouseEvent> groupMoveEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
				Node node = (Node) event.getSource();

				moveContext.setMouseAnchorX(event.getSceneX());
				moveContext.setMouseAnchorY(event.getSceneY());
				moveContext.setInitialTranslateX(node.getLayoutX());
				moveContext.setInitialTranslateY(node.getLayoutY());
			} else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
				Node node = (Node) event.getSource();

				node.setLayoutX(moveContext.getDragDestX(event.getSceneX()));
				node.setLayoutY(moveContext.getDragDestY(event.getSceneY()));

				System.out.println("Layout X: " + node.getLayoutX());
				System.out.println("Layout Y: " + node.getLayoutY());

				System.out.println("X: "
						+ node.localToParent(node.getLayoutBounds()));
				System.out.println("Y: "
						+ node.localToParent(node.getLayoutBounds()));
				
//				checkCollision(lens, node);

			}

		}
	};

	// Check if the nodes are in the lens and change the node's color
	// appropriately
	public void checkCollision(Rectangle rect, Node node) {
		Bounds result = moveableGroup.localToParent(node.getLayoutBounds());
		if (rect.intersects(result)) {
			System.out.println("COLLIDED");
		}
	}

	class MoveContext {
		private double mouseAnchorX;
		private double mouseAnchorY;

		private double initialTranslateX;
		private double initialTranslateY;

		public MoveContext() {
			this(0, 0, 0, 0);
		}

		/**
		 * Create a move context
		 * 
		 * @param mouseAnchorX
		 *            - start mouse x position
		 * @param mouseAnchorY
		 *            - start mouse y position
		 * @param initialTranslateX
		 *            - initial object x position
		 * @param initialTranslateY
		 *            - initial object y position
		 */
		public MoveContext(double mouseAnchorX, double mouseAnchorY,
				double initialTranslateX, double initialTranslateY) {
			this.mouseAnchorX = mouseAnchorX;
			this.mouseAnchorY = mouseAnchorY;
			this.initialTranslateX = initialTranslateX;
			this.initialTranslateY = initialTranslateY;
		}

		public double getDragDestX(double destX) {
			return initialTranslateX + destX - mouseAnchorX;
		}

		public double getDragDestY(double destY) {
			return initialTranslateY + destY - mouseAnchorY;
		}

		/**
		 * Get the value of initialTranslateY
		 * 
		 * @return the value of initialTranslateY
		 */
		public double getInitialTranslateY() {
			return initialTranslateY;
		}

		/**
		 * Set the value of initialTranslateY
		 * 
		 * @param initialTranslateY
		 *            new value of initialTranslateY
		 */
		public void setInitialTranslateY(double initialTranslateY) {
			this.initialTranslateY = initialTranslateY;
		}

		/**
		 * Get the value of initialTranslateX
		 * 
		 * @return the value of initialTranslateX
		 */
		public double getInitialTranslateX() {
			return initialTranslateX;
		}

		/**
		 * Set the value of initialTranslateX
		 * 
		 * @param initialTranslateX
		 *            new value of initialTranslateX
		 */
		public void setInitialTranslateX(double initialTranslateX) {
			this.initialTranslateX = initialTranslateX;
		}

		/**
		 * Get the value of mouseAnchorY
		 * 
		 * @return the value of mouseAnchorY
		 */
		public double getMouseAnchorY() {
			return mouseAnchorY;
		}

		/**
		 * Set the value of mouseAnchorY
		 * 
		 * @param mouseAnchorY
		 *            new value of mouseAnchorY
		 */
		public void setMouseAnchorY(double mouseAnchorY) {
			this.mouseAnchorY = mouseAnchorY;
		}

		/**
		 * Get the value of mouseAnchorX
		 * 
		 * @return the value of mouseAnchorX
		 */
		public double getMouseAnchorX() {
			return mouseAnchorX;
		}

		/**
		 * Set the value of mouseAnchorX
		 * 
		 * @param mouseAnchorX
		 *            new value of mouseAnchorX
		 */
		public void setMouseAnchorX(double mouseAnchorX) {
			this.mouseAnchorX = mouseAnchorX;
		}

		@Override
		public String toString() {
			return "MoveContext{" + "mouseAnchorX=" + mouseAnchorX
					+ ", mouseAnchorY=" + mouseAnchorY + ", initialTranslateX="
					+ initialTranslateX + ", initialTranslateY="
					+ initialTranslateY + '}';
		}

	}

}
