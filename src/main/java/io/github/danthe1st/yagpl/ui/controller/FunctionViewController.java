package io.github.danthe1st.yagpl.ui.controller;

import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import io.github.danthe1st.yagpl.api.Function;
import io.github.danthe1st.yagpl.api.GenericObject;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

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
				}
			}
		});
	}
	@FXML
    void onClick(MouseEvent event) {
		if(event.getButton().equals(MouseButton.PRIMARY)&&event.getClickCount() == 2){
			Class<?>[] expectedParameters = function.getExpectedParameters();
			Object[] params;
			if(expectedParameters==null) {
				params=new Object[0];
			}else {
				params=new Object[expectedParameters.length];
				for (int i = 0; i < expectedParameters.length; i++) {
					if(expectedParameters[i]==String.class||expectedParameters[i]==Object.class) {
						Dialog<String> prompt=new TextInputDialog();
						Optional<String> param = prompt.showAndWait();
						params[i]=param.isPresent()?param.get():null;
					}
				}
			}
            try {
            	System.out.println(Arrays.toString(params));
				function.execute(null, params);
			} catch (YAGPLException e) {
				e.printStackTrace();
			}
        }
    }
}
