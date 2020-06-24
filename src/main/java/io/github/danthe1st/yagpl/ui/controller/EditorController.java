package io.github.danthe1st.yagpl.ui.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.github.danthe1st.yagpl.api.Function;
import io.github.danthe1st.yagpl.api.GenericObject;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class EditorController extends ControllerAdapter<AnchorPane>{
	
	private Map<GenericObject<?, ?>, Node> nodeIndex=new HashMap<>();

    @FXML
    private AnchorPane editorPane;
    
    public <R> void addFunction(Function<R, ?> func) throws IOException {
    	FunctionViewController<R> functionView = main.loadView("FunctionView");
    	functionView.setEditor(this);
    	functionView.setFunction(func);
    	nodeIndex.put(func, functionView.getView());
    	editorPane.getChildren().add(functionView.getView());
    }
    public <R> void addToEndOfFunction(Function<R, ?> func,GenericObject<?, R> toAdd) {
    	//TODO
    }
    public Node getUIElement(GenericObject<?, ?> obj,String[] params) {
    	Node element=nodeIndex.get(obj);
    	if(element==null) {
    		HBox box=new HBox();
        	box.setSpacing(10);
        	box.setBorder(new Border(new BorderStroke(null, BorderStrokeStyle.SOLID, null, null)));
        	Label label=new Label(obj.getName());
        	label.setFont(Font.font(label.getFont().getFamily(),FontWeight.BOLD,label.getFont().getSize()));
        	box.getChildren().add(label);
        	Class<?>[] expectedParameters = obj.getExpectedParameters();
        	if(expectedParameters==null) {
        		for (String param : params) {
    				box.getChildren().add(new Label(param));
    			}
        		box.getChildren().add(new Label("<...>"));
        	}else {
        		for (int i = 0; i < expectedParameters.length; i++) {
    				Class<?> paramCl = expectedParameters[i];
    				String text="<"+paramCl.getSimpleName()+">";
        			if(params.length>i&&params[i]!=null) {
        				text=params[i];
        			}
        			box.getChildren().add(new Label(text));
    			}
        	}
        	nodeIndex.put(obj,box);
        	element=box;
    	}
    	
    	return element;
    }
}
