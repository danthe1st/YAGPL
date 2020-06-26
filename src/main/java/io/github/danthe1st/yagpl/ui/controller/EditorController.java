package io.github.danthe1st.yagpl.ui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.collections4.map.AbstractReferenceMap.ReferenceStrength;
import org.apache.commons.collections4.map.ReferenceMap;

import io.github.danthe1st.yagpl.api.Function;
import io.github.danthe1st.yagpl.api.GenericObject;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class EditorController extends ControllerAdapter<AnchorPane> implements Initializable {

	private Map<GenericObject<?, ?>, Node> nodeIndex = new ReferenceMap<>(ReferenceStrength.WEAK,
			ReferenceStrength.WEAK);

	@FXML
	private ListView<Map.Entry<GenericObject<?, ?>, String[]>> availableElementView;

	private ObservableList<Map.Entry<GenericObject<?, ?>, String[]>> availableElements = FXCollections
			.observableArrayList();

	@FXML
	private AnchorPane editorPane;

	public <R> void addFunction(Function<R, ?> func) throws IOException {
		FunctionViewController<R> functionView = main.loadView("FunctionView");
		functionView.setEditor(this);
		functionView.setFunction(func);
		nodeIndex.put(func, functionView.getView());
		editorPane.getChildren().add(functionView.getView());
		allowDrag(functionView.getView());
	}

	public Node getUIElement(GenericObject<?, ?> obj, String[] params) {
		Node element = nodeIndex.get(obj);
		if (element == null) {
			HBox box = new HBox();
			box.setSpacing(10);
			box.setBorder(new Border(new BorderStroke(null, BorderStrokeStyle.SOLID, null, null)));
			Label label = new Label(obj.getName());
			label.setFont(Font.font(label.getFont().getFamily(), FontWeight.BOLD, label.getFont().getSize()));
			box.getChildren().add(label);
			Class<?>[] expectedParameters = obj.getExpectedParameters();
			if (expectedParameters == null) {
				for (String param : params) {
					box.getChildren().add(new Label(param));
				}
				box.getChildren().add(new Label("<...>"));
			} else {
				for (int i = 0; i < expectedParameters.length; i++) {
					Class<?> paramCl = expectedParameters[i];
					String text = "<" + paramCl.getSimpleName() + ">";
					if (params.length > i && params[i] != null) {
						text = params[i];
					}
					box.getChildren().add(new Label(text));
				}
			}
			nodeIndex.put(obj, box);
			element = box;
		}

		return element;
	}

	public void setAvailableElements(Map<GenericObject<?, ?>, String[]> available) {
		availableElements.clear();
		availableElements.addAll(available.entrySet());
		available.forEach(this::allowCopyDrag);
	}

	private void allowCopyDrag(GenericObject<?, ?> obj, String[] params) {
		Node outerNode = getUIElement(obj, params);
		outerNode.setOnMousePressed(e -> {
			final Delta dragDelta = new Delta();
			try {
				Node node = getUIElement(obj.createCopy(), params);
				addElementToPaneAndFillDeltaWithPosition(dragDelta, node, editorPane, e);
				outerNode.setOnMouseDragged(evt -> {
					node.setLayoutX(dragDelta.getX() + evt.getSceneX());
					node.setLayoutY(calculateDrag(dragDelta.getY(), evt.getSceneY(), 0));
				});
				outerNode.setOnMouseReleased(evt -> {
					if (!removeIfTooFarLeft(node)) {
						allowDrag(node);
					}
				});

			} catch (YAGPLException e1) {
				error("Cannot create copy", e1);
			}
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		availableElementView.setItems(availableElements);
		availableElementView.setCellFactory(param -> new ListCell<Map.Entry<GenericObject<?, ?>, String[]>>() {
			@Override
			protected void updateItem(Map.Entry<GenericObject<?, ?>, String[]> item, boolean empty) {
				if (item != null) {
					this.setGraphic(getUIElement(item.getKey(), item.getValue()));
				}
			}
		});
	}

	public Pane getEditorPane() {
		return editorPane;
	}
}
