package io.github.danthe1st.yagpl.ui;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.danthe1st.yagpl.api.Assignment;
import io.github.danthe1st.yagpl.api.Function;
import io.github.danthe1st.yagpl.api.GenericObject;
import io.github.danthe1st.yagpl.api.ReturnStatement;
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
	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		List<Map.Entry<GenericObject<?,String>,String[]>> op=new ArrayList<>();
		op.add(new AbstractMap.SimpleEntry<>(new PrintArgsStatement<>(), new String[0]));
		op.add(new AbstractMap.SimpleEntry<>(new Assignment<>(), new String[] {"\"variableToPrint\"","param0"}));
		op.add(new AbstractMap.SimpleEntry<>(new PrintFunctionContext<>(), new String[0]));
		op.add(new AbstractMap.SimpleEntry<>(new PrintArgsStatement<>(), new String[] {"variableToPrint"}));
		op.add(new AbstractMap.SimpleEntry<>(new ReturnStatement<>(new LambdaExpression<>("get-ret", p->"Hello"+p[0])),new String[] {"variableToPrint"}));
		op.add(new AbstractMap.SimpleEntry<>(new LambdaStatement<>("sout", params->System.err.println("THIS SHOULD NOT BE PRINTED")), new String[0]));
		Function<String, Void> main=new Function<>("main", op,new Class<?>[] {String.class});
		
		//FunctionContext<Void> ctx=new FunctionContext<>();
		
		EditorController ctl=loadView("Editor");
		ctl.addFunction(main);
		primaryStage.setScene(new Scene(ctl.getView()));
		primaryStage.show();
	}
	public <C extends Controller<V>,V extends Parent> C loadView(String name) throws IOException {
		FXMLLoader loader=new FXMLLoader(this.getClass().getResource("view/"+name+".fxml"));
		V editor = loader.load();
		C ctl = loader.getController();
		ctl.setView(editor);
		ctl.setMain(this);
		return ctl;
	}
}
