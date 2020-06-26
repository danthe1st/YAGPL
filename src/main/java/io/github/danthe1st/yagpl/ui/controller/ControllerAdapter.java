package io.github.danthe1st.yagpl.ui.controller;

import io.github.danthe1st.yagpl.api.GlobalContext;
import io.github.danthe1st.yagpl.ui.YAGPL;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

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
		error(text);
		e.printStackTrace();
	}
	
	protected void allowDrag(Node node) {
		// Custom object to hold x and y positions
		final Delta dragDelta = new Delta();

		node.setOnMousePressed(e -> fillDeltaWithNodePosition(dragDelta,node,e));
		setDragUpdate(node,dragDelta);
		node.setOnMouseReleased(e->removeIfTooFarLeft(node));
	}
	protected void setDragUpdate(Node node, Delta dragDelta) {
		node.setOnMouseDragged(e -> {
			node.setLayoutX(dragDelta.x+e.getSceneX());
			node.setLayoutY(calculateDrag(dragDelta.y,e.getSceneY(),0));
		});
	}
	protected void addElementToPaneAndFillDeltaWithPosition(Delta delta,Node node, Pane pane,MouseEvent e) {
		pane.getChildren().add(node);
		Bounds boundsInScene=node.localToScene(node.getBoundsInLocal());
		node.setLayoutX(e.getSceneX()-boundsInScene.getMinX());
		node.setLayoutY(e.getSceneY()-boundsInScene.getMinY());
		fillDeltaWithNodePosition(delta,node,e);
	}
	protected void fillDeltaWithNodePosition(Delta delta, Node node,MouseEvent e) {
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

	protected static class Delta {
		private double x;
		private double y;
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
