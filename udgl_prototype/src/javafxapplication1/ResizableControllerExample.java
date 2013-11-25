package javafxapplication1;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.stage.Stage;

public class ResizableControllerExample extends Application{
	
	private Color selectionStrokeColor = Color.web("#FF883E");
	private Color selectionFillColor = Color.web("#FF883E", 0.1);
	private double initY;
	private double initX;
	private double rectWidth;
	private double rectHeight;

	@Override
	public void start(Stage stage) throws Exception {
		// TODO Auto-generated method stub
		Group root = new Group();
		
		Rectangle r = RectangleBuilder.create()
				.x(200)
				.y(200)
				.width(200)
				.height(200)
				.fill(selectionFillColor)
				.stroke(selectionStrokeColor)
				.build();
		
		ArrayList<Rectangle> anchors = createAnchors(r);
		
		
		root.getChildren().addAll(anchors);
		root.getChildren().add(r);
		
		Scene scene = new Scene(root, 800, 800);
		stage.setTitle("Resizable Controller");
		stage.setScene(scene);
		stage.show();
	}
	
	public static void main (String[] args) {
		launch(args);
	}
	
	public ArrayList<Rectangle> createAnchors (final Rectangle rect) {
		ArrayList<Rectangle> anchors = new ArrayList<>();
		final int anchorSize = 10;
		
		Rectangle l = RectangleBuilder.create()
				.x(rect.getX() - anchorSize/2)
				.y(rect.getY() + rect.getHeight()/2 - anchorSize/2)
				.width(anchorSize)
				.height(anchorSize)
				.fill(Color.BLACK)
				.build();
		
		l.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				
			}
		});
		
		Rectangle tl = RectangleBuilder.create()
				.x(rect.getX() - anchorSize/2)
				.y(rect.getY() - anchorSize/2)
				.width(anchorSize)
				.height(anchorSize)
				.fill(Color.BLACK)
				.build();
		
		Rectangle bl = RectangleBuilder.create()
				.x(rect.getX() - anchorSize/2)
				.y(rect.getY() + rect.getHeight() - anchorSize/2)
				.width(anchorSize)
				.height(anchorSize)
				.fill(Color.BLACK)
				.build();
		
		Rectangle tm = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth()/2 - anchorSize/2)
				.y(rect.getY() - anchorSize/2)
				.width(anchorSize)
				.height(anchorSize)
				.fill(Color.BLACK)
				.build();
		
		Rectangle bm = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth()/2 - anchorSize/2)
				.y(rect.getY() + rect.getHeight() - anchorSize/2)
				.width(anchorSize)
				.height(anchorSize)
				.fill(Color.BLACK)
				.build();
		
		Rectangle tr = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth() - anchorSize/2)
				.y(rect.getY() - anchorSize/2)
				.width(anchorSize)
				.height(anchorSize)
				.fill(Color.BLACK)
				.build();
		
		final Rectangle r = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth() - anchorSize/2)
				.y(rect.getY() + rect.getHeight()/2 - anchorSize*3/2)
				.width(anchorSize)
				.height(anchorSize*3)
				.fill(Color.BLACK)
				.build();
		
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
			}
		});
		
		r.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				r.setX(rect.getX() + rect.getWidth() - anchorSize/2);
			}
		});
		
		Rectangle br = RectangleBuilder.create()
				.x(rect.getX() + rect.getWidth() - anchorSize/2)
				.y(rect.getY() + rect.getHeight() - anchorSize/2)
				.width(anchorSize)
				.height(anchorSize)
				.fill(Color.BLACK)
				.build();
		
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
