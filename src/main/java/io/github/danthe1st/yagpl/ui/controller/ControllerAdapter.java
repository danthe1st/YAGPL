package io.github.danthe1st.yagpl.ui.controller;

import io.github.danthe1st.yagpl.api.GlobalContext;
import io.github.danthe1st.yagpl.ui.YAGPL;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 * adapter class for {@link Controller}.<br/>
 * contains multiple utility methods
 * @author dan1st
 * @param <T> the type of the root view
 */
public abstract class ControllerAdapter<T extends Parent> implements Controller<T> {

	protected YAGPL main;
	protected T view;
	
	protected GlobalContext globalCtx;
	
	@Override
	public void setMain(YAGPL main) {
		this.main=main;
	}

	@Override
	public void setView(T view) {
		this.view=view;
	}
	
	public T getView() {
		return view;
	}
	
	@Override
	public void setGlobalContext(GlobalContext globalCtx) {
		this.globalCtx=globalCtx;
	}
	/**
	 * display an error message
	 * @param text the text to display
	 */
	public final void error(String text) {
		Alert alert=new Alert(AlertType.ERROR,text);
		alert.showAndWait();
	}
	/**
	 * display an error message and log an {@link Exception}
	 * @param text the text to display
	 * @param e the {@link Exception} to log
	 */
	public final void error(String text,Exception e) {
		e.printStackTrace();
		error(text);
	}
	/**
	 * display an error message, log an {@link Exception} and shut down the application
	 * @param text the text to display
	 * @param e the {@link Exception} to log
	 */
	public final void fatal(String text,Exception e) {
		error(text,e);
		Platform.exit();
	}
	/**
	 * allows dragging of a {@link Node}
	 * @param node
	 */
	protected final void allowDrag(Node node) {
		// Custom object to hold x and y positions
		final Position dragDelta = new Position();
		node.setOnMousePressed(e -> fillDeltaWithNodePosition(dragDelta,node,e));
		setDragUpdate(node,dragDelta);
		node.setOnMouseReleased(e->removeIfTooFarLeft(node));
	}
	/**
	 * configures a node to be moved when it is dragged
	 * @param node the node to configure for dragging
	 * @param dragDelta a mutable coordinate-object used for storing where the node started when dragging it
	 */
	protected final void setDragUpdate(Node node, Position dragDelta) {
		node.setOnMouseDragged(e -> {
			node.setLayoutX(dragDelta.x+e.getSceneX());
			node.setLayoutY(calculateDrag(dragDelta.y,e.getSceneY(),0));
		});
	}
	/**
	 * gets the absolute coordinates of a {@link Node}.<br/>
	 * In this context, absolute means relative to the {@link Scene}
	 * @param node the node to get the absolute coordinates
	 * @return the coordinates of the {@link Node} relative to the {@link Scene}
	 */
	protected final Position getAbsoluteCoord(Node node){
		Bounds boundsInScene=node.localToScene(node.getBoundsInLocal());
		return new Position(boundsInScene.getMinX(), boundsInScene.getMinY());
	}
	/**
	 * tries to get the height of a {@link Node}
	 * @param node the {@link Node} to try to get the height from
	 * @param width the width of the node
	 * @return the height of the node
	 */
	protected final double estimateHeight(Node node,double width) {
		if(node instanceof Region) {
			return ((Region) node).getHeight();
		}
		return Math.min(Math.max(node.minHeight(width), node.prefHeight(width)), node.maxHeight(width));
	}
	/**
	 * adds an element to a {@link Pane} and position it to the point of the mouse event relative to the {@link Pane}
	 * @param node the node to add to the {@link Pane}
	 * @param pane the {@link Pane} to add the {@link Node} to
	 * @param e the {@link MouseEvent} used for getting the mouse position
	 * @return A {@link Position} containing the relative position of the {@link Node} in the {@link Pane}
	 */
	protected final Position addElementToPaneAndFillDeltaWithPosition(Node node, Pane pane,MouseEvent e) {
		pane.getChildren().add(node);
		Position coord=getAbsoluteCoord(node);
		node.setLayoutX(e.getSceneX()-coord.x);
		node.setLayoutY(e.getSceneY()-coord.y);
		Position delta=new Position();
		fillDeltaWithNodePosition(delta,node,e);
		return delta;
	}
	/**
	 * sets the x and y values of a {@link Position} to the relative position of the mouse pointer to a {@link Node}
	 * @param delta the {@link Position} to fill in the coordinates of the mouse pointer relative to the {@link Node}
	 * @param node the {@link Node} the mouse pointer should be relative to
	 * @param e the {@link MouseEvent} to calculate the position
	 */
	protected final void fillDeltaWithNodePosition(Position delta, Node node,MouseEvent e) {
		delta.x = node.getLayoutX() - e.getSceneX();
		delta.y = node.getLayoutY() - e.getSceneY();
	}
	/**
	 * removes a {@link Node} from its parent if it exceeds the left border of its parent
	 * @param node the {@link Node} to remove from its parent
	 * @return <code>true</code> if the node has been removed, else <code>false</code>
	 */
	protected final boolean removeIfTooFarLeft(Node node) {
		Bounds boundsInParent = node.getBoundsInParent();
		if(boundsInParent.getMaxX()<0||boundsInParent.getMaxY()<0) {
			((Pane)node.getParent()).getChildren().remove(node);//TODO test with instanceof
			return true;
		}
		return false;
	}
	/**
	 * Calculates the position after dragging an element
	 * @param prevLayout the previous position
	 * @param change how the position has changed
	 * @param min the minimum position
	 * @return the new position
	 */
	protected final double calculateDrag(double prevLayout,double change,double min) {
		return Math.max(prevLayout+change,min);
	}

	/**
	 * a class representing coordinates
	 * @author dan1st
	 */
	protected static class Position {
		private double x;
		private double y;
		public Position() {
			//default constructor
		}
		public Position(double x,double y) {
			this.x=x;
			this.y=y;
		}
		public double getX() {
			return x;
		}
		public void setX(double x) {
			this.x = x;
		}
		public double getY() {
			return y;
		}
		public void setY(double y) {
			this.y = y;
		}
	}
}
