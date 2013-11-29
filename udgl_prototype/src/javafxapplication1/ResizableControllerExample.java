package javafxapplication1;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.stage.Stage;

public class ResizableControllerExample extends Application {

	private Color selectionStrokeColor = Color.web("#FF883E");
	private Color selectionFillColor = Color.web("#FF883E", 0.1);
	private Color anchorColor = Color.RED;
	private double initY;
	private double initX;
	private double rectWidth;
	private double rectHeight;
	private Image NE_SW = new Image(getClass().getResourceAsStream(
			"resources/ne_sw_corner.png"));
	private Image NW_SE = new Image(getClass().getResourceAsStream(
			"resources/nw_se_corner.png"));

	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
		Group root = new Group();

		Rectangle r = RectangleBuilder.create().x(200).y(200).width(200)
				.height(200).fill(selectionFillColor)
				.stroke(selectionStrokeColor).build();

		ArrayList<Rectangle> anchors = createAnchors(r);

		root.getChildren().addAll(anchors);
		root.getChildren().add(r);

		Scene scene = new Scene(root, 800, 800);
		stage.setTitle("Resizable Controller");
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public ArrayList<Rectangle> createAnchors(final Rectangle rect) {
		ArrayList<Rectangle> anchors = new ArrayList<>();
		final int anchorSize = 10;

		final Rectangle l = RectangleBuilder.create()
				.x(rect.getX() - anchorSize / 2)
				.y(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2)
				.width(anchorSize).height(anchorSize * 3).fill(anchorColor)
				.build();

		final Rectangle tl = RectangleBuilder.create()
				.x(rect.getX() - anchorSize / 2)
				.y(rect.getY() - anchorSize / 2).width(anchorSize)
				.height(anchorSize).fill(anchorColor).build();

		final Rectangle bl = RectangleBuilder.create()
				.x(rect.getX() - anchorSize / 2)
				.y(rect.getY() + rect.getHeight() - anchorSize / 2)
				.width(anchorSize).height(anchorSize).fill(anchorColor).build();

		final Rectangle tm = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2)
				.y(rect.getY() - anchorSize / 2).width(anchorSize * 3)
				.height(anchorSize).fill(anchorColor).build();

		final Rectangle bm = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2)
				.y(rect.getY() + rect.getHeight() - anchorSize / 2)
				.width(anchorSize * 3).height(anchorSize).fill(anchorColor)
				.build();

		final Rectangle tr = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth() - anchorSize / 2)
				.y(rect.getY() - anchorSize / 2).width(anchorSize)
				.height(anchorSize).fill(anchorColor).build();

		final Rectangle r = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth() - anchorSize / 2)
				.y(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2)
				.width(anchorSize).height(anchorSize * 3).fill(anchorColor)
				.build();

		final Rectangle br = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth() - anchorSize / 2)
				.y(rect.getY() + rect.getHeight() - anchorSize / 2)
				.width(anchorSize).height(anchorSize).fill(anchorColor).build();

		// Top Mid anchor event handler
		tm.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				tm.setCursor(Cursor.V_RESIZE);
			}
		});

		tm.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				initY = event.getY();
				rectHeight = rect.getHeight();
			}
		});

		tm.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				tm.setY(event.getY());
				rect.setY(tm.getY());
				rect.setHeight(bl.getY() - tl.getY());

				tm.setY(rect.getY() - anchorSize / 2);
				r.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);
				l.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);
			}
		});

		// Top Right anchor event handler
		tr.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				tr.setCursor(new ImageCursor(NE_SW, 5, 5));
			}
		});

		tr.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				initY = event.getY();
				rectHeight = rect.getHeight();
			}
		});

		tr.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
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
			}
		});

		// Right anchor event handler
		r.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				r.setCursor(Cursor.H_RESIZE);
			}
		});

		r.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				initX = event.getX();
				rectWidth = rect.getWidth();
			}
		});

		r.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				r.setX(event.getX());
				rect.setWidth(rectWidth + r.getX() - initX);

				r.setX(rect.getX() + rect.getWidth() - anchorSize / 2);
				tm.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
				bm.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
			}
		});

		// Bottom right anchor event handler
		br.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				br.setCursor(new ImageCursor(NW_SE, 5, 5));
			}
		});

		br.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				initY = event.getY();
				rectHeight = rect.getHeight();
			}
		});

		br.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
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
			}
		});

		// Bottom mid anchor event handler
		bm.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				bm.setCursor(Cursor.V_RESIZE);
			}
		});

		bm.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				initY = event.getY();
				rectHeight = rect.getHeight();
			}
		});

		bm.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				bm.setY(event.getY());
				rect.setHeight(rectHeight + bm.getY() - initY);

				bm.setY(rect.getY() + rect.getHeight() - anchorSize / 2);
				r.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);
				l.setY(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2);
			}
		});

		// Bottom left anchor event handler
		bl.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				bl.setCursor(new ImageCursor(NE_SW, 5, 5));
			}
		});

		bl.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				initY = event.getY();
				rectHeight = rect.getHeight();
			}
		});

		bl.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
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
			}
		});

		// Left anchor event handler
		l.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				l.setCursor(Cursor.H_RESIZE);
			}
		});

		l.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				initY = event.getY();
			}
		});

		l.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				l.setX(event.getX());
				rect.setX(l.getX());
				rect.setWidth(br.getX() - bl.getX());

				l.setX(rect.getX() - anchorSize / 2);
				tm.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
				bm.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
			}
		});

		// Top left anchor event handler
		tl.setOnMouseEntered(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				tl.setCursor(new ImageCursor(NW_SE, 5, 5));
			}
		});

		tl.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				initY = event.getY();
				rectWidth = rect.getWidth();
				rectHeight = rect.getHeight();
			}
		});

		tl.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
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

		anchors.add(l);
		anchors.add(tl);
		anchors.add(bl);
		anchors.add(tm);
		anchors.add(bm);
		anchors.add(tr);
		anchors.add(r);
		anchors.add(br);

		return anchors;
	}

}
