package io.github.danthe1st.yagpl;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.github.danthe1st.yagpl.api.Assignment;
import io.github.danthe1st.yagpl.api.Expression;
import io.github.danthe1st.yagpl.api.Function;
import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.GenericObject;
import io.github.danthe1st.yagpl.api.ReturnStatement;
import io.github.danthe1st.yagpl.api.Statement;
import io.github.danthe1st.yagpl.api.concrete.debug.PrintFunctionContext;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

public class YAGPLTest {
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
		op.add(new AbstractMap.SimpleEntry<>(new Statement<>("stmt-sout", params->System.out.println(Arrays.toString(params))), new String[0]));
		op.add(new AbstractMap.SimpleEntry<>(new Assignment<>("assign", "variableToPrint"), new String[] {"param0"}));
		op.add(new AbstractMap.SimpleEntry<>(new PrintFunctionContext<>(), new String[0]));
		op.add(new AbstractMap.SimpleEntry<>(new Statement<>("stmt-sout", params->System.out.println(Arrays.toString(params))), new String[] {"variableToPrint"}));
		op.add(new AbstractMap.SimpleEntry<>(new ReturnStatement<>("ret", new Expression<>("get-ret", p->"Hello"+p[0])),new String[] {"variableToPrint"}));
		op.add(new AbstractMap.SimpleEntry<>(new Statement<>("stmt-sout", params->System.err.println("THIS SHOULD NOT BE PRINTED")), new String[0]));
		Function<String, Void> main=new Function<>("func-main", op);
		
		FunctionContext<Void> ctx=new FunctionContext<>();
		try {
			String ret = main.execute(ctx,1337);
			System.out.println(ret);
		} catch (YAGPLException e) {
			e.printStackTrace();
		}
	}
}
