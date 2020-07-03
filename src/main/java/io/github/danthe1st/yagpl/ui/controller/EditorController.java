package io.github.danthe1st.yagpl.ui.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import javafx.event.ActionEvent;
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

	private Map<String, FunctionViewController<?>> functions = new ReferenceMap<>(ReferenceStrength.WEAK,
			ReferenceStrength.WEAK);

	@FXML
	private ListView<ParameterizedGenericObject<?, ?>> availableElementView;

	private ObservableList<ParameterizedGenericObject<?, ?>> availableElements = FXCollections.observableArrayList();

	@FXML
	private AnchorPane editorPane;

	@FXML
	void save(ActionEvent event) {
		try (ObjectOutputStream oos = new ObjectOutputStream(
				new BufferedOutputStream(new FileOutputStream("program.dat")))) {
			Map<String, FunctionViewController<?>> copy = new HashMap<>(functions);
			oos.writeInt(copy.size());
			for (FunctionViewController<?> functionViewCtl : copy.values()) {
				functionViewCtl.save(oos);
			}
		} catch (IOException e) {
			error("Cannot write function", e);
		}
	}

	public void load() {
		File file = new File("program.dat");
		if (file.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
				int size = ois.readInt();
				for (int i = 0; i < size; i++) {
					addFunction((Function<?, ?>) ois.readObject());
				}
			} catch (IOException | ClassNotFoundException e) {
				error("loading failed", e);
			}
		}
	}
	private <R> FunctionViewController<R> loadFunctionElement(Function<R, ?> func) throws IOException {
		FunctionViewController<R> functionView = main.loadView("FunctionView");
		functionView.setEditor(this);
		functionView.setFunction(func);
		nodeIndex.put(func, functionView.getView());
		allowDrag(functionView.getView());
		functions.put(func.getName(), functionView);
		return functionView;
	}
	public <R> FunctionViewController<R> addFunction(Function<R, ?> func) throws IOException {
		FunctionViewController<R> functionView = loadFunctionElement(func);
		editorPane.getChildren().add(functionView.getView());
		return functionView;
	}

	public Node getUIElement(ParameterizedGenericObject<?, ?> uiExpr) {
		Node element = nodeIndex.get(uiExpr.getObj());
		if (element == null) {
			if (uiExpr.getObj() instanceof Function) {
				try {
					element=loadFunctionElement((Function<?, ?>) uiExpr.getObj()).getView();
				} catch (IOException e) {
					fatal("cannot load function", e);
				}
			} else {

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
					Label anyArgsLabel = new Label("<...>");// TODO also for available elements..?
					box.getChildren().add(anyArgsLabel);
					anyArgsLabel.setOnMouseClicked(evt -> {
						if (evt.getClickCount() == 2) {
							Node newLabel = createParameterLabel(Object.class, null, value -> uiExpr
									.setParams(copyArrayAndAddElement(uiExpr.getParams(), value, String[].class)));
							box.getChildren().add(box.getChildren().size() - 1, newLabel);
							newLabel.getOnMouseClicked().handle(evt);
						}
					});
				} else {

					for (int i = 0; i < expectedParameters.length; i++) {
						final int iCopy = i;// FIXME do not work on copy of params but real params..?
						box.getChildren()
								.add(createParameterLabel(expectedParameters[i],
										uiExpr.getParams().length > i ? uiExpr.getParams()[i] : null,
										value -> uiExpr.getParams()[iCopy] = value));
					}

				}
				element = box;
				nodeIndex.put(uiExpr.getObj(), element);
			}
			

		}
		return element;
	}

	private <T> T[] copyArrayAndAddElement(T[] arr, T additionalArgument, Class<T[]> cl) {
		T[] ret = Arrays.copyOf(arr, arr.length + 1, cl);
		ret[arr.length] = additionalArgument;
		return ret;
	}

	public String loadVariableName(Class<?> type, String varName) {
		String typeName = type == null ? "any" : type.getSimpleName();
		TextInputDialog prompt = new TextInputDialog();
		prompt.setTitle("Variable required");
		prompt.setHeaderText("Please resolve variable " + varName + " (" + typeName + ")");
		Optional<String> varValue = prompt.showAndWait();
		return varValue.isPresent() && !"".equals(varValue.get()) ? varValue.get() : null;
	}

	public Object resolveVariable(Class<?> type, String varName) throws NotResolveableException {
		Object ret;
		TextInputDialog prompt = new TextInputDialog();
		prompt.setTitle("Variable required");
		prompt.setHeaderText("Please resolve variable " + varName + " (" + type.getSimpleName() + ")");
		Optional<String> varValue = prompt.showAndWait();
		ret = varValue.isPresent() ? Resolver.resolveVariable(globalCtx, varValue.get()) : null;
		if (ret != null && !type.isInstance(ret)) {
			throw new NotResolveableException();
		}
		return ret;
	}

	private Node createParameterLabel(Class<?> paramClass, String param, Consumer<String> setter) {
		StringBuilder text;
		if (param == null) {
			text = new StringBuilder("<" + paramClass.getSimpleName() + ">");
		} else {
			text = new StringBuilder(param);
		}
		Label label = new Label(text.toString());
		label.setOnMouseClicked(evt -> {
			if (evt.getClickCount() == 2) {
				String varName = loadVariableName(paramClass, text.toString());
				if (varName != null) {
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
					drop(node, evt, Arrays.asList(uiExpr));
				});
				allowDrag(node);

			} catch (YAGPLException e1) {
				error("Cannot create copy", e1);
			}
		});
	}

	public <T> void allowDrop(Node node, List<ParameterizedGenericObject<?, ?>> toDrop) {
		node.setOnMouseReleased(evt -> {
			drop(node, evt, toDrop);
		});
	}

	public void drop(Node prevNode, MouseEvent evt, List<ParameterizedGenericObject<?, ?>> toDrop) {
		Iterator<FunctionViewController<?>> funcIter = functions.values().iterator();
		boolean goOn = true;
		while (goOn && funcIter.hasNext()) {
			FunctionViewController<?> funcView = funcIter.next();
			try {
				goOn = !addCopiesToFuncViewIfIntersects(evt, funcView, toDrop);
			} catch (YAGPLException e1) {
				e1.printStackTrace();// TODO
			}
		}
		if (!goOn) {
			editorPane.getChildren().remove(prevNode);
		}
	}

	private static <T> boolean addCopiesToFuncViewIfIntersects(MouseEvent evt, FunctionViewController<T> ctl,
			List<ParameterizedGenericObject<?, ?>> toAdd) throws YAGPLException {
		List<ParameterizedGenericObject<?, T>> toAddChanged = new ArrayList<>();
		for (ParameterizedGenericObject<?, ?> add : toAdd) {
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
				if (!empty && item != null) {
					Node elem;
					if(item.getObj() instanceof Function) {
						if(nodeIndex.containsKey(item.getObj())) {
							elem=getUIElement(item);
						}else {
							elem=new Label("Function");
							nodeIndex.put(item.getObj(), elem);
						}
					}else {
						elem=getUIElement(item);
						if (elem instanceof Parent) {
							for (Node node : ((Parent) elem).getChildrenUnmodifiable()) {
								node.setOnMouseClicked(null);
							}
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
