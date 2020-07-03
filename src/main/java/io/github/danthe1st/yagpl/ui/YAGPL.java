package io.github.danthe1st.yagpl.ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.reflections.Reflections;

import io.github.danthe1st.yagpl.api.Assignment;
import io.github.danthe1st.yagpl.api.Function;
import io.github.danthe1st.yagpl.api.GenericObject;
import io.github.danthe1st.yagpl.api.GlobalContext;
import io.github.danthe1st.yagpl.api.ParameterizedGenericObject;
import io.github.danthe1st.yagpl.api.ReturnStatement;
import io.github.danthe1st.yagpl.api.StandardElement;
import io.github.danthe1st.yagpl.api.concrete.debug.LambdaExpression;
import io.github.danthe1st.yagpl.api.concrete.debug.LambdaStatement;
import io.github.danthe1st.yagpl.api.concrete.debug.PrintArgsStatement;
import io.github.danthe1st.yagpl.api.concrete.debug.PrintFunctionContext;
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
		
		List<ParameterizedGenericObject<?, String>> op=new ArrayList<>();
		op.add(new ParameterizedGenericObject<>(new PrintArgsStatement<>(), new String[0]));
		op.add(new ParameterizedGenericObject<>(new Assignment<>(), new String[] {"\"variableToPrint\"","param0"}));
		op.add(new ParameterizedGenericObject<>(new PrintFunctionContext<>(), new String[0]));
		op.add(new ParameterizedGenericObject<>(new PrintArgsStatement<>(), new String[] {"variableToPrint"}));
		op.add(new ParameterizedGenericObject<>(new ReturnStatement<>(new LambdaExpression<>("get-ret", p->"Hello"+p[0])),new String[] {"variableToPrint"}));
		op.add(new ParameterizedGenericObject<>(new LambdaStatement<>("sout", params->System.err.println("THIS SHOULD NOT BE PRINTED")), new String[0]));
		Function<String, Void> main=new Function<>("main", op,new Class<?>[] {Object.class});
		
		EditorController ctl=loadView("Editor");
		ctl.addFunction(main);
		ctl.setGlobalContext(globalCtx);
		ctl.setAvailableElements(load());
		primaryStage.setScene(new Scene(ctl.getView()));
		primaryStage.show();
	}
	private static List<ParameterizedGenericObject<?,?>> load() throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		List<ParameterizedGenericObject<?,?>> operations=new ArrayList<>();
		Reflections reflections=new Reflections("io.github.danthe1st.yagpl.api");
		for(Class<?> cl:reflections.getTypesAnnotatedWith(StandardElement.class)) {
			Object instance=cl.getDeclaredConstructor().newInstance();
			if(instance instanceof GenericObject<?,?>) {
				String[] params=new String[0];
				if(((GenericObject<?,?>) instance).getExpectedParameters()!=null) {
					params=new String[((GenericObject<?,?>) instance).getExpectedParameters().length];
				}
				operations.add(new ParameterizedGenericObject<>((GenericObject<?,?>)instance, params));
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
