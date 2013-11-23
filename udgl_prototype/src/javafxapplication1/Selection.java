package javafxapplication1;

import javafx.beans.property.StringProperty;
import javafx.scene.Group;
import javafx.scene.shape.Rectangle;

public class Selection {
	Group root, graphRoot;
	Rectangle background;
	StringProperty name;
	
	public Selection(Group root, Group graphRoot, Rectangle background) {
		this.root = root;
		this.graphRoot = graphRoot;
		this.background = background;
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
	
	
}
