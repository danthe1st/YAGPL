package io.github.danthe1st.yagpl.ui.controller;

import io.github.danthe1st.yagpl.api.GlobalContext;
import io.github.danthe1st.yagpl.ui.YAGPL;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

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
	public void error(String text) {
		Alert alert=new Alert(AlertType.ERROR,text);
		alert.showAndWait();
	}
	public void error(String text,Exception e) {
		e.printStackTrace();
		error(text);
	}
	public void fatal(String text,Exception e) {
		e.printStackTrace();
		error(text);
		Platform.exit();
	}
	
	protected void allowDrag(Node node) {
		// Custom object to hold x and y positions
		final Coord dragDelta = new Coord();

		node.setOnMousePressed(e -> fillDeltaWithNodePosition(dragDelta,node,e));
		setDragUpdate(node,dragDelta);
		node.setOnMouseReleased(e->removeIfTooFarLeft(node));
	}
	protected void setDragUpdate(Node node, Coord dragDelta) {
		node.setOnMouseDragged(e -> {
			node.setLayoutX(dragDelta.x+e.getSceneX());
			node.setLayoutY(calculateDrag(dragDelta.y,e.getSceneY(),0));
		});
	}
	protected Coord getAbsoluteCoord(Node node){
		Bounds boundsInScene=node.localToScene(node.getBoundsInLocal());
		return new Coord(boundsInScene.getMinX(), boundsInScene.getMinY());
	}
	protected double estimateHeight(Node node,double width) {//TODO also for width
		if(node instanceof Region) {
			return ((Region) node).getHeight();
		}
		return Math.min(Math.max(node.minHeight(width), node.prefHeight(width)), node.maxHeight(width));
	}
	protected void addElementToPaneAndFillDeltaWithPosition(Coord delta,Node node, Pane pane,MouseEvent e) {
		pane.getChildren().add(node);
		Coord coord=getAbsoluteCoord(node);
		node.setLayoutX(e.getSceneX()-coord.x);
		node.setLayoutY(e.getSceneY()-coord.y);
		fillDeltaWithNodePosition(delta,node,e);
	}
	protected void fillDeltaWithNodePosition(Coord delta, Node node,MouseEvent e) {
		delta.x = node.getLayoutX() - e.getSceneX();
		delta.y = node.getLayoutY() - e.getSceneY();
	}
	protected boolean removeIfTooFarLeft(Node node) {
		Bounds boundsInParent = node.getBoundsInParent();
		if(boundsInParent.getMaxX()<0||boundsInParent.getMaxY()<0) {
			((Pane)node.getParent()).getChildren().remove(node);//TODO test with instanceOf
			return true;
		}
		return false;
	}
	protected double calculateDrag(double prevLayout,double change,double min) {
		return Math.max(prevLayout+change,min);
	}

	protected static class Coord {
		private double x;
		private double y;
		public Coord() {
			//default constructor
		}
		public Coord(double x,double y) {
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
