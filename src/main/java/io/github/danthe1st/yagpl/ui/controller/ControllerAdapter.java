package io.github.danthe1st.yagpl.ui.controller;

import io.github.danthe1st.yagpl.api.GlobalContext;
import io.github.danthe1st.yagpl.ui.YAGPL;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

		node.setOnMousePressed(e -> {
			dragDelta.x = node.getLayoutX() - e.getSceneX();
			dragDelta.y = node.getLayoutY() - e.getSceneY();
		});

		node.setOnMouseDragged(e -> {
			System.out.println("drag");
			node.setLayoutX(dragDelta.x+e.getSceneX());
			node.setLayoutY(calculateDrag(dragDelta.y,e.getSceneY(),0));
		});
		node.setOnMouseReleased(e->{
			if(node.getLayoutX()<0) {
				((Pane)node.getParent()).getChildren().remove(node);//TODO test with instanceOf
			}
		});
	}
	protected double calculateDrag(double prevLayout,double change,double min) {
		return Math.max(prevLayout+change,min);
	}

	protected class Delta {
		double x;
		double y;
	}
}
