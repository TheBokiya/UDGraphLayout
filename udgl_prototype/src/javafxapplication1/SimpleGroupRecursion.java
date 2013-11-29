package javafxapplication1;

import java.util.ArrayList;

import com.javafx.experiments.scenicview.ScenicView;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CircleBuilder;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineBuilder;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.Stage;

public class SimpleGroupRecursion<E> extends Application {

	final MoveContext moveContext = new MoveContext();
	Rectangle lens;
	Group root;
	
	ArrayList<Circle> selected = new ArrayList<>();

	@Override
	public void start(Stage stage) throws Exception {
		root = new Group();
		

		Circle c1 = drawCircle();
		c1.setCenterX(200);
		c1.setCenterY(100);
		c1.setOnMousePressed(circleMoveEventHandler);
		c1.setOnMouseDragged(circleMoveEventHandler);
		Text t1 = TextBuilder.create().text(c1.toString()).x(220).y(100)
				.font(Font.font("Verdana", 12)).fill(Color.BLACK).build();
		t1.xProperty().bind(c1.centerXProperty().add(20));
		t1.yProperty().bind(c1.centerYProperty());

		Circle c2 = drawCircle();
		c2.setCenterX(200);
		c2.setCenterY(300);
		c2.setOnMousePressed(circleMoveEventHandler);
		c2.setOnMouseDragged(circleMoveEventHandler);
		Text t2 = TextBuilder.create().text(c2.toString()).x(220).y(300)
				.font(Font.font("Verdana", 12)).fill(Color.BLACK).build();
		t2.xProperty().bind(c2.centerXProperty().add(20));
		t2.yProperty().bind(c2.centerYProperty());

		Line c1c2 = drawLine();
		c1c2.startXProperty().bind(c1.centerXProperty());
		c1c2.startYProperty().bind(c1.centerYProperty());
		c1c2.endXProperty().bind(c2.centerXProperty());
		c1c2.endYProperty().bind(c2.centerYProperty());

		Pane group1 = new Pane();
		group1.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		group1.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
		group1.setMinSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

		Circle c3 = drawCircle();
		c3.setCenterX(500);
		c3.setCenterY(100);
		c3.setOnMousePressed(circleMoveEventHandler);
		c3.setOnMouseDragged(circleMoveEventHandler);
		Text t3 = TextBuilder.create().text(c3.toString()).x(520).y(100)
				.font(Font.font("Verdana", 12)).fill(Color.BLACK).build();
		t3.xProperty().bind(c3.centerXProperty().add(20));
		t3.yProperty().bind(c3.centerYProperty());

		group1.getChildren().addAll(c3, t3);

		Group group2 = new Group();

		Circle c4 = drawCircle();
		c4.setCenterX(500);
		c4.setCenterY(600);
		c4.setOnMousePressed(circleMoveEventHandler);
		c4.setOnMouseDragged(circleMoveEventHandler);
		Text t4 = TextBuilder.create().text(c4.toString()).x(520).y(600)
				.font(Font.font("Verdana", 12)).fill(Color.BLACK).build();
		t4.xProperty().bind(c4.centerXProperty().add(20));
		t4.yProperty().bind(c4.centerYProperty());

		Circle c5 = drawCircle();
		c5.setCenterX(550);
		c5.setCenterY(650);
		c5.setOnMousePressed(circleMoveEventHandler);
		c5.setOnMouseDragged(circleMoveEventHandler);
		Text t5 = TextBuilder.create().text(c5.toString()).x(570).y(650)
				.font(Font.font("Verdana", 12)).fill(Color.BLACK).build();
		t5.xProperty().bind(c5.centerXProperty().add(20));
		t5.yProperty().bind(c5.centerYProperty());

		Circle c6 = drawCircle();
		c6.setCenterX(450);
		c6.setCenterY(650);
		c6.setOnMousePressed(circleMoveEventHandler);
		c6.setOnMouseDragged(circleMoveEventHandler);
		Text t6 = TextBuilder.create().text(c6.toString()).x(470).y(650)
				.font(Font.font("Verdana", 12)).fill(Color.BLACK).build();
		t6.xProperty().bind(c6.centerXProperty().add(20));
		t6.yProperty().bind(c6.centerYProperty());

		Line c4c5 = drawLine();
		c4c5.startXProperty().bind(c4.centerXProperty());
		c4c5.startYProperty().bind(c4.centerYProperty());
		c4c5.endXProperty().bind(c5.centerXProperty());
		c4c5.endYProperty().bind(c5.centerYProperty());

		Line c4c6 = drawLine();
		c4c6.startXProperty().bind(c4.centerXProperty());
		c4c6.startYProperty().bind(c4.centerYProperty());
		c4c6.endXProperty().bind(c6.centerXProperty());
		c4c6.endYProperty().bind(c6.centerYProperty());
		
		lens = drawLens();
		lens.setOnMousePressed(rectangleMoveEventHandler);
		lens.setOnMouseDragged(rectangleMoveEventHandler);

		group2.getChildren().addAll(c4, t4, c5, t5, c6, t6, c4c5, c4c6);
//		group2.translateYProperty().set(-200);

		group1.getChildren().addAll(group2);

		

		root.getChildren().addAll(c1, t1, c2, t2, c1c2, group1);
		
		

		Scene scene = new Scene(root, 800, 800);
		stage.setTitle("RecursionTest");
		stage.setScene(scene);
		stage.show();
		ScenicView.show(scene);
	}

