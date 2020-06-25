package io.github.danthe1st.yagpl;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.danthe1st.yagpl.api.Assignment;
import io.github.danthe1st.yagpl.api.Function;
import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.GenericObject;
import io.github.danthe1st.yagpl.api.GlobalContext;
import io.github.danthe1st.yagpl.api.ReturnStatement;
import io.github.danthe1st.yagpl.api.concrete.debug.LambdaExpression;
import io.github.danthe1st.yagpl.api.concrete.debug.LambdaStatement;
import io.github.danthe1st.yagpl.api.concrete.debug.PrintArgsStatement;
import io.github.danthe1st.yagpl.api.concrete.debug.PrintFunctionContext;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

public class YAGPLTest {//NOSONAR this is a main class, I don't use JUnit or similar (yet)
	/**
	 * expected:
	 * OUT: [] --> no parameters because of new String[0]
	 * assign variableToPrint to first parameter (param0)
	 * OUT: toString of FunctionContext with param0 and variableToPrint as variables
	 * OUT: [1337] --> print all parameters, only one parameter <-- variableToPrint <-- 1337
	 * RET: Hello1337 --> return result of expression <-- "Hello" + first parameter <-- variableToPrint
	 * execution should stop
	 * OUT: Hello1337 --> returned result
	 * @param args unused
	 */
	public static void main(String[] args) {
		List<Map.Entry<GenericObject<?,String>,String[]>> op=new ArrayList<>();
		op.add(new AbstractMap.SimpleEntry<>(new PrintArgsStatement<>(), new String[0]));
		op.add(new AbstractMap.SimpleEntry<>(new Assignment<>(), new String[] {"variableToPrint","param0"}));
		op.add(new AbstractMap.SimpleEntry<>(new PrintFunctionContext<>(), new String[0]));
		op.add(new AbstractMap.SimpleEntry<>(new PrintArgsStatement<>(), new String[] {"variableToPrint"}));
		op.add(new AbstractMap.SimpleEntry<>(new ReturnStatement<>(new LambdaExpression<>("get-ret", p->"Hello"+p[0])),new String[] {"variableToPrint"}));
		op.add(new AbstractMap.SimpleEntry<>(new LambdaStatement<>("sout", params->System.err.println("THIS SHOULD NOT BE PRINTED")), new String[0]));
		Function<String, Void> main=new Function<>("main", op);
		
		FunctionContext<Void> ctx=new FunctionContext<>(new GlobalContext());
		try {
			String ret = main.execute(ctx,1337);
			System.out.println(ret);
		} catch (YAGPLException e) {
			e.printStackTrace();
		}
	}
}
