package io.github.danthe1st.yagpl.ui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.apache.commons.collections4.map.AbstractReferenceMap.ReferenceStrength;
import org.apache.commons.collections4.map.ReferenceMap;

import io.github.danthe1st.yagpl.api.Function;
import io.github.danthe1st.yagpl.api.GenericObject;
import io.github.danthe1st.yagpl.api.ParameterizedGenericObject;
import io.github.danthe1st.yagpl.api.throwables.NotResolveableException;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;
import io.github.danthe1st.yagpl.api.util.Resolver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
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

	private Map<String, FunctionViewController<?>> functions = new ReferenceMap<>(ReferenceStrength.WEAK, ReferenceStrength.WEAK);

	@FXML
	private ListView<ParameterizedGenericObject<?, ?>> availableElementView;

	private ObservableList<ParameterizedGenericObject<?, ?>> availableElements = FXCollections
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
		functions.put(func.getName(), functionView);
	}
	public Node getUIElement(ParameterizedGenericObject<?, ?> uiExpr) {
		Node element = nodeIndex.get(uiExpr.getObj());
		if (element == null) {
			HBox box = new HBox();
			box.setSpacing(10);
			box.setBorder(new Border(new BorderStroke(null, BorderStrokeStyle.SOLID, null, null)));
			Label label = new Label(uiExpr.getObj().getName());
			label.setFont(Font.font(label.getFont().getFamily(), FontWeight.BOLD, label.getFont().getSize()));
			box.getChildren().add(label);
			Class<?>[] expectedParameters = uiExpr.getObj().getExpectedParameters();
			if (expectedParameters == null) {
				for (String param : uiExpr.getParams()) {
					box.getChildren().add(new Label(param));
				}
				Label anyArgsLabel=new Label("<...>");//TODO also for available elements..?
				anyArgsLabel.setOnMouseClicked(evt->{
					if(evt.getClickCount()==2) {
						String varName=loadVariableName(null, "any");
						System.out.println(box.getParent());
						copyArrayAndAddElement(uiExpr.getParams(),varName,String[].class);
						anyArgsLabel.setText(varName);
						copyArrayAndAddElement(uiExpr.getParams(), varName, String[].class);//TODO set real parameters in function
					}
				});
				
			} else {
				
				for (int i = 0; i < expectedParameters.length; i++) {
					final int iCopy=i;//FIXME do not work on copy of params but real params..?
					box.getChildren().add(createParameterLabel(expectedParameters[i],uiExpr.getParams().length>i?uiExpr.getParams()[i]:null,value->uiExpr.getParams()[iCopy]=value));
				}
				
			}
			nodeIndex.put(uiExpr.getObj(), box);
			element = box;
		}
		return element;
	}
	private <T> T[] copyArrayAndAddElement(T[] arr, T additionalArgument,Class<T[]> cl) {
		T[] ret=Arrays.copyOf(arr, arr.length+1, cl);
		ret[arr.length]=additionalArgument;
		return ret;
	}
	public String loadVariableName(Class<?> type,String varName) {
		TextInputDialog prompt = new TextInputDialog();
		prompt.setTitle("Variable required");
		prompt.setHeaderText(
				"Please resolve variable " + varName + " (" + type.getSimpleName() + ")");
		Optional<String> varValue = prompt.showAndWait();
		return varValue.isPresent()&&!"".equals(varValue.get())?varValue.get():null;
	}
	public Object resolveVariable(Class<?> type,String varName) throws NotResolveableException {
		Object ret;
		TextInputDialog prompt = new TextInputDialog();
		prompt.setTitle("Variable required");
		prompt.setHeaderText(
				"Please resolve variable " + varName + " (" + type.getSimpleName() + ")");
		Optional<String> varValue = prompt.showAndWait();
		ret = varValue.isPresent() ? Resolver.resolveVariable(globalCtx, varValue.get()) : null;
		if (ret != null && !type.isInstance(ret)) {
			throw new NotResolveableException();
		}
		return ret;
	}
	private Node createParameterLabel(Class<?> paramClass,String param,Consumer<String> setter) {
		StringBuilder text;
		if (param == null) {
			text=new StringBuilder("<" + paramClass.getSimpleName() + ">");
		}else {
			text = new StringBuilder(param);
		}
		Label label=new Label(text.toString());
		label.setOnMouseClicked(evt->{
			if(evt.getClickCount()==2) {
				String varName=loadVariableName(paramClass, text.toString());
				if(varName!=null) {
					setter.accept(varName);
					label.setText(varName);
					text.setLength(varName.length());
					text.replace(0, varName.length(), varName);
				}
			}
		});
		return label;
	}

	public void setAvailableElements(List<ParameterizedGenericObject<?, ?>> available) {
		availableElements.clear();
		availableElements.addAll(available);
	}

	private void allowCopyDrag(ParameterizedGenericObject<?, ?> uiExpr) {
		Node outerNode = getUIElement(uiExpr);
		outerNode.setOnMousePressed(e -> {
			final Coord dragDelta = new Coord();
			try {
				ParameterizedGenericObject<?, ?> copy = uiExpr.createCopy();
				Node node = getUIElement(copy);
				node.setOnMouseDragOver(System.out::println);
				addElementToPaneAndFillDeltaWithPosition(dragDelta, node, editorPane, e);

				outerNode.setOnMouseDragged(evt -> {
					node.setLayoutX(dragDelta.getX() + evt.getSceneX());
					node.setLayoutY(calculateDrag(dragDelta.getY(), evt.getSceneY(), 0));
				});
				outerNode.setOnMouseReleased(evt -> {
					drop(node,evt,Arrays.asList(uiExpr));
				});
				allowDrag(node);

			} catch (YAGPLException e1) {
				error("Cannot create copy", e1);
			}
		});
	}
	public <T> void allowDrop(Node node,List<ParameterizedGenericObject<?,?>> toDrop) {
		node.setOnMouseReleased(evt -> {
			drop(node,evt,toDrop);
		});
	}
	public void drop(Node prevNode,MouseEvent evt,List<ParameterizedGenericObject<?,?>> toDrop) {
		Iterator<FunctionViewController<?>> funcIter = functions.values().iterator();
		boolean goOn=true;
		while(goOn&&funcIter.hasNext()) {
			FunctionViewController<?> funcView = funcIter.next();
			try {
				goOn=!addCopiesToFuncViewIfIntersects(evt,funcView,toDrop);
			} catch (YAGPLException e1) {
				e1.printStackTrace();//TODO
			}
		}
		if(!goOn) {
			editorPane.getChildren().remove(prevNode);
		}
	}

	private static <T> boolean addCopiesToFuncViewIfIntersects(MouseEvent evt,FunctionViewController<T> ctl,List<ParameterizedGenericObject<?,?>> toAdd) throws YAGPLException {
		List<ParameterizedGenericObject<?,T>> toAddChanged=new ArrayList<>();
		for(ParameterizedGenericObject<?,?> add:toAdd) {
			toAddChanged.add(add.createCopy());
		}
		return ctl.addIfIntersects(evt, toAddChanged);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		availableElementView.setItems(availableElements);
		availableElementView.setCellFactory(param -> new ListCell<ParameterizedGenericObject<?, ?>>() {
			@Override
			protected void updateItem(ParameterizedGenericObject<?, ?> item, boolean empty) {
				if (!empty&&item != null) {
					Node elem=getUIElement(item);
					if(elem instanceof Parent) {
						for(Node node:((Parent) elem).getChildrenUnmodifiable()) {
							node.setOnMouseClicked(null);
						}
					}
					this.setGraphic(elem);
					allowCopyDrag(item);
				}
			}
		});
	}

	public Pane getEditorPane() {
		return editorPane;
	}

}