	public static void main(String[] args) {
		launch(args);

	}

	public Circle drawCircle() {
		return CircleBuilder.create().radius(15).fill(Color.GREY).build();
	}

	public Line drawLine() {
		return LineBuilder.create().stroke(Color.ORANGE).strokeWidth(2).build();
	}

	public Rectangle drawLens() {
		return RectangleBuilder.create().x(0).y(0).fill(Color.PINK)
				.opacity(0.5).stroke(Color.PURPLE).strokeWidth(2).width(100)
				.height(100).build();
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

				event.consume();

			}

		}
	};

	EventHandler<MouseEvent> rectangleMoveEventHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent event) {
			if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
				Rectangle node = (Rectangle) event.getSource();

				moveContext.setMouseAnchorX(event.getX());
				moveContext.setMouseAnchorY(event.getY());
				moveContext.setInitialTranslateX(node.getX());
				moveContext.setInitialTranslateY(node.getY());
				event.consume();
			} else if (event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
				Rectangle node = (Rectangle) event.getSource();

				node.setX(moveContext.getDragDestX(event.getX()));
				node.setY(moveContext.getDragDestY(event.getY()));
				
				selected.clear();
				walkingTheTree(lens, root);
				System.out.println(selected);

				event.consume();

			}

		}
	};

	// Check if the nodes are in the lens and change the node's color
	// appropriately
	public void checkCollision(Rectangle rect, Group root) {
		@SuppressWarnings("unchecked")
		ObservableList<E> children = (ObservableList<E>) root.getChildren();
		ArrayList<Bounds> circleBound = new ArrayList<>();
		
		for (int i = 0; i < children.size(); i ++) {
//			if (children.get(i).getClass().getSimpleName().equalsIgnoreCase("Circle")) {
//				Bounds result = ((Circle) children.get(i)).getLayoutBounds();
//				
//				if (rect.intersects(result)) {
//					System.out.println("COLLIDED");
//				}
//			}
		}
//		Bounds result = node.getLayoutBounds();
//		if (rect.intersects(result)) {
//			System.out.println("COLLIDED");
//		}
	}
	
	public void walkingTheTree(Rectangle rect, Group root) {
		ObservableList<Node> children = root.getChildren();
		for (Node n : children) {
			Bounds result = root.localToScene(n.getBoundsInParent());
			if (rect.intersects(result)) {
				if (n.getClass().getSimpleName().equalsIgnoreCase("Group")) {
					walkingTheTree(rect, (Group) n);
				} else {
					if (n.getClass().getSimpleName().equalsIgnoreCase("Circle")) {
						selected.add((Circle) n);
					}
				}
			}	
		}
	}

}
