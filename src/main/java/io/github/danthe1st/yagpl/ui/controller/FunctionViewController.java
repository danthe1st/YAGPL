package io.github.danthe1st.yagpl.ui.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.github.danthe1st.yagpl.api.Function;
import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.ParameterizedGenericObject;
import io.github.danthe1st.yagpl.api.throwables.NotResolveableException;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FunctionViewController<R> extends ControllerAdapter<BorderPane>{

	@FXML
	private Label title;

	@FXML
	private AnchorPane operationBox;
	private List<ParameterizedGenericObject<?, R>> operations=new ArrayList<>();
	
	private Function<R, ?> function;
	private EditorController editor;
	
	public boolean addIfIntersects(MouseEvent event, List<ParameterizedGenericObject<?, R>> operationsToAdd) {
		Coord coord = getAbsoluteCoord(operationBox);
		Bounds bounds = new BoundingBox(coord.getX(), coord.getY(), operationBox.getWidth(), operationBox.getHeight());
		if (bounds.intersects(event.getSceneX(), event.getSceneY(), 0, 0)) {
			Map.Entry<Integer, Double> posAndIndex=findIndexAndPositionToInsert(event.getSceneY() - coord.getY());
			int index = posAndIndex.getKey();
			operations.addAll(index, operationsToAdd);
			function.getOperations().addAll(index, operationsToAdd);
			for (int i = 0; i < operationsToAdd.size(); i++) {
				ParameterizedGenericObject<?, R> op=operationsToAdd.get(i);
				operationBox.getChildren().add(index+i, editor.getUIElement(op));
				updateElementListeners(op);
			}
			adjustPositionsFrom(index,posAndIndex.getValue());
			return true;
		} else {
			return false;
		}
	}
	private void adjustPositionsFrom(int startIndex,double valueAtStartIndex) {
		System.out.println(operations);
//		startIndex=0;
//		valueAtStartIndex=0;
		operationBox.applyCss();
		operationBox.layout();
		ObservableList<Node> children = operationBox.getChildren();
		for (int i = startIndex; i < children.size(); i++) {
			Node elem=children.get(i);
			AnchorPane.setTopAnchor(elem, valueAtStartIndex);
			valueAtStartIndex+=estimateHeight(elem, 0);
			System.out.println(valueAtStartIndex);
		}
	}
	
	private Map.Entry<Integer, Double> findIndexAndPositionToInsert(double posY) {
		double currentPos=0;
		int i=0;
		for (ParameterizedGenericObject<?, R> parameterizedGenericObject : operations) {
			if((currentPos+=estimateHeight(editor.getUIElement(parameterizedGenericObject), operationBox.getWidth()))>=posY) {
				break;
			}
			i++;
		}
		return new AbstractMap.SimpleEntry<>(i, currentPos);
	}
	
	public void setEditor(EditorController editor) {
		this.editor = editor;
	}

	public void setFunction(Function<R, ?> function) {
		this.function = function;
		title.setText(function.getName());
		operations.clear();
		operations.addAll(function.getOperations());
		operationBox.getChildren().clear();
		for (ParameterizedGenericObject<?, R> op : function.getOperations()) {
			operationBox.getChildren().add(editor.getUIElement(op));
		}
		Platform.runLater(()->{
			adjustPositionsFrom(0, 0);
		});
	}
	private void updateElementListeners(ParameterizedGenericObject<?, R> item) {//TODO use wherever element is added
		Node wholeNode = editor.getUIElement(item);
		Node draggableNode=wholeNode;
		if(wholeNode instanceof HBox) {
			ObservableList<Node> children = ((Parent) wholeNode).getChildrenUnmodifiable();
			if (!children.isEmpty()) {
				draggableNode = children.get(0);
			}
		}
		draggableNode.setOnMousePressed(e -> {
			VBox box = new VBox();
			boolean take = false;
			Iterator<ParameterizedGenericObject<?, R>> operationIterator = operations.iterator();
			List<ParameterizedGenericObject<?, ?>> elementsInBox = new ArrayList<>();
			while (operationIterator.hasNext()) {
				ParameterizedGenericObject<?, R> operation = operationIterator.next();
				if (!take && item == operation) {
					take = true;
				}
				if (take) {
					Node uiElement = editor.getUIElement(operation);
					elementsInBox.add(operation);
					uiElement.setOnMousePressed(null);
					box.getChildren().add(uiElement);
					operationIterator.remove();
				}
			}
			Coord delta = new Coord();
			addElementToPaneAndFillDeltaWithPosition(delta, box, editor.getEditorPane(), e);
			setDragUpdate(box, delta);
			editor.allowDragDrop(box, elementsInBox);
		});
	}
	
	@FXML
	void onClick(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
			Class<?>[] expectedParameters = function.getExpectedParameters();
			Object[] params;
			if (expectedParameters == null) {
				params = new Object[0];
			} else {
				params = new Object[expectedParameters.length];
				for (int i = 0; i < expectedParameters.length; i++) {
					try {
						params[i] = editor.resolveVariable(expectedParameters[i], String.valueOf(i));
					} catch (NotResolveableException e) {
						Alert alert = new Alert(AlertType.ERROR, "Cannot be resolved",
								new ButtonType("set null", ButtonData.APPLY),
								new ButtonType("retry", ButtonData.BACK_PREVIOUS), ButtonType.CANCEL);
						Optional<ButtonType> typeOptional = alert.showAndWait();
						if (typeOptional.isPresent()) {
							ButtonType type = typeOptional.get();
							switch (type.getButtonData()) {
							case APPLY:
								params[i] = null;
								break;
							case BACK_PREVIOUS:
								i--;// NOSONAR It ain't beautiful but it works
								continue;
							case CANCEL_CLOSE:
								return;
							default:
								error("Invalid option");// should never happen
								return;
							}
						} else {
							// same as cancel
							return;
						}
					}
				}
			}
			try {
				function.execute(new FunctionContext<>(globalCtx), params);
			} catch (YAGPLException e) {
				error("An error occured while executing the function", e);
			}
		}
	}

	public void save(ObjectOutputStream oos) throws IOException {
		oos.writeObject(function);
	}
	@SuppressWarnings("unchecked")
	public void load(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		setFunction((Function<R, ?>) ois.readObject());
	}
}
