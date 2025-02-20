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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.apache.commons.collections4.map.AbstractReferenceMap.ReferenceStrength;
import org.apache.commons.collections4.map.ReferenceMap;

import io.github.danthe1st.yagpl.api.Expression;
import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.GenericObject;
import io.github.danthe1st.yagpl.api.OperationBlock;
import io.github.danthe1st.yagpl.api.ParameterizedGenericObject;
import io.github.danthe1st.yagpl.api.blocks.Function;
import io.github.danthe1st.yagpl.api.constant.ConstantExpression;
import io.github.danthe1st.yagpl.api.throwables.NotResolveableException;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;
import io.github.danthe1st.yagpl.api.util.Resolver;
import javafx.application.Platform;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * controller class for the editor pane
 * @author dan1st
 */
public class EditorController extends ControllerAdapter<BorderPane> implements Initializable {

	/**
	 * a lookup table used for finding the node associated with a {@link GenericObject}
	 */
	private Map<GenericObject<?>, Node> nodeIndex = new ReferenceMap<>(ReferenceStrength.WEAK, ReferenceStrength.WEAK);

	/**
	 * a lookup table to find the controller managing an {@link OperationBlock}
	 */
	private Map<OperationBlock<?>, OperationBlockViewController> operationBlocks = new ReferenceMap<>(ReferenceStrength.WEAK,
			ReferenceStrength.WEAK);

	/**
	 * a {@link ListView} containing the template elements
	 */
	@FXML
	private ListView<ParameterizedGenericObject<?>> availableElementView;

	/**
	 * a list containing the template elements
	 */
	private ObservableList<ParameterizedGenericObject<?>> availableElements = FXCollections.observableArrayList();

	@FXML
	private AnchorPane editorPane;
	
	private ExecutorService execThreadPool = Executors.newSingleThreadExecutor(this::createThread);

	private Future<?> currentTask;

	/**
	 * saves the current operation blocks for later use
	 * @param event unused
	 */
	@FXML
	void save(ActionEvent event) {
		try (ObjectOutputStream oos = new ObjectOutputStream(
				new BufferedOutputStream(new FileOutputStream("program.dat")))) {
			Map<OperationBlock<?>, OperationBlockViewController> copy = new HashMap<>(operationBlocks);
			oos.writeInt(copy.size());
			for (OperationBlockViewController operationBlockViewCtl : copy.values()) {
				oos.writeObject(operationBlockViewCtl.getOperationBlock());
				oos.writeDouble(view.getLayoutX());
				oos.writeDouble(view.getLayoutY());
				oos.writeObject(operationBlockViewCtl.getParamNames());
			}
		} catch (IOException e) {
			error("Cannot write operation block", e);
		}
	}

	/**
	 * loads operation blocks saved previously
	 */
	public void load() {
		File file = new File("program.dat");
		if (file.exists()) {
			try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {

				int size = ois.readInt();
				for (int i = 0; i < size; i++) {
					addOperationBlock((OperationBlock<?>) ois.readObject(), ois.readDouble(), ois.readDouble(),
							(String[]) ois.readObject());
				}
			} catch (IOException | ClassNotFoundException e) {
				error("loading failed", e);
			}
		}
	}

	/**
	 * loads the UI element of an {@link OperationBlock}
	 * @param func the {@link OperationBlock} to load the UI for
	 * @param paramNames the names of the parameters for this {@link OperationBlock}
	 * @return the controller of the loaded {@link OperationBlock}
	 * @throws IOException if the controller cannot be loaded
	 */
	private OperationBlockViewController loadOperationBlock(OperationBlock<?> func, String[] paramNames)
			throws IOException {
		OperationBlockViewController functionView = main.loadView("OperationBlockView");
		functionView.setEditor(this);
		functionView.setOperationBlock(func, paramNames);
		nodeIndex.put(func, functionView.getView());
		allowDrag(functionView.getView());
		if (!(func instanceof Function<?>)) {
			functionView.getView().setOnMouseReleased(evt -> {
				drop(functionView.getView(), evt,
						Arrays.asList(new ParameterizedGenericObject<>(func, functionView.getParamNames())));
				functionView.getView().setOnMouseReleased(null);
			});
		}
		operationBlocks.put(func, functionView);
		nodeIndex.put(func, functionView.getView());

		return functionView;
	}

	/**
	 * adds an operation block view at specific coordinates
	 * @param func the {@link OperationBlock} to add
	 * @param x the x coordinate of the position where the view should be added to
	 * @param y the y coordinate of the position where the view should be added to
	 * @param params the parameter names of the {@link OperationBlock}
	 * @return the controller of the created view
	 * @throws IOException if the view cannot be loaded
	 */
	public OperationBlockViewController addOperationBlock(OperationBlock<?> func, double x, double y, String[] params)
			throws IOException {
		OperationBlockViewController operationBlockViewView = loadOperationBlock(func, params);
		Node view = operationBlockViewView.getView();
		view.setLayoutX(x);
		view.setLayoutY(y);
		editorPane.getChildren().add(view);
		return operationBlockViewView;
	}

