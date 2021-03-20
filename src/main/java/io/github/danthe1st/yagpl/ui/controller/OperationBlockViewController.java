package io.github.danthe1st.yagpl.ui.controller;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.github.danthe1st.yagpl.api.Expression;
import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.OperationBlock;
import io.github.danthe1st.yagpl.api.ParameterizedGenericObject;
import io.github.danthe1st.yagpl.api.constant.ConstantExpression;
import io.github.danthe1st.yagpl.api.throwables.NotResolveableException;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;
import io.github.danthe1st.yagpl.api.util.Resolver;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * controller for a function view
 * @author dan1st
 *
 */
public class OperationBlockViewController extends ControllerAdapter<BorderPane> {

	@FXML
	private HBox titleBox;

	@FXML
	private Label title;

	@FXML
	private AnchorPane operationBox;
	private List<ParameterizedGenericObject<?>> operations = new ArrayList<>();

	private OperationBlock<?> block;
	private EditorController editor;

	private String[] paramNames;

	/**
	 * adds one or more operations to this function view if the mouse pointer intersects with this function view
	 * @param event the {@link MouseEvent} used for calculating the cursor position
	 * @param operationsToAdd the operations to add to this function view
	 * @return <code>true</code> if the operations were added, else <code>false</code>
	 */
	public boolean addIfIntersects(MouseEvent event, List<ParameterizedGenericObject<?>> operationsToAdd) {
		Position coord = getAbsoluteCoord(operationBox);
		Bounds bounds = new BoundingBox(coord.getX(), coord.getY(), operationBox.getWidth(), operationBox.getHeight());
		if (bounds.intersects(event.getSceneX(), event.getSceneY(), 0, 0)) {
			Map.Entry<Integer, Double> posAndIndex = findIndexAndPositionToInsert(event.getSceneY() - coord.getY());
			int index = posAndIndex.getKey();
			operations.addAll(index, operationsToAdd);
			block.getOperations().addAll(index, operationsToAdd);
			for (int i = 0; i < operationsToAdd.size(); i++) {
				ParameterizedGenericObject<?> op = operationsToAdd.get(i);
				ObservableList<Node> children = operationBox.getChildren();
				children.add(Math.min(index + i, children.size()), editor.getUIElement(op));
				updateElementListeners(op);
			}
			Platform.runLater(() -> adjustPositionsFrom(index, posAndIndex.getValue()));

			return true;
		} else {
			return false;
		}
	}

	/**
	 * adjust the positions of the operations
	 * @param startIndex the start index to adjust operation positions
	 * @param valueAtStartIndex the position of the element at the start index (cumulative height of the elements before it)
	 */
	private void adjustPositionsFrom(int startIndex, double valueAtStartIndex) {
		operationBox.applyCss();
		operationBox.layout();
		ObservableList<Node> children = operationBox.getChildren();
		for (int i = startIndex; i < children.size(); i++) {
			Node elem = children.get(i);
			elem.applyCss();
			if (elem instanceof Pane) {
				((Pane) elem).layout();
			}
			AnchorPane.setTopAnchor(elem, valueAtStartIndex);
			valueAtStartIndex += estimateHeight(elem, 0);
		}

		operationBox.setPrefHeight(valueAtStartIndex + 20);
	}

	/**
	 * finds the index and the position to insert an element
	 * @param posY the position of the element
	 * @return a {@link Map.Entry} containing the index and the position of the element to insert
	 */
	private Map.Entry<Integer, Double> findIndexAndPositionToInsert(double posY) {
		double currentPos = 0;
		int i = 0;
		for (ParameterizedGenericObject<?> operation : operations) {
			if ((currentPos += estimateHeight(editor.getUIElement(operation), operationBox.getWidth())) >= posY) {
				break;
			}
			i++;
		}
		return new AbstractMap.SimpleEntry<>(i, currentPos);
	}

	public void setEditor(EditorController editor) {
		this.editor = editor;
	}

