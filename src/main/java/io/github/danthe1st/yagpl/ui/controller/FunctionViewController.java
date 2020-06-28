package io.github.danthe1st.yagpl.ui.controller;

import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;

import io.github.danthe1st.yagpl.api.Function;
import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.GenericObject;
import io.github.danthe1st.yagpl.api.throwables.NotResolveableException;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;
import io.github.danthe1st.yagpl.api.util.Resolver;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class FunctionViewController<R> extends ControllerAdapter<BorderPane> implements Initializable {

	@FXML
	private Label title;

	@FXML
	private ListView<Map.Entry<GenericObject<?, R>, String[]>> operationBox;

	private Function<R, ?> function;
	private EditorController editor;
    public boolean addIfIntersects(MouseEvent event,List<Map.Entry<GenericObject<?, R>, String[]>> operationsToAdd) {
    	Coord coord = getAbsoluteCoord(operationBox);
    	Bounds bounds=new BoundingBox(coord.getX(), coord.getY(), operationBox.getWidth(), operationBox.getHeight());
    	if(bounds.intersects(event.getSceneX(), event.getSceneY(), 0, 0)) {
    		int index=Math.min((int)((event.getSceneY()-coord.getY())/24),operationBox.getItems().size());
    		operationBox.getItems().addAll(index,operationsToAdd);
    		initialize(null, null);
    		return true;
    	}else {
    		return false;
    	}
    }
	public void setEditor(EditorController editor) {
		this.editor = editor;
	}

	public void setFunction(Function<R, ?> function) {
		this.function = function;
		title.setText(function.getName());
		operationBox.getItems().clear();
		operationBox.getItems().addAll(function.getOperations());
		function.setOperations(operationBox.getItems());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		operationBox.setCellFactory(param -> new ListCell<Map.Entry<GenericObject<?, R>, String[]>>() {
			@Override
			protected void updateItem(Map.Entry<GenericObject<?, R>, String[]> item, boolean empty) {
				if (!empty&&item != null) {
					Node node = editor.getUIElement(item.getKey(), item.getValue());
					this.setGraphic(node);
					node.setOnMousePressed(e->{
						
						VBox box=new VBox();
						boolean take=false;
						Iterator<Entry<GenericObject<?, R>, String[]>> operationIterator = operationBox.getItems().iterator();
						List<Map.Entry<GenericObject<?, ?>, String[]>> elementsInBox=new ArrayList<>();
						while(operationIterator.hasNext()) {
							Entry<GenericObject<?, R>, String[]> operation=operationIterator.next();
							if(!take&&item==operation) {
								take=true;
							}
							if(take) {
								Node uiElement=editor.getUIElement(operation.getKey(), operation.getValue());
								elementsInBox.add(new AbstractMap.SimpleEntry<>(operation.getKey(), operation.getValue()));
								uiElement.setOnMousePressed(null);
								box.getChildren().add(uiElement);
								operationIterator.remove();
							}
						}
						Coord delta=new Coord();
						addElementToPaneAndFillDeltaWithPosition(delta, box, editor.getEditorPane(), e);
						setDragUpdate(box, delta);
						box.setOnMouseReleased(evt->{
							removeIfTooFarLeft(box);
							allowDrag(box);
							editor.allowDrop(box, elementsInBox);
						});
						initialize(null,null);//setCellFactory-->workaround for weird bug with ListView
					});
				}
			}
		});
	}

	@FXML
	void onClick(MouseEvent event) {
		if (event.getButton()==MouseButton.PRIMARY && event.getClickCount() == 2) {
			Class<?>[] expectedParameters = function.getExpectedParameters();
			Object[] params;
			if (expectedParameters == null) {
				params = new Object[0];
			} else {
				params = new Object[expectedParameters.length];
				for (int i = 0; i < expectedParameters.length; i++) {
					boolean ex = false;
					try {
						TextInputDialog prompt = new TextInputDialog();
						prompt.setTitle("Function requires parameters");
						prompt.setHeaderText(
								"Please resolve parameter " + i + " (" + expectedParameters[i].getSimpleName() + ")");
						Optional<String> param = prompt.showAndWait();
						params[i] = param.isPresent() ? Resolver.resolveVariable(globalCtx, param.get()) : null;
						if (params[i] != null && !expectedParameters[i].isInstance(params[i])) {
							ex = true;
						}
					} catch (NotResolveableException e) {
						ex = true;
					}
					if (ex) {
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
}
