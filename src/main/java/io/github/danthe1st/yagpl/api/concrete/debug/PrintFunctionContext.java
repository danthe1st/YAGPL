package io.github.danthe1st.yagpl.api.concrete.debug;

import io.github.danthe1st.yagpl.api.FunctionContext;
import io.github.danthe1st.yagpl.api.Statement;

public class PrintFunctionContext<C> extends Statement<C>{

	public PrintFunctionContext() {
		super("debug-printCTX", null,new Class<?>[] {});
	}

	@Override
	public Void execute(FunctionContext<C> ctx, Object... params) {
		System.out.println(ctx);
		return null;
	}
}
