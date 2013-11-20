package javafxapplication1;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class GroupMoveHandler implements EventHandler<MouseEvent> {
	
	private MoveContext moveContext;
	
	public GroupMoveHandler () {
		moveContext = new MoveContext();
	}

	@Override
	public void handle(MouseEvent e) {
		if (e.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
			Node node = (Node) e.getSource();

			moveContext.setMouseAnchorX(e.getSceneX());
			moveContext.setMouseAnchorY(e.getSceneY());
			moveContext.setInitialTranslateX(node.getLayoutX());
			moveContext.setInitialTranslateY(node.getLayoutY());
			e.consume();
		} else if (e.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
			Node node = (Node) e.getSource();

			node.setLayoutX(moveContext.getDragDestX(e.getSceneX()));
			node.setLayoutY(moveContext.getDragDestY(e.getSceneY()));
			e.consume();

		} else if (e.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
			e.consume();
		}
	}

}