	/**
	 * gets the UI element of a {@link ParameterizedGenericObject} or creates one if it does not exist
	 * @param uiExpr the {@link ParameterizedGenericObject} to load the UI for
	 * @return the {@link Node} containing the UI for the {@link ParameterizedGenericObject}
	 */
	public Node getUIElement(ParameterizedGenericObject<?> uiExpr) {
		Node element = nodeIndex.get(uiExpr.getObj());
		if (element == null) {
			if (uiExpr.getObj() instanceof OperationBlock) {
				try {
					element = loadOperationBlock((OperationBlock<?>) uiExpr.getObj(), uiExpr.getParams()).getView();
				} catch (IOException e) {
					fatal("cannot load operation block", e);
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
						Label l = new Label(param);
						box.getChildren().add(l);
					}
					Label anyArgsLabel = new Label("<...>");// TODO also for available elements..?
					box.getChildren().add(anyArgsLabel);
					anyArgsLabel.setOnMouseClicked(evt -> {
						if (evt.getClickCount() == 2) {
							Node newLabel = createParameterLabel(Object.class, null, value -> uiExpr
									.setParams(copyArrayAndAddElement(uiExpr.getParams(), value)));
							box.getChildren().add(box.getChildren().size() - 1, newLabel);
							newLabel.getOnMouseClicked().handle(evt);
						}
					});
				} else {

					for (int i = 0; i < expectedParameters.length; i++) {
						final int iCopy = i;
						box.getChildren()
								.add(createParameterLabel(expectedParameters[i],
										uiExpr.getParams().length > i ? uiExpr.getParams()[i] : null,
										value -> uiExpr.getParams()[iCopy] = value));
					}
				}
				box.applyCss();
				box.layout();
				element = box;
				nodeIndex.put(uiExpr.getObj(), element);
			}
			element.setViewOrder(1);
		}
		return element;
	}

	/**
	 * copies an array and adds an element to it
	 * @param <T> the type of the array to copy
	 * @param arr the array to copy
	 * @param additionalArgument the element to add
	 * @return a new array with the elements of the old array and a new element
	 */
	private <T> T[] copyArrayAndAddElement(T[] arr, T additionalArgument) {
		@SuppressWarnings("unchecked")//the type is the same as the type of the array
		Class<? extends T[]> cl=(Class<? extends T[]>)arr.getClass();
		if(!cl.componentType().isInstance(additionalArgument)) {
			throw new IllegalArgumentException(additionalArgument+" is not an instance of "+arr);
		}
		T[] ret = Arrays.copyOf(arr, arr.length + 1, cl);
		ret[arr.length] = additionalArgument;
		return ret;
	}

	/**
	 * loads the name of a variable
	 * @param type the type of the variable
	 * @param varName the old name of the variable
	 * @return the new name of the variable
	 */
	public String loadVariableName(Class<?> type, String varName) {
		String typeName = type == null ? "any" : type.getSimpleName();
		TextInputDialog prompt = new TextInputDialog();
		prompt.setTitle("Variable required");
		prompt.setHeaderText("Please resolve variable " + varName + " (" + typeName + ")");
		Optional<String> varValue = prompt.showAndWait();
		return varValue.isPresent() && !"".equals(varValue.get()) ? varValue.get() : null;
	}

	/**
	 * resolves the value of a global variable
	 * @param type the type of the variable
	 * @param varName the name of the variable
	 * @return the value of the variable
	 * @throws NotResolveableException if the variable cannot be resolved
	 */
	public Object resolveVariable(Class<?> type, String varName) throws NotResolveableException {
		Object ret;
		TextInputDialog prompt = new TextInputDialog();
		prompt.setTitle("Variable required");
		prompt.setHeaderText("Please resolve variable " + varName + " (" + type.getSimpleName() + ")");
		Optional<String> varValue = prompt.showAndWait();
		ret = varValue.isPresent() ? Resolver.resolveVariable(globalCtx, varValue.get()) : null;
		if (type.isAssignableFrom(Expression.class)) {
			ret = new ConstantExpression<>(ret);
		}
		if (ret != null && !type.isInstance(ret)) {
			throw new NotResolveableException();
		}
		return ret;
	}

	/**
	 * creates an editable label for a parameter
	 * @param paramClass the type of the parameter
	 * @param param the name of the parameter
	 * @param setter a {@link Consumer} that is executed when the parameter is changed
	 * @return the parameter label
	 */
	public Node createParameterLabel(Class<?> paramClass, String param, Consumer<String> setter) {
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
					if (setter != null) {
						setter.accept(varName);
					}
					label.setText(varName);
					text.setLength(varName.length());
					text.replace(0, varName.length(), varName);
				}
			}
		});
		return label;
	}

	/**
	 * sets the template elements
	 * @param available the template elements
	 */
	public void setAvailableElements(List<ParameterizedGenericObject<?>> available) {
		availableElements.clear();
		availableElements.addAll(available);
	}

	/**
	 * configures a node so that it can be dragged while a copy of the element is created when dragging it
	 * @param uiExpr the {@link ParameterizedGenericObject} to allow copy-dragging
	 */
	private void allowCopyDrag(ParameterizedGenericObject<?> uiExpr) {
		Node outerNode = getUIElement(uiExpr);
		outerNode.setOnMousePressed(e -> {
			try {
				ParameterizedGenericObject<?> copy = uiExpr.createCopy();
				Node node = getUIElement(copy);
				final Position dragDelta=addElementToPaneAndFillDeltaWithPosition(node, editorPane, e);

				outerNode.setOnMouseDragged(evt -> {
					node.setLayoutX(dragDelta.getX() + evt.getSceneX());
					node.setLayoutY(calculateDrag(dragDelta.getY(), evt.getSceneY(), 0));
				});
				allowDragDrop(node, Arrays.asList(copy));
				outerNode.setOnMouseReleased(evt -> drop(node, evt, Arrays.asList(copy)));

			} catch (YAGPLException e1) {
				error("Cannot create copy", e1);
			}
		});
	}

	/**
	 * allow dragging and dropping
	 * @param node the node that should be dragged/dropped
	 * @param toDrop the elements to drop when the node is dropped in an {@link OperationBlock}
	 */
	public void allowDragDrop(Node node, List<ParameterizedGenericObject<?>> toDrop) {
		allowDrag(node);
		node.setOnMouseReleased(evt -> drop(node, evt, toDrop));
	}

	/**
	 * drops an element
	 * @param node the UI element to drop
	 * @param evt the {@link MouseEvent} used for calculating the position
	 * @param toDrop the elements to drop
	 */
	public void drop(Node node, MouseEvent evt, List<ParameterizedGenericObject<?>> toDrop) {
		Iterator<OperationBlockViewController> funcIter = operationBlocks.values().iterator();
		
		OperationBlockViewController inserted = null;
		if (toDrop.isEmpty() || toDrop.get(0).getObj() instanceof Function<?>) {
			return;
		}
		while (inserted == null && funcIter.hasNext()) {
			OperationBlockViewController funcView = funcIter.next();
			try {
				if (funcView.getView() != node && funcView.getView().isVisible()) {
					if (addCopiesToFuncViewIfIntersects(evt, funcView, toDrop)) {
						inserted = funcView;
					}
				}
			} catch (YAGPLException e1) {
				e1.printStackTrace();// TODO
			}
		}
		if (inserted != null && node.getParent() != inserted.getView()) {
			if (node.getParent() instanceof Pane) {
				((Pane) node.getParent()).getChildren().remove(node);
			} else if (!editorPane.getChildren().remove(node)) {
				System.out.println("Cannot remove: " + node + " from " + node.getParent());
			}
		}
	}

	/**
	 * adds copies of one or more {@link ParameterizedGenericObject}s to a function view if the elements intersect
	 * @param evt the {@link MouseEvent} used for calculating the position
	 * @param ctl the {@link OperationBlockViewController function view} to copy the elements
	 * @param toAdd the elements to copy
	 * @return <code>true</code> if the elements were copied, else <code>false</code>
	 * @throws YAGPLException if an error occured while copying
	 */
	private static boolean addCopiesToFuncViewIfIntersects(MouseEvent evt, OperationBlockViewController ctl,
			List<ParameterizedGenericObject<?>> toAdd) throws YAGPLException {
		List<ParameterizedGenericObject<?>> toAddChanged = new ArrayList<>();
		for (ParameterizedGenericObject<?> add : toAdd) {
			ParameterizedGenericObject<?> copy = add.createCopy();
			copy.setParams(add.getParams());
			toAddChanged.add(copy);
		}
		return ctl.addIfIntersects(evt, toAddChanged);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		availableElementView.setItems(availableElements);
		availableElementView.setCellFactory(param -> new ListCell<ParameterizedGenericObject<?>>() {
			@Override
			protected void updateItem(ParameterizedGenericObject<?> item, boolean empty) {
				if (!empty && item != null) {
					Node elem;
					elem = getUIElement(item);
					if (elem instanceof Parent) {
						for (Node node : ((Parent) elem).getChildrenUnmodifiable()) {
							node.setOnMouseClicked(null);
						}
					}
					//					}
					this.setGraphic(elem);
					allowCopyDrag(item);
				}
			}
		});
		editorPane.setViewOrder(-10);
	}

	public Pane getEditorPane() {
		return editorPane;
	}
	
	/**
	 * executes an {@link OperationBlock}
	 * @param block the {@link OperationBlock} to execute
	 * @param params the parameters for the {@link OperationBlock}
	 */
	public void exec(OperationBlock<?> block, Object[] params) {
		if(currentTask!=null) {
			currentTask.cancel(true);
		}
		currentTask = execThreadPool.submit(() -> {
			try {
				block.execute(new FunctionContext(globalCtx), params);
			} catch (Exception e) {
				Platform.runLater(() -> error("An error occured while executing the block\n" + e.getMessage(), e));
			}
		});
	}
	
	private Thread createThread(Runnable r) {
		Thread t=new Thread(r);
		t.setDaemon(true);
		return t;
	}
}
