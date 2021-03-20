package io.github.danthe1st.yagpl.ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.reflections.Reflections;

import io.github.danthe1st.yagpl.api.GenericObject;
import io.github.danthe1st.yagpl.api.GlobalContext;
import io.github.danthe1st.yagpl.api.ParameterizedGenericObject;
import io.github.danthe1st.yagpl.api.StandardElement;
import io.github.danthe1st.yagpl.ui.controller.Controller;
import io.github.danthe1st.yagpl.ui.controller.EditorController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class that instantiates the UI
 * @author dan1st
 */
public class YAGPL extends Application{
	
	private GlobalContext globalCtx=new GlobalContext();
	
	/**
	 * start the UI
	 * @param args arguments that are passed to JavaFX
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}

	/**
	 * starts the application
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		EditorController ctl=loadView("Editor");
		ctl.setGlobalContext(globalCtx);
		ctl.setAvailableElements(load());
		ctl.load();
		primaryStage.setScene(new Scene(ctl.getView()));
		primaryStage.show();
	}
	/**
	 * loads the default operations annotated with {@link StandardElement}.<br/>
	 * Only elements in the package {@code io.github.danthe1st.yagpl} are used<br/>
	 * @return a {@link List} containing all standard YAGPL operations
	 * @throws InstantiationException if an operation annotated with {@link StandardElement} is abstract
	 * @throws IllegalAccessException if an operation annotated with {@link StandardElement} cannot be accessed
	 * @throws InvocationTargetException if an exception occurred while creating an operation annotated with {@link StandardElement}
	 * @throws NoSuchMethodException if an operation annotated with {@link StandardElement} does not declare a no-args-constructor
	 * @throws SecurityException if the {@link SecurityManager} does not permit creating an operation annotated with {@link StandardElement}
	 */
	private static List<ParameterizedGenericObject<?>> load() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		List<ParameterizedGenericObject<?>> operations=new ArrayList<>();
		Reflections reflections=new Reflections("io.github.danthe1st.yagpl");
		for(Class<?> cl:reflections.getTypesAnnotatedWith(StandardElement.class)) {
			Object instance=cl.getDeclaredConstructor().newInstance();
			if(instance instanceof GenericObject<?>) {
				String[] params=new String[0];
				if(((GenericObject<?>) instance).getExpectedParameters()!=null) {
					params=new String[((GenericObject<?>) instance).getExpectedParameters().length];
				}
				operations.add(new ParameterizedGenericObject<>((GenericObject<?>)instance, params));
			}
		}
		return operations;
	}
	/**
	 * loads a view by its name
	 * @param <C> the type of the associated {@link Controller}
	 * @param <V> the type of the root element if the loaded view
	 * @param name the name of the view to load
	 * @return the controller of the view
	 * @throws IOException if the view does not exist
	 */
	public <C extends Controller<V>,V extends Parent> C loadView(String name) throws IOException {
		FXMLLoader loader=new FXMLLoader(this.getClass().getResource("view/"+name+".fxml"));
		V editor = loader.load();
		C ctl = loader.getController();
		ctl.setView(editor);
		ctl.setMain(this);
		ctl.setGlobalContext(globalCtx);
		return ctl;
	}
}
