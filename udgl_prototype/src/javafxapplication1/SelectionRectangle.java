package javafxapplication1;

import java.util.ArrayList;

import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;

public class SelectionRectangle {

	private Rectangle tl, tm, tr, r, br, bm, bl, l;
	private int anchorSize = 10;
	private Color anchorColor = Color.RED;
	private Rectangle rect;

	public SelectionRectangle(Rectangle rect) {
		
		this.rect = rect;
		
		l = RectangleBuilder.create().x(rect.getX() - anchorSize / 2)
				.y(rect.getY() + rect.getHeight() / 2 - anchorSize * 3 / 2)
				.width(anchorSize).height(anchorSize * 3).fill(anchorColor)
				.build();

		tl = RectangleBuilder.create().x(rect.getX() - anchorSize / 2)
				.y(rect.getY() - anchorSize / 2).width(anchorSize)
				.height(anchorSize).fill(anchorColor).build();
		
		bl = RectangleBuilder.create()
				.x(rect.getX() - anchorSize / 2)
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
	}
	
	public void handleLeftAnchorMousePressed (MouseEvent event) {
		event.getY();
	}
	
	public void handleLeftAnchorMouseEntered (MouseEvent event) {
		l.setCursor(Cursor.H_RESIZE);
	}
	
	public void handleLeftAnchorMouseDragged (MouseEvent event) {
		l.setX(event.getX());
		rect.setX(l.getX());
		rect.setWidth(br.getX() - bl.getX());

		l.setX(rect.getX() - anchorSize / 2);
		tm.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
		bm.setX(rect.getX() + rect.getWidth() / 2 - anchorSize * 3 / 2);
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

	public void setRect(double x, double y, double width, double height) {
		rect.setX(x);
		rect.setY(y);
		rect.setWidth(width);
		rect.setHeight(height);
	}
}
