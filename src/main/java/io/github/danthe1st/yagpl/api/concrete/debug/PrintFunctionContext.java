package io.github.danthe1st.yagpl.api.concrete.debug;

import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.StandardElement;
import io.github.danthe1st.yagpl.api.Statement;

@StandardElement
public class PrintFunctionContext extends Statement{

	public PrintFunctionContext() {
		super("debug-printCTX", null,new Class<?>[] {});
	}

	@Override
	public Void execute(FunctionContext ctx, Object... params) {
		System.out.println(ctx);
		return null;
	}
}
