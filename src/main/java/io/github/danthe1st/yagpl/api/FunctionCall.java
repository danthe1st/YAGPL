package io.github.danthe1st.yagpl.api;

import io.github.danthe1st.yagpl.api.blocks.Function;
import io.github.danthe1st.yagpl.api.throwables.YAGPLException;

public class FunctionCall<R> extends GenericObjectAdapter<R> {
	
	private Function<R> func;

	public FunctionCall(Function<R> func) {
		super(func.getName(), func.getExpectedParameters());
	}

	@Override
	public R execute(FunctionContext ctx, Object... params) throws YAGPLException {
		return func.execute(ctx, params);
	}

}
