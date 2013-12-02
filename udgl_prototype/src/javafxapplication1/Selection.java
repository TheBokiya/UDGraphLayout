package javafxapplication1;


import java.util.List;

import javafx.beans.property.StringProperty;
import javafx.scene.Group;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Selection {
	private Group root, graphRoot;
	private Rectangle background;
	private StringProperty name;
	private List<Circle> circles;
	private SelectionRectangle selectionAnchors;
	
	public Selection(Group root, Group graphRoot, Rectangle background, List<Circle> circles, SelectionRectangle selectionAnchors) {
		this.root = root;
		this.graphRoot = graphRoot;
		this.background = background;
		this.circles = circles;
		this.selectionAnchors = selectionAnchors;
	}



	public Group getRoot() {
		return root;
	}

	public void setRoot(Group root) {
		this.root = root;
	}

	public Group getGraphRoot() {
		return graphRoot;
	}

	public void setGraphRoot(Group graphRoot) {
		this.graphRoot = graphRoot;
	}

	public Rectangle getBackground() {
		return background;
	}

	public void setBackground(Rectangle background) {
		this.background = background;
	}
	
	public String getName() {
		return name.get();
	}
	
	public void setName(String name) {
		this.name.set(name);
	}
	
	public StringProperty nameProperty() {
		return name;
	}
	
	
	
	public SelectionRectangle getSelectionAnchors() {
		return selectionAnchors;
	}



	public void setSelectionAnchors(SelectionRectangle selectionAnchors) {
		this.selectionAnchors = selectionAnchors;
	}



	public void correctCirclePositions(){
		double smallestX = Double.MAX_VALUE;
		double smallestY = Double.MAX_VALUE;
		for(Circle c: circles){
			smallestX = Math.min(smallestX, c.getCenterX());
			smallestY = Math.min(smallestY, c.getCenterY());
		}
		
		for(Circle c: circles){
			c.setCenterX(c.getCenterX() - smallestX + 15);
			c.setCenterY(c.getCenterY() - smallestY + 15);
		}
	}
}