	/**
	 * sets the {@link OperationBlock} of this function view
	 * @param function the new {@link OperationBlock}
	 * @param paramNames the names of the parameters to set
	 */
	public void setOperationBlock(OperationBlock<?> function, String[] paramNames) {
		this.block = function;
		titleBox.getChildren().clear();
		titleBox.getChildren().add(title);
		title.setText(function.getName());
		operations.clear();
		operations.addAll(function.getOperations());
		operationBox.getChildren().clear();
		if (block.getExpectedParameters() == null) {
			//TODO varargs
		} else {
			Class<?>[] expectedParameters = block.getExpectedParameters();
			this.paramNames = paramNames;
			for (int i = 0; i < expectedParameters.length; i++) {
				final int index = i;
				Class<?> param = expectedParameters[i];
				Node paramLabel = editor.createParameterLabel(param,
						paramNames[index] == null ? (param == null ? "<?>" : "<" + param.getSimpleName() + ">")
								: paramNames[index],
						resolvedParam -> {
							paramNames[index] = resolvedParam;
						});
				titleBox.getChildren().add(paramLabel);
			}
		}

		for (ParameterizedGenericObject<?> op : function.getOperations()) {
			operationBox.getChildren().add(editor.getUIElement(op));
			updateElementListeners(op);
		}
		Platform.runLater(() -> adjustPositionsFrom(0, 0));
	}

	/**
	 * updates the drag/drop listeners of the UI of a {@link ParameterizedGenericObject}
	 * @param item the {@link ParameterizedGenericObject}
	 */
	private void updateElementListeners(ParameterizedGenericObject<?> item) {
		Node wholeNode = editor.getUIElement(item);
		if (wholeNode instanceof HBox) {
			ObservableList<Node> children = ((Parent) wholeNode).getChildrenUnmodifiable();
			if (!children.isEmpty()) {
				wholeNode = children.get(0);
			}
		}
		Node draggableNode = wholeNode;
		draggableNode.setOnMousePressed(e -> {
			VBox box = new VBox();
			boolean take = false;
			Iterator<ParameterizedGenericObject<?>> operationIterator = operations.iterator();
			List<ParameterizedGenericObject<?>> elementsInBox = new ArrayList<>();
			while (operationIterator.hasNext()) {
				ParameterizedGenericObject<?> operation = operationIterator.next();
				if (!take && item == operation) {
					take = true;
				}
				if (take) {
					Node uiElement = editor.getUIElement(operation);
					elementsInBox.add(operation);
					uiElement.setOnMousePressed(null);
					box.getChildren().add(uiElement);
					operationIterator.remove();
					adjustPositionsFrom(0, 0);//adjust height of box
				}
			}
			Position delta = addElementToPaneAndFillDeltaWithPosition(box, editor.getEditorPane(), e);

			draggableNode.setOnMouseReleased(evt -> {
				editor.allowDragDrop(box, elementsInBox);
			});
			setDragUpdate(box, delta);
		});

	}

	/**
	 * runs the operation block if it is double-clicked
	 * @param event the {@link MouseEvent} to check the click
	 */
	@FXML
	void onClick(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
			Class<?>[] expectedParameters = block.getExpectedParameters();
			Object[] params;
			if (expectedParameters == null) {
				params = new Object[0];
			} else {
				params = new Object[expectedParameters.length];
				for (int i = 0; i < expectedParameters.length; i++) {
					try {
						if (paramNames[i] == null) {
							params[i] = editor.resolveVariable(expectedParameters[i], String.valueOf(i));
						} else {
							params[i] = Resolver.resolveVariable(globalCtx, paramNames[i]);
						}
						if (expectedParameters[i].isAssignableFrom(Expression.class)
								&& !(params[i] instanceof Expression)) {
							params[i] = new ConstantExpression<>(params[i]);
						}
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
			editor.exec(block,params);

		}
	}

	public OperationBlock<?> getOperationBlock() {
		return block;
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
}
