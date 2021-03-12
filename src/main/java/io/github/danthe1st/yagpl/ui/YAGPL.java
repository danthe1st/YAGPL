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

public class YAGPL extends Application{
	
	private GlobalContext globalCtx=new GlobalContext();
	
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		EditorController ctl=loadView("Editor");
		ctl.setGlobalContext(globalCtx);
		ctl.setAvailableElements(load());
		ctl.load();
		primaryStage.setScene(new Scene(ctl.getView()));
		primaryStage.show();
	}
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
