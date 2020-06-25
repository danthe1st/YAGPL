package io.github.danthe1st.yagpl.ui.controller;

import java.net.URL;
import java.util.Map;
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

public class FunctionViewController<R> extends ControllerAdapter<BorderPane> implements Initializable {

	@FXML
	private Label title;

	@FXML
	private ListView<Map.Entry<GenericObject<?, R>, String[]>> operationBox;

	private Function<R, ?> function;
	private EditorController editor;

	@FXML
	void dragOut(MouseEvent event) {

	}

	public void setEditor(EditorController editor) {
		this.editor = editor;
	}

	public void setFunction(Function<R, ?> function) {
		this.function = function;
		title.setText(function.getName());
		operationBox.getItems().clear();
		operationBox.getItems().addAll(function.getOperations());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		operationBox.setCellFactory(param -> new ListCell<Map.Entry<GenericObject<?, R>, String[]>>() {
			@Override
			protected void updateItem(Map.Entry<GenericObject<?, R>, String[]> item, boolean empty) {
				if (item != null) {
					this.setGraphic(editor.getUIElement(item.getKey(), item.getValue()));
					//TODO allow drag out
				}
			}
		});
	}

	@FXML
	void onClick(MouseEvent event) {
		if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {

			Class<?>[] expectedParameters = function.getExpectedParameters();
			Object[] params;
			if (expectedParameters == null) {
				params = new Object[0];
			} else {
				params = new Object[expectedParameters.length];
				for (int i = 0; i < expectedParameters.length; i++) {
					boolean ex=false;
					try {
						TextInputDialog prompt = new TextInputDialog();
						prompt.setTitle("Function requires parameters");
						prompt.setHeaderText(
								"Please resolve parameter " + i + " (" + expectedParameters[i].getSimpleName() + ")");
						Optional<String> param = prompt.showAndWait();
						params[i] = param.isPresent() ? Resolver.resolveVariable(globalCtx, param.get()) : null;
						if(params[i]!=null&&!expectedParameters[i].isInstance(params[i])) {
							ex=true;
						}
					} catch (NotResolveableException e) {
						ex=true;
					}
					if(ex) {
						Alert alert=new Alert(AlertType.ERROR,"Cannot be resolved",new ButtonType("set null",ButtonData.APPLY),new ButtonType("retry",ButtonData.BACK_PREVIOUS),ButtonType.CANCEL);
						Optional<ButtonType> typeOptional = alert.showAndWait();
						if(typeOptional.isPresent()) {
							ButtonType type=typeOptional.get();
							switch (type.getButtonData()) {
							case APPLY:
								params[i] = null;
								break;
							case BACK_PREVIOUS:
								i--;//NOSONAR It ain't beautiful but it works
								continue;
							case CANCEL_CLOSE:
								return;
							default:
								error("Invalid option");// should never happen
								return;
							}
						}else {
							//same as cancel
							return;
						}
					}
				}
			}
			try {
				function.execute(new FunctionContext<>(globalCtx), params);
			} catch (YAGPLException e) {
				error("An error occured while executing the function",e);
			}

		}
	}
}
